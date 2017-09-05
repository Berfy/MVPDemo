package com.wlb.agent.ui.offer.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.Offer;
import com.wlb.agent.ui.offer.adapter.OfferListAdapter;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.ActivityManager;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.listview.ScrollListView;

/**
 * Created by Berfy on 2017/7/17.
 * 报价
 */

public class OfferFrag extends SimpleFrag implements View.OnClickListener {

    private OfferListAdapter mAdapter;
    private PopupWindowUtil mPopupWindowUtil;

    @BindView(R.id.listView)
    ScrollListView mListView;
    @BindView(R.id.layout_top_info_content)
    LinearLayout mLlTopInfoContent;
    @BindView(R.id.v_tag)
    View mVTag;

    private boolean mIsShowTopCarInfo = true;//是否显示顶部车辆信息

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.offer, ComfirmCarInfoFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.offer_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ActivityManager.getInstance().pushActivity("offer", getActivity());
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        getTitleBar().setRightText(R.string.share);
        getTitleBar().setOnRightTxtClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                OfferShareFrag.start(mContext);
            }
        });
        mAdapter = new OfferListAdapter(mContext, new OfferListAdapter.OnBuyListener() {
            @Override
            public void buy(Offer offer) {
                SimpleFragAct.start(mContext, new SimpleFragAct.SimpleFragParam(R.string.underwrite_info, UnderWriteFrag.class));
            }
        });
        mListView.setAdapter(mAdapter);
        //测试
        List<Offer> converages = new ArrayList<>();
        converages.add(new Offer("", getString(R.string.offer_pingan), "100", "98", 0.5, 0.5, "800", "350", "3000", null));
        converages.add(new Offer("", getString(R.string.offer_taipingyang), "100", "98", 0.5, 0.5, "800", "350", "3000", null));
        converages.add(new Offer("", getString(R.string.offer_renmin), "100", "98", 0.5, 0.5, "800", "350", "3000", null));
        mAdapter.refresh(converages);
    }

    @OnClick({R.id.layout_hotline, R.id.btn_submit, R.id.layout_top_info})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_top_info:
                mIsShowTopCarInfo = !mIsShowTopCarInfo;
                mLlTopInfoContent.setVisibility(mIsShowTopCarInfo ? View.VISIBLE : View.GONE);
                if (mIsShowTopCarInfo) {
                    mVTag.setBackgroundResource(R.drawable.ic_top_icon);
                } else {
                    mVTag.setBackgroundResource(R.drawable.ic_down_icon);
                }
                break;
            case R.id.layout_hotline:
                mPopupWindowUtil.showHotLine();
                break;
            case R.id.btn_submit:
                close();
                break;
        }
    }
}
