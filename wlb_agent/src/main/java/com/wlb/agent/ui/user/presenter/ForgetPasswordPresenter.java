package com.wlb.agent.ui.user.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.BasePresenter;
import com.wlb.agent.ui.user.helper.GetCodeTimer;
import com.wlb.agent.ui.user.presenter.view.IForgetPasswordView;
import com.wlb.agent.ui.user.util.UserUtil;

import common.widget.dialog.loading.LoadingDialog;

import static com.android.util.LContext.getString;

/**
 * @author 张全
 */

public class ForgetPasswordPresenter implements BasePresenter {
  private IForgetPasswordView view;
    private GetCodeTimer getCodeTimer;
    private Task doForgetPassword, getCodeTask;
    private final int INTERVAL = 60 * 1000;
    private Context context;
    public ForgetPasswordPresenter(IForgetPasswordView view){
        this.view=view;
        this.context=view.getContext();
        getCodeTimer = new GetCodeTimer(view.getCodeView());
        long nextTime = UserUtil.getNextTime();
        if (nextTime > 0 && ((nextTime - System.currentTimeMillis()) > 1 * 1000)) {
            getCodeTimer.start();
        }
    }
    private boolean isGetCode;
    public void getCode(String phone) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isGetCode) {
            return;
        }

        // 检测电话号码
        if (!UserUtil.checkPhoneNum(phone)) {
            return;
        }
        final LoadingDialog dialog = new LoadingDialog(context, getString(R.string.getting_code)).show();


        getCodeTask = UserClient.doGetCode(phone, UserClient.CodeAction.RESETPWD, new ICallback() {
            @Override
            public void start() {
                isGetCode = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("验证码已发出");
                    UserUtil.setNextTime(System.currentTimeMillis() + INTERVAL);
                    getCodeTimer.start();
                    view.showFocus();
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
                isGetCode = false;
                dialog.dissmiss();
            }
        });
    }

    private boolean isUploadPassword;

    public void doResetPassword(String phone, String code, String password) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isUploadPassword) {
            return;
        }

        if (!UserUtil.checkPhoneNum(phone)) {
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtil.show(R.string.tip_code_null);
            return;
        }
        if(!UserUtil.checkPwd(password)){
            return;
        }

        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(context, "请稍后...");

        doForgetPassword = UserClient.doResetPassword(phone, code, password, new ICallback() {

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("密码修改成功");
                    view.close();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isUploadPassword = true;
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isUploadPassword = false;
                dialog.dissmiss();
            }
        });

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestory() {
        if (null != getCodeTask) {
            getCodeTask.stop();
        }
        if (null != doForgetPassword) {
            doForgetPassword.stop();
        }
    }
}
