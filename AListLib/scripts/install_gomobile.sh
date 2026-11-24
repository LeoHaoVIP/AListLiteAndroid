cd ../sources
# gomobile 生成 per-arch go.mod 前会执行 `go list -m -json all`（遍历完整依赖图）。
# OpenList 的间接依赖 github.com/ajroetker/go-highway 把子模块 hwy/goat 引到根本不存在的
# v0.0.0（module proxy 上只有 v0.0.1）→ 探测整体报错 → gomobile 静默写出空 go.mod →
# `go mod tidy` 报 "go: error reading go.mod: missing module declaration"，
# alistlib.aar 产不出来（Actions run 152 起持续失败就是这个）。
# 补一条 replace 让依赖图可解析；上游修好依赖后这段可删。
if grep -q "github.com/ajroetker/go-highway " go.mod; then
  go mod edit -replace=github.com/ajroetker/go-highway/hwy/goat=github.com/ajroetker/go-highway/hwy/goat@v0.0.1
fi

go mod download golang.org/x/mobile
go install golang.org/x/mobile/cmd/gomobile@latest
gomobile init
go get golang.org/x/mobile/bind
