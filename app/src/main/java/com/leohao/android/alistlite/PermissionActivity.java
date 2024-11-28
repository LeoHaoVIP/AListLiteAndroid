package com.leohao.android.alistlite;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.leohao.android.alistlite.adaptor.PermissionListAdapter;
import com.leohao.android.alistlite.model.PermissionItem;
import com.leohao.android.alistlite.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.leohao.android.alistlite.AlistLiteApplication.context;

/**
 * 所有轨迹list展示activity
 *
 * @author LeoHao
 */
public class PermissionActivity extends AppCompatActivity implements OnItemClickListener {
    private static final String TAG = "PermissionActivity";
    private PermissionListAdapter permissionListAdapter;
    private final List<PermissionItem> permissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        //控件和数据初始化
        init();
    }

    /**
     * 初始化控件和用户信息
     */
    private void init() {
        ListView permissionListView = findViewById(R.id.permission_list);
        //初始化刷新权限列表
        refreshPermissionList();
        //初始化列表视图适配器
        permissionListAdapter = new PermissionListAdapter(context, permissionList);
        permissionListView.setAdapter(permissionListAdapter);
        permissionListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PermissionItem item = (PermissionItem) parent.getAdapter().getItem(position);
        //跳过已授权的权限
        if (item.getIsGranted()) {
            return;
        }
        //跳转到对应权限设置页面
        try {
            XXPermissions.with(this).permission(item.getPermissionName()).request(new OnPermissionCallback() {
                @Override
                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                    //新授权的权限提示设置完成
                    if (!item.getIsGranted()) {
                        showToast("设置成功");
                    }
                    //重新获取权限列表
                    refreshPermissionList();
                    //通知适配器数据变化
                    permissionListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                    if (doNotAskAgain) {
                        showToast("设置失败，请手动授予相关权限");
                    } else {
                        showToast("设置失败");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "fail to request permission: " + item.getPermissionName());
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    /**
     * 获取权限列表
     */
    private void refreshPermissionList() {
        //清空当前列表
        permissionList.clear();
        //获取当前应用的包名
        String packageName = getPackageName();
        try {
            //获取PackageInfo对象
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            //获取软件所需的所有权限
            String[] requestedPermissions = packageInfo.requestedPermissions;
            //依次检测权限授予状态
            for (String permission : requestedPermissions) {
                //跳过未声明的权限（未声明的权限代表默认允许）
                if (!Constants.permissionDescriptionMap.containsKey(permission)) {
                    continue;
                }
                //若当前 API 版本大于 23，则跳过 READ_EXTERNAL_STORAGE 检查（新版本被弃用）
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Permission.READ_EXTERNAL_STORAGE.equals(permission)) {
                    continue;
                }
                //获取权限状态
                boolean isGranted = XXPermissions.isGranted(context, permission);
                PermissionItem permissionItem = new PermissionItem(permission, permission.replaceAll(Constants.androidPermissionPrefix, ""), Constants.permissionDescriptionMap.get(permission), isGranted);
                permissionList.add(permissionItem);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
