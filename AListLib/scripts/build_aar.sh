cd ../sources
# 保持 -ldflags "-s -w"：Go 的 pclntab（运行时符号表）不受 -s 影响，
# 线上原生崩溃可用 scripts/symbolize_native_crash.sh（go tool addr2line）离线还原符号；
# 去掉 -s 只会让第三方解析器（Bugly native 解析、gdb）满意，但 aar/APK 会明显变大。
gomobile bind -ldflags "-s -w" -v -androidapi 21 "github.com/OpenListTeam/OpenList/v4/alistlib" "github.com/OpenListTeam/OpenList/v4/alitvlib"
mkdir -p ../../app/libs/
cp -f ./alistlib.aar ../../app/libs/
