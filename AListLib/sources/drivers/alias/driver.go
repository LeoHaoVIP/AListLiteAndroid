package alias

import (
	"context"
	"errors"
	"fmt"
	"io"
	stdpath "path"
	"strings"

	"github.com/OpenListTeam/OpenList/v4/internal/driver"
	"github.com/OpenListTeam/OpenList/v4/internal/errs"
	"github.com/OpenListTeam/OpenList/v4/internal/fs"
	"github.com/OpenListTeam/OpenList/v4/internal/model"
	"github.com/OpenListTeam/OpenList/v4/internal/sign"
	"github.com/OpenListTeam/OpenList/v4/internal/stream"
	"github.com/OpenListTeam/OpenList/v4/pkg/utils"
	"github.com/OpenListTeam/OpenList/v4/server/common"
)

type Alias struct {
	model.Storage
	Addition
	pathMap     map[string][]string
	autoFlatten bool
	oneKey      string
}

func (d *Alias) Config() driver.Config {
	return config
}

func (d *Alias) GetAddition() driver.Additional {
	return &d.Addition
}

func (d *Alias) Init(ctx context.Context) error {
	if d.Paths == "" {
		return errors.New("paths is required")
	}
	d.pathMap = make(map[string][]string)
	for _, path := range strings.Split(d.Paths, "\n") {
		path = strings.TrimSpace(path)
		if path == "" {
			continue
		}
		k, v := getPair(path)
		d.pathMap[k] = append(d.pathMap[k], v)
	}
	if len(d.pathMap) == 1 {
		for k := range d.pathMap {
			d.oneKey = k
		}
		d.autoFlatten = true
	} else {
		d.oneKey = ""
		d.autoFlatten = false
	}
	return nil
}

func (d *Alias) Drop(ctx context.Context) error {
	d.pathMap = nil
	return nil
}

func (d *Alias) Get(ctx context.Context, path string) (model.Obj, error) {
	if utils.PathEqual(path, "/") {
		return &model.Object{
			Name:     "Root",
			IsFolder: true,
			Path:     "/",
		}, nil
	}
	root, sub := d.getRootAndPath(path)
	dsts, ok := d.pathMap[root]
	if !ok {
		return nil, errs.ObjectNotFound
	}
	for _, dst := range dsts {
		obj, err := d.get(ctx, path, dst, sub)
		if err == nil {
			return obj, nil
		}
	}
	return nil, errs.ObjectNotFound
}

func (d *Alias) List(ctx context.Context, dir model.Obj, args model.ListArgs) ([]model.Obj, error) {
	path := dir.GetPath()
	if utils.PathEqual(path, "/") && !d.autoFlatten {
		return d.listRoot(), nil
	}
	root, sub := d.getRootAndPath(path)
	dsts, ok := d.pathMap[root]
	if !ok {
		return nil, errs.ObjectNotFound
	}
	var objs []model.Obj
	fsArgs := &fs.ListArgs{NoLog: true, Refresh: args.Refresh}
	for _, dst := range dsts {
		tmp, err := d.list(ctx, dst, sub, fsArgs)
		if err == nil {
			objs = append(objs, tmp...)
		}
	}
	return objs, nil
}

func (d *Alias) Link(ctx context.Context, file model.Obj, args model.LinkArgs) (*model.Link, error) {
	root, sub := d.getRootAndPath(file.GetPath())
	dsts, ok := d.pathMap[root]
	if !ok {
		return nil, errs.ObjectNotFound
	}
	for _, dst := range dsts {
		reqPath := stdpath.Join(dst, sub)
		link, file, err := d.link(ctx, reqPath, args)
		if err != nil {
			continue
		}
		var resultLink *model.Link
		if link != nil {
			resultLink = &model.Link{
				URL:           link.URL,
				Header:        link.Header,
				RangeReader:   link.RangeReader,
				SyncClosers:   utils.NewSyncClosers(link),
				ContentLength: link.ContentLength,
			}
			if link.MFile != nil {
				resultLink.RangeReader = &model.FileRangeReader{
					RangeReaderIF: stream.GetRangeReaderFromMFile(file.GetSize(), link.MFile),
				}
			}

		} else {
			resultLink = &model.Link{
				URL: fmt.Sprintf("%s/p%s?sign=%s",
					common.GetApiUrl(ctx),
					utils.EncodePath(reqPath, true),
					sign.Sign(reqPath)),
			}

		}
		if !args.Redirect {
			if d.DownloadConcurrency > 0 {
				resultLink.Concurrency = d.DownloadConcurrency
			}
			if d.DownloadPartSize > 0 {
				resultLink.PartSize = d.DownloadPartSize * utils.KB
			}
		}
		return resultLink, nil
	}
	return nil, errs.ObjectNotFound
}

