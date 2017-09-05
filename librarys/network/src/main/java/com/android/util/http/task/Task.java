package com.android.util.http.task;

import com.android.util.http.request.NetRequest;

import okhttp3.Call;

public class Task {
    private Call mCall;
    private NetRequest mRequest;

    public Task(Call call, NetRequest request) {
        this.mCall = call;
        this.mRequest = request;
    }
    public NetRequest getRequest(){
        return mRequest;
    }

    /**
     * 停止
     */
    public void stop() {
        if (null != mRequest) {
            mRequest.stop();
        }
        mRequest=null;
        try {
            if (null != mCall) {
                mCall.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCall=null;
    }
}