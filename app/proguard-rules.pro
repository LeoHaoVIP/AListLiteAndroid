# ===== 通用 =====
# 保留注解、泛型签名、行号（Bugly 还原堆栈需要）
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*,SourceFile,LineNumberTable

# ===== Bugly =====
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.** { *; }

# ===== gomobile 生成的原生桥接类（JNI 双向调用，不能混淆/裁剪） =====
-keep class go.** { *; }
-keep class alistlib.** { *; }
-keep class alitvlib.** { *; }

# ===== WebView JS 桥：JS 里按固定名字 "Android" 调用，成员不能混淆 =====
-keepclassmembers class com.leohao.android.alistlite.interfaces.DownloadBlobFileJsInterface {
    public *;
}

# ===== 反射 / 序列化 =====
# 配置读写走 hutool JSON 反射，模型字段名必须保留
-keep class com.leohao.android.alistlite.model.** { *; }
-keep class cn.hutool.** { *; }
-keep class com.jayway.jsonpath.** { *; }
-keep class com.yuyh.jsonviewer.** { *; }
-keep class com.google.zxing.** { *; }

# ===== 可忽略的告警 =====
-dontwarn cn.hutool.**
-dontwarn com.jayway.jsonpath.**
-dontwarn org.bouncycastle.**
-dontwarn javax.**
