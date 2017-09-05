package com.wlb.agent.common.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.entity.MessageInfo;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.util.ApkDefender;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 后台任务
 *
 * @author 张全
 */
public class BackgroundTaskService extends Service {
    private static final String APK = "APK";
    private static final String MSG = "MSG";
    private static final String TASK_NOTIFY = "TASK_NOTIFY";
    private static final String CONTACTS="CONTACT";
    private Task reportTask;
    private Subscription apkChecker;
    /**
     * 消息已读回执
     *
     * @param ctx
     * @param messageInfo
     */
    public static void start(Context ctx, MessageInfo messageInfo) {
        Intent intent = new Intent(ctx, BackgroundTaskService.class);
        intent.putExtra(MSG, messageInfo);
        ctx.startService(intent);
    }

    /**
     * 积分汇报
     *
     * @param ctx
     * @param taskCode
     */
    public static void start(Context ctx, String taskCode) {
        Intent intent = new Intent(ctx, BackgroundTaskService.class);
        intent.putExtra(TASK_NOTIFY, taskCode);
        ctx.startService(intent);
    }

    /**
     * APK校验
     *
     * @param ctx
     * @param tag
     */
    public static void start(Context ctx, int tag) {
        Intent intent = new Intent(ctx, BackgroundTaskService.class);
        intent.putExtra(APK, tag);
        ctx.startService(intent);
    }

    /**
     * 上传联系人
     * @param ctx
     * @param contacts
     */
    public static void postContacts(Context ctx, String contacts ) {
        Intent intent = new Intent(ctx, BackgroundTaskService.class);
        intent.putExtra(CONTACTS, contacts);
        ctx.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            if (intent.hasExtra(MSG)) {
                //发送已读消息回执
                MessageInfo msgInfo = (MessageInfo) intent.getSerializableExtra(MSG);
                sendMsgReadReport(msgInfo);
            } else if (intent.hasExtra(TASK_NOTIFY)) {
                String taskCode = intent.getStringExtra(TASK_NOTIFY);
                notifyStatus(taskCode);
            } else if (intent.hasExtra(APK)) {
                if (!LContext.isDebug) {
                    checkAPK();
                }
            }else if(intent.hasExtra(CONTACTS)){
                String contacts = intent.getStringExtra(CONTACTS);
                postContacts(contacts);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != reportTask) {
            reportTask.stop();
        }
        if (null != notifyTask) {
            notifyTask.stop();
        }
        if (null != apkChecker && !apkChecker.isUnsubscribed()) {
            apkChecker.unsubscribe();
        }
    }

    private void sendMsgReadReport(MessageInfo messageInfo) {
        if (null == messageInfo) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(this)) {
            return;
        }
        reportTask = AgentServiceClient.doMsgReadReport(messageInfo.msgId, 2, new ICallback() {

            @Override
            public void start() {
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new PushEvent(Constant.IntentAction.MSG_READ));
                }
            }

            @Override
            public void failure(NetException e) {

            }

            @Override
            public void end() {
            }
        });
    }

    //------------------------------积分模块------------------------------------
    private boolean isSign;
    private Task notifyTask;

    private void notifyStatus(String name) {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isSign) return;
        notifyTask = UserClient.doNotifyIntegral(name, new ICallback() {
            @Override
            public void start() {
                isSign = true;
            }

            @Override
            public void success(Object data) {
            }

            @Override
            public void failure(NetException e) {
            }

            @Override
            public void end() {
                isSign = false;
            }
        });
    }
    //------------------------------APP安全机制------------------------------------
    private void checkAPK() {
        apkChecker = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean validate = ApkDefender.checkInvalidate(BackgroundTaskService.this);
                subscriber.onNext(validate);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            ToastUtil.show("当前版本为破解版，请从正规市场下载");
                        }
                    }
                })
                .delay(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            Process.killProcess(Process.myPid());
                        }
                    }
                });
    }
    //------------------------------上传联系人------------------------------------
    private boolean isPostContact;
    private void postContacts(String contacts){
        if (!NetworkUtil.isNetworkAvailable(this)) {
            return;
        }
        if (isPostContact) return;
        AgentServiceClient.doPostContacts(contacts, new ICallback() {
            @Override
            public void start() {
                isPostContact=true;
            }

            @Override
            public void success(Object data) {

            }

            @Override
            public void failure(NetException e) {

            }

            @Override
            public void end() {
                isPostContact=false;
            }
        });
    }

}
