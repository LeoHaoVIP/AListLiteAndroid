package handles

import (
	"fmt"
	"regexp"
	"slices"

	"github.com/OpenListTeam/OpenList/v4/internal/conf"
	"github.com/OpenListTeam/OpenList/v4/internal/errs"
	"github.com/OpenListTeam/OpenList/v4/internal/fs"
	"github.com/OpenListTeam/OpenList/v4/internal/model"
	"github.com/OpenListTeam/OpenList/v4/internal/op"
	"github.com/OpenListTeam/OpenList/v4/pkg/generic"
	"github.com/OpenListTeam/OpenList/v4/server/common"
	"github.com/gin-gonic/gin"
	"github.com/pkg/errors"
)

type RecursiveMoveReq struct {
	SrcDir         string `json:"src_dir"`
	DstDir         string `json:"dst_dir"`
	ConflictPolicy string `json:"conflict_policy"`
}

func FsRecursiveMove(c *gin.Context) {
	var req RecursiveMoveReq
	if err := c.ShouldBind(&req); err != nil {
		common.ErrorResp(c, err, 400)
		return
	}

	user := c.Request.Context().Value(conf.UserKey).(*model.User)
	if !user.CanMove() {
		common.ErrorResp(c, errs.PermissionDenied, 403)
		return
	}
	srcDir, err := user.JoinPath(req.SrcDir)
	if err != nil {
		common.ErrorResp(c, err, 403)
		return
	}
	dstDir, err := user.JoinPath(req.DstDir)
	if err != nil {
		common.ErrorResp(c, err, 403)
		return
	}

	meta, err := op.GetNearestMeta(srcDir)
	if err != nil {
		if !errors.Is(errors.Cause(err), errs.MetaNotFound) {
			common.ErrorResp(c, err, 500, true)
			return
		}
	}
	common.GinWithValue(c, conf.MetaKey, meta)

	rootFiles, err := fs.List(c.Request.Context(), srcDir, &fs.ListArgs{})
	if err != nil {
		common.ErrorResp(c, err, 500)
		return
	}

	var existingFileNames []string
	if req.ConflictPolicy != OVERWRITE {
		dstFiles, err := fs.List(c.Request.Context(), dstDir, &fs.ListArgs{})
		if err != nil {
			common.ErrorResp(c, err, 500)
			return
		}
		existingFileNames = make([]string, 0, len(dstFiles))
		for _, dstFile := range dstFiles {
			existingFileNames = append(existingFileNames, dstFile.GetName())
		}
	}

	// record the file path
	filePathMap := make(map[model.Obj]string)
	movingFiles := generic.NewQueue[model.Obj]()
	movingFileNames := make([]string, 0, len(rootFiles))
	for _, file := range rootFiles {
		movingFiles.Push(file)
		filePathMap[file] = srcDir
	}

	for !movingFiles.IsEmpty() {

		movingFile := movingFiles.Pop()
		movingFilePath := filePathMap[movingFile]
		movingFileName := fmt.Sprintf("%s/%s", movingFilePath, movingFile.GetName())
		if movingFile.IsDir() {
			// directory, recursive move
			subFilePath := movingFileName
			subFiles, err := fs.List(c.Request.Context(), movingFileName, &fs.ListArgs{Refresh: true})
			if err != nil {
				common.ErrorResp(c, err, 500)
				return
			}
			for _, subFile := range subFiles {
				movingFiles.Push(subFile)
				filePathMap[subFile] = subFilePath
			}
		} else {
			if movingFilePath == dstDir {
				// same directory, don't move
				continue
			}

			if slices.Contains(existingFileNames, movingFile.GetName()) {
				if req.ConflictPolicy == CANCEL {
					common.ErrorStrResp(c, fmt.Sprintf("file [%s] exists", movingFile.GetName()), 403)
					return
				} else if req.ConflictPolicy == SKIP {
					continue
				}
			} else if req.ConflictPolicy != OVERWRITE {
				existingFileNames = append(existingFileNames, movingFile.GetName())
			}
			movingFileNames = append(movingFileNames, movingFileName)

		}

	}

	var count = 0
	for i, fileName := range movingFileNames {
		// move
		_, err := fs.Move(c.Request.Context(), fileName, dstDir, len(movingFileNames) > i+1)
		if err != nil {
			common.ErrorResp(c, err, 500)
			return
		}
		count++
	}

	common.SuccessWithMsgResp(c, fmt.Sprintf("Successfully moved %d %s", count, common.Pluralize(count, "file", "files")))
}

