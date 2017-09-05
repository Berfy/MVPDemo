package com.wlb.agent.ui.common;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.util.device.DeviceUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.common.SimpleFrag;
import java.util.List;
import common.widget.LoadingBar;
import common.widget.LoadingBar.LoadingStatus;
import common.widget.adapter.ListAdapter;
import common.widget.listview.swipelist.SwipeMenuListView;
import common.widget.listview.material.SwipeRefreshListView;
import rx.Subscriber;
import rx.Subscription;

/**
 * 通用List Frag，滑动到底部可实现加载更多
 * 基于RxJava
 *
 * @author zhangquan
 */
public abstract class CommonListFrag2 extends SimpleFrag implements OnClickListener {
    private SwipeRefreshListView mListView;
    private View footView;
    private TextView footTextView;
    private ProgressBar footProgressBar;
    private LoadingBar mLoadingBar;
    protected ListAdapter mAdapter;
    protected FrameLayout mHeadContainer;
    private Subscription subscription;
    private boolean hasMore = true;// 是否还有更多数据
    private boolean showLoadingBar = true;
    private boolean addFootView;
    protected boolean lazyLoad; //是否延迟加载

    @Override
    protected int getLayoutId() {
        return R.layout.common_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mHeadContainer = findViewById(R.id.headerGroup);
        mLoadingBar = (LoadingBar) findViewById(R.id.loadingBar);
        mLoadingBar.setOnClickListener(this);

        initFootView();

        mListView = findViewById(R.id.listview);
        mListView.setDivider(getResources().getDrawable(R.color.translate));
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position;
                int headViewCount = mListView.getListView().getHeaderViewsCount();
                if (headViewCount > 0) {
                    pos -= headViewCount;
                }
                if (pos < 0) return;
                CommonListFrag2.this.onItemClick(parent, view, pos, id);
            }
        });
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (hasMore && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        doLoadData(lastId);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        mListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doLoadData(0);
            }
        });

        initParams(savedInstanceState);
        if (!lazyLoad) doLoadData(0);
    }

    protected void setBackgroundColor(int color) {
        findViewById(R.id.container).setBackgroundColor(color);
    }

    protected void setDivider(Drawable drawable) {
        mListView.setDivider(drawable);
    }

    protected void setDividerHeight(int dividerH) {
        mListView.setDividerHeight(DeviceUtil.dip2px(mContext, dividerH));
    }

    protected void setDividerWithHeight(int resId, int dividerH) {
        setDividerWithHeight(getResources().getDrawable(resId), dividerH);
    }

    protected void setDividerWithHeight(Drawable drawable, int dividerH) {
        setDividerWithHeight(drawable, dividerH, false);
    }


    protected void setDividerWithHeight(int resId, int dividerH, boolean headDividerEnabled) {
        setDividerWithHeight(getResources().getDrawable(resId), dividerH, headDividerEnabled);
    }

    protected void setDividerWithHeight(Drawable drawable, int dividerH, boolean headDividerEnabled) {
        mListView.setDivider(drawable);
        mListView.setDividerHeight(DeviceUtil.dip2px(mContext, dividerH));
        if (headDividerEnabled) {
            setHeaderDividerEnabled();
        }
    }

    protected void setHeaderDividerEnabled() {
        SwipeMenuListView implListView = mListView.getListView();
        if (implListView.getHeaderViewsCount() > 0) {
            implListView.setHeaderDividersEnabled(true);
        } else {
            View headView = new View(mContext);
            headView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 1));
            headView.setBackgroundColor(Color.TRANSPARENT);
            addListHead(headView);
            implListView.setHeaderDividersEnabled(true);
        }
    }

    protected FrameLayout getmHeadContainer() {
        return mHeadContainer;
    }

    /**
     * 给ListView添加header
     *
     * @param headView
     */
    protected void addListHead(View headView) {
        getListView().addHeaderView(headView);
    }

    /**
     * 添加title
     *
     * @param titleView
     */
    protected void addTitleView(View titleView) {
        getmHeadContainer().addView(titleView);
    }

    protected SwipeRefreshListView getListView() {
        return mListView;
    }

    protected ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置每页加载的条数
     *
     * @param pageCount
     */
    public void setPageCount(int pageCount) {
        if (pageCount > 0)
            this.pageCount = pageCount;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    private boolean isLoad;
    private long lastId;
    private long curLastId;
    private int pageCount = 10;//每页加载的条数
    private boolean showFootBar = true;

    public void setLastId(long lastId) {
        this.lastId = lastId;
    }

    public long getLastId() {
        return this.lastId;
    }

    public void showLoadingBar() {
        this.showLoadingBar = true;
    }

    public void showFootBar(boolean showFootBar) {
        this.showFootBar = showFootBar;
    }

    public void reloadData() {
        showLoadingBar();
        doLoadData(0);
    }

    public void doLoadData(final long id) {
        if (isLoad) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            mListView.stopRefresh();
            if (showLoadingBar) mLoadingBar.setLoadingStatus(LoadingStatus.NOCONNECTION);
            changeFootViewStatus(FootLoadingStatus.NOCONNECTION);
            return;
        }
        curLastId = id;
        changeFootViewStatus(FootLoadingStatus.START);
        if (showLoadingBar) mLoadingBar.setLoadingStatus(LoadingStatus.START);
        subscription = executeTask(id, new Subscriber<BaseResponse>() {

            @Override
            public void onStart() {
                isLoad = true;
            }

            @Override
            public void onCompleted() {
                isLoad = false;
                mListView.stopRefresh();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                isLoad = false;
                mListView.stopRefresh();
                e.printStackTrace();
                loadFail();
            }

            @Override
            public void onNext(BaseResponse response) {
                if (response.isSuccessful()) {
                    //在第一页下拉刷新 还原状态
                    if (curLastId == 0) {
                        if (null != footView) footView.setEnabled(true);
                        if (null != mAdapter) {
                            mAdapter.getList().clear();
                        }
                    }
                    ListData listData = getDataListFromResponse(response);
                    System.out.println(listData.dataList);
                    lastId = listData.lastId;
                    hasMore = listData.dataList.size() >= pageCount;
                    setAdapter(listData.dataList);
                } else {
                    loadFail();
                }
            }
        });
    }

    private void loadFail() {
        if (showLoadingBar) mLoadingBar.setLoadingStatus(LoadingStatus.RELOAD);
        changeFootViewStatus(FootLoadingStatus.FAIL);
    }

    private void setAdapter(List mList) {
        if (!showFootBar) {
            hasMore = false;
        }

        if (!hasMore) {
            changeFootViewStatus(FootLoadingStatus.FINISHED);
        }

        if (hasMore && !addFootView) {
            mListView.addFooterView(footView);
            addFootView = true;
        }

        if (null == mAdapter) {
            mAdapter = initAdapterWithData(mList);
            mListView.setAdapter(mAdapter);
        } else {
            List dataList = mAdapter.getList();
            if (!mList.isEmpty()) {
                dataList.addAll(mList);
                mAdapter.refresh(dataList);
            }
        }

        List dataList = mAdapter.getList();
        if (dataList.isEmpty()) {
            mLoadingBar.setLoadingStatus(LoadingStatus.EMPTY);
        } else {
            mLoadingBar.setLoadingStatus(LoadingStatus.SUCCESS);
            showLoadingBar = false;
        }
    }

    private void initFootView() {
        footView = View.inflate(mContext, R.layout.common_list_footer, null);
        footProgressBar = (ProgressBar) footView.findViewById(R.id.footer_progressbar);
        footTextView = (TextView) footView.findViewById(R.id.footer_text);
        footView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doLoadData(lastId);
            }
        });
    }

    private void changeFootViewStatus(FootLoadingStatus status) {
        if (!addFootView) {
            return;
        }
        footTextView.setText(status.text);
        switch (status) {
            case START:// 开始加载
                footProgressBar.setVisibility(View.VISIBLE);
                break;
            case FAIL:// 加载失败
                footProgressBar.setVisibility(View.GONE);
                break;
            case NOCONNECTION:// 无网络连接
                footProgressBar.setVisibility(View.GONE);
                break;
            case FINISHED:// 完成加载
                footProgressBar.setVisibility(View.GONE);
                footView.setEnabled(false);
                break;
        }
    }

    public enum FootLoadingStatus {
        /**
         * 开始加载
         */
        START("数据加载中..."),
        /**
         * 加载失败
         */
        FAIL("加载失败"),
        /**
         * 无网络连接
         */
        NOCONNECTION("无网络连接"),
        /**
         * 完成加载
         */
        FINISHED("到底啦");

        private FootLoadingStatus(String text) {
            this.text = text;
        }

        public String text;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != subscription && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mLoadingBar && mLoadingBar.canLoading()) {
            LogUtil.e(TAG,"点击加载");
            doLoadData(lastId);
        }
        switch (v.getId()) {
            case R.id.loadingBar:
                LogUtil.e(TAG,"点击加载1");
                doLoadData(0);
                break;
        }
    }

    /**
     * 初始化参数
     *
     * @param savedInstanceState
     */
    protected abstract void initParams(Bundle savedInstanceState);

    /**
     * 执行网络加载任务
     *
     * @param subscriber
     * @return
     */
    protected abstract Subscription executeTask(long lastId, Subscriber<BaseResponse> subscriber);

    /**
     * 初始化adapter
     *
     * @return
     */
    protected abstract ListAdapter initAdapterWithData(List mDataList);

    /**
     * 从响应中获取数据列表
     *
     * @param baseResponse
     * @return
     */
    protected abstract ListData getDataListFromResponse(BaseResponse baseResponse);

    protected abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);


    public static class ListData {
        /**
         * 当前数据列表
         */
        public List dataList;
        /**
         * 最后一条数据的id
         */
        public long lastId;

        public ListData(List dataList, long lastId) {
            this.dataList = dataList;
            this.lastId = lastId;
        }

        public ListData(long lastId, List dataList) {
            this.dataList = dataList;
            this.lastId = lastId;
        }
    }
}
