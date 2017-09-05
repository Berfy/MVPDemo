package com.wlb.agent.ui.user.frag;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.response.MessageResponse;
import com.wlb.agent.core.data.insurance.entity.OrderNum;
import com.wlb.agent.core.data.insurance.response.OrderNumResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.AuthStatus;
import com.wlb.agent.core.data.user.entity.WalletInfo;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.core.data.user.response.WalletResponse;
import com.wlb.agent.ui.common.view.BadgeView;
import com.wlb.agent.ui.common.view.RoundImageView;
import com.wlb.agent.ui.order.frag.OrderFrag;
import com.wlb.agent.ui.user.helper.BackEventListener;
import com.wlb.agent.ui.user.setting.frag.SettingFrag;
import com.wlb.common.SimpleFrag;
import com.wlb.common.imgloader.ImageLoaderImpl;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 我的
 *
 * @author Berfy
 */
public class UserCenterFrag2 extends SimpleFrag implements BackEventListener,
        OnClickListener {

    private BadgeView mNoticeUnReadNum;

    private boolean mIsGetMsgCount;//避免重复请求
    private Task mUnReadMsgTask;
    private boolean mIsGetOrderCount;//避免重复请求
    private Task mGetOrderCountTask;

    private BadgeView mBadgeViewStatus1;
    private BadgeView mBadgeViewStatus2;
    private BadgeView mBadgeViewStatus3;
    private BadgeView mBadgeViewStatus4;

    private WalletInfo mWalletInfo;//钱包
    private UserResponse mUserResponse;//登录用户信息

    @BindView(R.id.scrollView)//消息中心
            ScrollView mScrollView;
    @BindView(R.id.layout_left)//消息中心
            LinearLayout mLlLeft;
    @BindView(R.id.layout_order_status1)//核保中
            LinearLayout mLlStatus1;
    @BindView(R.id.layout_order_status2)//待支付
            LinearLayout mLlStatus2;
    @BindView(R.id.layout_order_status3)//出单中
            LinearLayout mLlStatus3;
    @BindView(R.id.layout_order_status4)//完成
            LinearLayout mLlStatus4;
    @BindView(R.id.btn_close_tuiguangfei)//推广费
            Button mBtnTuiguangfei;
    @BindView(R.id.tv_tuiguangfei)//推广费
            TextView mTvTuiguangfei;
    @BindView(R.id.tv_tuiguangfei_close)//推广费隐藏
            TextView mTvTuiguangfeiClose;
    @BindView(R.id.iv_user_icon)//用户头像
            RoundImageView mIvIcon;
    @BindView(R.id.tv_my_withdrawal)//可提现
            TextView mTvWithdrawal;
    @BindView(R.id.tv_my_will_can)//待审核
            TextView mTvWillCan;
    @BindView(R.id.tv_my_cumulative)//累计
            TextView mTvCumulative;
    @BindView(R.id.layout_padding)
    LinearLayout mLlPadding;//标题栏高度的padding栏，布局下移

    @Override
    protected int getLayoutId() {
        return R.layout.user_center_frag2;
    }

    @Override
    protected void init(final Bundle savedInstanceState) {
        mNoticeUnReadNum = new BadgeView(mContext);
        mNoticeUnReadNum.setTargetView(mLlLeft);
        mNoticeUnReadNum.setTextColor(Color.WHITE);
        mNoticeUnReadNum.setBackground(10, Color.RED);
        mNoticeUnReadNum.setBadgeCount(99);
        mNoticeUnReadNum.setBadgeMargin(0, 5, 5, 0);

        //订单数红点初始化
        mBadgeViewStatus1 = new BadgeView(mContext);
        mBadgeViewStatus1.setTargetView(mLlStatus1);
        mBadgeViewStatus1.setTextColor(Color.WHITE);
        mBadgeViewStatus1.setBackground(10, Color.RED);
        mBadgeViewStatus1.setBadgeMargin(0, 2, 10, 0);

        mBadgeViewStatus2 = new BadgeView(mContext);
        mBadgeViewStatus2.setTargetView(mLlStatus2);
        mBadgeViewStatus2.setTextColor(Color.WHITE);
        mBadgeViewStatus2.setBackground(10, Color.RED);
        mBadgeViewStatus2.setBadgeMargin(0, 2, 10, 0);

        mBadgeViewStatus3 = new BadgeView(mContext);
        mBadgeViewStatus3.setTargetView(mLlStatus3);
        mBadgeViewStatus3.setTextColor(Color.WHITE);
        mBadgeViewStatus3.setBackground(10, Color.RED);
        mBadgeViewStatus3.setBadgeMargin(0, 2, 10, 0);

        mBadgeViewStatus4 = new BadgeView(mContext);
        mBadgeViewStatus4.setTargetView(mLlStatus4);
        mBadgeViewStatus4.setTextColor(Color.WHITE);
        mBadgeViewStatus4.setBackground(10, Color.RED);
        mBadgeViewStatus4.setBadgeMargin(0, 2, 10, 0);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLlPadding.getLayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            layoutParams.height = DeviceUtil.getStatusBarHeight(getContext());
            mLlPadding.setLayoutParams(layoutParams);
        }

        UserResponse userResponse = UserClient.getLoginedUser();
        updateInfo(userResponse);
        updateSwitch();
//        mScrollView.setPadding(0, OsUtil.getStatusBarHeaight(getActivity()), 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        doGetUserInfo();
        doGetWalletInfo();
        updateSwitch();
        doGetUnreadMsgCount();
        doGetOrderCount();
        mUserResponse = UserClient.getLoginedUser();
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
        if (null != mUnReadMsgTask) {
            mUnReadMsgTask.stop();
        }
        if (null != mGetOrderCountTask) {
            mGetOrderCountTask.stop();
        }
    }

    @OnClick({R.id.title_left_button, R.id.title_right_button, R.id.iv_user_icon,
            R.id.layout_will_can, R.id.layout_withdrawal, R.id.layout_order, R.id.layout_order_status1,
            R.id.layout_order_status2, R.id.layout_order_status3, R.id.layout_order_status4, R.id.layout_tuiguangfei,
            R.id.tv_tuiguangfei_close, R.id.layout_wallet, R.id.layout_cumulative, R.id.layout_renzheng,
            R.id.layout_tuiguangfei_close})

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
            case R.id.layout_tuiguangfei_close:
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
                    if (null != mUserResponse.id_auth_info && mUserResponse.id_auth_info.getAuthStatus() != AuthStatus.AUTH_SUCCESS) {
                        ToastUtil.show(R.string.user_auth_tip);
                        return;
                    }
                    WalletWillCanFrag.start(mContext);
                }
                break;
            case R.id.layout_withdrawal:
                if (null != mWalletInfo)
                    if (UserClient.getLoginedUser().isUnBind()) {
                        UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                    } else {
                        if (null != mUserResponse.id_auth_info && mUserResponse.id_auth_info.getAuthStatus() != AuthStatus.AUTH_SUCCESS) {
                            ToastUtil.show(R.string.user_auth_tip);
                            return;
                        }
                        WalletWithdrawFrag.start(mContext, mWalletInfo);
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
            case R.id.layout_tuiguangfei:
            case R.id.tv_tuiguangfei_close:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.COUPON_LIST);
                } else {
                    WalletFrag.start(mContext);
                }
                break;
            case R.id.layout_renzheng:
                if (UserClient.getLoginedUser().isUnBind()) {
                    UserBindPhoneFrag.start(mContext, UserBindPhoneFrag.BindTargetPage.VERIFY);
                } else {
                    UserAuthFrag.start(mContext);
//                    VerifyFrag.start(mContext, VerifyFrag.switchProfession);
                }
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
    }

    private void updateSwitch() {
        if (UserClient.getTuifuangfei()) {
            mBtnTuiguangfei.setBackgroundResource(R.drawable.ic_converage_press);
            mTvTuiguangfei.setVisibility(View.VISIBLE);
            mTvTuiguangfeiClose.setVisibility(View.GONE);
        } else {
            mBtnTuiguangfei.setBackgroundResource(R.drawable.ic_converage);
            mTvTuiguangfei.setVisibility(View.GONE);
            mTvTuiguangfeiClose.setVisibility(View.VISIBLE);
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
                        mWalletInfo = response.walletSummary;
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

    /**
     * 未读消息
     */
    private void doGetUnreadMsgCount() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetMsgCount) {
            return;
        }
        mIsGetMsgCount = true;
        mUnReadMsgTask = AgentServiceClient.doGetUnreadMessageCount(new SimpleCallback() {
            @Override
            public void success(Object data) {
                MessageResponse response = (MessageResponse) data;
                if (response.isSuccessful()) {
                    int unreadMessageCount = response.tipCount;
                    if (unreadMessageCount > 0) {
                        mNoticeUnReadNum.setVisibility(View.VISIBLE);
                        mNoticeUnReadNum.setBadgeCount(unreadMessageCount);
                    } else {
                        mNoticeUnReadNum.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void end() {
                mIsGetMsgCount = false;
            }
        });
    }

    /**
     * 我的订单数
     */
    private void doGetOrderCount() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetOrderCount) {
            return;
        }
        mIsGetOrderCount = true;
        mGetOrderCountTask = UserClient.doGetOrderNum(new SimpleCallback() {
            @Override
            public void success(Object data) {
                OrderNumResponse response = (OrderNumResponse) data;
                if (response.isSuccessful()) {
                    if (null != response.getList()) {
                        List<OrderNum> datas = response.getList();
                        if (datas.size() > 0) {
                            for (OrderNum orderNum : datas) {
                                if (orderNum.getStatus() == 1) {
                                    mBadgeViewStatus1.setBadgeCount(orderNum.getOrderCount());
                                } else if (orderNum.getStatus() == 2) {
                                    mBadgeViewStatus2.setBadgeCount(orderNum.getOrderCount());
                                } else if (orderNum.getStatus() == 7) {
                                    mBadgeViewStatus3.setBadgeCount(orderNum.getOrderCount());
                                } else if (orderNum.getStatus() == 5) {
                                    mBadgeViewStatus4.setBadgeCount(orderNum.getOrderCount());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void end() {
                mIsGetOrderCount = false;
            }
        });
    }
}
