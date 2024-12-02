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
        //更新日志
        popupView.findViewById(R.id.btn_showReleaseLog).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().showReleaseLog(view);
        });
        //高级配置
        popupView.findViewById(R.id.btn_manageConfigData).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().manageConfigData(view);
        });
        //关于 AList
        popupView.findViewById(R.id.btn_showSystemInfo).setOnClickListener((view) -> {
            dismiss();
            MainActivity.getInstance().showSystemInfo(view);
        });
    }
}
