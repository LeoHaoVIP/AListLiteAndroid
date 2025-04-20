package ipfs

import (
	"context"
	"fmt"
	"net/url"
	"path/filepath"
	"strings"

	shell "github.com/ipfs/go-ipfs-api"

	"github.com/alist-org/alist/v3/internal/driver"
	"github.com/alist-org/alist/v3/internal/model"
)

type IPFS struct {
	model.Storage
	Addition
	sh      *shell.Shell
	gateURL *url.URL
}

func (d *IPFS) Config() driver.Config {
	return config
}

func (d *IPFS) GetAddition() driver.Additional {
	return &d.Addition
}

func (d *IPFS) Init(ctx context.Context) error {
	d.sh = shell.NewShell(d.Endpoint)
	gateURL, err := url.Parse(d.Gateway)
	if err != nil {
		return err
	}
	d.gateURL = gateURL
	return nil
}

func (d *IPFS) Drop(ctx context.Context) error {
	return nil
}

func (d *IPFS) List(ctx context.Context, dir model.Obj, args model.ListArgs) ([]model.Obj, error) {
	path := dir.GetPath()
	switch d.Mode {
	case "ipfs":
		path, _ = url.JoinPath("/ipfs", path)
	case "ipns":
		path, _ = url.JoinPath("/ipns", path)
	case "mfs":
		fileStat, err := d.sh.FilesStat(ctx, path)
		if err != nil {
			return nil, err
		}
		path, _ = url.JoinPath("/ipfs", fileStat.Hash)
	default:
		return nil, fmt.Errorf("mode error")
	}

	dirs, err := d.sh.List(path)
	if err != nil {
		return nil, err
	}

	objlist := []model.Obj{}
	for _, file := range dirs {
		gateurl := *d.gateURL.JoinPath("/ipfs/" + file.Hash)
		gateurl.RawQuery = "filename=" + url.PathEscape(file.Name)
		objlist = append(objlist, &model.ObjectURL{
			Object: model.Object{ID: "/ipfs/" + file.Hash, Name: file.Name, Size: int64(file.Size), IsFolder: file.Type == 1},
			Url:    model.Url{Url: gateurl.String()},
		})
	}

	return objlist, nil
}

func (d *IPFS) Link(ctx context.Context, file model.Obj, args model.LinkArgs) (*model.Link, error) {
	gateurl := d.gateURL.JoinPath(file.GetID())
	gateurl.RawQuery = "filename=" + url.PathEscape(file.GetName())
	return &model.Link{URL: gateurl.String()}, nil
}

func (d *IPFS) MakeDir(ctx context.Context, parentDir model.Obj, dirName string) error {
	if d.Mode != "mfs" {
		return fmt.Errorf("only write in mfs mode")
	}
	path := parentDir.GetPath()
	if path[len(path):] != "/" {
		path += "/"
	}
	return d.sh.FilesMkdir(ctx, path+dirName)
}

func (d *IPFS) Move(ctx context.Context, srcObj, dstDir model.Obj) error {
	if d.Mode != "mfs" {
		return fmt.Errorf("only write in mfs mode")
	}
	return d.sh.FilesMv(ctx, srcObj.GetPath(), dstDir.GetPath())
}

func (d *IPFS) Rename(ctx context.Context, srcObj model.Obj, newName string) error {
	if d.Mode != "mfs" {
		return fmt.Errorf("only write in mfs mode")
	}
	newFileName := filepath.Dir(srcObj.GetPath()) + "/" + newName
	return d.sh.FilesMv(ctx, srcObj.GetPath(), strings.ReplaceAll(newFileName, "\\", "/"))
}

func (d *IPFS) Copy(ctx context.Context, srcObj, dstDir model.Obj) error {
	if d.Mode != "mfs" {
		return fmt.Errorf("only write in mfs mode")
	}
	newFileName := dstDir.GetPath() + "/" + filepath.Base(srcObj.GetPath())
	return d.sh.FilesCp(ctx, srcObj.GetPath(), strings.ReplaceAll(newFileName, "\\", "/"))
}

func (d *IPFS) Remove(ctx context.Context, obj model.Obj) error {
	if d.Mode != "mfs" {
		return fmt.Errorf("only write in mfs mode")
	}
	return d.sh.FilesRm(ctx, obj.GetPath(), true)
}

func (d *IPFS) Put(ctx context.Context, dstDir model.Obj, s model.FileStreamer, up driver.UpdateProgress) error {
	if d.Mode != "mfs" {
		return fmt.Errorf("only write in mfs mode")
	}
	outHash, err := d.sh.Add(driver.NewLimitedUploadStream(ctx, &driver.ReaderUpdatingProgress{
		Reader:         s,
		UpdateProgress: up,
	}))
	if err != nil {
		return err
	}
	err = d.sh.FilesCp(ctx, "/ipfs/"+outHash, dstDir.GetPath()+"/"+strings.ReplaceAll(s.GetName(), "\\", "/"))
	return err
}

//func (d *Template) Other(ctx context.Context, args model.OtherArgs) (interface{}, error) {
//	return nil, errs.NotSupport
//}

var _ driver.Driver = (*IPFS)(nil)
