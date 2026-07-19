package com.leohao.android.alistlite.window;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import cn.hutool.json.JSONUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.leohao.android.alistlite.R;
import com.leohao.android.alistlite.model.Alist;
import com.leohao.android.alistlite.util.ClipBoardHelper;
import com.leohao.android.alistlite.util.Constants;
import com.yuyh.jsonviewer.library.JsonRecyclerView;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 弹框辅助类：二维码、密码、HTTPS、配置编辑、日志查看
 *
 * @author LeoHao
 */
public class DialogHelper {
    private static final String TAG = "DialogHelper";

    /**
     * 显示远程访问二维码弹框
     */
    public static void showQrCode(Activity activity, Alist alistServer) {
        if (!alistServer.hasRunning()) {
            Toast.makeText(activity, "AList 服务未启动", Toast.LENGTH_SHORT).show();
            return;
        }
        LinkedHashMap<String, String> ipMap = alistServer.getLocalAddresses();
        boolean isHttps = alistServer.isHttpsEnabled();
        String portStr;
        try {
            portStr = alistServer.getConfigValue(isHttps ? "scheme.https_port" : "scheme.http_port");
        } catch (IOException e) {
            Log.e(TAG, "showQrCode: 读取端口配置失败", e);
            Toast.makeText(activity, "读取服务配置失败", Toast.LENGTH_SHORT).show();
            return;
        }
        int port = Integer.parseInt(portStr);
        List<String> allAddresses = new ArrayList<>();
        List<String> allLabels = new ArrayList<>();
        for (Map.Entry<String, String> entry : ipMap.entrySet()) {
            allAddresses.add(Alist.formatServerUrl(entry.getKey(), port, isHttps));
            allLabels.add(entry.getValue());
        }
        // 无外部网络时提示用户
        if (allAddresses.isEmpty()) {
            Toast.makeText(activity, "当前无可用网络，无法远程访问", Toast.LENGTH_SHORT).show();
            return;
        }
        final AtomicInteger currentIndex = new AtomicInteger(0);
        final int totalCount = allAddresses.size();

        ImageView qrImageView = new ImageView(activity);
        qrImageView.setAdjustViewBounds(true);
        qrImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        qrImageView.setOnClickListener(v -> openUrl(activity, allAddresses.get(currentIndex.get())));

        TextView currentIpText = new TextView(activity);
        currentIpText.setGravity(Gravity.CENTER);
        currentIpText.setTextSize(14);
        currentIpText.setPadding(0, 10, 0, 0);

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity, R.style.IOSAlertDialog);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.setTitle("远程访问");

        final Runnable refreshView = () -> {
            int idx = currentIndex.get();
            String addr = allAddresses.get(idx);
            String ipType = addr.contains("://[") ? "IPv6" : "IPv4";
            qrImageView.setImageBitmap(generateQr(addr, 500));
            currentIpText.setText(String.format(Locale.CHINA, "(%d/%d) %s", idx + 1, totalCount, addr));
            alertDialog.setMessage(String.format("提示：请确保在同一网络环境内操作\r\n\r\n当前网卡 %s（%s），点击按钮可切换", allLabels.get(idx), ipType));
        };
        refreshView.run();

        int btnSize = dp(activity, 36);
        int btnMargin = dp(activity, 8);
        GradientDrawable circleBg = circleDrawable();
        GradientDrawable circleBgRight = circleDrawable();

        TextView leftButton = navButton(activity, "◀", circleBg);
        leftButton.setOnClickListener(v -> {
            currentIndex.set((currentIndex.get() - 1 + totalCount) % totalCount);
            refreshView.run();
        });

        TextView rightButton = navButton(activity, "▶", circleBgRight);
        rightButton.setOnClickListener(v -> {
            currentIndex.set((currentIndex.get() + 1) % totalCount);
            refreshView.run();
        });

        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setShape(GradientDrawable.RECTANGLE);
        pillBg.setCornerRadius(dp(activity, 16));
        pillBg.setColor(0xCC000000);
        TextView copyButton = new TextView(activity);
        copyButton.setText("复制地址");
        copyButton.setTextSize(12);
        copyButton.setTextColor(Color.WHITE);
        copyButton.setGravity(Gravity.CENTER);
        copyButton.setIncludeFontPadding(false);
        int padH = dp(activity, 12), padV = dp(activity, 6);
        copyButton.setPadding(padH, padV, padH, padV);
        copyButton.setBackground(pillBg);
        copyButton.setOnClickListener(v -> {
            ClipBoardHelper.getInstance().copyText(allAddresses.get(currentIndex.get()));
            Toast.makeText(activity, "地址已复制", Toast.LENGTH_SHORT).show();
        });

