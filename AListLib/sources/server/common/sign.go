package common

import (
	stdpath "path"

	"github.com/OpenListTeam/OpenList/internal/conf"
	"github.com/OpenListTeam/OpenList/internal/model"
	"github.com/OpenListTeam/OpenList/internal/setting"
	"github.com/OpenListTeam/OpenList/internal/sign"
)

func Sign(obj model.Obj, parent string, encrypt bool) string {
	if obj.IsDir() || (!encrypt && !setting.GetBool(conf.SignAll)) {
		return ""
	}
	return sign.Sign(stdpath.Join(parent, obj.GetName()))
}
