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
            // 横屏 / TV：占屏幕宽度的 40%，最小 300dp
            popupWidth = Math.max((int) (screenWidth * 0.4f),
                    (int) (300 * metrics.density));
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
        int maxHeight = (int) (screenHeight * 0.75f);
        if (contentHeight > maxHeight) {
            setHeight(maxHeight);
        }

        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.PopupMenuWindowStyle);
        //定义点击事件监听
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
        //权限配置
        popupView.findViewById(R.id.btn_startPermissionCheckActivity).setOnClickListener((view) -> {
            dismiss();
            listener.startPermissionCheckActivity(view);
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
        //高级配置
        popupView.findViewById(R.id.btn_manageConfigData).setOnClickListener((view) -> {
            dismiss();
            listener.manageConfigData(view);
        });
        //服务日志
        popupView.findViewById(R.id.btn_serviceLogs).setOnClickListener((view) -> {
            dismiss();
            listener.showServiceLogs(view);
        });
        //检查更新
        popupView.findViewById(R.id.btn_checkUpdates).setOnClickListener((view) -> {
            dismiss();
            listener.checkUpdates(view);
        });
        //进入阿里云盘 TV 版 Token 获取页面（该 Token 对于开通阿里云盘会员的用户暂不限速）
        popupView.findViewById(R.id.btn_showAliTvTokenGetPage).setOnClickListener((view) -> {
            dismiss();
            listener.showAliTvTokenGetPage(view);
        });
        //关于 AList
        popupView.findViewById(R.id.btn_showSystemInfo).setOnClickListener((view) -> {
            dismiss();
            listener.showSystemInfo(view);
        });
    }
}
