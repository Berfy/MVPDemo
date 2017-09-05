package com.wlb.agent.ui.user.presenter;

import android.text.TextUtils;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.BasePresenter;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.user.presenter.view.IBackCardAddView;
import com.wlb.agent.ui.user.util.UserUtil;

import org.greenrobot.eventbus.EventBus;

import common.widget.dialog.loading.LoadingDialog;

/**
 * @author 张全
 */

public class BackCardAddPresenter implements BasePresenter {
    private IBackCardAddView view;
    private Task doAddCardTask;
    private boolean isAddBankCard;

    public BackCardAddPresenter(IBackCardAddView view) {
        this.view = view;
    }

    public void addBankCard(String carMaster, final String cardNumber) {
        if (!NetworkUtil.isNetworkAvailable(view.getContext())) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isAddBankCard) {
            return;
        }
        if(!UserUtil.checkShowName(carMaster)){
            return;
        }
        if (TextUtils.isEmpty(cardNumber)) {
            ToastUtil.show("卡号不能为空");
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(view.getContext(), "请稍候");
        doAddCardTask = UserClient.doAddBankCard(cardNumber, carMaster, new ICallback() {
            @Override
            public void start() {
                isAddBankCard = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("银行卡添加成功");
                    EventBus.getDefault().post(new PushEvent(Constant.IntentAction.BANKCARD_ADD));
                    view.close();
                } else {
                    view.showErrorBar(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isAddBankCard = false;
                dialog.dissmiss();
            }
        });
    }

    //--------------------生命周期
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
        if (null != doAddCardTask) {
            doAddCardTask.stop();
        }
    }

}
