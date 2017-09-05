package com.wlb.agent.ui.main.frag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.android.util.ext.SoftInputUtil;
import com.android.util.ext.ToastUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.common.CommonBean;
import com.wlb.agent.ui.main.adapter.ChooseCityListSimpleAdapter;
import com.wlb.agent.util.DataParseUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.ui.pinyin.PinYinUtil;
import com.wlb.common.ui.pinyin.SideBar;
import com.wlb.common.ui.pinyin.SortModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.wlb.agent.R.id.sideBar;

/**
 * Created by Berfy on 2017/7/26.
 * 选择地区
 */

public class ChooseCitySimpleFrag extends SimpleFrag {

    private ChooseCityListSimpleAdapter mAdapter;
    public static final String PARAME_CITY = "city";
    public static final int REQUESTCODE = 1314;

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(sideBar)
    SideBar mSideBar;
    @BindView(R.id.tv_tip)
    TextView mTvTip;

    public static void start(Context context) {
        SimpleFragAct.startForResult(context, new SimpleFragAct.SimpleFragParam(R.string.home_choose_city, ChooseCitySimpleFrag.class), REQUESTCODE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.choose_city_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSideBar.setTextView(mTvTip);
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (mAdapter != null) {
                    // 该字母首次出现的位置
                    int position = mAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        mListView.setSelection(position);
                    }
                }
            }
        });
        mAdapter = new ChooseCityListSimpleAdapter(mContext, new OnCitySelectListener() {
            @Override
            public void search(String keywords) {
                ToastUtil.show("搜索" + keywords);
                int position = mAdapter.getPositionForSection(PinYinUtil.getInstance(mContext).getSortLetter(keywords).charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
                SoftInputUtil.hideSoftInput((Activity) mContext);
            }

            @Override
            public void select(String city) {
                SoftInputUtil.hideSoftInput((Activity) mContext);
                Intent intent = new Intent();
                intent.putExtra(PARAME_CITY, city);
                setResult(REQUESTCODE, intent);
                close();
            }
        });
        mListView.setAdapter(mAdapter);
        List<List<CommonBean>> datas = new ArrayList<>();
        List<CommonBean> list3 = new ArrayList<>();
        List<SortModel> citys = PinYinUtil.getInstance(mContext).getPinYinData(DataParseUtil.getInstance(mContext).getCitys());
        for (SortModel city : citys) {
            list3.add(new CommonBean(city));
        }
        datas.add(list3);
        mAdapter.setData(datas);
    }

    public interface OnCitySelectListener {
        void search(String keywords);

        void select(String city);
    }
}
