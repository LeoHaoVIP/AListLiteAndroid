package com.leohao.android.alistlite;

import com.leohao.android.alistlite.util.ThemeHelper;
import com.leohao.android.alistlite.util.AppLog;

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
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.http.SslError;
import android.os.*;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.text.TextUtils;
import android.util.TypedValue;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author LeoHao
 */
public class MainActivity extends AppCompatActivity implements OnMenuActionListener {
    /** 当前已应用的主题色序号，用于 onResume 时判断是否需要重建 */
    private int appliedAccentIndex = 0;
    private static final String TAG = "MainActivity";
    private static final String KEY_TV_HINT_SHOWN = "tv_hint_shown";
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
    /** 「所有文件访问」缺失提示是否已弹过（权限恢复后会自动重置） */
    private boolean allFilesAccessPrompted = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable networkCheckRunnable = this::updateServerAddressIfNeeded;
    /**
     * 文件上传回调
     */
    private ValueCallback<Uri[]> mFilePathCallback;
    private ValueCallback<Uri> mUploadMessage;
    private static final int FILE_CHOOSER_REQUEST_CODE = 100;
    /**
     * OpenList 4.x 前端（Vue3 + Vite）依赖较新的 Chromium 内核（ES2020：可选链/空值合并等）。
     * 低于该版本的系统 WebView 无法渲染页面，表现为白屏/黑屏。
     */
    private static final int MIN_SUPPORTED_CHROMIUM_VERSION = 80;
    /**
     * 检测到的 WebView 内核（Chromium）主版本号，-1 表示未检测或检测失败
     */
    private int webViewChromiumVersion = -1;
    /**
     * 是否已就内核过低提示过用户（保证只提示一次）
     */
    private boolean webViewCompatWarned = false;

    // ==================== 生命周期 ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.apply(this);
        appliedAccentIndex = ThemeHelper.indexOf(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 仅 debug 包生效：用于验证崩溃落盘链路（adb ... --es __crash_test crash）
        if (BuildConfig.DEBUG && getIntent() != null
                && "crash".equals(getIntent().getStringExtra("__crash_test"))) {
            throw new IllegalStateException("AppLog crash handler test");
        }
        alistServer = Alist.getInstance();
        initWidgets();
        initFocusSettings();
        if (isTvDevice()) {
            applyTvTextScale();
            maybeShowTvHint();
        }
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
        ThemeHelper.recreateIfChanged(this, appliedAccentIndex);
        if (alistServer != null && serviceSwitch != null) {
            boolean isRunning = alistServer.hasRunning();
            if (serviceSwitch.isChecked() != isRunning) {
                serviceSwitch.setCheckedNoEvent(isRunning);
            }
        }
        updateSslIndicator();
        updateServerAddressIfNeeded();
        checkAllFilesAccess();
    }

