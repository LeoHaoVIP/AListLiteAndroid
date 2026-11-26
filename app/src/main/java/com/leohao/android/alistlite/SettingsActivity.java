package com.leohao.android.alistlite;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.util.ConfigBackupHelper;
import com.leohao.android.alistlite.window.DialogHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 二级页：低频设置项集中在这里（权限、诊断、高级配置、保活、备份）
 */
public class SettingsActivity extends AppCompatActivity {

    private static final int IMPORT_REQUEST_CODE = 202;

    private static class Item {
        final int iconRes;
        final String title;
        final String desc;
        final String actionText;
        final Runnable action;

        Item(int iconRes, String title, String desc, String actionText, Runnable action) {
            this.iconRes = iconRes;
            this.title = title;
            this.desc = desc;
            this.actionText = actionText;
            this.action = action;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LinearLayout container = findViewById(R.id.settings_container);
        container.setPadding(dp(12), 0, dp(12), dp(16));
        renderItems(container);
    }

    /**
     * 分组标题 + 卡片容器：每组一块圆角卡片，行间用 1px 分隔线
     */
    private void renderItems(LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout card = null;
        boolean firstGroup = true;
        for (Object entry : buildItems()) {
            if (entry instanceof String) {
                TextView header = new TextView(this);
                header.setText((String) entry);
                header.setTextSize(12);
                header.setPadding(dp(6), firstGroup ? dp(2) : dp(20), dp(6), dp(8));
                header.setTextColor(getResources().getColor(R.color.ios_text_secondary));
                container.addView(header);
                firstGroup = false;

                card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setBackgroundResource(R.drawable.settings_card_bg);
                container.addView(card, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                continue;
            }
            if (card == null) {
                continue;
            }
            final Item item = (Item) entry;
            View row = inflater.inflate(R.layout.settings_item_view, card, false);
            ((ImageView) row.findViewById(R.id.iv_icon)).setImageResource(item.iconRes);
            ((TextView) row.findViewById(R.id.tv_title)).setText(item.title);
            ((TextView) row.findViewById(R.id.tv_desc)).setText(item.desc);
            ((TextView) row.findViewById(R.id.tv_action)).setText(item.actionText);
            row.setOnClickListener(v -> item.action.run());

            if (card.getChildCount() > 0) {
                View divider = new View(this);
                divider.setBackgroundColor(getResources().getColor(R.color.ios_separator_opaque));
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
                dlp.setMarginStart(dp(52));
                card.addView(divider, dlp);
            }
            card.addView(row);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusLine();
    }

    /**
     * 顶部状态卡片：服务运行状态 + 版本 + 访问地址，点一下可复制地址
     */
    private void updateStatusLine() {
        TextView state = findViewById(R.id.settings_status_state);
        TextView version = findViewById(R.id.settings_status_version);
        TextView address = findViewById(R.id.settings_status_address);
        View dot = findViewById(R.id.settings_status_dot);

        Alist alist = Alist.getInstance();
        String addr;
        try {
            addr = alist.hasRunning() ? alist.getServerAddress() : null;
        } catch (Exception e) {
            addr = null;
        }
        final boolean running = addr != null;
        state.setText(running ? "服务运行中" : "服务未运行");
        version.setText("v" + getVersionName());
        address.setText(running ? addr : "启动服务后这里会显示访问地址");

        int dotColor = getResources().getColor(
                running ? R.color.ios_success : R.color.ios_text_tertiary);
        dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(dotColor));

        final String copyText = running ? addr : null;
        findViewById(R.id.settings_status_card).setOnClickListener(v -> {
            if (copyText == null) {
                toast("服务未运行，暂无地址可复制");
                return;
            }
            android.content.ClipboardManager cm = (android.content.ClipboardManager)
                    getSystemService(CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(android.content.ClipData.newPlainText("AListLite 地址", copyText));
                toast("已复制 " + copyText);
            }
        });
    }

    private List<Object> buildItems() {
        List<Object> list = new ArrayList<>();

        list.add("存储与权限");
        list.add(new Item(R.drawable.ic_menu_permission, "权限配置",
                "查看并开启存储、通知、后台等权限", "去查看",
                () -> startActivity(new Intent(this, PermissionActivity.class))));
        list.add(new Item(R.drawable.ic_menu_diagnostic, "存储诊断",
                "检查各存储卷能否写入、权限是否生效", "检测",
                () -> startActivity(new Intent(this, StorageDiagnosticActivity.class))));

        list.add("服务");
        list.add(new Item(R.drawable.ic_menu_config, "高级配置",
                "编辑 config.json：端口、HTTPS、数据库等（改错会导致服务无法启动）", "编辑",
                () -> DialogHelper.showConfigEditor(this)));
        list.add(new Item(R.drawable.ic_menu_keepalive, "后台保活",
                "按当前系统给出自启动与电池优化的设置入口", "去设置",
                () -> startActivity(new Intent(this, KeepAliveActivity.class))));

        list.add("备份");
        list.add(new Item(R.drawable.ic_menu_export, "导出配置",
                "把网盘挂载与账号配置打包保存，便于换机", "导出",
                this::startExport));
        list.add(new Item(R.drawable.ic_menu_import, "导入配置",
                "从备份文件恢复配置，会覆盖当前设置", "导入",
                this::startImport));
        return list;
    }

    /**
     * 直接写到「下载/AListLite」，不再弹系统的保存位置对话框
     * （那个对话框在部分机型上会弹出一个无法点击的文件名输入框）
     */
    private void startExport() {
        if (!Alist.getInstance().hasRunning()) {
            toast("请先启动服务，再导出配置");
            return;
        }
        final String fileName = ConfigBackupHelper.suggestFileName();
        toast("正在导出…");
        new Thread(() -> {
            String message;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    exportToDownloads(fileName);
                } else {
                    exportToDownloadsLegacy(fileName);
                }
                message = "已导出 " + fileName + "（下载/AListLite）";
            } catch (Exception e) {
                Log.e("SettingsActivity", "export config failed", e);
                message = "导出失败：" + e.getMessage();
            }
            final String text = message;
            runOnUiThread(() -> toast(text));
        }, "config-export").start();
    }

