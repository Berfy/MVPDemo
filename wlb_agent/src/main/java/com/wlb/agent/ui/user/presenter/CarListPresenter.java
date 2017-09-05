package com.wlb.agent.ui.user.presenter;

import android.text.TextUtils;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.Ivehicle;
import com.wlb.agent.core.data.user.response.CarResponse;
import com.wlb.agent.ui.common.BasePresenter;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.user.adapter.MyCarAdapter;
import com.wlb.agent.ui.user.presenter.view.ICarListView;

import java.util.ArrayList;

import common.widget.LoadingBar;

/**
 * @author 张全
 */

public class CarListPresenter implements BasePresenter {
    private MyCarAdapter adapter;
    private ICarListView view;
    private Task deleteTesk;
    private Task getTask;
    private int type = -1;
    private String keywords;

    public CarListPresenter(ICarListView view){
        this.view=view;
        adapter = new MyCarAdapter(view.getContext(), new ArrayList<Ivehicle>(),
                R.layout.car_list_item);
        view.setAdapter(adapter);
    }

    public void deleteCar(int pos){
        Ivehicle ivehicle = adapter.getList().get(pos);
        doDelete(ivehicle);
    }

    private void doDelete(final Ivehicle ivehicle) {
        if (!NetworkUtil.isNetworkAvailable(view.getContext())) {
            ToastUtil.show("当前网络不可用");
            return;
        }
        if (isDeleteCar) return;
        deleteTesk = UserClient.doDeleteCar(ivehicle, new ICallback() {
            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    adapter.getList().remove(ivehicle);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isDeleteCar = true;
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isDeleteCar = false;
            }
        });
    }


    public void doGetCarList() {
        if (isGetCar) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(LContext.getContext())) {
            ToastUtil.show(R.string.net_noconnection);
            if (showLoadingBar) {
                view.setLoadingBarStatus(LoadingBar.LoadingStatus.NOCONNECTION);
            }
            view.setRefreshing(false);
            return;
        }
        if (showLoadingBar) {
            view.setLoadingBarStatus(LoadingBar.LoadingStatus.START);
        }
        if (!TextUtils.isEmpty(keywords)) {
            getTask = UserClient.doSearchCar(keywords, callback);
        } else {
            getTask = UserClient.doGetCarList(type, callback);
        }
    }

    ICallback callback = new ICallback() {

        @Override
        public void success(Object data) {
            CarResponse response = (CarResponse) data;
            if (response.isSuccessful()) {
                showLoadingBar=false;
                adapter.refresh(response.list);

                if(response.list.isEmpty()){
                    view.setLoadingBarStatus(LoadingBar.LoadingStatus.EMPTY);
                }else {
                    view.setLoadingBarStatus(LoadingBar.LoadingStatus.SUCCESS);
                }
            } else {
                loadFail();
            }
        }

        @Override
        public void start() {
            isGetCar = true;
        }

        @Override
        public void failure(NetException e) {
            loadFail();
        }

        @Override
        public void end() {
            isGetCar = false;
            view.setRefreshing(false);
        }
    };

    private void loadFail() {
        if (showLoadingBar) {
            view.setLoadingBarStatus(LoadingBar.LoadingStatus.RELOAD);
        }
    }

    private boolean isGetCar;
    private boolean isDeleteCar;
    private boolean showLoadingBar=true;

    public void loadCarList(int type) {
        if (this.type == type) {
            return;
        }
        this.type=type;
        showLoadingBar=true;
        doGetCarList();
    }
    public void loadCarList(String keywords) {
        if (keywords.equals(this.keywords)) {
            return;
        }
        this.keywords=keywords;
        showLoadingBar=true;
        doGetCarList();
    }

    public void onItemClick(int pos){
        Ivehicle car = adapter.getList().get(pos);
        if (!car.isNormalStatus()) {// 检查车辆是否是正常状态
            return;
        }

        // 车险报价
        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
        webViewParam.url = H5.INSURANCE_LIST + "?vehicleId=" + car.vehicleId + "&region=" + car.region;
        WebViewFrag.start(view.getContext(), webViewParam);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestory() {
        if (null != getTask) {
            getTask.stop();
        }
        if (null != deleteTesk) {
            deleteTesk.stop();
        }
    }
}
