package com.wlb.agent.ui.user.frag;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wlb.agent.R;
import com.wlb.agent.common.share.ShareBottomSheet;
import com.wlb.agent.common.share.ShareContentListener;
import com.wlb.agent.common.share.ShareHelper;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.imgloader.ImageFactory;
import com.zxing.support.library.qrcode.QRCodeEncode;

import butterknife.BindView;
import butterknife.OnClick;
import common.Constant;
import common.share.ShareApi;
import common.share.SharePlatform;
import common.widget.dialog.loading.LoadingDialog;
import rx.functions.Action1;


/**
 * 我的二维码
 *
 * @author 张全
 */

public class QrcodeFrag extends SimpleFrag {
    @BindView(R.id.user_photo)
    public ImageView iv_userPhoto;
    @BindView(R.id.qrcode)
    public ImageView iv_qrcode;
    @BindView(R.id.qrcode_bar)
    public View qrcodeBar;
    private ShareBottomSheet shareBottomSheet;
    private Bitmap shareBitmap;

    private String url;


    public static SimpleFragAct.SimpleFragParam getStartParam() {
        SimpleFragAct.SimpleFragParam simpleFragParam = new SimpleFragAct.SimpleFragParam(null, QrcodeFrag.class);
        simpleFragParam.coverTilteBar(true);
        return simpleFragParam;
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_qrcode;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(null);
        UserResponse loginedUser = UserClient.getLoginedUser();
        if (!TextUtils.isEmpty(loginedUser.avatar)) {
            ImageFactory.getUniversalImpl().loadImage(loginedUser.avatar, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), loadedImage);
                    drawable.setCornerRadius(DeviceUtil.dip2px(mContext, 8));
                    iv_userPhoto.setImageDrawable(drawable);
                    buildQrcode();
                }
            });
        }
        url = H5.INVITE + loginedUser.token;
        buildQrcode();
        shareBottomSheet = new ShareBottomSheet(mContext);
        shareBottomSheet.addShareContentListener(new ShareContentListener() {

            @Override
            public void setShareContent(SharePlatform platform, ShareHelper shareHelper) {
                shareHelper.setOnlyImageShare(true);
                RxPermissions rxPermissions = new RxPermissions(getActivity());
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        )
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) {
                                    shareHelper.setShareImage(shareBitmap);
                                } else {
                                    ToastUtil.show(R.string.permission_failed);
                                }
                            }
                        });


            }
        });
    }

    private void buildQrcode() {
        int size = DeviceUtil.dip2px(getContext(), 230);
        QRCodeEncode.Builder builder = new QRCodeEncode.Builder();
        builder.setBackgroundColor(getColor(R.color.white))
                .setOutputBitmapWidth(size)
                .setOutputBitmapHeight(size)
                .setOutputBitmapPadding(1)
                .setCodeColor(getColor(R.color.c_00a0ea));
//                .setLogo(logo);
        LoadingDialog loadingDialog = LoadingDialog.showBackCancelableDialog(mContext, getString(R.string.loadingbar_start));
        Constant.EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.obj = builder.build().encode(url);
                mHandler.sendMessage(message);
                loadingDialog.dissmiss();
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            iv_qrcode.setImageBitmap((Bitmap) msg.obj);
        }
    };

    @OnClick({R.id.share})
    public void showShareCompanent() {
        //截图相关
        qrcodeBar.setDrawingCacheEnabled(true);//初始化截图
        qrcodeBar.buildDrawingCache();//启用DrawingCache并创建位图
        Bitmap drawingCache = qrcodeBar.getDrawingCache();
        if (null == drawingCache) {
            qrcodeBar.setVisibility(View.VISIBLE);
            return;
        }

        shareBitmap = Bitmap.createBitmap(drawingCache);
        shareBottomSheet.show();
    }

    @OnClick(R.id.iv_close)
    public void closePage() {
        close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != shareBottomSheet) {
            shareBottomSheet.release();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareApi.getInstance().onActivityResult(requestCode, resultCode, data);
    }

}
