package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.wlb.agent.Constant.IntentAction;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.SimpleFragAct.SimpleFragParam;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 车辆列表
 *
 * @author 张全
 */

public class CarFrag extends SimpleFrag implements OnClickListener {

    private CarListFrag carListFrag;
    private int selType;
    private List<Filter> filters = new ArrayList<>();

    public static SimpleFragParam getStartParam() {
        SimpleFragParam param = new SimpleFragParam(
                R.string.usercenter_cars, CarFrag.class);
        return param;
    }

    public static void start(Context context) {
        SimpleFragParam param = getStartParam();
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.car_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        addAction(IntentAction.ORDER_BUY_SUCCESS);
        addAction(IntentAction.ORDER_BUY_FAIL);

        getTitleBar().setRightText("添加车辆");
        TextView mRightTextView = getTitleBar().mRightTxtView;
        getTitleBar().setCompoundDrawable(mRightTextView, R.drawable.add_car, 4);
        mRightTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addCar();
            }
        });

        carListFrag = replaceFragment(R.id.carlist, CarListFrag.class, null);
        findViewById(R.id.seekbar).setOnClickListener(this);
        findViewById(R.id.seek_content).setOnClickListener(this);
        findViewById(R.id.btn_select).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        carListFrag.loadCarList(selType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (containsAction(action)) {
            if (IntentAction.ORDER_BUY_SUCCESS.equals(action)//
                    || IntentAction.ORDER_BUY_FAIL.equals(action)
                    ) {
                close();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seekbar:
            case R.id.seek_content:
                CarSeekFrag.start(mContext);
                break;
            case R.id.btn_select:
                showFilterBar();
                break;
        }
    }

    private void addCar() {
        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
        webViewParam.url = H5.checkUrl(H5.HOMEPAGE_OFFER);
        WebViewFrag.start(mContext, webViewParam);
        close();
    }

    BottomSheetDialog filterDialog;

    private void showFilterBar() {
        if (null == filterDialog) {
            ListView listView = (ListView) View.inflate(mContext, R.layout.common_listview, null);

            filters.add(new Filter("已投保车辆", 21));
            filters.add(new Filter("未投保车辆", 20));
            filters.add(new Filter("车险到期时间(由近到远)", 10));
            filters.add(new Filter("车险报价时间(由近到远)", 11));
            Filter filter = new Filter("全部车辆", 0);
            filter.isSelected = true;
            filters.add(filter);

            FilterListAdapter adapter = new FilterListAdapter(mContext, filters, R.layout.car_list_filter);
            listView.setAdapter(adapter);
            filterDialog = new BottomSheetDialog(mContext);
            filterDialog.setContentView(listView);

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (Filter filter : filters) {
                        filter.isSelected = false;
                    }
                    Filter filter = filters.get(position);
                    filter.isSelected = true;
                    selType = filter.type;
                    carListFrag.loadCarList(filter.type);
                    filterDialog.dismiss();
                }
            });
        }
        filterDialog.show();

    }

    private static class Filter {
        public String filterName;
        public int type;
        public boolean isSelected;

        public Filter(String filterName, int type) {
            this.filterName = filterName;
            this.type = type;
        }
    }

    private class FilterListAdapter extends ListAdapter<Filter> {

        public FilterListAdapter(Context context, List<Filter> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        public void setViewData(ViewHolder holder, Filter data) {
            holder.setText(R.id.item_filterName, data.filterName);
            holder.setSelected(R.id.item_filterName, data.isSelected);
        }
    }

}
