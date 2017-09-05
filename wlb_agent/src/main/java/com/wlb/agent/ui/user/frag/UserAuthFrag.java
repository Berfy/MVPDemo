package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.ProveUploadingResponse;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.view.PhotoPickSheet;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.imgloader.ImageFactory;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * Created by Berfy on 2017/7/24.
 * 实名认证3.0
 */
public class UserAuthFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.edit_name)
    EditText mEditName;//姓名
    @BindView(R.id.edit_idcard)
    EditText mEditIdcard;//身份证
    @BindView(R.id.iv_idcard1)
    ImageView mFrontImg;
    @BindView(R.id.iv_idcard2)
    ImageView mVersoImg;
    @BindView(R.id.tv_hint)
    TextView mTvHint;
    @BindView(R.id.tv_hint_tip)
    TextView mTvHintTip;
    @BindView(R.id.layout_tip)
    LinearLayout mLlTip;
    @BindView(R.id.btn_submit)
    Button mBtnSubmit;

    private int mUploadType = 1;//1是上传正面，2是上传反面
    private String mFrontPath, mVersoPath;//图片地址
    private Task mUploadingTask;

    private PhotoPickSheet mPhotoPickSheet;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.user_auth,
                UserAuthFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_auth_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPhotoPickSheet = new PhotoPickSheet(this);
        UserResponse userInfo = UserClient.getLoginedUser();
        List<String> imgList = userInfo.id_auth_info.cert_url;
        mEditName.setText(userInfo.id_auth_info.real_name);
        mEditIdcard.setText(userInfo.id_auth_info.certificate_no);
        if (imgList.size() > 0) {
            mFrontPath = imgList.get(0);
            ImageFactory.getUniversalImpl().getImg(mFrontPath, mFrontImg);//设置图片
        }
        if (imgList.size() > 1) {
            mVersoPath = imgList.get(1);
            ImageFactory.getUniversalImpl().getImg(mVersoPath, mVersoImg);//设置图片
        }

        if (!TextUtils.isEmpty(userInfo.id_auth_info.tip)) {
            mTvHintTip.setText(userInfo.id_auth_info.tip);
        }
        switch (userInfo.id_auth_info.getAuthStatus()) {
            case AUTH_NOT://未认证
                mBtnSubmit.setVisibility(View.VISIBLE);
                mLlTip.setVisibility(View.GONE);
                mEditName.setEnabled(true);
                mEditIdcard.setEnabled(true);
                mFrontImg.setEnabled(true);
                mVersoImg.setEnabled(true);
                break;
            case AUTHING://认证中
                mBtnSubmit.setVisibility(View.GONE);
                mLlTip.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(userInfo.id_auth_info.tip)) {
                    mTvHintTip.setText(userInfo.id_auth_info.tip);
                } else {
                    mTvHintTip.setVisibility(View.GONE);
                }
                mTvHint.setText(R.string.user_authing);
                mEditName.setEnabled(false);
                mEditIdcard.setEnabled(false);
                mFrontImg.setEnabled(false);
                mVersoImg.setEnabled(false);
                break;
            case AUTH_FAIL://认证失败
                mBtnSubmit.setVisibility(View.VISIBLE);
                mLlTip.setVisibility(View.VISIBLE);
                mTvHint.setText(R.string.user_auth_failed);
                if (!TextUtils.isEmpty(userInfo.id_auth_info.tip)) {
                    mTvHintTip.setText(userInfo.id_auth_info.tip);
                } else {
                    mTvHintTip.setText(getString(R.string.user_auth_failed_tip));
                }
                mEditName.setEnabled(true);
                mEditIdcard.setEnabled(true);
                mFrontImg.setEnabled(true);
                mVersoImg.setEnabled(true);
                break;
            case AUTH_SUCCESS://认证成功
                mBtnSubmit.setVisibility(View.GONE);
                mLlTip.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(userInfo.id_auth_info.tip)) {
                    mTvHintTip.setText(userInfo.id_auth_info.tip);
                } else {
                    mTvHintTip.setText(getString(R.string.user_auth_suc_tip));
                }
                mEditName.setEnabled(false);
                mEditIdcard.setEnabled(false);
                mFrontImg.setEnabled(false);
                mVersoImg.setEnabled(false);
                mTvHint.setText(R.string.user_auth_suc);
                break;
        }
    }

    @OnClick({R.id.iv_idcard1, R.id.iv_idcard2, R.id.btn_submit})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_idcard1:
                mUploadType = 1;
                mPhotoPickSheet.show();
                break;
            case R.id.iv_idcard2:
                mUploadType = 2;
                mPhotoPickSheet.show();
                break;
            case R.id.btn_submit:
                String name = mEditName.getText().toString().trim();
                String idcard = mEditIdcard.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtil.show(R.string.user_auth_name_hint);
                    return;
                }
                if (TextUtils.isEmpty(idcard)) {
                    ToastUtil.show(R.string.user_auth_idcard_hint);
                    return;
                }
                if (null == mFrontPath) {
                    ToastUtil.show(R.string.user_auth_idcard_zhengmian);
                    return;
                }
                if (null == mVersoPath) {
                    ToastUtil.show(R.string.user_auth_idcard_beimian);
                    return;
                }
                JSONArray arrayData = new JSONArray();
                arrayData.put(mFrontPath);
                arrayData.put(mVersoPath);
                uploadingImg(name, idcard, arrayData);//上传一张图片可以不？？？？
                break;
        }
    }

    private boolean isUploading;

    private void uploadingImg(String name, String certificateNo, JSONArray arrays) {
        if (isUploading) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(),
                "上传中...").show();
        mUploadingTask = UserClient.doProveUploading(name, certificateNo, arrays, new ICallback() {
            @Override
            public void start() {
                isUploading = true;
            }

            @Override
            public void success(Object data) {
                ProveUploadingResponse response = (ProveUploadingResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("上传成功");
                    close();
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
                isUploading = false;
                loadingDialog.dissmiss();
            }
        });
    }

    private void refreshView(int type) {
        mPhotoPickSheet.dismiss();
        if (1 == type) {
            Bitmap bitmap = mPhotoPickSheet.getBitmap();
            if (null != bitmap) {
                mFrontImg.setImageBitmap(bitmap);
            }
            mFrontPath = mPhotoPickSheet.getUploadImg();
        } else if (2 == type) {
            Bitmap bitmap = mPhotoPickSheet.getBitmap();
            if (null != bitmap) {
                mVersoImg.setImageBitmap(bitmap);
            }
            mVersoPath = mPhotoPickSheet.getUploadImg();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPhotoPickSheet.isShowing()) {
            mPhotoPickSheet.onActivityResult(requestCode, resultCode, data);
            refreshView(mUploadType);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mUploadingTask) {
            mUploadingTask.stop();
        }
    }
}
