package com.wlb.agent.ui.user.frag;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.core.data.user.response.WalletResponse;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.common.view.BadgeView;
import com.wlb.agent.ui.common.view.RoundImageView;
import com.wlb.agent.ui.order.frag.OrderFrag;
import com.wlb.agent.ui.user.helper.BackEventListener;
import com.wlb.agent.ui.user.setting.frag.SettingFrag;
import com.wlb.common.SimpleFrag;
import com.wlb.common.imgloader.ImageLoaderImpl;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.scaleable.ScaleableTextView;

import static com.wlb.agent.R.id.tv_tuiguangfei;


/**
 * 我的
 *
 * @author Berfy
 */
public class UserCenterFrag1 extends SimpleFrag implements BackEventListener,
        OnClickListener {

    private BadgeView mNoticeUnReadNum;

    @BindView(R.id.layout_left)//消息中心
            LinearLayout mLlLeft;
    @BindView(R.id.tv_tuiguangfei_switch)//推广费
            ScaleableTextView mBtnTuiguangfei;
    @BindView(R.id.iv_user_icon)//用户头像
            RoundImageView mIvIcon;
    @BindView(tv_tuiguangfei)//推广费
            TextView mTvTuiguangfei;
    @BindView(R.id.tv_tuiguangfei_close)//推广费隐藏
            TextView mTvTuiguangfeiClose;
    @BindView(R.id.tv_my_withdrawal)//可提现
            TextView mTvWithdrawal;
    @BindView(R.id.tv_my_will_can)//待审核
            TextView mTvWillCan;
    @BindView(R.id.tv_my_cumulative)//累计
            TextView mTvCumulative;
    @BindView(R.id.layout_score)//积分位置
            LinearLayout mLlScore;//积分
    BadgeView mBadgeViewScore;//积分红点

    @Override
    protected int getLayoutId() {
        return R.layout.user_center_frag1;
    }

    @Override
    protected void init(final Bundle savedInstanceState) {
        mBadgeViewScore = new BadgeView(mContext);
        mBadgeViewScore.setTargetView(mLlScore);
        mBadgeViewScore.setBadgeMargin(0, 5, 5, 0);

        mNoticeUnReadNum = new BadgeView(mContext);
        mNoticeUnReadNum.setTargetView(mLlLeft);
        mNoticeUnReadNum.setTextColor(Color.WHITE);
        mNoticeUnReadNum.setBackground(10, Color.RED);
        mNoticeUnReadNum.setBadgeCount(99);
        mNoticeUnReadNum.setBadgeMargin(0, 5, 5, 0);

        UserResponse userResponse = UserClient.getLoginedUser();
        updateInfo(userResponse);
        updateSwitch();
    }

    @Override
    public void onResume() {
        super.onResume();
        doGetUserInfo();
        doGetWalletInfo();
        updateSwitch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != logoutTask) {
            logoutTask.stop();
        }
        if (null != getUserInfoTask) {
            getUserInfoTask.stop();
        }
        if (null != getWalletTask) {
            getWalletTask.stop();
        }
    }

    @OnClick({R.id.title_left_button, R.id.title_right_button, R.id.tv_tuiguangfei_switch, R.id.tv_tuiguangfei,
            R.id.iv_user_icon, R.id.layout_will_can,
            R.id.layout_withdrawal, R.id.layout_order, R.id.layout_order_status1, R.id.layout_order_status2,
            R.id.layout_order_status3, R.id.layout_order_status4, R.id.layout_tuiguangfei, R.id.layout_wallet,
            R.id.layout_cumulative, R.id.layout_youhui, R.id.layout_renzheng,
            R.id.layout_baojia, R.id.layout_jifen, R.id.layout_xinshou})

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_button:
                UserResponse info = UserClient.getLoginedUser();
                if (null != info) {
                    UserNotifyFrag.start(mContext);
                } else {
                    UserLoginFrag.start(mContext, UserLoginFrag.LoginTargetPage.MESSAGE, null);
                }
                break;
            case R.id.title_right_button:
                SettingFrag.start(mContext);
                break;
            case R.id.tv_tuiguangfei_switch:
                UserClient.saveTuifuangfei(!UserClient.getTuifuangfei());
                updateSwitch();
                break;
            case R.id.iv_user_icon:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    UserInfoFrag.start(mContext);
                }
                break;
            case R.id.layout_will_can:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    WalletWillCanFrag.start(mContext);
                }
                break;
            case R.id.layout_withdrawal:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    WithdrawalsFrag.start(mContext);
                }
                break;
            case R.id.layout_order:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    OrderFrag.start(mContext, 4);
                }
                break;
            case R.id.layout_order_status1:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    OrderFrag.start(mContext, 0);
                }
                break;
            case R.id.layout_order_status2:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    OrderFrag.start(mContext, 1);
                }
                break;
            case R.id.layout_order_status3:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    OrderFrag.start(mContext, 2);
                }
                break;
            case R.id.layout_order_status4:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    OrderFrag.start(mContext, 3);
                }
                break;
            case R.id.layout_wallet:
            case R.id.layout_cumulative:
            case R.id.tv_tuiguangfei:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    WalletFrag.start(mContext);
                }
                break;
            case R.id.layout_baojia:
                ToastUtil.show(R.string.test);
