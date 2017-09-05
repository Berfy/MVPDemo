package com.wlb.agent.ui.order.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.util.ext.SoftInputUtil;
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
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import common.widget.adapter.ListAdapter;

import static com.wlb.agent.R.string.pay;

/**
 * 搜索订单列表
 *
 * @author Berfy
 */
public class OrderSearchListFrag extends CommonListFrag implements View.OnClickListener, OrderListAdapter.OnSubmitClickListener {

    private String mKeyword = "";//搜索关键字
    private OrderSendCodeUtil mOrderSendCodeUtil;

    /**
     * 搜索
     */
    private void refresh() {
        doLoadData(0);
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        mOrderSendCodeUtil = new OrderSendCodeUtil(mContext, new OrderSendCodeUtil.OnFinishListener() {
            @Override
            public void onFinish() {

            }
        });
        View view = View.inflate(mContext, R.layout.view_search, null);
        ((EditText) view.findViewById(R.id.edit_search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mKeyword = charSequence.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ((EditText) view.findViewById(R.id.edit_search)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    refresh();
                    SoftInputUtil.hideSoftInput(getActivity());
                }
                return false;
            }
        });
        setNoContentTip(R.drawable.ic_order_null, getString(R.string.order_tip_null), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PushEvent(Constant.IntentAction.ORDER_GO));
                close();
            }
        }, R.drawable.btn_gray1_line_selector, getString(R.string.order_null_go));
        addTitleView(view);
    }

    @Override
    protected Task executeTask(long lastId, ICallback callback) {
//        InsuranceOrderResponse orderResponse = new InsuranceOrderResponse();
//        if (TextUtils.isEmpty(mKeyword)) {
//            List<InsuranceOrder> list = new ArrayList<>();
//            orderResponse.getList().addAll(list);
//            callback.success(orderResponse);
//            callback.end();
//            return new Task(null, null);
//        }
//        //mKey
//        List<InsuranceOrder> datas = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            InsuranceOrder order = new InsuranceOrder();
//            datas.add(order);
//        }
//        orderResponse.getList().clear();
//        orderResponse.getList().addAll(datas);
//        callback.success(orderResponse);
//        callback.end();
        if (TextUtils.isEmpty(mKeyword)) {
            InsuranceOrderResponse orderResponse = new InsuranceOrderResponse();
            callback.success(orderResponse);
            callback.end();
            return null;
        }
        return InsuranceClient.searchOrder(lastId, mKeyword, callback);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        return new OrderListAdapter(mContext, mDataList, this);
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
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InsuranceOrder order = (InsuranceOrder) getAdapter().getList().get(position);
        OrderDetailFrag.start(mContext, order.policyId + "");
    }

    @Override
    public void submit(InsuranceOrder order) {
        if (null != order) {
            WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
            if (order.uploadIDFlag()) {
                webViewParam.title = "上传照片";
                webViewParam.url = H5.UPLOAD_PAPERS + order.orderNo;
                WebViewFrag.start(mContext, webViewParam);
            } else {
                switch (order.statusCode) {
                    case 2://支付
                    case 4://核保成功
//                        webViewParam.url = order.payForward;
                        webViewParam.url = "https://www.wolaibao.com/h5test/common/pay/pay.html?token=" + UserClient.getLoginedUser().token
                                + "&orderNo=" + order.orderNo + "&shareFlag=0";
                        webViewParam.title = getString(pay);
                        SimpleFragAct.SimpleFragParam simpleFragParam = WebViewFrag.getStartParam(webViewParam);
                        WebViewFrag.start(mContext, simpleFragParam);
                        break;
                    case 1://核保中
                    case 3://重新核保
                        if (order.isUnderwritingFail()) {    // 3 核保失败  底部显示从新核保按钮
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
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}