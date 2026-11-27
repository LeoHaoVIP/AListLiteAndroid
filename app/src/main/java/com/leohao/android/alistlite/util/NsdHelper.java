package com.leohao.android.alistlite.util;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * mDNS（DNS-SD）封装：
 * · 把自己的 AList 服务注册到局域网，其它设备/工具能直接发现
 * · 反向发现同一网段内其它 AListLite / OpenList 实例
 *
 * NsdManager 的 resolveService 同一时刻只允许一个监听器，所以这里做了串行队列，
 * 否则并发解析会抛「listener already in use」。
 */
public class NsdHelper {

    private static final String TAG = "NsdHelper";

    /** 自定义服务类型：与 _http._tcp 区分开，避免和打印机之类的设备混在一起 */
    public static final String SERVICE_TYPE = "_alistlite._tcp";

    private static final java.util.List<NsdManager.RegistrationListener> registrationListeners
            = new java.util.ArrayList<>();
    private static NsdManager nsdManager;
    private static String registeredName;

    // ---------------- 注册 ----------------

    /**
     * 注册本机服务。服务名带机型，方便在别的设备上一眼认出是谁。
     * <p>
     * 同时注册两种类型：
     * · _alistlite._tcp —— 本 App 自己搜索用，避免和打印机、电视等设备混在一起
     * · _http（或 _https）._tcp —— 标准类型，Windows Bonjour / VLC / 各类 mDNS 工具都能看到
     */
    public static void register(Context context, int port, boolean https) {
        NsdManager manager = (NsdManager) context.getApplicationContext()
                .getSystemService(Context.NSD_SERVICE);
        if (manager == null) {
            AppLog.w(TAG, "NSD 服务不可用，跳过注册");
            return;
        }
        unregister(context);

        String name = "AListLite-" + Build.MODEL;
        // 服务名超过 63 字节会被系统拒绝，先截断
        if (name.length() > 40) {
            name = name.substring(0, 40);
        }
        nsdManager = manager;
        registeredName = name;

        registerOne(manager, name, SERVICE_TYPE, port);
        registerOne(manager, name, https ? "_https._tcp" : "_http._tcp", port);
    }

