package zip

import (
	"io"
	"os"
	stdpath "path"
	"strings"

	"github.com/alist-org/alist/v3/internal/archive/tool"
	"github.com/alist-org/alist/v3/internal/errs"
	"github.com/alist-org/alist/v3/internal/model"
	"github.com/alist-org/alist/v3/internal/stream"
	"github.com/yeka/zip"
)

type Zip struct {
}

func (*Zip) AcceptedExtensions() []string {
	return []string{".zip"}
}

func (*Zip) GetMeta(ss *stream.SeekableStream, args model.ArchiveArgs) (model.ArchiveMeta, error) {
	reader, err := stream.NewReadAtSeeker(ss, 0)
	if err != nil {
		return nil, err
	}
	zipReader, err := zip.NewReader(reader, ss.GetSize())
	if err != nil {
		return nil, err
	}
	encrypted := false
	dirMap := make(map[string]*model.ObjectTree)
	dirMap["."] = &model.ObjectTree{}
	for _, file := range zipReader.File {
		if file.IsEncrypted() {
			encrypted = true
			break
		}

		name := strings.TrimPrefix(decodeName(file.Name), "/")
		var dir string
		var dirObj *model.ObjectTree
		isNewFolder := false
		if !file.FileInfo().IsDir() {
			// 先将 文件 添加到 所在的文件夹
			dir = stdpath.Dir(name)
			dirObj = dirMap[dir]
			if dirObj == nil {
				isNewFolder = true
				dirObj = &model.ObjectTree{}
				dirObj.IsFolder = true
				dirObj.Name = stdpath.Base(dir)
				dirObj.Modified = file.ModTime()
				dirMap[dir] = dirObj
			}
			dirObj.Children = append(
				dirObj.Children, &model.ObjectTree{
					Object: *toModelObj(file.FileInfo()),
				},
			)
		} else {
			dir = strings.TrimSuffix(name, "/")
			dirObj = dirMap[dir]
			if dirObj == nil {
				isNewFolder = true
				dirObj = &model.ObjectTree{}
				dirMap[dir] = dirObj
			}
			dirObj.IsFolder = true
			dirObj.Name = stdpath.Base(dir)
			dirObj.Modified = file.ModTime()
		}
		if isNewFolder {
			// 将 文件夹 添加到 父文件夹
			dir = stdpath.Dir(dir)
			pDirObj := dirMap[dir]
			if pDirObj != nil {
				pDirObj.Children = append(pDirObj.Children, dirObj)
				continue
			}

			for {
				//	考虑压缩包仅记录文件的路径，不记录文件夹
				pDirObj = &model.ObjectTree{}
				pDirObj.IsFolder = true
				pDirObj.Name = stdpath.Base(dir)
				pDirObj.Modified = file.ModTime()
				dirMap[dir] = pDirObj
				pDirObj.Children = append(pDirObj.Children, dirObj)
				dir = stdpath.Dir(dir)
				if dirMap[dir] != nil {
					break
				}
				dirObj = pDirObj
			}
		}
	}

	return &model.ArchiveMetaInfo{
		Comment:   zipReader.Comment,
		Encrypted: encrypted,
		Tree:      dirMap["."].GetChildren(),
	}, nil
}

func (*Zip) List(ss *stream.SeekableStream, args model.ArchiveInnerArgs) ([]model.Obj, error) {
	reader, err := stream.NewReadAtSeeker(ss, 0)
	if err != nil {
		return nil, err
	}
	zipReader, err := zip.NewReader(reader, ss.GetSize())
	if err != nil {
		return nil, err
	}
	if args.InnerPath == "/" {
		ret := make([]model.Obj, 0)
		passVerified := false
		var dir *model.Object
		for _, file := range zipReader.File {
			if !passVerified && file.IsEncrypted() {
				file.SetPassword(args.Password)
				rc, e := file.Open()
				if e != nil {
					return nil, filterPassword(e)
				}
				_ = rc.Close()
				passVerified = true
			}
			name := strings.TrimSuffix(decodeName(file.Name), "/")
			if strings.Contains(name, "/") {
				// 有些压缩包不压缩第一个文件夹
				strs := strings.Split(name, "/")
				if dir == nil && len(strs) == 2 {
					dir = &model.Object{
						Name:     strs[0],
						Modified: ss.ModTime(),
						IsFolder: true,
					}
				}
				continue
			}
			ret = append(ret, toModelObj(file.FileInfo()))
		}
		if len(ret) == 0 && dir != nil {
			ret = append(ret, dir)
		}
		return ret, nil
	} else {
		innerPath := strings.TrimPrefix(args.InnerPath, "/") + "/"
		ret := make([]model.Obj, 0)
		exist := false
		for _, file := range zipReader.File {
			name := decodeName(file.Name)
			dir := stdpath.Dir(strings.TrimSuffix(name, "/")) + "/"
			if dir != innerPath {
				continue
			}
			exist = true
			ret = append(ret, toModelObj(file.FileInfo()))
		}
		if !exist {
			return nil, errs.ObjectNotFound
		}
		return ret, nil
	}
}

func (*Zip) Extract(ss *stream.SeekableStream, args model.ArchiveInnerArgs) (io.ReadCloser, int64, error) {
	reader, err := stream.NewReadAtSeeker(ss, 0)
	if err != nil {
		return nil, 0, err
	}
	zipReader, err := zip.NewReader(reader, ss.GetSize())
	if err != nil {
		return nil, 0, err
	}
	innerPath := strings.TrimPrefix(args.InnerPath, "/")
	for _, file := range zipReader.File {
		if decodeName(file.Name) == innerPath {
			if file.IsEncrypted() {
				file.SetPassword(args.Password)
			}
			r, e := file.Open()
			if e != nil {
				return nil, 0, e
			}
			return r, file.FileInfo().Size(), nil
		}
	}
	return nil, 0, errs.ObjectNotFound
}

func (*Zip) Decompress(ss *stream.SeekableStream, outputPath string, args model.ArchiveInnerArgs, up model.UpdateProgress) error {
	reader, err := stream.NewReadAtSeeker(ss, 0)
	if err != nil {
		return err
	}
	zipReader, err := zip.NewReader(reader, ss.GetSize())
	if err != nil {
		return err
	}
	if args.InnerPath == "/" {
		for i, file := range zipReader.File {
			name := decodeName(file.Name)
			err = decompress(file, name, outputPath, args.Password)
			if err != nil {
				return err
			}
			up(float64(i+1) * 100.0 / float64(len(zipReader.File)))
		}
	} else {
		innerPath := strings.TrimPrefix(args.InnerPath, "/")
		innerBase := stdpath.Base(innerPath)
		createdBaseDir := false
		for _, file := range zipReader.File {
			name := decodeName(file.Name)
			if name == innerPath {
				err = _decompress(file, outputPath, args.Password, up)
				if err != nil {
					return err
				}
				break
			} else if strings.HasPrefix(name, innerPath+"/") {
				targetPath := stdpath.Join(outputPath, innerBase)
				if !createdBaseDir {
					err = os.Mkdir(targetPath, 0700)
					if err != nil {
						return err
					}
					createdBaseDir = true
				}
				restPath := strings.TrimPrefix(name, innerPath+"/")
				err = decompress(file, restPath, targetPath, args.Password)
				if err != nil {
					return err
				}
			}
		}
	}
	return nil
}

var _ tool.Tool = (*Zip)(nil)

func init() {
	tool.RegisterTool(&Zip{})
}
