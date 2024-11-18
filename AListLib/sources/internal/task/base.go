package task

import (
	"github.com/alist-org/alist/v3/internal/model"
	"github.com/xhofe/tache"
)

type TaskWithCreator struct {
	tache.Base
	Creator *model.User
}

func (t *TaskWithCreator) SetCreator(creator *model.User) {
	t.Creator = creator
	t.Persist()
}

func (t *TaskWithCreator) GetCreator() *model.User {
	return t.Creator
}

type TaskInfoWithCreator interface {
	tache.TaskWithInfo
	SetCreator(creator *model.User)
	GetCreator() *model.User
}
