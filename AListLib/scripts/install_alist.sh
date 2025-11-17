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
