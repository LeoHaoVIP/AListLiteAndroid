package bootstrap

import (
	"github.com/OpenListTeam/OpenList/v4/internal/offline_download/tool"
	"github.com/OpenListTeam/OpenList/v4/pkg/utils"
)

func InitOfflineDownloadTools() {
	for k, v := range tool.Tools {
		res, err := v.Init()
		if err != nil {
			utils.Log.Warnf("init offline download tool %s failed: %s", k, err)
		} else {
			utils.Log.Infof("init offline download tool %s success: %s", k, res)
		}
	}
}
