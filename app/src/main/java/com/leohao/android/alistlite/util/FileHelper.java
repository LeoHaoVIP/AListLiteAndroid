package com.leohao.android.alistlite.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 极简文件工具，用来替代 commons-io。
 * <p>
 * 为什么不用 commons-io：
 * 1. 本项目原用 2.4（2014 年），受 CVE-2021-29425（文件名规范化路径穿越）影响；
 * 2. 2.6+ 在 Android 8（API 26）以下会因 java.nio.file 相关类缺失抛 NoClassDefFoundError
 * （PathUtils / AbstractStreamBuilder 静态初始化），而本项目 minSdk 21、且有大量 5.x/7.x 电视盒子；
 * 3. 全项目只用到 4 个 API（读文本、写文本、递归拷贝），java.io 实现不到 80 行。
 *
 * @author LeoHao
 */
public final class FileHelper {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final int BUF_SIZE = 8192;

    private FileHelper() {
    }

    /**
     * 以 UTF-8 读取整个文件
     */
    public static String readText(File file) throws IOException {
        try (InputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[BUF_SIZE];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            return new String(out.toByteArray(), UTF8);
        }
    }

    /**
     * 以 UTF-8 写入整个文件（自动创建父目录，覆盖写）
     */
    public static void writeText(File file, CharSequence content) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("无法创建目录: " + parent.getAbsolutePath());
        }
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(content.toString().getBytes(UTF8));
            out.flush();
        }
    }

    /**
     * 递归拷贝目录（目标目录不存在时创建）
     */
    public static void copyDirectory(File src, File dst) throws IOException {
        if (src == null || !src.isDirectory()) {
            throw new IOException("源目录不可用: " + src);
        }
        if (!dst.exists() && !dst.mkdirs()) {
            throw new IOException("无法创建目录: " + dst.getAbsolutePath());
        }
        File[] children = src.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            File target = new File(dst, child.getName());
            if (child.isDirectory()) {
                copyDirectory(child, target);
            } else {
                copyFile(child, target);
            }
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUF_SIZE];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }
    }
}
