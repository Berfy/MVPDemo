package com.wlb.agent.common.receiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wlb.common.service.CoreService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 曹泽琛.
 */
public class PushMsgHandleService extends Service {
    private static final String PARAM="PUSH_MSG";
    public static void start(Context ctx, String pushMsg){
        Intent intent = new Intent(ctx, PushMsgHandleService.class);
        intent.putExtra(PARAM,pushMsg);
        ctx.startService(intent);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null!=intent){
            //激活主程序守护Service
            CoreService.startCoreService(this, CoreService.ACTIVE);

            String pushMsg = intent.getStringExtra(PARAM);
            try {
                JSONObject  json = new JSONObject(pushMsg);
                PushMsgHandler.handlePushData(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return Service.START_STICKY;
    }
}
