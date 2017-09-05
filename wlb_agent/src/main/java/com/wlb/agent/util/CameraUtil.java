package com.wlb.agent.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wlb.agent.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;

public class CameraUtil implements PopupWindowUtil.OnItemClickListener {

    private Context mContext;
    private final String TAG = "CameraUtil";
    private File mTempFile;// 创建一个以当前时间为名称的文件
    public static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    public static final int PHOTO_REQUEST_CUT = 3;// 结果
    private boolean isCrop = true;
    private int aspectX = 1, aspectY = 1, outWidth = 150, outHeight = 150;

    private OnPhotoListener mOnPhotoListener;

    private PopupWindowUtil mPopupWindowUtil;
    private List<String> mItems = new ArrayList<>();

    public interface OnPhotoListener {
        void getPhotoPath(String filePath);

        void getPhotoCropData(byte[] data);
    }

    public CameraUtil(Context context, OnPhotoListener onPhotoListener) {
        mContext = context;
        mOnPhotoListener = onPhotoListener;
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mItems.add(mContext.getString(R.string.take_photo));
        mItems.add(mContext.getString(R.string.pick_photo));
    }

    public void show() {
        mPopupWindowUtil.showPopListView("", mItems, this);
    }

    public void dismiss() {
        mPopupWindowUtil.dismiss();
    }

    @Override
    public void onItemClick(int position, String item) {
        switch (position) {
            case 0:
                mPopupWindowUtil.dismiss();
                RxPermissions rxPermissions = new RxPermissions((Activity) mContext);
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        )
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) { // Always true pre-M
                                    tackPhoto();
                                } else {
                                    ToastUtil.show("照相机权限被拒绝");
                                }
                            }
                        });
                break;
            case 1:
                mPopupWindowUtil.dismiss();
                pickPhoto();
                break;
        }
    }

    public void setTempFile(File file) {
        mTempFile = file;
    }


    public File getTakePhotoFile() {
        return mTempFile;
    }

    public void setConfig(boolean isCrop, int aspectX, int aspectY, int outWidth, int outHeight) {
        this.isCrop = isCrop;
        this.aspectX = aspectX;
        this.aspectY = aspectY;
        this.outWidth = outWidth;
        this.outHeight = outHeight;
    }

    public void setUpTempFile() {
        if (null != mTempFile)
            FileUtils.deleteFile(mTempFile.getPath());
        mTempFile = FileUtils.createFile(mContext, getPhotoFileName());
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     */
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    public void tackPhoto() {
        setUpTempFile();
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(mTempFile));
        ((Activity) mContext).startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
    }

    public void pickPhoto() {
        setUpTempFile();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ((Activity) mContext).startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case CameraUtil.PHOTO_REQUEST_TAKEPHOTO:
                    if (isCrop) {
                        startPhotoZoom(Uri.fromFile(getTakePhotoFile()));
                    } else {
                        if (null != mOnPhotoListener) {
                            mOnPhotoListener.getPhotoPath(getUriToPath(Uri.fromFile(getTakePhotoFile())));
                        }
                    }
                    break;

                case CameraUtil.PHOTO_REQUEST_GALLERY:
                    if (data != null) {
                        if (isCrop) {
                            try {
                                startPhotoZoom(data.getData());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (null != mOnPhotoListener) {
                                mOnPhotoListener.getPhotoPath(getUriToPath(data.getData()));
                            }
                        }
                    }
                    break;

                case CameraUtil.PHOTO_REQUEST_CUT:
                    LogUtil.e(TAG, "裁剪结果" + data);
                    if (data != null) {
                        byte[] bytes = getDataToBytes(data);
                        if (null != bytes) {
                            if (null != mOnPhotoListener) {
                                FileUtils.createFile(mTempFile, bytes);
                                mOnPhotoListener.getPhotoCropData(bytes);
                                mOnPhotoListener.getPhotoPath(mTempFile.getPath());
                            }
                        }
                    }
                    break;
            }
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", outWidth);
        intent.putExtra("outputY", outHeight);
        intent.putExtra("return-data", true);
        ((Activity) mContext).startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private String getUriToPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ((Activity) mContext).managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(actual_image_column_index).trim();
    }

    public Uri getPathToURI(String path) {
        if (path != null) {
            path = Uri.decode(path);
            ContentResolver cr = mContext.getContentResolver();
            StringBuffer buff = new StringBuffer();
            buff.append("(")
                    .append(MediaStore.Images.ImageColumns.DATA)
                    .append("=")
                    .append("'" + path + "'")
                    .append(")");
            Cursor cur = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns._ID},
                    buff.toString(), null, null);
            int index = 0;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur
                    .moveToNext()) {
                index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                // set _id value
                index = cur.getInt(index);
            }
            if (index == 0) {
                //do nothing
            } else {
                Uri uri_temp = Uri
                        .parse("content://media/external/images/media/"
                                + index);
                if (uri_temp != null) {
                    return uri_temp;
                }
            }
        }
        return null;
    }

    // 将进行剪裁后的图片显示到UI界面上
    private byte[] getDataToBytes(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            // Constants.drawable = new BitmapDrawable(mIconBitmap);//太占用内存
            byte[] bytes = FileUtils.bitmap2Bytes((Bitmap) bundle.getParcelable("data"), 100);
            return bytes;
        } else {
            ToastUtil.show("取消了选择");
        }
        return null;
    }

    public void clearTemp() {
        FileUtils.deleteFile(mTempFile.getPath());
    }
}
