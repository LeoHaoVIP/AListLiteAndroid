package com.leohao.android.alistlite.service;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.*;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
    public final static String ACTION_UPDATE_ADDRESS = "com.leohao.android.alistlite.ACTION_UPDATE_ADDRESS";
    private final Alist alistServer = Alist.getInstance();
    private ConnectivityManager connectivityManager = null;
    private ConnectivityManager.NetworkCallback networkCallback = null;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable networkCheckRunnable = this::updateNotificationAddress;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId;
        // 8.0 以上需要特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = CHANNEL_ID;
        } else {
            channelId = "";
        }
        //根据action决定是否启动AList服务端
        if (ACTION_SHUTDOWN.equals(intent.getAction())) {
            if (alistServer.hasRunning()) {
                //关闭服务
                exitService();
                showToast("AList 服务已关闭");
            }
            //更新磁贴状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updateAlistTileServiceState(AlistTileService.ACTION_TILE_OFF);
            }
        }
        if (ACTION_STARTUP.equals(intent.getAction())) {
            try {
                // startForeground 已在 onCreate() 中调用，此处更新通知内容
                boolean justStarted = false;
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
                    justStarted = true;
                }
                //AList 本地访问地址（WebView 用 127.0.0.1）和外部地址（通知用）
                String localAddress = getAlistServerAddress();
                String externalAddress = alistServer.getExternalAddress();
                alistServer.setCachedServerAddress(externalAddress);
                //更新通知中的外部服务地址
                updateNotification(externalAddress, channelId);
                //通知 Activity 更新 UI（WebView 加载本地地址）
                sendStatusBroadcast(Alist.STATUS_STARTED, localAddress);
                //更新磁贴状态
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    updateAlistTileServiceState(AlistTileService.ACTION_TILE_ON);
                }
                // 仅在服务实际被启动时提示，避免重复 Toast
                if (justStarted) {
                    String toastMsg = alistServer.isHttpsEnabled() ? "AList 服务已开启（HTTPS 加密）" : "AList 服务已开启";
                    showToast(toastMsg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                // 通知 Activity 启动失败
                sendStatusBroadcast(Alist.STATUS_STARTUP_ERROR);
                // 修正磁贴状态（磁贴 onClick 已预先切换为开启）
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    updateAlistTileServiceState(AlistTileService.ACTION_TILE_OFF);
                }
                showToast(String.format("AList 服务开启失败: %s", e.getLocalizedMessage()));
            }
        }
        if (ACTION_UPDATE_ADDRESS.equals(intent.getAction())) {
            try {
                String externalAddress = alistServer.getExternalAddress();
                // 同步更新缓存的外部地址
                alistServer.setCachedServerAddress(externalAddress);
                updateNotification(externalAddress, channelId);
            } catch (IOException e) {
                Log.e(TAG, "ACTION_UPDATE_ADDRESS: " + e.getMessage());
            }
        }
        return START_STICKY;
    }

    /**
     * 发送状态变更广播到 MainActivity（解耦：Service 不直接操作 UI）
     */
    private void sendStatusBroadcast(String status) {
        sendStatusBroadcast(status, null);
    }

    private void sendStatusBroadcast(String status, String address) {
        Intent intent = new Intent(Alist.ACTION_STATUS_CHANGED);
        intent.putExtra("status", status);
        if (address != null) {
            intent.putExtra("address", address);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * 更新通知栏中的服务地址
     */
    private void updateNotification(String serverAddress, String channelId) {
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
        //点击通知进入主页面
        Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        //更新消息内容里的服务地址
        String contentText = alistServer.isHttpsEnabled() ? "【SSL】" + serverAddress : serverAddress;
        Notification updatedNotification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.alist_service_is_running))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(addressCopyAction)
                .setContentIntent(pendingIntent).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, updatedNotification);
    }

    /**
     * 获取 AList 服务地址
     *
     * @return AList 服务地址（根据当前采用的协议类型动态）
     * @throws IOException
     */
    public String getAlistServerAddress() throws IOException {
        return alistServer.getServerAddress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(networkCheckRunnable);
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private void registerNetworkMonitor() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                handler.removeCallbacks(networkCheckRunnable);
                updateNotificationAddress();
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
                handler.removeCallbacks(networkCheckRunnable);
                updateNotificationAddress();
            }

            @Override
            public void onLost(@NonNull Network network) {
                handler.removeCallbacks(networkCheckRunnable);
                handler.postDelayed(networkCheckRunnable, 500);
            }
        };
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    private void updateNotificationAddress() {
        if (!alistServer.hasRunning()) return;
        try {
            String newAddress = alistServer.refreshServerAddress();
            if (newAddress == null) return; // 地址未变化
            String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? CHANNEL_ID : "";
            updateNotification(newAddress, channelId);
        } catch (IOException e) {
            Log.e(TAG, "updateNotificationAddress: " + e.getMessage());
        }
    }

    public void exitService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        //关闭服务
        alistServer.shutdown();
        // 通知 Activity 服务已停止
        sendStatusBroadcast(Alist.STATUS_STOPPED);
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
        wakeLock.setReferenceCounted(false);
        //noinspection AndroidLintWakelockTimeout
        wakeLock.acquire();
        // 立即调用 startForeground() 以防止 Android 8.0+ 的 5 秒 ANR 限制
        // 后续在 onStartCommand 中会更新通知内容
        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel(CHANNEL_ID, CHANNEL_NAME);
        } else {
            channelId = "";
        }
        Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        Notification initialNotification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.alist_service_is_running))
                .setContentText("服务正在初始化…")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, initialNotification);
        registerNetworkMonitor();
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