        FrameLayout qrContainer = new FrameLayout(activity);
        FrameLayout.LayoutParams qrParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        qrParams.gravity = Gravity.CENTER;
        qrContainer.addView(qrImageView, qrParams);

        FrameLayout buttonRow = new FrameLayout(activity);
        buttonRow.setPadding(0, dp(activity, 10), 0, dp(activity, 10));
        FrameLayout.LayoutParams leftBtnParams = new FrameLayout.LayoutParams(btnSize, btnSize);
        leftBtnParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        leftBtnParams.setMargins(btnMargin, 0, 0, 0);
        buttonRow.addView(leftButton, leftBtnParams);
        FrameLayout.LayoutParams copyBtnParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        copyBtnParams.gravity = Gravity.CENTER;
        buttonRow.addView(copyButton, copyBtnParams);
        FrameLayout.LayoutParams rightBtnParams = new FrameLayout.LayoutParams(btnSize, btnSize);
        rightBtnParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        rightBtnParams.setMargins(0, 0, btnMargin, 0);
        buttonRow.addView(rightButton, rightBtnParams);

        LinearLayout mainLayout = new LinearLayout(activity);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        int pad = dp(activity, 5);
        mainLayout.setPadding(pad, pad, pad, pad);
        mainLayout.addView(qrContainer);
        mainLayout.addView(currentIpText);
        mainLayout.addView(buttonRow);