func (d *Alias) MakeDir(ctx context.Context, parentDir model.Obj, dirName string) error {
	if !d.Writable {
		return errs.PermissionDenied
	}
	reqPath, err := d.getReqPath(ctx, parentDir, true)
	if err == nil {
		for _, path := range reqPath {
			err = errors.Join(err, fs.MakeDir(ctx, stdpath.Join(*path, dirName)))
		}
		return err
	}
	if errs.IsNotImplement(err) {
		return errors.New("same-name dirs cannot make sub-dir")
	}
	return err
}

func (d *Alias) Move(ctx context.Context, srcObj, dstDir model.Obj) error {
	if !d.Writable {
		return errs.PermissionDenied
	}
	srcPath, err := d.getReqPath(ctx, srcObj, false)
	if errs.IsNotImplement(err) {
		return errors.New("same-name files cannot be moved")
	}
	if err != nil {
		return err
	}
	dstPath, err := d.getReqPath(ctx, dstDir, true)
	if errs.IsNotImplement(err) {
		return errors.New("same-name dirs cannot be moved to")
	}
	if err != nil {
		return err
	}
	if len(srcPath) == len(dstPath) {
		for i := range srcPath {
			_, e := fs.Move(ctx, *srcPath[i], *dstPath[i])
			err = errors.Join(err, e)
		}
		return err
	} else {
		return errors.New("parallel paths mismatch")
	}
}

func (d *Alias) Rename(ctx context.Context, srcObj model.Obj, newName string) error {
	if !d.Writable {
		return errs.PermissionDenied
	}
	reqPath, err := d.getReqPath(ctx, srcObj, false)
	if err == nil {
		for _, path := range reqPath {
			err = errors.Join(err, fs.Rename(ctx, *path, newName))
		}
		return err
	}
	if errs.IsNotImplement(err) {
		return errors.New("same-name files cannot be Rename")
	}
	return err
}

func (d *Alias) Copy(ctx context.Context, srcObj, dstDir model.Obj) error {
	if !d.Writable {
		return errs.PermissionDenied
	}
	srcPath, err := d.getReqPath(ctx, srcObj, false)
	if errs.IsNotImplement(err) {
		return errors.New("same-name files cannot be copied")
	}
	if err != nil {
		return err
	}
	dstPath, err := d.getReqPath(ctx, dstDir, true)
	if errs.IsNotImplement(err) {
		return errors.New("same-name dirs cannot be copied to")
	}
	if err != nil {
		return err
	}
	if len(srcPath) == len(dstPath) {
		for i := range srcPath {
			_, e := fs.Copy(ctx, *srcPath[i], *dstPath[i])
			err = errors.Join(err, e)
		}
		return err
	} else if len(srcPath) == 1 || !d.ProtectSameName {
		for _, path := range dstPath {
			_, e := fs.Copy(ctx, *srcPath[0], *path)
			err = errors.Join(err, e)
		}
		return err
	} else {
		return errors.New("parallel paths mismatch")
	}
}

func (d *Alias) Remove(ctx context.Context, obj model.Obj) error {
	if !d.Writable {
		return errs.PermissionDenied
	}
	reqPath, err := d.getReqPath(ctx, obj, false)
	if err == nil {
		for _, path := range reqPath {
			err = errors.Join(err, fs.Remove(ctx, *path))
		}
		return err
	}
	if errs.IsNotImplement(err) {
		return errors.New("same-name files cannot be Delete")
	}
	return err
}

