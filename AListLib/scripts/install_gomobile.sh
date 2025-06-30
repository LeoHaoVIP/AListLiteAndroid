cd ../sources
go install golang.org/x/mobile/cmd/gomobile
export PATH=$PATH:$GOPATH/bin
gomobile init
go get golang.org/x/mobile/bind
cd ../scripts
