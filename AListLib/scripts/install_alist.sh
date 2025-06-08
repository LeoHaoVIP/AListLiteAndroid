# Backend
TAG_NAME=$(curl -s -k https://api.github.com/repos/AlistGo/alist/releases/latest | grep -o '"tag_name": ".*"' | cut -d'"' -f4)
#TAG_NAME=v3.44.0
URL="https://github.com/AlistGo/alist/archive/refs/tags/${TAG_NAME}.tar.gz"
echo "Downloading alist ${TAG_NAME} from ${URL}"
curl -L -k $URL -o "alist${TAG_NAME}.tar.gz"
tar xf "alist${TAG_NAME}.tar.gz" --strip-components 1 -C ../sources
rm -f ../sources/.gitignore
# Frontend
URL=https://github.com/alist-org/alist-web/releases/latest/download/dist.tar.gz
echo "Downloading alist-frontend from ${URL}"
curl -L -k ${URL} -o dist.tar.gz
tar -zxvf dist.tar.gz
rm -rf ../sources/public/dist
mv -f dist ../sources/public
