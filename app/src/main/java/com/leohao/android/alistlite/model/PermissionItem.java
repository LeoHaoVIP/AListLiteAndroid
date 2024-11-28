package com.leohao.android.alistlite.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


/**
 * 单个权限内容
 *
 * @author LeoHao
 */
@AllArgsConstructor
@ToString
@Getter
public class PermissionItem {
    private String permissionName;
    private String permissionShortName;
    private String permissionDescription;
    private Boolean isGranted;
}
