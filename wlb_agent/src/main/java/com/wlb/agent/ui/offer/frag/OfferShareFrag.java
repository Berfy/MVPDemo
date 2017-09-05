package com.wlb.agent.ui.offer.frag;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.android.util.device.DeviceUtil;
import com.android.util.ext.SoftInputUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wlb.agent.R;
import com.wlb.agent.common.share.ShareHelper;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.offer.OfferClient;
import com.wlb.agent.core.data.offer.entity.Insurer;
import com.wlb.agent.core.data.offer.entity.OfferShare;
import com.wlb.agent.core.data.offer.entity.OfferShareChooseData;
import com.wlb.agent.core.data.offer.entity.OfferSharePrimary;
import com.wlb.agent.core.data.offer.entity.OfferShareSave;
import com.wlb.agent.core.data.offer.entity.OfferShareTotalPrice;
import com.wlb.agent.core.data.offer.response.OfferShareResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.offer.adapter.OfferShareListAdapter;
import com.wlb.agent.util.GsonUtil;
import com.wlb.agent.util.StringUtil;
import com.wlb.common.ActivityManager;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.share.CShareListener;
import common.share.SharePlatform;
import common.widget.listview.ScrollListView;
import rx.functions.Action1;

/**
 * Created by Berfy on 2017/7/17.
 * 报价分享
 */

public class OfferShareFrag extends SimpleFrag implements View.OnClickListener, CShareListener {

    private final static String PARAM = "data";
    private OfferShareListAdapter mAdapter;
    private OfferShareChooseData mData;
    private String mShareUrl = "";
    private String mShareTitle = "";
    private String mShareContent = "";
    private String mShareSms = "";

//    private boolean mIsSavePremium;//避免重复请求
//    private Task mTaskSavePremium;

    private boolean mIsGetData;//避免重复请求
    private Task mTaskGetData;

    private boolean mIsSavePremium;//避免重复请求
    private Task mTaskSavePremium;

    @BindView(R.id.listView)
    ScrollListView mListView;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;

