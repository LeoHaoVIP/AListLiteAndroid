package com.leohao.android.alistlite.service;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.leohao.android.alistlite.MainActivity;
import com.leohao.android.alistlite.R;
import com.leohao.android.alistlite.broadcast.CopyReceiver;
import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.util.AppUtil;
import com.leohao.android.alistlite.util.Constants;

import java.io.IOException;
import java.util.Locale;

/**
 * AList 服务
 *
 * @author LeoHao
 */
public class AlistService extends Service {
    /**
     * 电源唤醒锁
     */
    private PowerManager.WakeLock wakeLock = null;
    public final static String TAG = "AListService";
    private final static String CHANNEL_ID = "com.leohao.android.alistlite";
    private final static String CHANNEL_NAME = "AlistService";
    public final static String ACTION_STARTUP = "com.leohao.android.alistlite.ACTION_STARTUP";
    public final static String ACTION_SHUTDOWN = "com.leohao.android.alistlite.ACTION_SHUTDOWN";
    private final Alist alistServer = Alist.getInstance();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId;
        // 8.0 以上需要特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel(CHANNEL_ID, CHANNEL_NAME);
        } else {
            channelId = "";
        }
        Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
        //用于点击状态栏进入主页面
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        //根据action决定是否启动AList服务端
        if (ACTION_SHUTDOWN.equals(intent.getAction())) {
            if (alistServer.hasRunning()) {
                //关闭服务
                exitService();
            }
            //更新磁贴状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updateAlistTileServiceState(AlistTileService.ACTION_TILE_OFF);
            }
            showToast("AList 服务已关闭");
        }
        if (ACTION_STARTUP.equals(intent.getAction())) {
            try {
                //创建消息以维持后台（此处必须先执行，否则可能产生由于未及时调用 startForeground 导致的 ANR 异常）
                Notification notification = new NotificationCompat.Builder(this, channelId).setContentTitle(getString(R.string.alist_service_is_running)).setContentText("服务正在初始化").setSmallIcon(R.drawable.ic_launcher).setContentIntent(pendingIntent).build();
                startForeground(startId, notification);
                //若服务未运行则开启
                if (!alistServer.hasRunning()) {
                    //开启AList服务端
                    alistServer.startup();
                    //判断 AList 是否为首次初始化
                    boolean hasInitialized = AppUtil.checkAlistHasInitialized();
                    if (!hasInitialized) {
                        //挂载本地存储
                        alistServer.addLocalStorageDriver(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.ALIST_STORAGE_DRIVER_MOUNT_PATH);
                        //初始化密码
                        alistServer.setAdminPassword(Constants.ALIST_DEFAULT_PASSWORD);
                        //管理员用户名
                        String adminUsername = alistServer.getAdminUser();
                        showToast(String.format("初始登录信息：%s | %s", adminUsername, Constants.ALIST_DEFAULT_PASSWORD), Toast.LENGTH_LONG);
                    }
                }
                //AList服务前端访问地址
                String serverAddress = getAlistServerAddress();
                if (MainActivity.getInstance() != null) {
                    //状态开关恢复到开启状态（不触发监听事件）
                    MainActivity.getInstance().serviceSwitch.setCheckedNoEvent(true);
                    //加载AList前端页面
                    MainActivity.getInstance().serverAddress = serverAddress;
                    MainActivity.getInstance().webView.loadUrl(serverAddress);
                    //隐藏服务未开启提示
                    MainActivity.getInstance().runningInfoTextView.setVisibility(View.GONE);
                }
                //创建 Intent，用于复制服务器地址到剪贴板
                Intent copyIntent = new Intent(this, CopyReceiver.class);
                copyIntent.putExtra("address", serverAddress);
                PendingIntent copyPendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    copyPendingIntent = PendingIntent.getBroadcast(this, 0, copyIntent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    copyPendingIntent = PendingIntent.getBroadcast(this, 0, copyIntent, PendingIntent.FLAG_ONE_SHOT);
                }
                //创建复制服务地址的 Action
                NotificationCompat.Action addressCopyAction = new NotificationCompat.Action.Builder(
                        R.drawable.copy,
                        "复制服务地址",
                        copyPendingIntent)
                        .build();
                //更新消息内容里的服务地址，同时添加服务地址复制入口
                notification = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(getString(R.string.alist_service_is_running))
                        .setContentText(serverAddress)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .addAction(addressCopyAction)
                        .setContentIntent(pendingIntent).build();
                startForeground(startId, notification);
                //更新磁贴状态
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    updateAlistTileServiceState(AlistTileService.ACTION_TILE_ON);
                }
                showToast("AList 服务已开启");
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                if (MainActivity.getInstance() != null) {
                    //状态开关恢复到关闭状态（不触发监听事件）
                    MainActivity.getInstance().serviceSwitch.setCheckedNoEvent(false);
                }
                showToast(String.format("AList 服务开启失败: %s", e.getLocalizedMessage()));
            }
        }
        return START_NOT_STICKY;
    }

    /**
     * 获取 AList 服务地址
     *
     * @return AList 服务地址（根据当前采用的协议类型动态）
     * @throws IOException
     */
    public String getAlistServerAddress() throws IOException {
        //判断是否强制开启了 HTTPS
        boolean isForceHttps = "true".equals(alistServer.getConfigValue("scheme.force_https"));
        boolean isHttpPortLegal = !"-1".equals(alistServer.getConfigValue("scheme.https_port"));
        boolean isHttpsMode = isForceHttps && isHttpPortLegal;
        //读取 AList 服务运行端口
        String serverPort = alistServer.getConfigValue(isHttpsMode ? "scheme.https_port" : "scheme.http_port");
        //AList 服务前端访问地址
        return String.format(Locale.CHINA, "%s://%s:%s", isHttpsMode ? "https" : "http", alistServer.getBindingIP(), serverPort);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void exitService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        //关闭服务
        alistServer.shutdown();
        if (MainActivity.getInstance() != null) {
            //状态开关恢复到关闭状态（不触发监听事件）
            MainActivity.getInstance().serviceSwitch.setCheckedNoEvent(false);
            //刷新 webview
            MainActivity.getInstance().webView.reload();
            //显示服务未开启提示
            MainActivity.getInstance().runningInfoTextView.setVisibility(View.VISIBLE);
        }
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        this.stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化电源管理器
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

    /**
     * 更新 AList 服务磁贴状态
     *
     * @param actionName 新服务磁贴状态对应的 ACTION 名称
     */
    private void updateAlistTileServiceState(String actionName) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            //请求监听状态
            TileService.requestListeningState(this, new ComponentName(this, AlistTileService.class));
            //更新磁贴开关状态
            Intent tileServiceIntent = new Intent(this, AlistTileService.class).setAction(actionName);
            LocalBroadcastManager.getInstance(this).sendBroadcast(tileServiceIntent);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String msg, int duration) {
        Toast.makeText(getApplicationContext(), msg, duration).show();
    }
}
