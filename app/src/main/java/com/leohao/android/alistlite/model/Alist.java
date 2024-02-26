package com.leohao.android.alistlite.model;

import alistlib.Alistlib;
import alistlib.Event;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.jayway.jsonpath.JsonPath;
import com.leohao.android.alistlite.AlistLiteApplication;
import com.leohao.android.alistlite.service.AlistService;
import com.leohao.android.alistlite.util.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author LeoHao
 */
public class Alist {
    private final Context context = AlistLiteApplication.context;
    public static String ACTION_STATUS_CHANGED = "com.leohao.android.alistlite.ACTION_STATUS_CHANGED";
    final String TYPE_HTTP = "http";
    final String TYPE_HTTPS = "https";
    final String TYPE_UNIX = "unix";
    /**
     * 应用数据存储目录
     */
    String dataPath = context.getExternalFilesDir("data").getAbsolutePath();
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
                Log.e("AListServer", "onStartError: " + s + " " + s1);
                notifyStatusChanged();
            }
        }, (level, msg) -> {
            //日志捕捉
            Log.i(AlistService.TAG, String.format("level : %s | msg : %s", level, msg));
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

    private void notifyStatusChanged() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_STATUS_CHANGED));
    }

    public void shutdown(Long timeout) {
        try {
            Alistlib.shutdown(timeout);
        } catch (Exception e) {
            showToast("Alist服务关闭失败");
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
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
