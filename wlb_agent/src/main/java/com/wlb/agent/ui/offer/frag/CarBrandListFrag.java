package com.wlb.agent.ui.offer.frag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.CarBrand;
import com.wlb.agent.ui.offer.adapter.CarBrandListAdapter;
import com.wlb.common.ActivityManager;
import com.wlb.common.SimpleFrag;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 车型列表
 * Created by Berfy on 2017/7/12.
 */

public class CarBrandListFrag extends SimpleFrag implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.listView)
    ListView mListView;
    private CarBrandListAdapter mCarBlandListAdapter;
    public static final int REQUEST_CODE = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.car_brand_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ActivityManager.getInstance().pushActivity("offer", getActivity());
        mCarBlandListAdapter = new CarBrandListAdapter(mContext);
        List<List<CarBrand>> datas = new ArrayList<>();
        List<CarBrand> top = new ArrayList<>();
        top.add(new CarBrand("", "", "", true));
        datas.add(top);
        //测试数据
        List<CarBrand> carBlands = new ArrayList<>();
        carBlands.add(new CarBrand("0", "esfafa", "", true));
        carBlands.add(new CarBrand("0", "gaegesa", "", false));
        carBlands.add(new CarBrand("0", "esfg2weqwafa", "", false));
        carBlands.add(new CarBrand("0", "gaegesa", "", false));
        carBlands.add(new CarBrand("0", "esfg2weqwafa", "", false));
        carBlands.add(new CarBrand("0", "gaegesa", "", false));
        carBlands.add(new CarBrand("0", "esfg2weqwafa", "", false));
        carBlands.add(new CarBrand("0", "gaegesa", "", false));
        carBlands.add(new CarBrand("0", "esfg2weqwafa", "", false));
        carBlands.add(new CarBrand("0", "gaegesa", "", false));
        carBlands.add(new CarBrand("0", "esfg2weqwafa", "", false));
        datas.add(carBlands);
        mCarBlandListAdapter.setData(datas);
        mListView.setAdapter(mCarBlandListAdapter);
        mListView.setOnItemClickListener(this);
    }

    @OnClick({R.id.tv_submit})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                for (int i = 0; i < mCarBlandListAdapter.getData().size(); i++) {
                    if (mCarBlandListAdapter.getItemViewType(i) == mCarBlandListAdapter.getPositions()[1]) {
                        CarBrand carBrand = mCarBlandListAdapter.getData().get(i);
                        if (mCarBlandListAdapter.getData().get(i).isSelect()) {
                            Intent intent = new Intent();
                            intent.putExtra("brand", carBrand.getBrand());
                            setResult(REQUEST_CODE, intent);
                            close();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        for (int i = 0; i < mCarBlandListAdapter.getData().size(); i++) {
            if (mCarBlandListAdapter.getItemViewType(i) == mCarBlandListAdapter.getPositions()[1]) {
                if (i == position) {
                    mCarBlandListAdapter.getData().get(i).setSelect(true);
                } else {
                    mCarBlandListAdapter.getData().get(i).setSelect(false);
                }
            }
        }
        mCarBlandListAdapter.notifyDataSetChanged();
    }
}
