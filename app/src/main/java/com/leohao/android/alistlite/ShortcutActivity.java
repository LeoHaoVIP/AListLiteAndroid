package com.leohao.android.alistlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.leohao.android.alistlite.service.AlistService;

/**
 * 透明代理 Activity，处理快捷方式启停服务，无 UI 无动画
 */
public class ShortcutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        String action = getIntent().getStringExtra("service_action");
        if (action != null) {
            Intent si = new Intent(this, AlistService.class).setAction(action);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(si);
            } else {
                startService(si);
            }
        }
        finish();
        overridePendingTransition(0, 0);
    }
}
