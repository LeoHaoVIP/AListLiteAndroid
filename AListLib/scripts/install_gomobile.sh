cd ../sources
go install golang.org/x/mobile/cmd/gomobile
echo "$(go env GOPATH)/bin" >> $GITHUB_PATH
gomobile init
go get golang.org/x/mobile/bind
cd ../scripts
