package weiyun

import (
	"github.com/OpenListTeam/OpenList/v4/internal/driver"
	"github.com/OpenListTeam/OpenList/v4/internal/op"
)

type Addition struct {
	RootFolderID   string `json:"root_folder_id"`
	Cookies        string `json:"cookies" required:"true"`
	OrderBy        string `json:"order_by" type:"select" options:"name,size,updated_at" default:"name"`
	OrderDirection string `json:"order_direction" type:"select" options:"asc,desc" default:"asc"`
	UploadThread   string `json:"upload_thread" default:"4" help:"4<=thread<=32"`
}

var config = driver.Config{
	Name:        "WeiYun",
	OnlyProxy:   true,
	CheckStatus: true,
}

func init() {
	op.RegisterDriver(func() driver.Driver {
		return &WeiYun{}
	})
}
