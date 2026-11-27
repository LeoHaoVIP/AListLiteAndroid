package com.leohao.android.alistlite.interfaces;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.leohao.android.alistlite.util.AppLog;

/**
 * 本地说明页（如 WebDAV 挂载指引）与 App 之间的桥：
 * 复制文本、用外部应用打开链接。
 * <p>
 * 只服务于本地 assets 页面；对任意页面暴露这两个能力风险有限，
 * 但仍然不提供文件读写之类的接口。
 *
 * @author LeoHao
 */
public class GuideJsInterface {

    private static final String TAG = "GuideBridge";

    private final Activity activity;

    public GuideJsInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void copy(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        activity.runOnUiThread(() -> {
            ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("AListLite", text));
                Toast.makeText(activity, "已复制", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 用外部应用打开（深链、系统浏览器）
     */
    @JavascriptInterface
    public void openExternal(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        activity.runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } catch (Exception e) {
                AppLog.w(TAG, "无法打开链接: " + e.getLocalizedMessage());
                Toast.makeText(activity, "未找到可打开该链接的应用", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