        alertDialog.setView(mainLayout);
        alertDialog.show();
    }

    /**
     * 设置管理员密码弹框
     */
    public static void showSetPassword(Activity activity, Alist alistServer) {
        final EditText editText = new EditText(activity);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editText.setSingleLine();
        editText.setHint("请输入密码");
        FrameLayout inputWrapper = new FrameLayout(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginH = (int) (20 * activity.getResources().getDisplayMetrics().density);
        params.setMargins(marginH, 0, marginH, 0);
        editText.setLayoutParams(params);
        inputWrapper.addView(editText);
        new AlertDialog.Builder(activity, R.style.IOSAlertDialog)
                .setTitle("设置管理员密码")
                .setView(inputWrapper)
                .setCancelable(true)
                .setPositiveButton("确定", (d, w) -> {
                    String pwd = editText.getText().toString().trim();
                    if (pwd.isEmpty()) {
                        Toast.makeText(activity, "管理员密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        alistServer.setAdminPassword(pwd);
                        String adminUsername = alistServer.getAdminUser();
                        Toast.makeText(activity, String.format("管理员密码已更新：%s | %s", adminUsername, pwd), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(activity, "管理员密码设置失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "setAdminPassword: ", e);
                    }
                })
                .show();
    }

    /**
     * HTTPS 开关弹框
     */
    public static void showToggleHttps(Activity activity, Alist alistServer, Runnable onRestart) {
        if (alistServer.isHttpsEnabled()) {
            new AlertDialog.Builder(activity, R.style.IOSAlertDialog)
                    .setTitle("关闭 HTTPS")
                    .setMessage("关闭后将使用 HTTP 协议访问，是否确认？")
                    .setPositiveButton("确定关闭", (d, w) -> {
                        try {
                            alistServer.disableHttps();
                            onRestart.run();
                        } catch (IOException e) {
                            Toast.makeText(activity, "操作失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            final EditText portInput = new EditText(activity);
            portInput.setHint("5245");
            portInput.setSingleLine();
            FrameLayout inputWrapper = new FrameLayout(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int marginH = (int) (20 * activity.getResources().getDisplayMetrics().density);
            params.setMargins(marginH, 0, marginH, 0);
            portInput.setLayoutParams(params);
            inputWrapper.addView(portInput);
            AlertDialog enableDialog = new AlertDialog.Builder(activity, R.style.IOSAlertDialog)
                    .setTitle("启用 HTTPS")
                    .setMessage("将生成自签名证书，浏览器访问时会提示不安全，请手动信任。\n请输入 HTTPS 端口：")
                    .setView(inputWrapper)
                    .setPositiveButton("启用", null)
                    .setNegativeButton("取消", null)
                    .create();
            enableDialog.show();
            enableDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String portStr = portInput.getText().toString().trim();
                int port = portStr.isEmpty() ? 5245 : Integer.parseInt(portStr);
                if (!Alist.isPortAvailable(port)) {
                    Toast.makeText(activity, "端口 " + port + " 已被占用，请更换", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    alistServer.enableHttps(port);
                    enableDialog.dismiss();
                    onRestart.run();
                } catch (Exception e) {
                    Toast.makeText(activity, "操作失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "toggleHttps enable: " + e.getMessage());
                }
            });
        }
    }

    /**
     * AList 配置查看/编辑弹框
     */
    public static void showConfigEditor(Activity activity) {
        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.IOSAlertDialog).create();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogView = inflater.inflate(R.layout.config_view, null);
        JsonRecyclerView jsonView = dialogView.findViewById(R.id.json_view_config);
        ImageButton editButton = dialogView.findViewById(R.id.btn_edit_config);
        EditText jsonEditText = dialogView.findViewById(R.id.edit_text_config);
        jsonView.setTextSize(14);
        String dataPath = activity.getExternalFilesDir("data").getAbsolutePath();
        String configPath = String.format("%s%s%s", dataPath, File.separator, Constants.ALIST_CONFIG_FILENAME);
        String configJsonData;
        File configFile = new File(configPath);
        try {
            configJsonData = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            configJsonData = Constants.ERROR_MSG_CONFIG_DATA_READ.replace("MSG", Objects.requireNonNull(e.getLocalizedMessage()));
            editButton.setVisibility(View.INVISIBLE);
        }
        jsonView.bindJson(configJsonData);
        dialog.setView(dialogView);
        dialog.show();
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        if (width < height) {
            dialog.getWindow().setLayout(width - 50, height * 2 / 5);
        } else {
            dialog.getWindow().setLayout(width * 5 / 6, height - 200);
        }
        AtomicBoolean isEditing = new AtomicBoolean(false);
        String finalConfigJsonData = configJsonData;
        editButton.setOnClickListener(v -> {
            if (isEditing.get()) {
                boolean isJsonLegal = true;
                try {
                    JSONUtil.parseObj(jsonEditText.getText());
                } catch (Exception ignored) {
                    isJsonLegal = false;
                }
                if (!isJsonLegal) {
                    Toast.makeText(activity, "配置文件不是合法的JSON文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    FileUtils.write(configFile, jsonEditText.getText());
                    Toast.makeText(activity, "重启服务以应用新配置", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(activity, Constants.ERROR_MSG_CONFIG_DATA_WRITE, Toast.LENGTH_SHORT).show();
                }
                isEditing.set(false);
                jsonView.setVisibility(View.VISIBLE);
                jsonEditText.setVisibility(View.INVISIBLE);
                editButton.setImageResource(R.drawable.edit);
            } else {
                Toast.makeText(activity, "错误配置可能导致服务无法启动，请谨慎修改！", Toast.LENGTH_SHORT).show();
                isEditing.set(true);
                jsonEditText.setText(finalConfigJsonData);
                jsonView.setVisibility(View.INVISIBLE);
                jsonEditText.setVisibility(View.VISIBLE);
                editButton.setImageResource(R.drawable.save);
            }
        });
    }

    /**
     * 服务日志查看弹框
     */
    public static void showLogViewer(Activity activity) {
        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.IOSAlertDialog).create();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogView = inflater.inflate(R.layout.service_logs_view, null);
        TextView textView = dialogView.findViewById(R.id.tv_service_logs);
        ScrollView scrollView = dialogView.findViewById(R.id.tv_logs_scroll_view);
        synchronized (Alist.ALIST_LOGS) {
            textView.setText(Alist.ALIST_LOGS.toString());
        }
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        final AtomicBoolean running = new AtomicBoolean(true);
        new Thread(() -> {
            while (running.get()) {
                activity.runOnUiThread(() -> {
                    synchronized (Alist.ALIST_LOGS) {
                        String logs = Alist.ALIST_LOGS.toString();
                        textView.setText(logs);
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
        dialog.setOnDismissListener(d -> running.set(false));
        dialog.setView(dialogView);
        dialog.show();
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        if (width < height) {
            dialog.getWindow().setLayout(width - 50, height * 2 / 5);
        } else {
            dialog.getWindow().setLayout(width * 5 / 6, height - 200);
        }
    }

    // ===== 内部工具方法 =====

    private static Bitmap generateQr(String content, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            int width = bitMatrix.getWidth(), height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    pixels[y * width + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            Log.e(TAG, "generateQr: " + e.getMessage());
            return null;
        }
    }

    private static GradientDrawable circleDrawable() {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(0x99000000);
        return d;
    }

    private static TextView navButton(Activity activity, String text, GradientDrawable bg) {
        TextView tv = new TextView(activity);
        tv.setText(text);
        tv.setTextSize(12);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        tv.setIncludeFontPadding(false);
        tv.setBackground(bg);
        return tv;
    }

    private static int dp(Activity activity, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics());
    }

    private static void openUrl(Activity activity, String url) {
        try {
            activity.startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));
        } catch (Exception ignored) {
        }
    }
}
