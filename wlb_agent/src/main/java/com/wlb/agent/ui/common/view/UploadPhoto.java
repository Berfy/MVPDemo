package com.wlb.agent.ui.common.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.file.SDUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.response.UploadImgReponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.PhotoResponse;
import com.wlb.agent.ui.user.helper.GetPhotoUtil;
import com.wlb.common.BaseFragment;

import net.iharder.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

import common.widget.dialog.loading.LoadingDialog;
import rx.functions.Action1;

import static com.android.util.LContext.getString;

/**
 * @author 张全
 */
public class UploadPhoto {

    private final String TAG = "UploadPhoto";
    private Activity ctx;
    private BaseFragment frag;
    private ImageView iv_photo;
    private FloatingBarListener floatingBar;
    private UploadPhotoListener listener;
    private boolean uploadPhoto;
    private int mReqSize = 700;//图片压缩大小
    private File imgFile;
    private String mLocalPath;
    private Uri mCacheImgFile;
    private Bitmap bm;
    private int mUploadType;//0头像上传1普通图片通用上传
    public static final int GET_IMG_FROM_CAMERA = 11;//拍照获取照片
    public static final int GET_IMG_FROM_PHOTO_ALBUM = 12; // 从相册获取照片
    private int mTakePhoto = -1;//自定义的相机回调
    private int mPickPhoto = -1;//自定义的相册回调
    private boolean isReleased;


    public UploadPhoto(BaseFragment frag) {
        this(frag, null);
    }

    public UploadPhoto(BaseFragment frag, FloatingBarListener floatingBar) {
        this.ctx = frag.getActivity();
        this.frag = frag;
        this.floatingBar = floatingBar;
    }

    public UploadPhoto(Activity ctx, FloatingBarListener floatingBar) {
        this.ctx = ctx;
        this.floatingBar = floatingBar;
    }

    /**
     * 设置图片压缩大小
     *
     * @param reqSize
     */
    public void setReqSize(int reqSize) {
        mReqSize = reqSize;
    }

    /**
     * 设置图片上传类型 0头像 1通用
     *
     * @param type
     */
    public void setUploadType(int type) {
        mUploadType = type;
    }

