package com.leohao.android.alistlite;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.util.AlistAdminApi;
import com.leohao.android.alistlite.util.Constants;
import com.leohao.android.alistlite.util.SharedDataHelper;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * 挂载管理：不用进网页，直接在 App 里查看 / 启用 / 停用 / 删除 / 改基础项。
 * <p>
 * 电视端价值最大：遥控器在网页里操作挂载太难。
 *
 * @author LeoHao
 */
public class StorageManageActivity extends AppCompatActivity {

    private static final String TAG = "StorageManage";
    private static final String ADMIN_USER = "admin";

    private LinearLayout container;
    private TextView subtitle;
    private AlistAdminApi api;
    private String currentBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_manage);
        container = findViewById(R.id.storage_container);
        subtitle = findViewById(R.id.tv_manage_subtitle);
        findViewById(R.id.btn_manage_refresh).setOnClickListener(v -> load());
        load();
    }

    private void load() {
        Alist alist = Alist.getInstance();
        if (!alist.hasRunning()) {
            showState("服务未启动", "请先在主界面打开服务开关");
            return;
        }
        String base;
        try {
            base = alist.getServerAddress();
        } catch (Exception e) {
            showState("获取服务地址失败", String.valueOf(e.getMessage()));
            return;
        }
        api = new AlistAdminApi(base);
        currentBaseUrl = base;
        showLoading();
        new Thread(() -> {
            try {
                api.login(ADMIN_USER, adminPassword());
                List<JSONObject> list = api.listStorages();
                runOnUiThread(() -> renderList(list, base));
            } catch (Exception e) {
                Log.e(TAG, "load: " + e.getMessage());
                final String msg = e.getMessage() == null ? "" : e.getMessage();
                runOnUiThread(() -> {
                    if (msg.toLowerCase().contains("password is incorrect")) {
                        // 用户可能在网页里改过管理员密码，本地保存的是旧的
                        promptPassword("管理员密码已变更，请输入当前密码（可在网页版「设置 → 账户」查看）");
                    } else {
                        showState("加载失败", msg + "\n可点右上角重试");
                    }
                });
            }
        }, "storage-list").start();
    }

    /**
     * 管理员密码失效时弹窗手动输入，成功后写回本地，后续一键登录/挂载管理都用新密码
     */
    private void promptPassword(String tip) {
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_password_input, null);
        final EditText input = content.findViewById(R.id.et_password);
        input.setHint("请输入当前管理员密码");
        TextView hint = content.findViewById(R.id.tv_password_hint);
        hint.setText(tip);
        new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                .setTitle("需要管理员密码")
                .setView(content)
                .setPositiveButton("确定", (d, w) -> {
                    String pwd = input.getText().toString().trim();
                    if (TextUtils.isEmpty(pwd)) {
                        toast("密码不能为空");
                        return;
                    }
                    savePassword(pwd);
                    api = new AlistAdminApi(currentBaseUrl);
                    load();
                })
                .setNegativeButton("取消", (d, w) -> showState("无法登录管理接口", "管理员密码不是 App 保存的那个\n可重新进入本页再试"))
                .show();
    }

    private void savePassword(String pwd) {
        try {
            SharedDataHelper.getInstance()
                    .putSharedData(Constants.ANDROID_SHARED_DATA_KEY_ADMIN_PASSWORD, pwd);
            Log.i(TAG, "管理员密码已更新到本地");
        } catch (Exception e) {
            Log.w(TAG, "savePassword: " + e.getLocalizedMessage());
        }
    }

    /**
     * 管理员密码取自「密码设置」里保存的值，没有则用默认密码
     */
    private String adminPassword() {
        try {
            String saved = SharedDataHelper.getInstance()
                    .getStringShareData(Constants.ANDROID_SHARED_DATA_KEY_ADMIN_PASSWORD);
            if (!TextUtils.isEmpty(saved)) {
                return saved;
            }
        } catch (Exception ignored) {
        }
        return Constants.ALIST_DEFAULT_PASSWORD;
    }

    private void renderList(List<JSONObject> list, String base) {
        container.removeAllViews();
        subtitle.setText(String.format("%s · 共 %d 个挂载", base, list.size()));
        if (list.isEmpty()) {
            showState("还没有挂载", "在网页版「存储」里添加后会出现在这里");
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        for (JSONObject item : list) {
            View row = inflater.inflate(R.layout.storage_item_view, container, false);
            bindRow(row, item);
            if (isTvDevice() && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                row.setForeground(getDrawable(R.drawable.focus_ring_overlay));
            }
            container.addView(row);
        }
    }

    private void bindRow(View row, final JSONObject item) {
        final long id = item.optLong("id");
        final String mountPath = item.optString("mount_path", "(未命名)");
        final String driver = item.optString("driver", "-");
        final boolean disabled = item.optBoolean("disabled", false);
        final String remark = item.optString("remark", "");

        ((TextView) row.findViewById(R.id.tv_mount_path)).setText(mountPath);

        TextView state = row.findViewById(R.id.tv_storage_state);
        state.setText(disabled ? getString(R.string.storage_state_disabled)
                : getString(R.string.storage_state_work));
        state.setTextColor(getResources().getColor(
                disabled ? R.color.ios_text_secondary : R.color.ios_success));

        StringBuilder meta = new StringBuilder(driver);
        String status = item.optString("status", "");
        if (!TextUtils.isEmpty(status) && !"work".equals(status)) {
            meta.append(" · ").append(status);
        }
        if (!TextUtils.isEmpty(remark)) {
            meta.append(" · ").append(remark);
        }
        ((TextView) row.findViewById(R.id.tv_storage_meta)).setText(meta.toString());

        TextView toggle = row.findViewById(R.id.btn_storage_toggle);
        toggle.setText(disabled ? getString(R.string.storage_manage_enable)
                : getString(R.string.storage_manage_disable));
        toggle.setOnClickListener(v -> toggleDisabled(id, !disabled));

        bindUsage(row, item, driver);

        row.findViewById(R.id.btn_storage_delete).setOnClickListener(v -> confirmDelete(id, mountPath));
        row.setOnClickListener(v -> showEditDialog(id, mountPath));
    }

    /**
     * 容量：优先用服务端 mount_details（要求 AList 关闭「隐藏存储详情」），
     * 拿不到再对 Local 挂载直接 statfs 本地目录，其余驱动显示「未提供」。
     */
    private void bindUsage(View row, JSONObject item, String driver) {
        TextView usage = row.findViewById(R.id.tv_storage_usage);
        ProgressBar bar = row.findViewById(R.id.pb_storage_usage);

        long total = 0, used = 0;
        JSONObject details = item.optJSONObject("mount_details");
        if (details != null) {
            JSONObject du = details.optJSONObject("disk_usage");
            if (du != null) {
                total = du.optLong("total_space", 0);
                used = du.optLong("used_space", 0);
            }
        }
        if (total <= 0 && "Local".equalsIgnoreCase(driver)) {
            long[] local = localDiskUsage(item.optString("addition", ""));
            if (local != null) {
                total = local[0];
                used = local[1];
            }
        }

        if (total > 0) {
            int percent = (int) Math.max(0, Math.min(100, used * 100 / total));
            usage.setText(String.format(Locale.CHINA, "已用 %s / %s（%d%%）",
                    formatSize(used), formatSize(total), percent));
            usage.setVisibility(View.VISIBLE);
            bar.setProgress(percent);
            bar.setVisibility(View.VISIBLE);
        } else {
            usage.setText("容量：驱动未提供");
            usage.setVisibility(View.VISIBLE);
            bar.setVisibility(View.GONE);
        }
    }

    /**
     * Local 驱动的根目录容量（App 有所有文件访问权限，可直接 statfs）
     */
    private long[] localDiskUsage(String addition) {
        try {
            JSONObject add = new JSONObject(addition);
            String root = add.optString("root_folder_path", "");
            if (TextUtils.isEmpty(root)) {
                return null;
            }
            android.os.StatFs stat = new android.os.StatFs(root);
            long total = stat.getTotalBytes();
            long free = stat.getAvailableBytes();
            if (total <= 0) {
                return null;
            }
            return new long[]{total, total - free};
        } catch (Exception e) {
            return null;
        }
    }

    private String formatSize(long bytes) {
        double gb = bytes / 1024.0 / 1024.0 / 1024.0;
        if (gb >= 1) {
            return String.format(Locale.CHINA, "%.1f GB", gb);
        }
        double mb = bytes / 1024.0 / 1024.0;
        return String.format(Locale.CHINA, "%.0f MB", mb);
    }

    private void toggleDisabled(final long id, final boolean disabled) {
        new Thread(() -> {
            try {
                api.setDisabled(id, disabled);
                runOnUiThread(() -> {
                    toast(disabled ? "已停用" : "已启用");
                    load();
                });
            } catch (Exception e) {
                Log.e(TAG, "toggle: " + e.getMessage());
                runOnUiThread(() -> toast("操作失败：" + e.getMessage()));
            }
        }, "storage-toggle").start();
    }

    private void confirmDelete(final long id, String mountPath) {
        new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                .setTitle("删除挂载")
                .setMessage("确定删除「" + mountPath + "」？网盘里的文件不会被删除，只是从 AList 移除这个挂载。")
                .setPositiveButton("删除", (d, w) -> doDelete(id))
                .setNegativeButton("取消", null)
                .show();
    }

    private void doDelete(final long id) {
        new Thread(() -> {
            try {
                api.delete(id);
                runOnUiThread(() -> {
                    toast("已删除");
                    load();
                });
            } catch (Exception e) {
                Log.e(TAG, "delete: " + e.getMessage());
                runOnUiThread(() -> toast("删除失败：" + e.getMessage()));
            }
        }, "storage-delete").start();
    }

    /**
     * 编辑基础项：挂载路径 / 备注 / 排序。其余字段原样回传，避免改坏网盘配置。
     */
    private void showEditDialog(final long id, String mountPath) {
        new Thread(() -> {
            try {
                final JSONObject storage = api.getStorage(id);
                runOnUiThread(() -> {
                    View content = LayoutInflater.from(this)
                            .inflate(R.layout.dialog_storage_edit, null);
                    final EditText pathInput = content.findViewById(R.id.et_mount_path);
                    final EditText remarkInput = content.findViewById(R.id.et_remark);
                    final EditText orderInput = content.findViewById(R.id.et_order);
                    pathInput.setText(mountPath);
                    remarkInput.setText(storage.optString("remark", ""));
                    orderInput.setText(String.valueOf(storage.optInt("order", 0)));

                    new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                            .setTitle("编辑挂载")
                            .setView(content)
                            .setPositiveButton("保存", (d, w) -> {
                                String newPath = pathInput.getText().toString().trim();
                                if (TextUtils.isEmpty(newPath)) {
                                    toast("挂载路径不能为空");
                                    return;
                                }
                                if (!newPath.startsWith("/")) {
                                    newPath = "/" + newPath;
                                }
                                try {
                                    storage.put("mount_path", newPath);
                                    storage.put("remark", remarkInput.getText().toString().trim());
                                    String orderText = orderInput.getText().toString().trim();
                                    storage.put("order", TextUtils.isEmpty(orderText)
                                            ? 0 : Integer.parseInt(orderText));
                                } catch (Exception e) {
                                    toast("输入不合法");
                                    return;
                                }
                                submitUpdate(storage);
                            })
                            .setNegativeButton("取消", null)
                            .show();
                });
            } catch (Exception e) {
                Log.e(TAG, "edit: " + e.getMessage());
                runOnUiThread(() -> toast("读取挂载信息失败：" + e.getMessage()));
            }
        }, "storage-edit-load").start();
    }

    private void submitUpdate(final JSONObject storage) {
        new Thread(() -> {
            try {
                api.update(storage);
                runOnUiThread(() -> {
                    toast("已保存");
                    load();
                });
            } catch (Exception e) {
                Log.e(TAG, "update: " + e.getMessage());
                runOnUiThread(() -> toast("保存失败：" + e.getMessage()));
            }
        }, "storage-update").start();
    }

    // ===== 状态视图 =====

    private void showLoading() {
        container.removeAllViews();
        subtitle.setText("正在读取挂载列表…");
        container.addView(stateView("⏳", "加载中…", null));
    }

    private void showState(String title, String desc) {
        container.removeAllViews();
        container.addView(stateView("🗂", title, desc));
    }

    private View stateView(String emoji, String title, String desc) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(android.view.Gravity.CENTER);
        box.setPadding(0, 60, 0, 60);

        TextView icon = new TextView(this);
        icon.setText(emoji);
        icon.setTextSize(30);
        icon.setGravity(android.view.Gravity.CENTER);
        box.addView(icon);

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(15);
        t.setTextColor(getResources().getColor(R.color.ios_text_primary));
        t.setGravity(android.view.Gravity.CENTER);
        t.setPadding(0, 12, 0, 0);
        box.addView(t);

        if (!TextUtils.isEmpty(desc)) {
            TextView d = new TextView(this);
            d.setText(desc);
            d.setTextSize(12.5f);
            d.setTextColor(getResources().getColor(R.color.ios_text_secondary));
            d.setGravity(android.view.Gravity.CENTER);
            d.setPadding(24, 8, 24, 0);
            box.addView(d);
        }
        return box;
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

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
