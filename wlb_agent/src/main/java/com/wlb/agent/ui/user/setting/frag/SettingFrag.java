package com.wlb.agent.ui.user.setting.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.view.AlertDialogView;
import com.wlb.agent.ui.user.frag.UserChangePasswordFrag;
import com.wlb.agent.ui.user.frag.UserLoginFrag;
import com.wlb.agent.ui.user.helper.SettingGroup;
import com.wlb.agent.ui.user.helper.SettingItem;
import com.wlb.agent.ui.user.setting.adapter.SettingAdapter;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.ActivityManager;
import com.wlb.common.BaseActivity;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.ui.TitleBarView;

import java.util.ArrayList;
import java.util.List;

import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;
import component.update.AppDownloadClient;
import component.update.AppVersion;

/**
 * 设置页面
 * Created by JiaHongYa
 */
public class SettingFrag extends SimpleFrag implements View.OnClickListener {

    private SettingAdapter adapter;
    private static int marginTop = -1;
    private List<SettingGroup> settingGroups = new ArrayList<SettingGroup>();
    private PopupWindowUtil mPopupWindowUtil;
    private Task mLogoutTask;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.setting, SettingFrag.class);
        param.coverTilteBar(true);
        return param;
    }

    public static void start(Context context) {
        getStartParam().coverTilteBar(true);
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.setting_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        TitleBarView titleBarView = findViewById(R.id.titlebar);
        titleBarView.setTitleText(getString(R.string.setting));
        initView();
    }

    private void initView() {
        final ListView mListView = findViewById(R.id.listview);
        adapter = new SettingAdapter(getContext(), settingGroups, this,
                R.layout.setting_item_group);
        View footView = View.inflate(getContext(), R.layout.setting_item_logout, null);
        Button btn_login = (Button) footView.findViewById(R.id.loginout);
        mListView.addFooterView(footView);
        mListView.setAdapter(adapter);
        setData();
        UserResponse loginedUser = UserClient.getLoginedUser();
        btn_login.setText(null != loginedUser ? R.string.login_out : R.string.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindowUtil.showConfirmPop("", getString(R.string.tip_logout), getString(R.string.sure),
                        getString(R.string.cancel), new PopupWindowUtil.OnPopConfirmctListener() {
                            @Override
                            public void ok() {
                                UserResponse loginedUser = UserClient.getLoginedUser();
                                if (null == loginedUser) {
                                    UserLoginFrag.start(mContext, null, null);
                                } else {
                                    doLoginout();
                                }
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            }
        });
        if (marginTop == -1) {
//            marginTop = (int) TypedValue.applyDimension(
//                    TypedValue.COMPLEX_UNIT_DIP, 0, getResources()
//                            .getDisplayMetrics());
        }
    }

    /**
     * 退出登录
     */
    public void doLoginout() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        final LoadingDialog dialog = new LoadingDialog(mContext,
                "退出登录中...").show();
        UserResponse loginedUser = UserClient.getLoginedUser();
        if (null == loginedUser) {
            return;
        }
        mLogoutTask = UserClient.doLoginout(loginedUser.token, new ICallback() {

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ActivityManager.getInstance().popAllActivityExceptOne(null);
                    UserLoginFrag.start(mContext, UserLoginFrag.LoginTargetPage.HOMEPAGE, null);
                    ((BaseActivity) getActivity()).close();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                dialog.dissmiss();
            }
        });
    }

    private boolean isLogined;

    private void setData() {
        settingGroups.clear();
        SettingGroup mGroup = null;
        SettingItem item = null;
        //-----------------第一分组
        mGroup = new SettingGroup();
        mGroup.topMargin = marginTop;
//        //修改密码
//        item = new SettingItem(R.id.setting_modify_pwd, getString(R.string.about_midify_pwd), mGroup);
//        mGroup.addItem(item);
//        //反馈意见
//        item = new SettingItem(R.id.setting_feedback, getString(R.string.about_feedback), mGroup);
//        mGroup.addItem(item);
        //关于我们
        item = new SettingItem(R.id.setting_about, getString(R.string.about_aboutus), mGroup);
        mGroup.addItem(item);
        settingGroups.add(mGroup);
        adapter.refresh(settingGroups);
    }

    private boolean isCheckVersion;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_feedback:// 用户反馈
                startFrag(R.string.fb_title, FeedbackFrag.class, false);
                break;
            case R.id.setting_about://关于我们
                AboutFrag.start(mContext);
                break;
            case R.id.setting_modify_pwd://修改密码
                UserChangePasswordFrag.start(mContext);
                break;
            default:
                break;

        }
    }

    private void showUpdateDialog(AppVersion appVersion) {
        String info = AppDownloadClient.getUpdateDes(appVersion);
        AlertDialogView dialogView = new AlertDialogView(mContext)
                .setTitle("发现新版本")
                .setContent(info, Gravity.LEFT)//
                .setRightBtn("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppDownloadClient.startDownloadApk();
                    }
                })
                .setLeftBtn("取消");

        new EffectDialogBuilder(mContext)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .setContentView(dialogView).show();
    }

    private void startFrag(int title, Class<? extends SimpleFrag> frag,
                           boolean fullScreen) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(title, frag);
        param.coverTilteBar(fullScreen);
        SimpleFragAct.start(getContext(), param);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mLogoutTask) {
            mLogoutTask.stop();
        }
    }
}
