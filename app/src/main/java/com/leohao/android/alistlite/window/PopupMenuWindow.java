package com.leohao.android.alistlite.window;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.leohao.android.alistlite.MainActivity;
import com.leohao.android.alistlite.R;

/**
 * 菜单栏弹窗窗口
 *
 * @author LeoHao
 */
public class PopupMenuWindow extends PopupWindow {
    public PopupMenuWindow(Context context) {
        super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //再次点击菜单时隐藏菜单
        setOutsideTouchable(true);
        setFocusable(true);
        View inflate = LayoutInflater.from(context).inflate(R.layout.popup_menu_view, null);
        setContentView(inflate);
        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.PopupMenuWindowStyle);
        //定义点击事件监听
        View popupView = getContentView();
        //远程访问（显示二维码）
        popupView.findViewById(R.id.btn_showQrCode).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().showQrCode(view);
        });
        //权限配置
        popupView.findViewById(R.id.btn_startPermissionCheckActivity).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().startPermissionCheckActivity(view);
        });
        //密码设置
        popupView.findViewById(R.id.btn_setAdminPassword).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().setAdminPassword(view);
        });
        //高级配置
        popupView.findViewById(R.id.btn_manageConfigData).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().manageConfigData(view);
        });
        //服务日志
        popupView.findViewById(R.id.btn_serviceLogs).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().showServiceLogs(view);
        });
        //检查更新
        popupView.findViewById(R.id.btn_checkUpdates).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().checkUpdates(view);
        });
        //关于 AList
        popupView.findViewById(R.id.btn_showSystemInfo).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().showSystemInfo(view);
        });
    }
}
