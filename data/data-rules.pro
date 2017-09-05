
################################# GSON反序列化 ##################################
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep class com.wlb.agent.core.data.**.entity.** { *; }
-keep class com.wlb.agent.core.data.**.response.** { *; }
