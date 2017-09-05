package com.wlb.agent.ui.main.adapter;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.common.CityBean;
import com.wlb.agent.core.data.common.CommonBean;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.main.frag.ChooseCityFrag;
import com.wlb.common.ui.pinyin.SortModel;

import java.util.List;

import common.widget.adapter.MultiListAdapter;
import common.widget.adapter.ViewHolder;
import common.widget.gridview.ScrollGridView;

/**
 * 选择城市弹出框
 *
 * @author Berfy
 */
public class ChooseCityListAdapter extends MultiListAdapter<CommonBean> implements SectionIndexer {

    private ChooseCityFrag.OnCitySelectListener mOnCitySelectListener;

    public ChooseCityListAdapter(Context context, ChooseCityFrag.OnCitySelectListener onCitySelectListener) {
        super(context, new int[]{R.layout.adapter_choose_city_search_item, R.layout.adapter_choose_city_hot_city_item
                , R.layout.adapter_choose_city_list_item});
        mOnCitySelectListener = onCitySelectListener;
    }

    @Override
    public void setViewData(int position, int type, ViewHolder holder, CommonBean data) {
        LogUtil.e("位置啊啊啊   ", position + "  " + type + "  " + data);
        if (type == getPositions()[0]) {
            ((EditText) holder.getView(R.id.edit_search)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        ToastUtil.show("搜索 " + textView.getText().toString());
                        if (null != mOnCitySelectListener) {
                            mOnCitySelectListener.search(textView.getText().toString());
                        }
                    }
                    return false;
                }
            });
        } else if (type == getPositions()[1]) {
            holder.setText(R.id.tv_gps_city, UserClient.getGPSCity());
            holder.getView(R.id.tv_gps_city).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCitySelectListener.select(UserClient.getGPSCity());
                }
            });
            ScrollGridView gridView = ((ScrollGridView) holder.getView(R.id.gridView));
            gridView.setNumColumns(4);
            ChooseCityHotGridListAdapter chooseCityHotGridListAdapter = new ChooseCityHotGridListAdapter(mContext);
            chooseCityHotGridListAdapter.refresh((List<CityBean>) data.getData());
            gridView.setAdapter(chooseCityHotGridListAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (null != mOnCitySelectListener) {
                        mOnCitySelectListener.select(chooseCityHotGridListAdapter.getItem(i).getName());
                    }
                }
            });
        } else {
            SortModel sortModel = (SortModel) data.getData();
            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);

            // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                holder.setVisibility(R.id.tv_type, View.VISIBLE);
                holder.setText(R.id.tv_type, sortModel.getSortLetters());
            } else {
                holder.setVisibility(R.id.tv_type, View.INVISIBLE);
            }
            holder.setText(R.id.tv_name, sortModel.getName());
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mOnCitySelectListener) {
                        mOnCitySelectListener.select(sortModel.getName());
                    }
                }
            });
        }
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if (getItemViewType(position) == getPositions()[2]) {//地区列表
            SortModel sortModel = (SortModel) getData().get(position).getData();
            return sortModel.getSortLetters().charAt(0);
        }
        return 0;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            if (getItemViewType(i) == getPositions()[2]) {//地区列表
                SortModel sortModel = (SortModel) getData().get(i).getData();
                String sortStr = sortModel.getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
