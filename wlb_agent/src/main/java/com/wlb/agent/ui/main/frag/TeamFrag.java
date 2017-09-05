package com.wlb.agent.ui.main.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.util.device.DeviceUtil;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.entity.BannerEntity;
import com.wlb.agent.core.data.agentservice.response.BannerResponse;
import com.wlb.agent.ui.main.adapter.BannerImgPageAdapter;
import com.wlb.common.BaseFragment;

import java.util.List;

import butterknife.BindView;
import common.widget.viewpager.AutoScrollViewPager;
import common.widget.viewpager.ViewPager;

/**
 * @author 张全
 *         保友圈
 */
public class TeamFrag extends BaseFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.bannerViewPagers)
    AutoScrollViewPager mBannerViewPager;
    @BindView(R.id.bannerImgIndictor)
    LinearLayout mIndicatorGroup;

    private BannerImgPageAdapter mBannerAdapter;
    private List<BannerEntity> bannerEntities;
    private boolean isGetBanner;
    private Task getBannerTask;// 获取banner信息任务

    @Override
    protected int getLayoutId() {
        return R.layout.team_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        // banner信息
        mBannerViewPager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                setSelectedIndictor(page % bannerEntities.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mIndicatorGroup = findViewById(R.id.bannerImgIndictor);


        TeamPagerAdapter adapter = new TeamPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setPagingEnabled(false);

        doGetBanners();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBannerViewPager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBannerViewPager.pauseAutoScroll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != getBannerTask) getBannerTask.stop();
    }

    /**
     * 请求banner信息
     */

    private void doGetBanners() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
//            ToastUtil.show(R.string.net_noconnection);
//            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (isGetBanner) return;
        isGetBanner = true;
        getBannerTask = AgentServiceClient.doGetBanner(1, new SimpleCallback() {

            @Override
            public void success(Object data) {
                BannerResponse response = (BannerResponse) data;
                if (response.isSuccessful()) {
                    // 请求成功
                    bannerEntities = response.getList();

                    if (bannerEntities.isEmpty()) {
                        mBannerViewPager.setAdapter(null);
                        mBannerViewPager.setVisibility(View.GONE);
                    } else {
                        mBannerViewPager.setVisibility(View.VISIBLE);
                        addIndictorGroup(bannerEntities.size());

                        if (null == mBannerAdapter) {
                            mBannerAdapter = new BannerImgPageAdapter(mContext, mBannerViewPager, bannerEntities, R.layout.team_banner_item);
                            mBannerViewPager.setAutoScrollAdapter(mBannerAdapter);
                            mBannerViewPager.setInterval(4 * 1000);
                            mBannerViewPager.setScrollDurationFactor(4.0f);
                            mBannerViewPager.startAutoScroll(5 * 1000);
                        } else {
                            mBannerViewPager.refresh(bannerEntities);
                            setSelectedIndictor(mBannerViewPager.getCurrentItem() % bannerEntities.size());
                        }
                    }
                }
            }

            @Override
            public void end() {
                isGetBanner = false;
            }
        });
    }

    /**
     * banner广告指示器
     *
     * @param size
     */
    private void addIndictorGroup(int size) {
        mIndicatorGroup.removeAllViews();
        if (size == 1) {
            // 如果只有一张banner图 则不显示底部圆圈
            return;
        }
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DeviceUtil.dip2px(mContext, 5), DeviceUtil.dip2px(mContext, 5));
            imageView.setImageResource(R.drawable.btn_banner_indictor_check);
            if (i > 0) {
                params.leftMargin = DeviceUtil.dip2px(mContext, 6);
            }
            mIndicatorGroup.addView(imageView, params);
        }
    }

    private void setSelectedIndictor(int pos) {
        int size = bannerEntities.size();
        if (size == 1) {
            // 如果只有一张banner图 则不显示底部圆圈
            return;
        }
        for (int i = 0; i < size; i++) {
            ImageView mImageView = (ImageView) mIndicatorGroup.getChildAt(i);
            mImageView.setSelected(i == pos ? true : false);
        }
    }

    public void onClick(View v) {
    }


    private class TeamPagerAdapter extends FragmentStatePagerAdapter {
        public TeamPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            if (position == 0) {
//                return Fragment.instantiate(mContext,
//                        RankFrag.class.getName());
//            }
//            else {
            return Fragment.instantiate(mContext,
                    NewsListFrag.class.getName());
//            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