    /**
     * 从图库选择图片
     */
    public void pickAlbumAction(int requestCode) {
        RxPermissions rxPermissions = new RxPermissions(ctx);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            pickAlbum(requestCode);
                        } else {
                            if (null != photoChooser) photoChooser.onPhotoChoose(null);
                        }
                    }
                });
    }

    /**
     * 从图库选择图片
     */
    public void pickAlbumAction() {
        RxPermissions rxPermissions = new RxPermissions(ctx);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            pickAlbum();
                        } else {
                            if (null != photoChooser) photoChooser.onPhotoChoose(null);
                        }
                    }
                });
    }

    private void pickAlbum(int requestCode) {
        mPickPhoto = requestCode;
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (null != frag) {
                frag.startActivityForResult(Intent.createChooser(intent, "选择照片"),
                        mPickPhoto);
            } else {
                ctx.startActivityForResult(Intent.createChooser(intent, "选择照片"),
                        mPickPhoto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("无法打开相册");
            if (null != photoChooser) photoChooser.onPhotoChoose(null);
        }
    }

    private void pickAlbum() {
        mPickPhoto = GET_IMG_FROM_PHOTO_ALBUM;
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (null != frag) {
                frag.startActivityForResult(Intent.createChooser(intent, "选择照片"),
                        mPickPhoto);
            } else {
                ctx.startActivityForResult(Intent.createChooser(intent, "选择照片"),
                        mPickPhoto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("无法打开相册");
            if (null != photoChooser) photoChooser.onPhotoChoose(null);
        }
    }

    /**
     * 拍照
     */
    public void takePhotoAction(int requestCode) {
        mTakePhoto = requestCode;
        RxPermissions rxPermissions = new RxPermissions(ctx);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            openCamera(requestCode);
                        } else {
                            ToastUtil.show(R.string.permission_failed);
                            if (null != photoChooser) photoChooser.onPhotoChoose(null);
                        }
                    }
                });
    }

    /**
     * 拍照
     */
    public void takePhotoAction() {
        RxPermissions rxPermissions = new RxPermissions(ctx);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            openCamera();
                        } else {
                            ToastUtil.show(R.string.permission_failed);
                            if (null != photoChooser) photoChooser.onPhotoChoose(null);
                        }
                    }
                });
    }

    private void openCamera(int requestCode) {
        if (null == imgFile) {
            imgFile = createImageFile(System.currentTimeMillis() + "");
        }

        if (imgFile == null) {
            ToastUtil.show("没有sd卡，暂时不能拍照");
            if (null != photoChooser) photoChooser.onPhotoChoose(null);
            return;
        }
        try {
            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            mCacheImgFile = Uri.fromFile(imgFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mCacheImgFile = FileProvider.getUriForFile(ctx, getString(R.string.fileprovider_authority), imgFile);
                intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, mCacheImgFile);
            frag.startActivityForResult(intentFromCapture, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("无法调用照相机");
            if (null != photoChooser) photoChooser.onPhotoChoose(null);
        }
    }

    private void openCamera() {
        LogUtil.e(TAG, "openCamera");
        mTakePhoto = GET_IMG_FROM_CAMERA;
        if (null == imgFile) {
            imgFile = createImageFile(System.currentTimeMillis() + "");
        }

        if (imgFile == null) {
            ToastUtil.show("没有sd卡，暂时不能拍照");
            if (null != photoChooser) photoChooser.onPhotoChoose(null);
            return;
        }
        try {
            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mCacheImgFile = Uri.fromFile(imgFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mCacheImgFile = FileProvider.getUriForFile(ctx, getString(R.string.fileprovider_authority), imgFile);
                intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                intentFromCapture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                ClipData clip =
//                        ClipData.newUri(ctx.getContentResolver(), "A photo", contentUri);
//                intentFromCapture.setClipData(clip);
//                intentFromCapture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            } else {
//                List<ResolveInfo> resInfoList =
//                        ctx.getPackageManager()
//                                .queryIntentActivities(intentFromCapture, PackageManager.MATCH_DEFAULT_ONLY);
//                for (ResolveInfo resolveInfo : resInfoList) {
//                    String packageName = resolveInfo.activityInfo.packageName;
//                    ctx.grantUriPermission(packageName, contentUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                }


            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, mCacheImgFile);
            frag.startActivityForResult(intentFromCapture, mTakePhoto);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("无法调用照相机");
            if (null != photoChooser) photoChooser.onPhotoChoose(null);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mTakePhoto) {
                LogUtil.e(TAG, "mTakePhoto" + mCacheImgFile);
                compressImg(true);
            } else if (requestCode == mPickPhoto) {
                LogUtil.e(TAG, "mPickPhoto" + mCacheImgFile);
                if (null == data) {
                    if (null != photoChooser) photoChooser.onPhotoChoose(null);
                    return;
                }
                mCacheImgFile = data.getData();
                compressImg(false);
            }
        } else {
            if (null != photoChooser) photoChooser.onPhotoChoose(null);
        }
    }

    private static File createImageFile(String fileName) {
        if (!SDUtil.isSDValiable()) {
            return null;
        }
        try {

            File fileDir = null;
            File externalDataDir = LContext.getContext().getExternalCacheDir();
            fileDir = new File(externalDataDir,
                    "app" + File.separator + "photos");

            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File[] files = fileDir.listFiles();
            if (null != files) {
                for (File item : files) {
                    if (null != item && item.exists()) {
                        item.delete();
                    }
                }
            }
            StringBuffer pathBuff = new StringBuffer(fileDir.getCanonicalPath());
            pathBuff.append(File.separatorChar).append(fileName + ".jpg");
            File imageFile = new File(pathBuff.toString());
            if (imageFile.exists() && imageFile.canRead()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩图片
     */
    private void compressImg(boolean isTakePhoto) {
        LogUtil.e(TAG, "compressImg");
        if (null == mCacheImgFile) {
            ToastUtil.show("照片获取失败");
            return;
        }
        if (null == imgFile) {
            imgFile = createImageFile(System.currentTimeMillis() + "");
        }
        if (imgFile == null) {
            ToastUtil.show("没有sd卡，暂时不能拍照");
            return;
        }
        LogUtil.e(TAG, "文件大小" + imgFile.getAbsolutePath() + "   " + imgFile.length());
        mLocalPath = imgFile.getAbsolutePath();
        if (isTakePhoto) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bm = GetPhotoUtil.resizeBitmap(imgFile.getAbsolutePath(), mReqSize);
            } else {
                bm = GetPhotoUtil.resizeBitmap(ctx, mCacheImgFile, mReqSize);
            }
        } else {
            bm = GetPhotoUtil.resizeBitmap(ctx, mCacheImgFile, mReqSize);
        }

        if (null != bm) {
            GetPhotoUtil.saveBitmap2File(bm, imgFile);
        }
        if (null != photoChooser) {
            photoChooser.onPhotoChoose(imgFile.getAbsolutePath());
        }
        if (bm == null) {
            ToastUtil.show("获取图片失败");
            return;
        }

        if (null != listener) {
            listener.onLoadBitmap(iv_photo, mLocalPath, bm);
        }
        if (uploadPhoto) {
            // 上传图片
            doUpload(bm);
        } else {
            if (null != floatingBar) floatingBar.hide();
        }
    }

    public boolean checkSaveImg() {
        if (null == mCacheImgFile) {
            ToastUtil.show("照片获取失败");
            return false;
        }
        if (null == imgFile) {
            imgFile = createImageFile(System.currentTimeMillis() + "");
        }
        if (imgFile == null) {
            ToastUtil.show("没有sd卡");
            return false;
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        bm = GetPhotoUtil.resizeBitmap(imgFile.getAbsolutePath(), mReqSize);
//        } else {
//            bm = GetPhotoUtil.resizeBitmap(ctx, mCacheImgFile, mReqSize);
//        }
        if (bm == null) {
            ToastUtil.show("获取图片失败");
            return false;
        }
        boolean handled = GetPhotoUtil.saveBitmap2File(bm, imgFile);
//        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//        System.out.println("bitmap=" + bitmap + ",bw=" + bitmap.getWidth() + ",bh=" + bitmap.getHeight());
        return handled;
    }

    public String getUploadImg() {
        return getImg(bm);
    }

    public Bitmap getBitmap() {
        return bm;
    }

    public static String getImg(Bitmap bitmap) {
        try {
            byte[] bytes = getBytes(bitmap);
            if (null != bytes) {
                return Base64.encodeBytes(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deletePhoto(Context ctx, Uri targetUri) {
        if (null == ctx || targetUri == null) return;
        try {
            ctx.getContentResolver().delete(targetUri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void recycleBitmap(Bitmap bm) {
        try {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bm = null;
        }
    }

    public void reset() {
        if (null != imgFile) {
            imgFile.delete();
        }
        imgFile = null;
        mCacheImgFile = null;
        recycleBitmap(bm);
    }

    /**
     * 解决拍照后在相册中找不到的问题
     */
    private static Uri addImageGallery(Context ctx, File file) {
        if (null == file) return null;
        try {

            String filePath = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), file.getAbsolutePath(), file.getName(), "");
            return Uri.parse(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void release() {
        isReleased = true;
        reset();
        if (null != uploadTask) {
            uploadTask.stop();
        }
        listener = null;
    }

    // ------------------------上传图片
    private Task uploadTask;
    private boolean isUpload;

    private void doUpload(final Bitmap bm) {
        if (isUpload) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(ctx)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        String img = getImg(bm);
        if (TextUtils.isEmpty(img)) {
            ToastUtil.show("请上传照片");
            return;
        }
        final LoadingDialog dialog = new LoadingDialog(ctx, "图片上传中...").show();
        switch (mUploadType) {
            case 0:
                uploadTask = UserClient.doUploadPhoto(img, new ICallback() {

                    @Override
                    public void success(Object data) {
                        if (isReleased) {
                            return;
                        }
                        PhotoResponse response = (PhotoResponse) data;
                        if (response.isSuccessful()) {
                            String imgUrl = response.imgUrl;
                            if (null != listener) {
                                listener.uploadSuccess(iv_photo, mLocalPath, imgUrl, bm);
                            }
                            if (null != iv_photo) {
                                iv_photo.setImageBitmap(bm);
                            }
                            if (null != floatingBar) {
                                floatingBar.hide();
                            }
                        } else {
                            uploadFail();
                        }
                    }

                    @Override
                    public void start() {
                        isUpload = true;
                    }

                    @Override
                    public void failure(NetException e) {
                        uploadFail();
                    }

                    @Override
                    public void end() {
                        isUpload = false;
                        dialog.dissmiss();
                    }
                });
                break;
            case 1:
                uploadTask = AgentServiceClient.doUploadFile(img, new ICallback() {
                    @Override
                    public void start() {
                        isUpload = true;
                    }

                    @Override
                    public void success(Object data) {
                        if (isReleased) {
                            return;
                        }
                        UploadImgReponse response = (UploadImgReponse) data;
                        if (response.isSuccessful()) {
                            String imgUrl = response.getImgUrls().get(0);
                            if (null != listener) {
                                listener.uploadSuccess(iv_photo, mLocalPath, imgUrl, bm);
                            }
                            if (null != iv_photo) {
                                iv_photo.setImageBitmap(bm);
                            }
                            if (null != floatingBar) {
                                floatingBar.hide();
                            }
                        } else {
                            uploadFail();
                        }
                    }

                    @Override
                    public void failure(NetException e) {
                        uploadFail();
                    }

                    @Override
                    public void end() {
                        isUpload = false;
                        dialog.dissmiss();
                    }
                });
                break;
        }
    }

    private void uploadFail() {
        ToastUtil.show("上传失败");
        if (null != listener) {
            listener.uploadFail(iv_photo, bm);
        }
    }

    public interface UploadPhotoListener {
        void uploadSuccess(ImageView imageView, String localUrl, String url, Bitmap bitmap);

        void uploadFail(ImageView imageView, Bitmap bitmap);

        void onLoadBitmap(ImageView imageView, String localUrl, Bitmap bitmap);
    }

    public interface FloatingBarListener {

        void show();

        void hide();
    }


    public void setImageView(ImageView imageView) {
        this.iv_photo = imageView;
    }

    public void setUploadPhoto(boolean uploadPhoto) {
        this.uploadPhoto = uploadPhoto;
    }

    public void setUploadListener(UploadPhotoListener listener) {
        this.listener = listener;
    }

    //-------------WebView FileChooser--------------------
    public void setPhotoChooser(PhotoChooser photoChooser) {
        this.photoChooser = photoChooser;
    }

    public static interface PhotoChooser {
        void onPhotoChoose(String localPath);
    }

    private PhotoChooser photoChooser;
}
