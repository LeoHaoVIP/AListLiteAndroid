package doubao

import (
	"errors"

	"github.com/alist-org/alist/v3/drivers/base"
	"github.com/alist-org/alist/v3/pkg/utils"
	log "github.com/sirupsen/logrus"
)

// do others that not defined in Driver interface
func (d *Doubao) request(path string, method string, callback base.ReqCallback, resp interface{}) ([]byte, error) {
	url := "https://www.doubao.com" + path
	req := base.RestyClient.R()
	req.SetHeader("Cookie", d.Cookie)
	if callback != nil {
		callback(req)
	}
	var r BaseResp
	req.SetResult(&r)
	res, err := req.Execute(method, url)
	log.Debugln(res.String())
	if err != nil {
		return nil, err
	}

	// 业务状态码检查（优先于HTTP状态码）
	if r.Code != 0 {
		return res.Body(), errors.New(r.Msg)
	}
	if resp != nil {
		err = utils.Json.Unmarshal(res.Body(), resp)
		if err != nil {
			return nil, err
		}
	}
	return res.Body(), nil
}
