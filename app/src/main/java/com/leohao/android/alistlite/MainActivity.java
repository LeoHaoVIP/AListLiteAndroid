package com.leohao.android.alistlite;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kyleduo.switchbutton.SwitchButton;
import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.service.AlistService;
import com.leohao.android.alistlite.util.ActivityUtil;
import com.leohao.android.alistlite.util.ClipBoardHelper;
import com.leohao.android.alistlite.util.Constants;
import com.leohao.android.alistlite.util.MyHttpUtil;
import com.yuyh.jsonviewer.library.JsonRecyclerView;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.leohao.android.alistlite.AlistLiteApplication.context;

/**
 * @author LeoHao
 */
public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private static final String TAG = "MainActivity";
    public WebView webView = null;
    public TextView runningInfoTextView = null;
    public TextView appInfoTextView = null;
    public SwitchButton serviceSwitch = null;
    public String serverAddress = Constants.URL_ABOUT_BLANK;
    private Alist alistServer;
    private ImageButton adminButton;
    private ImageButton homepageButton;
    private ImageButton systemInfoButton;
    private ImageButton webViewGoBackButton;
    private ImageButton webViewGoForwardButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    String currentAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        //初始化控件
        initWidgets();
        //焦点设置
        initFocusSettings();
        //权限检查
        checkPermissions();
    }

    /**
     * 初始化焦点设置
     */
    private void initFocusSettings() {
        //初始化焦点为密码按钮，便于用户设置
        adminButton.postDelayed(() -> {
            //初始时焦点设置为密码按钮
            adminButton.requestFocus();
        }, 500);
        //适配 TV 端操作，控件获取到焦点时显示边框
        List<View> buttons = ActivityUtil.getAllViews(this);
        for (View button : buttons) {
            button.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    button.setBackgroundResource(R.drawable.background_border);
                } else {
                    button.setBackground(null);
                }
            });
        }
    }

    /**
     * 权限检查
     */
    private void checkPermissions() {
        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.POST_NOTIFICATIONS)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            showToast("部分权限未授予，软件可能无法正常运行");
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            showToast("请手动授予相关权限");
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //默认开启服务
        serviceSwitch.setChecked(true);
    }

    private void readyToStartService() {
        //Service启动Intent
        Intent intent = new Intent(this, AlistService.class).setAction(AlistService.ACTION_STARTUP);
        //调用服务
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        alistServer = Alist.getInstance();
        adminButton.setVisibility(View.VISIBLE);
        homepageButton.setVisibility(View.VISIBLE);
        webViewGoBackButton.setVisibility(View.VISIBLE);
        webViewGoForwardButton.setVisibility(View.VISIBLE);
    }

    private void readyToShutdownService() {
        //Service关闭Intent
        Intent intent = new Intent(this, AlistService.class).setAction(AlistService.ACTION_SHUTDOWN);
        //调用服务
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        adminButton.setVisibility(View.INVISIBLE);
        homepageButton.setVisibility(View.INVISIBLE);
        webViewGoBackButton.setVisibility(View.INVISIBLE);
        webViewGoForwardButton.setVisibility(View.INVISIBLE);
    }

    private void initWidgets() {
        serviceSwitch = findViewById(R.id.switchButton);
        appInfoTextView = findViewById(R.id.tv_app_info);
        adminButton = findViewById(R.id.btn_admin);
        //服务未开启时禁止用户设置管理员密码
        adminButton.setVisibility(View.INVISIBLE);
        homepageButton = findViewById(R.id.btn_homepage);
        homepageButton.setVisibility(View.INVISIBLE);
        systemInfoButton = findViewById(R.id.btn_showSystemInfo);
        webViewGoBackButton = findViewById(R.id.btn_webViewGoBack);
        webViewGoBackButton.setVisibility(View.INVISIBLE);
        webViewGoForwardButton = findViewById(R.id.btn_webViewGoForward);
        webViewGoForwardButton.setVisibility(View.INVISIBLE);
        runningInfoTextView = findViewById(R.id.tv_alist_status);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        webView = findViewById(R.id.webview_alist);
        // 设置背景色
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (webView.getProgress() == 100) {
                    //停止下拉刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        //获取当前APP版本号
        currentAppVersion = getCurrentAppVersion();
        //更新AppName显示版本信息
        appInfoTextView.setText(String.format("%s %s", appInfoTextView.getText(), currentAppVersion));
        //设置服务开关监听
        serviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                //准备停止AList服务
                readyToShutdownService();
                return;
            }
            try {
                //准备开启AList服务
                readyToStartService();
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
        });
        //设置下滑刷新控件监听
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            //webView刷新
            webView.reload();
        }, 0));
    }

    /**
     * 显示系统信息
     */
    public void showSystemInfo(View view) {
        AlertDialog systemInfoDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.system_info, null);
        //设定APP版本号
        TextView appVersionTextView = dialogView.findViewById(R.id.tv_app_version);
        appVersionTextView.setText(String.format("v%s ", currentAppVersion));
        systemInfoDialog.setView(dialogView);
        systemInfoDialog.show();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        //窗口大小设置必须在show()之后
        systemInfoDialog.getWindow().setLayout(width - 100, height * 2 / 5);
    }

    /**
     * 设定管理员密码
     */
    public void setAdminPassword(View view) {
        final EditText editText = new EditText(MainActivity.this);
        //设置密码不可见
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editText.setSingleLine();
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("设置管理员密码");
        dialog.setView(editText);
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            try {
                //去除前后空格后的密码
                String pwd = editText.getText().toString().trim();
                if (!"".equals(pwd)) {
                    alistServer.setAdminPassword(editText.getText().toString());
                    showToast("管理员密码已更新");
                } else {
                    showToast("管理员密码不能为空");
                }
            } catch (Exception e) {
                showToast("管理员密码设置失败");
                Log.e(TAG, "setAdminPassword: ", e);
            }
        });
        dialog.show();
    }

    /**
     * 跳转到AList主页面
     */
    public void jumpToHomepage(View view) {
        webView.loadUrl(serverAddress);
    }

    /**
     * 管理(查看/修改) AList 配置文件
     */
    public void manageConfigData(View view) {
        AlertDialog systemInfoDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.config_view, null);
        JsonRecyclerView jsonView = dialogView.findViewById(R.id.json_view_config);
        ImageButton editButton = dialogView.findViewById(R.id.btn_edit_config);
        EditText jsonEditText = dialogView.findViewById(R.id.edit_text_config);
        jsonView.setTextSize(14);
        //读取 AList 配置
        String dataPath = context.getExternalFilesDir("data").getAbsolutePath();
        String configPath = String.format("%s%s%s", dataPath, File.separator, Constants.ALIST_CONFIG_FILENAME);
        String configJsonData;
        File configFile = new File(configPath);
        try {
            //AList 配置数据
            configJsonData = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            configJsonData = Constants.ERROR_MSG_CONFIG_DATA_READ.
                    replace("MSG", Objects.requireNonNull(e.getLocalizedMessage()));
            editButton.setVisibility(View.INVISIBLE);
        }
        //显示 AList 配置
        jsonView.bindJson(configJsonData);
        systemInfoDialog.setView(dialogView);
        systemInfoDialog.show();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        //窗口大小设置必须在show()之后
        systemInfoDialog.getWindow().setLayout(width - 100, height / 2);
        //配置编辑按钮点击事件
        String finalConfigJsonData = configJsonData;
        AtomicBoolean isEditing = new AtomicBoolean(false);
        editButton.setOnClickListener(v -> {
            //若当前为编辑状态则保存配置，否则进入编辑模式
            if (isEditing.get()) {
                //json合法性验证
                boolean isJsonLegal = true;
                try {
                    JSONUtil.parseObj(jsonEditText.getText());
                } catch (Exception ignored) {
                    isJsonLegal = false;
                }
                if (!isJsonLegal) {
                    showToast("配置文件不是合法的JSON文件");
                    return;
                }
                try {
                    //持久化配置
                    FileUtils.write(configFile, jsonEditText.getText());
                    showToast("重启服务以应用新配置");
                } catch (IOException e) {
                    showToast(Constants.ERROR_MSG_CONFIG_DATA_WRITE);
                }
                isEditing.set(false);
                //显示jsonView
                jsonView.setVisibility(View.VISIBLE);
                jsonEditText.setVisibility(View.INVISIBLE);
                editButton.setImageResource(R.drawable.edit);
            } else {
                showToast("错误配置可能导致服务无法启动，请谨慎修改！");
                isEditing.set(true);
                jsonEditText.setText(finalConfigJsonData);
                //隐藏jsonView
                jsonView.setVisibility(View.INVISIBLE);
                jsonEditText.setVisibility(View.VISIBLE);
                editButton.setImageResource(R.drawable.save);
            }
        });
    }

    public void checkUpdates(View view) {
        new Thread(() -> {
            //获取最新release版本信息
            try {
                String releaseInfo = MyHttpUtil.request(Constants.UPDATE_CHECK_URL, Method.GET);
                JSONObject release = JSONUtil.parseObj(releaseInfo);
                if (!release.containsKey("tag_name")) {
                    Looper.prepare();
                    showToast("无法获取更新");
                    Looper.loop();
                    return;
                }
                //最新版本号
                String latestVersion = release.getStr("tag_name").substring(1);
                //最新版本基于的AList版本
                String latestOnAlistVersion = release.getStr("name").substring(12);
                //新版本APK下载地址（Github）
//            String downloadLinkGitHub = (String) release.getByPath("assets[0].browser_download_url");
                //加速地址（pan.leohao.cn）
                String downloadLinkFast = String.format("%s/AListLite-v%s.apk", Constants.QUICK_DOWNLOAD_ADDRESS, latestVersion);
                //发现新版本
                if (latestVersion.compareTo(currentAppVersion) > 0) {
                    Looper.prepare();
                    showToast("发现新版本 " + latestVersion + " | AList " + latestOnAlistVersion);
                    //跳转到浏览器下载
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLinkFast));
                    startActivity(intent);
                    Looper.loop();
                } else {
                    Looper.prepare();
                    showToast("当前已是最新版本");
                    Looper.loop();
                }
            } catch (Exception e) {
                Looper.prepare();
                showToast("无法获取新版本信息");
                Looper.loop();
                Log.e(TAG, "checkUpdates: " + e.getLocalizedMessage());
            }
        }).start();
    }

    /**
     * 获取当前APP版本
     */
    private String getCurrentAppVersion() {
        String versionName = "unknown";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getCurrentVersion: ", e);
        }
        return versionName;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        //关闭服务
        readyToShutdownService();
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //设置用户按返回键后，APP不退出（针对较低版本的Android）
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理webView前进后退按钮点击事件
     */
    public void webViewGoBackOrForward(View view) {
        if (view.getId() == R.id.btn_webViewGoBack) {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        }
        if (view.getId() == R.id.btn_webViewGoForward) {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        }
    }

    /**
     * 复制 AList 服务地址到剪切板
     */
    public void copyAddressToClipboard(View view) {
        ClipBoardHelper clipBoardHelper = new ClipBoardHelper(getApplicationContext());
        if (!Constants.URL_ABOUT_BLANK.equals(this.serverAddress)) {
            clipBoardHelper.copyText(this.serverAddress);
            showToast("AList 服务地址已复制");
        }
    }
}
