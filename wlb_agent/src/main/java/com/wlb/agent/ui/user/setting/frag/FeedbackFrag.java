package com.wlb.agent.ui.user.setting.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import common.widget.dialog.loading.LoadingDialog;

/**
 * 用户反馈
 * <p>
 * Created by JiaHongYa
 */
public class FeedbackFrag extends SimpleFrag implements View.OnClickListener {
    private EditText contact, content;
    private Button submit;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.fb_title,
                FeedbackFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.feedback_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(null);
        getTitleBar().setBackgroundColor(Color.WHITE);

        contact = findViewById(R.id.fb_email);//联系方式
        content = findViewById(R.id.fb_content);//反馈内容
        submit = findViewById(R.id.btn_submit);//提交按钮
        submit.setOnClickListener(this);

    }

    private boolean isFeedBack;
    private Task doFeedBack;

    private void submitFeedBack(String contactTex, String contentTex) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            return;
        }
        if (isFeedBack) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "请稍后...");
        doFeedBack = UserClient.doFeedBack(contactTex, contentTex, new ICallback() {
            @Override
            public void start() {
                isFeedBack = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("提交成功");
                    contact.setText("");
                    content.setText("");
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show("网络请求失败，请稍后再试！");
            }

            @Override
            public void end() {
                isFeedBack = false;
                dialog.dissmiss();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (doFeedBack != null) {
            doFeedBack.stop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                String contactTex = StringUtil.trim(contact);
                String contentTex = StringUtil.trim(content);
                if (TextUtils.isEmpty(contentTex)) {
                    ToastUtil.show("亲！内容不能为空哦！！");
                    return;
                }
                submitFeedBack(contactTex, contentTex);
                break;
        }
    }
}
