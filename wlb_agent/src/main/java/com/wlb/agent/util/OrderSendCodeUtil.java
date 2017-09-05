package com.wlb.agent.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.insurance.InsuranceClient;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserCodeResponse;
import com.wlb.agent.ui.common.view.AlertDialogView;
import com.wlb.agent.ui.common.view.CountButton;

import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;
import rx.Subscriber;

import static com.android.util.LContext.getString;

/**
 * Created by Berfy on 2017/7/28.
 * 订单提交验证码工具类
 */
public class OrderSendCodeUtil {

    private Context mContext;
    private PopupWindowUtil mPopupWindowUtil;
    private long mOrderNo;//订单号
    private int mVerifyCodeStatus;//验证码状态  0正常 2异常
    private String mMobile;//手机号
    private String mTip;

    private boolean mIsModifyPhone;//避免重复请求

    private Task mTaskGetCode;
    private boolean mIsGetCode;

    private OnFinishListener mOnFinishListener;

    public OrderSendCodeUtil(Context context, OnFinishListener onFinishListener) {
        mContext = context;
        mOnFinishListener = onFinishListener;
        init();
    }

    public void setOrderInfo(long orderNo, int verifyCodeStatus, String mobile) {
        mOrderNo = orderNo;
        mVerifyCodeStatus = verifyCodeStatus;
        mMobile = mobile;
    }

    private void init() {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        if (mVerifyCodeStatus == 0) {
            mTip = "请输入您接收到的验证码";
        } else if (mVerifyCodeStatus == 2) {
            mTip = "同一个手机号只能接收3辆车的验证码！您的号码" + mMobile + "已经超过接收次数，请更换手机号：";
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://修改手机号
                    mPopupWindowUtil.showChangeMobileConfirmPop(new PopupWindowUtil.OnOrderChangeMobileListener() {
                        @Override
                        public void sendCode(CountButton countButton, String mobile) {
                            doGetCode(countButton, mobile);
                        }

                        @Override
                        public void submit(String mobile, String code) {
                            doModifyVerifyPhone(mobile, code);
                        }

                        @Override
                        public void callHotline() {
                            mHandler.sendEmptyMessageDelayed(1, 500);
                        }
                    });
                    break;
                case 1://拨打客服
                    mPopupWindowUtil.showHotLine();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void show() {
        ToastUtil.show(mTip);
        mPopupWindowUtil.showOrderSendCodeConfirmPop(mMobile, new PopupWindowUtil.OnOrderSendCodeListener() {
            @Override
            public void submit(String code) {
                submitCode(code);
            }

            @Override
            public void changeMobile() {
                mHandler.sendEmptyMessageDelayed(0, 500);
            }
        });
    }

    private boolean mIsVerifyCode;

    private void submitCode(String verifyCode) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsVerifyCode) return;
        mIsVerifyCode = true;
        InsuranceClient.doVerifyCode(mOrderNo, verifyCode).subscribe(new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {
                mIsVerifyCode = false;
            }

            @Override
            public void onError(Throwable e) {
                mIsVerifyCode = false;
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void onNext(BaseResponse response) {
                if (response.isSuccessful()) {
                    ToastUtil.show(R.string.underwrite_submit_suc);
                    mPopupWindowUtil.dismiss();
                    mOnFinishListener.onFinish();
                } else {
                    ToastUtil.show(response.msg);
                }
            }
        });
    }

    private void doGetCode(CountButton countButton, String phoneNo) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsModifyPhone) return;
        final LoadingDialog dialog = new LoadingDialog(mContext, getString(R.string.getting_code)).show();
        mTaskGetCode = UserClient.doGetCode(phoneNo, UserClient.CodeAction.ORDER_CHANGE_MOBILE, new ICallback() {
            @Override
            public void start() {

            }

            @Override
            public void success(Object data) {
                UserCodeResponse response = (UserCodeResponse) data;
                if (response.isSuccessful()) {
                    countButton.setText("", "s");
                    countButton.start(60);
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {

            }

            @Override
            public void end() {
                dialog.dissmiss();
            }
        });
    }

    private void doModifyVerifyPhone(String phoneNo, String code) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsModifyPhone) return;
        mIsModifyPhone = true;
        InsuranceClient.doModifyVerifyPhone(mOrderNo, phoneNo, code).subscribe(new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {
                mIsModifyPhone = false;
            }

            @Override
            public void onError(Throwable e) {
                mIsModifyPhone = false;
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void onNext(BaseResponse response) {
                if (response.isSuccessful()) {
                    AlertDialogView dialogView = new AlertDialogView(mContext)
                            .setContent("验证码发送会在系统消息中提示！\n请耐心等待核保验证码，24小时内当天内有效！")//
                            .setSingleBtn("知道了", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mPopupWindowUtil.dismiss();
                                    mOnFinishListener.onFinish();
                                }
                            });

                    new EffectDialogBuilder(mContext)
                            .setCancelable(false)
                            .setCancelableOnTouchOutside(false)
                            .setContentView(dialogView).show();
                } else {
                    ToastUtil.show(response.msg);
                }
            }
        });
    }

    public interface OnFinishListener {
        void onFinish();
    }
}
