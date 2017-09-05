package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.wlb.agent.R;
import com.wlb.agent.ui.common.CommonListFrag2;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.combo.ComboClient;
import com.wlb.agent.core.data.combo.response.ComboResponse;
import com.wlb.agent.ui.user.adapter.ComboListAdapter;
import com.wlb.common.SimpleFragAct;

import java.util.List;

import common.widget.adapter.ListAdapter;
import rx.Subscriber;
import rx.Subscription;

/**
 * 套餐列表
 * Created by JiaHongYa
 */

public class ComboListFrag extends CommonListFrag2 {

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("我的套餐", ComboListFrag.class);
        return param;
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(null);
        getTitleBar().setBackgroundColor(Color.WHITE);
        getTitleBar().setRightText("去购买");
        getTitleBar().setRightTextColor(getColor(R.color.titlebar_righttxt));
        getTitleBar().setOnRightTxtClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.url = H5.RECOMMAND_GOODS;
                WebViewFrag.start(mContext, webViewParam);
            }
        });
        setDividerWithHeight(R.drawable.transparent, 10, true);
        setBackgroundColor(getColor(R.color.c_eff3f6));
        getListView().setPullRefreshEnabled(false);
    }

    @Override
    protected Subscription executeTask(long lastId, Subscriber<BaseResponse> subscriber) {
        return ComboClient.doGetUserComboList().subscribe(subscriber);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        return new ComboListAdapter(mContext, mDataList, R.layout.combo_list_item);
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        ComboResponse response = (ComboResponse) baseResponse;
        List<ComboResponse.ComboEntity> list = response.getList();
        long lastId = 0;
        if (!list.isEmpty()) {
            lastId = list.get(list.size() - 1).getId();
        }
        return new CommonListFrag2.ListData(list, lastId);
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
