package com.wlb.agent.ui.user.presenter.view;

import android.widget.BaseAdapter;

import com.wlb.agent.ui.common.BaseView;

import common.widget.LoadingBar;

/**
 * @author 张全
 */

public interface IListView  extends BaseView{
    /**
     * 设置列表Adapter
     * @param adapter
     */
    void setAdapter(BaseAdapter adapter);

    /**
     * 设置LoadingBar加载状态
     * @param loadingStatus
     */
    void setLoadingBarStatus(LoadingBar.LoadingStatus loadingStatus);

    /**
     * 设置刷新状态
     * @param refreshing
     */
    void setRefreshing(boolean refreshing);
}
