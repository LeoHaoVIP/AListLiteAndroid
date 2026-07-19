package com.leohao.android.alistlite;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.http.SslError;
import android.os.*;
import android.service.quicksettings.TileService;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kyleduo.switchbutton.SwitchButton;
import com.leohao.android.alistlite.interfaces.DownloadBlobFileJsInterface;
import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.service.AlistService;
import com.leohao.android.alistlite.service.AlistTileService;
import com.leohao.android.alistlite.util.*;
import com.leohao.android.alistlite.window.DialogHelper;
import com.leohao.android.alistlite.window.OnMenuActionListener;
import com.leohao.android.alistlite.window.PopupMenuWindow;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author LeoHao
 */
public class MainActivity extends AppCompatActivity implements OnMenuActionListener {
    private static final String TAG = "MainActivity";
    private ScheduledExecutorService broadcastScheduler = null;
    private ConnectivityManager.NetworkCallback networkCallback = null;
    private ConnectivityManager connectivityManager = null;
    private BroadcastReceiver statusReceiver = null;
    private String currentAppVersion;
    private ActionBar actionBar = null;
    private WebView webView = null;
    private TextView runningInfoTextView = null;
    private SwitchButton serviceSwitch = null;
    private Alist alistServer;
    private TextView appInfoTextView;
    private TextView sslIndicator;
    private PopupMenuWindow popupMenuWindow;
    private AlertDialog networkChangeDialog = null;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable networkCheckRunnable = this::updateServerAddressIfNeeded;
    /**
     * 文件上传回调
     */
    private ValueCallback<Uri[]> mFilePathCallback;
    private ValueCallback<Uri> mUploadMessage;
    private static final int FILE_CHOOSER_REQUEST_CODE = 100;

