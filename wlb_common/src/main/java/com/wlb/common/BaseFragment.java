package com.wlb.common;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment基类
 *
 * @author zhangquan
 */
public abstract class BaseFragment extends Fragment {

    protected View mView;
    protected Context mContext;
    protected boolean isDestoryed;
    private boolean isPause;
    public static final String TAG = "BaseFragment";
    protected List<String> actionList = new ArrayList<String>();
    private boolean hasRegistedEventBus;
    private Unbinder mBinder;
    private OnFragmentListener mOnFragmentListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mContext = getActivity();
        isDestoryed = false;
        LogUtil.d(TAG, "onCreate " + getPageObjName());
        com.wlb.common.ActivityManager.getInstance().pushActivity(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView " + getPageName());
        mView = inflater.inflate(R.layout.frag_base, container, false);
        ((LinearLayout) mView.findViewById(R.id.layout_content)).addView(inflater.inflate(getLayoutId(), null, false),
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mBinder = ButterKnife.bind(this, mView);
        init(savedInstanceState);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != mOnFragmentListener)
            mOnFragmentListener.onCreate();
    }

    public void setListener(OnFragmentListener onFragmentListener) {
        mOnFragmentListener = onFragmentListener;
    }

    @Override
    public void onResume() {
        LogUtil.d(TAG, "onResume " + getPageObjName());
        super.onResume();
        isPause = false;

        if (isRecordClick()) {
            MobclickAgent.onPageStart(getPageName());
        }
    }

    @Override
    public void onPause() {
        LogUtil.d(TAG, "onPause " + getPageObjName());
        super.onPause();
        isPause = true;
        if (isRecordClick()) {
            MobclickAgent.onPageEnd(getPageName());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop " + getPageName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy " + getPageObjName());
        isDestoryed = true;
        if (!actionList.isEmpty()) {
            EventBus.getDefault().unregister(this);
        }
        if (null != mBinder) {
            mBinder.unbind();
        }
        com.wlb.common.ActivityManager.getInstance().popActivity(getActivity());
    }

    protected String getPageName() {
        return getClass().getSimpleName();
    }

    private String getPageObjName() {
        return getPageName() + hashCode();
    }

    protected boolean isRecordClick() {
        return !AnalyticsConfig.ACTIVITY_DURATION_OPEN;
    }

    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    public Context getContext() {
        return getActivity();
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(int id) {
        View view = mView.findViewById(id);
        return (T) view;
    }

    public void setOnClickListener(int id, OnClickListener listener) {
        mView.findViewById(id).setOnClickListener(listener);
    }

    public void setOnClickListener(View view, OnClickListener listener) {
        view.setOnClickListener(listener);
    }

    protected void startService(Class<? extends Service> service) {
        startService(new Intent(mContext, service));
    }

    protected void startService(Intent intent) {
        mContext.startService(intent);
    }

    protected void stopService(Class<? extends Service> service) {
        mContext.stopService(new Intent(mContext, service));
    }

    /**
     * 部分手机重写了系统的动画，这里用原生的动画
     *
     * @param cls
     */
    protected void startActWithSysAnim(Class<?> cls) {
        startAct(cls);
        overridePendingTransition(R.anim.activity_open_enter,
                R.anim.activity_open_exit);
    }

    protected void startAct(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        startAct(intent);
    }

    protected void startAct(String action) {
        Intent intent = new Intent(action);
        startAct(intent);
    }

    protected void startAct(Intent intent) {
        startActivity(intent);
        if (CommonApp.swipeFinish) {
            overridePendingTransition(R.anim.base_slide_right_in,
                    R.anim.base_slide_remain);
        }
    }

    protected void finish() {
        getActivity().finish();
    }

    public void close() {
        if (getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.close();
        } else {
            finish();
        }
    }

    public void showSoftInput(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void hideSoftInput(View view) {
        InputMethodManager inputManger = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(getActivity().getWindow()
                .getDecorView().getWindowToken(), 0);
    }

    public void hideSoftInput() {
        InputMethodManager inputManger = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(getActivity().getWindow()
                .getDecorView().getWindowToken(), 0);
    }


    protected void overridePendingTransition(int enterAnim, int exitAnim) {
        getActivity().overridePendingTransition(enterAnim, exitAnim);
    }

    public void showToastShort(String s) {
        FragmentActivity activity = getActivity();
        if (null == activity || isRemoving() || activity.isFinishing()
                || isPause) {
            return;
        }
        ToastUtil.show(s);
    }

    public void showToastShort(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity || isRemoving() || activity.isFinishing()
                || isPause) {
            return;
        }
        ToastUtil.show(resId);
    }

    public void showToastLong(String s) {
        FragmentActivity activity = getActivity();
        if (null == activity || isRemoving() || activity.isFinishing()
                || isPause) {
            return;
        }
        ToastUtil.showLong(s);
    }

    public void showToastLong(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity || isRemoving() || activity.isFinishing()
                || isPause) {
            return;
        }
        ToastUtil.showLong(resId);
    }

