package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.CardInfoResponse;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.view.RoundImageView;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.imgloader.ImageLoaderImpl;

import butterknife.BindView;

/**
 * Created by Berfy on 2017/7/20.
 * 我的名片
 */

public class UserInfoCardFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.iv_icon)
    RoundImageView mIvIcon;
    @BindView(R.id.tv_nick)
    TextView mTvNick;
    @BindView(R.id.tv_name)
    EditText mEditName;
    @BindView(R.id.tv_mobile)
    EditText mEditMobile;
    @BindView(R.id.tv_wx)
    EditText mEditWX;
    @BindView(R.id.tv_qq)
    EditText mEditQQ;
    @BindView(R.id.tv_company)
    EditText mEditCom;
    @BindView(R.id.edit_intro)
    EditText mEditIntro;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.user_card, UserInfoCardFrag.class);
        return param;
    }

    public static void start(Context context) {
        getStartParam().coverTilteBar(true);
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_info_card_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        UserResponse response = UserClient.getLoginedUser();
        mTvNick.setText(response.nick_name);
        ImageLoader.getInstance().displayImage(response.avatar, mIvIcon, ImageLoaderImpl.cacheDiskOptions);
        getCardInfo();
    }

    private boolean mIsGetInfo;
    private Task mGetCardTask;

    public void getCardInfo() {
        if (!NetworkUtil.isNetworkAvailable(mView.getContext())) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetInfo) return;
        mGetCardTask = UserClient.doGetCardInfo(new ICallback() {
            @Override
            public void start() {
                mIsGetInfo = true;
            }

            @Override
            public void success(Object data) {
                CardInfoResponse response = (CardInfoResponse) data;
                if (response.isSuccessful()) {
                    mEditName.setText(response.avatar);
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
                mIsGetInfo = false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mGetCardTask) {
            mGetCardTask.stop();
        }
    }
}
