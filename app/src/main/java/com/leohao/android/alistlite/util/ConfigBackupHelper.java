package com.leohao.android.alistlite.util;

import android.content.Context;
import android.text.TextUtils;

import com.leohao.android.alistlite.model.Alist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 配置备份工具：把 AList 的 config.json 与 SQLite 数据（含 WAL）打成一个 zip。
 * <p>
 * 关闭 android:allowBackup 之后，换机 / 重装会丢失全部网盘挂载，因此提供手动导出导入。
 * 导出的文件不加密，内含各家网盘 token，请用户自行妥善保管。
 *
 * @author LeoHao
 */
public final class ConfigBackupHelper {
    private static final String TAG = "ConfigBackup";

    /** 需要备份的文件（WAL 一并带上，否则数据库可能缺最近写入） */
    private static final String[] BACKUP_FILES = {
            "config.json", "data.db", "data.db-wal", "data.db-shm"
    };

    private ConfigBackupHelper() {
    }

    /**
     * 建议的文件名，如 alistlite-config-20261126-1345.zip
     */
    public static String suggestFileName() {
        String ts = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA).format(new Date());
        return "alistlite-config-" + ts + ".zip";
    }

    /**
     * 导出配置到输出流，返回实际写入的文件数
     */
    public static int export(Context context, OutputStream os) throws IOException {
        File dataDir = resolveDataDir();
        if (dataDir == null) {
            throw new IOException("无法获取数据目录");
        }
        int count = 0;
        try (ZipOutputStream zos = new ZipOutputStream(os)) {
            for (String name : BACKUP_FILES) {
                File f = new File(dataDir, name);
                if (!f.exists() || !f.isFile()) {
                    continue;
                }
                zos.putNextEntry(new ZipEntry(name));
                try (InputStream in = new FileInputStream(f)) {
                    copy(in, zos);
                }
                zos.closeEntry();
                count++;
            }
        }
        if (count == 0) {
            throw new IOException("没有可导出的配置（服务是否已启动过？）");
        }
        AppLog.i(TAG, "exported " + count + " files from " + dataDir);
        return count;
    }

    /**
     * 从输入流导入配置，返回实际恢复的文件数。
     * 会先解到临时目录并校验 config.json，再覆盖数据目录。
     */
    public static int importFrom(Context context, InputStream is) throws IOException {
        File dataDir = resolveDataDir();
        if (dataDir == null) {
            throw new IOException("无法获取数据目录");
        }
        File tmpDir = new File(context.getCacheDir(), "config_import");
        deleteRecursively(tmpDir);
        if (!tmpDir.mkdirs()) {
            throw new IOException("无法创建临时目录");
        }

        int extracted = 0;
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                // 只接受已知文件名，避免 zip 内路径穿越
                if (!isAllowed(name)) {
                    continue;
                }
                File out = new File(tmpDir, name);
                try (OutputStream os = new FileOutputStream(out)) {
                    copy(zis, os);
                }
                extracted++;
            }
        }

        File tmpConfig = new File(tmpDir, "config.json");
        if (!tmpConfig.exists() || !looksLikeJson(tmpConfig)) {
            deleteRecursively(tmpDir);
            throw new IOException("备份文件无效：缺少 config.json");
        }

        int restored = 0;
        for (String name : BACKUP_FILES) {
            File src = new File(tmpDir, name);
            File dst = new File(dataDir, name);
            if (src.exists()) {
                if (dst.exists() && !dst.delete()) {
                    throw new IOException("无法覆盖 " + name);
                }
                if (!src.renameTo(dst) && !copyFile(src, dst)) {
                    throw new IOException("写入失败：" + name);
                }
                restored++;
            } else if ("data.db-wal".equals(name) || "data.db-shm".equals(name)) {
                // 备份里没有 WAL 时，清掉旧的，避免和新的 data.db 不匹配
                //noinspection ResultOfMethodCallIgnored
                dst.delete();
            }
        }
        deleteRecursively(tmpDir);
        AppLog.i(TAG, "imported " + restored + " files into " + dataDir);
        return restored;
    }

    private static boolean isAllowed(String name) {
        if (TextUtils.isEmpty(name) || name.contains("/") || name.contains("\\")) {
            return false;
        }
        for (String allowed : BACKUP_FILES) {
            if (allowed.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean looksLikeJson(File file) {
        try (InputStream in = new FileInputStream(file)) {
            int b = in.read();
            while (b == ' ' || b == '\n' || b == '\r' || b == '\t') {
                b = in.read();
            }
            return b == '{';
        } catch (IOException e) {
            return false;
        }
    }

    private static File resolveDataDir() {
        String path = Alist.getInstance().getDataPath();
        return TextUtils.isEmpty(path) ? null : new File(path);
    }

    private static boolean copyFile(File src, File dst) {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            copy(in, out);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}
