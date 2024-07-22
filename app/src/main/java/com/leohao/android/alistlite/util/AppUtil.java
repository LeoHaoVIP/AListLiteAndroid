package com.leohao.android.alistlite.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
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

    private static List<View> getAllChildViews(View view) {
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
}
