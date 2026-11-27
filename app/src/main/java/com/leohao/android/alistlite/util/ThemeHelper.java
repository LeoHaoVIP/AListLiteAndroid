package com.leohao.android.alistlite.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;

import com.leohao.android.alistlite.R;

/**
 * 主题色：把强调色做成可切换的预设。
 * <p>
 * 布局与 drawable 里统一用 ?attr/colorAccent 取色，这里在 Activity 创建前
 * setTheme() 套对应主题，就能整体换色，不用逐个控件 tint。
 *
 * @author LeoHao
 */
public final class ThemeHelper {

    private static final String PREFS = "appearance";
    private static final String KEY_ACCENT = "accent_index";

    public static final String[] ACCENT_NAMES = {"蓝色", "紫色", "绿色", "橙色", "粉色", "青色"};
    public static final int[] ACCENT_COLORS = {
            0xFF0A84FF, 0xFFAF52DE, 0xFF30D158, 0xFFFF9F0A, 0xFFFF375F, 0xFF40C8E0
    };
    private static final int[] ACCENT_THEMES = {
            R.style.Theme_AList,
            R.style.Theme_AList_Purple,
            R.style.Theme_AList_Green,
            R.style.Theme_AList_Orange,
            R.style.Theme_AList_Pink,
            R.style.Theme_AList_Teal,
    };

    private ThemeHelper() {
    }

    /**
     * 在 super.onCreate 之前调用
     */
    public static void apply(Activity activity) {
        activity.setTheme(ACCENT_THEMES[indexOf(activity)]);
    }

    /**
     * 主题在 Activity 生命周期中变了就重建（用户在「更多选项」里换了颜色）
     */
    public static void recreateIfChanged(Activity activity, int appliedIndex) {
        if (indexOf(activity) != appliedIndex) {
            activity.recreate();
        }
    }

    public static int indexOf(Context context) {
        int index = prefs(context).getInt(KEY_ACCENT, 0);
        return (index < 0 || index >= ACCENT_THEMES.length) ? 0 : index;
    }

    public static void setIndex(Context context, int index) {
        prefs(context).edit().putInt(KEY_ACCENT,
                (index < 0 || index >= ACCENT_THEMES.length) ? 0 : index).apply();
    }

    public static String nameOf(Context context) {
        return ACCENT_NAMES[indexOf(context)];
    }

    /**
     * 代码里需要强调色时的取值（跟随主题）
     */
    public static int accentColor(Context context) {
        TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.colorAccent, value, true)) {
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                    && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return value.data;
            }
        }
        return ACCENT_COLORS[indexOf(context)];
    }

    /**
     * 给不支持 ?attr 的场景（程序化设置的 background/tint）取色
     */
    public static int accentColorFrom(View view) {
        return accentColor(view.getContext());
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
