package com.leohao.android.alistlite;

import com.leohao.android.alistlite.util.AppLog;
import com.leohao.android.alistlite.util.NsdHelper;
import com.leohao.android.alistlite.util.ThemeHelper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 局域网发现：显示本机注册的 mDNS 名称，并列出同网段内其它 AListLite / OpenList 实例。
 * <p>
 * 点条目可直接打开对方的 Web 界面（外部浏览器），避免让用户在一堆 IP 里猜。
 *
 * @author LeoHao
 */
public class LanDevicesActivity extends AppCompatActivity {

    private static final String TAG = "LanDevices";
    private static final long SEARCH_WINDOW_MS = 5000;

    private LinearLayout container;
    private TextView subtitle;
    private TextView empty;
    private TextView sectionTitle;

    private NsdHelper.Discovery discovery;
    private final List<NsdHelper.Device> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_devices);
        // 电视端：遥控器没有指针，列表和卡片也要画焦点环
        findViewById(android.R.id.content).post(() -> com.leohao.android.alistlite.util.TvUiHelper.applyFocusRing(this));
        container = findViewById(R.id.lan_container);
        subtitle = findViewById(R.id.tv_lan_subtitle);
        empty = findViewById(R.id.tv_lan_empty);
        sectionTitle = findViewById(R.id.tv_lan_section);
        findViewById(R.id.btn_lan_refresh).setOnClickListener(v -> startSearch());
        bindSelf();
        startSearch();
    }

    private void bindSelf() {
        TextView name = findViewById(R.id.tv_self_name);
        TextView desc = findViewById(R.id.tv_self_desc);
        String registered = NsdHelper.getRegisteredName();
        if (registered == null) {
            name.setText("本机未注册");
            desc.setText("服务未运行时不广播，启动服务后再回来看看");
        } else {
            name.setText(registered);
            desc.setText("已在局域网广播 " + NsdHelper.SERVICE_TYPE
                    + "，其它设备与支持 mDNS 的播放器可以直接发现");
        }
    }

    private void startSearch() {
        stopSearch();
        devices.clear();
        render();
        empty.setText(getString(R.string.lan_searching));
        empty.setVisibility(View.VISIBLE);
        sectionTitle.setVisibility(View.GONE);

        discovery = new NsdHelper.Discovery(this, new NsdHelper.DiscoveryListener() {
            @Override
            public void onServiceAdded(NsdHelper.Device device) {
                devices.add(device);
                render();
            }

            @Override
            public void onDiscoveryError(String message) {
                AppLog.w(TAG, "discovery error: " + message);
                empty.setText(message);
                empty.setVisibility(View.VISIBLE);
            }
        });
        discovery.start();
        container.postDelayed(this::finishSearchWindow, SEARCH_WINDOW_MS);
    }

    /**
     * 只搜索几秒：mDNS 是持续广播的，5 秒足够，长时间挂着费电
     */
    private void finishSearchWindow() {
        stopSearch();
        if (devices.isEmpty()) {
            subtitle.setText(getString(R.string.lan_none));
            empty.setText(getString(R.string.lan_empty));
            empty.setVisibility(View.VISIBLE);
            sectionTitle.setVisibility(View.GONE);
        }
    }

    private void stopSearch() {
        if (discovery != null) {
            discovery.stop();
            discovery = null;
        }
    }

    private void render() {
        if (devices.isEmpty()) {
            subtitle.setText(getString(R.string.lan_searching));
            return;
        }
        subtitle.setText(getString(R.string.lan_found_count, devices.size()));
        empty.setVisibility(View.GONE);
        sectionTitle.setVisibility(View.VISIBLE);
        container.setVisibility(View.VISIBLE);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < devices.size(); i++) {
            final NsdHelper.Device device = devices.get(i);
            View row = inflater.inflate(R.layout.lan_device_item, container, false);
            ((TextView) row.findViewById(R.id.tv_device_name)).setText(device.name);
            ((TextView) row.findViewById(R.id.tv_device_url)).setText(device.getUrl());
            row.setOnClickListener(v -> showDeviceDialog(device));
            container.addView(row);
            if (i < devices.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (getResources().getDisplayMetrics().density));
                lp.leftMargin = (int) (46 * getResources().getDisplayMetrics().density);
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(getResources().getColor(R.color.ios_separator));
                container.addView(divider);
            }
        }
    }

    private void showDeviceDialog(final NsdHelper.Device device) {
        final String url = device.getUrl();
        new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                .setTitle(device.name)
                .setMessage(url)
                .setPositiveButton("浏览器打开", (d, w) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        toast("没有可用的浏览器");
                    }
                })
                .setNeutralButton("复制地址", (d, w) -> {
                    ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    if (cm != null) {
                        cm.setPrimaryClip(ClipData.newPlainText("AListLite 地址", url));
                        toast("已复制 " + url);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        stopSearch();
        super.onDestroy();
    }
}
