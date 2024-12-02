package com.leohao.android.alistlite.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import static com.leohao.android.alistlite.AlistLiteApplication.applicationContext;

/**
 * 剪切板工具类
 *
 * @author LeoHao
 */
public class ClipBoardHelper {
    private static ClipBoardHelper instance = null;
    private final ClipboardManager manager;

    private ClipBoardHelper(Context context) {
        //获取剪贴板管理器：
        manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public synchronized static ClipBoardHelper getInstance() {
        if (instance == null) {
            instance = new ClipBoardHelper(applicationContext);
        }
        return instance;
    }

    /**
     * 复制文字到剪切板
     *
     * @param text text
     */
    public void copyText(String text) {
        ClipData mClipData = ClipData.newPlainText("Label", text);
        manager.setPrimaryClip(mClipData);
    }

    /**
     * 复制链接url到剪切板
     *
     * @param url url
     */
    public void copyUrl(String url) {
        ClipData mClipData = ClipData.newRawUri("Label", Uri.parse(url));
        //将ClipData数据复制到剪贴板：
        manager.setPrimaryClip(mClipData);
    }

    /**
     * 复制Intent到剪切板
     *
     * @param intent intent
     */
    public void copyIntent(Intent intent) {
        //‘Label’这是任意文字标签
        ClipData mClipData = ClipData.newIntent("Label", intent);
        //将ClipData数据复制到剪贴板：
        manager.setPrimaryClip(mClipData);
    }

    /**
     * 从剪贴板中获取数据,如text文字，链接等，
     */
    public String getCopyString() {
        ClipData clipData = manager.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            return clipData.getItemAt(0).getText().toString();
        }
        return null;
    }
}
