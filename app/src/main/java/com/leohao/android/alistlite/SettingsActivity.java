package com.leohao.android.alistlite;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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
    private static final int EXPORT_TREE_REQUEST_CODE = 203;

    private static final String PREFS = "settings_ui";
    private static final String PREF_LAST_EXPORT_TREE = "last_export_tree";

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
            if (isTvDevice() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 遥控器方向键移动焦点时给个明显的描边
                row.setForeground(getDrawable(R.drawable.focus_ring_overlay));
            }

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
        list.add(new Item(R.drawable.ic_menu_config, "挂载管理",
                "查看挂载、启停、删除，改挂载路径与备注（不用进网页）", "管理",
                () -> startActivity(new Intent(this, StorageManageActivity.class))));
        list.add(new Item(R.drawable.ic_menu_config, "高级配置",
                "编辑 config.json：端口、HTTPS、数据库等（改错会导致服务无法启动）", "编辑",
                () -> DialogHelper.showConfigEditor(this)));
        list.add(new Item(R.drawable.ic_menu_keepalive, "后台保活",
                "按当前系统给出自启动与电池优化的设置入口", "去设置",
                () -> startActivity(new Intent(this, KeepAliveActivity.class))));
        list.add(new Item(R.drawable.ic_menu_diagnostic, "一键排障包",
                "收集版本、设备、挂载与日志（已脱敏），反馈问题时直接附上", "生成",
                this::startDiagnostics));

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
     * 导出入口：三种方式并存，覆盖不同 ROM 的文件选择器差异
     * 1) 上次的文件夹（长期授权，最快）
     * 2) 选择文件夹（ACTION_OPEN_DOCUMENT_TREE，无文件名输入栏）
     * 3) 兼容模式：直接写系统「下载」目录（不经过任何选择器，任何机型都能走通）
     */
    private void startExport() {
        if (!Alist.getInstance().hasRunning()) {
            toast("请先启动服务，再导出配置");
            return;
        }
        final String last = getSharedPreferences(PREFS, MODE_PRIVATE)
                .getString(PREF_LAST_EXPORT_TREE, null);
        final boolean hasLast = last != null;
        final String[] items = hasLast
                ? new String[]{"导出到上次的文件夹", "选择文件夹", "保存到「下载」文件夹（兼容模式）"}
                : new String[]{"选择文件夹", "保存到「下载」文件夹（兼容模式）"};
        new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                .setTitle("导出配置")
                .setItems(items, (d, which) -> {
                    if (hasLast && which == 0) {
                        doExportToTree(Uri.parse(last), true);
                    } else if ((hasLast && which == 1) || (!hasLast && which == 0)) {
                        pickExportTree();
                    } else {
                        doExportToDownloadsCompat();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void pickExportTree() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try {
            startActivityForResult(intent, EXPORT_TREE_REQUEST_CODE);
        } catch (Exception e) {
            Log.e("SettingsActivity", "no document tree picker", e);
            toast("当前设备没有文件夹选择器，已改用「下载」文件夹");
            doExportToDownloadsCompat();
        }
    }

    /**
     * 写入用户选的文件夹。
     *
     * @param fromSaved true 表示来自上次保存的授权目录，失败时清除记录以便重新选择
     */
    private void doExportToTree(Uri tree, boolean fromSaved) {
        final String fileName = ConfigBackupHelper.suggestFileName();
        toast("正在导出…");
        new Thread(() -> {
            String message;
            try {
                String folderName = queryTreeFolderName(tree);
                String docId = DocumentsContract.getTreeDocumentId(tree);
                Uri parent = DocumentsContract.buildDocumentUriUsingTree(tree, docId);
                Uri target = createDocumentCompat(parent, fileName);
                writeZip(target);
                message = folderName == null || folderName.isEmpty()
                        ? "已导出 " + fileName
                        : "已导出 " + fileName + "（" + folderName + "）";
            } catch (Exception e) {
                Log.e("SettingsActivity", "export to tree failed", e);
                if (fromSaved) {
                    // 目录被删除、SD 卡拔出、授权失效 —— 清掉记录，下次重新选
                    getSharedPreferences(PREFS, MODE_PRIVATE)
                            .edit().remove(PREF_LAST_EXPORT_TREE).apply();
                    message = "上次的文件夹不可用（" + e.getMessage() + "），请重新选择文件夹或使用兼容模式";
                } else {
                    message = "导出失败：" + e.getMessage() + "，可改用兼容模式";
                }
            }
            final String text = message;
            runOnUiThread(() -> toast(text));
        }, "config-export").start();
    }

    /**
     * 不同 provider 对 MIME 的接受度不同：先按 zip 建，失败再退回通用二进制类型
     */
    private Uri createDocumentCompat(Uri parent, String fileName) throws Exception {
        Uri target = null;
        try {
            target = DocumentsContract.createDocument(
                    getContentResolver(), parent, "application/zip", fileName);
        } catch (Exception e) {
            Log.w("SettingsActivity", "createDocument zip failed, retry octet-stream", e);
        }
        if (target == null) {
            target = DocumentsContract.createDocument(
                    getContentResolver(), parent, "application/octet-stream", fileName);
        }
        if (target == null) {
            throw new Exception("该文件夹不允许创建文件");
        }
        return target;
    }

    private void writeZip(Uri target) throws Exception {
        OutputStream os = getContentResolver().openOutputStream(target);
        if (os == null) {
            throw new Exception("无法写入文件");
        }
        try {
            ConfigBackupHelper.export(this, os);
        } finally {
            os.close();
        }
    }

    /**
     * 兼容模式：不经过选择器，直接写系统下载目录
     */
    private void doExportToDownloadsCompat() {
        final String fileName = ConfigBackupHelper.suggestFileName();
        toast("正在导出…");
        new Thread(() -> {
            String message;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    exportViaMediaStore(fileName);
                } else {
                    exportViaPublicDir(fileName);
                }
                message = "已导出 " + fileName + "（下载/AListLite）";
            } catch (Exception e) {
                Log.e("SettingsActivity", "compat export failed", e);
                message = "导出失败：" + e.getMessage();
            }
            final String text = message;
            runOnUiThread(() -> toast(text));
        }, "config-export-compat").start();
    }

    private void exportViaMediaStore(String fileName) throws Exception {
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
            writeZip(target);
            ContentValues done = new ContentValues();
            done.put(MediaStore.MediaColumns.IS_PENDING, 0);
            getContentResolver().update(target, done, null, null);
        } catch (Exception e) {
            getContentResolver().delete(target, null, null);
            throw e;
        }
    }

    private void exportViaPublicDir(String fileName) throws Exception {
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

    /**
     * 取所选文件夹的名字，用于提示
     */
    private String queryTreeFolderName(Uri tree) {
        try {
            String docId = DocumentsContract.getTreeDocumentId(tree);
            Uri parent = DocumentsContract.buildDocumentUriUsingTree(tree, docId);
            try (android.database.Cursor cursor = getContentResolver().query(
                    parent, new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME},
                    null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 一键排障包：收集设备/服务/挂载/日志（脱敏）打包到下载目录
     */
    private void startDiagnostics() {
        toast("正在收集信息…");
        new Thread(() -> {
            String message;
            try {
                String name = com.leohao.android.alistlite.util.DiagnosticsCollector
                        .saveToDownloads(this);
                message = "已生成 " + name + "（下载/AListLite）";
            } catch (Exception e) {
                Log.e("SettingsActivity", "diagnostics failed", e);
                message = "生成失败：" + e.getMessage();
            }
            final String text = message;
            runOnUiThread(() -> toast(text));
        }, "diagnostics").start();
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
        if (requestCode == EXPORT_TREE_REQUEST_CODE) {
            // 长期授权，下次可以直接导出到同一个文件夹
            try {
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } catch (Exception ignored) {
            }
            getSharedPreferences(PREFS, MODE_PRIVATE)
                    .edit().putString(PREF_LAST_EXPORT_TREE, uri.toString()).apply();
            doExportToTree(uri, false);
        } else if (requestCode == IMPORT_REQUEST_CODE) {
            confirmImport(uri);
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
        final String sourceName = queryDisplayName(source);
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
                try {
                    ConfigBackupHelper.importFrom(this, is);
                } finally {
                    is.close();
                }
                message = "已导入 " + sourceName + "，服务正在重启";
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

    /**
     * 取文件的显示名（用户可能改过名），用于导入提示
     */
    private String queryDisplayName(Uri uri) {
        try (android.database.Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    String name = cursor.getString(index);
                    if (name != null && !name.isEmpty()) {
                        return name;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "配置文件";
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    /**
     * 是否运行在电视 / 大屏设备上
     */
    private boolean isTvDevice() {
        android.content.pm.PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK)
                || pm.hasSystemFeature("android.hardware.type.television")) {
            return true;
        }
        int uiMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_TYPE_MASK;
        return uiMode == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION;
    }
}
