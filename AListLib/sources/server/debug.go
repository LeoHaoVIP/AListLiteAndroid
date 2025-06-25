package server

import (
	"net/http"
	_ "net/http/pprof"
	"runtime"

	"github.com/OpenListTeam/OpenList/internal/sign"
	"github.com/OpenListTeam/OpenList/server/common"
	"github.com/OpenListTeam/OpenList/server/middlewares"
	"github.com/gin-gonic/gin"
)

func _pprof(g *gin.RouterGroup) {
	g.Any("/*name", gin.WrapH(http.DefaultServeMux))
}

func debug(g *gin.RouterGroup) {
	g.GET("/path/*path", middlewares.Down(sign.Verify), func(ctx *gin.Context) {
		rawPath := ctx.MustGet("path").(string)
		ctx.JSON(200, gin.H{
			"path": rawPath,
		})
	})
	g.GET("/hide_privacy", func(ctx *gin.Context) {
		common.ErrorStrResp(ctx, "This is ip: 1.1.1.1", 400)
	})
	g.GET("/gc", func(c *gin.Context) {
		runtime.GC()
		c.String(http.StatusOK, "ok")
	})
	_pprof(g.Group("/pprof"))
}
