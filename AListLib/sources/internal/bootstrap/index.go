package bootstrap

import (
	"github.com/OpenListTeam/OpenList/v4/internal/search"
	log "github.com/sirupsen/logrus"
)

func InitIndex() {
	progress, err := search.Progress()
	if err != nil {
		log.Errorf("init index error: %+v", err)
		return
	}
	if !progress.IsDone {
		progress.IsDone = true
		search.WriteProgress(progress)
	}
}