    /**
     * Android 11+ 的「所有文件访问」权限一旦丢失，本地存储会表现为
     * 「能挂载、能浏览，但写入/删除报 permission denied」（Issue #63/#71/#77 的真实成因，
     * 已在真机上用撤销 appops 的方式完整复现）。这里每次回到前台检查一次并明确引导。
     */
    private void checkAllFilesAccess() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return;
        }
        if (Environment.isExternalStorageManager()) {
            // 权限恢复后允许下次再次提示
            allFilesAccessPrompted = false;
            return;
        }
        if (allFilesAccessPrompted) {
            return;
        }
        allFilesAccessPrompted = true;
        new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                .setTitle("需要文件访问权限")
                .setMessage("开启「所有文件访问」后，才能向本地存储上传、重命名或删除文件。")
                .setPositiveButton("去开启", (d, w) -> {
                    try {
                        startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                Uri.parse("package:" + getPackageName())));
                    } catch (Exception e) {
                        startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                    }
                })
                .setNegativeButton("稍后", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(networkCheckRunnable);
        if (networkChangeDialog != null) {
            DialogHelper.safeDismiss(networkChangeDialog);
            networkChangeDialog = null;
        }
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
            DialogHelper.showQrCode(this, alistServer, url -> webView.loadUrl(url));
        }
    }

    @Override
    public void finish() {
        readyToShutdownService();
        super.finish();
    }

    /**
     * 遥控器「菜单」键在任何视图消费之前拦截下来，直接唤起选项面板
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_SETTINGS) {
                showPopupMenu(findViewById(R.id.btn_showPopupMenu));
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
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
            return;
        }
    }

    @Override
    public void showSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
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
        // 长按标题手动重连：网络异常时不用重启 App
        if (appInfoTextView != null) {
            appInfoTextView.setOnLongClickListener(v -> {
                showToast("正在重新检测网络");
                scheduleNetworkRecover();
                return true;
            });
        }
        webView = findViewById(R.id.webview_alist);
        // 页面自身有背景色，WebView 保持透明，避免加载/切换时露出白底（白框）
        webView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
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
                AppLog.e(TAG, "服务启动失败: " + e.getLocalizedMessage());
            }
        });
        serviceSwitch.setChecked(true);
    }

    /**
     * 焦点处理：只在电视/机顶盒上生效。
     * 之前对所有设备、所有视图无条件 setBackground(border / null)，
     * 会把按钮原本的背景（蓝色圆形刷新键、状态卡片描边）冲掉；改造后：
     * 用 foreground 叠加焦点环，保留原有 background。
     */
    private void initFocusSettings() {
        final boolean tv = isTvDevice();
        if (tv) {
            appInfoTextView.postDelayed(() -> appInfoTextView.requestFocus(), 1000);
        }
        List<View> views = AppUtil.getAllViews(this);
        views.addAll(AppUtil.getAllChildViews(popupMenuWindow.getContentView()));
        for (View view : views) {
            if (!tv || view instanceof WebView) {
                continue;
            }
            if (!view.isFocusable() && !view.isClickable()) {
                continue;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setForeground(getDrawable(R.drawable.focus_ring_overlay));
            }
        }
    }

    /**
     * 电视上给一次遥控器操作提示（只提示一次）
     */
    private void maybeShowTvHint() {
        if (!isTvDevice()) {
            return;
        }
        SharedDataHelper shared = SharedDataHelper.getInstance();
        if (shared.getBoolShareData(KEY_TV_HINT_SHOWN, false)) {
            return;
        }
        shared.putSharedData(KEY_TV_HINT_SHOWN, true);
        // 延后一点，避免和启动时的「初始登录信息」等 Toast 互相顶掉
        appInfoTextView.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                AppLog.d(TAG, "show tv key hint");
                showToast("方向键移动焦点 · 菜单键打开选项 · 返回键后退");
            }
        }, 15000);
    }

    private void checkPermissions() {
        // 只在首次启动自动申请一次。此前每次冷启动都走 XXPermissions，
        // 用户拒绝过「所有文件访问」时会被反复弹到系统设置页（真机上已确认），非常打扰。
        SharedDataHelper shared = SharedDataHelper.getInstance();
        if (shared.getBoolShareData(Constants.ANDROID_SHARED_DATA_KEY_PERMISSION_PROMPTED, false)) {
            return;
        }
        shared.putSharedData(Constants.ANDROID_SHARED_DATA_KEY_PERMISSION_PROMPTED, true);
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
        if (isTvDevice()) {
            // 电视/盒子没有触屏，让 WebView 能被遥控器方向键滚动
            webView.setFocusable(true);
            webView.setFocusableInTouchMode(true);
        }
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
                // WebView 内核过旧时提示一次，避免用户面对白屏/黑屏不知所措
                if (!webViewCompatWarned && webViewChromiumVersion > 0
                        && webViewChromiumVersion < MIN_SUPPORTED_CHROMIUM_VERSION
                        && url.startsWith("http")) {
                    webViewCompatWarned = true;
                    showWebViewCompatDialog();
                }
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
                // 默认所有网页都在 WebView 里打开：部分设备（电视、平板）没有浏览器。
                // 只有非 http 的 scheme（mailto:、intent:、market: 等）才交给系统处理。
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
        // 检测 WebView 内核版本，用于在页面无法渲染时给出明确提示
        webViewChromiumVersion = detectWebViewChromiumVersion();
    }

    /**
     * 检测 WebView 内核（Chromium）主版本号。
     * 通过解析 User-Agent 中的 "Chrome/x" 得到，无法解析时返回 -1。
     */
    private int detectWebViewChromiumVersion() {
        try {
            String ua = webView.getSettings().getUserAgentString();
            Matcher matcher = Pattern.compile("Chrome/(\\d+)").matcher(ua);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            AppLog.w(TAG, "检测 WebView 内核版本失败: " + e.getMessage());
        }
        return -1;
    }

    /**
     * WebView 内核过旧时弹窗提示：新版 OpenList 前端依赖较新的 Chromium 内核，
     * 旧内核会导致白屏/黑屏，但服务端本身正常，可改用外部浏览器访问。
     */
    private void showWebViewCompatDialog() {
        new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                .setTitle("WebView 内核版本过低")
                .setMessage(String.format(
                        "当前设备的 WebView 内核（Chrome %d）过旧，可能无法正常显示页面（白屏/黑屏）。\n\n" +
                                "AList 服务本身仍在正常运行，可尝试：\n" +
                                "1. 升级系统 WebView 组件；\n" +
                                "2. 使用外部浏览器访问服务地址。",
                        webViewChromiumVersion))
                .setPositiveButton("在浏览器打开", (d, w) -> openInExternalBrowser())
                .setNegativeButton("知道了", null)
                .show();
    }

    /**
     * 用系统浏览器打开服务的外部访问地址（局域网地址），供 WebView 无法渲染时兜底。
     * 设备上没有浏览器时（电视、精简平板）自动回落到应用内 WebView。
     */
    private void openInExternalBrowser() {
        if (alistServer == null || !alistServer.hasRunning()) {
            showToast("AList 服务未启动");
            return;
        }
        String address;
        try {
            address = alistServer.getExternalAddress();
        } catch (Exception e) {
            showToast("获取服务地址失败");
            return;
        }
        try {
            startActivity(Intent.parseUri(address, Intent.URI_INTENT_SCHEME));
        } catch (Exception e) {
            AppLog.w(TAG, "no external browser, fallback to webview: " + e.getLocalizedMessage());
            showToast("未找到浏览器，已在应用内打开");
            webView.loadUrl(address);
        }
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
                scheduleNetworkRecover();
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
                scheduleNetworkRecover();
            }

            @Override
            public void onLost(@NonNull Network network) {
                // 延迟检查，等网络接口完全拆除后再获取最终状态，避免读到过渡态 IPv6
                scheduleNetworkRecover();
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26+ — registerDefaultNetworkCallback 可用
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            // API 21-25 — 用 registerNetworkCallback 代替（监听所有网络作为回退方案）
            NetworkRequest request = new NetworkRequest.Builder().build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
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
                        if (address != null) {
                            // 只替换主机部分，保留用户当前浏览的路径（网络切换/服务重启不再跳回首页）
                            swapWebViewHost(address);
                        }
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
    /** 上一次提示过的对外地址，避免重复 toast */
    private String lastShownAddress = null;

    /**
     * 网络自愈：切换 Wi-Fi / 热点 / 拔插网线后，自动把服务拉回可用状态。
     * <p>
     * 1) 服务已停（系统回收、切换时被杀）→ 直接重启
     * 2) 端口探活失败（绑定丢了）→ 重启
     * 3) 对外地址变了 → 刷新缓存、页面从旧地址跳到新地址、给一次轻提示
     */
    private void updateServerAddressIfNeeded() {
        if (alistServer == null || serviceSwitch == null) {
            return;
        }
        final boolean wantRunning = serviceSwitch.isChecked();
        if (alistServer.hasRunning()) {
            // Go 内核可能还在跑，但前台 Service / 通知栏被系统停掉了。
            // ACTION_STARTUP 是幂等的（内核已在运行就只刷新通知），这里补一次最省事。
            if (wantRunning) {
                ensureWrapperService();
            }
        }
        if (!alistServer.hasRunning()) {
            if (wantRunning) {
                AppLog.i(TAG, "网络变化：服务已停止，自动重启");
                showToast("网络环境变化，正在重新启动服务");
                restartService();
            }
            return;
        }
        final String oldAddress = alistServer.getCachedServerAddress();
        final String oldPrimaryIP = lastPrimaryIP;
        new Thread(() -> {
            final boolean alive = probeLocalServer();
            String address = null;
            try {
                address = alistServer.refreshServerAddress();
            } catch (Exception e) {
                AppLog.w(TAG, "updateServerAddressIfNeeded: 刷新地址失败 " + e.getLocalizedMessage());
            }
            try {
                lastPrimaryIP = alistServer.getPrimaryIP();
            } catch (Exception ignored) {
            }
            final String newAddress = address;
            runOnUiThread(() -> {
                if (!alive) {
                    AppLog.i(TAG, "网络变化：端口无响应，自动重启服务");
                    showToast("服务无响应，正在重启");
                    restartService();
                    return;
                }
                if (newAddress == null) {
                    return;
                }
                reloadWebViewIfStale(oldAddress, newAddress);
                boolean ipChanged = oldPrimaryIP != null && !oldPrimaryIP.equals(lastPrimaryIP);
                if (ipChanged && !newAddress.equals(lastShownAddress)) {
                    lastShownAddress = newAddress;
                    AppLog.i(TAG, "外部访问地址已更新为 " + newAddress);
                    showToast("访问地址已更新：" + newAddress);
                }
            });
        }, "network-recover").start();
    }

    /**
     * 网络事件去抖：一次切换会连发 available / capabilities / lost 多个回调，
     * 合并成 1.5s 后的一次自愈，避免重复重启服务或刷页面。
     */
    private void scheduleNetworkRecover() {
        handler.removeCallbacks(networkCheckRunnable);
        handler.postDelayed(networkCheckRunnable, 1500);
    }

    /**
     * 补挂前台 Service：内核还活着但通知栏没了时用。onStartCommand 内部会判断
     * 「已在运行就只刷新通知」，所以重复调用安全。
     */
    private void ensureWrapperService() {
        try {
            AppLog.i(TAG, "网络变化：确保前台服务在位");
            readyToStartService();
        } catch (RuntimeException e) {
            AppLog.w(TAG, "ensureWrapperService: " + e.getLocalizedMessage());
        }
    }

    /**
     * 本机端口探活：TCP 连一下当前配置的端口，判断服务是否还在监听
     */
    private boolean probeLocalServer() {
        try {
            String key = alistServer.isHttpsEnabled() ? "scheme.https_port" : "scheme.http_port";
            int port = Integer.parseInt(alistServer.getConfigValue(key).trim());
            java.net.Socket socket = new java.net.Socket();
            try {
                socket.connect(new java.net.InetSocketAddress("127.0.0.1", port), 800);
                return true;
            } finally {
                socket.close();
            }
        } catch (Exception e) {
            AppLog.w(TAG, "probeLocalServer: " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 把 WebView 当前页的主机部分换成新地址，路径/锚点保持不变。
     * 页面为空或停在本地说明页时才直接导航到新地址。
     */
    private void swapWebViewHost(String newAddress) {
        if (webView == null || TextUtils.isEmpty(newAddress)) {
            return;
        }
        String current = webView.getUrl();
        if (current == null || current.startsWith("about:") || current.startsWith("file:")) {
            webView.loadUrl(newAddress);
            return;
        }
        try {
            Uri uri = Uri.parse(current);
            String authority = uri.getAuthority();
            if (authority == null) {
                webView.loadUrl(newAddress);
                return;
            }
            String currentBase = uri.getScheme() + "://" + authority;
            if (currentBase.equals(newAddress)) {
                return;
            }
            String suffix = current.substring(Math.min(current.length(), currentBase.length()));
            AppLog.i(TAG, "页面主机切换 " + currentBase + " → " + newAddress + "，保留路径" + suffix);
            webView.loadUrl(newAddress + suffix);
        } catch (Exception e) {
            AppLog.w(TAG, "swapWebViewHost: " + e.getLocalizedMessage());
            webView.loadUrl(newAddress);
        }
    }

    /**
     * 页面还停在旧地址时，带着原来的路径一起切到新地址（不跳回首页）
     */
    private void reloadWebViewIfStale(String oldAddress, String newAddress) {
        if (webView == null || TextUtils.isEmpty(oldAddress) || TextUtils.isEmpty(newAddress)) {
            return;
        }
        String current = webView.getUrl();
        if (current == null || current.startsWith("file:")) {
            return;
        }
        if (oldAddress.equals(newAddress)) {
            return;
        }
        if (!current.startsWith(oldAddress)) {
            return;
        }
        String suffix = current.substring(oldAddress.length());
        AppLog.i(TAG, "页面地址已过期，切换到 " + newAddress + suffix);
        webView.loadUrl(newAddress + suffix);
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
            AppLog.e(TAG, "readyToStartService: 前台服务启动失败", e);
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
                AppLog.e(TAG, "restartService: " + e.getMessage());
            }
        }, 1500);
    }

    // ==================== OnMenuActionListener 实现 ====================

    @Override
    public void showQrCode(View view) {
        DialogHelper.showQrCode(this, alistServer, url -> webView.loadUrl(url));
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
                        AppLog.e(TAG, "oneClickLogin: ", e);
                    });
                }
            }).start();
        } catch (Exception e) {
            showToast("一键登录失败: " + e.getMessage());
            AppLog.e(TAG, "oneClickLogin: ", e);
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
        // 下载页优先在 App 内 WebView 打开（部分电视/平板没有浏览器）
        UpdateChecker.check(this, currentAppVersion, url -> webView.loadUrl(url));
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
            // TV 上默认焦点会落在菜单中间项，把顶部的「服务管理」组挤出屏幕，
            // 导致「远程访问」「服务日志」不可见也不可点。强制焦点回到第一项。
            View firstItem = popupMenuWindow.getContentView().findViewById(R.id.btn_showQrCode);
            if (firstItem != null) {
                firstItem.requestFocus();
            }
        }
    }

    // ==================== 工具方法 ====================

    private String getCurrentAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            AppLog.e(TAG, "getCurrentVersion: ", e);
        }
        return "unknown";
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
        if (bgAlpha >= 1.0f) {
            // 复位时必须清掉 dim，否则后续页面（如系统文件选择器）可能吃到残留的遮罩/焦点状态
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
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

    // ==================== TV 适配 ====================

    /**
     * 是否运行在电视 / 大屏设备上（含 Leanback 盒子和投影）
     */
    private boolean isTvDevice() {
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
                || pm.hasSystemFeature("android.hardware.type.television")) {
            return true;
        }
        int uiMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_TYPE_MASK;
        return uiMode == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION;
    }

    /**
     * 电视观看距离远，原生控件与网页字号各放大一档
     */
    private void applyTvTextScale() {
        try {
            if (appInfoTextView != null) {
                appInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        appInfoTextView.getTextSize() * 1.2f);
            }
            if (sslIndicator != null) {
                sslIndicator.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        sslIndicator.getTextSize() * 1.1f);
            }
            // WebView 内的 OpenList 页面整体放大，避免远距离看不清
            if (webView != null && webView.getSettings().getTextZoom() < 115) {
                webView.getSettings().setTextZoom(115);
            }
            if (popupMenuWindow != null) {
                scaleTextSizes(popupMenuWindow.getContentView(), 1.15f);
            }
        } catch (Throwable t) {
            AppLog.w(TAG, "applyTvTextScale: " + t);
        }
    }

    private void scaleTextSizes(View view, float factor) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getTextSize() * factor);
        } else if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                scaleTextSizes(group.getChildAt(i), factor);
            }
        }
    }

}
