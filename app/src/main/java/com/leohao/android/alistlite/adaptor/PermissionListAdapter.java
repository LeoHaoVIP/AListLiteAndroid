package com.leohao.android.alistlite.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.leohao.android.alistlite.R;
import com.leohao.android.alistlite.model.PermissionItem;

import java.util.List;

/**
 * @author LeoHao
 */
public class PermissionListAdapter extends BaseAdapter {

    private final Context context;
    /**
     * 权限列表
     */
    private final List<PermissionItem> permissionList;

    public PermissionListAdapter(Context context, List<PermissionItem> list) {
        this.context = context;
        this.permissionList = list;
    }

    @Override
    public int getCount() {
        return permissionList.size();
    }

    @Override
    public Object getItem(int position) {
        return permissionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.permission_item_view, null);
            holder.permissionShortNameTextView = convertView.findViewById(R.id.tv_permission_short_name);
            holder.permissionStatusTextView = convertView.findViewById(R.id.tv_permission_status);
            holder.permissionDescriptionTextView = convertView.findViewById(R.id.tv_permission_description);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PermissionItem item = permissionList.get(position);
        //控件赋值
        holder.permissionShortNameTextView.setText(item.getPermissionShortName());
        //根据是否授权分别显示
        String statusText = item.getIsGranted() ? "已设置" : "未设置";
        String statusColor = item.getIsGranted() ? "#FF444444" : "#FFCC00";
        holder.permissionStatusTextView.setText(statusText);
        holder.permissionStatusTextView.setTextColor(Color.parseColor(statusColor));
        holder.permissionDescriptionTextView.setText(item.getPermissionDescription());
        return convertView;
    }

    private static class ViewHolder {
        TextView permissionShortNameTextView;
        TextView permissionStatusTextView;
        TextView permissionDescriptionTextView;
    }
}