func (d *Alias) Put(ctx context.Context, dstDir model.Obj, s model.FileStreamer, up driver.UpdateProgress) error {
	if !d.Writable {
		return errs.PermissionDenied
	}
	reqPath, err := d.getReqPath(ctx, dstDir, true)
	if err == nil {
		if len(reqPath) == 1 {
			return fs.PutDirectly(ctx, *reqPath[0], &stream.FileStream{
				Obj:          s,
				Mimetype:     s.GetMimetype(),
				WebPutAsTask: s.NeedStore(),
				Reader:       s,
			})
		} else {
			file, err := s.CacheFullInTempFile()
			if err != nil {
				return err
			}
			for _, path := range reqPath {
				err = errors.Join(err, fs.PutDirectly(ctx, *path, &stream.FileStream{
					Obj:          s,
					Mimetype:     s.GetMimetype(),
					WebPutAsTask: s.NeedStore(),
					Reader:       file,
				}))
				_, e := file.Seek(0, io.SeekStart)
				if e != nil {
					return errors.Join(err, e)
				}
			}
			return err
		}
	}
	if errs.IsNotImplement(err) {
		return errors.New("same-name dirs cannot be Put")
	}
	return err
}

func (d *Alias) PutURL(ctx context.Context, dstDir model.Obj, name, url string) error {
	if !d.Writable {
		return errs.PermissionDenied
	}
	reqPath, err := d.getReqPath(ctx, dstDir, true)
	if err == nil {
		for _, path := range reqPath {
			err = errors.Join(err, fs.PutURL(ctx, *path, name, url))
		}
		return err
	}
	if errs.IsNotImplement(err) {
		return errors.New("same-name files cannot offline download")
	}
	return err
}

func (d *Alias) GetArchiveMeta(ctx context.Context, obj model.Obj, args model.ArchiveArgs) (model.ArchiveMeta, error) {
	root, sub := d.getRootAndPath(obj.GetPath())
	dsts, ok := d.pathMap[root]
	if !ok {
		return nil, errs.ObjectNotFound
	}
	for _, dst := range dsts {
		meta, err := d.getArchiveMeta(ctx, dst, sub, args)
		if err == nil {
			return meta, nil
		}
	}
	return nil, errs.NotImplement
}

func (d *Alias) ListArchive(ctx context.Context, obj model.Obj, args model.ArchiveInnerArgs) ([]model.Obj, error) {
	root, sub := d.getRootAndPath(obj.GetPath())
	dsts, ok := d.pathMap[root]
	if !ok {
		return nil, errs.ObjectNotFound
	}
	for _, dst := range dsts {
		l, err := d.listArchive(ctx, dst, sub, args)
		if err == nil {
			return l, nil
		}
	}
	return nil, errs.NotImplement
}

func (d *Alias) Extract(ctx context.Context, obj model.Obj, args model.ArchiveInnerArgs) (*model.Link, error) {
	// alias的两个驱动，一个支持驱动提取，一个不支持，如何兼容？
	// 如果访问的是不支持驱动提取的驱动内的压缩文件，GetArchiveMeta就会返回errs.NotImplement，提取URL前缀就会是/ae，Extract就不会被调用
	// 如果访问的是支持驱动提取的驱动内的压缩文件，GetArchiveMeta就会返回有效值，提取URL前缀就会是/ad，Extract就会被调用
	root, sub := d.getRootAndPath(obj.GetPath())
	dsts, ok := d.pathMap[root]
	if !ok {
		return nil, errs.ObjectNotFound
	}
	for _, dst := range dsts {
		link, err := d.extract(ctx, dst, sub, args)
		if err == nil {
			return link, nil
		}
	}
	return nil, errs.NotImplement
}

func (d *Alias) ArchiveDecompress(ctx context.Context, srcObj, dstDir model.Obj, args model.ArchiveDecompressArgs) error {
	if !d.Writable {
		return errs.PermissionDenied
	}
	srcPath, err := d.getReqPath(ctx, srcObj, false)
	if errs.IsNotImplement(err) {
		return errors.New("same-name files cannot be decompressed")
	}
	if err != nil {
		return err
	}
	dstPath, err := d.getReqPath(ctx, dstDir, true)
	if errs.IsNotImplement(err) {
		return errors.New("same-name dirs cannot be decompressed to")
	}
	if err != nil {
		return err
	}
	if len(srcPath) == len(dstPath) {
		for i := range srcPath {
			_, e := fs.ArchiveDecompress(ctx, *srcPath[i], *dstPath[i], args)
			err = errors.Join(err, e)
		}
		return err
	} else if len(srcPath) == 1 || !d.ProtectSameName {
		for _, path := range dstPath {
			_, e := fs.ArchiveDecompress(ctx, *srcPath[0], *path, args)
			err = errors.Join(err, e)
		}
		return err
	} else {
		return errors.New("parallel paths mismatch")
	}
}

var _ driver.Driver = (*Alias)(nil)
