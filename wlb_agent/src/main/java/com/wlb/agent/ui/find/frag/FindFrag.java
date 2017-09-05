package com.wlb.agent.ui.find.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.entity.NewsInfo;
import com.wlb.agent.core.data.agentservice.response.MessageResponse;
import com.wlb.agent.core.data.agentservice.response.NewsListResponse;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.find.FindClient;
import com.wlb.agent.core.data.find.response.BannerResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.common.banner.Banner;
import com.wlb.agent.ui.common.banner.BannerView;
import com.wlb.agent.ui.common.view.BadgeView;
import com.wlb.agent.ui.find.adapter.FindListAdapter;
import com.wlb.agent.ui.user.frag.UserLoginFrag;
import com.wlb.agent.ui.user.frag.UserNotifyFrag;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.listview.swipelist.SwipeMenuListView;

/**
 * Created by Berfy on 2017/7/20.
 * 发现
 */
public class FindFrag extends CommonListFrag implements View.OnClickListener {

    private BannerView mBannerView;
    private ImageView mIvBannerDefault;//没有数据空白图
    private View mHeadView;
    private View mTitleView;

    LinearLayout mLlLeft;

    private Task mBannerTask;
    private boolean mIsGetBanner;

    private boolean mIsGetMsgCount;
    private Task mUnReadMsgTask;

    private PopupWindowUtil mPopupWindowUtil;
    private BadgeView mNoticeUnReadNum;

    public static void start(Context context) {
        SimpleFragAct.start(context, new SimpleFragAct.SimpleFragParam(R.string.find,
                FindFrag.class));
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mTitleView = View.inflate(mContext, R.layout.adapter_find_list_item_title, null);
        mHeadView = View.inflate(mContext, R.layout.adapter_find_list_item_top, null);
//        if (DeviceUtil.getAppVersionCode(mContext) > 30000) {
        addTitleViewFrameLayout(mTitleView);
//        } else {
//            addTitleView(mTitleView);
//        }
        addListHead(mHeadView);
        mLlLeft = (LinearLayout) mTitleView.findViewById(R.id.layout_left);
        mLlLeft.setOnClickListener(this);
        mTitleView.findViewById(R.id.title_left_button).setOnClickListener(this);
        mTitleView.findViewById(R.id.title_right_button).setOnClickListener(this);
        mBannerView = (BannerView) mHeadView.findViewById(R.id.bannerView);
        mIvBannerDefault = (ImageView) mHeadView.findViewById(R.id.iv_banner_default);
        mHeadView.findViewById(R.id.layout_user_manage).setOnClickListener(this);
        mHeadView.findViewById(R.id.layout_group).setOnClickListener(this);
        mHeadView.findViewById(R.id.layout_score).setOnClickListener(this);
        mHeadView.findViewById(R.id.layout_go).setOnClickListener(this);
        mBannerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (DeviceUtil.getScreenWidthPx(mContext) / 1.72)));
        mIvBannerDefault.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (DeviceUtil.getScreenWidthPx(mContext) / 1.72)));
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
            public void onItemClick(Banner banner, int position) {
                WebViewFrag.start(mContext, new WebViewFrag.WebViewParam(banner.getTitle(), false, banner.getPageUrl(), null));
            }
        });

        mNoticeUnReadNum = new BadgeView(mContext);
        mNoticeUnReadNum.setTargetView(mLlLeft);
        mNoticeUnReadNum.setTextColor(Color.WHITE);
        mNoticeUnReadNum.setBackground(10, Color.RED);
        mNoticeUnReadNum.setBadgeCount(99);
        mNoticeUnReadNum.setBadgeMargin(0, 5, 5, 0);
        getBanner();
        setOnTouchScrollListener(new SwipeMenuListView.OnTouchScrollListener() {
            @Override
            public void scroll(float y) {
                if (y / DeviceUtil.dip2px(mContext, 50) <= 1) {
                    mTitleView.setVisibility(View.VISIBLE);
                    mTitleView.setAlpha(1 - y / DeviceUtil.dip2px(mContext, 50));
                } else {
                    mTitleView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        doGetUnreadMsgCount();
    }

    private void getBanner() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetBanner) {
            return;
        }
        mBannerTask = FindClient.getBanner(new ICallback() {
            @Override
            public void start() {
                mIsGetBanner = true;
            }

            @Override
            public void success(Object data) {
                BannerResponse response = (BannerResponse) data;
                if (response.isSuccessful()) {
                    int size = response.getList().size();
                    LogUtil.e("banner数量", size + "");
                    if (size == 0) {
                        mIvBannerDefault.setVisibility(View.VISIBLE);
                        mBannerView.setVisibility(View.GONE);
                    } else {
                        mIvBannerDefault.setVisibility(View.GONE);
                        mBannerView.setVisibility(View.VISIBLE);
                        List<Banner> banners = new ArrayList<>();
                        for (com.wlb.agent.core.data.find.entity.Banner banner : response.getList()) {
                            Banner banner1 = new Banner(banner.title, banner.type, banner.thumbImage, banner.webUrl);
                            banners.add(banner1);
                        }
                        mBannerView.setData(banners);
                    }
                }
            }

            @Override
            public void failure(NetException e) {

            }

            @Override
            public void end() {
                mIsGetBanner = false;
            }
        });
    }

    /**
     * 未读消息
     */
    private void doGetUnreadMsgCount() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetMsgCount) {
            return;
        }
        mUnReadMsgTask = AgentServiceClient.doGetUnreadMessageCount(new SimpleCallback() {
            @Override
            public void start() {
                mIsGetMsgCount = true;
            }

            @Override
            public void success(Object data) {
                MessageResponse response = (MessageResponse) data;
                if (response.isSuccessful()) {
                    int unreadMessageCount = response.tipCount;
                    if (unreadMessageCount > 0) {
                        mNoticeUnReadNum.setVisibility(View.VISIBLE);
                        mNoticeUnReadNum.setBadgeCount(unreadMessageCount);
                    } else {
                        mNoticeUnReadNum.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void end() {
                mIsGetMsgCount = false;
            }
        });

    }

    @Override
    protected Task executeTask(long lastId, ICallback callback) {
        if (lastId == 0) {
            getBanner();
        }
//        List<Find> datas = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Find find = new Find("", "标题" + i, "", "", 123, 222);
//            datas.add(find);
//        }
//        FindResponse findResponse = new FindResponse(datas);
//        callback.success(findResponse);
//        callback.end();
        return AgentServiceClient.doGetNewsList("", lastId, getPageCount(), callback);
//        return new Task(null, null);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        FindListAdapter adapter = new FindListAdapter(mContext);
        adapter.setList(mDataList);
        return adapter;
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        NewsListResponse response = (NewsListResponse) baseResponse;
        long lastId = 0;
        if (!response.list.isEmpty()) {
            lastId = response.list.get(response.list.size() - 1).id;
        }
        return new ListData(lastId, response.list);
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewsInfo newsInfo = (NewsInfo) mAdapter.getList().get(position);
        WebViewFrag.start(mContext, new WebViewFrag.WebViewParam(newsInfo.title, false, newsInfo.url, null));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.layout_user_manage:
                ToastUtil.show(R.string.test);
                break;
            case R.id.layout_group:
                ToastUtil.show(R.string.test);
                break;
            case R.id.layout_score:
                ToastUtil.show(R.string.test);
                break;
            case R.id.layout_go:
                ToastUtil.show(R.string.test);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBannerTask) {
            mBannerTask.stop();
        }
        if (null != mUnReadMsgTask) {
            mUnReadMsgTask.stop();
        }
    }
}