    private static void registerOne(NsdManager manager, String name, String type,
                                    final int port) {
        NsdServiceInfo info = new NsdServiceInfo();
        info.setServiceName(name);
        info.setServiceType(type);
        info.setPort(port);

        NsdManager.RegistrationListener listener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                registeredName = serviceInfo.getServiceName();
                // 部分 ROM（如 MIUI）回调里 getPort() 返回 0，用注册时的端口打日志
                AppLog.i(TAG, "mDNS 已注册: " + registeredName + " " + serviceInfo.getServiceType()
                        + " :" + port);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                AppLog.w(TAG, "mDNS 注册失败(" + serviceInfo.getServiceType() + "): " + errorCode);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                AppLog.i(TAG, "mDNS 已注销: " + serviceInfo.getServiceType());
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                AppLog.w(TAG, "mDNS 注销失败: " + errorCode);
            }
        };
        registrationListeners.add(listener);
        try {
            manager.registerService(info, NsdManager.PROTOCOL_DNS_SD, listener);
        } catch (Exception e) {
            AppLog.w(TAG, "mDNS 注册异常: " + e.getLocalizedMessage());
        }
    }

    public static void unregister(Context context) {
        if (nsdManager == null || registrationListeners.isEmpty()) {
            return;
        }
        for (NsdManager.RegistrationListener listener : registrationListeners) {
            try {
                nsdManager.unregisterService(listener);
            } catch (Exception e) {
                AppLog.w(TAG, "mDNS 注销异常: " + e.getLocalizedMessage());
            }
        }
        registrationListeners.clear();
    }

    public static String getRegisteredName() {
        return registeredName;
    }

    // ---------------- 发现 ----------------

    public interface DiscoveryListener {
        void onServiceAdded(Device device);

        void onDiscoveryError(String message);
    }

    public static class Device {
        public final String name;
        public final String host;
        public final int port;

        Device(String name, String host, int port) {
            this.name = name;
            this.host = host;
            this.port = port;
        }

        public String getUrl() {
            String h = host == null ? "" : host;
            // IPv6 需要方括号
            if (h.indexOf(':') >= 0 && !h.startsWith("[")) {
                h = "[" + h + "]";
            }
            return "http://" + h + ":" + port;
        }
    }

    /**
     * 一次性发现器：找到即回调，用完 stop()。
     */
    public static class Discovery {
        private final NsdManager manager;
        private final DiscoveryListener listener;
        private final Handler main = new Handler(Looper.getMainLooper());
        private final Deque<NsdServiceInfo> pending = new ArrayDeque<>();
        private final List<String> seen = new ArrayList<>();
        private NsdManager.DiscoveryListener discoveryListener;
        private NsdManager.ResolveListener activeResolveListener;
        private boolean resolving;

        public Discovery(Context context, DiscoveryListener listener) {
            this.manager = (NsdManager) context.getApplicationContext()
                    .getSystemService(Context.NSD_SERVICE);
            this.listener = listener;
        }

        public void start() {
            if (manager == null) {
                listener.onDiscoveryError("当前系统不支持局域网发现");
                return;
            }
            discoveryListener = new NsdManager.DiscoveryListener() {
                @Override
                public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                    listener.onDiscoveryError("搜索失败(" + errorCode + ")");
                }

                @Override
                public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                    AppLog.w(TAG, "停止搜索失败: " + errorCode);
                }

                @Override
                public void onDiscoveryStarted(String serviceType) {
                    AppLog.i(TAG, "开始搜索 " + serviceType);
                }

                @Override
                public void onDiscoveryStopped(String serviceType) {
                    AppLog.i(TAG, "搜索结束");
                }

                @Override
                public void onServiceFound(NsdServiceInfo serviceInfo) {
                    if (serviceInfo.getServiceType() == null
                            || !serviceInfo.getServiceType().startsWith(SERVICE_TYPE)) {
                        return;
                    }
                    // 自己注册的那台不用再列出来
                    if (serviceInfo.getServiceName() != null
                            && serviceInfo.getServiceName().equals(registeredName)) {
                        return;
                    }
                    pending.add(serviceInfo);
                    resolveNext();
                }

                @Override
                public void onServiceLost(NsdServiceInfo serviceInfo) {
                    // 列表简单处理：不主动移除，用户点刷新即可
                }
            };
            try {
                manager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
            } catch (Exception e) {
                listener.onDiscoveryError("搜索异常：" + e.getLocalizedMessage());
            }
        }

        private void resolveNext() {
            if (resolving || manager == null) {
                return;
            }
            final NsdServiceInfo next = pending.poll();
            if (next == null) {
                return;
            }
            resolving = true;
            activeResolveListener = new NsdManager.ResolveListener() {
                @Override
                public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                    AppLog.w(TAG, "解析失败: " + serviceInfo.getServiceName() + " " + errorCode);
                    finishOne();
                }

                @Override
                public void onServiceResolved(NsdServiceInfo serviceInfo) {
                    String host = null;
                    if (serviceInfo.getHost() != null) {
                        host = serviceInfo.getHost().getHostAddress();
                    }
                    final Device device = new Device(serviceInfo.getServiceName(), host,
                            serviceInfo.getPort());
                    // 同一台设备可能通过多个网卡/地址被解析多次，按服务名去重
                    String key = device.name;
                    if (!seen.contains(key)) {
                        seen.add(key);
                        main.post(() -> listener.onServiceAdded(device));
                    }
                    finishOne();
                }
            };
            try {
                manager.resolveService(next, activeResolveListener);
            } catch (Exception e) {
                AppLog.w(TAG, "解析异常: " + e.getLocalizedMessage());
                finishOne();
            }
        }

        private void finishOne() {
            resolving = false;
            activeResolveListener = null;
            main.post(this::resolveNext);
        }

        public void stop() {
            if (manager != null && discoveryListener != null) {
                try {
                    manager.stopServiceDiscovery(discoveryListener);
                } catch (Exception e) {
                    AppLog.w(TAG, "停止搜索异常: " + e.getLocalizedMessage());
                }
            }
            discoveryListener = null;
            pending.clear();
        }
    }
}
