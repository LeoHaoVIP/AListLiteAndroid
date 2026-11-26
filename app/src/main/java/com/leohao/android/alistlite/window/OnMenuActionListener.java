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

    /** 导出配置（config.json + 数据库） */
    void exportConfig(View view);

    /** 导入配置 */
    void importConfig(View view);

    /** 存储诊断 */
    void showStorageDiagnostic(View view);

    /** 后台保活引导 */
    void showKeepAliveGuide(View view);
}
