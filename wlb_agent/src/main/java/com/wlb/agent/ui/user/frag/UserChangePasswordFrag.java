package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
 * 修改密码页
 * Created by JiaHongYa
 */
public class UserChangePasswordFrag extends SimpleFrag implements View.OnClickListener {
    private EditText old_password, new_password;
    private static final String PHONE_PARAM = "PHONE_PARAM";
    private ImageView old_deleteBut, new_deleteBut;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam("修改密码", UserChangePasswordFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_change_password;
    }

    @Override
    protected void init(Bundle savedInstanceState) {


        old_password = findViewById(R.id.old_password);//原始密码
        old_deleteBut = findViewById(R.id.delete_old_password);
        old_deleteBut.setOnClickListener(this);
        addTExtChange(old_password, old_deleteBut);

        new_password = findViewById(R.id.new_password);//新密码
        new_deleteBut = findViewById(R.id.delete_new_password);
        new_deleteBut.setOnClickListener(this);
        addTExtChange(new_password, new_deleteBut);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    public void addTExtChange(EditText editText, final ImageView imageView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                String oldPassword = StringUtil.trim(old_password);//原始密码
                if (TextUtils.isEmpty(oldPassword)) {
                    ToastUtil.show("原始密码不能为空");
                    return;
                }
                String newPassword = StringUtil.trim(new_password);
                if (TextUtils.isEmpty(newPassword)) {
                    ToastUtil.show("新密码不能为空");
                    return;
                }
                amendPassword(oldPassword, newPassword);
                break;
            case R.id.delete_old_password:
                old_password.setText("");
                break;
            case R.id.delete_new_password:
                new_password.setText("");
                break;
            default:
                break;
        }
    }

    private Task doAmentPassword;
    private boolean isPassword;

    private void amendPassword(String oldPassword, String newPassword) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isPassword) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "密码修改中...");
        doAmentPassword = UserClient.doChangePassword(oldPassword, newPassword, new ICallback() {
            @Override
            public void start() {
                isPassword = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("密码修改成功");
                    finish();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isPassword = false;
                dialog.dissmiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != doAmentPassword) {
            doAmentPassword.stop();
        }
    }
}
