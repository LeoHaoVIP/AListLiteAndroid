package alistlib

import (
	"context"
	"github.com/alist-org/alist/v3/cmd"
	"github.com/alist-org/alist/v3/cmd/flags"
	"github.com/alist-org/alist/v3/internal/model"
	"github.com/alist-org/alist/v3/internal/op"
	"github.com/alist-org/alist/v3/pkg/utils"
)

func SetConfigData(path string) {
	flags.DataDir = path
}

func SetConfigLogStd(b bool) {
	flags.LogStd = b
}

func SetConfigDebug(b bool) {
	flags.Debug = b
}

func SetConfigNoPrefix(b bool) {
	flags.NoPrefix = b
}

func GetAllStorages() int {
	var drivers = op.GetAllStorages()
	return len(drivers)
}

func AddLocalStorage(localPath string, mountPath string) {
	//设置本地存储
	storage := model.Storage{Driver: "Local",
		MountPath: mountPath, Proxy: model.Proxy{WebdavPolicy: "native_proxy"},
		EnableSign: false,
		Addition:   "{\"root_folder_path\":\"" + localPath + "\",\"thumbnail\":false,\"thumb_cache_folder\":\"\",\"show_hidden\":true,\"mkdir_perm\":\"777\",\"recycle_bin_path\":\"delete permanently\"}"}
	//创建本地存储
	storageId, err := op.CreateStorage(context.Background(), storage)
	if err != nil {
		utils.Log.Errorf("failed to mount local storage: %+v", err)
		return
	}
	utils.Log.Infof("success: mount local storage with id:%+v", storageId)
}

func SetAdminPassword(pwd string) {
	admin, err := op.GetAdmin()
	if err != nil {
		utils.Log.Errorf("failed get admin user: %+v", err)
		return
	}
	admin.SetPassword(pwd)
	if err := op.UpdateUser(admin); err != nil {
		utils.Log.Errorf("failed update admin user: %+v", err)
		return
	}
	utils.Log.Infof("admin user has been updated:")
	utils.Log.Infof("username: %s", admin.Username)
	utils.Log.Infof("password: %s", pwd)
	cmd.DelAdminCacheOnline()
}
