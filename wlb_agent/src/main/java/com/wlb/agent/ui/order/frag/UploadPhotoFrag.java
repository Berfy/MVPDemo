package com.wlb.agent.ui.order.frag;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.util.device.DeviceUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.view.PhotoPickSheet;
import com.wlb.agent.ui.order.adapter.UploadPhotoGridAdapter;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.LoadingBar;

/**
 * Created by Berfy on 2017/7/19.
 * 上传审核照片
 */
public class UploadPhotoFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.iv_toubaoren_idcard1)
    ImageView mIvToubaoren1;//投保人正面
    @BindView(R.id.iv_toubaoren_idcard2)
    ImageView mIvToubaoren2;//投保人背面
    @BindView(R.id.iv_beibaoren_idcard1)
    ImageView mIvBeibaoren1;//被保人正面
    @BindView(R.id.iv_beibaoren_idcard2)
    ImageView mIvBeibaoren2;//被保人背面
    @BindView(R.id.iv_chezhu_idcard1)
    ImageView mIvChezhu1;//车主正面
    @BindView(R.id.iv_chezhu_idcard2)
    ImageView mIvChezhu2;//车主背面
    @BindView(R.id.grid_yanche)
    GridView mGridYanche;
    @BindView(R.id.grid_qita)
    GridView mGridQita;
    @BindView(R.id.loadingBar)
    LoadingBar mLoadingBar;

    private UploadPhotoGridAdapter mAdapterYanche;
    private UploadPhotoGridAdapter mAdapterQita;
    private PhotoPickSheet mPhotoPickSheet;

    private List<String> mYancheImages = new ArrayList<>();//验车照
    private List<String> mQitaImages = new ArrayList<>();//其他照片

    private final static String PARAM_ORDER_NO = "orderNo";//订单号
    private String mOrderNo;//订单号
    private boolean mIsUploading = false;//标记正在上传，不可打断

    public static void start(Context context, String orderNo) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_ORDER_NO, orderNo);
        SimpleFragAct.start(context, new SimpleFragAct.SimpleFragParam(R.string.order_upload_photo, UploadPhotoFrag.class, bundle));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.order_upload_photo_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (null != getArguments()) {
            mOrderNo = getArguments().getString(PARAM_ORDER_NO);
        }
        mPhotoPickSheet = new PhotoPickSheet(this);
        mPhotoPickSheet.uploadPhoto(true);
        mPhotoPickSheet.setUploadType(1);
        mPhotoPickSheet.setOnPhotoUploadListener(new PhotoPickSheet.OnPhotoUploadListener() {
            @Override
            public void uploadSuccess(View view, String localUrl, String url, Bitmap bitmap) {

            }

            @Override
            public void local(String localUrl, Bitmap bitmap) {

            }
        });
        LinearLayout.LayoutParams t1 = (LinearLayout.LayoutParams) mIvToubaoren1.getLayoutParams();
        LinearLayout.LayoutParams t2 = (LinearLayout.LayoutParams) mIvToubaoren2.getLayoutParams();
        LinearLayout.LayoutParams b1 = (LinearLayout.LayoutParams) mIvBeibaoren1.getLayoutParams();
        LinearLayout.LayoutParams b2 = (LinearLayout.LayoutParams) mIvBeibaoren2.getLayoutParams();
        LinearLayout.LayoutParams c1 = (LinearLayout.LayoutParams) mIvChezhu1.getLayoutParams();
        LinearLayout.LayoutParams c2 = (LinearLayout.LayoutParams) mIvChezhu2.getLayoutParams();
        t1.width = (DeviceUtil.getScreenWidthPx(mContext) - DeviceUtil.dip2px(mContext, 80)) / 2;
        t1.height = t1.width * 2 / 3;
        mIvToubaoren1.setLayoutParams(t1);
        mIvToubaoren2.setLayoutParams(t1);
        mIvBeibaoren1.setLayoutParams(t1);
        mIvBeibaoren2.setLayoutParams(t1);
        mIvChezhu1.setLayoutParams(t1);
        mIvChezhu2.setLayoutParams(t1);
        mGridYanche.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        mGridQita.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        mAdapterYanche = new UploadPhotoGridAdapter(mContext);
        mAdapterQita = new UploadPhotoGridAdapter(mContext);
        mGridYanche.setAdapter(mAdapterYanche);
        mGridQita.setAdapter(mAdapterQita);


    }

    @OnClick({R.id.btn_submit})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:

                break;
        }
    }
}
