package com.leohao.android.alistlite.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;

/**
 * 应用内日志缓冲 + 崩溃落盘。
 * <p>
 * 背景：Android 7 之后普通应用读不到 logcat（无 READ_LOGS 权限，run-as 也返回空），
 * 而 AList 的 Go 日志只覆盖服务端。为了让「一键排障包」能带上 App 自身的异常，
 * 这里做两件事：
 * 1) 环形缓冲最近 800 条日志，导出到排障包的 app-log.txt
 * 2) 捕获未处理异常，把堆栈写到 files/crash/crash-latest.txt，导出为 app-crash.txt，
 *    同时继续交给原有 handler（Bugly），不影响线上崩溃上报
 *
 * @author LeoHao
 */
public final class AppLog {

    private static final int MAX_LINES = 800;
    private static final String CRASH_DIR = "crash";
    private static final String CRASH_FILE = "crash-latest.txt";

    private static final Deque<String> BUFFER = new ArrayDeque<>(MAX_LINES);
    private static final SimpleDateFormat TIME = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINA);

    private AppLog() {
    }

    public static void d(String tag, String msg) {
        append("D", tag, msg, null);
        Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        append("I", tag, msg, null);
        Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        append("W", tag, msg, null);
        Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable t) {
        append("W", tag, msg, t);
        Log.w(tag, msg, t);
    }

    public static void e(String tag, String msg) {
        append("E", tag, msg, null);
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        append("E", tag, msg, t);
        Log.e(tag, msg, t);
    }

    private static synchronized void append(String level, String tag, String msg, Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(TIME.format(new Date())).append(' ').append(level).append('/').append(tag)
                .append(": ").append(msg == null ? "" : msg);
        if (t != null) {
            sb.append("\n").append(stackOf(t));
        }
        if (BUFFER.size() >= MAX_LINES) {
            BUFFER.pollFirst();
        }
        BUFFER.addLast(sb.toString());
    }

    /**
     * 导出缓冲内容
     */
    public static synchronized String dump() {
        if (BUFFER.isEmpty()) {
            return "(应用内暂无日志)";
        }
        StringBuilder sb = new StringBuilder();
        for (String line : BUFFER) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /**
     * 安装崩溃捕获，串接原有 handler（Bugly）
     */
    public static void installCrashHandler(final Context context) {
        final Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                saveCrash(context, thread, throwable);
            } catch (Throwable ignored) {
            }
            if (previous != null) {
                previous.uncaughtException(thread, throwable);
            }
        });
        i("AppLog", "crash handler installed (previous="
                + (previous == null ? "none" : previous.getClass().getSimpleName()) + ")");
    }

    private static void saveCrash(Context context, Thread thread, Throwable throwable) throws Exception {
        File dir = new File(context.getFilesDir(), CRASH_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date())).append('\n');
        sb.append("线程: ").append(thread == null ? "-" : thread.getName()).append('\n');
        sb.append("设备: ").append(android.os.Build.MANUFACTURER).append(' ')
                .append(android.os.Build.MODEL).append(" / Android ")
                .append(android.os.Build.VERSION.RELEASE)
                .append(" (SDK ").append(android.os.Build.VERSION.SDK_INT).append(")\n\n");
        sb.append(stackOf(throwable)).append('\n');
        sb.append("\n--- 最近日志 ---\n").append(dump());
        byte[] bytes = sb.toString().getBytes(Charset.forName("UTF-8"));
        FileOutputStream fos = new FileOutputStream(new File(dir, CRASH_FILE));
        try {
            fos.write(bytes);
        } finally {
            fos.close();
        }
    }

    /**
     * 读取上次崩溃记录；没有则返回说明文字
     */
    public static String readLastCrash(Context context) {
        try {
            File file = new File(new File(context.getFilesDir(), CRASH_DIR), CRASH_FILE);
            if (!file.exists()) {
                return "(暂无崩溃记录)";
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
        } catch (Exception e) {
            return "读取崩溃记录失败: " + e.getMessage();
        }
    }

    private static String stackOf(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
