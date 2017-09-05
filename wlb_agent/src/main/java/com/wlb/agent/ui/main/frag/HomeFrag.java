package com.wlb.agent.ui.main.frag;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.adapter.SimplePagerAdapter;
import com.wlb.agent.ui.common.banner.Banner;
import com.wlb.agent.ui.common.banner.BannerView;
import com.wlb.agent.ui.common.view.BadgeView;
import com.wlb.agent.ui.common.view.LooperTextView;
import com.wlb.agent.ui.offer.frag.ComfirmCarInfoFrag;
import com.wlb.agent.ui.user.frag.UserLoginFrag;
import com.wlb.agent.ui.user.frag.UserNotifyFrag;
import com.wlb.agent.util.DialogUtil;
import com.wlb.agent.util.GpsUtil;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页(App3.0新版)
 *
 * @author Berfy
 */
public class HomeFrag extends BaseFragment implements OnClickListener, GpsUtil.OnGpsListener {

    private final String TAG = "HomePageFrag";
    private PopupWindowUtil mPopupWindowUtil;
    private DialogUtil mDialogUtil;
    private GpsUtil mGpsUtil;
    private SimplePagerAdapter mAdapter;

    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.layout_left)
    LinearLayout mLlLeft;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_car_city)
    TextView mTvCarCity;
    @BindView(R.id.tv_car_number)
    TextView mTvCarNumber;
    @BindView(R.id.loop_notice)
    LooperTextView mLooperTextView;
    @BindView(R.id.bannerView)
    BannerView mBannerView;

    private BadgeView mNoticeUnReadNum;
    private String mCarNum = "";

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mDialogUtil = new DialogUtil(mContext);
        mGpsUtil = new GpsUtil(mContext);
        mGpsUtil.setListener(this);
        mGpsUtil.startGps();
        if (!TextUtils.isEmpty(mTvCarNumber.getText().toString())) {
            mCarNum = mTvCarNumber.getText().toString().trim().toUpperCase();
        }
        List<String> testNotices = new ArrayList<>();
        testNotices.add("测试1");
        testNotices.add("测试2");
        mLooperTextView.setTipList(testNotices);
        mNoticeUnReadNum = new BadgeView(mContext);
        mNoticeUnReadNum.setTargetView(mLlLeft);
        mNoticeUnReadNum.setTextColor(Color.WHITE);
        mNoticeUnReadNum.setTextSize(10);
        mNoticeUnReadNum.setBackground(10, Color.RED);
        mNoticeUnReadNum.setBadgeCount(99);
        mNoticeUnReadNum.setBadgeMargin(0, 5, 5, 0);
        List<View> views = new ArrayList<>();

        mAdapter = new SimplePagerAdapter();
        mAdapter.setData(views);
        mBannerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (DeviceUtil.getScreenWidthPx(mContext) / 2.8)));
        mBannerView.setAutoScrollEnable(true);
        mBannerView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBannerView.setOnItemClickListener(new BannerView.OnBannerItemClick() {
            @Override
            public void onItemClick(Banner item, int position) {

            }
        });

        List<Banner> testBanners = new ArrayList<>();
        testBanners.add(new Banner("fafa", 0, "afaf"));
        testBanners.add(new Banner("gfagag", 0, "afaf"));
        mBannerView.setData(testBanners);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.title_left_button, R.id.title_right_button, R.id.tv_city, R.id.tv_car_city, R.id.tv_car_city_tag, R.id.tv_car_number, R.id.btn_go})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_button:
                UserResponse info = UserClient.getLoginedUser();
                if (null != info) {
                    UserNotifyFrag.start(mContext);
                } else {
                    UserLoginFrag.start(mContext, UserLoginFrag.LoginTargetPage.MESSAGE, null);
                }
                break;
            case R.id.title_right_button:
                mPopupWindowUtil.showHotLine();
                break;
            case R.id.tv_city:
                ChooseCityFrag.start(mContext);
                break;
            case R.id.tv_car_city:
            case R.id.tv_car_city_tag:
                mPopupWindowUtil.showCarCity(new PopupWindowUtil.OnCarCitySelectListener() {
                    @Override
                    public void select(String text) {
                        mTvCarCity.setText(text);
                    }

                    @Override
                    public void opened() {
                        mScrollView.smoothScrollBy(0, DeviceUtil.getScreenHeightPx(mContext));
                    }
                });
                break;
            case R.id.tv_car_number:
                int[] locations = new int[2];
                mBannerView.getLocationOnScreen(locations);
                mPopupWindowUtil.showCarNumber(new PopupWindowUtil.OnCarNumberSelectListener() {
                    @Override
                    public void input(String text) {
                        mCarNum += text;
                        mTvCarNumber.setText(mCarNum);
                    }

                    @Override
                    public void delete() {
                        int size = mCarNum.length();
                        if (size > 0) {
                            mCarNum = mCarNum.substring(0, size - 1);
                        }
                        mTvCarNumber.setText(mCarNum);
                    }

                    @Override
                    public void opened() {
                        mScrollView.smoothScrollBy(0, DeviceUtil.getScreenHeightPx(mContext));
                    }
                });
                break;
            case R.id.btn_go:
                ComfirmCarInfoFrag.start(mContext);
                break;
        }
    }

    @Override
    public void onReceiveLocation(double lat, double lng, String province, String city,
                                  String distrct, String address) {
        ToastUtil.show("定位" + lat + "," + lng + "  城市" + province + " " + city + " "
                + distrct + " " + address);
        LogUtil.e(TAG, "定位" + lat + "," + lng + "  城市" + province + " " + city + " "
                + distrct + " " + address);
        mTvCity.setText(city);
    }

    @Override
    public void onError() {
        ToastUtil.show("定位错误");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGpsUtil.stopGps();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            if (requestCode == ChooseCityFrag.REQUESTCODE) {
                mTvCity.setText(data.getStringExtra(ChooseCityFrag.PARAME_CITY));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
