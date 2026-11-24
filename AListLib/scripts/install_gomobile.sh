cd ../sources
# 固定 golang.org/x/mobile 版本。
# 上游 @latest（v0.0.0-20261117202452-47699113848a 起）只升了依赖
# （x/mod 0.43 / x/tools 0.53 / x/sys 0.50 / x/image 0.48），cmd/gomobile 源码无变化，
# 但会让 gomobile 生成的 per-arch go.mod 在 `go mod tidy` 阶段报
#   go: error reading go.mod: missing module declaration
#   gomobile: go mod tidy -v failed: exit status 1
# 导致 alistlib.aar 产不出来（Actions run 152 起持续失败）。
# 20261011 这一版经 run 151 实测可正常产出 aar，钉住以免每天被上游漂移打穿。
# 上游修好后可改回 @latest。
X_MOBILE_VERSION=v0.0.0-20261011034528-197770444867
go mod download golang.org/x/mobile
go install golang.org/x/mobile/cmd/gomobile@${X_MOBILE_VERSION}
gomobile init
go get golang.org/x/mobile/bind@${X_MOBILE_VERSION}
