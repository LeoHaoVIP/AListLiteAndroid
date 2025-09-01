cd ../sources
gomobile bind -ldflags "-s -w" -v -androidapi 21 "github.com/OpenListTeam/OpenList/v4/alistlib"
mkdir -p ../../app/libs/
cp -f ./alistlib.aar ../../app/libs/
