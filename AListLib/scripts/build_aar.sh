cd ../sources
# 保持 -ldflags "-s -w"：Go 的 pclntab（运行时符号表）不受 -s 影响，需要排查原生崩溃时
# 用 `go tool addr2line <从APK提取的libgojni.so>` 仍可把 PC 还原成函数/行号；
# 去掉 -s 只会让第三方解析器（Bugly、gdb）受益，代价是 aar/APK 明显变大。
gomobile bind -ldflags "-s -w" -v -androidapi 21 "github.com/OpenListTeam/OpenList/v4/alistlib" "github.com/OpenListTeam/OpenList/v4/alitvlib"
mkdir -p ../../app/libs/
cp -f ./alistlib.aar ../../app/libs/
