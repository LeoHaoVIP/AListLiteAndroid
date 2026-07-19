package com.leohao.android.alistlite.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.leohao.android.alistlite.R;

/**
 * 版本更新检查工具
 *
 * @author LeoHao
 */
public class UpdateChecker {
    private static final String TAG = "UpdateChecker";

    /**
     * 检查新版本并提示下载
     *
     * @param activity       Activity 上下文
     * @param currentVersion 当前应用版本号
     */
    public static void check(Activity activity, String currentVersion) {
        new Thread(() -> {
            try {
                String releaseInfo;
                try {
                    releaseInfo = MyHttpUtil.request(Constants.URL_RELEASE_LATEST, Method.GET);
                } catch (Throwable t) {
                    showToastOnMain(activity, "无法获取更新: " + t.getLocalizedMessage());
                    return;
                }
                JSONObject release = JSONUtil.parseObj(releaseInfo);
                if (!release.containsKey("tag_name")) {
                    showToastOnMain(activity, "未发现新版本信息");
                    return;
                }

                String latestVersion = release.getStr("tag_name").substring(1);
                String latestOnOpenListVersion = release.getStr("name").substring(15);
                String updateJournal = String.format("🔥 新版本基于 OpenList %s 构建\r\n\r\n%s",
                        latestOnOpenListVersion, release.getStr("body"));
                String downloadLinkGitHub = (String) release.getByPath("assets[0].browser_download_url");
                String downloadLinkFast = String.format("%s/%s",
                        Constants.QUICK_DOWNLOAD_ADDRESS_GH_PROXY_PREFIX, downloadLinkGitHub);

                if (AppUtil.compareVersion(latestVersion, currentVersion) > 0) {
                    activity.runOnUiThread(() -> {
                        String title = String.format("🎉 AListLite %s 已发布", latestVersion);
                        new AlertDialog.Builder(activity, R.style.IOSAlertDialog)
                                .setTitle(title)
                                .setMessage(updateJournal)
                                .setCancelable(true)
                                .setPositiveButton("镜像加速下载", (d, w) -> openExternalUrl(activity, downloadLinkFast))
                                .setNeutralButton("GitHub官网下载", (d, w) -> openExternalUrl(activity, downloadLinkGitHub))
                                .setNegativeButton("取消", null)
                                .show();
                    });
                } else {
                    showToastOnMain(activity, String.format("当前已是最新版本（v%s）", currentVersion));
                }
            } catch (Exception e) {
                Log.e(TAG, "check: " + e.getLocalizedMessage());
            }
        }).start();
    }

    private static void showToastOnMain(Activity activity, String msg) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        } else {
            activity.runOnUiThread(() -> Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show());
        }
    }

    private static void openExternalUrl(Activity activity, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "无法打开此外部链接", Toast.LENGTH_SHORT).show();
        }
    }
}
