package com.leohao.android.alistlite;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import com.leohao.android.alistlite.util.Constants;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author LeoHao
 */
public class AlistLiteApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AlistLiteApplication.context = this.getApplicationContext();
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        //获取设备型号
        strategy.setDeviceModel(Build.MODEL);
        CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_APP_ID, false, strategy);
    }
}
