package server

import (
	"github.com/OpenListTeam/OpenList/v4/cmd/flags"
	"github.com/OpenListTeam/OpenList/v4/internal/conf"
	"github.com/OpenListTeam/OpenList/v4/internal/message"
	"github.com/OpenListTeam/OpenList/v4/internal/sign"
	"github.com/OpenListTeam/OpenList/v4/internal/stream"
	"github.com/OpenListTeam/OpenList/v4/pkg/utils"
	"github.com/OpenListTeam/OpenList/v4/server/common"
	"github.com/OpenListTeam/OpenList/v4/server/handles"
	"github.com/OpenListTeam/OpenList/v4/server/middlewares"
	"github.com/OpenListTeam/OpenList/v4/server/static"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

func Init(e *gin.Engine) {
	e.ContextWithFallback = true
	if !utils.SliceContains([]string{"", "/"}, conf.URL.Path) {
		e.GET("/", func(c *gin.Context) {
			c.Redirect(302, conf.URL.Path)
		})
	}
	Cors(e)
	g := e.Group(conf.URL.Path)
	if conf.Conf.Scheme.HttpPort != -1 && conf.Conf.Scheme.HttpsPort != -1 && conf.Conf.Scheme.ForceHttps {
		e.Use(middlewares.ForceHttps)
	}
	g.Any("/ping", func(c *gin.Context) {
		c.String(200, "pong")
	})
	g.GET("/favicon.ico", handles.Favicon)
	g.GET("/robots.txt", handles.Robots)
	g.GET("/manifest.json", static.ManifestJSON)
	g.GET("/i/:link_name", handles.Plist)
	common.SecretKey = []byte(conf.Conf.JwtSecret)
	g.Use(middlewares.StoragesLoaded)
	if conf.Conf.MaxConnections > 0 {
		g.Use(middlewares.MaxAllowed(conf.Conf.MaxConnections))
	}
	WebDav(g.Group("/dav"))
	S3(g.Group("/s3"))

	downloadLimiter := middlewares.DownloadRateLimiter(stream.ClientDownloadLimit)
	signCheck := middlewares.Down(sign.Verify)
	g.GET("/d/*path", signCheck, downloadLimiter, handles.Down)
	g.GET("/p/*path", signCheck, downloadLimiter, handles.Proxy)
	g.HEAD("/d/*path", signCheck, handles.Down)
	g.HEAD("/p/*path", signCheck, handles.Proxy)
	archiveSignCheck := middlewares.Down(sign.VerifyArchive)
	g.GET("/ad/*path", archiveSignCheck, downloadLimiter, handles.ArchiveDown)
	g.GET("/ap/*path", archiveSignCheck, downloadLimiter, handles.ArchiveProxy)
	g.GET("/ae/*path", archiveSignCheck, downloadLimiter, handles.ArchiveInternalExtract)
	g.HEAD("/ad/*path", archiveSignCheck, handles.ArchiveDown)
	g.HEAD("/ap/*path", archiveSignCheck, handles.ArchiveProxy)
	g.HEAD("/ae/*path", archiveSignCheck, handles.ArchiveInternalExtract)

	api := g.Group("/api")
	auth := api.Group("", middlewares.Auth)
	webauthn := api.Group("/authn", middlewares.Authn)

	api.POST("/auth/login", handles.Login)
	api.POST("/auth/login/hash", handles.LoginHash)
	api.POST("/auth/login/ldap", handles.LoginLdap)
	auth.GET("/me", handles.CurrentUser)
	auth.POST("/me/update", handles.UpdateCurrent)
	auth.GET("/me/sshkey/list", handles.ListMyPublicKey)
	auth.POST("/me/sshkey/add", handles.AddMyPublicKey)
	auth.POST("/me/sshkey/delete", handles.DeleteMyPublicKey)
	auth.POST("/auth/2fa/generate", handles.Generate2FA)
	auth.POST("/auth/2fa/verify", handles.Verify2FA)
	auth.GET("/auth/logout", handles.LogOut)

	// auth
	api.GET("/auth/sso", handles.SSOLoginRedirect)
	api.GET("/auth/sso_callback", handles.SSOLoginCallback)
	api.GET("/auth/get_sso_id", handles.SSOLoginCallback)
	api.GET("/auth/sso_get_token", handles.SSOLoginCallback)

	// webauthn
	api.GET("/authn/webauthn_begin_login", handles.BeginAuthnLogin)
	api.POST("/authn/webauthn_finish_login", handles.FinishAuthnLogin)
	webauthn.GET("/webauthn_begin_registration", handles.BeginAuthnRegistration)
	webauthn.POST("/webauthn_finish_registration", handles.FinishAuthnRegistration)
	webauthn.POST("/delete_authn", handles.DeleteAuthnLogin)
	webauthn.GET("/getcredentials", handles.GetAuthnCredentials)

	// no need auth
	public := api.Group("/public")
	public.Any("/settings", handles.PublicSettings)
	public.Any("/offline_download_tools", handles.OfflineDownloadTools)
	public.Any("/archive_extensions", handles.ArchiveExtensions)

	_fs(auth.Group("/fs"))
	_task(auth.Group("/task", middlewares.AuthNotGuest))
	admin(auth.Group("/admin", middlewares.AuthAdmin))
	if flags.Debug || flags.Dev {
		debug(g.Group("/debug"))
	}
	static.Static(g, func(handlers ...gin.HandlerFunc) {
		e.NoRoute(handlers...)
	})
}

func admin(g *gin.RouterGroup) {
	meta := g.Group("/meta")
	meta.GET("/list", handles.ListMetas)
	meta.GET("/get", handles.GetMeta)
	meta.POST("/create", handles.CreateMeta)
	meta.POST("/update", handles.UpdateMeta)
	meta.POST("/delete", handles.DeleteMeta)

	user := g.Group("/user")
	user.GET("/list", handles.ListUsers)
	user.GET("/get", handles.GetUser)
	user.POST("/create", handles.CreateUser)
	user.POST("/update", handles.UpdateUser)
	user.POST("/cancel_2fa", handles.Cancel2FAById)
	user.POST("/delete", handles.DeleteUser)
	user.POST("/del_cache", handles.DelUserCache)
	user.GET("/sshkey/list", handles.ListPublicKeys)
	user.POST("/sshkey/delete", handles.DeletePublicKey)

	storage := g.Group("/storage")
	storage.GET("/list", handles.ListStorages)
	storage.GET("/get", handles.GetStorage)
	storage.POST("/create", handles.CreateStorage)
	storage.POST("/update", handles.UpdateStorage)
	storage.POST("/delete", handles.DeleteStorage)
	storage.POST("/enable", handles.EnableStorage)
	storage.POST("/disable", handles.DisableStorage)
	storage.POST("/load_all", handles.LoadAllStorages)

	driver := g.Group("/driver")
	driver.GET("/list", handles.ListDriverInfo)
	driver.GET("/names", handles.ListDriverNames)
	driver.GET("/info", handles.GetDriverInfo)

	setting := g.Group("/setting")
	setting.GET("/get", handles.GetSetting)
	setting.GET("/list", handles.ListSettings)
	setting.POST("/save", handles.SaveSettings)
	setting.POST("/delete", handles.DeleteSetting)
	setting.POST("/default", handles.DefaultSettings)
	setting.POST("/reset_token", handles.ResetToken)
	setting.POST("/set_aria2", handles.SetAria2)
	setting.POST("/set_qbit", handles.SetQbittorrent)
	setting.POST("/set_transmission", handles.SetTransmission)
	setting.POST("/set_115", handles.Set115)
	setting.POST("/set_115_open", handles.Set115Open)
	setting.POST("/set_pikpak", handles.SetPikPak)
	setting.POST("/set_thunder", handles.SetThunder)
	setting.POST("/set_thunderx", handles.SetThunderX)
	setting.POST("/set_thunder_browser", handles.SetThunderBrowser)

	// retain /admin/task API to ensure compatibility with legacy automation scripts
	_task(g.Group("/task"))

	ms := g.Group("/message")
	ms.POST("/get", message.HttpInstance.GetHandle)
	ms.POST("/send", message.HttpInstance.SendHandle)

	index := g.Group("/index")
	index.POST("/build", middlewares.SearchIndex, handles.BuildIndex)
	index.POST("/update", middlewares.SearchIndex, handles.UpdateIndex)
	index.POST("/stop", middlewares.SearchIndex, handles.StopIndex)
	index.POST("/clear", middlewares.SearchIndex, handles.ClearIndex)
	index.GET("/progress", middlewares.SearchIndex, handles.GetProgress)
}

func _fs(g *gin.RouterGroup) {
	g.Any("/list", handles.FsList)
	g.Any("/search", middlewares.SearchIndex, handles.Search)
	g.Any("/get", handles.FsGet)
	g.Any("/other", handles.FsOther)
	g.Any("/dirs", handles.FsDirs)
	g.POST("/mkdir", handles.FsMkdir)
	g.POST("/rename", handles.FsRename)
	g.POST("/batch_rename", handles.FsBatchRename)
	g.POST("/regex_rename", handles.FsRegexRename)
	g.POST("/move", handles.FsMove)
	g.POST("/recursive_move", handles.FsRecursiveMove)
	g.POST("/copy", handles.FsCopy)
	g.POST("/remove", handles.FsRemove)
	g.POST("/remove_empty_directory", handles.FsRemoveEmptyDirectory)
	uploadLimiter := middlewares.UploadRateLimiter(stream.ClientUploadLimit)
	g.PUT("/put", middlewares.FsUp, uploadLimiter, handles.FsStream)
	g.PUT("/form", middlewares.FsUp, uploadLimiter, handles.FsForm)
	g.POST("/link", middlewares.AuthAdmin, handles.Link)
	// g.POST("/add_aria2", handles.AddOfflineDownload)
	// g.POST("/add_qbit", handles.AddQbittorrent)
	// g.POST("/add_transmission", handles.SetTransmission)
	g.POST("/add_offline_download", handles.AddOfflineDownload)
	a := g.Group("/archive")
	a.Any("/meta", handles.FsArchiveMeta)
	a.Any("/list", handles.FsArchiveList)
	a.POST("/decompress", handles.FsArchiveDecompress)
}

func _task(g *gin.RouterGroup) {
	handles.SetupTaskRoute(g)
}

func Cors(r *gin.Engine) {
	config := cors.DefaultConfig()
	// config.AllowAllOrigins = true
	config.AllowOrigins = conf.Conf.Cors.AllowOrigins
	config.AllowHeaders = conf.Conf.Cors.AllowHeaders
	config.AllowMethods = conf.Conf.Cors.AllowMethods
	r.Use(cors.New(config))
}

func InitS3(e *gin.Engine) {
	Cors(e)
	S3Server(e.Group("/"))
}
