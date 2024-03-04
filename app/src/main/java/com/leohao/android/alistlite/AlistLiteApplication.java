package com.leohao.android.alistlite;

import android.app.Application;
import android.content.Context;
import com.leohao.android.alistlite.util.Constants;
import com.uqm.crashsight.crashreport.CrashReport;

/**
 * @author LeoHao
 */
public class AlistLiteApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AlistLiteApplication.context = this.getApplicationContext();
        CrashReport.setServerUrl(Constants.CRASH_REPORT_SERVER_URL);
        //初始化异常上报模块
        CrashReport.initCrashReport(getApplicationContext(), "f894249ddc", false);
    }
}
