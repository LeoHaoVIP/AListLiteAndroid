package com.leohao.android.alistlite.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.leohao.android.alistlite.AlistLiteApplication.context;

/**
 * @author LeoHao
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class AlistTileService extends TileService {
    public final static String ACTION_TILE_ON = "com.leohao.android.alistlite.ACTION_TILE_ON";
    public final static String ACTION_TILE_OFF = "com.leohao.android.alistlite.ACTION_TILE_OFF";
    /**
     * 广播监听
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据接收到的广播消息类型，更新磁贴状态
            updateTileState(ACTION_TILE_ON.equals(intent.getAction()) ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册本地广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TILE_ON);
        filter.addAction(ACTION_TILE_OFF);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消注册本地广播接收器
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        Tile tile = getQsTile();
        int tileState = tile.getState();
        //根据磁贴状态启停服务
        switch (tileState) {
            case Tile.STATE_ACTIVE:
            case Tile.STATE_INACTIVE:
                //Service启动Intent
                String actionName = Tile.STATE_INACTIVE == tileState ? AlistService.ACTION_STARTUP : AlistService.ACTION_SHUTDOWN;
                Intent intent = new Intent(context, AlistService.class).setAction(actionName);
                //调用服务
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
                //更新磁贴状态
                updateTileState(Tile.STATE_ACTIVE == tileState ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
                break;
            default:
                break;
        }
        super.onClick();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        //默认磁贴状态为关闭状态
        updateTileState(Tile.STATE_INACTIVE);
        showToast("AList 磁贴已挂载");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        showToast("AList 磁贴已卸载");
    }

    /**
     * 更新磁贴状态
     */
    private void updateTileState(int tileState) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(tileState);
            tile.updateTile();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