    private void exportToDownloads(String fileName) throws Exception {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/zip");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS + "/AListLite");
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);

        Uri target = getContentResolver()
                .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (target == null) {
            throw new Exception("无法创建文件");
        }
        try {
            OutputStream os = getContentResolver().openOutputStream(target);
            if (os == null) {
                throw new Exception("无法写入文件");
            }
            try {
                ConfigBackupHelper.export(this, os);
            } finally {
                os.close();
            }
            ContentValues done = new ContentValues();
            done.put(MediaStore.MediaColumns.IS_PENDING, 0);
            getContentResolver().update(target, done, null, null);
        } catch (Exception e) {
            getContentResolver().delete(target, null, null);
            throw e;
        }
    }

    private void exportToDownloadsLegacy(String fileName) throws Exception {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "AListLite");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new Exception("无法创建目录");
        }
        FileOutputStream os = new FileOutputStream(new File(dir, fileName));
        try {
            ConfigBackupHelper.export(this, os);
        } finally {
            os.close();
        }
    }

    private void startImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        try {
            startActivityForResult(intent, IMPORT_REQUEST_CODE);
        } catch (Exception e) {
            toast("当前设备不支持选择文件");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }
        if (requestCode == IMPORT_REQUEST_CODE) {
            confirmImport(data.getData());
        }
    }

    private void confirmImport(Uri source) {
        new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                .setTitle("导入配置")
                .setMessage("导入会覆盖当前的网盘挂载与账号配置，且需要重启服务后生效。确定继续？")
                .setPositiveButton("导入", (d, w) -> doImport(source))
                .setNegativeButton("取消", null)
                .show();
    }

    private void doImport(Uri source) {
        final Alist alist = Alist.getInstance();
        // 先停服，避免数据库正在写入时被覆盖
        if (alist.hasRunning()) {
            alist.shutdown();
        }
        new Thread(() -> {
            String message;
            try {
                InputStream is = getContentResolver().openInputStream(source);
                if (is == null) {
                    throw new Exception("无法读取所选文件");
                }
                int count;
                try {
                    count = ConfigBackupHelper.importFrom(this, is);
                } finally {
                    is.close();
                }
                message = "已导入 " + count + " 个文件，服务正在重启";
            } catch (Exception e) {
                message = "导入失败：" + e.getMessage();
            }
            final String text = message;
            runOnUiThread(() -> {
                toast(text);
                try {
                    alist.startup();
                } catch (Exception ignored) {
                }
            });
        }, "config-import").start();
    }

    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "unknown";
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
