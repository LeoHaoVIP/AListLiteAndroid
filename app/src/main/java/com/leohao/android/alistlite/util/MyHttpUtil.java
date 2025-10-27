package com.leohao.android.alistlite.util;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 手动解析 contentDisposition 获取文件名
     *
     * @param contentDisposition contentDisposition
     * @return 文件名
     */
    public static String guessFileName(String contentDisposition) {
        if (TextUtils.isEmpty(contentDisposition)) {
            return "file";
        }
        // 优先匹配 filename*=utf-8''xxx 格式
        Pattern utf8Pattern = Pattern.compile("filename\\*=utf-8''([^;]+)");
        Matcher utf8Matcher = utf8Pattern.matcher(contentDisposition);
        if (utf8Matcher.find()) {
            String fileName = utf8Matcher.group(1);
            // 解码URL编码的字符（如空格可能被编码为%20）
            try {
                return URLDecoder.decode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return fileName;
            }
        }
        // 再匹配 filename="xxx" 或 filename=xxx 格式
        Pattern normalPattern = Pattern.compile("filename=\"?([^\"]+)\"?");
        Matcher normalMatcher = normalPattern.matcher(contentDisposition);
        if (normalMatcher.find()) {
            return normalMatcher.group(1);
        }
        return "file";
    }

    /**
     * 根据 MIME 类型获取文件扩展名
     *
     * @param mimeType MIME 类型
     * @return 文件扩展名
     */
    public static String getFileExtension(String mimeType) {
        if (mimeType == null) return ".bin";
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        return extension != null ? "." + extension : ".bin";
    }

    /**
     * 清理文件名中的非法字符（避免保存失败）
     *
     * @param fileName 文件名
     * @return 文件名
     */
    private String sanitizeFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "download_file";
        }
        // 替换Windows和Linux中的非法文件字符
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
