package com.leohao.android.alistlite;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.util.ConfigBackupHelper;
import com.leohao.android.alistlite.util.UpdateChecker;
import com.leohao.android.alistlite.window.DialogHelper;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置二级页：把主菜单里的低频功能收进来，避免弹出菜单过长。
 * 分为「存储」「后台」「数据」「其它」四组。
 *
 * @author LeoHao
 */
public class SettingsActivity extends AppCompatActivity {

    private static final int EXPORT_REQUEST_CODE = 201;
    private static final int IMPORT_REQUEST_CODE = 202;

    private static class Item {
        final String title;
        final String desc;
        final String actionText;
        final Runnable action;

        Item(String title, String desc, String actionText, Runnable action) {
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
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Object entry : buildItems()) {
            if (entry instanceof String) {
                TextView header = new TextView(this);
                header.setText((String) entry);
                header.setTextSize(12);
                header.setPadding(dp(4), dp(12), dp(4), dp(4));
                header.setTextColor(getResources().getColor(R.color.ios_text_secondary));
                container.addView(header);
                continue;
            }
            final Item item = (Item) entry;
            View row = inflater.inflate(R.layout.keep_alive_item_view, container, false);
            ((TextView) row.findViewById(R.id.tv_guide_title)).setText(item.title);
            ((TextView) row.findViewById(R.id.tv_guide_desc)).setText(item.desc);
            TextView actionView = row.findViewById(R.id.tv_guide_action);
            actionView.setText(item.actionText + " ›");
            row.setOnClickListener(v -> item.action.run());
            container.addView(row);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusLine();
    }

    /**
     * 顶部状态行：服务是否运行 + 当前版本，方便判断某些操作（导出/诊断）的前提是否满足
     */
    private void updateStatusLine() {
        TextView status = findViewById(R.id.settings_status);
        Alist alist = Alist.getInstance();
        String address;
        try {
            address = alist.hasRunning() ? alist.getServerAddress() : null;
        } catch (Exception e) {
            address = null;
        }
        String state = address != null ? "服务运行中 · " + address : "服务未运行";
        status.setText(String.format("%s · v%s", state, getVersionName()));
    }

    private List<Object> buildItems() {
        List<Object> list = new ArrayList<>();

        list.add("存储与权限");
        list.add(new Item("权限配置", "查看并开启存储、通知、后台等权限", "去查看",
                () -> startActivity(new Intent(this, PermissionActivity.class))));
        list.add(new Item("存储诊断", "检查各存储卷能否写入、权限是否生效", "检测",
                () -> startActivity(new Intent(this, StorageDiagnosticActivity.class))));

        list.add("服务");
        list.add(new Item("高级配置", "编辑 config.json：端口、HTTPS、数据库等（改错会导致服务无法启动）", "编辑",
                () -> DialogHelper.showConfigEditor(this)));
        list.add(new Item("后台保活", "按当前系统给出自启动与电池优化的设置入口", "去设置",
                () -> startActivity(new Intent(this, KeepAliveActivity.class))));

        list.add("备份");
        list.add(new Item("导出配置", "把网盘挂载与账号配置打包保存，便于换机", "导出",
                this::startExport));
        list.add(new Item("导入配置", "从备份文件恢复配置，会覆盖当前设置", "导入",
                this::startImport));
        return list;
    }

    private void startExport() {
        if (!Alist.getInstance().hasRunning()) {
            toast("请先启动服务，再导出配置");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        intent.putExtra(Intent.EXTRA_TITLE, ConfigBackupHelper.suggestFileName());
        try {
            startActivityForResult(intent, EXPORT_REQUEST_CODE);
        } catch (Exception e) {
            toast("当前设备不支持选择保存位置");
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
        Uri uri = data.getData();
        if (requestCode == EXPORT_REQUEST_CODE) {
            doExport(uri);
        } else if (requestCode == IMPORT_REQUEST_CODE) {
            confirmImport(uri);
        }
    }

    private void doExport(Uri target) {
        new Thread(() -> {
            String message;
            try {
                OutputStream os = getContentResolver().openOutputStream(target);
                if (os == null) {
                    throw new Exception("无法写入所选位置");
                }
                int count;
                try {
                    count = ConfigBackupHelper.export(this, os);
                } finally {
                    os.close();
                }
                message = "已导出 " + count + " 个文件";
            } catch (Exception e) {
                message = "导出失败：" + e.getMessage();
            }
            final String text = message;
            runOnUiThread(() -> toast(text));
        }, "config-export").start();
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

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            toast("无法打开该页面");
        }
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
