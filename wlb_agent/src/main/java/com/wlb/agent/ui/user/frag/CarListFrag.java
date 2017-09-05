package com.wlb.agent.ui.user.frag;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.android.util.device.DeviceUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.user.presenter.CarListPresenter;
import com.wlb.agent.ui.user.presenter.view.ICarListView;
import com.wlb.common.SimpleFrag;

import org.greenrobot.eventbus.EventBus;

import common.widget.LoadingBar;
import common.widget.listview.material.SwipeRefreshListView;
import common.widget.listview.swipelist.SwipeMenu;
import common.widget.listview.swipelist.SwipeMenuCreator;
import common.widget.listview.swipelist.SwipeMenuItem;
import common.widget.listview.swipelist.SwipeMenuListView;


/**
 * 车辆列表
 * @author 张全
 */

public class CarListFrag extends SimpleFrag implements ICarListView, View.OnClickListener, AdapterView.OnItemClickListener {

    private LoadingBar loadingBar;
    private SwipeRefreshListView mRefreshListView;

    private CarListPresenter carListPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.car_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mRefreshListView = findViewById(R.id.listview);
        mRefreshListView.setOnItemClickListener(this);
        mRefreshListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                carListPresenter.doGetCarList();
            }
        });
        mRefreshListView.setDivider(getResources().getDrawable(R.drawable.line));
        mRefreshListView.setSwipeEnabled(true);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.c_e7e7e7)));
                deleteItem.setWidth(DeviceUtil.dip2px(mContext, 30));
//                deleteItem.setHeight(DeviceUtil.dip2px(context, 40));
                deleteItem.setBackgroundW(DeviceUtil.dip2px(mContext, 80));
                deleteItem.setIcon(R.drawable.me_collection_delete_icon_selected);
                menu.addMenuItem(deleteItem);
            }
        };
        mRefreshListView.setMenuCreator(creator);
        mRefreshListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        carListPresenter.deleteCar(position);
                }
                return false;
            }
        });

        loadingBar = findViewById(R.id.loadingBar);
        loadingBar.setOnClickListener(this);
        carListPresenter = new CarListPresenter(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        int pos = position;
        int headerViewsCount = mRefreshListView.getListView().getHeaderViewsCount();
        pos -= headerViewsCount;
        if (pos < 0) return;
        carListPresenter.onItemClick(pos);
    }

    @Override
    public void onClick(View v) {
        if (v == loadingBar && loadingBar.canLoading()) {
            carListPresenter.doGetCarList();
        }
    }

    public void loadCarList(int type) {
        carListPresenter.loadCarList(type);
    }

    public void loadCarList(String keywords) {
        carListPresenter.loadCarList(keywords);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        carListPresenter.onDestory();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        mRefreshListView.setAdapter(adapter);
    }

    @Override
    public void setLoadingBarStatus(LoadingBar.LoadingStatus loadingStatus) {
        loadingBar.setLoadingStatus(loadingStatus);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mRefreshListView.setRefreshing(false);
    }
}
