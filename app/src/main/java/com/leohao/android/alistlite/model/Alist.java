package com.leohao.android.alistlite.model;

import alistlib.Alistlib;
import alistlib.Event;
import alitvlib.Alitvlib;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jayway.jsonpath.JsonPath;
import com.leohao.android.alistlite.service.AlistService;
import com.leohao.android.alistlite.util.Constants;
import com.leohao.android.alistlite.util.SelfSignedCertGenerator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.leohao.android.alistlite.AlistLiteApplication.applicationContext;

/**
 * @author LeoHao
 */
public class Alist {
    public static String ACTION_STATUS_CHANGED = "com.leohao.android.alistlite.ACTION_STATUS_CHANGED";
    /**
     * 状态常量: 服务已启动
     */
    public static final String STATUS_STARTED = "started";
    /**
     * 状态常量: 服务已停止
     */
    public static final String STATUS_STOPPED = "stopped";
    /**
     * 状态常量: 服务启动失败
     */
    public static final String STATUS_STARTUP_ERROR = "startup_error";
    public static final StringBuilder ALIST_LOGS = new StringBuilder();
    private static final int MAX_LOG_ENTRIES = 50;
    private static final String LOG_SEPARATOR = "\r\n\r\n";
    final String TYPE_HTTP = "http";
    final String TYPE_HTTPS = "https";
    final String TYPE_UNIX = "unix";
    /**
     * 应用数据存储目录（懒加载，避免 applicationContext 未初始化导致 NPE）
     */
    private volatile String dataPath;
    /**
     * 配置数据存储目录
     */
    private String configPath;
    /**
     * 缓存的服务访问地址，网络变化时通过 refreshServerAddress() 更新
     */
    private String cachedServerAddress = Constants.URL_ABOUT_BLANK;

    private static class SingletonHolder {
        private static final Alist INSTANCE = new Alist();
    }

    private Alist() {
    }

