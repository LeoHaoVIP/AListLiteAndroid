package com.leohao.android.alistlite.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;

import com.leohao.android.alistlite.R;

import java.util.List;

/**
 * 电视端 UI 适配：焦点环、TV 判定。
 * <p>
 * 遥控器没有指针，看不见焦点就等于不会用 —— 所以二级页也要画焦点环，
 * 不能只在主界面画。
 *
 * @author LeoHao
 */
public class TvUiHelper {

    public static boolean isTv(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
                || pm.hasSystemFeature("android.hardware.type.television")) {
            return true;
        }
        int uiMode = activity.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_TYPE_MASK;
        return uiMode == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    /**
     * 给页面里所有可聚焦/可点击的控件叠加焦点环（不动原背景，避免冲掉圆形按钮与卡片描边）
     */
    public static void applyFocusRing(Activity activity) {
        if (!isTv(activity) || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        List<View> views = AppUtil.getAllViews(activity);
        for (View view : views) {
            if (view instanceof WebView) {
                continue;
            }
            if (!view.isFocusable() && !view.isClickable()) {
                continue;
            }
            view.setForeground(activity.getDrawable(R.drawable.focus_ring_overlay));
        }
    }
}
