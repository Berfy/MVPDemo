package com.wlb.agent.ui.order.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.android.util.http.callback.ICallback;
import com.android.util.http.task.Task;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.insurance.InsuranceClient;
import com.wlb.agent.core.data.insurance.entity.InsuranceOrder;
import com.wlb.agent.core.data.insurance.response.InsuranceOrderResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.order.adapter.OrderListAdapter;
import com.wlb.agent.util.OrderSendCodeUtil;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import common.widget.adapter.ListAdapter;

/**
 * 订单列表
 *
 * @author Berfy
 */
public class OrderListFrag extends CommonListFrag implements View.OnClickListener, OrderListAdapter.OnSubmitClickListener {

    private int mPosition;
    private List<JSONArray> mOptionsList = new ArrayList<>();
    private PopupWindowUtil mPopupWindowUtil;
    private OrderSendCodeUtil mOrderSendCodeUtil;

    @Override
    protected void initParams(Bundle savedInstanceState) {
        //不主动加载
        lazyLoad = true;
        mOrderSendCodeUtil = new OrderSendCodeUtil(mContext, new OrderSendCodeUtil.OnFinishListener() {
            @Override
            public void onFinish() {
                doLoadData(0);
            }
        });
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        if (null != getArguments()) {
            mPosition = getArguments().getInt("position");
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(1);
        mOptionsList.add(jsonArray);

        jsonArray = new JSONArray();
        jsonArray.put(2);
        mOptionsList.add(jsonArray);

        jsonArray = new JSONArray();
        jsonArray.put(7);
        mOptionsList.add(jsonArray);

        jsonArray = new JSONArray();
        jsonArray.put(5);
        mOptionsList.add(jsonArray);

        jsonArray = new JSONArray();
        jsonArray.put(-1);
        mOptionsList.add(jsonArray);
        setNoContentTip(R.drawable.ic_order_null, getString(R.string.order_tip_null), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PushEvent(Constant.IntentAction.ORDER_GO));
                close();
            }
        }, R.drawable.btn_gray1_line_selector, getString(R.string.order_null_go));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InsuranceOrder order = (InsuranceOrder) getAdapter().getList().get(position);
        OrderDetailFrag.start(mContext, order.policyId + "");
    }

    @Override
    protected Task executeTask(long lastId, ICallback callback) {
        return InsuranceClient.doGetOrders(lastId, mOptionsList.get(mPosition), callback);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        return new OrderListAdapter(getContext(), mDataList, this);
    }

    public void setTuiguangfei(boolean isOpen) {
        if (null != getAdapter()) {
            getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        InsuranceOrderResponse response = (InsuranceOrderResponse) baseResponse;
        List<InsuranceOrder> orders = response.getList();
        long lastId = 0;
        if (orders.size() > 0) {
            lastId = orders.get(orders.size() - 1).policyId;
        }
        return new ListData(orders, lastId);
    }

    @Override
    public void submit(InsuranceOrder order) {
        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
        switch (order.statusCode) {
            case 2://支付
            case 4://核保成功
//                webViewParam.url = order.payForward;
                webViewParam.url = "https://www.wolaibao.com/h5test/common/pay/pay.html?token=" + UserClient.getLoginedUser().token
                        + "&orderNo=" + order.orderNo + "&shareFlag=0";
                webViewParam.title = getString(R.string.pay);
                SimpleFragAct.SimpleFragParam simpleFragParam = WebViewFrag.getStartParam(webViewParam);
                WebViewFrag.start(mContext, simpleFragParam);
                break;
            case 1://核保中
            case 3://核保失败 重新核保
                if (order.uploadIDFlag()) {
                    webViewParam.title = "上传照片";
                    webViewParam.url = H5.UPLOAD_PAPERS + order.orderNo;
                    WebViewFrag.start(mContext, webViewParam);
                } else if (order.isUnderwritingFail()) {    // 3 核保失败  底部显示从新核保按钮
                    webViewParam.title = "重新核保";
                    webViewParam.url = H5.AGAIN_OFFERA_PRICE + order.vehicleId;
                    WebViewFrag.start(mContext, webViewParam);
                }
                break;
            case 7://支付成功 提交验证码
                mOrderSendCodeUtil.setOrderInfo(order.policyId,
                        order.verifyCodeStatus,
                        order.verifyPhoneNo);
                mOrderSendCodeUtil.show();
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}