package com.leohao.android.alistlite.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.leohao.android.alistlite.MainActivity;
import com.leohao.android.alistlite.R;
import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.util.Constants;

import java.util.Locale;

/**
 * @author LeoHao
 */
public class AlistService extends Service {
    /**
     * 电源唤醒锁
     */
    private PowerManager.WakeLock wakeLock = null;
    private final static String TAG = "AListService";
    private final static String CHANNEL_ID = "com.leohao.android.alistlite";
    private final static String CHANNEL_NAME = "AlistService";
    public final static String ACTION_SHUTDOWN = "com.leohao.android.alistlite.ACTION_SHUTDOWN";
    private final Alist alistServer = Alist.getInstance();
    private String serverAddress;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId;
        // 8.0 以上需要特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel(CHANNEL_ID, CHANNEL_NAME);
        } else {
            channelId = "";
        }
        //AList服务前端访问地址
        serverAddress = String.format(Locale.CHINA, "http://%s:%d", alistServer.getBindingIP(), Constants.ALIST_RUNNING_PORT);
        Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
        //用于点击状态栏进入主页面
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        //创建消息以维持后台
        Notification notification = new NotificationCompat.Builder(this, channelId).setColor(Constants.NOTIFICATION_COLOR).setContentTitle(getString(R.string.alist_service_is_running)).setContentText(serverAddress).setSmallIcon(R.drawable.ic_launcher).setContentIntent(pendingIntent).build();
        startForeground(startId, notification);
        try {
            //根据action决定是否启动AList服务端
            if (ACTION_SHUTDOWN.equals(intent.getAction())) {
                //关闭服务
                exitService();
            } else if (!alistServer.hasRunning()) {
                //开启AList服务端
                alistServer.startup();
                //加载AList前端页面
                MainActivity.getInstance().webView.loadUrl(serverAddress);
                //更新AList运行状态
                MainActivity.getInstance().runningInfoTextView.setVisibility(View.VISIBLE);
                MainActivity.getInstance().runningInfoTextView.setText(String.format("AList 服务已启动: %s", serverAddress));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void exitService() {
        stopForeground(true);
        //关闭服务
        alistServer.shutdown();
        //清空webView
        MainActivity.getInstance().webView.loadUrl("about:blank");
        //更新AList运行状态
        MainActivity.getInstance().runningInfoTextView.setText(R.string.alist_service_not_running);
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        this.stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, AlistService.class.getName());
        wakeLock.acquire();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 创建通道并返回通道ID
     */
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            service.createNotificationChannel(channel);
        }
        return channelId;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
