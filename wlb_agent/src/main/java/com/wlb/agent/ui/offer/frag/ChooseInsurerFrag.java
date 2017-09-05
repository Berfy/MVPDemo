package com.wlb.agent.ui.offer.frag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.Insurer;
import com.wlb.agent.ui.offer.adapter.ChooseInsurerListAdapter;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.ActivityManager;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.listview.ScrollListView;

/**
 * 选择保险公司
 * Created by Berfy on 2017/7/13.
 */
public class ChooseInsurerFrag extends SimpleFrag implements AdapterView.OnItemClickListener, View.OnClickListener {

    @BindView(R.id.listView)
    ScrollListView mListView;

    private ChooseInsurerListAdapter mAdapter;
    private PopupWindowUtil mPopupWindowUtil;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.choose_insurer, ComfirmCarInfoFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.offer_choose_insurer_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ActivityManager.getInstance().pushActivity("offer", getActivity());
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mAdapter = new ChooseInsurerListAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        //测试
        List<Insurer> datas = new ArrayList<>();
        datas.add(new Insurer("0", "1", true));
        datas.add(new Insurer("0", "2", false));
        datas.add(new Insurer("0", "3", false));
        mAdapter.refresh(datas);
    }

    @OnClick({R.id.btn_submit, R.id.layout_hotline})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    Insurer insurer = mAdapter.getList().get(i);
                    if (mAdapter.getList().get(i).isSelect()) {
                        Intent intent = new Intent();
                        intent.putExtra("brand", insurer.getId());
                        Bundle bundle = new Bundle();
                        bundle.putString("brand", insurer.getId());
                        SimpleFragAct.SimpleFragParam simpleFragParam = new SimpleFragAct.SimpleFragParam(R.string.choose_insurer_coverage,
                                ChooseInsuranceCoverageFrag.class);
                        simpleFragParam.paramBundle = bundle;
                        SimpleFragAct.start(mContext, simpleFragParam);
                    }
                }
                break;
            case R.id.layout_hotline:
                mPopupWindowUtil.showHotLine();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (i == position) {
                mAdapter.getList().get(i).setSelect(true);
            } else {
                mAdapter.getList().get(i).setSelect(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
