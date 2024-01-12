package com.leohao.android.alistlite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.kyleduo.switchbutton.SwitchButton;
import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.service.AlistService;
import com.leohao.android.alistlite.util.Constants;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        //初始化控件
        initView();
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

    private void initView() {
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
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("URL", url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
    }

    /**
     * 显示系统信息
     */
    public void showSystemInfo(View view) {
        showToast("AList version: " + Constants.ALIST_VERSION);
    }

    /**
     * 设定管理员密码
     */
    public void setAdminPassword(View view) {
        final EditText editText = new EditText(MainActivity.this);
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
                    showToast("管理员密码已更新: " + pwd);
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