    public static Alist getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取应用数据目录，带空安全保护。
     * 优先使用内部存储（避免原生 Go 代码在外部存储上的 SELinux 权限问题），
     * 同时兼容旧版本：若外部存储已有数据而内部存储尚无，则自动迁移。
     */
    public String getDataPath() {
        if (dataPath == null) {
            synchronized (this) {
                if (dataPath == null) {
                    Context ctx = applicationContext;
                    if (ctx == null) {
                        Log.w("Alist", "applicationContext is null, this should not happen normally");
                        return null;
                    }
                    // 优先使用内部存储，避免原生代码 SELinux 权限问题
                    File intDir = new File(ctx.getFilesDir(), "data");
                    // 检查外部存储是否有旧数据需要迁移
                    File extDir = ctx.getExternalFilesDir("data");
                    if (extDir != null && extDir.exists() && !intDir.exists()) {
                        // 外部存储存在旧数据，迁移至内部存储
                        try {
                            FileUtils.copyDirectory(extDir, intDir);
                            Log.i("Alist", "数据已从外部存储迁移到内部存储: " + intDir.getAbsolutePath());
                        } catch (IOException e) {
                            Log.w("Alist", "数据迁移失败，沿用外部存储作为数据目录: " + e.getMessage());
                            dataPath = extDir.getAbsolutePath();
                            return dataPath;
                        }
                    }
                    if (!intDir.exists()) {
                        intDir.mkdirs();
                    }
                    dataPath = intDir.getAbsolutePath();
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

    private JSONObject readConfig() throws IOException {
        File configFile = new File(getConfigPath());
        String configString = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        return JSONUtil.parseObj(configString);
    }

    private void writeConfig(JSONObject config) throws IOException {
        File configFile = new File(getConfigPath());
        FileUtils.write(configFile, config.toStringPretty(), StandardCharsets.UTF_8);
    }

    /**
     * 是否为 HTTPS 模式
     */
    public boolean isHttpsEnabled() {
        try {
            String httpsPort = getConfigValue("scheme.https_port");
            return httpsPort != null && !"-1".equals(httpsPort);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查端口是否可用
     */
    public static boolean isPortAvailable(int port) {
        try {
            ServerSocket ss = new ServerSocket(port);
            ss.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 启用 HTTPS（自签名证书）
     *
     * @param port HTTPS 端口号
     */
    public void enableHttps(int port) throws Exception {
        if (!isPortAvailable(port)) {
            throw new IOException("端口 " + port + " 已被占用");
        }
        String dataDir = getDataPath();
        String certPath = dataDir + File.separator + "cert.pem";
        String keyPath = dataDir + File.separator + "key.pem";
        // 证书已存在则复用，避免用户重复信任
        File certFile = new File(certPath);
        File keyFile = new File(keyPath);
        if (!certFile.exists() || !keyFile.exists()) {
            SelfSignedCertGenerator.generate(certPath, keyPath, getPrimaryIP());
        }
        // 修改配置
        JSONObject config = readConfig();
        JSONObject scheme = config.getJSONObject("scheme");
        scheme.set("force_https", true);
        scheme.set("https_port", port);
        scheme.set("cert_file", certPath);
        scheme.set("key_file", keyPath);
        writeConfig(config);
    }

    /**
     * 禁用 HTTPS
     */
    public void disableHttps() throws IOException {
        JSONObject config = readConfig();
        JSONObject scheme = config.getJSONObject("scheme");
        scheme.set("force_https", false);
        scheme.set("https_port", -1);
        scheme.set("cert_file", "");
        scheme.set("key_file", "");
        writeConfig(config);
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

    /**
     * 获取 AList 服务本地访问地址（WebView 与服务器在同一设备，始终用 127.0.0.1）
     */
    public String getServerAddress() throws IOException {
        return buildUrl("127.0.0.1");
    }

    /**
     * 获取 AList 服务外部访问地址（供通知栏复制、远程访问等场景使用）
     */
    public String getExternalAddress() throws IOException {
        return buildUrl(getPrimaryIP());
    }

    private String buildUrl(String ip) throws IOException {
        boolean isForceHttps = "true".equals(getConfigValue("scheme.force_https"));
        boolean isHttpPortLegal = !"-1".equals(getConfigValue("scheme.https_port"));
        boolean isHttpsMode = isForceHttps && isHttpPortLegal;
        String serverPortStr = getConfigValue(isHttpsMode ? "scheme.https_port" : "scheme.http_port");
        int serverPort = Integer.parseInt(serverPortStr);
        return formatServerUrl(ip, serverPort, isHttpsMode);
    }

    /**
     * 获取缓存的服务地址（轻量，可频繁调用）
     */
    public String getCachedServerAddress() {
        return cachedServerAddress;
    }

    /**
     * 设置缓存的服务地址（供服务启动时初始化用）
     */
    public void setCachedServerAddress(String address) {
        this.cachedServerAddress = address;
    }

    /**
     * 刷新服务地址：重新获取当前 IP 并与缓存比较。
     * 若地址发生变化，更新缓存并返回新地址；否则返回 null。
     *
     * @return 变化后的新地址，未变化返回 null
     * @throws IOException 读取配置文件失败时抛出
     */
    @Nullable
    public String refreshServerAddress() throws IOException {
        String newAddress = getExternalAddress();
        if (!newAddress.equals(cachedServerAddress)) {
            cachedServerAddress = newAddress;
            return newAddress;
        }
        return null;
    }

    /**
     * 获取首选 IP 地址（优先 IPv4，无则取 IPv6，均无则回退 127.0.0.1）
     */
    public String getPrimaryIP() {
        for (String ip : getLocalAddresses().keySet()) {
            if (!"127.0.0.1".equals(ip) && !"localhost".equals(ip)) {
                return ip;
            }
        }
        return "127.0.0.1";
    }

    /**
     * 获取所有外部可用地址（IPv4 + IPv6），一次遍历网卡完成收集。
     * 首选 IP 排首位，其余按遍历顺序。不含本地回环地址。
     *
     * @return LinkedHashMap，key=IP地址，value=网卡显示名称；无可用网络时为空
     */
    public LinkedHashMap<String, String> getLocalAddresses() {
        LinkedHashMap<String, String> ipMap = new LinkedHashMap<>();
        String primaryIP = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;
                String displayName = ni.getDisplayName();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isLoopbackAddress()) continue;
                    if (addr instanceof Inet4Address) {
                        String ip = addr.getHostAddress();
                        if (primaryIP == null) primaryIP = ip;
                        ipMap.put(ip, displayName);
                    } else if (addr instanceof Inet6Address) {
                        String ip = addr.getHostAddress();
                        int scopeIdx = ip.indexOf('%');
                        if (scopeIdx != -1) ip = ip.substring(0, scopeIdx);
                        String lower = ip.toLowerCase();
                        if (lower.startsWith("fe80:") || lower.startsWith("fec0:")) continue;
                        ipMap.put(ip, displayName);
                    }
                }
            }
        } catch (Exception e) {
            Log.w("Alist", "getLocalAddresses: " + e.getLocalizedMessage());
        }
        // 首选 IP 排首位，其余按遍历顺序
        LinkedHashMap<String, String> sorted = new LinkedHashMap<>();
        if (primaryIP != null) {
            String name = ipMap.get(primaryIP);
            sorted.put(primaryIP, name != null ? name : "出口网络");
        }
        for (Map.Entry<String, String> entry : ipMap.entrySet()) {
            if (!entry.getKey().equals(primaryIP)) {
                sorted.put(entry.getKey(), entry.getValue());
            }
        }
        return sorted;
    }

    /**
     * 格式化 AList 服务访问 URL，IPv6 地址自动加中括号
     *
     * @param ip      IP 地址
     * @param port    端口号
     * @param isHttps 是否 HTTPS
     * @return 格式化后的完整 URL，如 http://[2001:db8::1]:5244
     */
    public static String formatServerUrl(String ip, int port, boolean isHttps) {
        String protocol = isHttps ? "https" : "http";
        // IPv6 地址需要中括号包裹
        if (ip.contains(":")) {
            return String.format(Locale.CHINA, "%s://[%s]:%d", protocol, ip, port);
        }
        return String.format(Locale.CHINA, "%s://%s:%d", protocol, ip, port);
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
