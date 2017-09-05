package com.wlb.agent.common.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.android.util.LContext;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.umeng.message.PushAgent;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.main.TabAct;
import com.wlb.agent.ui.splash.SplashAct;
import com.wlb.agent.ui.user.frag.WalletFlowDetailFrag;
import com.wlb.agent.ui.user.frag.WalletFlowFrag;
import com.wlb.common.SimpleFragAct;

import org.json.JSONObject;

import component.update.AppDownloadClient;
import component.update.AppDownloadService;

/**
 * 推送消息处理器
 *
 * @author zhangquan
 */
public class PushMsgHandler {
    public static final String TAG = "PushReceiver";
    public static void log(String msg) {
        LogUtil.d(TAG, msg);
    }

    /**
     * 处理推送消息
     *
     * @param jsonObject
     */
    public static void handlePushData(JSONObject jsonObject) {
        PushMsgHandler.log("msg="+jsonObject.toString());

        // 测试消息
        boolean isTest = jsonObject.optInt("isTest") == 1 ? true : false;
        if (isTest && !LContext.isDebug) {
            // 如果服务器发的测试推送消息，但是目前版本为线上版本 则不处理推送
            return;
        }
        // 用户id
        String uid = jsonObject.optString("token");// 推送消息的目标用户
        String curUid = UserClient.getToken();// 当前登录用户id

        // 消息内容
        JSONObject dataJson = jsonObject.optJSONObject("data");
        if (null == dataJson) {
            return;
        }

        SimpleFragAct.SimpleFragParam fragParam = null;
        String pushMsg = dataJson.optString("pushMsg");// 展示在通知栏的消息
        // 消息类型
        int type = dataJson.optInt("type");
        switch (type) {
            case 1: // 版本升级
                long versionCode = dataJson.optLong("versionCode");
                if (versionCode > LContext.versionCode) {
                    // 发现新版本
                    AppDownloadClient.startDownloadService(LContext.getContext(), AppDownloadService.ACTION_CHECK_VERSION);
                    // 发送通知
                    sendNotify(pushMsg, null);
                }
                break;
            case 2: // 车险订单
                if (uid.equals(curUid)) {// 目标推送用户非当前登录用户
                    return;
                }
                // int orderStatus = dataJson.optInt("orderStatus");
                long orderId = dataJson.optLong("requestId");
                if (orderId > 0) {
                    //要修改
                    // 订单id不为0，则进入订单详情页
//                    fragParam = InsuranceOrderDetailFrag.getStartParam(orderId);
                } else {
                    // 订单列表页
//                    fragParam= OrderFrag.getStartParam();
                }
//                sendNotify(pushMsg, fragParam);
                break;
            case 3: // / 打开WebView
                String url = dataJson.optString("url");// 打开的链接url
                String title = dataJson.optString("title");// WebView页的标题
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.url=url;
                webViewParam.title=title;
                sendNotify(pushMsg, WebViewFrag.getStartParam(webViewParam));
                break;
            case 4: // 佣金通知
                long billId = dataJson.optLong("requestId", -1);
                if (billId > 0) {
                    // 订单id不为0，则进入订单详情页
                    fragParam = WalletFlowDetailFrag.getStartParam(billId);
                } else {
                    // 佣金列表页
                    fragParam = WalletFlowFrag.getStartParam();
                }
                sendNotify(pushMsg, fragParam);
                break;
        }
    }

    /**
     * 发送通知
     *
     * @param pushMsg 通知栏消息
     * @param param   点击通知跳转的页面
     */
    @SuppressWarnings("deprecation")
    private static void sendNotify(String pushMsg, SimpleFragAct.SimpleFragParam param) {
        System.out.println("pushMsg="+pushMsg);
        if (TextUtils.isEmpty(pushMsg)) {
            return;
        }
        Context ctx = LContext.getContext();

        // 点击通知栏跳转的页面
        Intent intent = new Intent(ctx, SplashAct.class);
//        if (null != param) {
//            intent = SimpleFragAct.getStartIntent(ctx, param);
//        }
        if(TabAct.IS_OPEN){
            if (null != param) intent = SimpleFragAct.getStartIntent(ctx, param);
        }else{
            intent = TabAct.getStartIntent(ctx,param);
        }

        int pushId = pushMsg.hashCode();
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,
                pushId, intent, 0);

        ApplicationInfo applicationInfo = ctx.getApplicationInfo();
        String appName = ctx.getString(applicationInfo.labelRes);
        int appIcon = applicationInfo.icon;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
        NotificationManager mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setContentTitle(appName);
        mBuilder.setContentText(pushMsg);
        mBuilder.setSmallIcon(appIcon);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.contentIntent = pendingIntent;
        mNotifyManager.notify(pushMsg.hashCode(), notification);

    }

    // ------------------------------------------------------

    public synchronized static void doOpenPush() {
        if (!NetworkUtil.isNetworkAvailable(LContext.getContext())) {
            return;
        }
        String deviceToken = PushAgent.getInstance(LContext.getContext()).getRegistrationId();
        UserClient.doPushBind(deviceToken, new SimpleCallback() {
            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    PushMsgHandler.log("推送服务注册成功");
                } else {
                    PushMsgHandler.log("推送服务注册失败,errorMsg=" + response.msg);
                }
            }
        });
    }
}
