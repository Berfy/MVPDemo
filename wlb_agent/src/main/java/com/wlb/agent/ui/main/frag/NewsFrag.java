package com.wlb.agent.ui.main.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.device.DeviceUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.entity.NewsCategory;
import com.wlb.agent.core.data.agentservice.response.NewsCategoryResponse;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.List;

import common.widget.LoadingBar;
import common.widget.LoadingBar.LoadingStatus;
import common.widget.viewpager.ViewPager;

public class NewsFrag extends SimpleFrag implements OnClickListener {
    private LoadingBar loadingBar;
    private ViewPager mViewPager;
    private HorizontalScrollView mScrollView;
    private LinearLayout mGallery;
    private Task getCategoryTask;
    private int lastPage;
    private boolean isGetCategory;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.tab_news,
                NewsFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (null != loadingBar && loadingBar.canLoading()) {
                if (NetworkUtil.isNetworkAvailable(LContext.getContext())) doGetCategory();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.news_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        normalColor = getResources().getColor(R.color.c_ee968f);
        loadingBar = findViewById(R.id.loadingBar);
        loadingBar.setOnClickListener(this);

        mScrollView = findViewById(R.id.motif_repository);
        mGallery = findViewById(R.id.classify);

        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                TextView lastChild = (TextView) mGallery.getChildAt(lastPage);
                lastChild.setSelected(false);
                lastChild.setTextColor(normalColor);
                TextView child = (TextView) mGallery.getChildAt(page);
                child.setSelected(true);
                child.setTextColor(selColor);

                final int scrollPos = child.getLeft() - (mScrollView.getWidth() - child.getWidth()) / 2;
                mScrollView.smoothScrollTo(scrollPos, 0);
                lastPage = page;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        doGetCategory();
    }

    protected void doGetCategory() {
        if (isGetCategory) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            showToastShort(R.string.net_noconnection);
            loadingBar.setLoadingStatus(LoadingStatus.NOCONNECTION);
            return;
        }

        loadingBar.setLoadingStatus(LoadingStatus.START);
        isGetCategory = true;

        getCategoryTask = AgentServiceClient.doGetNewCategory(new ICallback() {

            @Override
            public void success(Object data) {
                NewsCategoryResponse response = (NewsCategoryResponse) data;
                if (response.isSuccessful()) {
                    loadSuccess(response.list);
                    loadingBar.setLoadingStatus(LoadingStatus.SUCCESS);
                } else {
                    loadFail();
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void failure(NetException e) {
                loadFail();
            }

            @Override
            public void end() {
                isGetCategory = false;
            }
        });
    }

    protected void loadFail() {
        loadingBar.setLoadingStatus(LoadingStatus.RELOAD);
    }

    private int normalColor;// 默认列表项字体颜色
    private int selColor = Color.WHITE;// 选中的列表项字体颜色

    private void loadSuccess(List<NewsCategory> newsCategories) {
        int size = newsCategories.size();
        mGallery.removeAllViews();
        if (newsCategories.isEmpty()) {
            return;
        }
        for (int i = 0; i < size; i++) {
            String name = newsCategories.get(i).name;
            TextView textView = new TextView(mContext);
            textView.setText(name);
            textView.setTextSize(15);
            textView.setTextColor(normalColor);
            LayoutParams params = new
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
//            textView.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));
            int d25 = DeviceUtil.dip2px(mContext, 25);
            textView.setPadding(d25, 0, d25, 0);
            textView.setGravity(Gravity.CENTER);
            final int page = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView mTextView = (TextView) v;
                    // 点击切换到指定页面
                    mViewPager.setCurrentItem(page, true);

                    mTextView.setSelected(true);
                    mTextView.setTextColor(selColor);
                }
            });
            mGallery.addView(textView, params);
        }
        TextView child = (TextView) mGallery.getChildAt(0);
        child.setTextColor(selColor);

        NewsPagerAdapter adapter = new NewsPagerAdapter(getChildFragmentManager(), newsCategories);
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == loadingBar && loadingBar.canLoading()) {
            doGetCategory();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != getCategoryTask) getCategoryTask.stop();
    }

    private class NewsPagerAdapter extends FragmentStatePagerAdapter {
        private List<NewsCategory> newsCategories;

        public NewsPagerAdapter(FragmentManager fm, List<NewsCategory> libraryEntities) {
            super(fm);
            this.newsCategories = libraryEntities;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = NewsListFrag.getParamBundle(newsCategories.get(position).code);
            return Fragment.instantiate(mContext,
                    NewsListFrag.class.getName(), bundle);
        }

        @Override
        public int getCount() {
            return newsCategories.size();
        }
    }
}
