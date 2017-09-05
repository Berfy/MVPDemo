package com.wlb.agent.ui.offer.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.InsurerConverage;
import com.wlb.agent.ui.offer.adapter.ChooseInsurerConverageListAdapter;
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
 * 选择险种
 * Created by Berfy on 2017/7/13.
 */
public class ChooseInsuranceCoverageFrag extends SimpleFrag implements View.OnClickListener {

    private ChooseInsurerConverageListAdapter mAdapter;

    @BindView(R.id.listView)
    ScrollListView mListView;

    private PopupWindowUtil mPopupWindowUtil;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.choose_insurer_coverage, ComfirmCarInfoFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.offer_choose_insurer_converage_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ActivityManager.getInstance().pushActivity("offer", getActivity());
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mAdapter = new ChooseInsurerConverageListAdapter(mContext);
        mListView.setAdapter(mAdapter);
        //测试
        List<InsurerConverage> datas = new ArrayList<>();
        datas.add(new InsurerConverage("0", "交强险/车船税", "", "", false, false, true));
        datas.add(new InsurerConverage("0", "机动车辆损失险", "", "", true, true, true));
        datas.add(new InsurerConverage("0", "第三者责任险", "100", "", true, true, true));
        datas.add(new InsurerConverage("0", "盗抢险", "", "", true, false, false));
        datas.add(new InsurerConverage("0", "司机险", "", "", true, false, false));
        datas.add(new InsurerConverage("0", "乘客险", "", "", true, false, false));
        datas.add(new InsurerConverage("0", "玻璃险", "", "", false, false, false));
        datas.add(new InsurerConverage("0", "无法找到第三方特约险", "", "", false, false, false));
        datas.add(new InsurerConverage("0", "划痕险", "", "", false, false, false));
        datas.add(new InsurerConverage("0", "自燃险", "", "", false, false, false));
        datas.add(new InsurerConverage("0", "涉水险", "", "", false, false, false));
        datas.add(new InsurerConverage("0", "指定修理厂险", "", "", false, false, false));
        datas.add(new InsurerConverage("0", "精神损害抚慰金责任险", "", "", false, false, false));
        mAdapter.refresh(datas);
    }

    @OnClick({R.id.btn_submit, R.id.layout_hotline})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                SimpleFragAct.SimpleFragParam simpleFragParam = new SimpleFragAct.SimpleFragParam(R.string.offer, OfferFrag.class);
                SimpleFragAct.start(mContext, simpleFragParam);
                break;
            case R.id.layout_hotline:
                mPopupWindowUtil.showHotLine();
                break;
        }
    }
}
