package com.wlb.agent.ui.order.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.task.Task;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.insurance.entity.OrderNum;
import com.wlb.agent.core.data.insurance.response.OrderNumResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.view.BadgeView;
import com.wlb.agent.util.StringUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 订单列表主页
 * Created by Berfy
 */
public class OrderFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.layout_status1)//核保中
            LinearLayout mLlStatus1;
    @BindView(R.id.layout_status2)//待支付
            LinearLayout mLlStatus2;
    @BindView(R.id.layout_status3)//出单中
            LinearLayout mLlStatus3;
    @BindView(R.id.layout_status4)//完成
            LinearLayout mLlStatus4;

    private BadgeView mBadgeViewStatus1;
    private BadgeView mBadgeViewStatus2;
    private BadgeView mBadgeViewStatus3;
    private BadgeView mBadgeViewStatus4;

    private List<View> mTabList = new ArrayList<View>();
    private boolean mIsGetOrderCount;//避免重复请求
    private Task mGetOrderCountTask;

    private static final String POSITION = "position";
    private int mTab = -1, mPosition;
    private InsuranceOrderAdapter mAdapter;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.order,
                OrderFrag.class);
        SimpleFragAct.start(context, param);
    }

    public static void start(Context context, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.order,
                OrderFrag.class, bundle);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.order_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (null != getArguments()) {
            mPosition = getArguments().getInt(POSITION);
        }//订单数红点初始化
        mBadgeViewStatus1 = new BadgeView(mContext);
        mBadgeViewStatus1.setTargetView(mLlStatus1);
        mBadgeViewStatus1.setTextColor(Color.WHITE);
        mBadgeViewStatus1.setBackground(10, Color.RED);
        mBadgeViewStatus1.setBadgeMargin(0, 2, 5, 0);

        mBadgeViewStatus2 = new BadgeView(mContext);
        mBadgeViewStatus2.setTargetView(mLlStatus2);
        mBadgeViewStatus2.setTextColor(Color.WHITE);
        mBadgeViewStatus2.setBackground(10, Color.RED);
        mBadgeViewStatus2.setBadgeMargin(0, 2, 5, 0);

        mBadgeViewStatus3 = new BadgeView(mContext);
        mBadgeViewStatus3.setTargetView(mLlStatus3);
        mBadgeViewStatus3.setTextColor(Color.WHITE);
        mBadgeViewStatus3.setBackground(10, Color.RED);
        mBadgeViewStatus3.setBadgeMargin(0, 2, 5, 0);

        mBadgeViewStatus4 = new BadgeView(mContext);
        mBadgeViewStatus4.setTargetView(mLlStatus4);
        mBadgeViewStatus4.setTextColor(Color.WHITE);
        mBadgeViewStatus4.setBackground(10, Color.RED);
        mBadgeViewStatus4.setBadgeMargin(0, 2, 5, 0);

        mTabList.add(findViewById(R.id.layout_status1));
        mTabList.add(findViewById(R.id.layout_status2));
        mTabList.add(findViewById(R.id.layout_status3));
        mTabList.add(findViewById(R.id.layout_status4));
        mTabList.add(findViewById(R.id.layout_status5));

        List<OrderListFrag> orderListFrags = new ArrayList<>();
        //设置点击事件
        for (int i = 0; i < mTabList.size(); i++) {
            mTabList.get(i).setOnClickListener(this);
            Bundle bundle = new Bundle();
            bundle.putInt("position", i);
            OrderListFrag frag = (OrderListFrag) Fragment.instantiate(mContext, OrderListFrag.class.getName(), bundle);
            orderListFrags.add(frag);
        }
        mAdapter = new InsuranceOrderAdapter(getChildFragmentManager(), orderListFrags);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mTabList.size());//缓存所显示的页面
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switchPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        getTitleBar().setOnRightBtnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UserCacheUtil.getInstance(mContext).saveTuifuangfei(!UserCacheUtil.getInstance(mContext).getTuifuangfei());
//                updateSwitch();
//            }
//        });
//        if (UserCacheUtil.getInstance(mContext).getTuifuangfei()) {
//            getTitleBar().setRightBtnDrawable(R.drawable.ic_order_tuiguangfei_open);
//        } else {
//            getTitleBar().setRightBtnDrawable(R.drawable.ic_order_tuiguangfei_close);
//        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchPage(mPosition);
            }
        }, 200);
    }

    private Handler mHandler = new Handler();

    @Override
    public void onResume() {
        super.onResume();
        doGetOrderCount();
        if (mTab != -1 && null != mAdapter) {
            mAdapter.getFragments().get(mTab).doLoadData(0);
        }
    }

    /**
     * 我的订单数
     */
    private void doGetOrderCount() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetOrderCount) {
            return;
        }
        mGetOrderCountTask = UserClient.doGetOrderNum(new SimpleCallback() {
            @Override
            public void start() {
                mIsGetOrderCount = true;
            }

            @Override
            public void success(Object data) {
                OrderNumResponse response = (OrderNumResponse) data;
                if (response.isSuccessful()) {
                    if (null != response.getList()) {
                        List<OrderNum> datas = response.getList();
                        if (datas.size() > 0) {
                            for (OrderNum orderNum : datas) {
                                if (orderNum.getStatus() == 1) {
                                    mBadgeViewStatus1.setBadgeCount(orderNum.getOrderCount());
                                } else if (orderNum.getStatus() == 2) {
                                    mBadgeViewStatus2.setBadgeCount(orderNum.getOrderCount());
                                } else if (orderNum.getStatus() == 7) {
                                    mBadgeViewStatus3.setBadgeCount(orderNum.getOrderCount());
                                } else if (orderNum.getStatus() == 5) {
                                    mBadgeViewStatus4.setBadgeCount(orderNum.getOrderCount());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void end() {
                mIsGetOrderCount = false;
            }
        });
    }

    private void switchPage(int position) {
        LogUtil.e("切换位置", position + " ");
        if (mTab == position) {
            return;
        }
        for (int i = 0; i < mTabList.size(); i++) {
            if (i == position) {
                mAdapter.getFragments().get(i).doLoadData(0);
                mTabList.get(i).findViewById(StringUtil.getRes(mContext, "v_line" + (i + 1), "id"))
                        .setVisibility(View.VISIBLE);

            } else {
                mTabList.get(i).findViewById(StringUtil.getRes(mContext, "v_line" + (i + 1), "id"))
                        .setVisibility(View.INVISIBLE);
            }
        }
        mViewPager.setCurrentItem(position);
        mTab = position;
    }

    @OnClick({R.id.layout_search})
    @Override
    public void onClick(View v) {
        int pos = mTabList.indexOf(v);
        if (pos != -1) {
            switchPage(pos);
        }
        switch (v.getId()) {
            case R.id.layout_search:
                SimpleFragAct.start(mContext, new SimpleFragAct.SimpleFragParam(R.string.order_search, OrderSearchListFrag.class));
                break;
        }
    }

    private void updateSwitch() {
        if (UserClient.getTuifuangfei()) {
            getTitleBar().setRightBtnDrawable(R.drawable.ic_order_tuiguangfei_open);
        } else {
            getTitleBar().setRightBtnDrawable(R.drawable.ic_order_tuiguangfei_close);
        }
        for (OrderListFrag orderListFrag : mAdapter.getFragments()) {
            orderListFrag.setTuiguangfei(UserClient.getTuifuangfei());
        }
    }

    private class InsuranceOrderAdapter extends FragmentStatePagerAdapter {

        private List<OrderListFrag> mOrderListFrags;

        public InsuranceOrderAdapter(FragmentManager fm, List<OrderListFrag> orderListFrags) {
            super(fm);
            mOrderListFrags = orderListFrags;
        }

        public List<OrderListFrag> getFragments() {
            return mOrderListFrags;
        }

        @Override
        public Fragment getItem(int position) {
            return mOrderListFrags.get(position);
        }

        @Override
        public int getCount() {
            return mTabList.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mGetOrderCountTask) {
            mGetOrderCountTask.stop();
        }
    }
}
