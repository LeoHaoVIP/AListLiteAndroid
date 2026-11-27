package com.leohao.android.alistlite.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * AList 管理接口客户端（挂载管理用）。
 * <p>
 * 接口契约（已在 OpenList v4.2.10 上实测）：
 * · POST /api/auth/login      JSON {username,password} → data.token
 * · GET  /api/admin/storage/list               → data.content[]
 * · GET  /api/admin/storage/get?id=N           → data
 * · POST /api/admin/storage/enable?id=N        → 启用
 * · POST /api/admin/storage/disable?id=N       → 停用
 * · POST /api/admin/storage/delete?id=N        → 删除
 * · POST /api/admin/storage/update  JSON 完整对象 → 更新
 * <p>
 * 注意：enable/disable/delete 的 id 走 query，放 JSON body 会报
 * `strconv.Atoi: parsing "": invalid syntax`。
 *
 * @author LeoHao
 */
public class AlistAdminApi {

    private static final String TAG = "AlistAdminApi";
    private static final int TIMEOUT_MS = 8000;

    private final String baseUrl;
    private String token;

    public AlistAdminApi(String baseUrl) {
        String base = baseUrl == null ? "" : baseUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        this.baseUrl = base;
    }

    /**
     * 管理员登录，成功后内部持有 token
     */
    public void login(String username, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("username", TextUtils.isEmpty(username) ? "admin" : username);
        body.put("password", password == null ? "" : password);
        JSONObject resp = requestJson("POST", "/api/auth/login", body);
        checkOk(resp);
        JSONObject data = resp.optJSONObject("data");
        String t = data == null ? null : data.optString("token", null);
        if (TextUtils.isEmpty(t)) {
            throw new Exception("登录返回没有 token");
        }
        token = t;
    }

    public boolean hasToken() {
        return !TextUtils.isEmpty(token);
    }

    /**
     * 挂载列表
     */
    public List<JSONObject> listStorages() throws Exception {
        JSONObject resp = requestJson("GET", "/api/admin/storage/list", null);
        checkOk(resp);
        List<JSONObject> list = new ArrayList<>();
        JSONObject data = resp.optJSONObject("data");
        JSONArray arr = data == null ? null : data.optJSONArray("content");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.optJSONObject(i);
                if (item != null) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    /**
     * 单个挂载的完整对象（更新时要回传整份）
     */
    public JSONObject getStorage(long id) throws Exception {
        JSONObject resp = requestJson("GET", "/api/admin/storage/get?id=" + id, null);
        checkOk(resp);
        JSONObject data = resp.optJSONObject("data");
        if (data == null) {
            throw new Exception("未取到挂载信息");
        }
        return data;
    }

    public void setDisabled(long id, boolean disabled) throws Exception {
        String path = "/api/admin/storage/" + (disabled ? "disable" : "enable") + "?id=" + id;
        checkOk(requestJson("POST", path, null));
    }

    public void delete(long id) throws Exception {
        checkOk(requestJson("POST", "/api/admin/storage/delete?id=" + id, null));
    }

    /**
     * 读取 AList 设置（用于「显示云盘容量」开关）
     */
    public java.util.Map<String, String> getSettings() throws Exception {
        JSONObject resp = requestJson("GET", "/api/admin/setting/list", null);
        checkOk(resp);
        java.util.Map<String, String> map = new java.util.HashMap<>();
        org.json.JSONArray arr = resp.optJSONArray("data");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.optJSONObject(i);
                if (item != null) {
                    map.put(item.optString("key"), item.optString("value"));
                }
            }
        }
        return map;
    }

    /**
     * 批量保存设置；body 必须是数组：[{"key":"a","value":"b"}]
     * （传对象会报 json: cannot unmarshal object into Go value of type []model.SettingItem）
     */
    public void saveSettings(java.util.Map<String, String> values) throws Exception {
        org.json.JSONArray arr = new org.json.JSONArray();
        for (java.util.Map.Entry<String, String> e : values.entrySet()) {
            JSONObject item = new JSONObject();
            item.put("key", e.getKey());
            item.put("value", e.getValue());
            arr.put(item);
        }
        JSONObject resp = requestRaw("POST", "/api/admin/setting/save", arr.toString());
        checkOk(resp);
    }

    /**
     * 提交整份挂载对象完成更新
     */
    public void update(JSONObject storage) throws Exception {
        checkOk(requestJson("POST", "/api/admin/storage/update", storage));
    }

    // ===== 内部实现 =====

    private void checkOk(JSONObject resp) throws Exception {
        int code = resp.optInt("code", -1);
        if (code != 200) {
            String msg = resp.optString("message", "未知错误");
            throw new Exception(msg);
        }
    }

    private JSONObject requestJson(String method, String path, JSONObject body) throws Exception {
        return requestRaw(method, path, body == null ? null : body.toString());
    }

    /**
     * 原始 JSON 文本请求（保存设置时 body 是数组，不能走 JSONObject）
     */
    private JSONObject requestRaw(String method, String path, String bodyText) throws Exception {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new Exception("服务地址为空");
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(baseUrl + path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("Accept", "application/json");
            if (!TextUtils.isEmpty(token)) {
                conn.setRequestProperty("Authorization", token);
            }
            if (bodyText != null) {
                byte[] bytes = bodyText.getBytes("UTF-8");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setFixedLengthStreamingMode(bytes.length);
                OutputStream os = conn.getOutputStream();
                try {
                    os.write(bytes);
                } finally {
                    os.close();
                }
            }
            int status = conn.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    status >= 400 && conn.getErrorStream() != null
                            ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            String text = sb.toString();
            AppLog.d(TAG, method + " " + path + " → " + status);
            if (TextUtils.isEmpty(text)) {
                throw new Exception("服务返回为空（HTTP " + status + "）");
            }
            return new JSONObject(text);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 供调用方做 URL 编码（目前未用到，保留给后续新增挂载）
     */
    static String enc(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}
