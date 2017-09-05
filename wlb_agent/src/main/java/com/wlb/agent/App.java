package com.wlb.agent;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.android.util.LContext;
import com.android.util.ext.PhoneModelUtil;
import com.android.util.log.LogUtil;
import com.android.util.scheduler.task.ScheduleTaskManager;
import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.wlb.agent.common.receiver.PushMsgHandleService;
import com.wlb.agent.core.data.DataConfig;
import com.wlb.agent.core.data.agentservice.AppVersionChecker;
import com.wlb.common.CommonApp;
import com.wlb.common.service.CoreService;
import com.wlb.common.service.task.UpdateTask;

import java.lang.reflect.Method;

import common.share.PlatformConfig;
import component.update.AppDownloadClient;
import component.update.AppVersionConfiguration;

/**
 * 应用全局类
 *
 * @author 张全
 */
public class App extends CommonApp {
    private final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化友盟
        initUmeng();

        String mMainProcess = getPackageName();// 主进程
        String curProcessName = getCurProcessName();
        LogUtil.d("curProcessName=" + curProcessName + ",pid="
                + android.os.Process.myPid());

        if (!mMainProcess.equals(curProcessName)) {
            return;
        }

//        if (BuildConfig.DEBUG) {
//            // 针对线程的相关策略
//            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
////                    .detectDiskReads()
////                    .detectDiskWrites()
////                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .detectAll()
//                    .penaltyLog()
//                    .build();
//            StrictMode.setThreadPolicy(threadPolicy);
//
//            // 针对VM的相关策略
//            StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
////                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build();
//            StrictMode.setVmPolicy(vmPolicy);
//        }

        //内存泄露检测工具
        if (BuildConfig.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                return;
//            }
//            LeakCanary.install(this);
        }

//        初始化bugtag
        if (BuildConfig.DEBUG) {
            initBugTag();
        }
        // 初始化配置
        initConfigs();
        //第三方分享
        initShareConfig();
        // 内存优化
        heapUtilization();
        // 初始化统计
        initAnalytics();
        // 初始化UniversalImageLoader配置
        initImageLoader();
        // 开启服务、任务
        initBackgroundTasks();

        // 检测版本
        AppVersionConfiguration configuration = new AppVersionConfiguration.Builder(this)
                .appIcon(R.drawable.push)
                .isDebug(BuildConfig.DEBUG)
                .setVersionChecker(new AppVersionChecker())
                .build();
        AppDownloadClient.getInstance().init(configuration);

        // 打印手机分辨率信息
        if (BuildConfig.DEBUG) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            LogUtil.d(TAG, metrics.toString());
            PhoneModelUtil.printMode();
        }

        //解决android N系统以上Uri转换文件路径报错FileUriExposedException的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initUmeng() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(BuildConfig.DEBUG);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        PushMsgHandleService.start(getApplicationContext(), msg.custom);
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                    }
                });
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
    }

    /**
     * 第三方分享
     */
    private void initShareConfig() {
        // 微信 appid appsecret
        PlatformConfig.setWeixin(getString(R.string.wx_appId), "9a866cf4b718d9b5f761fce8a9f199d7");
        // 新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("2579265060", "99ac14682e0946f3caf4e26432cb3d61", "http://101.200.151.11");
        // QQ和Qzone appid appkey 并注意替换manifest.xml中的appid
        PlatformConfig.setQQ("1105276267", "ESBK1K2Ct340UU2K");
    }

    private void initBugTag() {
        //customizable init option
        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(true).//是否获取位置
                trackingCrashLog(true).//是否收集crash
                trackingConsoleLog(true).//是否收集console log
                trackingUserSteps(true).//是否收集用户操作步骤
                build();
        Bugtags.start("31477d09ddaf6afe4499a4699bdb33ac", this, Bugtags.BTGInvocationEventBubble, options);
    }

    /**
     * 初始化友盟统计
     */
    private void initAnalytics() {
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);//普通统计场景类型
//        MobclickAgent.openActivityDurationTrack(false);//禁止默认的Activity页面统计方式
    }

    private void initConfigs() {
        PackageManager pm = getPackageManager();
        int versionCode = 0;
        String versionName = null;
        String appName = null;
        try {
            ApplicationInfo info = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
            appName = pm.getApplicationLabel(info).toString();
            if (packInfo != null) {
                versionName = packInfo.versionName;
                versionCode = packInfo.versionCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LContext.init(this, BuildConfig.DEBUG);
        LContext.appIcon = R.drawable.push;
        LContext.appName = appName;
        LContext.pkgName = getPackageName();
        LContext.versionCode = versionCode;
        LContext.versionName = versionName;
        //是否启用沉浸式布局
        LContext.isTranslucent = true;

        DataConfig.init(this);
    }

    /**
     * 初始化niversal-image-loader
     */
    private void initImageLoader() {
        FileNameGenerator fileNameGenerator = new Md5FileNameGenerator();
        // DiskCache diskCache =
        // DefaultConfigurationFactory.createDiskCache(this, fileNameGenerator,
        // 0, 0);
        // AppConfig.getInstance().setBaseDiscCache(diskCache);

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                // .memoryCacheSize(1500000)
                // .memoryCacheSize((int) 1.5 * 1024 * 1024)
                // .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(fileNameGenerator)
                // .diskCache(diskCache)
                .memoryCache(new WeakMemoryCache());
        if (LogUtil.isOpen) {
            builder.writeDebugLogs();
        }

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(builder.build());
    }

    /**
     * 使用 dalvik.system.VMRuntime类提供的setTargetHeapUtilization方法可以增强程序堆内存的处理效率
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void heapUtilization() {
        try {
            Class localClass = Class.forName("dalvik.system.VMRuntime");
            Method localMethod1 = localClass.getDeclaredMethod("getRuntime",
                    new Class[0]);
            Class[] arrayOfClass = new Class[1];
            arrayOfClass[0] = Float.TYPE;
            Method localMethod2 = localClass.getDeclaredMethod(
                    "setTargetHeapUtilization", arrayOfClass);
            Object localObject = localMethod1.invoke(localClass, new Object[0]);
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Float.valueOf(0.75F);
            localMethod2.invoke(localObject, arrayOfObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前进程名称
     *
     * @return
     */
    private String getCurProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 初始化后台定时任务
     */
    private void initBackgroundTasks() {
        ScheduleTaskManager taskManager = ScheduleTaskManager.getInstance();
        // 定位任务
        // taskManager.addTask(new LocationScheduleTask());
        // 更新任务
        taskManager.addTask(new UpdateTask());

        // 开启后台服务
        startService(new Intent(this, CoreService.class));
    }

}
