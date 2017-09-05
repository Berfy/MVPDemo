package com.wlb.agent.ui.offer.frag;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.view.PhotoPickSheet;
import com.wlb.agent.ui.user.helper.GetPhotoUtil;
import com.wlb.agent.ui.common.view.UploadPhoto;
import com.wlb.agent.ui.user.helper.UriUtil;
import com.wlb.agent.ui.user.helper.ocr.ACameraActivity;
import com.wlb.agent.ui.user.helper.ocr.OcrConstant;
import com.wlb.agent.ui.user.helper.ocr.parser.DataParser;
import com.wlb.agent.ui.user.helper.ocr.parser.DrivingLicense;
import com.wlb.agent.ui.user.helper.ocr.utils.HttpUtil;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.ActivityManager;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.imgloader.ImageLoaderImpl;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;
import rx.functions.Action1;

/**
 * 确认车辆信息
 *
 * @author Berfy
 */
public class ComfirmCarInfoFrag extends SimpleFrag implements View.OnClickListener {

    private PopupWindowUtil mPopupWindowUtil;

    @BindView(R.id.iv_driving_info)
    ImageView mIvDrivingInfo;
    @BindView(R.id.tv_bland)
    TextView mTvBrand;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.edit_people)
    TextView mTvPeople;
    @BindView(R.id.tv_car_num)
    TextView mTvCarNum;
    @BindView(R.id.tv_vin)
    TextView mTvVin;
    @BindView(R.id.tv_engine_number)
    TextView mTvEngineNumber;

    private String extension;
    private final int OCR_IMPORT_PHOTO = 52;
    private byte[] imgBytes;
    private PhotoPickSheet ocrPickView;
    private MyAsynTask ocrTask;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.confirm_car_info, ComfirmCarInfoFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.offer_confirm_car_info_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ActivityManager.getInstance().pushActivity("offer", getActivity());
        mPopupWindowUtil = new PopupWindowUtil(mContext);
    }

    @OnClick({R.id.btn_help, R.id.layout_bland, R.id.tv_date, R.id.layout_camera, R.id.btn_submit})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_help:
                mPopupWindowUtil.showConfirmCarInfoHelp();
                break;
            case R.id.layout_camera:
                showOcrPickView();
                break;
            case R.id.layout_bland:
                SimpleFragAct.start(mContext, new SimpleFragAct.SimpleFragParam(R.string.confirm_car_info_bland_title,
                        CarBrandListFrag.class), CarBrandListFrag.REQUEST_CODE);
                break;
            case R.id.tv_date:
                Calendar calendar = Calendar.getInstance();
                mPopupWindowUtil.showDateChoose(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH), new PopupWindowUtil.OnSelectDateListener() {
                            @Override
                            public void select(String year, String month, String day) {
                                mTvDate.setText(year + "-" + month + "-" + day);
                            }
                        });
                break;
            case R.id.btn_submit:
                Bundle bundle = new Bundle();
                SimpleFragAct.SimpleFragParam simpleFragParam = new SimpleFragAct.SimpleFragParam(R.string.choose_insurer,
                        ChooseInsurerFrag.class);
                simpleFragParam.paramBundle = bundle;
                SimpleFragAct.start(mContext, simpleFragParam);
                break;
        }
    }

    class MyAsynTask extends AsyncTask<Void, Void, String> {
        private LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = LoadingDialog.showBackCancelableDialog(mContext, "正在加载请稍后!!!");
        }

        @Override
        protected String doInBackground(Void... params) {
            return startScan();
        }

        @Override
        protected void onPostExecute(String result) {
            if (loadingDialog.isShowing()) handleOcrResult(result);
            loadingDialog.dissmiss();

        }
    }

    private void showOcrPickView() {
        if (null == ocrPickView) {
            ocrPickView = new PhotoPickSheet(this);
            ocrPickView.setOnOperateListener(new PhotoPickSheet.OnOperateListener() {
                @Override
                public void onTakePhoto() {
                    RxPermissions rxPermissions = new RxPermissions(getActivity());
                    rxPermissions
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                            )
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean granted) {
                                    if (granted) { // Always true pre-M
                                        openOcrCamera();
                                    } else {
                                        ToastUtil.show("照相机权限被拒绝");
                                    }
                                }
                            });
                    ocrPickView.dismiss();
                }

                @Override
                public void onPickPhoto() {
                    importOcrImg();
                    ocrPickView.dismiss();
                }

                @Override
                public void onDialogCancel() {
                }
            });
        }
        ocrPickView.show();
    }

    private void openOcrCamera() {
        Intent intent = new Intent(mContext, ACameraActivity.class);
        startActivityForResult(intent, OCR_IMPORT_PHOTO);
    }

    /**
     * 导入图片
     */
    public void importOcrImg() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "选择照片"),
                    OCR_IMPORT_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("无法打开相册");
        }
    }

    public String startScan() {
        String xml = HttpUtil.getSendXML(OcrConstant.action, extension);
        String result = HttpUtil.send(xml, imgBytes);
        return result;
    }

    private void handleOcrResult(String result) {
        try {
            LogUtil.d("NetClient", "行驶证返回xml=" + result);
            DrivingLicense drivingLicense = DataParser.parseXml(result);
            mTvPeople.setText(drivingLicense.owner);
            mTvCarNum.setText(drivingLicense.licenseNo);
            mTvVin.setText(drivingLicense.vin);
            mTvEngineNumber.setText(drivingLicense.enginNo);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("请求失败");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            if (requestCode == CarBrandListFrag.REQUEST_CODE) {
                mTvBrand.setText(data
                        .getStringExtra("brand"));
            }
        }
        if (requestCode == OCR_IMPORT_PHOTO) {
            Uri uri = null;
            if (null != data) {
                uri = data.getData();
            }
            if (uri == null) {
                ToastUtil.show("获取图片失败");
                return;
            }
            try {
                String uriPath = UriUtil.getAbsolutePath(uri, mContext);
                extension = GetPhotoUtil.getExtensionByPath(uriPath);

                Bitmap bitmap = GetPhotoUtil.reduceImage(uriPath, 1000);
                ImageLoader.getInstance().displayImage(uriPath, mIvDrivingInfo, ImageLoaderImpl.cacheDiskOptions);
                imgBytes = UploadPhoto.getBytes(bitmap);
                LogUtil.d("NetClient", "行驶证拍照 路径" + uriPath + "  图片大小=" + (imgBytes.length * 1.0f / 1024 / 1024) + "MB");

                if (!(imgBytes.length > (1000 * 1024 * 4))) {
                    ocrTask = new MyAsynTask();
                    ocrTask.execute();
                } else {
                    ToastUtil.show("图片大于5M，请选择小于5M的图片。");
                }

            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.show("图片处理失败");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
