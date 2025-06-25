package _115_open

import (
	"github.com/OpenListTeam/OpenList/internal/driver"
	"github.com/OpenListTeam/OpenList/internal/op"
)

type Addition struct {
	// Usually one of two
	driver.RootID
	// define other
	OrderBy        string  `json:"order_by" type:"select" options:"file_name,file_size,user_utime,file_type"`
	OrderDirection string  `json:"order_direction" type:"select" options:"asc,desc"`
	LimitRate      float64  `json:"limit_rate,string" type:"float" default:"1" help:"limit all api request rate ([limit]r/1s)"`
	AccessToken    string  `json:"access_token" required:"true"`
	RefreshToken   string  `json:"refresh_token" required:"true"`
}

var config = driver.Config{
	Name:              "115 Open",
	LocalSort:         false,
	OnlyLocal:         false,
	OnlyProxy:         false,
	NoCache:           false,
	NoUpload:          false,
	NeedMs:            false,
	DefaultRoot:       "0",
	CheckStatus:       false,
	Alert:             "",
	NoOverwriteUpload: false,
}

func init() {
	op.RegisterDriver(func() driver.Driver {
		return &Open115{}
	})
}
