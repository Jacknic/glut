# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\ADT\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#保持所有
#-keep class *.** { *; }

-dontwarn okio.**
#-keep class android.** { *; }
#-keep class com.android.support.** { *; }


#保持bean以免注解被混淆
-keep class com.jacknic.glut.bean.** { *; }
-keep class com.jacknic.glut.model.dao.** { *; }
-keep class com.jacknic.glut.model.entity.** { *; }

#第三方jar包不混淆
-keep class com.tencent.** { *; }
-keep class com.lzy.okgo.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class com.alibaba.fastjson.** { *; }
-keep class org.jsoup.** { *; }
-keep class me.gujun.** { *; }


### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**

