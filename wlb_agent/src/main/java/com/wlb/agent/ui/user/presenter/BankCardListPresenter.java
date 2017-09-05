package com.wlb.agent.ui.user.presenter;

import android.content.Context;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.BankCardInfo;
import com.wlb.agent.core.data.user.response.BankCardResponse;
import com.wlb.agent.ui.common.BasePresenter;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.user.adapter.BankCardAdapter;
import com.wlb.agent.ui.user.presenter.view.IBankCardListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import common.widget.LoadingBar;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 我的银行卡列表
 *
 * @author 张全
 */
public class BankCardListPresenter implements BasePresenter {

    private IBankCardListView view;
    private BankCardAdapter mAdapter;
    private Context context;
    private Task doGetBankCardTask;
    private Task doSetDefaultCard;
    private boolean isBankCard;
    private boolean isSetDefault;
    private boolean showLoadingBar = true;

    public BankCardListPresenter(IBankCardListView view) {
        this.view = view;
        this.context = view.getContext();
        mAdapter = new BankCardAdapter(context, new ArrayList<BankCardInfo>(), R.layout.bankcard_list_item);
        view.setAdapter(mAdapter);
    }

    public void onItemClick(int position) {
        BankCardInfo bankData = mAdapter.getItem(position);
        if (!bankData.isDefault()) {
            doSetDefault(bankData);
        }
    }

    public void setData(List<BankCardInfo> data) {
        if (data.size() == 0) {
            ToastUtil.show(R.string.wallet_withdrawal_bank_tip);
            return;
        }
        mAdapter.refresh(data);
    }

    public void sendUpdateEvent(BankCardInfo cardInfo) {
        PushEvent pushEvent = new PushEvent(Constant.IntentAction.WALLET_UPDATE);
        pushEvent.setData(cardInfo);
        EventBus.getDefault().post(pushEvent);//更新钱包
    }

    //获取银行卡列表

    public void doGetBankCard() {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            ToastUtil.show(R.string.net_noconnection);
            if (showLoadingBar) view.setLoadingBarStatus(LoadingBar.LoadingStatus.NOCONNECTION);
            view.setRefreshing(false);
            return;
        }
        if (isBankCard) {
            return;
        }

        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(context, "请稍后");
        doGetBankCardTask = UserClient.doGetBankCard(new ICallback() {
            @Override
            public void start() {
                isBankCard = true;
                if (showLoadingBar) view.setLoadingBarStatus(LoadingBar.LoadingStatus.START);
            }

            @Override
            public void success(Object data) {
                BankCardResponse response = (BankCardResponse) data;
                if (response.isSuccessful()) {
                    if (null != response.list) {
                        List<BankCardInfo> bankCardInfos = new ArrayList<BankCardInfo>();
                        for (BankCardInfo bankCardInfo : response.list) {
                            if(bankCardInfo.getCardType().equals("BANK")){
                                bankCardInfos.add(bankCardInfo);
                            }
                        }
                        setData(bankCardInfos);
                        if (showLoadingBar)
                            view.setLoadingBarStatus(LoadingBar.LoadingStatus.SUCCESS);
                    } else {
                        if (showLoadingBar)
                            view.setLoadingBarStatus(LoadingBar.LoadingStatus.RELOAD);
                    }
                } else {
                    ToastUtil.show(response.msg);
                    if (showLoadingBar) view.setLoadingBarStatus(LoadingBar.LoadingStatus.RELOAD);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
                if (showLoadingBar) view.setLoadingBarStatus(LoadingBar.LoadingStatus.RELOAD);
            }

            @Override
            public void end() {
                showLoadingBar = false;
                isBankCard = false;
                dialog.dissmiss();
                view.setRefreshing(false);
            }
        });
    }

    //修改默认银行卡
    public void doSetDefault(final BankCardInfo bankData) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isSetDefault) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(context, "请稍候");
        doSetDefaultCard = UserClient.doSetDefaultCard(bankData.bank_id, new ICallback() {
            @Override
            public void start() {
                isSetDefault = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    bankData.setDefault(true);
                    ToastUtil.show("修改成功");
                    sendUpdateEvent(bankData);
                    if (view.isPickCard()) {
                        view.close();
                    } else {
                        List<BankCardInfo> list = mAdapter.getList();
                        for (BankCardInfo cardInfo : list) {
                            if (cardInfo.bank_id != bankData.bank_id) {
                                cardInfo.setDefault(false);
                            }
                        }
                        mAdapter.refresh(list);
                    }
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
                dialog.dissmiss();
                isSetDefault = false;
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
        if (null != doGetBankCardTask) {
            doGetBankCardTask.stop();
        }
        if (null != doSetDefaultCard) {
            doSetDefaultCard.stop();
        }
    }
}
