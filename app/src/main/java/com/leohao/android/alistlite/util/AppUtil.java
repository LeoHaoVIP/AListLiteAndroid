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
    private static SharedDataHelper sharedDataHelper;

    private static SharedDataHelper getSharedDataHelper() {
        if (sharedDataHelper == null) {
            sharedDataHelper = SharedDataHelper.getInstance();
        }
        return sharedDataHelper;
    }

    /**
     * 判断 AList 是否已初始化
     *
     * @return bool
     */
    public static boolean checkAlistHasInitialized() {
        String key = Constants.ANDROID_SHARED_DATA_KEY_ALIST_INITIALIZED;
        boolean isInitialized = getSharedDataHelper().getBoolShareData(key, false);
        if (!isInitialized) {
            getSharedDataHelper().putSharedData(key, true);
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
     * 比较两个版本号（支持语义化版本，如 2.0.10 > 2.0.9，2.0.1-beta10 > 2.0.1-beta9）
     * 版本号可包含预发布后缀（如 2.0.7-beta5），预发布版本视为小于正式版。
     * 预发布后缀中的数字按出现顺序逐段数值比较。
     *
     * @param v1 版本号1
     * @param v2 版本号2
     * @return 正数表示 v1 > v2，负数表示 v1 < v2，0 表示相等
     */
    public static int compareVersion(String v1, String v2) {
        // 提取主版本号（去掉预发布后缀，如 -beta5-auto9）
        String[] parts1 = v1.split("-")[0].split("\\.");
        String[] parts2 = v2.split("-")[0].split("\\.");
        int maxLen = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLen; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return num1 - num2;
            }
        }
        // 主版本号相同，比较预发布后缀
        boolean hasSuffix1 = v1.contains("-");
        boolean hasSuffix2 = v2.contains("-");
        if (hasSuffix1 && !hasSuffix2) return -1;
        if (!hasSuffix1 && hasSuffix2) return 1;
        if (hasSuffix1 && hasSuffix2) {
            // 提取后缀中的数字序列并逐段比较
            int[] suffixNums1 = extractNumbers(v1.substring(v1.indexOf('-') + 1));
            int[] suffixNums2 = extractNumbers(v2.substring(v2.indexOf('-') + 1));
            int minLen = Math.min(suffixNums1.length, suffixNums2.length);
            for (int i = 0; i < minLen; i++) {
                if (suffixNums1[i] != suffixNums2[i]) {
                    return suffixNums1[i] - suffixNums2[i];
                }
            }
            return suffixNums1.length - suffixNums2.length;
        }
        return 0;
    }

    /**
     * 从字符串中提取所有连续数字
     */
    private static int[] extractNumbers(String s) {
        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        StringBuilder num = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
            } else if (num.length() > 0) {
                list.add(Integer.parseInt(num.toString()));
                num.setLength(0);
            }
        }
        if (num.length() > 0) {
            list.add(Integer.parseInt(num.toString()));
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
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
