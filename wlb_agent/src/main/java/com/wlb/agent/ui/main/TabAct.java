package com.wlb.agent.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.message.PushAgent;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.common.receiver.PushMsgHandler;
import com.wlb.agent.common.service.BackgroundTaskService;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.view.AlertDialogView;
import com.wlb.agent.ui.find.frag.FindFrag;
import com.wlb.agent.ui.main.frag.HomePageFrag;
import com.wlb.agent.ui.user.frag.UserCenterFrag2;
import com.wlb.agent.ui.user.frag.UserLoginFrag;
import com.wlb.agent.ui.user.helper.BackEventListener;
import com.wlb.common.ActivityManager;
import com.wlb.common.BaseActivity;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.share.ShareApi;
import common.share.SharePlatform;
import common.share.ShareUserInfo;
import common.share.UserInfoCallback;
import common.widget.dialog.EffectDialogBuilder;
import common.widget.viewpager.ViewPager;
import component.update.AppDownloadClient;
import component.update.AppDownloadService;
import component.update.AppVersion;
import component.update.VersionUpdateListener;
import rx.functions.Action1;

/**
 * 主页面
 *
 * @author 张全
 */
public class TabAct extends BaseActivity implements OnClickListener {

    private Class[] mClassNames = new Class[]{HomePageFrag.class, FindFrag.class, UserCenterFrag2.class};
    private List<Fragment> mFraments = new ArrayList<>();
    private ViewPager mViewPager;
    private boolean isDestroyed;
    private VersionUpdateListener updateListener;
    private static final String PARAM = "PARAM";
    private int mPage = -1;
    public static boolean IS_OPEN;
    static int counter = 0;

    private TabPagerAdapter mAdapter;

    @BindView(R.id.v_home)
    View mVHome;
    @BindView(R.id.v_faxian)
    View mVFaxian;
    @BindView(R.id.v_mine)
    View mVMine;
    @BindView(R.id.tv_home)
    TextView mTvHome;
    @BindView(R.id.tv_faxian)
    TextView mTvFaxian;
    @BindView(R.id.tv_mine)
    TextView mTvMine;

    public static void start(Context context) {
        UserResponse loginedUser = UserClient.getLoginedUser();
        if (null == loginedUser) { //未登录进入主页面
            UserLoginFrag.start(context, UserLoginFrag.LoginTargetPage.HOMEPAGE);
        } else {
            context.startActivity(getStartIntent(context, null));
        }
    }

    public static Intent getStartIntent(Context context, SimpleFragAct.SimpleFragParam param) {
        Intent intent = new Intent(context, TabAct.class);
        if (null != param) intent.putExtra(PARAM, param);
        return intent;
    }

