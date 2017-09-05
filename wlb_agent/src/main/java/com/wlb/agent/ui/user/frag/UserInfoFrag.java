package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.view.PhotoPickSheet;
import com.wlb.agent.ui.common.view.RoundImageView;
import com.wlb.agent.ui.main.frag.ChooseCitySimpleFrag;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.imgloader.ImageLoaderImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * Created by Berfy on 2017/7/20.
 * 用户基本资料
 */

public class UserInfoFrag extends SimpleFrag implements View.OnClickListener {

    private PhotoPickSheet mPhotoPickSheet;
    private PopupWindowUtil mPopupWindowUtil;
    private List<String> mSexItems = new ArrayList<>();
    private Task mUpdateTask;

    @BindView(R.id.iv_icon)
    RoundImageView mIvIcon;
    @BindView(R.id.tv_nick)
    TextView mTvNick;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_email)
    TextView mTvEmail;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.user_info, UserInfoFrag.class);
        return param;
    }

    public static void start(Context context) {
        getStartParam().coverTilteBar(true);
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_info_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPhotoPickSheet = new PhotoPickSheet(this);
        mPhotoPickSheet.setReqSize(150);
        mPhotoPickSheet.uploadPhoto(true);
        mPhotoPickSheet.setOnPhotoUploadListener(new PhotoPickSheet.OnPhotoUploadListener() {
            @Override
            public void uploadSuccess(View view, String localUrl, String url, Bitmap bitmap) {
                doUpdate(5, url);
                ImageLoader.getInstance().displayImage("file:///" + localUrl, mIvIcon, ImageLoaderImpl.cacheDiskOptions);
            }

            @Override
            public void local(String localUrl, Bitmap bitmap) {

            }
        });
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mSexItems.add(getString(R.string.male));
        mSexItems.add(getString(R.string.female));
        setInfo();
    }

    private void setInfo() {
        UserResponse userResponse = UserClient.getLoginedUser();
        if (null != userResponse) {
            ImageLoader.getInstance().displayImage(userResponse.avatar, mIvIcon, ImageLoaderImpl.cacheDiskOptions);
            mTvNick.setText(userResponse.nick_name);
            mTvEmail.setText(userResponse.email);
            mTvCity.setText(userResponse.city);
        }
    }

    //更新个人信息
    private boolean isUpdateUserInfo;

    /**
     * 更新资料
     *
     * @param type 1昵称 2性别 3邮箱 4地区 5头像
     */
    private void doUpdate(int type, String content) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isUpdateUserInfo) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "请稍后...");
        UserResponse userResponse = UserClient.getLoginedUser();
        switch (type) {
            case 1://昵称
                userResponse.nick_name = content;
                break;
            case 2://性别
//                userResponse.se = content;
                break;
            case 3://邮箱
                userResponse.email = content;
                break;
            case 4://地区
                userResponse.city = content;
                break;
            case 5://头像
                userResponse.avatar = content;
                break;
        }
        mUpdateTask = UserClient.doUpdateUserInfo(userResponse, new ICallback() {
            @Override
            public void success(Object data) {
                if (isDestoryed) return;
                UserResponse response = (UserResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show(R.string.user_info_uodate_suc);
                    setInfo();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isUpdateUserInfo = true;
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show("网络请求失败,请稍后重试");
            }

            @Override
            public void end() {
                isUpdateUserInfo = false;
                dialog.dissmiss();
            }
        });
    }

    @OnClick({R.id.layout_icon, R.id.layout_nick, R.id.layout_sex, R.id.layout_city, R.id.layout_email, R.id.layout_card, R.id.layout_qr})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_icon:
                mPhotoPickSheet.show();
                break;
            case R.id.layout_nick:
                UserInfoEditFrag.start(mContext, UserInfoEditFrag.TYPE_NICK, getString(R.string.user_info_nick_title),
                        getString(R.string.user_info_nick_hint), UserInfoEditFrag.TYPE_NICK);
                break;
            case R.id.layout_sex:
                mPopupWindowUtil.showPopListView("", mSexItems, new PopupWindowUtil.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, String item) {
                        mTvSex.setText(item);
                    }
                });
                break;
            case R.id.layout_city:
                ChooseCitySimpleFrag.start(mContext);
                break;
            case R.id.layout_email:
                UserInfoEditFrag.start(mContext, UserInfoEditFrag.TYPE_EMAIL, getString(R.string.user_info_nick_title),
                        getString(R.string.user_info_email_hint), UserInfoEditFrag.TYPE_EMAIL);
                break;
            case R.id.layout_card:
                UserInfoCardFrag.start(mContext);
                break;
            case R.id.layout_qr:
                QrcodeFrag.start(mContext);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPhotoPickSheet.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            switch (requestCode) {
                case UserInfoEditFrag.TYPE_NICK:
                    mTvNick.setText(data.getStringExtra(UserInfoEditFrag.PARAM_CONTENT));
                    doUpdate(1, data.getStringExtra(UserInfoEditFrag.PARAM_CONTENT));
                    break;
                case UserInfoEditFrag.TYPE_EMAIL:
                    mTvEmail.setText(data.getStringExtra(UserInfoEditFrag.PARAM_CONTENT));
                    doUpdate(3, data.getStringExtra(UserInfoEditFrag.PARAM_CONTENT));
                    break;
            }
            if (requestCode == ChooseCitySimpleFrag.REQUESTCODE) {
                mTvCity.setText(data.getStringExtra(ChooseCitySimpleFrag.PARAME_CITY));
                doUpdate(4, data.getStringExtra(ChooseCitySimpleFrag.PARAME_CITY));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mUpdateTask) {
            mUpdateTask.stop();
        }
        mPhotoPickSheet.release();
    }
}