    /**
     * 实例化并添加UI组件.
     *
     * @param cls  组件
     * @param id   组件资源id
     * @param args 组件参数
     * @return 实例化的组件
     */
    @SuppressWarnings("unchecked")
    protected <T> T addFragment(int id, Class<T> cls, Bundle args) {
        FragmentActivity activity = getActivity();
        if (null == cls || id <= 0 || null == activity)
            return null;
        T fragment = (T) Fragment.instantiate(activity, cls.getName(), null);
        Fragment frag = (Fragment) fragment;
        frag.setArguments(args);
        addFragment(id, frag);
        return fragment;
    }

    protected void addFragment(int id, Fragment fragment) {
        FragmentActivity activity = getActivity();
        if (null == activity || null == fragment)
            return;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(id, fragment);
        ft.commitAllowingStateLoss();
    }

    @SuppressWarnings("unchecked")
    protected <T> T replaceFragment(int id, Class<T> cls, Bundle args) {
        FragmentActivity activity = getActivity();
        if (null == cls || id <= 0 || null == activity)
            return null;
        T fragment = (T) Fragment.instantiate(activity, cls.getName(), null);
        Fragment frag = (Fragment) fragment;
        frag.setArguments(args);
        replaceFragment(id, frag);
        return fragment;
    }

    protected void replaceFragment(int id, Fragment fragment) {
        FragmentActivity activity = getActivity();
        if (null == activity || null == fragment)
            return;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(id, fragment);
        ft.commitAllowingStateLoss();
    }

    protected void addFragment(Fragment fragment, int id) {
        FragmentActivity activity = getActivity();
        if (null == activity || null == fragment)
            return;
        FragmentTransaction ft = activity.getSupportFragmentManager()
                .beginTransaction();
        ft.replace(id, fragment);
        ft.commitAllowingStateLoss();
    }

    public void removeFragment(Fragment fragment) {
        FragmentActivity activity = getActivity();
        if (null == activity || null == fragment)
            return;
        FragmentTransaction ft = activity.getSupportFragmentManager()
                .beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
    }

    // #########################################################
    public int getColor(int color) {
        return getActivity().getResources().getColor(color);
    }

    public final void setResult(int resultCode) {
        getActivity().setResult(resultCode);
    }

    public final void setResult(int resultCode, Intent data) {
        getActivity().setResult(resultCode, data);
    }

    // #########################################################

    public void addAction(String action) {
        if (!actionList.contains(action))
            actionList.add(action);
        if (!hasRegistedEventBus) {
            EventBus.getDefault().register(this);
        }
        hasRegistedEventBus = true;
    }

    public boolean containsAction(String action) {
        return actionList.contains(action);
    }

    // #########################################################

    protected abstract int getLayoutId();

    protected abstract void init(Bundle savedInstanceState);

    public interface OnFragmentListener {
        void onCreate();
    }
}
