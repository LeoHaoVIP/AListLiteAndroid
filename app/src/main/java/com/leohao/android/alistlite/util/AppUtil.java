package com.leohao.android.alistlite.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统工具类
 *
 * @author LeoHao
 */
public class AppUtil {
    private static final SharedDataHelper SHARED_DATA_HELPER = SharedDataHelper.getInstance();

    /**
     * 判断 AList 是否已初始化
     *
     * @return bool
     */
    public static boolean checkAlistHasInitialized() {
        String key = Constants.ANDROID_SHARED_DATA_KEY_ALIST_INITIALIZED;
        boolean isInitialized = SHARED_DATA_HELPER.getBoolShareData(key, false);
        if (!isInitialized) {
            SHARED_DATA_HELPER.putSharedData(key, true);
        }
        return isInitialized;
    }

    /**
     * 获取指定 Activity 所有控件
     *
     * @param activity activity
     * @return 控件列表
     */
    public static List<View> getAllViews(Activity activity) {
        return getAllChildViews(activity.getWindow().getDecorView());
    }

    public static List<View> getAllChildViews(View view) {
        List<View> allChildren = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewChild = vp.getChildAt(i);
                allChildren.add(viewChild);
                allChildren.addAll(getAllChildViews(viewChild));
            }
        }
        return allChildren;
    }

    /**
     * 获取设备 CPU 对应的 ABI 名称
     *
     * @return ABI
     */
    public static String getAbiName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // For Android API 21 and above
            String[] supportedABIs = Build.SUPPORTED_ABIS;
            return supportedABIs.length > 0 ? supportedABIs[0] : "unknown";
        } else {
            return Build.CPU_ABI;
        }
    }
}
