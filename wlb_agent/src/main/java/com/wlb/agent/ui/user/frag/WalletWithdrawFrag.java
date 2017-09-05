package com.wlb.agent.ui.user.frag;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.ext.SoftInputUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.keyboard.SoftKeyboardUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.BankCardInfo;
import com.wlb.agent.core.data.user.entity.WalletInfo;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.core.data.user.response.WalletResponse;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.view.CountButton;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.imgloader.ImageFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * Created by JiaHongYa
 */

public class WalletWithdrawFrag extends SimpleFrag implements View.OnClickListener {

    private static final String WITHDRAW_PARAM = "withdraw_param";

    @BindView(R.id.withdraws_money_hint)
    TextView mTvWithdrawMoneyHint;
    @BindView(R.id.withdraws_money)
    EditText mEditWithdrawMoney;
    @BindView(R.id.layout_tip)
    LinearLayout mLlTip;
    @BindView(R.id.layout_input)
    LinearLayout mLlInput;
    @BindView(R.id.btn_code)
    CountButton btn_code;
    @BindView(R.id.withdraw_code)
    EditText mEditWithdrawCode;
    @BindView(R.id.iv_bank_card_icon)
    ImageView mIvBankIcon;
    @BindView(R.id.tv_bank_card)
    TextView mTvBankCard;
    @BindView(R.id.tv_bank_card_no)
    TextView mTvBankCardNo;
    @BindView(R.id.layout_no_bank)
    RelativeLayout mRlNoBank;
    @BindView(R.id.layout_bank)
    LinearLayout mLlBank;
    @BindView(R.id.layout_code)
    LinearLayout mLlCode;
    private Task mTaskWithdraw;
    private Task mTaskCode;
    private boolean mIsGetWalletInfo;
    private Task mGetWalletTask;

    private int mKeyBoardHeight;// 键盘高度
    private double mPrice;
    private WalletInfo walletInfo;
    private SoftKeyboardUtil mSoftKeyboardUtil;
    private PopupWindowUtil mPopupWindowUtil;
    private DisplayImageOptions mOptions = ImageFactory.getImageOptions(R.drawable.bank_itom);

