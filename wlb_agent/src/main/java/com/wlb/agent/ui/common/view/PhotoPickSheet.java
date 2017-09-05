package com.wlb.agent.ui.common.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.ImageView;

import com.wlb.agent.R;
import com.wlb.common.BaseFragment;

/**
 * @author 张全
 *         拍照选择弹出框
 */
public class PhotoPickSheet implements View.OnClickListener {
    private Context context;
    private BottomSheetDialog dialog;
    private UploadPhoto uploadPhoto;
    private OnPhotoUploadListener uploadListener;
    private OnOperateListener onOperateListener;
    private boolean operate;

    public PhotoPickSheet(BaseFragment frag) {
        context = frag.getContext();
        uploadPhoto = new UploadPhoto(frag);

        dialog = new BottomSheetDialog(context);
        View view = View.inflate(context, R.layout.upload_photo, null);
        view.findViewById(R.id.takePhoto).setOnClickListener(this);
        view.findViewById(R.id.pickPhoto).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!operate && null != onOperateListener) onOperateListener.onDialogCancel();
            }
        });
        dialog.setContentView(view);
    }

    public void show() {
        operate = false;
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void setReqSize(int reqSize) {
        uploadPhoto.setReqSize(reqSize);
    }

    public void uploadPhoto(boolean upload) {
        uploadPhoto.setUploadPhoto(upload);
//        if (upload) {
        uploadPhoto.setUploadListener(uploadPhotoListener);
//        } else {
//            uploadPhoto.setUploadListener(null);
//        }
    }


    /**
     * 设置图片上传类型 0头像 1通用
     *
     * @param type
     */
    public void setUploadType(int type) {
        uploadPhoto.setUploadType(type);
    }


    public Bitmap getBitmap() {
        return uploadPhoto.getBitmap();
    }

    public String getUploadImg() {
        return uploadPhoto.getUploadImg();
    }

    public void setUploadPhoto(UploadPhoto uploadPhoto) {
        this.uploadPhoto = uploadPhoto;
    }

    public UploadPhoto getUploadPhoto() {
        return uploadPhoto;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto:
                operate = true;
                if (null != onOperateListener) onOperateListener.onTakePhoto();
                else uploadPhoto.takePhotoAction();
                break;
            case R.id.pickPhoto:
                operate = true;
                if (null != onOperateListener) onOperateListener.onPickPhoto();
                else uploadPhoto.pickAlbumAction();
                break;
            case R.id.cancel:
                operate = false;
                dialog.dismiss();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        uploadPhoto.onActivityResult(requestCode, resultCode, data);
    }

    public void release() {
        uploadPhoto.release();
    }


    public void setOnOperateListener(OnOperateListener onOperateListener) {
        this.onOperateListener = onOperateListener;
    }


    public void setOnPhotoUploadListener(OnPhotoUploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    private UploadPhoto.UploadPhotoListener uploadPhotoListener = new UploadPhoto.UploadPhotoListener() {
        @Override
        public void uploadSuccess(ImageView imageView, String localUrl, String url, Bitmap bitmap) {
            if (null != uploadListener)
                uploadListener.uploadSuccess(imageView, localUrl, url, bitmap);
            if (null != dialog) {
                dialog.dismiss();
            }
        }

        @Override
        public void uploadFail(ImageView imageView, Bitmap bitmap) {
            if (null != dialog) {
                dialog.dismiss();
            }
        }

        @Override
        public void onLoadBitmap(ImageView imageView, String localUrl, Bitmap bitmap) {
            if (null != uploadListener) uploadListener.local(localUrl, bitmap);
        }
    };

    /**
     * 图片上传
     */
    public interface OnPhotoUploadListener {
        void uploadSuccess(View imageView, String localUrl, String url, Bitmap bitmap);

        void local(String localUrl, Bitmap bitmap);
    }

    /**
     * 操作
     */
    public interface OnOperateListener {
        /**
         * 拍照
         */
        void onTakePhoto();

        /**
         * 选择相册
         */
        void onPickPhoto();

        /**
         * 取消
         */
        void onDialogCancel();
    }

}
