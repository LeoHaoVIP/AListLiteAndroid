package doubao

import (
	"github.com/OpenListTeam/OpenList/v4/internal/driver"
	"github.com/OpenListTeam/OpenList/v4/internal/op"
)

type Addition struct {
	// Usually one of two
	// driver.RootPath
	driver.RootID
	// define other
	Cookie       string `json:"cookie" type:"text"`
	UploadThread string `json:"upload_thread" default:"3"`
	DownloadApi  string `json:"download_api" type:"select" options:"get_file_url,get_download_info" default:"get_file_url"`
}

var config = driver.Config{
	Name:        "Doubao",
	LocalSort:   true,
	DefaultRoot: "0",
}

func init() {
	op.RegisterDriver(func() driver.Driver {
		return &Doubao{}
	})
}
