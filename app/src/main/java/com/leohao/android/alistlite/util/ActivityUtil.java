package com.leohao.android.alistlite.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LeoHao
 */
public class ActivityUtil {
    /**
     * 获取指定 Activity 所有控件
     *
     * @param activity
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