    public static void start(Context context, OfferShareChooseData offerShareChooseData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, offerShareChooseData);
        SimpleFragAct.start(context, new SimpleFragAct.SimpleFragParam(R.string.offer_share, OfferShareFrag.class, bundle));
    }

    @Override
    protected int getLayoutId() {
        ActivityManager.getInstance().pushActivity("offer", getActivity());
        return R.layout.offer_share_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (null != getArguments()) {
            mData = (OfferShareChooseData) getArguments().getSerializable(PARAM);
        }
        mAdapter = new OfferShareListAdapter(mContext);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SoftInputUtil.hideSoftInput((Activity) mContext);
                return false;
            }
        });
        mListView.setAdapter(mAdapter);
        if (null != mData) {
            List<OfferShare> converages = new ArrayList<>();
            for (int i = 0; i < mData.getCompany().size(); i++) {
                Insurer insurer = mData.getCompany().get(i);
                if (null == insurer) {
                    mData.getCompany().remove(i);
                    i--;
                    continue;
                }
                String company = getString(R.string.offer_renmin);
                if (insurer.getCompanyCode().equals("rb")) {
                    company = getString(R.string.offer_renmin);
                } else if (insurer.getCompanyCode().equals("pn")) {
                    company = getString(R.string.offer_pingan);
                } else if (insurer.getCompanyCode().equals("tpy")) {
                    company = getString(R.string.offer_taipingyang);
                }
                OfferShare offerShare = new OfferShare();
                offerShare.setSelect(true);
                offerShare.setSeqNo(insurer.getSeqNo());
                offerShare.setCompanyName(company);
                offerShare.setCompanyCode(insurer.getCompanyCode());
                OfferShareTotalPrice totalPrice = new OfferShareTotalPrice();
                totalPrice.setBusinessTotalPrice(insurer.getBusinessTotalPrice());
                totalPrice.setTaxPrice(insurer.getTaxPrice());
                totalPrice.setVehiclePrice(insurer.getVehiclePrice());
                offerShare.setTotalPrice(totalPrice);
                offerShare.setSyxZhekou(80);
                offerShare.setJqxZhekou(80);
                converages.add(offerShare);
            }
            mAdapter.refresh(converages);
        }
        getShareData();
    }

    @OnClick({R.id.layout_wx, R.id.layout_qq, R.id.layout_sms})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_wx:
                checkShare(0);
                break;
            case R.id.layout_qq:
                checkShare(1);
                break;
            case R.id.layout_sms:
                checkShare(2);
                break;
        }
    }

    /**
     * @param type 0微信 1QQ 2短信
     */
    private void checkShare(int type) {
        UserResponse userResponse = UserClient.getLoginedUser();
        StringBuffer sbSeqNo = new StringBuffer();
        List<OfferShare> offerShares = mAdapter.getList();
        String owner = "";//车主
        String liscenNo = "";//车牌号
        String company = "";//保险公司
        double jqx = 0;//交强险
        double ccs = 0;//车船税
        double syxZhekou = 0;//商业险折扣
        double jqxZhekou = 0;//交强险折扣
        double bjmp = 0;//原价
        double syx = 0;//原价
        double price = 0;//折后总额
        double totalPrice = 0;//原价
        int size = 0;
        StringBuffer sbXianzhong = new StringBuffer();
        for (OfferShare offerShare : offerShares) {
            if (offerShare.isSelect()) {
                size++;
                owner = offerShare.getOwner();
                liscenNo = offerShare.getLicenseNo();
                company = offerShare.getCompanyName();
                syxZhekou = (offerShare.getSyxZhekou() * 0.1) / 10;
                jqxZhekou = (offerShare.getJqxZhekou() * 0.1) / 10;
                price = offerShare.getPrice();
                sbSeqNo.append(offerShare.getSeqNo() + ",");
                for (OfferSharePrimary offerSharePrimary : offerShare.getPrimary()) {
                    if (offerSharePrimary.getSelected() == 1) {//选中
                        sbXianzhong.append(offerSharePrimary.getInsuranceName() +
                                offerSharePrimary.getPrice() + "元，");
                        if (offerSharePrimary.getExempt() == 1) {
                            bjmp += offerSharePrimary.getExemptPrice();
                        }
                    }
                }
                for (OfferSharePrimary offerSharePrimary : offerShare.getAdditional()) {
                    if (offerSharePrimary.getSelected() == 1) {//选中
                        sbXianzhong.append(offerSharePrimary.getInsuranceName() +
                                offerSharePrimary.getPrice() + "元，");
                        if (offerSharePrimary.getExempt() == 1) {//不计免赔
                            bjmp += offerSharePrimary.getExemptPrice();
                        }
                    }
                }
                jqx = offerShare.getTotalPrice().getVehiclePrice();
                ccs = offerShare.getTotalPrice().getTaxPrice();
                syx = offerShare.getTotalPrice().getBusinessTotalPrice();
                totalPrice = offerShare.getTotalPrice().getBusinessTotalPrice()
                        * offerShare.getSyxZhekou() / 100
                        + offerShare.getTotalPrice().getVehiclePrice()
                        * offerShare.getJqxZhekou() / 100 + offerShare.getTotalPrice().getTaxPrice();
            }
        }
        String seqNo = sbSeqNo.toString();
        if (TextUtils.isEmpty(seqNo)) {
            ToastUtil.show(R.string.offer_share_tip);
            return;
        }
        if (type == 2) {
            if (size > 1) {//短信只可分享一个报价
                ToastUtil.show(R.string.offer_share_tip_sms);
                return;
            }
            try {
                LogUtil.e("保险保险啊啊啊", sbXianzhong.toString());
                mShareSms = "您好，" + liscenNo + "的【" + company + "】报价："
                        + (sbXianzhong.substring(0, sbXianzhong.length() - 1)) + "，不计免赔总计" + StringUtil.subZeroAndDot(bjmp)
                        + "，商业险总计" + StringUtil.subZeroAndDot(syx) + "元，交强险" + StringUtil.subZeroAndDot(jqx) + "元，车船税"
                        + StringUtil.subZeroAndDot(ccs) + "元，费用总计" + StringUtil.subZeroAndDot(totalPrice) + "元。其中商业险折扣率"
                        + syxZhekou + "，交强险折扣率" + jqxZhekou + "，折后您需缴纳" + StringUtil.subZeroAndDot(price) + "元。投保请联系"
                        + (TextUtils.isEmpty(userResponse.name) ? userResponse.nick_name : userResponse.name) + "，电话"
                        + userResponse.phone + "，竭诚为您服务。【我来保】";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mShareUrl = H5.OFFER_SHARE_TEST + "?seqNos=" + sbSeqNo.substring(0, sbSeqNo.length() - 1)
                    + "&token=" + userResponse.token;
            mShareTitle = "车主" + owner + "的车险订单";
            mShareContent = "车主" + owner + "的车险订单，点击查看";
        }
        shareSave(type);
    }

    private String getShareJSON() {
        if (null != mAdapter) {
            List<OfferShareSave> offerShares = new ArrayList<>();
            for (OfferShare offerShare : mAdapter.getList()) {
                OfferShareSave offerShareSave = new OfferShareSave();
                offerShareSave.setSeqNo(offerShare.getSeqNo());
                offerShareSave.setCompanyCode(offerShare.getCompanyCode());
                //优惠后金额=保费总额-推广费
                offerShareSave.setPremium(offerShare.getPrice());
                offerShares.add(offerShareSave);
            }
            return GsonUtil.getInstance().toJson(offerShares);
        }
        return "";
    }

    private String getSeqNos() {
        if (null != mData) {
            StringBuffer sb = new StringBuffer();
            for (Insurer insurer : mData.getCompany()) {
                if (null == insurer) {
                    continue;
                }
                sb.append(insurer.getSeqNo() + ",");
            }
            if (sb.length() > 0) {
                return sb.substring(0, sb.length() - 1);
            }
            return "";
        }
        return "";
    }

    private void getShareData() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetData) {
            return;
        }
        mTaskGetData = OfferClient.getOfferShareList(getSeqNos(), new ICallback() {
            @Override
            public void start() {
                mIsGetData = true;
            }

            @Override
            public void success(Object data) {
                OfferShareResponse response = (OfferShareResponse) data;
                if (response.isSuccessful()) {
                    if (null != response.getList()) {
                        if (null != mData) {
                            for (int i = 0; i < mData.getCompany().size(); i++) {
                                if (null == mData.getCompany().get(i)) {
                                    continue;
                                }
                                OfferShare offerShare = response.getList().get(i);
                                offerShare.setSelect(true);
                                offerShare.setSyxZhekou(100);
                                offerShare.setJqxZhekou(100);
                                //默认计算总价和推广费
                                offerShare.setPrice(offerShare.getTotalPrice().getBusinessTotalPrice() * offerShare.getSyxZhekou() / 100
                                        + offerShare.getTotalPrice().getVehiclePrice()
                                        * offerShare.getJqxZhekou() / 100 + offerShare.getTotalPrice().getTaxPrice());
                                offerShare.setMyPrice(offerShare.getTotalPrice().getTaxPrice() + offerShare.getTotalPrice().getVehiclePrice()
                                        + offerShare.getTotalPrice().getBusinessTotalPrice() - offerShare.getPrice());
                                offerShare.setSeqNo(mData.getCompany().get(i).getSeqNo());
                            }
                            mAdapter.refresh(response.getList());
                        }
                    }
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
            }

            @Override
            public void end() {
                mIsGetData = false;
            }
        });
    }

    private void shareSave(int type) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetData) {
            return;
        }
        mTaskSavePremium = OfferClient.saveOfferSharePremiumPrice(getShareJSON(), new ICallback() {
            @Override
            public void start() {
                mIsSavePremium = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse baseResponse = (BaseResponse) data;
                if (baseResponse.isSuccessful()) {
                    switch (type) {
                        case 0:
                            share(SharePlatform.WEIXIN);
                            break;
                        case 1:
                            share(SharePlatform.QQ);
                            break;
                        case 2:
                            DeviceUtil.sendSMS(mContext, mShareSms);
                            break;
                    }
                }
            }

            @Override
            public void failure(NetException e) {

            }

            @Override
            public void end() {
                mIsSavePremium = false;
            }
        });
    }

    private void share(SharePlatform platform) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            ready2ShareContent(platform);
                            ShareHelper.getInstance().setShareTitle(mShareTitle);
                            ShareHelper.getInstance().setShareContent(mShareContent);
                            ShareHelper.getInstance().setLinkUrl(mShareUrl);
                            ShareHelper.getInstance().setShareImage(BitmapFactory.decodeResource(getResources(), R.drawable.push));
                            ShareHelper.getInstance().share2Platform((Activity) mContext, platform, OfferShareFrag.this);
                        } else {
                            ToastUtil.show(R.string.permission_failed);
                        }
                    }
                });
    }

    private void ready2ShareContent(SharePlatform platform) {
        ShareHelper shareHelper = ShareHelper.getInstance();
        shareHelper.reset();
    }

    @Override
    public void onResult(SharePlatform platform) {

    }

    @Override
    public void onError(SharePlatform platform, Throwable e) {

    }

    @Override
    public void onCancel(SharePlatform platform) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTaskGetData) mTaskGetData.stop();
        if (null != mTaskSavePremium) mTaskSavePremium.stop();
    }
}
