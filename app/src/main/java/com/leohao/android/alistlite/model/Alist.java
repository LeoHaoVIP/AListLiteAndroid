package com.leohao.android.alistlite.model;

import alistlib.Alistlib;
import alistlib.Event;
import alitvlib.Alitvlib;
import android.content.Context;
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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.leohao.android.alistlite.AlistLiteApplication.applicationContext;

/**
 * @author LeoHao
 */
public class Alist {
    public static String ACTION_STATUS_CHANGED = "com.leohao.android.alistlite.ACTION_STATUS_CHANGED";
    public static final StringBuilder ALIST_LOGS = new StringBuilder();
    private static final int MAX_LOG_ENTRIES = 10;
    private static final String LOG_SEPARATOR = "\r\n\r\n";
    final String TYPE_HTTP = "http";
    final String TYPE_HTTPS = "https";
    final String TYPE_UNIX = "unix";
    /**
     * 应用数据存储目录（懒加载，避免 applicationContext 未初始化导致 NPE）
     */
    private String dataPath;
    /**
     * 配置数据存储目录
     */
    private String configPath;

    private static class SingletonHolder {
        private static final Alist INSTANCE = new Alist();
    }

    private Alist() {
    }

    public static Alist getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取应用数据目录，带空安全保护
     * 当外部存储不可用时回退到内部存储
     */
    private String getDataPath() {
        if (dataPath == null) {
            synchronized (this) {
                if (dataPath == null) {
                    Context ctx = applicationContext;
                    if (ctx == null) {
                        // 极端情况：applicationContext 尚未初始化，使用 /data/data/<pkg>/files/data
                        Log.w("Alist", "applicationContext is null, this should not happen normally");
                        return null;
                    }
                    File extDir = ctx.getExternalFilesDir("data");
                    if (extDir != null) {
                        dataPath = extDir.getAbsolutePath();
                    } else {
                        // 外置存储不可用，回退到内置存储
                        File intDir = new File(ctx.getFilesDir(), "data");
                        if (!intDir.exists()) {
                            intDir.mkdirs();
                        }
                        dataPath = intDir.getAbsolutePath();
                        Log.w("Alist", "外部存储不可用，回退到内部存储: " + dataPath);
                    }
                }
            }
        }
        return dataPath;
    }

    /**
     * 获取配置文件路径
     */
    private String getConfigPath() {
        if (configPath == null) {
            String dp = getDataPath();
            if (dp == null) {
                return null;
            }
            configPath = String.format("%s%s%s", dp, File.separator, Constants.ALIST_CONFIG_FILENAME);
        }
        return configPath;
    }

    /**
     * 获取当前服务运行状态
     */
    public Boolean hasRunning() {
        return (Alistlib.isRunning(TYPE_HTTP) || Alistlib.isRunning(TYPE_HTTPS) || Alistlib.isRunning(TYPE_UNIX));
    }

    public void init() throws Exception {
        Alistlib.setConfigData(getDataPath());
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
            appendLog(log);
            Log.i(AlistService.TAG, log);
        });
    }

    /**
     * 从本地配置文件中读取指定配置项
     *
     * @param jsonPath 配置项路径 如 scheme.http_port
     */
    public String getConfigValue(String jsonPath) throws IOException {
        File configFile = new File(getConfigPath());
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
            //同步关闭阿里云盘 TV API 接口
            if (Alitvlib.isRunning()) {
                Alitvlib.stopServer();
            }
            appendLog("------ 服务已关闭 ------\r\n\r\n");
        } catch (Exception e) {
            showToast("Alist服务关闭失败");
            appendLog("------ 服务关闭失败 ------\r\n\r\n");
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
        //同步开启阿里云盘 TV API 接口
        if (!Alitvlib.isRunning()) {
            Alitvlib.startServer();
        }
        notifyStatusChanged();
    }

    public String getBindingIP() {
        return Alistlib.getOutboundIPString();
    }

    /**
     * 获取设备所有本地 IPv4 地址与网卡名称的映射（非回环、已启用），出口 IP 排在首位
     * @return LinkedHashMap，key=IP地址，value=网卡显示名称（如 wlan0、eth0）
     */
    public LinkedHashMap<String, String> getAllLocalIPs() {
        LinkedHashMap<String, String> ipMap = new LinkedHashMap<>();
        String outboundIP = Alistlib.getOutboundIPString();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                String displayName = ni.getDisplayName();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        ipMap.put(addr.getHostAddress(), displayName);
                    }
                }
            }
        } catch (Exception e) {
            Log.w("Alist", "getAllLocalIPs: " + e.getLocalizedMessage());
        }
        // 将出口 IP 排在首位
        if (!"localhost".equals(outboundIP) && ipMap.containsKey(outboundIP)) {
            String outboundName = ipMap.get(outboundIP);
            LinkedHashMap<String, String> sorted = new LinkedHashMap<>();
            sorted.put(outboundIP, outboundName);
            for (Map.Entry<String, String> entry : ipMap.entrySet()) {
                if (!entry.getKey().equals(outboundIP)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
            return sorted;
        }
        return ipMap;
    }

    private void showToast(String msg) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 追加日志并裁剪至最近 MAX_LOG_ENTRIES 条，防止日志过多导致界面卡顿
     */
    private static void appendLog(String log) {
        synchronized (ALIST_LOGS) {
            ALIST_LOGS.append(log);
            // 统计当前日志条数
            int count = 0;
            int idx = 0;
            while ((idx = ALIST_LOGS.indexOf(LOG_SEPARATOR, idx)) != -1) {
                count++;
                idx += LOG_SEPARATOR.length();
            }
            // 超出上限时删除最旧的日志
            if (count > MAX_LOG_ENTRIES) {
                int entriesToRemove = count - MAX_LOG_ENTRIES;
                idx = 0;
                for (int i = 0; i < entriesToRemove; i++) {
                    idx = ALIST_LOGS.indexOf(LOG_SEPARATOR, idx);
                    if (idx != -1) {
                        idx += LOG_SEPARATOR.length();
                    }
                }
                ALIST_LOGS.delete(0, idx);
            }
        }
    }
}
