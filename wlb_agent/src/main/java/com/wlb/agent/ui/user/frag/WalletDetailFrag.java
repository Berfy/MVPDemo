package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.util.StringUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 收支明细
 * Created by Berfy
 */
public class WalletDetailFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private List<View> mTabList = new ArrayList<View>();

    private int mTab = -1;
    private PagerAdapter mAdapter;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.wallet_detail,
                WalletDetailFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_wallet_detail_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mTabList.add(findViewById(R.id.layout_status1));
        mTabList.add(findViewById(R.id.layout_status2));
        mTabList.add(findViewById(R.id.layout_status3));

        List<WalletDetailListFrag> orderListFrags = new ArrayList<>();
        //设置点击事件
        for (int i = 0; i < mTabList.size(); i++) {
            mTabList.get(i).setOnClickListener(this);
            Bundle bundle = new Bundle();
            if (i == 0) {
                bundle.putInt(WalletDetailListFrag.PARAM_STATUS, 10001);
            } else if (i == 1) {
                bundle.putInt(WalletDetailListFrag.PARAM_STATUS, 20002);
            } else {
                bundle.putInt(WalletDetailListFrag.PARAM_STATUS, 0);
            }
            orderListFrags.add((WalletDetailListFrag) Fragment.instantiate(mContext, WalletDetailListFrag.class.getName(), bundle));
        }
        mAdapter = new PagerAdapter(getChildFragmentManager(), orderListFrags);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mTabList.size());//缓存所显示的页面
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switchPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        switchPage(0);
    }

    private void switchPage(int position) {
        if (mTab == position) {
            return;
        }
        for (int i = 0; i < mTabList.size(); i++) {
            if (i == position) {
                mTabList.get(i).findViewById(StringUtil.getRes(mContext, "v_line" + (i + 1), "id"))
                        .setVisibility(View.VISIBLE);
            } else {
                mTabList.get(i).findViewById(StringUtil.getRes(mContext, "v_line" + (i + 1), "id"))
                        .setVisibility(View.INVISIBLE);
            }
        }
        mViewPager.setCurrentItem(position);
        mTab = position;
    }

    @Override
    public void onClick(View v) {
        int pos = mTabList.indexOf(v);
        if (pos != -1) {
            switchPage(pos);
        }
        switch (v.getId()) {
        }
    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        private List<WalletDetailListFrag> mListFrags;

        public PagerAdapter(FragmentManager fm, List<WalletDetailListFrag> orderListFrags) {
            super(fm);
            mListFrags = orderListFrags;
        }

        public List<WalletDetailListFrag> getFragments() {
            return mListFrags;
        }

        @Override
        public Fragment getItem(int position) {
            return mListFrags.get(position);
        }

        @Override
        public int getCount() {
            return mTabList.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
