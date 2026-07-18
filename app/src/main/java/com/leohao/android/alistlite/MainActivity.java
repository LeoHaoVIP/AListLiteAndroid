package com.leohao.android.alistlite;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.service.quicksettings.TileService;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kyleduo.switchbutton.SwitchButton;
import com.leohao.android.alistlite.interfaces.DownloadBlobFileJsInterface;
import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.service.AlistService;
import com.leohao.android.alistlite.service.AlistTileService;
import com.leohao.android.alistlite.util.AppUtil;
import com.leohao.android.alistlite.util.ClipBoardHelper;
import com.leohao.android.alistlite.util.Constants;
import com.leohao.android.alistlite.util.MyHttpUtil;
import com.leohao.android.alistlite.window.PopupMenuWindow;
import com.yuyh.jsonviewer.library.JsonRecyclerView;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LeoHao
 */
public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private static final String TAG = "MainActivity";
    /**
     * 广播定时发送定时器（用于实时更新服务磁贴状态）
     */
    private ScheduledExecutorService broadcastScheduler = null;
    private String currentAppVersion;
    private String currentAlistVersion;
    public ActionBar actionBar = null;
    public WebView webView = null;
    public TextView runningInfoTextView = null;
    public SwitchButton serviceSwitch = null;
    public String serverAddress = Constants.URL_ABOUT_BLANK;
    private Alist alistServer;
    public TextView appInfoTextView;
    private TextView sslIndicator;
    private PopupMenuWindow popupMenuWindow;
    private final ClipBoardHelper clipBoardHelper = ClipBoardHelper.getInstance();
    /**
     * 文件上传回调变量
     */
    private ValueCallback<Uri[]> mFilePathCallback;
    private ValueCallback<Uri> mUploadMessage;
    private static final int FILE_CHOOSER_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        //获取 AListServer 对象
        alistServer = Alist.getInstance();
        //初始化控件
        initWidgets();
        //焦点设置
        initFocusSettings();
        //权限检查
        checkPermissions();
        //检查系统更新
        checkUpdates(null);
        //初始化广播发送定时器
        initBroadcastScheduler();
    }

    /**
     * 初始化广播发送定时器
     */
    private void initBroadcastScheduler() {
        //初始化广播定时发送定时器
        broadcastScheduler = Executors.newSingleThreadScheduledExecutor();
        //定时向 TileService 发送服务开启状态
        broadcastScheduler.scheduleWithFixedDelay(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //请求监听状态
                TileService.requestListeningState(this, new ComponentName(this, AlistTileService.class));
                //根据 AList 服务开启状态选择广播消息类型
                String actionName = (alistServer != null && alistServer.hasRunning()) ? AlistTileService.ACTION_TILE_ON : AlistTileService.ACTION_TILE_OFF;
                //更新磁贴开关状态
                Intent tileServiceIntent = new Intent(this, AlistTileService.class).setAction(actionName);
                LocalBroadcastManager.getInstance(this).sendBroadcast(tileServiceIntent);
            }
        }, 2, 3, TimeUnit.SECONDS);
    }

    /**
     * 初始化焦点设置
     */
    private void initFocusSettings() {
        //初始化焦点为主页按钮
        appInfoTextView.postDelayed(() -> {
            //初始时焦点设置为密码按钮
            appInfoTextView.requestFocus();
        }, 1000);
        //适配 TV 端操作，控件获取到焦点时显示边框
        List<View> views = AppUtil.getAllViews(this);
        views.addAll(AppUtil.getAllChildViews(popupMenuWindow.getContentView()));
        for (View view : views) {
            view.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    view.setBackgroundResource(R.drawable.background_border);
                } else {
                    view.setBackground(null);
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
                .permission(Permission.POST_NOTIFICATIONS).permission(Permission.MANAGE_EXTERNAL_STORAGE).permission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).request(new OnPermissionCallback() {
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
        // 恢复前台时同步开关状态（可能通过磁贴等方式在后台改变了服务状态）
        if (alistServer != null && serviceSwitch != null) {
            boolean isRunning = alistServer.hasRunning();
            if (serviceSwitch.isChecked() != isRunning) {
                serviceSwitch.setCheckedNoEvent(isRunning);
            }
        }
        updateSslIndicator();
    }

    /**
     * 更新标题栏 SSL 加密标识
     */
    public void updateSslIndicator() {
        if (sslIndicator != null && alistServer != null) {
            boolean show = alistServer.isHttpsEnabled() && alistServer.hasRunning();
            sslIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void readyToStartService() {
        //Service启动Intent
        Intent intent = new Intent(this, AlistService.class).setAction(AlistService.ACTION_STARTUP);
        //调用服务
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        } catch (RuntimeException e) {
            // Android 12+ 可能因后台启动限制导致前台服务启动失败
            // 回退开关状态并提示用户
            serviceSwitch.setCheckedNoEvent(false);
            showToast("服务启动失败，请检查系统权限设置或重启应用");
            Log.e(TAG, "readyToStartService: 前台服务启动失败", e);
            throw e;
        }
    }

    private void readyToShutdownService() {
        //Service关闭Intent
        Intent intent = new Intent(this, AlistService.class).setAction(AlistService.ACTION_SHUTDOWN);
        //调用服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    /**
     * 重启 AList 服务（用于配置变更后生效）
     */
    private void restartService() {
        showToast("正在重启服务");
        if (alistServer.hasRunning()) {
            readyToShutdownService();
        }
        // 等待 shutdown 完成后再启动
        new Handler(getMainLooper()).postDelayed(() -> {
            try {
                readyToStartService();
                updateSslIndicator();
            } catch (RuntimeException e) {
                showToast("服务重启失败: " + e.getMessage());
                Log.e(TAG, "restartService: " + e.getMessage());
            }
        }, 1500);
    }

    private void initWidgets() {
        // 设置标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        serviceSwitch = findViewById(R.id.switchButton);
        appInfoTextView = findViewById(R.id.tv_app_info);
        sslIndicator = findViewById(R.id.tv_ssl_indicator);
        runningInfoTextView = findViewById(R.id.tv_alist_status);
        webView = findViewById(R.id.webview_alist);
        //初始化菜单栏弹框
        popupMenuWindow = new PopupMenuWindow(this);
        popupMenuWindow.setOnDismissListener(() -> {
            backgroundAlpha(1.0f);
        });
        //初始化 webView 设定
        initWebview();
        //获取当前APP版本号
        currentAppVersion = getCurrentAppVersion();
        //获取基于的AList版本
        currentAlistVersion = getCurrentAlistVersion();
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
            } catch (RuntimeException e) {
                // readyToStartService 内部已将开关回退，此处不再处理
                Log.e(TAG, "服务启动失败: " + e.getLocalizedMessage());
            }
        });
        //默认开启服务
        serviceSwitch.setChecked(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebview() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.removeJavascriptInterface("searchBoxJavaBredge_");
        // 添加JavascriptInterface
        webView.addJavascriptInterface(new DownloadBlobFileJsInterface(this), "Android");
        webView.setWebChromeClient(new WebChromeClient() {
            private View mCustomView;
            private CustomViewCallback mCustomViewCallback;
            final FrameLayout videoContainer = findViewById(R.id.video_container);

            /**
             * 处理Android 5.0及以上的文件选择
             */
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // 确保之前的回调已处理
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                // 创建文件选择意图
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
                } catch (Exception e) {
                    mFilePathCallback = null;
                    showToast("无法打开文件选择器");
                    return false;
                }
                return true;
            }

            /**
             * 处理Android 3.0到4.4的文件选择
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_CHOOSER_REQUEST_CODE);
            }

            /**
             * 处理早期Android版本的文件选择
             */
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
                //隐藏标题栏
                actionBar.hide();
                // 隐藏状态栏
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                //切换至横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                webView.setVisibility(View.VISIBLE);
                if (mCustomView == null) {
                    return;
                }
                mCustomView.setVisibility(View.GONE);
                videoContainer.removeView(mCustomView);
                mCustomViewCallback.onCustomViewHidden();
                mCustomView = null;
                //显示标题栏
                actionBar.show();
                //显示状态栏
                getWindow().getDecorView().setSystemUiVisibility(0);
                //切换至竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                super.onHideCustomView();
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 拦截页面中的 blob 下载，直接捕获 blob 数据发送到 Android
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
                //JS 注入，更新版本信息
                if (url.equals(Constants.URL_LOCAL_ABOUT_ALIST_LITE) || url.equals(Constants.URL_LOCAL_RELEASE_LOG)) {
                    String versionInfo = String.format(Constants.VERSION_INFO, currentAppVersion, currentAlistVersion);
                    String jsCode = "document.getElementById('text_version').innerHTML='" + versionInfo + "';";
                    webView.evaluateJavascript("javascript:(function(){" + jsCode + "})();", null);
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // blob URL 放行，交由下载监听器处理
                if (url.startsWith("blob")) {
                    return false;
                }
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
        // 设置下载监听器以支持文件下载
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            // blob URL：调用预注入的 JS 辅助函数，通过 fetch 读取并回调 Android 下载
            if (url.startsWith("blob")) {
                webView.evaluateJavascript("__blobDownload('" + url + "')", null);
                return;
            }
            // 使用系统下载管理器
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimetype);
            // 获取文件名
            String fileName = MyHttpUtil.guessFileName(contentDisposition);
            // 从文件名中提取扩展名（如果有）
            String extensionFromName = MimeTypeMap.getFileExtensionFromUrl(fileName);
            if (!TextUtils.isEmpty(extensionFromName)) {
                // 文件名已包含扩展名，直接使用
                fileName = fileName.replace("." + extensionFromName, "") + "." + extensionFromName;
            } else {
                // 否则使用MIME类型生成的扩展名
                fileName += MyHttpUtil.getFileExtension(mimetype);
            }
            request.setTitle(fileName);
            // 显示下载通知
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // 设置下载路径
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            // 获取下载服务并开始下载
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "开始下载: " + fileName, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            // 处理Android 5.0及以上的回调
            if (mFilePathCallback != null) {
                Uri[] results = null;
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            }
            // 处理旧版本Android的回调
            if (mUploadMessage != null) {
                Uri result = (resultCode == Activity.RESULT_OK && data != null) ? data.getData() : null;
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    /**
     * 显示远程访问链接二维码
     */
    public void showQrCode(View view) {
        if (!alistServer.hasRunning()) {
            showToast("AList 服务未启动");
            return;
        }
        // 获取所有本地 IP 地址与网卡名称映射
        LinkedHashMap<String, String> ipMap = alistServer.getAllLocalIPs();
        // 从 serverAddress 提取端口和协议
        String protocol = serverAddress.startsWith("https") ? "https" : "http";
        String portStr = serverAddress.substring(serverAddress.lastIndexOf(":") + 1);
        int port = Integer.parseInt(portStr);
        boolean isHttps = "https".equals(protocol);
        // 构建所有完整地址列表和对应的网卡名称列表
        List<String> allAddresses = new ArrayList<>();
        List<String> allLabels = new ArrayList<>();
        for (Map.Entry<String, String> entry : ipMap.entrySet()) {
            String address = Alist.formatServerUrl(entry.getKey(), port, isHttps);
            allAddresses.add(address);
            allLabels.add(entry.getValue());
        }
        // 当前显示的地址索引
        final AtomicInteger currentIndex = new AtomicInteger(0);
        final int totalCount = allAddresses.size();

        // 二维码 ImageView
        final ImageView qrImageView = new ImageView(MainActivity.this);
        qrImageView.setAdjustViewBounds(true);
        qrImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        qrImageView.setOnClickListener(v -> openExternalUrl(allAddresses.get(currentIndex.get())));

        // 当前 IP 文字（需在按钮之前声明，供 lambda 引用）
        final TextView currentIpText = new TextView(MainActivity.this);
        currentIpText.setGravity(Gravity.CENTER);
        currentIpText.setTextSize(14);
        currentIpText.setPadding(0, 10, 0, 0);

        // 对话框（需在按钮之前创建，供 lambda 引用）
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.IOSAlertDialog);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.setTitle("远程访问");

        // 统一刷新视图的辅助方法
        final Runnable refreshView = () -> {
            int idx = currentIndex.get();
            String newAddress = allAddresses.get(idx);
            // IPv6 地址 URL 包含 "://["，据此判断协议类型
            String ipType = newAddress.contains("://[") ? "IPv6" : "IPv4";
            qrImageView.setImageBitmap(generateQrBitmap(newAddress, 500));
            currentIpText.setText(String.format("(%d/%d) %s", idx + 1, totalCount, newAddress));
            alertDialog.setMessage(String.format("提示：请确保在同一网络环境内操作\r\n\r\n当前网卡 %s（%s），点击按钮可切换", allLabels.get(idx), ipType));
        };

        // 初始化视图
        refreshView.run();

        // 圆形按钮尺寸和样式
        int btnSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
        int btnMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(0x99000000);

        // 左切换按钮（◀）
        TextView leftButton = new TextView(MainActivity.this);
        leftButton.setText("◀");
        leftButton.setTextSize(12);
        leftButton.setTextColor(0xFFFFFFFF);
        leftButton.setGravity(Gravity.CENTER);
        leftButton.setIncludeFontPadding(false);
        leftButton.setBackground(circleBg);
        leftButton.setOnClickListener(v -> {
            currentIndex.set((currentIndex.get() - 1 + totalCount) % totalCount);
            refreshView.run();
        });

        // 右切换按钮（▶），复用圆角样式需新建 Drawable 实例
        GradientDrawable circleBgRight = new GradientDrawable();
        circleBgRight.setShape(GradientDrawable.OVAL);
        circleBgRight.setColor(0x99000000);
        TextView rightButton = new TextView(MainActivity.this);
        rightButton.setText("▶");
        rightButton.setTextSize(12);
        rightButton.setTextColor(0xFFFFFFFF);
        rightButton.setGravity(Gravity.CENTER);
        rightButton.setIncludeFontPadding(false);
        rightButton.setBackground(circleBgRight);
        rightButton.setOnClickListener(v -> {
            currentIndex.set((currentIndex.get() + 1) % totalCount);
            refreshView.run();
        });

        // 浮动复制按钮（底部居中，圆角胶囊形）
        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setShape(GradientDrawable.RECTANGLE);
        pillBg.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        pillBg.setColor(0xCC000000);
        TextView copyButton = new TextView(MainActivity.this);
        copyButton.setText("复制地址");
        copyButton.setTextSize(12);
        copyButton.setTextColor(0xFFFFFFFF);
        copyButton.setGravity(Gravity.CENTER);
        copyButton.setIncludeFontPadding(false);
        int copyBtnPaddingH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        int copyBtnPaddingV = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
        copyButton.setPadding(copyBtnPaddingH, copyBtnPaddingV, copyBtnPaddingH, copyBtnPaddingV);
        copyButton.setBackground(pillBg);
        copyButton.setOnClickListener(v -> {
            clipBoardHelper.copyText(allAddresses.get(currentIndex.get()));
            showToast("地址已复制");
        });

        // 二维码容器（仅包裹二维码图片）
        FrameLayout qrContainer = new FrameLayout(MainActivity.this);
        FrameLayout.LayoutParams qrParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        qrParams.gravity = Gravity.CENTER;
        qrContainer.addView(qrImageView, qrParams);

        // 按钮行：◀ 靠左 | 复制地址 居中 | ▶ 靠右
        FrameLayout buttonRow = new FrameLayout(MainActivity.this);
        int btnRowPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        buttonRow.setPadding(0, btnRowPadding, 0, btnRowPadding);
        // 左按钮靠左
        FrameLayout.LayoutParams leftBtnParams = new FrameLayout.LayoutParams(btnSize, btnSize);
        leftBtnParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        leftBtnParams.setMargins(btnMargin, 0, 0, 0);
        buttonRow.addView(leftButton, leftBtnParams);
        // 复制按钮居中
        FrameLayout.LayoutParams copyBtnParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        copyBtnParams.gravity = Gravity.CENTER;
        buttonRow.addView(copyButton, copyBtnParams);
        // 右按钮靠右
        FrameLayout.LayoutParams rightBtnParams = new FrameLayout.LayoutParams(btnSize, btnSize);
        rightBtnParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        rightBtnParams.setMargins(0, 0, btnMargin, 0);
        buttonRow.addView(rightButton, rightBtnParams);

        // 垂直布局
        LinearLayout mainLayout = new LinearLayout(MainActivity.this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        mainLayout.setPadding(padding, padding, padding, padding);
        mainLayout.addView(qrContainer);
        mainLayout.addView(currentIpText);
        mainLayout.addView(buttonRow);

        alertDialog.setView(mainLayout);
        alertDialog.show();
    }

    /**
     * 将BitMatrix对象转换为Bitmap对象
     */
    /**
     * 使用 ZXing 生成二维码 Bitmap
     */
    private Bitmap generateQrBitmap(String content, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            return bitMatrixToBitmap(bitMatrix);
        } catch (WriterException e) {
            Log.e(TAG, "generateQrBitmap: " + e.getMessage());
            return null;
        }
    }

    private Bitmap bitMatrixToBitmap(BitMatrix bitMatrix) {
        final int width = bitMatrix.getWidth();
        final int height = bitMatrix.getHeight();
        final int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 显示系统信息
     */
    public void showSystemInfo(View view) {
        webView.loadUrl(Constants.URL_LOCAL_ABOUT_ALIST_LITE);
    }

    /**
     * 进入阿里云盘 TV 版 Token 获取页面
     */
    public void showAliTvTokenGetPage(View view) {
        webView.loadUrl("http://127.0.0.1:4015");
    }

    /**
     * 设定管理员密码
     */
    public void setAdminPassword(View view) {
        final EditText editText = new EditText(MainActivity.this);
        //设置密码不可见
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editText.setSingleLine();
        editText.setHint("请输入密码");
        // 包裹输入框，20dp 水平边距 → 约 80% 弹框宽度
        FrameLayout inputWrapper = new FrameLayout(MainActivity.this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        int marginH = (int) (20 * getResources().getDisplayMetrics().density);
        params.setMargins(marginH, 0, marginH, 0);
        editText.setLayoutParams(params);
        inputWrapper.addView(editText);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.IOSAlertDialog);
        dialog.setTitle("设置管理员密码");
        dialog.setView(inputWrapper);
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            try {
                //去除前后空格后的密码
                String pwd = editText.getText().toString().trim();
                if (!pwd.isEmpty()) {
                    alistServer.setAdminPassword(pwd);
                    String adminUsername = alistServer.getAdminUser();
                    showToast(String.format("管理员密码已更新：%s | %s", adminUsername, pwd), Toast.LENGTH_LONG);
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
     * HTTPS 开关设置
     */
    public void toggleHttps(View view) {
        boolean isHttpsEnabled = alistServer.isHttpsEnabled();
        if (isHttpsEnabled) {
            // 已启用 → 确认关闭
            new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                    .setTitle("关闭 HTTPS")
                    .setMessage("关闭后将使用 HTTP 协议访问，是否确认？")
                    .setPositiveButton("确定关闭", (d, w) -> {
                        try {
                            alistServer.disableHttps();
                            restartService();
                        } catch (IOException e) {
                            showToast("操作失败: " + e.getMessage());
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            // 未启用 → 输入端口并启用
            final EditText portInput = new EditText(this);
            portInput.setHint("5245");
            portInput.setSingleLine();
            // 包裹输入框，20dp 水平边距 → 约 80% 弹框宽度
            FrameLayout inputWrapper = new FrameLayout(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            int marginH = (int) (20 * getResources().getDisplayMetrics().density);
            params.setMargins(marginH, 0, marginH, 0);
            portInput.setLayoutParams(params);
            inputWrapper.addView(portInput);
            AlertDialog enableDialog = new AlertDialog.Builder(this, R.style.IOSAlertDialog)
                    .setTitle("启用 HTTPS")
                    .setMessage("将生成自签名证书，浏览器访问时会提示不安全，请手动信任。\n请输入 HTTPS 端口：")
                    .setView(inputWrapper)
                    .setPositiveButton("启用", null)
                    .setNegativeButton("取消", null)
                    .create();
            enableDialog.show();
            // 覆写按钮点击行为：校验通过才关闭对话框
            enableDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String portStr = portInput.getText().toString().trim();
                int port = portStr.isEmpty() ? 5245 : Integer.parseInt(portStr);
                if (!Alist.isPortAvailable(port)) {
                    showToast("端口 " + port + " 已被占用，请更换");
                    return;
                }
                try {
                    alistServer.enableHttps(port);
                    enableDialog.dismiss();
                    restartService();
                } catch (Exception e) {
                    showToast("操作失败: " + e.getMessage());
                    Log.e(TAG, "toggleHttps enable: " + e.getMessage());
                }
            });
        }
    }

    /**
     * 跳转到AList主页面
     */
    public void jumpToHomepage(View view) {
        if (alistServer.hasRunning()) {
            webView.loadUrl(serverAddress);
        } else {
            showToast("AList 服务未启动");
        }
    }

    /**
     * 管理(查看/修改) AList 配置文件
     */
    public void manageConfigData(View view) {
        AlertDialog configDataDialog = new AlertDialog.Builder(this, R.style.IOSAlertDialog).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.config_view, null);
        JsonRecyclerView jsonView = dialogView.findViewById(R.id.json_view_config);
        ImageButton editButton = dialogView.findViewById(R.id.btn_edit_config);
        EditText jsonEditText = dialogView.findViewById(R.id.edit_text_config);
        jsonView.setTextSize(14);
        //读取 AList 配置
        String dataPath = this.getExternalFilesDir("data").getAbsolutePath();
        String configPath = String.format("%s%s%s", dataPath, File.separator, Constants.ALIST_CONFIG_FILENAME);
        String configJsonData;
        File configFile = new File(configPath);
        try {
            //AList 配置数据
            configJsonData = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            configJsonData = Constants.ERROR_MSG_CONFIG_DATA_READ.replace("MSG", Objects.requireNonNull(e.getLocalizedMessage()));
            editButton.setVisibility(View.INVISIBLE);
        }
        //显示 AList 配置
        jsonView.bindJson(configJsonData);
        configDataDialog.setView(dialogView);
        configDataDialog.show();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        //窗口大小设置必须在show()之后
        if (width < height) {
            configDataDialog.getWindow().setLayout(width - 50, height * 2 / 5);
        } else {
            configDataDialog.getWindow().setLayout(width * 5 / 6, height - 200);
        }
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

    /**
     * 查看服务日志
     */
    public void showServiceLogs(View view) {
        AlertDialog configDataDialog = new AlertDialog.Builder(this, R.style.IOSAlertDialog).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.service_logs_view, null);
        TextView textView = dialogView.findViewById(R.id.tv_service_logs);
        ScrollView scrollView = dialogView.findViewById(R.id.tv_logs_scroll_view);
        //显示服务日志
        synchronized (Alist.ALIST_LOGS) {
            textView.setText(Alist.ALIST_LOGS.toString());
        }
        //滚动到底部最新日志
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        //日志实时刷新（弹窗关闭时自动停止）
        final AtomicBoolean logRefreshRunning = new AtomicBoolean(true);
        new Thread(() -> {
            while (logRefreshRunning.get()) {
                runOnUiThread(() -> {
                    synchronized (Alist.ALIST_LOGS) {
                        String logs = Alist.ALIST_LOGS.toString();
                        textView.setText(logs);
                        //日志更新时，滚动到底部最新日志
                        if (!logs.equals(textView.getText().toString())) {
                            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                        }
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
        configDataDialog.setOnDismissListener(d -> logRefreshRunning.set(false));
        configDataDialog.setView(dialogView);
        configDataDialog.show();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        //窗口大小设置必须在show()之后
        if (width < height) {
            configDataDialog.getWindow().setLayout(width - 50, height * 2 / 5);
        } else {
            configDataDialog.getWindow().setLayout(width * 5 / 6, height - 200);
        }
    }

    /**
     * 页面刷新
     *
     * @param view view
     */
    public void refreshWebPage(View view) {
        webView.reload();
    }

    /**
     * 检查版本更新
     *
     * @param view view
     */
    public void checkUpdates(View view) {
        new Thread(() -> {
            //获取最新release版本信息
            try {
                //捕捉HTTP请求异常
                String releaseInfo = null;
                try {
                    releaseInfo = MyHttpUtil.request(Constants.URL_RELEASE_LATEST, Method.GET);
                } catch (Throwable t) {
                    Looper.prepare();
                    showToast("无法获取更新: " + t.getLocalizedMessage());
                    Looper.loop();
                    Log.e(TAG, "checkUpdates: " + t.getLocalizedMessage());
                }
                JSONObject release = JSONUtil.parseObj(releaseInfo);
                if (!release.containsKey("tag_name")) {
                    Looper.prepare();
                    showToast("未发现新版本信息");
                    Looper.loop();
                    return;
                }
                //设备 CPU 支持的 ABI 名称
                String abiName = AppUtil.getAbiName();
                //若 ABI 名称不在支持的分包架构列表中，则下载完整的安装包
                if (!Constants.SUPPORTED_DOWNLOAD_ABI_NAMES.contains(abiName)) {
                    abiName = Constants.UNIVERSAL_ABI_NAME;
                }
                //最新版本号
                String latestVersion = release.getStr("tag_name").substring(1);
                //最新版本基于的 OpenList 版本
                String latestOnOpenListVersion = release.getStr("name").substring(15);
                //版本更新日志
                String updateJournal = String.format("\uD83D\uDD25 新版本基于 OpenList %s 构建\r\n\r\n%s", latestOnOpenListVersion, release.getStr("body"));
                //新版本APK下载地址（Github）
                String downloadLinkGitHub = (String) release.getByPath("assets[0].browser_download_url");
                //镜像加速地址
                String downloadLinkFast = String.format("%s/%s", Constants.QUICK_DOWNLOAD_ADDRESS_GH_PROXY_PREFIX, downloadLinkGitHub);
                //发现新版本
                if (AppUtil.compareVersion(latestVersion, currentAppVersion) > 0) {
                    Looper.prepare();
                    String dialogTitle = String.format("\uD83C\uDF89 AListLite %s 已发布", latestVersion);
                    //弹出更新下载确认
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.IOSAlertDialog);
                    dialog.setTitle(dialogTitle);
                    dialog.setMessage(updateJournal);
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("镜像加速下载", (dialog1, which) -> {
                        //跳转到浏览器下载
                        openExternalUrl(downloadLinkFast);
                    });
                    dialog.setNeutralButton("GitHub官网下载", (dialog2, which) -> {
                        //跳转到浏览器下载
                        openExternalUrl(downloadLinkGitHub);
                    });
                    dialog.setNegativeButton("取消", (dialog3, which) -> {
                    });
                    dialog.show();
                    Looper.loop();
                } else {
                    if (view != null) {
                        Looper.prepare();
                        showToast(String.format("当前已是最新版本（v%s）", currentAppVersion));
                        Looper.loop();
                    }
                }
            } catch (Exception e) {
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

    /**
     * 获取当前AList版本
     */
    private String getCurrentAlistVersion() {
        return Constants.OPENLIST_VERSION;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String msg, int duration) {
        Toast.makeText(getApplicationContext(), msg, duration).show();
    }

    @Override
    public void finish() {
        //关闭服务
        readyToShutdownService();
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //自定义返回键功能，实现webView的后退以及退出时保持后台运行而不是关闭app
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
        if (alistServer != null && alistServer.hasRunning()) {
            clipBoardHelper.copyText(this.serverAddress);
            showToast("AList 服务地址已复制");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭定时任务调度器
        if (broadcastScheduler != null) {
            broadcastScheduler.shutdownNow();
        }
    }

    /**
     * 打开权限检查配置页面
     */
    public void startPermissionCheckActivity(View view) {
        Intent intent = new Intent(MainActivity.this, PermissionActivity.class);
        startActivity(intent);
    }

    /**
     * 显示菜单弹窗
     */
    public void showPopupMenu(View view) {
        if (isActivityRunning()) {
            popupMenuWindow.showAsDropDown(view, 0, 50);
            backgroundAlpha(0.6f);
        }
    }

    /**
     * 修改背景透明度，实现变暗效果
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
        // 此方法用来设置浮动层，防止部分手机变暗无效
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * 打开外部链接
     *
     * @param url URL 链接
     */
    private void openExternalUrl(String url) {
        try {
            //跳转到浏览器下载
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            startActivity(intent);
        } catch (Exception e) {
            showToast("无法打开此外部链接");
        }
    }

    /**
     * 检查 Activity 是否正在运行
     */
    private boolean isActivityRunning() {
        return !isFinishing() && !isDestroyed();
    }
}
