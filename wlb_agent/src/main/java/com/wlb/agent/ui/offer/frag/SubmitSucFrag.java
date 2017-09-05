package com.wlb.agent.ui.offer.frag;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.util.ext.ToastUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.main.TabAct;
import com.wlb.agent.ui.order.frag.OrderDetailFrag;
import com.wlb.agent.ui.order.frag.OrderFrag;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.ActivityManager;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Berfy on 2017/7/18.
 * 提交核保成功
 */
public class SubmitSucFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.layout_hotline)
    LinearLayout mLlHotline;
    @BindView(R.id.btn_order_detail)
    Button mBtnOrderDetail;

    private PopupWindowUtil mPopupWindowUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.underwrite_submit_suc_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ActivityManager.getInstance().pushActivity("offer", getActivity());
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        getTitleBar().setOnLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.show("返回订单");
                TabAct.start(mContext);
                ActivityManager.getInstance().popActivityWithTag("offer");
            }
        });
    }

    @OnClick({R.id.btn_order_list, R.id.btn_order_detail, R.id.layout_hotline})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_order_list:
                SimpleFragAct.start(mContext, new SimpleFragAct.SimpleFragParam(R.string.order, OrderFrag.class));
                ActivityManager.getInstance().popAllActivityExceptOne(null);
                break;
            case R.id.btn_order_detail:
                Bundle bundle = new Bundle();
                bundle.putInt("status", 0);
                SimpleFragAct.start(mContext, new SimpleFragAct.SimpleFragParam(R.string.order_detail, OrderDetailFrag.class, new Bundle()));
                ActivityManager.getInstance().popAllActivityExceptOne(null);
                break;
            case R.id.layout_hotline:
                mPopupWindowUtil.showHotLine();
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        ToastUtil.show("返回订单");
        TabAct.start(mContext);
        ActivityManager.getInstance().popAllActivityExceptOne(null);
        return false;
    }
}
