#!/bin/sh
# 离线符号化 Bugly 上报的原生崩溃（Go 写的 libgojni.so）
#
# 背景：aar 以 -ldflags "-s -w" 构建，ELF .symtab 被剥离，Bugly 的 native 解析器
# 无法还原 Go 栈（例如 #78079 只给出两帧假栈）。但 Go 的 pclntab 仍在，
# `go tool addr2line` 可以直接把 PC 还原成「函数 + 文件:行号」。
#
# 用法：
#   sh symbolize_native_crash.sh <apk或so路径> <pc1> [pc2 ...]
# 示例：
#   sh symbolize_native_crash.sh ~/Downloads/AListLite-v3.0.1-auto7-arm64-v8a-release-signed.apk 0x67879e0 0x6786a64
#
# 说明：
#   - PC 取 Bugly 崩溃详情里的 "#00 pc 00000000067879e0"
#   - 只对 Go 代码有效；cgo/C 部分需要保留 ELF 符号或额外上传符号表
#   - 需要本机已安装 Go（go tool addr2line）

set -e

if [ $# -lt 2 ]; then
  echo "用法: sh symbolize_native_crash.sh <apk或so路径> <pc1> [pc2 ...]"
  exit 1
fi

INPUT="$1"
shift

WORK="$(mktemp -d 2>/dev/null || mktemp -d -t symbolize)"
SO=""

case "$INPUT" in
  *.apk)
    echo "从 APK 提取 arm64-v8a/libgojni.so ..."
    unzip -o -q "$INPUT" "lib/arm64-v8a/libgojni.so" -d "$WORK"
    SO="$WORK/lib/arm64-v8a/libgojni.so"
    ;;
  *)
    SO="$INPUT"
    ;;
esac

if [ ! -f "$SO" ]; then
  echo "找不到 so: $SO"
  exit 1
fi

# git-bash/MSYS 下 mktemp 给的是 /tmp/...，原生 go 不认，需要转成 Windows 路径
SO_ARG="$SO"
if command -v cygpath >/dev/null 2>&1; then
  SO_ARG="$(cygpath -w "$SO" 2>/dev/null || echo "$SO")"
fi

echo "so: $SO ($(wc -c < "$SO") bytes)"
echo "--- 符号化结果（PC / 函数 / 文件:行号 交替输出）---"
for pc in "$@"; do
  hex=$(echo "$pc" | sed 's/^0x//')
  echo "== $pc"
  printf '%s\n' "$hex" | go tool addr2line "$SO_ARG"
done

rm -rf "$WORK"
