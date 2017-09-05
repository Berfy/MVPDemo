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
import com.android.util.ext.SoftInputUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.common.SimpleFrag;

import java.util.List;

import butterknife.BindView;
import common.widget.LoadingBar;
import common.widget.LoadingBar.LoadingStatus;
import common.widget.adapter.ListAdapter;
import common.widget.listview.swipelist.SwipeMenuListView;
import common.widget.listview.material.SwipeRefreshListView;

/**
 * 通用List Frag，滑动到底部可实现加载更多
 *
 * @author zhangquan
 */
public abstract class CommonListFrag extends SimpleFrag implements OnClickListener {

    private final String TAG = "CommonListFrag";
    private View footView;
    private TextView footTextView;
    private ProgressBar footProgressBar;
    protected ListAdapter mAdapter;
    private Task getListTask;
    private boolean hasMore = true;// 是否还有更多数据
    private boolean showLoadingBar = true;
    private boolean addFootView;
    protected boolean lazyLoad; //是否延迟加载

    @BindView(R.id.layout_top)
    FrameLayout mFlTitle;
    @BindView(R.id.headerGroup)
    FrameLayout mHeadContainer;
    @BindView(R.id.listview)
    SwipeRefreshListView mListView;
    @BindView(R.id.loadingBar)
    LoadingBar mLoadingBar;

    private int mImgRes;//没有内容时的图片
    private String mTipText;//没有内容时的文字
    private OnClickListener mOnClickListener;//没有内容时的操作的按钮点击事件
    private int mBtnRes;//没有内容时的操作的按钮背景
    private String mBtnText;//按钮文字

    @Override
    protected int getLayoutId() {
        return R.layout.common_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mLoadingBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e(TAG, "点击加载");
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    return;
                }
                doLoadData(0);
            }
        });
        initFootView();
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
                CommonListFrag.this.onItemClick(parent, view, pos, id);
            }
        });
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                SoftInputUtil.hideSoftInput(getActivity());
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

    protected void setOnTouchScrollListener(SwipeMenuListView.OnTouchScrollListener onTouchScrollListener) {
        mListView.setOnTouchScrollListener(onTouchScrollListener);
    }

    protected void setNoContentTip(int imgRes, String text, OnClickListener onClickListener, int btnBgRes, String btnText) {
        mImgRes = imgRes;
        mTipText = text;
        mOnClickListener = onClickListener;
        mBtnRes = btnBgRes;
        mBtnText = btnText;
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

    /**
     * 添加title
     *
     * @param titleView
     */
    protected void addTitleViewFrameLayout(View titleView) {
        mFlTitle.addView(titleView);
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

    public void doLoadData(long lastId) {
        if (null == mContext) {
            return;
        }
        if (isLoad) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            mListView.stopRefresh();
            if (showLoadingBar) mLoadingBar.setLoadingStatus(LoadingStatus.NOCONNECTION);
            changeFootViewStatus(FootLoadingStatus.NOCONNECTION);
            return;
        }
        curLastId = lastId;
        changeFootViewStatus(FootLoadingStatus.START);
        if (showLoadingBar) mLoadingBar.setLoadingStatus(LoadingStatus.START);
        getListTask = executeTask(lastId, callback);
    }

    protected ICallback callback = new ICallback() {

        @Override
        public void success(Object data) {
            BaseResponse response = (BaseResponse) (data);
            if (response.isSuccessful()) {
                //在第一页下拉刷新 还原状态
                if (curLastId == 0) {
                    if (null != footView) footView.setEnabled(true);
                    if (null != mAdapter) {
                        mAdapter.getList().clear();
                    }
                    LogUtil.e(TAG, "mAdapter.getList().clear()");
                }
                ListData listData = getDataListFromResponse(response);
                LogUtil.e(TAG, "getDataListFromResponse");
                lastId = listData.lastId;
                hasMore = listData.dataList.size() >= pageCount;
                setAdapter(listData.dataList);
            } else {
                loadFail();
            }
        }

        @Override
        public void start() {
            isLoad = true;
        }

        @Override
        public void failure(NetException e) {
            loadFail();
        }

        @Override
        public void end() {
            isLoad = false;
            mListView.stopRefresh();
        }
    };

    private void loadFail() {
        if (showLoadingBar) mLoadingBar.setLoadingStatus(LoadingStatus.RELOAD);
        changeFootViewStatus(FootLoadingStatus.FAIL);
    }

    private void setAdapter(List mList) {
        LogUtil.e(TAG, "setAdapter");
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
            if (mImgRes > 0) {
                mLoadingBar.setLoadingStatus(LoadingStatus.EMPTY, mImgRes, mTipText, mOnClickListener, mBtnRes, R.color.common_text, mBtnText);
            } else {
                mLoadingBar.setLoadingStatus(LoadingStatus.EMPTY);
            }
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
        if (null != getListTask) {
            getListTask.stop();
        }
    }

    @Override
    public void onClick(View v) {
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
     * @param callback
     * @return
     */
    protected abstract Task executeTask(long lastId, ICallback callback);

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
