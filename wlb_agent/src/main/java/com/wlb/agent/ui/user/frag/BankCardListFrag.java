package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.android.util.ext.ToastUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.user.presenter.BankCardListPresenter;
import com.wlb.agent.ui.user.presenter.view.IBankCardListView;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.LoadingBar;
import common.widget.listview.material.SwipeRefreshListView;

/**
 * Created by JiaHongYa
 * 银行卡
 */
public class BankCardListFrag extends SimpleFrag implements View.OnClickListener, IBankCardListView {

    private static final String PARAM = "PICK_CARD";
    @BindView(R.id.listview)
    SwipeRefreshListView mRefreshLayout;
    @BindView(R.id.loadingBar)
    LoadingBar mLoadingBar;
    private boolean mIsPickCard;
    private BankCardListPresenter presenter;

    private static SimpleFragAct.SimpleFragParam getStartParam(boolean pickCard) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAM, pickCard);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("银行卡", BankCardListFrag.class, bundle);
        return param;
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam(false));
    }

    public static void start(Context context, boolean pickCard) {
        SimpleFragAct.start(context, getStartParam(pickCard));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.bankcard_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getTitleBar().setRightBtnDrawable(R.drawable.add_bankcard);
        getTitleBar().setOnRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankCardAddFrag.start(mContext);
            }
        });
        if (null != getArguments()) mIsPickCard = getArguments().getBoolean(PARAM);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    mRefreshLayout.setRefreshing(false);
                    return;
                }
                presenter.doGetBankCard();
            }
        });

        mRefreshLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onItemClick(position);
            }
        });
        addAction(Constant.IntentAction.BANKCARD_ADD);

        presenter = new BankCardListPresenter(this);
        presenter.doGetBankCard();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (containsAction(action)) {
            presenter.doGetBankCard();
        }
    }

    @OnClick({R.id.loadingBar})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loadingBar:
                presenter.doGetBankCard();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        mRefreshLayout.setAdapter(adapter);
    }

    @Override
    public void setLoadingBarStatus(LoadingBar.LoadingStatus loadingStatus) {
        mLoadingBar.setLoadingStatus(loadingStatus);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public boolean isPickCard() {
        return mIsPickCard;
    }
}
