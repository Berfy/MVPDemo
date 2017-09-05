package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.util.db.DBLog;
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
 * 上传实名认证的图片
 * <p>
 * Created by 曹泽琛.
 */

public class ProveUploadingFrag extends SimpleFrag {

    @BindView(R.id.uploading_img_front)
    ImageView mFrontImg;
    @BindView(R.id.uploading_img_verso)
    ImageView mVersoImg;
    private PhotoPickSheet photoPickSheet;
    private int type = 1;//1是上传正面，2是上传反面
    public static final String PROVE_NAME = "prove_name";
    public static final String PROVE_CERTIFICATENO = "prove_certificateNo";
    private Task doUploadingTask;
    private String saveFrontBitmap, saveVersoBitmap;
    private String name, certificateNo;

    public static void start(Context context, String name, String certificateNo) {
        Bundle bundle = new Bundle();
        bundle.putString(PROVE_NAME, name);
        bundle.putString(PROVE_CERTIFICATENO, certificateNo);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.prove_name,
                ProveUploadingFrag.class, bundle);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.prove_uploading_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        name = getArguments().getString(PROVE_NAME);
        certificateNo = getArguments().getString(PROVE_CERTIFICATENO);

        photoPickSheet = new PhotoPickSheet(this);
        photoPickSheet.setReqSize(500);

        UserResponse userInfo = UserClient.getLoginedUser();
        DBLog.log("UserInfo_toDBValue: " + userInfo.toString());
        List<String> imgList = userInfo.id_auth_info.cert_url;
        if (imgList.size() > 0) {
            saveFrontBitmap = imgList.get(0);
            saveVersoBitmap = imgList.get(1);
        }
        if (null != saveFrontBitmap)
            ImageFactory.getUniversalImpl().getImg(saveFrontBitmap, mFrontImg);//设置图片
        if (null != saveVersoBitmap)
            ImageFactory.getUniversalImpl().getImg(saveVersoBitmap, mVersoImg);
    }

    @OnClick({R.id.uploading_img_front, R.id.uploading_img_verso, R.id.prove_uploading, R.id.user_login})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploading_img_front://上传身份证正面
                type = 1;
                photoPickSheet.show();
                break;
            case R.id.uploading_img_verso://上传身份证反面
                type = 2;
                photoPickSheet.show();
                break;
            case R.id.prove_uploading://查看拍照要求
                ProveTakePhotoRequire.start(mContext);
                break;
            case R.id.user_login://确认上传
                if (null == saveFrontBitmap || null == saveVersoBitmap) {
                    ToastUtil.show("请选择上传图片");
                    return;
                }
                JSONArray arrayData = new JSONArray();
                arrayData.put(saveFrontBitmap);
                arrayData.put(saveVersoBitmap);
                uploadingImg(name, certificateNo, arrayData);//上传一张图片可以不？？？？
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
        doUploadingTask = UserClient.doProveUploading(name, certificateNo, arrays, new ICallback() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (photoPickSheet.isShowing()) {
            photoPickSheet.onActivityResult(requestCode, resultCode, data);
            refreshView(type);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        photoPickSheet.release();
        if (null != doUploadingTask) {
            doUploadingTask.stop();
        }
    }

    private void refreshView(int type) {

        photoPickSheet.dismiss();
        if (1 == type) {
            Bitmap bitmap = photoPickSheet.getBitmap();
            if (null != bitmap) {
                mFrontImg.setImageBitmap(bitmap);
            }
            saveFrontBitmap = photoPickSheet.getUploadImg();
        } else if (2 == type) {
            Bitmap bitmap = photoPickSheet.getBitmap();
            if (null != bitmap) {
                mVersoImg.setImageBitmap(bitmap);
            }
            saveVersoBitmap = photoPickSheet.getUploadImg();
        }
    }
}
