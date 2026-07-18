package com.leohao.android.alistlite.broadcast;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.leohao.android.alistlite.MainActivity;
import com.leohao.android.alistlite.R;
import com.leohao.android.alistlite.service.AlistService;

/**
 * 系统启动广播消息接收
 *
 * @author LeoHao
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String CHANNEL_ID = "boot_start_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        //处理启动完成的广播消息
        if (intent.getAction().equals(ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, AlistService.class).setAction(AlistService.ACTION_STARTUP);
            try {
                // Android 12+ (API 31+) 禁止从后台启动前台服务
                // 后台启动前台服务会抛出 ForegroundServiceStartNotAllowedException
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // 尝试启动，如果失败则发出通知提示用户手动打开应用
                    try {
                        ContextCompat.startForegroundService(context, serviceIntent);
                    } catch (RuntimeException e) {
                        Log.w("BootCompleteReceiver", "Android 12+ 禁止后台启动前台服务，发送通知提醒用户");
                        sendBootStartNotification(context);
                    }
                } else {
                    ContextCompat.startForegroundService(context, serviceIntent);
                }
            } catch (Exception e) {
                Log.e("BootCompleteReceiver", "onReceive: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * 发送通知提示用户打开应用以启动服务
     */
    private void sendBootStartNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 8.0+ 需要创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "开机自启",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("AListLite")
                .setContentText("点击打开应用以启动 AList 服务")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
