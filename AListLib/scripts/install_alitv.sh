# Backend
TAG_NAME=$(curl -s -k https://api.github.com/repos/lilu0826/alitv_openlist/releases/latest | grep -o '"tag_name": ".*"' | cut -d'"' -f4)
# TAG_NAME=v1.0.3
URL="https://github.com/lilu0826/alitv_openlist/archive/refs/tags/${TAG_NAME}.tar.gz"
echo "Downloading alitv ${TAG_NAME} from ${URL}"
curl -L -k $URL -o "alitv_${TAG_NAME}.tar.gz"
find ../sources/alitvlib -mindepth 1 -maxdepth 1 ! -name "server.go" -exec rm -rf {} +
tar xf "alitv_${TAG_NAME}.tar.gz" --strip-components 1 -C ../sources/alitvlib
rm -f ../sources/alitvlib/main.go
rm -f ../sources/alitvlib/go.*
