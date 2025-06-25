package data

import "github.com/OpenListTeam/OpenList/cmd/flags"

func InitData() {
	initUser()
	initSettings()
	initTasks()
	if flags.Dev {
		initDevData()
		initDevDo()
	}
}