    // ==================== 生命周期 ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alistServer = Alist.getInstance();
        initWidgets();
        initFocusSettings();
        checkPermissions();
        checkUpdates(null);
        initBroadcastScheduler();
        initNetworkMonitor();
        initStatusReceiver();
        updateAppShortcuts();
        handleShortcutIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (alistServer != null && serviceSwitch != null) {
            boolean isRunning = alistServer.hasRunning();
            if (serviceSwitch.isChecked() != isRunning) {
                serviceSwitch.setCheckedNoEvent(isRunning);
            }
        }
        updateSslIndicator();
        updateServerAddressIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(networkCheckRunnable);
        if (networkChangeDialog != null && networkChangeDialog.isShowing()) networkChangeDialog.dismiss();
        if (broadcastScheduler != null) broadcastScheduler.shutdownNow();
        if (connectivityManager != null && networkCallback != null)
            connectivityManager.unregisterNetworkCallback(networkCallback);
        if (statusReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(statusReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleShortcutIntent(intent);
    }

    private void handleShortcutIntent(Intent intent) {
        if (intent == null) return;
        if ("remote_access".equals(intent.getStringExtra("shortcut"))) {
            DialogHelper.showQrCode(this, alistServer);
        }
    }

    @Override
    public void finish() {
        readyToShutdownService();
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack() && alistServer != null && alistServer.hasRunning()) {
                webView.goBack();
            } else {
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (mFilePathCallback != null) {
                Uri[] results = null;
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) results = new Uri[]{Uri.parse(dataString)};
                }
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            }
            if (mUploadMessage != null) {
                Uri result = (resultCode == Activity.RESULT_OK && data != null) ? data.getData() : null;
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    // ==================== 初始化 ====================

    private void initWidgets() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        serviceSwitch = findViewById(R.id.switchButton);
        appInfoTextView = findViewById(R.id.tv_app_info);
        sslIndicator = findViewById(R.id.tv_ssl_indicator);
        runningInfoTextView = findViewById(R.id.tv_alist_status);
        webView = findViewById(R.id.webview_alist);
        popupMenuWindow = new PopupMenuWindow(this, this);
        popupMenuWindow.setOnDismissListener(() -> backgroundAlpha(1.0f));
        initWebview();
        currentAppVersion = getCurrentAppVersion();
        serviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                readyToShutdownService();
                return;
            }
            try {
                readyToStartService();
            } catch (RuntimeException e) {
                Log.e(TAG, "服务启动失败: " + e.getLocalizedMessage());
            }
        });
        serviceSwitch.setChecked(true);
    }

    private void initFocusSettings() {
        appInfoTextView.postDelayed(() -> appInfoTextView.requestFocus(), 1000);
        List<View> views = AppUtil.getAllViews(this);
        views.addAll(AppUtil.getAllChildViews(popupMenuWindow.getContentView()));
        for (View view : views) {
            view.setOnFocusChangeListener((v, hasFocus) -> {
                view.setBackground(hasFocus ? getDrawable(R.drawable.background_border) : null);
            });
        }
    }

    private void checkPermissions() {
        XXPermissions.with(this)
                .permission(Permission.POST_NOTIFICATIONS)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) showToast("部分权限未授予，软件可能无法正常运行");
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) showToast("请手动授予相关权限");
                    }
                });
    }

    // ==================== WebView ====================

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebview() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.removeJavascriptInterface("searchBoxJavaBredge_");
        webView.addJavascriptInterface(new DownloadBlobFileJsInterface(this), "Android");
        webView.setWebChromeClient(new WebChromeClient() {
            private View mCustomView;
            private CustomViewCallback mCustomViewCallback;
            final FrameLayout videoContainer = findViewById(R.id.video_container);

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = filePathCallback;
                try {
                    startActivityForResult(fileChooserParams.createIntent(), FILE_CHOOSER_REQUEST_CODE);
                } catch (Exception e) {
                    mFilePathCallback = null;
                    showToast("无法打开文件选择器");
                    return false;
                }
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_CHOOSER_REQUEST_CODE);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(acceptType);
                startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_CHOOSER_REQUEST_CODE);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomView = view;
                videoContainer.addView(mCustomView);
                mCustomViewCallback = callback;
                webView.setVisibility(View.GONE);
                actionBar.hide();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                webView.setVisibility(View.VISIBLE);
                if (mCustomView == null) return;
                mCustomView.setVisibility(View.GONE);
                videoContainer.removeView(mCustomView);
                mCustomViewCallback.onCustomViewHidden();
                mCustomView = null;
                actionBar.show();
                getWindow().getDecorView().setSystemUiVisibility(0);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                super.onHideCustomView();
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Blob 下载拦截
                view.evaluateJavascript(
                        "(function(){" +
                                "  var origClick=HTMLAnchorElement.prototype.click;" +
                                "  HTMLAnchorElement.prototype.click=function(){" +
                                "    var u=this.href;" +
                                "    if(u&&u.startsWith('blob:')){" +
                                "      fetch(u).then(function(r){return r.blob()}).then(function(b){" +
                                "        var r=new FileReader();" +
                                "        r.onloadend=function(){Android.getBase64FromBlobData(r.result);};" +
                                "        r.readAsDataURL(b);" +
                                "      }).catch(function(e){});" +
                                "      return;" +
                                "    }" +
                                "    origClick.call(this);" +
                                "  };" +
                                "})();", null);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                if (url.equals(Constants.URL_LOCAL_ABOUT_ALIST_LITE) || url.equals(Constants.URL_LOCAL_RELEASE_LOG)) {
                    String versionInfo = String.format(Constants.VERSION_INFO, currentAppVersion, Constants.OPENLIST_VERSION);
                    String jsCode = "document.getElementById('text_version').innerHTML='" + versionInfo + "';";
                    webView.evaluateJavascript("javascript:(function(){" + jsCode + "})();", null);
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("blob")) return false;
                if (!url.startsWith("http") && !url.startsWith("file")) {
                    try {
                        openExternalUrl(url);
                    } catch (Exception ignored) {
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }
        });
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            if (url.startsWith("blob")) {
                webView.evaluateJavascript("__blobDownload('" + url + "')", null);
                return;
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimetype);
            String fileName = MyHttpUtil.guessFileName(contentDisposition);
            String extensionFromName = MimeTypeMap.getFileExtensionFromUrl(fileName);
            if (!TextUtils.isEmpty(extensionFromName))
                fileName = fileName.replace("." + extensionFromName, "") + "." + extensionFromName;
            else
                fileName += MyHttpUtil.getFileExtension(mimetype);
            request.setTitle(fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "开始下载: " + fileName, Toast.LENGTH_SHORT).show();
        });
    }

    // ==================== 服务通信 ====================

    private void initBroadcastScheduler() {
        broadcastScheduler = Executors.newSingleThreadScheduledExecutor();
        broadcastScheduler.scheduleWithFixedDelay(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TileService.requestListeningState(this, new ComponentName(this, AlistTileService.class));
                String action = (alistServer != null && alistServer.hasRunning())
                        ? AlistTileService.ACTION_TILE_ON : AlistTileService.ACTION_TILE_OFF;
                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(new Intent(this, AlistTileService.class).setAction(action));
            }
        }, 2, 3, TimeUnit.SECONDS);
    }

    private void initNetworkMonitor() {
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                runOnUiThread(MainActivity.this::updateServerAddressIfNeeded);
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
                runOnUiThread(MainActivity.this::updateServerAddressIfNeeded);
            }

            @Override
            public void onLost(@NonNull Network network) {
                // 延迟检查，等网络接口完全拆除后再获取最终状态，避免读到过渡态 IPv6
                handler.removeCallbacks(networkCheckRunnable);
                handler.postDelayed(networkCheckRunnable, 2000);
            }
        };
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    private void initStatusReceiver() {
        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");
                if (status == null) return;
                switch (status) {
                    case Alist.STATUS_STARTED: {
                        String address = intent.getStringExtra("address");
                        serviceSwitch.setCheckedNoEvent(true);
                        if (address != null) webView.loadUrl(address);
                        runningInfoTextView.setVisibility(View.GONE);
                        updateSslIndicator();
                        updateAppShortcuts();
                        break;
                    }
                    case Alist.STATUS_STOPPED:
                        serviceSwitch.setCheckedNoEvent(false);
                        webView.reload();
                        runningInfoTextView.setVisibility(View.VISIBLE);
                        updateAppShortcuts();
                        break;
                    case Alist.STATUS_STARTUP_ERROR:
                        serviceSwitch.setCheckedNoEvent(false);
                        updateAppShortcuts();
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(statusReceiver, new IntentFilter(Alist.ACTION_STATUS_CHANGED));
    }

    private String lastPrimaryIP = null;
    private boolean firstNetworkCheck = true;

    /**
     * 网络变化时弹框提示（仅比较首选 IP，避免临时 IPv6 地址轮换导致误判）
     */
    private void updateServerAddressIfNeeded() {
        if (alistServer == null || !alistServer.hasRunning()) return;
        try {
            String currentPrimaryIP = alistServer.getPrimaryIP();
            if (firstNetworkCheck) {
                firstNetworkCheck = false;
                lastPrimaryIP = currentPrimaryIP;
                return;
            }
            // 首选 IP 未变化则无需弹框（临时 IPv6 地址轮换不影响对外服务地址）
            if (currentPrimaryIP.equals(lastPrimaryIP)) return;
            lastPrimaryIP = currentPrimaryIP;
            String address = alistServer.getExternalAddress();
            Log.i(TAG, "外部地址已更新为 " + address);
            boolean isLocal = address.contains("localhost") || address.contains("127.0.0.1");
            if (networkChangeDialog != null && networkChangeDialog.isShowing()) {
                networkChangeDialog.dismiss();
            }
            networkChangeDialog = new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                    .setTitle("网络环境变化")
                    .setMessage(isLocal
                            ? "已切换至本地回环地址，仅本机可访问"
                            : "外部访问地址已更新为 " + address)
                    .setPositiveButton("确定", (d, w) -> networkChangeDialog = null)
                    .setOnDismissListener(d -> networkChangeDialog = null)
                    .show();
        } catch (IOException e) {
            Log.e(TAG, "updateServerAddressIfNeeded: 获取外部地址失败", e);
        }
    }

    private void updateSslIndicator() {
        if (sslIndicator != null && alistServer != null) {
            boolean show = alistServer.isHttpsEnabled() && alistServer.hasRunning();
            sslIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 更新桌面图标长按快捷菜单
     */
    private void updateAppShortcuts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return;
        ShortcutManager sm = getSystemService(ShortcutManager.class);
        if (sm == null) return;

        boolean running = alistServer != null && alistServer.hasRunning();
        boolean https = alistServer != null && alistServer.isHttpsEnabled();
        List<ShortcutInfo> shortcuts = new ArrayList<>();

        if (running) {
            shortcuts.add(new ShortcutInfo.Builder(this, "stop_service")
                    .setShortLabel("关闭服务")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_stop))
                    .setIntent(new Intent(this, ShortcutActivity.class)
                            .setAction(Intent.ACTION_VIEW)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            .putExtra("service_action", AlistService.ACTION_SHUTDOWN))
                    .build());
            shortcuts.add(new ShortcutInfo.Builder(this, "remote_access")
                    .setShortLabel("远程访问")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_menu_qrcode))
                    .setIntent(new Intent(this, MainActivity.class)
                            .setAction(Intent.ACTION_VIEW)
                            .putExtra("shortcut", "remote_access"))
                    .build());
        } else {
            shortcuts.add(new ShortcutInfo.Builder(this, "start_service")
                    .setShortLabel("启动服务")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_start))
                    .setIntent(new Intent(this, ShortcutActivity.class)
                            .setAction(Intent.ACTION_VIEW)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            .putExtra("service_action", AlistService.ACTION_STARTUP))
                    .build());
        }

        sm.setDynamicShortcuts(shortcuts);
    }

    private void readyToStartService() {
        Intent intent = new Intent(this, AlistService.class).setAction(AlistService.ACTION_STARTUP);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(intent);
            else
                startService(intent);
        } catch (RuntimeException e) {
            serviceSwitch.setCheckedNoEvent(false);
            showToast("服务启动失败，请检查系统权限设置或重启应用");
            Log.e(TAG, "readyToStartService: 前台服务启动失败", e);
            throw e;
        }
    }

    private void readyToShutdownService() {
        Intent intent = new Intent(this, AlistService.class).setAction(AlistService.ACTION_SHUTDOWN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else
            startService(intent);
    }

    private void restartService() {
        showToast("正在重启服务");
        if (alistServer.hasRunning()) readyToShutdownService();
        new Handler(getMainLooper()).postDelayed(() -> {
            try {
                readyToStartService();
                updateSslIndicator();
                updateAppShortcuts();
            } catch (RuntimeException e) {
                showToast("服务重启失败: " + e.getMessage());
                Log.e(TAG, "restartService: " + e.getMessage());
            }
        }, 1500);
    }

    // ==================== OnMenuActionListener 实现 ====================

    @Override
    public void showQrCode(View view) {
        DialogHelper.showQrCode(this, alistServer);
    }

    @Override
    public void openInBrowser(View view) {
        if (!alistServer.hasRunning()) {
            showToast("AList 服务未启动");
            return;
        }
        try {
            startActivity(Intent.parseUri(alistServer.getServerAddress(), Intent.URI_INTENT_SCHEME));
        } catch (Exception e) {
            showToast("获取服务地址失败");
        }
    }

    @Override
    public void oneClickLogin(View view) {
        if (!alistServer.hasRunning()) {
            showToast("AList 服务未启动");
            return;
        }
        new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                .setTitle("一键登录")
                .setMessage("一键登录仅在通过 APP 设置管理员密码时有效，若您通过浏览器或其他方式修改了密码，一键登录可能不会成功。是否继续？")
                .setPositiveButton("继续", (dialog, which) -> performOneClickLogin())
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 执行一键登录：调用 AList 登录 API，获取 token 后注入 WebView localStorage
     */
    private void performOneClickLogin() {
        try {
            String serverAddress = alistServer.getServerAddress();
            String username = alistServer.getAdminUser();
            if (username == null || username.isEmpty()) {
                showToast("无法获取管理员用户名");
                return;
            }
            String password = SharedDataHelper.getInstance()
                    .getStringShareData(Constants.ANDROID_SHARED_DATA_KEY_ADMIN_PASSWORD);
            if (password == null || password.isEmpty()) {
                password = Constants.ALIST_DEFAULT_PASSWORD;
            }
            final String apiUrl = serverAddress + "/api/auth/login";
            final String finalPassword = password;
            showToast("正在一键登录");
            // 在后台线程调用登录 API
            new Thread(() -> {
                try {
                    JSONObject requestBody = JSONUtil.createObj()
                            .set("username", username)
                            .set("password", finalPassword);
                    String response = HttpUtil.createRequest(Method.POST, apiUrl)
                            .body(requestBody.toString())
                            .execute()
                            .body();
                    JSONObject json = JSONUtil.parseObj(response);
                    if (json.getInt("code") != 200) {
                        runOnUiThread(() -> showToast("一键登录失败：" + json.getStr("message", "未知错误")));
                        return;
                    }
                    String token = json.getJSONObject("data").getStr("token");
                    if (token == null || token.isEmpty()) {
                        runOnUiThread(() -> showToast("一键登录失败：无法获取登录凭证"));
                        return;
                    }
                    final String escapedToken = token.replace("\\", "\\\\").replace("'", "\\'");
                    runOnUiThread(() -> {
                        webView.evaluateJavascript(
                                "localStorage.setItem('token','" + escapedToken + "');", null);
                        try {
                            webView.loadUrl(alistServer.getServerAddress());
                        } catch (IOException e) {
                            showToast("获取服务地址失败");
                        }
                        showToast("一键登录成功");
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        showToast("一键登录失败：" + e.getMessage());
                        Log.e(TAG, "oneClickLogin: ", e);
                    });
                }
            }).start();
        } catch (Exception e) {
            showToast("一键登录失败: " + e.getMessage());
            Log.e(TAG, "oneClickLogin: ", e);
        }
    }

    @Override
    public void setAdminPassword(View view) {
        DialogHelper.showSetPassword(this, alistServer);
    }

    @Override
    public void toggleHttps(View view) {
        DialogHelper.showToggleHttps(this, alistServer, this::restartService);
    }

    @Override
    public void manageConfigData(View view) {
        DialogHelper.showConfigEditor(this);
    }

    @Override
    public void showServiceLogs(View view) {
        DialogHelper.showLogViewer(this);
    }

    @Override
    public void showSystemInfo(View view) {
        webView.loadUrl(Constants.URL_LOCAL_ABOUT_ALIST_LITE);
    }

    @Override
    public void showAliTvTokenGetPage(View v) {
        webView.loadUrl("http://127.0.0.1:4015");
    }

    @Override
    public void checkUpdates(View view) {
        UpdateChecker.check(this, currentAppVersion);
    }

    @Override
    public void startPermissionCheckActivity(View v) {
        startActivity(new Intent(this, PermissionActivity.class));
    }

    // ==================== 公开动作 ====================

    public void jumpToHomepage(View view) {
        if (alistServer.hasRunning()) {
            try {
                webView.loadUrl(alistServer.getServerAddress());
            } catch (IOException e) {
                showToast("获取服务地址失败");
            }
        } else showToast("AList 服务未启动");
    }

    public void refreshWebPage(View view) {
        webView.reload();
    }

    public void webViewGoBackOrForward(View view) {
        if (view.getId() == R.id.btn_webViewGoBack && webView.canGoBack()) webView.goBack();
        if (view.getId() == R.id.btn_webViewGoForward && webView.canGoForward()) webView.goForward();
    }

    public void copyAddressToClipboard(View view) {
        if (alistServer != null && alistServer.hasRunning()) {
            ClipBoardHelper.getInstance().copyText(alistServer.getCachedServerAddress());
            showToast("AList 服务地址已复制");
        }
    }

    public void showPopupMenu(View view) {
        if (isActivityRunning()) {
            popupMenuWindow.showAsDropDown(view, 0, 50);
            backgroundAlpha(0.6f);
        }
    }

    // ==================== 工具方法 ====================

    private String getCurrentAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getCurrentVersion: ", e);
        }
        return "unknown";
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void openExternalUrl(String url) {
        try {
            startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));
        } catch (Exception e) {
            showToast("无法打开此外部链接");
        }
    }

    private boolean isActivityRunning() {
        return !isFinishing() && !isDestroyed();
    }

    void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
