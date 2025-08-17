package common

import (
	"context"
	"strings"

	"github.com/OpenListTeam/OpenList/v4/cmd/flags"
	"github.com/OpenListTeam/OpenList/v4/internal/conf"
	"github.com/gin-gonic/gin"
	log "github.com/sirupsen/logrus"
)

func hidePrivacy(msg string) string {
	for _, r := range conf.PrivacyReg {
		msg = r.ReplaceAllStringFunc(msg, func(s string) string {
			return strings.Repeat("*", len(s))
		})
	}
	return msg
}

// ErrorResp is used to return error response
// @param l: if true, log error
func ErrorResp(c *gin.Context, err error, code int, l ...bool) {
	ErrorWithDataResp(c, err, code, nil, l...)
	//if len(l) > 0 && l[0] {
	//	if flags.Debug || flags.Dev {
	//		log.Errorf("%+v", err)
	//	} else {
	//		log.Errorf("%v", err)
	//	}
	//}
	//c.JSON(200, Resp[interface{}]{
	//	Code:    code,
	//	Message: hidePrivacy(err.Error()),
	//	Data:    nil,
	//})
	//c.Abort()
}

func ErrorWithDataResp(c *gin.Context, err error, code int, data interface{}, l ...bool) {
	if len(l) > 0 && l[0] {
		if flags.Debug || flags.Dev {
			log.Errorf("%+v", err)
		} else {
			log.Errorf("%v", err)
		}
	}
	c.JSON(200, Resp[interface{}]{
		Code:    code,
		Message: hidePrivacy(err.Error()),
		Data:    data,
	})
	c.Abort()
}

func ErrorStrResp(c *gin.Context, str string, code int, l ...bool) {
	if len(l) != 0 && l[0] {
		log.Error(str)
	}
	c.JSON(200, Resp[interface{}]{
		Code:    code,
		Message: hidePrivacy(str),
		Data:    nil,
	})
	c.Abort()
}

func SuccessResp(c *gin.Context, data ...interface{}) {
	SuccessWithMsgResp(c, "success", data...)
}

func SuccessWithMsgResp(c *gin.Context, msg string, data ...interface{}) {
	var respData interface{}
	if len(data) > 0 {
		respData = data[0]
	}

	c.JSON(200, Resp[interface{}]{
		Code:    200,
		Message: msg,
		Data:    respData,
	})
}

func Pluralize(count int, singular, plural string) string {
	if count == 1 {
		return singular
	}
	return plural
}

func GinWithValue(c *gin.Context, keyAndValue ...any) {
	c.Request = c.Request.WithContext(
		ContentWithValue(c.Request.Context(), keyAndValue...),
	)
}

func ContentWithValue(ctx context.Context, keyAndValue ...any) context.Context {
	if len(keyAndValue) < 1 || len(keyAndValue)%2 != 0 {
		panic("keyAndValue must be an even number of arguments (key, value, ...)")
	}
	for len(keyAndValue) > 0 {
		ctx = context.WithValue(ctx, keyAndValue[0], keyAndValue[1])
		keyAndValue = keyAndValue[2:]
	}
	return ctx
}
