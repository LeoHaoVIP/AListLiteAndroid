package halalcloud

import (
	"github.com/OpenListTeam/OpenList/v4/internal/driver"
	"github.com/OpenListTeam/OpenList/v4/internal/op"
)

type Addition struct {
	// Usually one of two
	driver.RootPath
	// define other
	RefreshToken string `json:"refresh_token" required:"true" help:"login type is refresh_token,this is required"`
	UploadThread string `json:"upload_thread" default:"3" help:"1 <= thread <= 32"`

	AppID      string `json:"app_id" required:"true" default:"openlist/10001"`
	AppVersion string `json:"app_version" required:"true" default:"1.0.0"`
	AppSecret  string `json:"app_secret" required:"true" default:"bR4SJwOkvnG5WvVJ"`
}

var config = driver.Config{
	Name:        "HalalCloud",
	OnlyProxy:   true,
	DefaultRoot: "/",
	NoLinkURL:   true,
}

func init() {
	op.RegisterDriver(func() driver.Driver {
		return &HalalCloud{}
	})
}
