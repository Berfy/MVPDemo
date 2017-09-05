package com.wlb.agent.ui.order.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.util.device.TimeUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.log.LogUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.insurance.InsuranceClient;
import com.wlb.agent.core.data.insurance.entity.Insurance;
import com.wlb.agent.core.data.insurance.response.InsuranceOrderDetail;
import com.wlb.agent.core.data.insurance.response.InsurancePriceResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.order.adapter.OrderDetailListSyxAdapter;
import com.wlb.agent.ui.order.helper.InsuranceUtil;
import com.wlb.agent.ui.user.frag.OrderCouponListFrag;
import com.wlb.agent.util.OrderSendCodeUtil;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.LoadingBar;
import common.widget.listview.ScrollListView;

/**
 * Created by Berfy on 2017/7/18.
 */

public class OrderDetailFrag extends SimpleFrag implements View.OnClickListener {

    private PopupWindowUtil mPopupWindowUtil;
    private OrderSendCodeUtil mOrderSendCodeUtil;
    private String mOrderNo;//订单号
    public static final String PARAM_ORDERNO = "orderNo";
    private OrderDetailListSyxAdapter mAdapter;
    private InsuranceOrderDetail mInsuranceOrderDetail;
    private boolean mIsDateShow = true;//是否显示起始日期
    private boolean mIsCarShow = false;//是否显示车辆信息
    private boolean mIsChezhuShow = false;//是否显示车主信息
    private boolean mIsToubaorenShow = false;//是否显示投保人
    private boolean mIsBeibaorenShow = false;//是否显示被保人
    private boolean mIsBaojiaShow = true;//是否显示报价明细

    private boolean mIsGetDetailData;//避免重复请求
    private Task mTaskGetDetailData;

    @BindView(R.id.loadingBar)
    LoadingBar mLoadingBar;

    @BindView(R.id.listView)
    ScrollListView mListView;
    //    @BindView(R.id.layout_youhui)
//    LinearLayout mLlYouhui;
    @BindView(R.id.layout_remark)
    LinearLayout mLlRemark;
    @BindView(R.id.v_status)
    View mVStatus;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.tv_status_tip)
    TextView mTvStatusTip;
    @BindView(R.id.tv_order_number)
    TextView mTvOrderNo;

    @BindView(R.id.layout_date)
    LinearLayout mLlDate;//起始日期layout
    @BindView(R.id.layout_date_content)
    LinearLayout mLlDateContent;//起始日期layout content
    @BindView(R.id.v_tag_date)
    View mVTagDate;//起始日期tag

    @BindView(R.id.layout_car)
    LinearLayout mLlCar;//车辆信息layout
    @BindView(R.id.layout_car_content)
    LinearLayout mLlCarContent;//车辆信息layout content
    @BindView(R.id.v_car_info_tag)
    View mVTagCar;//车辆信息tag

    @BindView(R.id.layout_chezhu)
    LinearLayout mLlChezhu;//车主layout
    @BindView(R.id.layout_chezhu_content)
    LinearLayout mLlChezhuContent;//车主layout content
    @BindView(R.id.v_chezhu_tag)
    View mVTagChezhu;//车主tag

    @BindView(R.id.layout_toubaoren)
    LinearLayout mLToubaoren;//投保人layout
    @BindView(R.id.layout_toubaoren_content)
    LinearLayout mLlToubaorenContent;//投保人layout content
    @BindView(R.id.v_toubaoren_tag)
    View mVTagToubaoren;//投保人tag

    @BindView(R.id.layout_beibaoren)
    LinearLayout mLlBeibaoren;//被保人layout
    @BindView(R.id.layout_beibaoren_content)
    LinearLayout mLlBeibaorenContent;//被保人layout content
    @BindView(R.id.v_beibaoren_tag)
    View mVTagBeibaoren;//被保人tag

    @BindView(R.id.layout_baojia)
    LinearLayout mLlBaojia;//报价明细layout
    @BindView(R.id.v_baojia_tag)
    View mVTagBaojia;//报价tag

    @BindView(R.id.iv_icon)
    ImageView mIvIcon;//保险公司
    @BindView(R.id.tv_carnum)
    TextView mTvCarNum;
    @BindView(R.id.tv_car_num)
    TextView mTvCarNum1;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_order_price)
    TextView mTvOrderPrice;//都是订单金额 特么
    @BindView(R.id.tv_order_price1)
    TextView mTvOrderPrice1;//订单金额
    @BindView(R.id.tv_price)
    TextView mTvPrice;
    @BindView(R.id.tv_order_product_price)//商品原价
            TextView mTvRawPrice;
    @BindView(R.id.tv_syx_date)
    TextView mTvSyxDate;
    @BindView(R.id.tv_jqx_date)
    TextView mTvJqxDate;
    @BindView(R.id.tv_car_brand)
    TextView mTvCarBrand;
    @BindView(R.id.tv_engine_number)
    TextView mTvEngineNumber;
    @BindView(R.id.tv_vin_number)
    TextView mTvVin;
    @BindView(R.id.tv_car_create_date)
    TextView mTvCarCreateDate;
    @BindView(R.id.tv_chezhu_name)
    TextView mTvChezhuName;
    @BindView(R.id.tv_chezhu_idcard)
    TextView mTvChezhuIdcard;
    @BindView(R.id.tv_chezhu_mobile)
    TextView mTvChezhuMobile;
    @BindView(R.id.tv_toubaoren_name)
    TextView mTvToubaorenName;
    @BindView(R.id.tv_toubaoren_idcard)
    TextView mTvToubaorenIdcard;
    @BindView(R.id.tv_toubaoren_mobile)
    TextView mTvToubaorenMobile;
    @BindView(R.id.tv_beibaoren_name)
    TextView mTvBeibaorenName;
    @BindView(R.id.tv_beibaoren_idcard)
    TextView mTvBeibaorenIdcard;
    @BindView(R.id.tv_beibaoren_mobile)
    TextView mTvBeibaorenMobile;
    @BindView(R.id.tv_syx_zhekou)
    TextView mTvSyxZhekou;
    @BindView(R.id.tv_jqx_zhekou)
    TextView mTvJqxZhekou;
    @BindView(R.id.btn_submit)
    Button mBtnSubmit;

    public static void start(Context context, String orderNo) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_ORDERNO, orderNo);
        SimpleFragAct.start(context, new SimpleFragAct.SimpleFragParam(R.string.order_detail, OrderDetailFrag.class, bundle));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.order_detail_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mOrderSendCodeUtil = new OrderSendCodeUtil(mContext, new OrderSendCodeUtil.OnFinishListener() {
            @Override
            public void onFinish() {
                getDetail();
            }
        });
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        getTitleBar().setRightBtnDrawable(R.drawable.service_phone);
        getTitleBar().setOnRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindowUtil.showHotLine();
            }
        });
        if (null != getArguments()) {
            mOrderNo = getArguments().getString(PARAM_ORDERNO);
        }

