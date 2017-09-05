package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.wlb.agent.R;
import com.wlb.agent.util.StringUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的优惠券
 * Created by Berfy
 */
public class UserCouponFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tv_status1)
    TextView mTvStatus1;
    @BindView(R.id.tv_status2)
    TextView mTvStatus2;
    @BindView(R.id.tv_status3)
    TextView mTvStatus3;
    private List<View> mTabList = new ArrayList<>();
    private List<TextView> mTabListText = new ArrayList<>();

    private int mTab = -1;
    private CouponAdapter mAdapter;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.user_coupon,
                UserCouponFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_coupon_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mTabList.add(findViewById(R.id.layout_status1));
        mTabList.add(findViewById(R.id.layout_status2));
        mTabList.add(findViewById(R.id.layout_status3));
        mTabListText.add(mTvStatus1);
        mTabListText.add(mTvStatus2);
        mTabListText.add(mTvStatus3);

        List<UserCouponListFrag> userCouponListFrags = new ArrayList<>();
        //设置点击事件
        for (int i = 0; i < mTabList.size(); i++) {
            mTabList.get(i).setOnClickListener(this);
            Bundle bundle = new Bundle();
            bundle.putInt(UserCouponListFrag.PARAM_STATUS, i);
            userCouponListFrags.add((UserCouponListFrag) Fragment.instantiate(mContext, UserCouponListFrag.class.getName(), bundle));
        }
        mAdapter = new CouponAdapter(getChildFragmentManager(), userCouponListFrags);
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
                mTabListText.get(i).setTextColor(getResources().getColor(R.color.common_red));
                mTabList.get(i).findViewById(StringUtil.getRes(mContext, "v_line" + (i + 1), "id"))
                        .setVisibility(View.VISIBLE);
            } else {
                mTabListText.get(i).setTextColor(getResources().getColor(R.color.common_text));
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
    }

    private class CouponAdapter extends FragmentStatePagerAdapter {

        private List<UserCouponListFrag> mUserCouponListFrags;

        public CouponAdapter(FragmentManager fm, List<UserCouponListFrag> userCouponListFrags) {
            super(fm);
            mUserCouponListFrags = userCouponListFrags;
        }

        public List<UserCouponListFrag> getFragments() {
            return mUserCouponListFrags;
        }

        @Override
        public Fragment getItem(int position) {
            return mUserCouponListFrags.get(position);
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
