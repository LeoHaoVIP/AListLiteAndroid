package com.leohao.android.alistlite.window;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import com.leohao.android.alistlite.R;

/**
 * 菜单栏弹窗窗口
 *
 * @author LeoHao
 */
public class PopupMenuWindow extends PopupWindow {
    public PopupMenuWindow(Context context, OnMenuActionListener listener) {
        super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // 获取屏幕尺寸
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // 根据屏幕方向和尺寸自适应宽度
        boolean isLandscape = context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        int popupWidth;
        if (isLandscape) {
            // 横屏 / TV：两列布局，宽度取屏幕的 58%，最小 420dp
            popupWidth = Math.max((int) (screenWidth * 0.58f),
                    (int) (420 * metrics.density));
        } else {
            // 竖屏：占屏幕宽度的 40%，最小 200dp，最大 300dp
            int widthByScreen = (int) (screenWidth * 0.4f);
            int minWidth = (int) (200 * metrics.density);
            int maxWidth = (int) (300 * metrics.density);
            popupWidth = Math.max(minWidth, Math.min(widthByScreen, maxWidth));
        }
        setWidth(popupWidth);

        //再次点击菜单时隐藏菜单
        setOutsideTouchable(true);
        setFocusable(true);
        View inflate = LayoutInflater.from(context).inflate(R.layout.popup_menu_view, null);
        setContentView(inflate);

        // 测量内容高度，若超出屏幕 75% 则限制高度启用滚动
        inflate.measure(View.MeasureSpec.makeMeasureSpec(popupWidth, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int contentHeight = inflate.getMeasuredHeight();
        // 横屏两列布局给到 88%，竖屏保持 75%
        int maxHeight = (int) (screenHeight * (isLandscape ? 0.88f : 0.75f));
        if (contentHeight > maxHeight) {
            setHeight(maxHeight);
        }

        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.PopupMenuWindowStyle);
        //定义点击事件监听（菜单只保留高频项，低频项在「设置」二级页）
        View popupView = getContentView();
        //远程访问（显示二维码）
        popupView.findViewById(R.id.btn_showQrCode).setOnClickListener((view) -> {
            dismiss();
            listener.showQrCode(view);
        });
        //浏览器打开
        popupView.findViewById(R.id.btn_openInBrowser).setOnClickListener((view) -> {
            dismiss();
            listener.openInBrowser(view);
        });
        //服务日志
        popupView.findViewById(R.id.btn_serviceLogs).setOnClickListener((view) -> {
            dismiss();
            listener.showServiceLogs(view);
        });
        //一键登录
        popupView.findViewById(R.id.btn_oneClickLogin).setOnClickListener((view) -> {
            dismiss();
            listener.oneClickLogin(view);
        });
        //密码设置
        popupView.findViewById(R.id.btn_setAdminPassword).setOnClickListener((view) -> {
            dismiss();
            listener.setAdminPassword(view);
        });
        //HTTPS 设置
        popupView.findViewById(R.id.btn_toggleHttps).setOnClickListener((view) -> {
            dismiss();
            listener.toggleHttps(view);
        });
        //阿里云盘令牌（在 WebView 内打开）
        popupView.findViewById(R.id.btn_showAliTvTokenGetPage).setOnClickListener((view) -> {
            dismiss();
            listener.showAliTvTokenGetPage(view);
        });
        //检查更新
        popupView.findViewById(R.id.btn_checkUpdates).setOnClickListener((view) -> {
            dismiss();
            listener.checkUpdates(view);
        });
        //设置（权限、诊断、保活、导入导出、高级配置）
        popupView.findViewById(R.id.btn_settings).setOnClickListener((view) -> {
            dismiss();
            listener.showSettings(view);
        });
        //关于 AList
        popupView.findViewById(R.id.btn_showSystemInfo).setOnClickListener((view) -> {
            dismiss();
            listener.showSystemInfo(view);
        });
    }
}