//                OfferRecordListFrag.start(mContext);
                break;
            case R.id.layout_youhui:
                ToastUtil.show(R.string.test);
//                if (OfferClient.getLoginedUser().isUnBind()) {
//                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
//                } else {
//                    UserCouponFrag.start(mContext);
//                }
                break;
            case R.id.layout_renzheng:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.VERIFY);
                } else {
                    UserAuthFrag.start(mContext);
//                    VerifyFrag.start(mContext, VerifyFrag.switchProfession);
                }
                break;
            case R.id.layout_xinshou:
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.url = H5.USER_GUIDE;
                webViewParam.title = getString(R.string.my_new_user_help);
                WebViewFrag.start(mContext, webViewParam);
                break;
            case R.id.layout_jifen:
                ToastUtil.show(R.string.test);
//                UserScoreFrag.start(mContext);
                break;
        }
    }

    @Override
    public boolean handleBackEvent() {
        return false;
    }

    //---------------------------------------------------------
    private Task getUserInfoTask;
    private Task getWalletTask;
    private Task logoutTask;
    private boolean isGetUserInfo;
    private boolean isGetWalletInfo;
    private boolean isLogout;

    /**
     * 获取用户资料
     */
    private void doGetUserInfo() {
        if (!NetworkUtil.isNetworkAvailable(LContext.getContext())) {
            return;
        }
        if (isGetUserInfo) {
            return;
        }
        isGetUserInfo = true;
        getUserInfoTask = UserClient.doGetUserInfo(new SimpleCallback() {
            @Override
            public void success(Object data) {
                UserResponse response = (UserResponse) data;
                if (response.isSuccessful()) {
                    updateInfo(response);
                }
            }

            @Override
            public void end() {
                isGetUserInfo = false;
            }
        });
    }

    private void updateInfo(UserResponse userResponse) {
        ImageLoader.getInstance().displayImage(userResponse.avatar, mIvIcon, ImageLoaderImpl.cacheDiskOptions);
        mBadgeViewScore.setBadgeCount(userResponse.score);
    }

    private void updateSwitch() {
        if (UserClient.getTuifuangfei()) {
            mBtnTuiguangfei.setBackgroundResource(R.drawable.ic_user_tuiguangfei_open);
            mTvTuiguangfeiClose.setVisibility(View.GONE);
            mTvTuiguangfei.setVisibility(View.VISIBLE);
        } else {
            mBtnTuiguangfei.setBackgroundResource(R.drawable.ic_user_tuiguangfei_close);
            mTvTuiguangfeiClose.setVisibility(View.VISIBLE);
            mTvTuiguangfei.setVisibility(View.GONE);
        }
    }

    /**
     * 获取钱包信息
     */
    private void doGetWalletInfo() {
        if (!NetworkUtil.isNetworkAvailable(LContext.getContext())) {
            return;
        }
        if (isGetWalletInfo) {
            return;
        }
        isGetWalletInfo = true;
        getWalletTask = UserClient.doGetWallet(new SimpleCallback() {
            @Override
            public void success(Object data) {
                WalletResponse response = (WalletResponse) data;
                if (response.isSuccessful()) {
                    if (null != response.walletSummary) {
                        mTvTuiguangfei.setText(response.walletSummary.curren_balance + "");
                        mTvWithdrawal.setText(response.walletSummary.available_money + "");
                        mTvWillCan.setText(response.walletSummary.processing_money + "");
                        mTvCumulative.setText(response.walletSummary.total_draw + "");
                    }
                }
            }

            @Override
            public void end() {
                isGetWalletInfo = false;
            }
        });
    }
}
