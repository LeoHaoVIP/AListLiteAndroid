package alistlib

import (
	"context"
	"errors"
	"fmt"
	"github.com/OpenListTeam/OpenList/alistlib/internal"
	"github.com/OpenListTeam/OpenList/cmd"
	"github.com/OpenListTeam/OpenList/cmd/flags"
	"github.com/OpenListTeam/OpenList/internal/bootstrap"
	"github.com/OpenListTeam/OpenList/internal/conf"
	"github.com/OpenListTeam/OpenList/internal/db"
	"github.com/OpenListTeam/OpenList/pkg/utils"
	"github.com/OpenListTeam/OpenList/server"
	"github.com/gin-gonic/gin"
	log "github.com/sirupsen/logrus"
	"net"
	"net/http"
	"os"
	"strconv"
	"time"
)

type LogCallback interface {
	OnLog(level int16, msg string)
}

type Event interface {
	OnStartError(t string, err string)
	OnShutdown(t string)
}

var event Event
var logFormatter *internal.MyFormatter

func Init(e Event, cb LogCallback) error {
	event = e
	cmd.Init()
	logFormatter = &internal.MyFormatter{
		OnLog: cb.OnLog,
	}
	if utils.Log == nil {
		return errors.New("utils.log is nil")
	} else {
		utils.Log.SetFormatter(logFormatter)
	}
	return nil
}

var httpSrv, httpsSrv, unixSrv *http.Server

func listenAndServe(t string, srv *http.Server) {
	err := srv.ListenAndServe()
	if err != nil && err != http.ErrServerClosed {
		event.OnStartError(t, err.Error())
	} else {
		event.OnShutdown(t)
	}
}

func IsRunning(t string) bool {
	switch t {
	case "http":
		return httpSrv != nil
	case "https":
		return httpsSrv != nil
	case "unix":
		return unixSrv != nil
	}
	return httpSrv != nil && httpsSrv != nil && unixSrv != nil
}

// Start starts the server
func Start() {
	if conf.Conf.DelayedStart != 0 {
		utils.Log.Infof("delayed start for %d seconds", conf.Conf.DelayedStart)
		time.Sleep(time.Duration(conf.Conf.DelayedStart) * time.Second)
	}
	bootstrap.InitOfflineDownloadTools()
	bootstrap.LoadStorages()
	bootstrap.InitTaskManager()
	if !flags.Debug && !flags.Dev {
		gin.SetMode(gin.ReleaseMode)
	}
	r := gin.New()
	r.Use(gin.LoggerWithWriter(log.StandardLogger().Out), gin.RecoveryWithWriter(log.StandardLogger().Out))
	server.Init(r)
	if conf.Conf.Scheme.HttpPort != -1 {
		httpBase := fmt.Sprintf("%s:%d", conf.Conf.Scheme.Address, conf.Conf.Scheme.HttpPort)
		utils.Log.Infof("start HTTP server @ %s", httpBase)
		httpSrv = &http.Server{Addr: httpBase, Handler: r}
		go func() {
			err := httpSrv.ListenAndServe()
			if err != nil && !errors.Is(err, http.ErrServerClosed) {
				utils.Log.Fatalf("failed to start http: %s", err.Error())
			}
		}()
	}
	if conf.Conf.Scheme.HttpsPort != -1 {
		httpsBase := fmt.Sprintf("%s:%d", conf.Conf.Scheme.Address, conf.Conf.Scheme.HttpsPort)
		utils.Log.Infof("start HTTPS server @ %s", httpsBase)
		httpsSrv = &http.Server{Addr: httpsBase, Handler: r}
		go func() {
			err := httpsSrv.ListenAndServeTLS(conf.Conf.Scheme.CertFile, conf.Conf.Scheme.KeyFile)
			if err != nil && !errors.Is(err, http.ErrServerClosed) {
				utils.Log.Fatalf("failed to start https: %s", err.Error())
			}
		}()
	}
	if conf.Conf.Scheme.UnixFile != "" {
		utils.Log.Infof("start unix server @ %s", conf.Conf.Scheme.UnixFile)
		unixSrv = &http.Server{Handler: r}
		go func() {
			listener, err := net.Listen("unix", conf.Conf.Scheme.UnixFile)
			if err != nil {
				utils.Log.Fatalf("failed to listen unix: %+v", err)
			}
			// set socket file permission
			mode, err := strconv.ParseUint(conf.Conf.Scheme.UnixFilePerm, 8, 32)
			if err != nil {
				utils.Log.Errorf("failed to parse socket file permission: %+v", err)
			} else {
				err = os.Chmod(conf.Conf.Scheme.UnixFile, os.FileMode(mode))
				if err != nil {
					utils.Log.Errorf("failed to chmod socket file: %+v", err)
				}
			}
			err = unixSrv.Serve(listener)
			if err != nil && !errors.Is(err, http.ErrServerClosed) {
				utils.Log.Fatalf("failed to start unix: %s", err.Error())
			}
		}()
	}
}

func Release() {
	db.Close()
}

// Shutdown timeout毫秒
func Shutdown(timeout int64) (err error) {
	timeoutDuration := time.Duration(timeout) * time.Millisecond
	utils.Log.Println("Shutdown server...")
	if conf.Conf.Scheme.HttpPort != -1 {
		err := shutdown(httpSrv, timeoutDuration)
		if err != nil {
			return err
		}
		httpSrv = nil
		utils.Log.Println("Server HTTP Shutdown")
	}
	if conf.Conf.Scheme.HttpsPort != -1 {
		err := shutdown(httpsSrv, timeoutDuration)
		if err != nil {
			return err
		}
		httpsSrv = nil
		utils.Log.Println("Server HTTPS Shutdown")
	}
	if conf.Conf.Scheme.UnixFile != "" {
		err := shutdown(unixSrv, timeoutDuration)
		if err != nil {
			return err
		}
		unixSrv = nil
		utils.Log.Println("Server UNIX Shutdown")
	}
	return nil
}

func shutdown(srv *http.Server, timeout time.Duration) error {
	if srv == nil {
		return nil
	}
	Release()
	ctx, cancel := context.WithTimeout(context.Background(), timeout)
	defer cancel()
	return srv.Shutdown(ctx)
}
