package com.leohao.android.alistlite.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.leohao.android.alistlite.service.AlistService;

import static com.leohao.android.alistlite.AlistLiteApplication.applicationContext;

/**
 * 系统启动广播消息接收
 *
 * @author LeoHao
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //处理启动完成的广播消息
        if (intent.getAction().equals(ACTION_BOOT_COMPLETED)) {
            //启动 AList 服务
            Intent serviceIntent = new Intent(context, AlistService.class).setAction(AlistService.ACTION_STARTUP);
            try {
                // 使用 ContextCompat 来启动服务，确保兼容性
                ContextCompat.startForegroundService(context, serviceIntent);
            } catch (Exception e) {
                // 捕获前台服务启动异常，避免崩溃
                Log.e("BootCompleteReceiver", "onReceive: " + e.getLocalizedMessage());
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();
    }
}
