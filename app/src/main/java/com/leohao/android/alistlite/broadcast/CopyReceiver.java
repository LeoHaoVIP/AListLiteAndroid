package com.leohao.android.alistlite.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.leohao.android.alistlite.util.ClipBoardHelper;

import static com.leohao.android.alistlite.AlistLiteApplication.applicationContext;

/**
 * @author LeoHao
 */
public class CopyReceiver extends BroadcastReceiver {
    private final ClipBoardHelper clipBoardHelper = ClipBoardHelper.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        String serverAddress = intent.getStringExtra("address");
        showToast("AList 服务地址已复制");
        clipBoardHelper.copyText(serverAddress);
    }

    private void showToast(String msg) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();
    }
}
