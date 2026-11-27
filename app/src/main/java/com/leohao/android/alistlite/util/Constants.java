package com.leohao.android.alistlite.util;

import android.Manifest;
import com.hjq.permissions.Permission;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LeoHao
 */
public class Constants {
    public static String OPENLIST_VERSION = "4.2.10";
    public static String ALIST_CONFIG_FILENAME = "config.json";
    public static String ALIST_STORAGE_DRIVER_MOUNT_PATH = "本地存储";
    public static String ALIST_DEFAULT_PASSWORD = "123456";
    public static Integer RECENT_RELEASE_RECORD_SIZE = 10;
    public static String ANDROID_PERMISSION_PREFIX = "android.permission.";
    public static String URL_RELEASE_LATEST = "https://api.github.com/repos/LeoHaoVIP/AListLiteAndroid/releases/latest";
    public static String URL_RELEASES = "https://api.github.com/repos/LeoHaoVIP/AListLiteAndroid/releases";
    public static String URL_OPEN_ISSUE = "https://github.com/LeoHaoVIP/AListLiteAndroid/issues";
    public static String URL_OPEN_DISCUSSION = "https://github.com/LeoHaoVIP/AListLiteAndroid/discussions";
    public static String URL_LOCAL_ABOUT_ALIST_LITE = "file:///android_asset/html/about-alistlite.html";
    public static String URL_LOCAL_RELEASE_LOG = "file:///android_asset/html/release-log.html";
    public static String URL_LOCAL_WEBDAV_GUIDE = "file:///android_asset/html/webdav-guide.html";
    public static String URL_ABOUT_BLANK = "about:blank";
    public static String QUICK_DOWNLOAD_ADDRESS = "https://gitee.com/leohaovip/AListLiteAndroid/releases/download";
    public static String QUICK_DOWNLOAD_ADDRESS_GH_PROXY_PREFIX = "https://gh-proxy.org";
    public static String ERROR_MSG_CONFIG_DATA_READ = "{\"info\":\"无法读取配置，请检查存储权限\",\"msg\":\"MSG\"}";
    public static String ERROR_MSG_CONFIG_DATA_WRITE = "配置更新失败";
    public static String ANDROID_SHARED_DATA_PREFERENCES_NAME = "USER_INFO";
    public static String ANDROID_SHARED_DATA_KEY_ALIST_INITIALIZED = "alist_initialized";
    public static String ANDROID_SHARED_DATA_KEY_ADMIN_PASSWORD = "admin_password";
    /** 是否已自动申请过权限（申请过一次后不再每次冷启动重复弹系统页） */
    public static String ANDROID_SHARED_DATA_KEY_PERMISSION_PROMPTED = "permission_prompted";
    public static String UNIVERSAL_ABI_NAME = "universal";
    public static String VERSION_INFO = "AListLite v%s | Powered by OpenList v%s";
    public static List<String> SUPPORTED_DOWNLOAD_ABI_NAMES = Arrays.asList("x86", "armeabi-v7a", "x86_64", "arm64-v8a");
    public static Map<String, String> permissionDescriptionMap = new HashMap<>();

    static {
        permissionDescriptionMap.put(Permission.POST_NOTIFICATIONS, "用于显示服务运行状态");
        permissionDescriptionMap.put(Permission.READ_EXTERNAL_STORAGE, "用于浏览本地存储");
        permissionDescriptionMap.put(Permission.READ_MEDIA_IMAGES, "用于浏览本地图片");
        permissionDescriptionMap.put(Permission.READ_MEDIA_VIDEO, "用于浏览本地视频");
        permissionDescriptionMap.put(Permission.READ_MEDIA_AUDIO, "用于浏览本地音频");
        permissionDescriptionMap.put(Permission.WRITE_EXTERNAL_STORAGE, "用于向本地存储写入文件");
        permissionDescriptionMap.put(Permission.MANAGE_EXTERNAL_STORAGE, "用于向本地存储上传、修改和删除文件");
        permissionDescriptionMap.put(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, "用于避免服务在后台被系统限制");
        permissionDescriptionMap.put(Manifest.permission.RECEIVE_BOOT_COMPLETED, "用于开机自动启动服务");
    }
}
