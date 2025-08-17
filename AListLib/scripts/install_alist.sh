# Backend
TAG_NAME=$(curl -s -k https://api.github.com/repos/OpenListTeam/OpenList/releases/latest | grep -o '"tag_name": ".*"' | cut -d'"' -f4)
# TAG_NAME=v4.0.3
URL="https://github.com/OpenListTeam/OpenList/archive/refs/tags/${TAG_NAME}.tar.gz"
echo "Downloading openlist ${TAG_NAME} from ${URL}"
curl -L -k $URL -o "openlist${TAG_NAME}.tar.gz"
find ../sources/ -mindepth 1 -maxdepth 1 ! -name "alistlib" -exec rm -rf {} +
tar xf "openlist${TAG_NAME}.tar.gz" --strip-components 1 -C ../sources
rm -f ../sources/.gitignore
# Frontend
URL=https://github.com/OpenListTeam/OpenList-Frontend/releases/latest/download/openlist-frontend-dist-${TAG_NAME}.tar.gz
echo "Downloading openlist-frontend from ${URL}"
curl -L -k ${URL} -o dist.tar.gz
rm -rf ../sources/public/dist
mkdir ../sources/public/dist
tar xf dist.tar.gz -C ../sources/public/dist
