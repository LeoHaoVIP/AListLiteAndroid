# Backend
# 取 OpenList 最新正式版 tag。
# runner 上匿名调用 GitHub API 极易被限流（Actions run 45963782778 即因此失败），
# 所以：带 GH_TOKEN 调用 + 三次重试 + git ls-remote 兜底。
fetch_latest_tag() {
  api="https://api.github.com/repos/OpenListTeam/OpenList/releases/latest"
  i=1
  while [ "$i" -le 3 ]; do
    if [ -n "$GH_TOKEN" ]; then
      body=$(curl -s -k -f --max-time 30 \
        -H "Authorization: Bearer $GH_TOKEN" \
        -H "Accept: application/vnd.github+json" "$api" 2>/dev/null)
    else
      body=$(curl -s -k -f --max-time 30 \
        -H "Accept: application/vnd.github+json" "$api" 2>/dev/null)
    fi
    tag=$(printf '%s' "$body" | grep -o '"tag_name": *"[^"]*"' | head -1 | cut -d'"' -f4)
    case "$tag" in
      v[0-9]*) printf '%s' "$tag"; return 0 ;;
    esac
    echo "获取 OpenList tag 失败（第 $i/3 次），10 秒后重试" >&2
    i=$((i + 1))
    sleep 10
  done

  # 兜底：git 协议不受 API 限流影响
  tag=$(git ls-remote --tags --refs --sort=-v:refname \
    https://github.com/OpenListTeam/OpenList.git 'v*' 2>/dev/null \
    | head -1 | sed 's#.*refs/tags/##')
  case "$tag" in
    v[0-9]*) printf '%s' "$tag"; return 0 ;;
  esac
  return 1
}

TAG_NAME=$(fetch_latest_tag) || TAG_NAME=""
if [ -z "$TAG_NAME" ]; then
  echo "ERROR: 获取 OpenList 最新 tag 失败"
  exit 1
fi
echo "OpenList tag: $TAG_NAME"
# TAG_NAME=v4.0.3
TGZ="openlist${TAG_NAME}.tar.gz"

# 下载并校验压缩包：runner 到 github 的连接会偶发抖动，残缺包会让 tar 解压失败，
# 而脚本是"先清空 sources 再解压"，所以必须校验通过后再动手清理（见 Actions run 164）。
fetch_tarball() {
  url="$1"
  out="$2"
  i=1
  while [ "$i" -le 5 ]; do
    echo "Downloading $url (attempt $i/5)"
    if curl -fL -k --retry 2 --retry-delay 3 --connect-timeout 20 --max-time 300 -o "$out" "$url" \
       && tar tzf "$out" > /dev/null 2>&1; then
      return 0
    fi
    echo "download or integrity check failed, retrying..."
    rm -f "$out"
    sleep 5
    i=$((i + 1))
  done
  return 1
}

fetch_tarball "https://github.com/OpenListTeam/OpenList/archive/refs/tags/${TAG_NAME}.tar.gz" "$TGZ" || {
  echo "ERROR: OpenList 源码包下载失败: ${TAG_NAME}"
  exit 1
}

find ../sources/ -mindepth 1 -maxdepth 1 ! -name "alistlib" ! -name "alitvlib" -exec rm -rf {} +
tar xf "$TGZ" --strip-components 1 -C ../sources
rm -f ../sources/.gitignore
# Frontend
fetch_tarball "https://github.com/OpenListTeam/OpenList-Frontend/releases/latest/download/openlist-frontend-dist-${TAG_NAME}.tar.gz" "dist.tar.gz" || {
  echo "ERROR: OpenList 前端包下载失败: ${TAG_NAME}"
  exit 1
}
rm -rf ../sources/public/dist
mkdir ../sources/public/dist
tar xf dist.tar.gz -C ../sources/public/dist

# 旧内核兜底：上游 index.html 的 legacy 入口只对"不支持 ES module"的浏览器生效，
# 而 iOS 15.4 以下 Safari、旧 Android WebView 都支持 module，会拿到用了
# structuredClone / Object.hasOwn / Array.prototype.at 的 modern 包 → 白屏。
# 这里注入能力探测，让这类内核改走 legacy(systemjs) 包（见 patch_frontend_compat.py）。
PY=""
if command -v python3 >/dev/null 2>&1; then
  PY=python3
elif command -v python >/dev/null 2>&1; then
  PY=python
fi
if [ -n "$PY" ]; then
  "$PY" patch_frontend_compat.py ../sources/public/dist/index.html || echo "WARN: 前端兼容补丁未应用（不阻塞构建）"
else
  echo "WARN: 未找到 python，跳过前端兼容补丁"
fi
# Alter Config
cp ../../app/src/main/res/drawable/alistlite.png ../sources/public/dist/images/logo.png
sed -i 's#https://github.com/OpenListTeam/OpenList#https://github.com/LeoHaoVIP/AListLiteAndroid#g' ../sources/internal/bootstrap/data/setting.go
sed -i 's#https://cdn.oplist.org/gh/OpenListTeam/Logo@main/logo.svg#/images/logo.png#g' ../sources/internal/bootstrap/data/setting.go
sed -i 's#https://res.oplist.org/logo/logo.svg#/images/logo.png#g' ../sources/internal/bootstrap/data/setting.go
sed -i 's#Key: "pagination_type", Value: "all"#Key: "pagination_type", Value: "pagination"#g' ../sources/internal/bootstrap/data/setting.go
sed -i 's#Key: conf.SearchIndex, Value: "none"#Key: conf.SearchIndex, Value: "database"#g' ../sources/internal/bootstrap/data/setting.go
sed -i 's#Key: conf.AutoUpdateIndex, Value: "false"#Key: conf.AutoUpdateIndex, Value: "true"#g' ../sources/internal/bootstrap/data/setting.go
sed -i 's#Permission: 0x71FF#Permission: 0xFFFF#g' ../sources/internal/bootstrap/data/user.go
sed -i -z 's#Disabled:   true#Disabled:   false#g' ../sources/internal/bootstrap/data/user.go
