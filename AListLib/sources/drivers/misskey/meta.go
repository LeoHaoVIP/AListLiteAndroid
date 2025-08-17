package misskey

import (
	"github.com/OpenListTeam/OpenList/v4/internal/driver"
	"github.com/OpenListTeam/OpenList/v4/internal/op"
)

type Addition struct {
	// Usually one of two
	driver.RootPath
	// define other
	// Field string `json:"field" type:"select" required:"true" options:"a,b,c" default:"a"`
	Endpoint    string `json:"endpoint" required:"true" default:"https://misskey.io"`
	AccessToken string `json:"access_token" required:"true"`
}

var config = driver.Config{
	Name:        "Misskey",
	DefaultRoot: "/",
}

func init() {
	op.RegisterDriver(func() driver.Driver {
		return &Misskey{}
	})
}
