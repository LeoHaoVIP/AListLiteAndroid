package com.leohao.android.alistlite;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import com.leohao.android.alistlite.model.Alist;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author LeoHao
 */
public class AlistLiteApplication extends Application {
    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        AlistLiteApplication.applicationContext = this.getApplicationContext();
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        //获取设备型号
        strategy.setDeviceModel(Build.MODEL);
        CrashReport.initCrashReport(getApplicationContext(), strategy);
        // 进程启动就在后台线程准备好数据目录：旧版本用户从外部存储迁移到内部存储时
        // 是一次完整目录拷贝，若等首次调用方（服务启动/配置弹窗）触发会卡住主线程。
        Alist.prepareDataDirInBackground();
    }
}
