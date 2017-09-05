package com.wlb.agent.ui.offer.frag;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.util.ext.SoftInputUtil;
import com.android.util.ext.ToastUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.common.ActivityManager;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Berfy on 2017/7/18.
 * 填写核保信息
 */
public class UnderWriteFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.btn_checked_tip1)//投保人、被保险人、车主是同一人
            Button mBtnCheckTip1;
    @BindView(R.id.btn_checked_tip2)//投保人、被保险人是同一人
            Button mBtnCheckTip2;
    @BindView(R.id.layout_beibaoren_idcard_info)//被保人证件信息
            LinearLayout mLlBeibaorenIdCardInfo;
    @BindView(R.id.layout_beibaoren)//被保人信息
            LinearLayout mLlBeibaoren;
    @BindView(R.id.layout_toubaoren)//投保人信息
            LinearLayout mLlToubaoren;
    @BindView(R.id.layout_chezhu)//车主信息
            LinearLayout mLlCheZhu;
    @BindView(R.id.tv_male)//男
            TextView mTvMale;
    @BindView(R.id.tv_female)//女
            TextView mTvFemale;
    @BindView(R.id.v_male)//男 图标
            View mVMale;
    @BindView(R.id.v_female)//女 图标
            View mVFemale;
    @BindView(R.id.v_warranty1)//电子保单
            View mVWarranty1;
    @BindView(R.id.v_warranty2)//纸质保单
            View mVWarranty2;

    private boolean mIsTip1Open;//车主、被保险人、投保人是同一人
    private boolean mIsTip2Open;//被保险人、投保人是同一人
    private int mWarrantyType = 0;//0电子 1纸质

    @Override
    protected int getLayoutId() {
        return R.layout.underwrite_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ActivityManager.getInstance().pushActivity("offer", getActivity());
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SoftInputUtil.hideSoftInput(getActivity());
                return false;
            }
        });
        setWarranty(mWarrantyType);
    }

    @OnClick({R.id.btn_peisong_support, R.id.tv_web, R.id.layout_tip1, R.id.layout_tip2, R.id.layout_male, R.id.layout_female,
            R.id.layout_warranty1, R.id.layout_warranty2, R.id.btn_submit})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_peisong_support:
                ToastUtil.show("弹出支持的电子保单");
                break;
            case R.id.tv_web:
                WebViewFrag.start(mContext, new WebViewFrag.WebViewParam(getString(R.string.underwrite_help_title),
                        false, "http://m.baidu.com", null));
                break;
            case R.id.layout_tip1:
                mIsTip1Open = !mIsTip1Open;
                mBtnCheckTip1.setBackgroundResource(mIsTip1Open ? R.drawable.ic_converage_press : R.drawable.ic_converage);
                updateInputInfo();
                break;
            case R.id.layout_tip2:
                mIsTip2Open = !mIsTip2Open;
                mBtnCheckTip2.setBackgroundResource(mIsTip2Open ? R.drawable.ic_converage_press : R.drawable.ic_converage);
                updateInputInfo();
                break;
            case R.id.layout_male:
                setSex(true);
                break;
            case R.id.layout_female:
                setSex(false);
                break;
            case R.id.layout_warranty1:
                setWarranty(0);
                break;
            case R.id.layout_warranty2:
                setWarranty(1);
                break;
            case R.id.btn_submit:
                SimpleFragAct.start(mContext, new SimpleFragAct.SimpleFragParam(R.string.underwrite_submit_suc, SubmitSucFrag.class));
                close();
                break;
        }
    }

    private void updateInputInfo() {
        if (!mIsTip1Open && !mIsTip2Open) {
            mLlBeibaoren.setVisibility(View.VISIBLE);
            mLlToubaoren.setVisibility(View.VISIBLE);
            mLlCheZhu.setVisibility(View.VISIBLE);
        } else if (!mIsTip1Open) {
            mLlBeibaoren.setVisibility(View.VISIBLE);
            mLlToubaoren.setVisibility(View.GONE);
            mLlCheZhu.setVisibility(View.VISIBLE);
        } else {
            mLlBeibaoren.setVisibility(View.VISIBLE);
            mLlToubaoren.setVisibility(View.GONE);
            mLlCheZhu.setVisibility(View.GONE);
        }
    }

    private void setSex(boolean isMale) {
        mVMale.setBackgroundResource(R.drawable.ic_male);
        mVFemale.setBackgroundResource(R.drawable.ic_female);
        mTvMale.setTextColor(getResources().getColor(R.color.common_text_gray));
        if (isMale) {
            mTvMale.setTextColor(getResources().getColor(R.color.common_blue));
            mVMale.setBackgroundResource(R.drawable.ic_male_select);
        } else {
            mTvFemale.setTextColor(getResources().getColor(R.color.common_blue));
            mVFemale.setBackgroundResource(R.drawable.ic_female_select);
        }
    }

    /**
     * 选择保单类型
     *
     * @param type 0电子 1纸质
     */
    private void setWarranty(int type) {
        mWarrantyType = type;
        mVWarranty1.setBackgroundResource(R.drawable.ic_underwrite_check);
        mVWarranty2.setBackgroundResource(R.drawable.ic_underwrite_check);
        switch (type) {
            case 0:
                mVWarranty1.setBackgroundResource(R.drawable.ic_underwrite_checked);
                break;
            case 1:
                mVWarranty2.setBackgroundResource(R.drawable.ic_underwrite_checked);
                break;
        }
    }
}
