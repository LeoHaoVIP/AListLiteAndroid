package com.leohao.android.alistlite;

import com.leohao.android.alistlite.util.ThemeHelper;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 后台保活引导：按厂商给出「自启动 / 电池优化 / 应用详情」的实际入口。
 * <p>
 * 各家 ROM 对后台的限制差异极大，用户往往找不到开关，导致服务被杀后不会再起来。
 *
 * @author LeoHao
 */
public class KeepAliveActivity extends AppCompatActivity {

    private static class Guide {
        final String title;
        final String desc;
        final String action;
        final Intent intent;

        Guide(String title, String desc, String action, Intent intent) {
            this.title = title;
            this.desc = desc;
            this.action = action;
            this.intent = intent;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_alive);
        // 电视端：遥控器没有指针，列表和卡片也要画焦点环
        findViewById(android.R.id.content).post(() -> com.leohao.android.alistlite.util.TvUiHelper.applyFocusRing(this));
        LinearLayout container = findViewById(R.id.keep_alive_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Guide guide : buildGuides()) {
            View row = inflater.inflate(R.layout.keep_alive_item_view, container, false);
            ((TextView) row.findViewById(R.id.tv_guide_title)).setText(guide.title);
            ((TextView) row.findViewById(R.id.tv_guide_desc)).setText(guide.desc);
            TextView action = row.findViewById(R.id.tv_guide_action);
            if (guide.intent == null) {
                action.setVisibility(View.GONE);
                row.setClickable(false);
                row.setFocusable(false);
            } else {
                action.setText(guide.action + " ›");
                row.setOnClickListener(v -> open(guide));
            }
            container.addView(row);
        }
    }

    private void open(Guide guide) {
        try {
            startActivity(guide.intent);
        } catch (Exception e) {
            // 厂商改了组件名就回退到应用详情页，至少不会无反应
            Toast.makeText(this, "当前系统没有该设置页，已打开应用详情", Toast.LENGTH_SHORT).show();
            openAppDetails();
        }
    }

    private void openAppDetails() {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName())));
        } catch (Exception ignored) {
            Toast.makeText(this, "无法打开设置页", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Guide> buildGuides() {
        List<Guide> guides = new ArrayList<>();
        String rom = detectRom();

        // 1. 当前 ROM 的自启动管理
        ComponentName autostart = autostartComponent(rom);
        if (autostart != null) {
            guides.add(new Guide(rom + " 自启动管理",
                    "允许本应用开机自启与后台自启，否则重启后服务不会自动恢复。",
                    "去开启",
                    new Intent().setComponent(autostart)));
        }

        // 2. 电池优化白名单（通用）
        Intent battery = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            battery.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .setData(Uri.parse("package:" + getPackageName()));
            guides.add(new Guide("忽略电池优化",
                    "把本应用加入电池优化白名单，减少后台被系统休眠的可能。",
                    "去设置", battery));
        } else {
            guides.add(new Guide("忽略电池优化",
                    "当前系统无需单独设置。",
                    null, null));
        }

        // 3. 应用详情（权限 / 通知 / 流量等总入口）
        guides.add(new Guide("应用详情",
                "通知权限、流量限制、后台活动等开关都在这里。",
                "去查看",
                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()))));

        // 4. 说明文字
        guides.add(new Guide("为什么会被杀",
                "部分系统会限制后台活动来省电。若服务经常自动停止，请依次完成上面的设置；"
                        + "被系统「强行停止」后，任何应用都无法自动恢复，需要手动打开一次。",
                null, null));
        return guides;
    }

    private String detectRom() {
        String brand = (Build.MANUFACTURER + " " + Build.BRAND).toLowerCase(Locale.ROOT);
        if (brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco")) {
            return "MIUI/HyperOS";
        }
        if (brand.contains("huawei") || brand.contains("honor")) {
            return "EMUI/HarmonyOS";
        }
        if (brand.contains("oppo") || brand.contains("realme") || brand.contains("oneplus")) {
            return "ColorOS";
        }
        if (brand.contains("vivo") || brand.contains("iqoo")) {
            return "OriginOS";
        }
        if (brand.contains("samsung")) {
            return "One UI";
        }
        if (brand.contains("meizu")) {
            return "Flyme";
        }
        return "本机";
    }

    private ComponentName autostartComponent(String rom) {
        switch (rom) {
            case "MIUI/HyperOS":
                return new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity");
            case "EMUI/HarmonyOS":
                return new ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
            case "ColorOS":
                return new ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity");
            case "OriginOS":
                return new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity");
            case "One UI":
                return new ComponentName("com.samsung.android.lool",
                        "com.samsung.android.sm.ui.battery.BatteryActivity");
            case "Flyme":
                return new ComponentName("com.meizu.safe",
                        "com.meizu.safe.security.SHOW_APPSEC");
            default:
                return null;
        }
    }
}
