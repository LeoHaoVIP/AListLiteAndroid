cd ../sources
go mod download golang.org/x/mobile
go install golang.org/x/mobile/cmd/gomobile@latest
gomobile init
go get golang.org/x/mobile/bind