    public static void start(Context context, WalletInfo walletInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(WITHDRAW_PARAM, walletInfo);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.wallet_get_rmb, WalletWithdrawFrag.class, bundle);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.wallet_withdraw_frag;
    }

    @Override
    public void onResume() {
        super.onResume();
        //银行卡信息
        doGetWalletInfo();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        walletInfo = (WalletInfo) getArguments().getSerializable(WITHDRAW_PARAM);
        if (null != walletInfo)
            if (walletInfo.available_money >= 0) {
                String moneyMessage = getString(R.string.wallet_withdrawal_withdrawal_bank_to_hint)
                        + com.wlb.agent.util.StringUtil.subZeroAndDot(walletInfo.available_money);
                mTvWithdrawMoneyHint.setText(moneyMessage);
            }
        addAction(Constant.IntentAction.WALLET_UPDATE);
        //监测键盘弹起
        mSoftKeyboardUtil = new SoftKeyboardUtil((Activity) mContext, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible) {
                if (mKeyBoardHeight != softKeybardHeight) {
                    if (mEditWithdrawCode.hasFocus() && mPrice > 0) {
                        return;
                    }
                    mKeyBoardHeight = softKeybardHeight;
                    if (visible) {
                        LogUtil.e(TAG, "键盘弹起");
                        mLlTip.setVisibility(View.GONE);
                        mLlInput.setVisibility(View.VISIBLE);
                        mLlCode.setVisibility(View.VISIBLE);
                        mEditWithdrawMoney.requestFocus();
                    } else {
                        LogUtil.e(TAG, "键盘落下");
                        if (mPrice > 0) {
                            mLlTip.setVisibility(View.GONE);
                            mLlInput.setVisibility(View.VISIBLE);
                            mLlCode.setVisibility(View.VISIBLE);
                        } else {
                            mLlTip.setVisibility(View.VISIBLE);
                            mLlInput.setVisibility(View.GONE);
                            mLlCode.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
//        mKeyBoard.setOnKeyboardStateChangedListener(new KeyboardLayout.IOnKeyboardStateChangedListener() {
//            @Override
//            public void onKeyboardStateChanged(int state) {
//                if (mEditWithdrawCode.hasFocus() && mPrice > 0) {
//                    return;
//                }
//                if (state == KeyboardLayout.KEYBOARD_STATE_SHOW) {//展开
//                    mLlTip.setVisibility(View.GONE);
//                    mLlInput.setVisibility(View.VISIBLE);
//                    mLlCode.setVisibility(View.VISIBLE);
//                    mEditWithdrawMoney.requestFocus();
//                } else {
//                    if (mPrice > 0) {
//                        mLlTip.setVisibility(View.GONE);
//                        mLlInput.setVisibility(View.VISIBLE);
//                        mLlCode.setVisibility(View.VISIBLE);
//                    } else {
//                        mLlTip.setVisibility(View.VISIBLE);
//                        mLlInput.setVisibility(View.GONE);
//                        mLlCode.setVisibility(View.GONE);
//                    }
//                }
//            }
//        });

        mEditWithdrawMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (TextUtils.isEmpty(s)) {
                        mPrice = 0;
                        return;
                    }
                    mPrice = Double.valueOf(s.toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditWithdrawCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        mTvWithdrawMoneyHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftInputUtil.showSoftInput(mEditWithdrawMoney, mContext);
            }
        });
    }

    /**
     * 获取钱包信息
     */
    private void doGetWalletInfo() {
        if (!NetworkUtil.isNetworkAvailable(LContext.getContext())) {
            return;
        }
        if (mIsGetWalletInfo) {
            return;
        }
        mIsGetWalletInfo = true;
        mGetWalletTask = UserClient.doGetWallet(new SimpleCallback() {
            @Override
            public void success(Object data) {
                WalletResponse response = (WalletResponse) data;
                if (response.isSuccessful()) {
                    if (null != response.walletSummary) {
                        walletInfo = response.walletSummary;
                        setDefCarInfo();
                    }
                }
            }

            @Override
            public void end() {
                mIsGetWalletInfo = false;
            }
        });
    }

    private void setDefCarInfo() {
        if (null != walletInfo) {
            if (null == walletInfo.def_draw_bank) {
                //显示添加银行卡布局
                mRlNoBank.setVisibility(View.VISIBLE);
                mLlBank.setVisibility(View.GONE);
                return;
            }
            mRlNoBank.setVisibility(View.GONE);
            mLlBank.setVisibility(View.VISIBLE);
            //显示银行卡布局
            String bankNo = walletInfo.def_draw_bank.bank_no;//银行卡号
            if (!TextUtils.isEmpty(bankNo)) {
                String bankName = walletInfo.def_draw_bank.bank_name;//银行名称
                String lastBankNo = bankNo.substring(bankNo.length() - 4);//银行卡后四位
                bankNo = getString(R.string.wallet_withdrawal_withdrawal_bank_weihao) + lastBankNo;
                mTvBankCard.setText(bankName);
                mTvBankCardNo.setText(bankNo);

                //银行logo
                if (!TextUtils.isEmpty(walletInfo.def_draw_bank.bank_logo)) {
                    ImageFactory.getUniversalImpl().getImg(walletInfo.def_draw_bank.bank_logo, mIvBankIcon, null, mOptions);
                } else {
                    mIvBankIcon.setImageResource(R.drawable.bank_itom);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (containsAction(action)) {
            if (action.equalsIgnoreCase(Constant.IntentAction.WALLET_UPDATE)) {
                if (null != walletInfo) {
                    BankCardInfo bankData = (BankCardInfo) event.getData();
                    walletInfo.def_draw_bank = bankData;
                    setDefCarInfo();
                }
            }
        }
    }

    @OnClick({R.id.bank_card_banner, R.id.all_withdraws, R.id.user_withdraw, R.id.btn_code, R.id.no_code})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bank_card_banner://选择银行卡
                BankCardListFrag.start(mContext, true);
                break;
            case R.id.all_withdraws://全部提现选项
                if (null != walletInfo) {
                    if (walletInfo.available_money > 0) {
                        String money = String.valueOf(walletInfo.available_money);
                        SoftInputUtil.showSoftInput(mEditWithdrawMoney, getContext());
                        mEditWithdrawMoney.requestFocus();
                        mEditWithdrawMoney.setText(money);
                        mEditWithdrawMoney.setSelection(money.length());
                    }
                }
                break;
            case R.id.user_withdraw://提现
                if (null != walletInfo) {
                    if (null == walletInfo.def_draw_bank) {
                        ToastUtil.show(R.string.wallet_withdrawal_bank_null_choose_tip);
                        return;
                    }

                    if (mPrice == 0) {
                        ToastUtil.show(R.string.wallet_withdrawal_price_null_tip);
                        return;
                    }
                    if (mPrice > walletInfo.available_money) {
                        ToastUtil.show(R.string.wallet_withdrawal_price_low_tip);
                        return;
                    }

                    String code = StringUtil.trim(mEditWithdrawCode);
                    if (TextUtils.isEmpty(code)) {
                        ToastUtil.show(R.string.login_tip_code_null);
                        return;
                    }

                    doWithdraw(code, walletInfo.def_draw_bank.bank_id);
                }
                break;
            case R.id.btn_code://获取验证码
                if (null != walletInfo) {
                    if (null == walletInfo.def_draw_bank) {
                        ToastUtil.show(R.string.wallet_withdrawal_bank_null_choose_tip);
                        return;
                    }
                    if (mPrice == 0) {
                        ToastUtil.show(R.string.wallet_withdrawal_price_null_tip);
                        return;
                    }
                    if (mPrice > walletInfo.available_money) {
                        ToastUtil.show(R.string.wallet_withdrawal_price_low_tip);
                        return;
                    }
                    UserResponse loginedUser = UserClient.getLoginedUser();
                    if (!TextUtils.isEmpty(loginedUser.phone)) {
                        getWithdrawCode(loginedUser.phone);
                    }
                }
                break;
            case R.id.no_code://收不到验证码 联系客服
                mPopupWindowUtil.showHotLine();
                break;
        }

    }

    //提现
    private boolean isWithdraw;

    private void doWithdraw(String verificationCode, long bankId) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        if (isWithdraw) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "提现申请提交中");
        mTaskWithdraw = UserClient.doWithDrawMoneny(mPrice, verificationCode, bankId, new ICallback() {
            @Override
            public void start() {
                isWithdraw = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("提现申请完成，预计三个工作日到账");
                    close();
                } else {
                    showToastShort(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isWithdraw = false;
                dialog.dissmiss();
            }
        });
    }

    private boolean isGetCode;

    private void getWithdrawCode(String phone) {
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "正在获取验证码");

        if (isGetCode) {
            return;
        }
        mTaskCode = UserClient.doGetCode(phone, UserClient.CodeAction.WITHDRAW, new ICallback() {
            @Override
            public void start() {
                isGetCode = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("验证码已发出");
                    btn_code.setText("", "s");
                    btn_code.start(60);
                    showFocus(mEditWithdrawCode);
                } else {
                    showToastShort(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                showToastShort("网络请求超时，请重新获取验证码。");
            }

            @Override
            public void end() {
                isGetCode = false;
                dialog.dissmiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mGetWalletTask)
            mGetWalletTask.stop();
        if (null != mTaskWithdraw) {
            mTaskWithdraw.stop();
        }
        if (null != mTaskCode) {
            mTaskCode.stop();
        }
    }

    private void showFocus(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
