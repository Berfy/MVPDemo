package com.wlb.agent.ui.main.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.view.ViewHelper;
import com.wlb.agent.R;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

/**
 * @author 张全
 * 通知
 */
public class MsgFrag extends SimpleFrag implements View.OnClickListener {
    private View mPublicMsg;
    private View mPriviateMsg;
    private ViewPager viewPager;
    private ImageView tabIndicator;
    private int tab = -1;
    private int transDiss = 0;
    private int rawX;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam("通知", MsgFrag.class);
    }

    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.msg_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mPublicMsg = findViewById(R.id.msg_public);
        mPriviateMsg = findViewById(R.id.msg_priviate);
        mPublicMsg.setOnClickListener(this);
        mPriviateMsg.setOnClickListener(this);

        tabIndicator = findViewById(R.id.tab_indictor);

        viewPager = findViewById(R.id.viewpager);
        MsgPagerAdapter adapter = new MsgPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateIndicator(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                switchPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPublicMsg.post(new Runnable() {
                            @Override
                            public void run() {
                                float centerX = mPublicMsg.getWidth() / 2.0f;
                                int bw = tabIndicator.getDrawable().getIntrinsicWidth();
                                rawX = Math.round(centerX - bw / 2.0f);
                                transDiss = mPublicMsg.getWidth();
                                ViewHelper.setX(tabIndicator, rawX);
                            }
                        }

        );

        switchPage(0);
    }

    public void switchPage(int pos) {
        if (this.tab == pos)
            return;
        this.tab = pos;
        viewPager.setCurrentItem(tab, false);
        if (pos == 0) {
            mPriviateMsg.setSelected(true);
            mPublicMsg.setSelected(false);
        } else if (pos == 1) {
            mPublicMsg.setSelected(true);
            mPriviateMsg.setSelected(false);
        }
    }

    /**
     * 移动Indicator
     *
     * @param page
     * @param per
     */
    private void updateIndicator(int page, float per) {
        int transX = (int) ((page + per) * transDiss);
        ViewHelper.setX(tabIndicator, rawX + transX);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_priviate://消息
                switchPage(0);
                break;
            case R.id.msg_public://公告
                switchPage(1);
                break;
        }

    }

    private class MsgPagerAdapter extends FragmentStatePagerAdapter {
        public MsgPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int category = position == 0 ? 2 : 1;
            Bundle bundle = MsgListFrag.getParamBundle(category);
            return Fragment.instantiate(mContext,
                    MsgListFrag.class.getName(), bundle);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
