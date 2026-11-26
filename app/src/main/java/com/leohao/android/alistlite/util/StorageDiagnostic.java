package com.leohao.android.alistlite.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.leohao.android.alistlite.model.Alist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 存储诊断：逐个检查「能否写入」，把结果翻译成人话。
 * <p>
 * 起因是大量 issue 反馈「能挂载、能浏览，但写不进去」——
 * 真机复现证明根因是「所有文件访问」未生效，而用户无从判断。
 *
 * @author LeoHao
 */
public final class StorageDiagnostic {

    private static final String TAG = "StorageDiagnostic";

    public static final int LEVEL_OK = 0;
    public static final int LEVEL_WARN = 1;
    public static final int LEVEL_ERROR = 2;

    public static class Item {
        public final String title;
        public final String detail;
        public final int level;

        Item(String title, String detail, int level) {
            this.title = title;
            this.detail = detail;
            this.level = level;
        }
    }

    private StorageDiagnostic() {
    }

    public static List<Item> run(Context context) {
        List<Item> items = new ArrayList<>();

        // 1. 所有文件访问
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            items.add(new Item("文件访问权限", "当前系统无需该权限", LEVEL_OK));
        } else if (Environment.isExternalStorageManager()) {
            items.add(new Item("文件访问权限", "已开启，可读写本地存储", LEVEL_OK));
        } else {
            items.add(new Item("文件访问权限", "未开启，只能浏览，无法上传/修改文件", LEVEL_ERROR));
        }

        // 2. 应用数据目录（内部存储，始终可写；写不了说明系统异常）
        String dataPath = Alist.getInstance().getDataPath();
        if (dataPath == null) {
            items.add(new Item("应用数据目录", "无法获取（应用上下文异常）", LEVEL_ERROR));
        } else {
            boolean ok = probeWritable(new File(dataPath));
            items.add(new Item("应用数据目录", dataPath, ok ? LEVEL_OK : LEVEL_ERROR));
        }

        // 3. 各存储卷
        Set<String> volumes = collectVolumes(context);
        for (String volume : volumes) {
            File root = new File(volume);
            boolean rootOk = probeWritable(root);
            items.add(new Item("存储卷 " + volume,
                    rootOk ? "可以写入" : "不可写入（缺少文件访问权限，或该卷只读）",
                    rootOk ? LEVEL_OK : LEVEL_ERROR));
        }

        // 4. 应用专属目录（无需「所有文件访问」也能写）
        File[] appDirs = context.getExternalFilesDirs(null);
        if (appDirs != null) {
            for (File dir : appDirs) {
                if (dir == null) {
                    continue;
                }
                boolean ok = probeWritable(dir);
                items.add(new Item("应用专属目录",
                        (ok ? "" : "不可写：") + dir.getAbsolutePath(),
                        ok ? LEVEL_OK : LEVEL_WARN));
            }
        }

        // 5. 服务状态
        Alist alist = Alist.getInstance();
        boolean running = alist.hasRunning();
        String address;
        try {
            address = running ? alist.getServerAddress() : "服务未运行";
        } catch (Exception e) {
            address = "地址获取失败： " + e.getMessage();
        }
        items.add(new Item("AList 服务", address, running ? LEVEL_OK : LEVEL_WARN));

        items.add(new Item("检测时间",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()),
                LEVEL_OK));
        return items;
    }

    /**
     * 收集所有对外暴露的存储卷根目录，如 /storage/emulated/0、/storage/6633-3938
     */
    private static Set<String> collectVolumes(Context context) {
        Set<String> volumes = new LinkedHashSet<>();
        File primary = Environment.getExternalStorageDirectory();
        if (primary != null) {
            volumes.add(primary.getAbsolutePath());
        }
        File[] appDirs = context.getExternalFilesDirs(null);
        if (appDirs != null) {
            for (File dir : appDirs) {
                if (dir == null) {
                    continue;
                }
                String path = dir.getAbsolutePath();
                int idx = path.indexOf("/Android/");
                if (idx > 0) {
                    volumes.add(path.substring(0, idx));
                }
            }
        }
        return volumes;
    }

    /**
     * 真正写一个探测文件，写完立刻删掉
     */
    private static boolean probeWritable(File dir) {
        if (dir == null || !dir.exists()) {
            return false;
        }
        File probe = new File(dir, ".alistlite_probe_" + System.currentTimeMillis());
        try (FileOutputStream out = new FileOutputStream(probe)) {
            out.write("ok".getBytes());
            out.flush();
            return true;
        } catch (IOException e) {
            Log.w(TAG, "probe failed: " + dir + " -> " + e.getMessage());
            return false;
        } finally {
            //noinspection ResultOfMethodCallIgnored
            probe.delete();
        }
    }
}