//        getTitleBar().setOnLeftBtnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SimpleFragAct.start(mContext, new SimpleFragAct.SimpleFragParam(R.string.order, OrderFrag.class));
//                close();
//            }
//        });
//        mLlYouhui.setVisibility(View.VISIBLE);

        mAdapter = new OrderDetailListSyxAdapter(mContext);
        mListView.setAdapter(mAdapter);
        updateLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDetail();
    }

    private void updateLayout() {
        mVTagDate.setBackgroundResource(mIsDateShow ? R.drawable.ic_top_icon : R.drawable.ic_down_icon);
        mLlDateContent.setVisibility(mIsDateShow ? View.VISIBLE : View.GONE);

        mVTagCar.setBackgroundResource(mIsCarShow ? R.drawable.ic_top_icon : R.drawable.ic_down_icon);
        mLlCarContent.setVisibility(mIsCarShow ? View.VISIBLE : View.GONE);

        mVTagChezhu.setBackgroundResource(mIsChezhuShow ? R.drawable.ic_top_icon : R.drawable.ic_down_icon);
        mLlChezhuContent.setVisibility(mIsChezhuShow ? View.VISIBLE : View.GONE);

        mVTagToubaoren.setBackgroundResource(mIsToubaorenShow ? R.drawable.ic_top_icon : R.drawable.ic_down_icon);
        mLlToubaorenContent.setVisibility(mIsToubaorenShow ? View.VISIBLE : View.GONE);

        mVTagBeibaoren.setBackgroundResource(mIsBeibaorenShow ? R.drawable.ic_top_icon : R.drawable.ic_down_icon);
        mLlBeibaorenContent.setVisibility(mIsBeibaorenShow ? View.VISIBLE : View.GONE);

        mVTagBaojia.setBackgroundResource(mIsBaojiaShow ? R.drawable.ic_top_icon : R.drawable.ic_down_icon);
        mListView.setVisibility(mIsBaojiaShow ? View.VISIBLE : View.GONE);
    }

    private void getDetail() {
        if (mIsGetDetailData) {
            return;
        }
        mTaskGetDetailData = InsuranceClient.doGetOrderDetail(mOrderNo, new ICallback() {
            @Override
            public void start() {
                mIsGetDetailData = true;
                mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.START);
            }

            @Override
            public void success(Object data) {
                mInsuranceOrderDetail = (InsuranceOrderDetail) data;
                if (mInsuranceOrderDetail.isSuccessful()) {
                    mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.SUCCESS);
                    try {
                        for (int i = 0; i < mInsuranceOrderDetail.insuranceInfo.primary.size(); i++) {
                            Insurance insurance = mInsuranceOrderDetail.insuranceInfo.primary.get(i);
                            if (insurance.price == 0 && insurance.amount == 0 && insurance.exemptPrice == 0) {
                                mInsuranceOrderDetail.insuranceInfo.primary.remove(i);
                                i--;
                            }
                        }
                        for (int i = 0; i < mInsuranceOrderDetail.insuranceInfo.additional.size(); i++) {
                            Insurance insurance = mInsuranceOrderDetail.insuranceInfo.additional.get(i);
                            if (insurance.price == 0 && insurance.amount == 0 && insurance.exemptPrice == 0) {
                                mInsuranceOrderDetail.insuranceInfo.additional.remove(i);
                                i--;
                            }
                        }
                        setViewData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD);
                    ToastUtil.show(mInsuranceOrderDetail.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD);
            }

            @Override
            public void end() {
                mIsGetDetailData = false;
            }
        });
    }

    private void setViewData() {
        LogUtil.e(TAG, "订单状态" + mInsuranceOrderDetail.statusCode);
        mTvOrderNo.setText(mInsuranceOrderDetail.orderNo + "");
        mTvCity.setText(mInsuranceOrderDetail.regionName);
        mLlRemark.setVisibility(View.GONE);
        mTvStatus.setVisibility(View.VISIBLE);
        mTvStatusTip.setVisibility(View.VISIBLE);
        mVStatus.setVisibility(View.VISIBLE);
        switch (mInsuranceOrderDetail.statusCode) {
            case 2://核保成功 出单中
            case 4://待支付
                mVStatus.setBackgroundResource(R.drawable.ic_order_detail_status2);
                mLlRemark.setVisibility(View.GONE);
                mTvStatus.setTextColor(getResources().getColor(R.color.common_red));
                mTvStatus.setText(getString(R.string.order_order_status2));
                mTvStatusTip.setText(getString(R.string.order_order_status2_tip));
                mBtnSubmit.setVisibility(View.VISIBLE);
                mBtnSubmit.setText(R.string.order_go_pay1);
                break;
            case 1://核保中
            case 3://核保失败
                mBtnSubmit.setVisibility(View.VISIBLE);
                if (mInsuranceOrderDetail.statusCode == 1) {
                    mVStatus.setBackgroundResource(R.drawable.ic_order_detail_status1);
                    mLlRemark.setVisibility(View.GONE);
                    mTvStatus.setTextColor(getResources().getColor(R.color.common_blue));
                    mTvStatus.setText(getString(R.string.order_order_status1));
                    mTvStatusTip.setText(getString(R.string.order_order_status1_tip));
                } else if (mInsuranceOrderDetail.isUnderwritingFail()) {
                    mVStatus.setBackgroundResource(R.drawable.ic_order_detail_status0);
                    mLlRemark.setVisibility(View.GONE);
                    mTvStatus.setTextColor(getResources().getColor(R.color.order_status0));
                    mTvStatus.setText(getString(R.string.order_order_status0));
                    mTvStatusTip.setText(mInsuranceOrderDetail.statusMsg);
                    mBtnSubmit.setVisibility(View.GONE);
                }
                if (mInsuranceOrderDetail.uploadIDFlag()) {// 上传照片 显示上传照片
                    mTvStatusTip.setText(getString(R.string.order_order_status0_tip));
                    mBtnSubmit.setText(R.string.order_go_upload);
                } else {
                    mBtnSubmit.setVisibility(View.GONE);
                }
                break;
            case 7://支付成功
                mVStatus.setVisibility(View.GONE);
                mLlRemark.setVisibility(View.GONE);
                mTvStatus.setTextColor(getResources().getColor(R.color.common_red));
                mTvStatus.setText(getString(R.string.order_order_status3));
                mTvStatusTip.setText(getString(R.string.order_order_status3_tip));
                mBtnSubmit.setVisibility(View.VISIBLE);
                mBtnSubmit.setText(R.string.order_go_submit);
                break;
            case 5://投保成功
                mVStatus.setVisibility(View.GONE);
//                mLlYouhui.setVisibility(View.GONE);
                mTvStatus.setTextColor(getResources().getColor(R.color.common_red));
                mTvStatus.setText(getString(R.string.order_order_status4));
                mTvStatusTip.setText(getString(R.string.order_order_status4_tip));
                mBtnSubmit.setVisibility(View.GONE);
                break;
            case 6://支付过期
                mVStatus.setVisibility(View.GONE);
//                mLlYouhui.setVisibility(View.GONE);
                mLlRemark.setVisibility(View.GONE);
                mTvStatus.setTextColor(getResources().getColor(R.color.common_red));
                mTvStatus.setText(getString(R.string.order_status6));
                mTvStatusTip.setText(mInsuranceOrderDetail.statusMsg);
                mBtnSubmit.setVisibility(View.GONE);
                break;
            default:
                mVStatus.setVisibility(View.GONE);
//                mLlYouhui.setVisibility(View.GONE);
                mLlRemark.setVisibility(View.GONE);
                mTvStatus.setTextColor(getResources().getColor(R.color.common_red));
                mTvStatus.setText(getString(R.string.order_order_status5));
                mTvStatusTip.setText(mInsuranceOrderDetail.statusMsg);
                mBtnSubmit.setVisibility(View.GONE);
                break;
        }
        //保险公司icon
        InsuranceUtil.setImageOption(mInsuranceOrderDetail.companyCode, mIvIcon);
        //车主
        mTvName.setText(mInsuranceOrderDetail.owner);//车主姓名
        mTvChezhuName.setText(mInsuranceOrderDetail.owner);//车主姓名
        mTvChezhuMobile.setText(mInsuranceOrderDetail.ownerPhoneNo);//车主电话
        mTvChezhuIdcard.setText(mInsuranceOrderDetail.cardNo);//车主身份证
        mTvOrderPrice.setText(mInsuranceOrderDetail.totalPremium + "");//订单价格
        mTvOrderPrice1.setText(mInsuranceOrderDetail.totalPremium + "");
        mTvPrice.setText(mInsuranceOrderDetail.totalPremium + "");
        mTvRawPrice.setText(mInsuranceOrderDetail.totalPremium + "");//车险原始价格

        //车辆信息
        mTvCarNum1.setText(mInsuranceOrderDetail.licenseNo);
        mTvCarNum.setText(mInsuranceOrderDetail.licenseNo);//车牌号
        mTvCarBrand.setText(mInsuranceOrderDetail.carModel);//品牌
        mTvEngineNumber.setText(mInsuranceOrderDetail.engineNo);//发动机
        mTvVin.setText(mInsuranceOrderDetail.vin);//车架号
        mTvCarCreateDate.setText(TimeUtil.timeFormat(mInsuranceOrderDetail.enrollDate, "yyyy-MM-dd"));//登记日期

        //投保人信息
        mTvToubaorenName.setText(mInsuranceOrderDetail.insure);//投保人姓名
        mTvToubaorenIdcard.setText(mInsuranceOrderDetail.insureCardNo);//投保人身份证
        mTvToubaorenMobile.setText(mInsuranceOrderDetail.insurePhoneNo);//投保人电话

        //被保人信息
        mTvBeibaorenName.setText(mInsuranceOrderDetail.insurant);//被保人姓名
        mTvBeibaorenIdcard.setText(mInsuranceOrderDetail.insurantCardNo);//被保人身份证
        mTvBeibaorenMobile.setText(mInsuranceOrderDetail.insurantPhoneNo);//被保人电话

        //生效时间
        if (mInsuranceOrderDetail.vehicleStartDate <= 0 && mInsuranceOrderDetail.vehicleEndDate <= 0) {
            mTvJqxDate.setText("未购买");//生效时间
        } else {
            mTvJqxDate.setText(TimeUtil.timeFormat(mInsuranceOrderDetail.vehicleStartDate, "yyyy-MM-dd"));//生效时间
        }
        if (mInsuranceOrderDetail.businessStartDate <= 0 && mInsuranceOrderDetail.businessEndDate <= 0) {
            mTvSyxDate.setText("未购买");//生效时间
        } else {
            mTvSyxDate.setText(TimeUtil.timeFormat(mInsuranceOrderDetail.businessStartDate, "yyyy-MM-dd"));//生效时间
        }
        InsurancePriceResponse insurancePriceResponse = mInsuranceOrderDetail.insuranceInfo;
        List<Insurance> insurances = new ArrayList<>();
        insurances.addAll(insurancePriceResponse.primary);
        List<Insurance> primaryBjmp = new ArrayList<>();//找出主险的不计免赔
        for (Insurance insurance : insurancePriceResponse.primary) {
            if (insurance.isBJMPChecked()) {
                Insurance priInsurance = new Insurance(getString(R.string.bjmp)
                        + "(" + insurance.insuranceName + ")", insurance.amount, insurance.exemptPrice);
                primaryBjmp.add(priInsurance);
            }
        }
        insurances.addAll(primaryBjmp);
        insurances.addAll(insurancePriceResponse.additional);
        List<Insurance> additionalBjmp = new ArrayList<>();//找出主险的不计免赔
        for (Insurance insurance : insurancePriceResponse.additional) {
            if (insurance.isBJMPChecked()) {
                Insurance addInsurance = new Insurance(getString(R.string.bjmp)
                        + "(" + insurance.insuranceName + ")", insurance.amount, insurance.exemptPrice);
                additionalBjmp.add(addInsurance);
            }
        }
        insurances.addAll(additionalBjmp);
        mAdapter.refresh(insurances);
        mTvSyxZhekou.setText(mInsuranceOrderDetail.biRate);
        mTvJqxZhekou.setText(mInsuranceOrderDetail.ciRate);
    }

    @OnClick({R.id.layout_date, R.id.layout_car, R.id.layout_toubaoren, R.id.layout_beibaoren, R.id.layout_chezhu, R.id.layout_baojia,
            R.id.layout_youhui_content, R.id.btn_submit})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loadingBar:
                getDetail();
                break;
            case R.id.layout_date:
                mIsDateShow = !mIsDateShow;
                updateLayout();
                break;
            case R.id.layout_car:
                mIsCarShow = !mIsCarShow;
                updateLayout();
                break;
            case R.id.layout_toubaoren:
                mIsToubaorenShow = !mIsToubaorenShow;
                updateLayout();
                break;
            case R.id.layout_beibaoren:
                mIsBeibaorenShow = !mIsBeibaorenShow;
                updateLayout();
                break;
            case R.id.layout_chezhu:
                mIsChezhuShow = !mIsChezhuShow;
                updateLayout();
                break;
            case R.id.layout_baojia:
                mIsBaojiaShow = !mIsBaojiaShow;
                updateLayout();
                break;
            case R.id.layout_youhui_content:
                if (null != mInsuranceOrderDetail)
                    OrderCouponListFrag.start(mContext, mInsuranceOrderDetail.orderNo);
                break;
            case R.id.btn_submit:
                if (null != mInsuranceOrderDetail) {
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    switch (mInsuranceOrderDetail.statusCode) {
                        case 2://支付
                        case 4://核保成功
//                            webViewParam.url = mInsuranceOrderDetail.payForward;
                            webViewParam.url = "https://www.wolaibao.com/h5test/common/pay/pay.html?token="
                                    + UserClient.getLoginedUser().token
                                    + "&orderNo=" + mInsuranceOrderDetail.orderNo + "&shareFlag=0";
                            webViewParam.title = getString(R.string.pay);
                            SimpleFragAct.SimpleFragParam simpleFragParam = WebViewFrag.getStartParam(webViewParam);
                            WebViewFrag.start(mContext, simpleFragParam);
                            break;
                        case 1://核保中
                        case 3://重新核保
                            if (mInsuranceOrderDetail.uploadIDFlag()) {
                                webViewParam.title = "上传照片";
                                webViewParam.url = H5.UPLOAD_PAPERS + mInsuranceOrderDetail.orderNo;
                                WebViewFrag.start(mContext, webViewParam);
                            } else if (mInsuranceOrderDetail.isUnderwritingFail()) {    // 3 核保失败  底部显示从新核保按钮
                                webViewParam.title = "重新核保";
                                webViewParam.url = H5.AGAIN_OFFERA_PRICE + mInsuranceOrderDetail.vehicleId;
                                WebViewFrag.start(mContext, webViewParam);
                            }
                            break;
                        case 7://支付成功 提交验证码
                            mOrderSendCodeUtil.setOrderInfo(mInsuranceOrderDetail.policyId,
                                    mInsuranceOrderDetail.verifyCodeStatus,
                                    mInsuranceOrderDetail.verifyPhoneNo);
                            mOrderSendCodeUtil.show();
                            break;
                    }
                }
                break;
        }
    }

//    @Override
//    public boolean onBackPressed() {
//        SimpleFragAct.start(mContext, new SimpleFragAct.SimpleFragParam(R.string.order, OrderFrag.class));
//        close();
//        return false;
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTaskGetDetailData) {
            mTaskGetDetailData.stop();
        }
    }
}
