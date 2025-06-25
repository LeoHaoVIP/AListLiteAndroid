package baidu_share

import (
	"github.com/OpenListTeam/OpenList/internal/driver"
	"github.com/OpenListTeam/OpenList/internal/op"
)

type Addition struct {
	// Usually one of two
	driver.RootPath
	// driver.RootID
	// define other
	// Field string `json:"field" type:"select" required:"true" options:"a,b,c" default:"a"`
	Surl  string `json:"surl"`
	Pwd   string `json:"pwd"`
	BDUSS string `json:"BDUSS"`
}

var config = driver.Config{
	Name:              "BaiduShare",
	LocalSort:         true,
	OnlyLocal:         false,
	OnlyProxy:         false,
	NoCache:           false,
	NoUpload:          true,
	NeedMs:            false,
	DefaultRoot:       "/",
	CheckStatus:       false,
	Alert:             "",
	NoOverwriteUpload: false,
}

func init() {
	op.RegisterDriver(func() driver.Driver {
		return &BaiduShare{}
	})
}
