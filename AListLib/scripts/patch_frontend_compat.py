#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""给 OpenList 前端 index.html 加"能力探测"，让旧内核（iOS 15.4 以下 Safari、
旧 Android WebView）自动走 legacy 包，避免白屏。

上游 dist 的做法：modern 入口（type=module）由一段内联脚本动态注入，legacy 入口
（System.register + systemjs polyfills）用 `nomodule` 标在标签上——而 `nomodule`
只有"不支持 ES module"的浏览器才会执行。iOS 12.4 / 15.x、Chrome WebView < 80 都
支持 module，因此拿到 modern 包，但 modern 包用了 structuredClone / Object.hasOwn /
Array.prototype.at 等 15.4+ API，直接白屏。

本脚本把注入器改成：
  supported() ? 原样跑上游注入器（modern）
              : 注入样式 + polyfills-legacy + System.import(legacy 入口)

无法识别上游格式时不做任何修改（安全退出），保证构建不因此失败。
"""
import os
import re
import sys

MARKER = "<!-- openlist-compat-loader -->"

FEATURES = """
  function __openlistSupported() {
    try {
      if (typeof structuredClone !== "function") return false;
      if (typeof Object.hasOwn !== "function") return false;
      if (typeof Array.prototype.at !== "function") return false;
      if (typeof String.prototype.replaceAll !== "function") return false;
      if (typeof Promise.allSettled !== "function") return false;
      return true;
    } catch (e) {
      return false;
    }
  }
"""

LOADER = """{marker}
<script>
(function () {{
{features}
  var ASSET_BASE = window.__dynamic_base__ || "";
  var CSS = {css};
  var POLYFILL = {poly};
  var LEGACY_ENTRY = {legacy};

  function absolute(path) {{ return ASSET_BASE + path; }}

  function addStyles() {{
    for (var i = 0; i < CSS.length; i++) {{
      var link = document.createElement("link");
      link.rel = "stylesheet";
      link.crossOrigin = "";
      link.href = absolute(CSS[i]);
      document.head.appendChild(link);
    }}
  }}

  if (__openlistSupported()) {{
    /* 现代内核：原样执行上游注入器 */
{injector}
  }} else {{
    /* 旧内核：走 legacy（systemjs）包 */
    addStyles();
    var poly = document.createElement("script");
    poly.crossOrigin = "";
    poly.src = absolute(POLYFILL);
    poly.onload = function () {{
      try {{
        System.import(absolute(LEGACY_ENTRY));
      }} catch (e) {{
        if (window.console) console.error("[openlist] legacy 入口加载失败", e);
      }}
    }};
    poly.onerror = function () {{
      if (window.console) console.error("[openlist] legacy polyfill 加载失败");
    }};
    document.head.appendChild(poly);
  }}
}})();
</script>
"""


def patch(index_path):
    with open(index_path, encoding="utf-8") as f:
        html = f.read()

    if MARKER in html:
        print("already patched, skip:", index_path)
        return True

    # 上游注入器：<script>\n(function(){\nvar preloads = [...]; ... })();\n</script>
    m = re.search(r"<script>\s*(\(function\(\)\{\s*var preloads = \[.*?\}\)\(\);)\s*</script>", html, re.S)
    if not m:
        print("WARN: 未识别到上游注入器，跳过兼容补丁", index_path)
        return False
    injector = m.group(1)

    def find_all(pattern):
        return re.findall(pattern, injector)

    css = find_all(r'\{"rel":"stylesheet","crossorigin":"","href":"([^"]+)"\}')
    poly = find_all(r'"id":"vite-legacy-polyfill","data-src":"([^"]+)"')
    legacy = re.findall(r'id="vite-legacy-entry"\s+data-src="([^"]+)"', html)

    if not css or not poly or not legacy:
        print("WARN: 缺少 css/polyfill/legacy 资源（css=%d poly=%d legacy=%d），跳过"
              % (len(css), len(poly), len(legacy)))
        return False

    def js_list(items):
        return "[" + ", ".join('"%s"' % i for i in items) + "]"

    def js_str(s):
        return '"%s"' % s

    indented = "\n".join("    " + line for line in injector.splitlines())
    loader = LOADER.format(
        marker=MARKER,
        features=FEATURES.rstrip("\n"),
        css=js_list(css),
        poly=js_str(poly[0]),
        legacy=js_str(legacy[0]),
        injector=indented,
    )

    new_html = html[: m.start()] + loader.strip() + html[m.end():]

    # 原来的 legacy 入口标签（nomodule、空内容）已无用，移除避免歧义
    new_html = re.sub(r'\s*<script nomodule crossorigin id="vite-legacy-entry"[^>]*></script>', "", new_html)

    with open(index_path, "w", encoding="utf-8", newline="") as f:
        f.write(new_html)

    print("patched:", index_path)
    print("  css:", len(css), "| polyfill:", poly[0], "| legacy:", legacy[0])
    return True


def main():
    targets = sys.argv[1:]
    if not targets:
        print("usage: patch_frontend_compat.py <index.html> [...]")
        return 2
    ok = True
    for t in targets:
        if not os.path.exists(t):
            print("missing:", t)
            ok = False
            continue
        if not patch(t):
            ok = False
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
