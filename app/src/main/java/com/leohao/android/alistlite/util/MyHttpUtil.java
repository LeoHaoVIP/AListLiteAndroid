package com.leohao.android.alistlite.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

import java.util.Map;

/**
 * 网络请求工具类
 *
 * @author LeoHao
 */
public class MyHttpUtil {
    /**
     * 发起HTTP请求
     *
     * @param url    请求URL
     * @param method 请求方法
     * @return HTTP 响应结果
     */
    public static String request(String url, Method method) {
        return HttpUtil.createRequest(method, url).execute().body();
    }

    /**
     * 发起HTTP请求
     *
     * @param url    请求URL
     * @param method 请求方法
     * @return HTTP 响应结果
     */
    public static String request(String url, Map<String, String> headers, Method method) {
        return HttpUtil.createRequest(method, url).addHeaders(headers).execute().body();
    }

    /**
     * 发起HTTP请求
     *
     * @param url    请求URL
     * @param method 请求方法
     * @return HTTP 响应结果
     */
    public static String request(String url, Map<String, String> headers, Map<String, Object> form, Method method) {
        return HttpUtil.createRequest(method, url).addHeaders(headers).form(form).execute().body();
    }
}
