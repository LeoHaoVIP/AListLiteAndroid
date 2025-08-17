package fs

import (
	"context"
	"path"
	"path/filepath"

	"github.com/OpenListTeam/OpenList/v4/internal/conf"
	"github.com/OpenListTeam/OpenList/v4/internal/model"
	"github.com/OpenListTeam/OpenList/v4/internal/op"
)

// WalkFS traverses filesystem fs starting at name up to depth levels.
//
// WalkFS will stop when current depth > `depth`. For each visited node,
// WalkFS calls walkFn. If a visited file system node is a directory and
// walkFn returns path.SkipDir, walkFS will skip traversal of this node.
func WalkFS(ctx context.Context, depth int, name string, info model.Obj, walkFn func(reqPath string, info model.Obj) error) error {
	// This implementation is based on Walk's code in the standard path/path package.
	walkFnErr := walkFn(name, info)
	if walkFnErr != nil {
		if info.IsDir() && walkFnErr == filepath.SkipDir {
			return nil
		}
		return walkFnErr
	}
	if !info.IsDir() || depth == 0 {
		return nil
	}
	meta, _ := op.GetNearestMeta(name)
	// Read directory names.
	objs, err := List(context.WithValue(ctx, conf.MetaKey, meta), name, &ListArgs{})
	if err != nil {
		return walkFnErr
	}
	for _, fileInfo := range objs {
		filename := path.Join(name, fileInfo.GetName())
		if err := WalkFS(ctx, depth-1, filename, fileInfo, walkFn); err != nil {
			if err == filepath.SkipDir {
				break
			}
			return err
		}
	}
	return nil
}
