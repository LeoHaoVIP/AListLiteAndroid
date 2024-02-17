package com.leohao.android.alistlite;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kyleduo.switchbutton.SwitchButton;
import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.service.AlistService;
import com.leohao.android.alistlite.util.Constants;
import com.leohao.android.alistlite.util.MyHttpUtil;

/**
 * @author LeoHao
 */
public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private static final String TAG = "MainActivity";
    public WebView webView = null;
    public TextView runningInfoTextView = null;
    public SwitchButton serviceSwitch = null;
    public String serverAddress = "about:blank";
    private Alist alistServer;
    private ImageButton adminButton;
    private ImageButton homepageButton;
    private ImageButton webViewGoBackButton;
    private ImageButton webViewGoForwardButton;
    String currentAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        //初始化控件
        init();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceSwitch.setChecked(true);
    }

    private void readyToStartService() {
        //启动服务
        startService(new Intent(this, AlistService.class).setAction(AlistService.ACTION_STARTUP));
        alistServer = Alist.getInstance();
        adminButton.setVisibility(View.VISIBLE);
        homepageButton.setVisibility(View.VISIBLE);
        webViewGoBackButton.setVisibility(View.VISIBLE);
        webViewGoForwardButton.setVisibility(View.VISIBLE);
    }

    private void readyToShutdownService() {
        //关闭服务
        startService(new Intent(this, AlistService.class).setAction(AlistService.ACTION_SHUTDOWN));
        adminButton.setVisibility(View.INVISIBLE);
        homepageButton.setVisibility(View.INVISIBLE);
        webViewGoBackButton.setVisibility(View.INVISIBLE);
        webViewGoForwardButton.setVisibility(View.INVISIBLE);
    }

    private void init() {
        serviceSwitch = findViewById(R.id.switchButton);
        adminButton = findViewById(R.id.btn_admin);
        //服务未开启时禁止用户设置管理员密码
        adminButton.setVisibility(View.INVISIBLE);
        homepageButton = findViewById(R.id.btn_homepage);
        homepageButton.setVisibility(View.INVISIBLE);
        webViewGoBackButton = findViewById(R.id.btn_webViewGoBack);
        webViewGoBackButton.setVisibility(View.INVISIBLE);
        webViewGoForwardButton = findViewById(R.id.btn_webViewGoForward);
        webViewGoForwardButton.setVisibility(View.INVISIBLE);
        runningInfoTextView = findViewById(R.id.tv_alist_status);
        webView = findViewById(R.id.webview_alist);
        // 设置背景色
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        //获取当前APP版本号
        currentAppVersion = getCurrentAppVersion();
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

    public void checkUpdates(View view) {
        new Thread(() -> {
            //获取最新release版本信息
            String releaseInfo = MyHttpUtil.request(Constants.updateCheckUrl, Method.GET);
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
            //新版本APK下载地址
            String latestAppDownloadLink = (String) release.getByPath("assets[0].browser_download_url");
            //发现新版本
            if (latestVersion.compareTo(currentAppVersion) > 0) {
                Looper.prepare();
                showToast("发现新版本 " + latestVersion + " | AList " + latestOnAlistVersion);
                //跳转到浏览器下载
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(latestAppDownloadLink));
                startActivity(intent);
                Looper.loop();
            } else {
                Looper.prepare();
                showToast("当前已是最新版本");
                Looper.loop();
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
}