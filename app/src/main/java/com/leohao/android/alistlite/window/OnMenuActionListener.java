package com.leohao.android.alistlite.window;

import android.view.View;

/**
 * PopupMenuWindow 菜单动作回调接口
 *
 * @author LeoHao
 */
public interface OnMenuActionListener {
    void showQrCode(View view);

    void openInBrowser(View view);

    void startPermissionCheckActivity(View view);

    void setAdminPassword(View view);

    void toggleHttps(View view);

    void manageConfigData(View view);

    void showServiceLogs(View view);

    void checkUpdates(View view);

    void showAliTvTokenGetPage(View view);

    void oneClickLogin(View view);

    void showSystemInfo(View view);

    /** 打开设置二级页（权限、诊断、保活、导入导出等低频功能） */
    void showSettings(View view);
}