type BatchRenameReq struct {
	SrcDir        string `json:"src_dir"`
	RenameObjects []struct {
		SrcName string `json:"src_name"`
		NewName string `json:"new_name"`
	} `json:"rename_objects"`
}

func FsBatchRename(c *gin.Context) {
	var req BatchRenameReq
	if err := c.ShouldBind(&req); err != nil {
		common.ErrorResp(c, err, 400)
		return
	}
	user := c.Request.Context().Value(conf.UserKey).(*model.User)
	if !user.CanRename() {
		common.ErrorResp(c, errs.PermissionDenied, 403)
		return
	}

	reqPath, err := user.JoinPath(req.SrcDir)
	if err != nil {
		common.ErrorResp(c, err, 403)
		return
	}

	meta, err := op.GetNearestMeta(reqPath)
	if err != nil {
		if !errors.Is(errors.Cause(err), errs.MetaNotFound) {
			common.ErrorResp(c, err, 500, true)
			return
		}
	}
	common.GinWithValue(c, conf.MetaKey, meta)
	for _, renameObject := range req.RenameObjects {
		if renameObject.SrcName == "" || renameObject.NewName == "" {
			continue
		}
		err = checkRelativePath(renameObject.NewName)
		if err != nil {
			common.ErrorResp(c, err, 403)
			return
		}
		filePath := fmt.Sprintf("%s/%s", reqPath, renameObject.SrcName)
		if err := fs.Rename(c.Request.Context(), filePath, renameObject.NewName); err != nil {
			common.ErrorResp(c, err, 500)
			return
		}
	}
	common.SuccessResp(c)
}

type RegexRenameReq struct {
	SrcDir       string `json:"src_dir"`
	SrcNameRegex string `json:"src_name_regex"`
	NewNameRegex string `json:"new_name_regex"`
}

func FsRegexRename(c *gin.Context) {
	var req RegexRenameReq
	if err := c.ShouldBind(&req); err != nil {
		common.ErrorResp(c, err, 400)
		return
	}
	user := c.Request.Context().Value(conf.UserKey).(*model.User)
	if !user.CanRename() {
		common.ErrorResp(c, errs.PermissionDenied, 403)
		return
	}

	reqPath, err := user.JoinPath(req.SrcDir)
	if err != nil {
		common.ErrorResp(c, err, 403)
		return
	}

	meta, err := op.GetNearestMeta(reqPath)
	if err != nil {
		if !errors.Is(errors.Cause(err), errs.MetaNotFound) {
			common.ErrorResp(c, err, 500, true)
			return
		}
	}
	common.GinWithValue(c, conf.MetaKey, meta)

	srcRegexp, err := regexp.Compile(req.SrcNameRegex)
	if err != nil {
		common.ErrorResp(c, err, 500)
		return
	}

	files, err := fs.List(c.Request.Context(), reqPath, &fs.ListArgs{})
	if err != nil {
		common.ErrorResp(c, err, 500)
		return
	}

	for _, file := range files {
		if srcRegexp.MatchString(file.GetName()) {
			newFileName := srcRegexp.ReplaceAllString(file.GetName(), req.NewNameRegex)
			err := checkRelativePath(newFileName)
			if err != nil {
				common.ErrorResp(c, err, 403)
				return
			}
			filePath := fmt.Sprintf("%s/%s", reqPath, file.GetName())
			if err := fs.Rename(c.Request.Context(), filePath, newFileName); err != nil {
				common.ErrorResp(c, err, 500)
				return
			}
		}

	}

	common.SuccessResp(c)
}
