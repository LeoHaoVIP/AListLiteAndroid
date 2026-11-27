package com.leohao.android.alistlite.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.leohao.android.alistlite.model.Alist;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 一键排障包：把排查问题需要的信息打包成一个 zip，用户直接在 Issues 里附上即可。
 * <p>
 * 包含：info.txt（设备/版本/服务/挂载摘要）、config.json（脱敏）、settings.json（脱敏）、
 * alist.log（服务日志尾部）。敏感字段（password / token / secret 等）统一替换为 ***。
 *
 * @author LeoHao
 */
public class DiagnosticsCollector {

    private static final String TAG = "Diagnostics";
    private static final String DIR_NAME = "AListLite";
    private static final int MAX_LOG_CHARS = 200 * 1024;

    public static String suggestFileName() {
        String ts = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA).format(new Date());
        return "alistlite-diagnostic-" + ts + ".zip";
    }

    /**
     * 生成并保存到系统「下载/AListLite」，返回文件名
     */
    public static String saveToDownloads(Context context) throws Exception {
        String fileName = suggestFileName();
        byte[] data = buildZip(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/zip");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/" + DIR_NAME);
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);
            android.net.Uri target = context.getContentResolver()
                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (target == null) {
                throw new Exception("无法创建文件");
            }
            OutputStream os = context.getContentResolver().openOutputStream(target);
            if (os == null) {
                throw new Exception("无法写入文件");
            }
            try {
                os.write(data);
            } finally {
                os.close();
            }
            ContentValues done = new ContentValues();
            done.put(MediaStore.MediaColumns.IS_PENDING, 0);
            context.getContentResolver().update(target, done, null, null);
        } else {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), DIR_NAME);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new Exception("无法创建目录");
            }
            FileOutputStream os = new FileOutputStream(new File(dir, fileName));
            try {
                os.write(data);
            } finally {
                os.close();
            }
        }
        return fileName;
    }

    /**
     * 生成 zip 到 cacheDir/diag（发邮件附件用，FileProvider 白名单指向这里）
     */
    public static File saveToCache(Context context) throws Exception {
        File dir = new File(context.getCacheDir(), "diag");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new Exception("无法创建缓存目录");
        }
        // 只保留最新一份
        File[] olds = dir.listFiles();
        if (olds != null) {
            for (File f : olds) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        }
        File out = new File(dir, suggestFileName());
        FileOutputStream os = new FileOutputStream(out);
        try {
            os.write(buildZip(context));
        } finally {
            os.close();
        }
        return out;
    }

    /**
     * 组装 zip 内容（不落盘，便于以后加「分享」入口）
     */
    public static byte[] buildZip(Context context) throws Exception {
        Alist alist = Alist.getInstance();
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);

        putEntry(zos, "info.txt", buildInfo(context, alist));
        putEntry(zos, "config.json", buildSanitizedConfig(alist));
        putEntry(zos, "settings.json", buildSanitizedSettings(alist));
        // App 侧：环形缓冲日志 + 上次崩溃堆栈（logcat 普通应用读不到，只能自己攒）
        putEntry(zos, "app-log.txt", AppLog.dump());
        putEntry(zos, "app-crash.txt", AppLog.readLastCrash(context));
        putEntry(zos, "alist.log", buildLogTail());

        zos.finish();
        zos.close();
        return bos.toByteArray();
    }

    // ===== 各部分内容 =====

    private static String buildInfo(Context context, Alist alist) {
        StringBuilder sb = new StringBuilder();
        sb.append("AListLite 排障包\n");
        sb.append("生成时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date())).append("\n\n");

        sb.append("[应用]\n");
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            sb.append("versionName: ").append(pi.versionName).append('\n');
            sb.append("versionCode: ").append(pi.versionCode).append('\n');
        } catch (Exception e) {
            sb.append("version: 读取失败 ").append(e.getMessage()).append('\n');
        }
        sb.append("openListVersion: ").append(Constants.OPENLIST_VERSION).append('\n');

        sb.append("\n[设备]\n");
        sb.append("brand: ").append(Build.BRAND).append('\n');
        sb.append("manufacturer: ").append(Build.MANUFACTURER).append('\n');
        sb.append("model: ").append(Build.MODEL).append('\n');
        sb.append("android: ").append(Build.VERSION.RELEASE)
                .append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("abis: ").append(join(Build.SUPPORTED_ABIS)).append('\n');
        sb.append("webviewUA: ").append(safeUserAgent()).append('\n');
        sb.append("defaultCharset: ").append(Charset.defaultCharset().name()).append('\n');

        sb.append("\n[服务]\n");
        sb.append("running: ").append(alist.hasRunning()).append('\n');
        try {
            sb.append("https: ").append(alist.isHttpsEnabled()).append('\n');
            sb.append("port: ").append(alist.getConfigValue(
                    alist.isHttpsEnabled() ? "scheme.https_port" : "scheme.http_port")).append('\n');
            if (alist.hasRunning()) {
                sb.append("localAddress: ").append(alist.getServerAddress()).append('\n');
                sb.append("externalAddress: ").append(alist.getExternalAddress()).append('\n');
            }
        } catch (Exception e) {
            sb.append("address: 读取失败 ").append(e.getMessage()).append('\n');
        }
        sb.append("interfaces:\n").append(describeInterfaces());

        sb.append("\n[挂载]\n").append(describeStorages(context, alist));

        sb.append("\n[日志]\n");
        sb.append("logcat: 普通应用无读取权限（Android 7+），改用应用内环形缓冲\n");
        sb.append("app-log.txt / app-crash.txt 为 App 侧日志与上次崩溃堆栈\n");
        sb.append("alist.log 为 OpenList 服务端日志\n");

        sb.append("\n[权限]\n");
        try {
            sb.append("manageExternalStorage: ")
                    .append(Environment.isExternalStorageManager()).append('\n');
        } catch (Throwable t) {
            sb.append("manageExternalStorage: 不可用\n");
        }
        return sb.toString();
    }

    private static String describeStorages(Context context, Alist alist) {
        if (!alist.hasRunning()) {
            return "服务未运行，跳过\n";
        }
        try {
            AlistAdminApi api = new AlistAdminApi(alist.getServerAddress());
            String pwd = SharedDataHelper.getInstance()
                    .getStringShareData(Constants.ANDROID_SHARED_DATA_KEY_ADMIN_PASSWORD);
            api.login("admin", TextUtils.isEmpty(pwd) ? Constants.ALIST_DEFAULT_PASSWORD : pwd);
            List<JSONObject> list = api.listStorages();
            StringBuilder sb = new StringBuilder();
            sb.append("count: ").append(list.size()).append('\n');
            for (JSONObject item : list) {
                sb.append(String.format(Locale.CHINA, "- #%d %s | %s | %s%s%s\n",
                        item.optLong("id"),
                        item.optString("mount_path"),
                        item.optString("driver"),
                        item.optBoolean("disabled") ? "disabled" : "enabled",
                        TextUtils.isEmpty(item.optString("remark")) ? "" : " | 备注:",
                        item.optString("remark")));
                JSONObject du = item.optJSONObject("mount_details");
                if (du != null) {
                    JSONObject usage = du.optJSONObject("disk_usage");
                    if (usage != null) {
                        sb.append("   容量: used=").append(usage.optLong("used_space"))
                                .append(" total=").append(usage.optLong("total_space")).append('\n');
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "无法读取挂载列表: " + e.getMessage() + "\n";
        }
    }

    private static String describeInterfaces() {
        StringBuilder sb = new StringBuilder();
        try {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            while (ifs != null && ifs.hasMoreElements()) {
                NetworkInterface ni = ifs.nextElement();
                if (!ni.isUp()) {
                    continue;
                }
                Enumeration<java.net.InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    java.net.InetAddress addr = addrs.nextElement();
                    if (addr.isLoopbackAddress()) {
                        continue;
                    }
                    sb.append("  ").append(ni.getDisplayName()).append(": ")
                            .append(addr.getHostAddress()).append('\n');
                }
            }
        } catch (Exception e) {
            sb.append("  读取失败: ").append(e.getMessage()).append('\n');
        }
        return sb.length() == 0 ? "  (无)\n" : sb.toString();
    }

    private static String buildSanitizedConfig(Alist alist) {
        try {
            File configFile = new File(alist.getDataPath(), "config.json");
            String text = readFile(configFile);
            if (TextUtils.isEmpty(text)) {
                return "{}";
            }
            Object parsed = new org.json.JSONTokener(text).nextValue();
            sanitize(parsed);
            return parsed instanceof JSONObject ? ((JSONObject) parsed).toString(2)
                    : String.valueOf(parsed);
        } catch (Exception e) {
            return "读取 config.json 失败: " + e.getMessage();
        }
    }

    private static String buildSanitizedSettings(Alist alist) {
        if (!alist.hasRunning()) {
            return "服务未运行，跳过";
        }
        try {
            AlistAdminApi api = new AlistAdminApi(alist.getServerAddress());
            String pwd = SharedDataHelper.getInstance()
                    .getStringShareData(Constants.ANDROID_SHARED_DATA_KEY_ADMIN_PASSWORD);
            api.login("admin", TextUtils.isEmpty(pwd) ? Constants.ALIST_DEFAULT_PASSWORD : pwd);
            Map<String, String> settings = api.getSettings();
            JSONObject out = new JSONObject();
            for (Map.Entry<String, String> e : settings.entrySet()) {
                String key = e.getKey();
                boolean sensitive = isSensitive(key) || isSensitive(e.getValue());
                out.put(key, sensitive ? "***" : e.getValue());
            }
            return out.toString(2);
        } catch (Exception e) {
            return "读取设置失败: " + e.getMessage();
        }
    }

    private static String buildLogTail() {
        String logs = Alist.ALIST_LOGS == null ? "" : Alist.ALIST_LOGS.toString();
        if (logs.length() > MAX_LOG_CHARS) {
            logs = "…（前 " + (logs.length() - MAX_LOG_CHARS) + " 字符已省略）\n"
                    + logs.substring(logs.length() - MAX_LOG_CHARS);
        }
        return logs;
    }

    // ===== 工具 =====

    private static void putEntry(ZipOutputStream zos, String name, String content) throws Exception {
        byte[] bytes = (content == null ? "" : content).getBytes("UTF-8");
        zos.putNextEntry(new ZipEntry(name));
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        byte[] buf = new byte[8192];
        int n;
        while ((n = bis.read(buf)) > 0) {
            zos.write(buf, 0, n);
        }
        zos.closeEntry();
    }

    private static String readFile(File file) throws Exception {
        if (!file.exists()) {
            return null;
        }
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        java.io.FileInputStream fis = new java.io.FileInputStream(file);
        try {
            byte[] buf = new byte[8192];
            int n;
            while ((n = fis.read(buf)) > 0) {
                bos.write(buf, 0, n);
            }
        } finally {
            fis.close();
        }
        return new String(bos.toByteArray(), "UTF-8");
    }

    private static boolean isSensitive(String key) {
        if (key == null) {
            return false;
        }
        String k = key.toLowerCase(Locale.US);
        return k.contains("password") || k.contains("passwd") || k.contains("pwd")
                || k.contains("token") || k.contains("secret") || k.contains("cookie")
                || k.contains("refresh") || k.contains("access_key") || k.contains("private")
                || k.contains("credential") || k.contains("authorization");
    }

    /**
     * 递归把敏感键的值替换成 ***
     */
    private static void sanitize(Object node) {
        if (node instanceof JSONObject) {
            JSONObject obj = (JSONObject) node;
            Iterator<String> keys = obj.keys();
            java.util.List<String> list = new java.util.ArrayList<>();
            while (keys.hasNext()) {
                list.add(keys.next());
            }
            for (String key : list) {
                Object value = obj.opt(key);
                if (isSensitive(key)) {
                    try {
                        obj.put(key, "***");
                    } catch (Exception ignored) {
                    }
                } else {
                    sanitize(value);
                }
            }
        } else if (node instanceof JSONArray) {
            JSONArray arr = (JSONArray) node;
            for (int i = 0; i < arr.length(); i++) {
                sanitize(arr.opt(i));
            }
        }
    }

    private static String safeUserAgent() {
        try {
            return WebSettings.getDefaultUserAgent(
                    com.leohao.android.alistlite.AlistLiteApplication.applicationContext);
        } catch (Throwable t) {
            return "unknown";
        }
    }

    private static String join(String[] arr) {
        if (arr == null || arr.length == 0) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    private static void log(String msg) {
        AppLog.d(TAG, msg);
    }
}
