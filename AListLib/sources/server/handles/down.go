package handles

import (
	"bytes"
	"fmt"
	"io"
	"net/http"
	stdpath "path"
	"strconv"
	"strings"

	"github.com/alist-org/alist/v3/internal/conf"
	"github.com/alist-org/alist/v3/internal/driver"
	"github.com/alist-org/alist/v3/internal/fs"
	"github.com/alist-org/alist/v3/internal/model"
	"github.com/alist-org/alist/v3/internal/setting"
	"github.com/alist-org/alist/v3/internal/sign"
	"github.com/alist-org/alist/v3/pkg/utils"
	"github.com/alist-org/alist/v3/server/common"
	"github.com/gin-gonic/gin"
	"github.com/microcosm-cc/bluemonday"
	log "github.com/sirupsen/logrus"
	"github.com/yuin/goldmark"
)

func Down(c *gin.Context) {
	rawPath := c.MustGet("path").(string)
	filename := stdpath.Base(rawPath)
	storage, err := fs.GetStorage(rawPath, &fs.GetStoragesArgs{})
	if err != nil {
		common.ErrorResp(c, err, 500)
		return
	}
	if common.ShouldProxy(storage, filename) {
		Proxy(c)
		return
	} else {
		link, _, err := fs.Link(c, rawPath, model.LinkArgs{
			IP:       c.ClientIP(),
			Header:   c.Request.Header,
			Type:     c.Query("type"),
			HttpReq:  c.Request,
			Redirect: true,
		})
		if err != nil {
			common.ErrorResp(c, err, 500)
			return
		}
		down(c, link)
	}
}

func Proxy(c *gin.Context) {
	rawPath := c.MustGet("path").(string)
	filename := stdpath.Base(rawPath)
	storage, err := fs.GetStorage(rawPath, &fs.GetStoragesArgs{})
	if err != nil {
		common.ErrorResp(c, err, 500)
		return
	}
	if canProxy(storage, filename) {
		downProxyUrl := storage.GetStorage().DownProxyUrl
		if downProxyUrl != "" {
			_, ok := c.GetQuery("d")
			if !ok {
				URL := fmt.Sprintf("%s%s?sign=%s",
					strings.Split(downProxyUrl, "\n")[0],
					utils.EncodePath(rawPath, true),
					sign.Sign(rawPath))
				c.Redirect(302, URL)
				return
			}
		}
		link, file, err := fs.Link(c, rawPath, model.LinkArgs{
			Header:  c.Request.Header,
			Type:    c.Query("type"),
			HttpReq: c.Request,
		})
		if err != nil {
			common.ErrorResp(c, err, 500)
			return
		}
		localProxy(c, link, file, storage.GetStorage().ProxyRange)
	} else {
		common.ErrorStrResp(c, "proxy not allowed", 403)
		return
	}
}

func down(c *gin.Context, link *model.Link) {
	var err error
	if link.MFile != nil {
		defer func(ReadSeekCloser io.ReadCloser) {
			err := ReadSeekCloser.Close()
			if err != nil {
				log.Errorf("close data error: %s", err)
			}
		}(link.MFile)
	}
	c.Header("Referrer-Policy", "no-referrer")
	c.Header("Cache-Control", "max-age=0, no-cache, no-store, must-revalidate")
	if setting.GetBool(conf.ForwardDirectLinkParams) {
		query := c.Request.URL.Query()
		for _, v := range conf.SlicesMap[conf.IgnoreDirectLinkParams] {
			query.Del(v)
		}
		link.URL, err = utils.InjectQuery(link.URL, query)
		if err != nil {
			common.ErrorResp(c, err, 500)
			return
		}
	}
	c.Redirect(302, link.URL)
}

func localProxy(c *gin.Context, link *model.Link, file model.Obj, proxyRange bool) {
	var err error
	if link.URL != "" && setting.GetBool(conf.ForwardDirectLinkParams) {
		query := c.Request.URL.Query()
		for _, v := range conf.SlicesMap[conf.IgnoreDirectLinkParams] {
			query.Del(v)
		}
		link.URL, err = utils.InjectQuery(link.URL, query)
		if err != nil {
			common.ErrorResp(c, err, 500)
			return
		}
	}
	if proxyRange {
		common.ProxyRange(link, file.GetSize())
	}

	//优先处理md文件
	if utils.Ext(file.GetName()) == "md" && setting.GetBool(conf.FilterReadMeScripts) {
		w := c.Writer
		buf := bytes.NewBuffer(make([]byte, 0, file.GetSize()))
		err = common.Proxy(&proxyResponseWriter{ResponseWriter: w, Writer: buf}, c.Request, link, file)
		if err == nil && buf.Len() > 0 {
			if w.Status() < 200 || w.Status() > 300 {
				w.Write(buf.Bytes())
				return
			}

			var html bytes.Buffer
			if err = goldmark.Convert(buf.Bytes(), &html); err != nil {
				err = fmt.Errorf("markdown conversion failed: %w", err)
			} else {
				buf.Reset()
				err = bluemonday.UGCPolicy().SanitizeReaderToWriter(&html, buf)
				if err == nil {
					w.Header().Set("Content-Length", strconv.FormatInt(int64(buf.Len()), 10))
					w.Header().Set("Content-Type", "text/html; charset=utf-8")
					_, err = utils.CopyWithBuffer(c.Writer, buf)
				}
			}
		}
	} else {
		err = common.Proxy(c.Writer, c.Request, link, file)
	}
	if err != nil {
		common.ErrorResp(c, err, 500, true)
		return
	}
}

// TODO need optimize
// when can be proxy?
// 1. text file
// 2. config.MustProxy()
// 3. storage.WebProxy
// 4. proxy_types
// solution: text_file + shouldProxy()
func canProxy(storage driver.Driver, filename string) bool {
	if storage.Config().MustProxy() || storage.GetStorage().WebProxy || storage.GetStorage().WebdavProxy() {
		return true
	}
	if utils.SliceContains(conf.SlicesMap[conf.ProxyTypes], utils.Ext(filename)) {
		return true
	}
	if utils.SliceContains(conf.SlicesMap[conf.TextTypes], utils.Ext(filename)) {
		return true
	}
	return false
}

type proxyResponseWriter struct {
	http.ResponseWriter
	io.Writer
}

func (pw *proxyResponseWriter) Write(p []byte) (int, error) {
	return pw.Writer.Write(p)
}
