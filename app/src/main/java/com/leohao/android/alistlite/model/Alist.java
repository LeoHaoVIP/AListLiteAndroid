package com.leohao.android.alistlite.model;

import alistlib.Alistlib;
import alistlib.Event;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.hutool.core.date.DateUtil;
import com.jayway.jsonpath.JsonPath;
import com.leohao.android.alistlite.service.AlistService;
import com.leohao.android.alistlite.util.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.leohao.android.alistlite.AlistLiteApplication.applicationContext;

/**
 * @author LeoHao
 */
public class Alist {
    public static String ACTION_STATUS_CHANGED = "com.leohao.android.alistlite.ACTION_STATUS_CHANGED";
    public static StringBuilder ALIST_LOGS = new StringBuilder();
    final String TYPE_HTTP = "http";
    final String TYPE_HTTPS = "https";
    final String TYPE_UNIX = "unix";
    /**
     * 应用数据存储目录
     */
    String dataPath = applicationContext.getExternalFilesDir("data").getAbsolutePath();
    /**
     * 配置数据存储目录
     */
    String configPath = String.format("%s%s%s", dataPath, File.separator, Constants.ALIST_CONFIG_FILENAME);

    private static class SingletonHolder {
        private static final Alist INSTANCE = new Alist();
    }

    private Alist() {
    }

    public static Alist getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取当前服务运行状态
     */
    public Boolean hasRunning() {
        return (Alistlib.isRunning(TYPE_HTTP) || Alistlib.isRunning(TYPE_HTTPS) || Alistlib.isRunning(TYPE_UNIX));
    }

    public void init() throws Exception {
        Alistlib.setConfigData(dataPath);
        Alistlib.setConfigLogStd(true);
        Alistlib.init(new Event() {
            @Override
            public void onShutdown(String s) {
                notifyStatusChanged();
            }

            @Override
            public void onStartError(String s, String s1) {
                String errorMsg = "onStartError: " + s + " " + s1;
                Log.e("AListServer", errorMsg);
                Looper.prepare();
                showToast(errorMsg);
                Looper.loop();
                notifyStatusChanged();
            }
        }, (level, msg) -> {
            //日志捕捉
            String levelName = "INFO";
            switch (level) {
                case 1:
                    levelName = "ERROR";
                    break;
                case 2:
                    levelName = "DEBUG";
                    break;
                case 3:
                    levelName = "WARN";
                    break;
                case 4:
                    levelName = "INFO";
                    break;
                default:
                    break;
            }
            String log = String.format("%s[%s] %s\r\n\r\n", levelName, DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"), msg);
            ALIST_LOGS.append(log);
            Log.i(AlistService.TAG, log);
        });
    }

    /**
     * 从本地配置文件中读取指定配置项
     *
     * @param jsonPath 配置项路径 如 scheme.http_port
     */
    public String getConfigValue(String jsonPath) throws IOException {
        File configFile = new File(configPath);
        String configString = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        return JsonPath.read(configString, jsonPath).toString();
    }

    public void setAdminPassword(String pwd) throws Exception {
        if (!hasRunning()) {
            init();
        }
        Alistlib.setAdminPassword(pwd);
    }

    public String getAdminUser() throws Exception {
        if (!hasRunning()) {
            init();
        }
        return Alistlib.getAdminUser();
    }

    /**
     * 挂载本地存储配置
     *
     * @param localPath 本地路径
     * @param mountPath 挂载路径
     */
    public void addLocalStorageDriver(String localPath, String mountPath) throws Exception {
        if (!hasRunning()) {
            init();
        }
        Alistlib.addLocalStorage(localPath, mountPath);
    }

    private void notifyStatusChanged() {
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(ACTION_STATUS_CHANGED));
    }

    public void shutdown(Long timeout) {
        try {
            Alistlib.shutdown(timeout);
            ALIST_LOGS.append("------ 服务已关闭 ------\r\n\r\n");
        } catch (Exception e) {
            showToast("Alist服务关闭失败");
            ALIST_LOGS.append("------ 服务关闭失败 ------\r\n\r\n");
        }
    }

    public void shutdown() {
        shutdown(5000L);
    }

    public void startup() throws Exception {
        if (Alistlib.isRunning("")) {
            return;
        }
        init();
        Alistlib.start();
        notifyStatusChanged();
    }

    public String getBindingIP() {
        return Alistlib.getOutboundIPString();
    }

    private void showToast(String msg) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();
    }
}
