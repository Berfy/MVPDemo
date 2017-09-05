-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontwarn android.support.**
-dontwarn android.support.v4.**
-dontwarn android.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-dontwarn com.sina.**

-dontwarn com.umeng.fb.**
-keep class com.umeng.fb.** { *; }

-dontwarn com.ut.device.**
-dontwarn com.ta.utdid2.**
-dontwarn com.alipay.**
-dontwarn org.json.alipay.**


#android6.0权限处理
-dontwarn com.zhy.m.**
-keep class com.zhy.m.** {*;}
-keep interface com.zhy.m.** { *; }
-keep class **$$PermissionProxy { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.FragmentActivity

-keep class com.ut.device.**{*;}
-keep class com.ta.utdid2.**{*;}
-keep class com.alipay.**{*;}
-keep class org.json.alipay.**{*;}

#share sdk
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class com.tencent.mm.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

-keep class com.umeng.*.** {*; }
-keep class android.support.v4.** {*;}
-keep class com.google.*.*.** { *; }


-dontwarn com.baidu.**
-keep class com.baidu.** { *; }

#个推推送
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}



-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keepclasseswithmembers class * {
    native <methods>;
}
-keep class **.R$* {*;}
-keep class **.R{*;}

-keepclasseswithmembers  class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers  class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


-keepattributes Signature, *Annotation*
#bugtag混淆配置
  -keepattributes LineNumberTable,SourceFile
  -keep class com.bugtags.library.** {*;}
  -dontwarn com.bugtags.library.**
  -keep class io.bugtags.** {*;}
  -dontwarn io.bugtags.**
  -dontwarn org.apache.http.**
  -dontwarn android.net.http.AndroidHttpClient
 # End Bugtags

#友盟推送混淆配置
-dontwarn com.ut.mini.**
-dontwarn okio.**
-dontwarn com.xiaomi.**
-dontwarn com.squareup.wire.**
-dontwarn android.support.v4.**

-keepattributes *Annotation*

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class okio.** {*;}
-keep class com.squareup.wire.** {*;}

-keep class com.umeng.message.protobuffer.* {
	 public <fields>;
         public <methods>;
}

-keep class com.umeng.message.* {
	 public <fields>;
         public <methods>;
}

-keep class org.android.agoo.impl.* {
	 public <fields>;
         public <methods>;
}

-keep class org.android.agoo.service.* {*;}

-keep class org.android.spdy.**{*;}

-keep public class **.R$*{
    public static final int *;
}
#如果compileSdkVersion为23，请添加以下混淆代码
-dontwarn org.apache.http.**
-dontwarn android.webkit.**
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }

#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

##################################Retrofit2.0+OkHttp3+RxAndroid#############
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# ssl
-dontwarn javax.net.ssl.**
-keep class javax.net.ssl.** { *; }
# OkHttp3
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

################################# Frodo ##################################
-dontwarn org.jetbrains.annotations.**
-keep class org.jetbrains.annotations.**{*;}
