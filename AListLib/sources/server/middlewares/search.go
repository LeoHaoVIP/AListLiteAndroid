package middlewares

import (
	"github.com/OpenListTeam/OpenList/internal/conf"
	"github.com/OpenListTeam/OpenList/internal/errs"
	"github.com/OpenListTeam/OpenList/internal/setting"
	"github.com/OpenListTeam/OpenList/server/common"
	"github.com/gin-gonic/gin"
)

func SearchIndex(c *gin.Context) {
	mode := setting.GetStr(conf.SearchIndex)
	if mode == "none" {
		common.ErrorResp(c, errs.SearchNotAvailable, 500)
		c.Abort()
	} else {
		c.Next()
	}
}