    @Override
    public int getLayoutId() {
        return R.layout.tab_main_act;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        ActivityManager.getInstance().pushActivity(this);
        boolean hasParam = getIntent().hasExtra(PARAM);
        if (hasParam) {
            SimpleFragAct.SimpleFragParam param = (SimpleFragAct.SimpleFragParam) getIntent().getSerializableExtra(PARAM);
            SimpleFragAct.start(this, param);
        }
        TabAct.IS_OPEN = true;
        initView();
        initData();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setPagingEnabled(false);
        for (Class c : mClassNames) {
            mFraments.add(Fragment.instantiate(TabAct.this,
                    c.getName(), null));
        }
        mAdapter = new TabPagerAdapter(
                getSupportFragmentManager(), mFraments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switchPage(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager
                .setScrollDurationFactor(ViewPager.SWIPE_SCROLLER_FACTOR);
        mViewPager.setOffscreenPageLimit(mClassNames.length);
        switchPage(0);
    }

    private void initData() {
        //版本检测
        checkVersion();
        //注册事件
        EventBus.getDefault().register(this);
        //注册通知
        registPush();
        //获取第三方登录的用户信息
        getThirdPartyUserInfo();

        //apk二次打包校验
        BackgroundTaskService.start(this, 1);
        //读取联系人
        if (counter > 0) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(Manifest.permission.READ_CONTACTS)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean granted) {
                            if (granted) {
                                getContactInfo();
                            }
                        }
                    });
        }
        counter++;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (Constant.IntentAction.VEHICLE_PRICE.equals(action)) {
            //我的任务第一次查价
            switchPage(0);
        }
    }

    private void registPush() {
        try {
            PushAgent mPushAgent = PushAgent.getInstance(this);
            mPushAgent.onAppStart();
            mPushAgent.setDisplayNotificationNumber(3);
            mPushAgent.enable();
            PushMsgHandler.doOpenPush();
        } catch (Exception e) {

        }
    }

    /**
     * 版本检测
     */
    private void checkVersion() {
        // 清除下载通知
        if (!AppDownloadClient.hasNewVersion()) {
            AppDownloadClient.startDownloadService(this, AppDownloadService.ACTION_CLEAR_NOTIFICATION);
        }

        // 检测升级
        if (NetworkUtil.isNetworkAvailable(this)) {
            updateListener = new VersionUpdateListener() {

                @Override
                public void onNoVersionReturned() {
                    updateH5();
                }

                @Override
                public void fail() {
                    updateH5();
                }

                @Override
                public void onNewVersionReturned(AppVersion appVersion) {
                    if (isDestroyed) return;
                    String info = AppDownloadClient.getUpdateDes(appVersion);
                    AlertDialogView dialogView = new AlertDialogView(TabAct.this)
                            .setTitle("发现新版本")
                            .setContent(info, Gravity.LEFT)//
                            .setRightBtn("确定", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppDownloadClient.startDownloadApk();
                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    updateH5();
                                }
                            })
                            .setLeftBtn("取消");

                    new EffectDialogBuilder(TabAct.this)
                            .setCancelable(false)
                            .setCancelableOnTouchOutside(false)
                            .setContentView(dialogView).show();
                }
            };
            AppDownloadClient.doCheckVersion(updateListener);
        }
    }

    private void updateH5() {
//        try {
//            ((HomePageFrag) mAdapter.getItem(0)).checkUpdateAndGetH5Data();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateListener = null;
        AppDownloadClient.stopCheckVersion();
        EventBus.getDefault().unregister(this);
        ActivityManager.getInstance().popActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
        TabAct.IS_OPEN = false;
    }

    @Override
    public void close() {
        isDestroyed = true;
        finish();
    }

    @OnClick({R.id.layout_home, R.id.layout_faxian, R.id.layout_mine})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_home:
                switchPage(0);
                break;
            case R.id.layout_faxian:
                switchPage(1);
                break;
            case R.id.layout_mine:
                switchPage(2);
                break;
            default:

                break;
        }
    }

    public void switchPage(int pos) {
        if (mPage == pos)
            return;
        mVHome.setBackgroundResource(R.drawable.ic_tab_home);
        mVFaxian.setBackgroundResource(R.drawable.ic_tab_faxian);
        mVMine.setBackgroundResource(R.drawable.ic_tab_mine);
        mTvHome.setSelected(false);
        mTvFaxian.setSelected(false);
        mTvMine.setSelected(false);
        switch (pos) {
            case 0:
                mVHome.setBackgroundResource(R.drawable.ic_tab_home_press);
                mTvHome.setSelected(true);
                break;
            case 1:
                mVFaxian.setBackgroundResource(R.drawable.ic_tab_faxian_press);
                mTvFaxian.setSelected(true);
                break;
            case 2:
                mVMine.setBackgroundResource(R.drawable.ic_tab_mine_press);
                mTvMine.setSelected(true);
                break;
            default:

                break;
        }
        mViewPager.setCurrentItem(pos, false);

        mPage = pos;
        // 处理子页面的返回键
        try {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (null == fragments || fragments.isEmpty()) return;
            for (Fragment fragment : fragments) {
                if (fragment instanceof TabChangeListener) {
                    TabChangeListener tabChangeListener = (TabChangeListener) fragment;
                    tabChangeListener.OnTabChange(pos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TabPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> fragments = new ArrayList<>();

        public TabPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    long backTime;

    @Override
    public void onBackPressed() {

        // 处理子页面的返回键
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof BackEventListener) {
                BackEventListener backEventListener = (BackEventListener) fragment;
                if (backEventListener.handleBackEvent()) {
                    return;
                }
            }
        }

        long now = System.currentTimeMillis();
        if (now - backTime < 1 * 1500) {
            super.onBackPressed();
        } else {
            backTime = now;
            ToastUtil.show("再按一次返回键退出应用");
        }
    }

    //-------------------------------------获取第三方账号的用户信息
    private UserInfoCallback userInfoCallback;

    private void getThirdPartyUserInfo() {
        if (!UserClient.isThirdPartyLogin()) return;

        if (!NetworkUtil.isNetworkAvailable(LContext.getContext())) {
            return;
        }
        userInfoCallback = new UserInfoCallback() {
            @Override
            public void success(ShareUserInfo userInfo) {
                LogUtil.d("userInfo=" + userInfo);
                UserResponse loginedUser = UserClient.getLoginedUser();
                if (null != loginedUser) {
                    loginedUser.nick_name = userInfo.nickName;
                    loginedUser.avatar = userInfo.avater;
                    UserClient.updateLoginedUser(loginedUser);
                }
            }

            @Override
            public void fail(Exception e) {

            }
        };

        SharePlatform sharePlatform = SharePlatform.QQ;
        int thirdPartyPlatform = UserClient.getThirdPartyPlatform();
        if (thirdPartyPlatform == 2) {
            sharePlatform = SharePlatform.WEIXIN;
        } else if (thirdPartyPlatform == 3) {
            sharePlatform = SharePlatform.SINA;
        }
        ShareApi.getInstance().getUserInfo(this, sharePlatform, userInfoCallback);
    }

    //-------------------------读取联系人-----------------

    public void getContactInfo() {
        Cursor cursor = null;
        Cursor phoneCursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            int contactIdIndex = 0;
            int nameIndex = 0;
            JSONArray jsonArray = new JSONArray();
            if (cursor.getCount() > 0) {
                contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            }
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(contactIdIndex);
                String name = cursor.getString(nameIndex);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("contactsName", name);
                JSONArray phoneArray = new JSONArray();

            /*
             * 查找该联系人的phone信息
             */
                phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                        null, null);
                int phoneIndex = 0;
                if (phoneCursor.getCount() > 0) {
                    phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                }
                while (phoneCursor.moveToNext()) {
                    String phoneNumber = phoneCursor.getString(phoneIndex);
                    phoneArray.put(phoneNumber);
                }
                jsonObject.put("contactsPhoneArray", phoneArray);
                jsonArray.put(jsonObject);
            }
            BackgroundTaskService.postContacts(this, jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != cursor) cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != phoneCursor) phoneCursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(PushEvent event) {
        if (event.getAction() == Constant.IntentAction.ORDER_GO) {
            switchPage(0);
        }
    }

}
