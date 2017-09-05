package com.wlb.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.bugtags.library.Bugtags;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity基类
 *
 * @author zhangquan
 */
public abstract class BaseActivity extends FragmentActivity {

    private static final String TAG = "BaseActivity";
    private String page;
    private boolean isPause;
    private Unbinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = this.getClass().getSimpleName();
        LogUtil.d(TAG, "onCreate " + page + ",pid=" + Process.myPid());
        View view = View.inflate(this, getLayoutId(), null);
        try {
            if (null != view) {
                setContentView(view);
                mBinder = ButterKnife.bind(this);
                init(savedInstanceState);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            return;
        }

//        setColor(this, getResources().getColor(R.color.common_red));
        setTranslucent();
        PushAgent.getInstance(this).onAppStart();
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    protected void setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    protected View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

    /**
     * 使状态栏透明
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     */
    protected void setTranslucent() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                getWindow().setStatusBarColor(Color.TRANSPARENT);
//            } else {
                // 设置状态栏透明
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            }
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            // 设置根布局的参数
//            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
//            rootView.setFitsSystemWindows(true);
//            rootView.setClipToPadding(true);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart " + page);
    }

    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume " + page);
        isPause = false;

        if (LContext.isDebug) {
            Bugtags.onResume(this);
        }

        // MobclickAgent.onPageStart(getPageName()); //统计页面
        boolean activityDurationOpen = AnalyticsConfig.ACTIVITY_DURATION_OPEN;
        if (!activityDurationOpen) { //自己统计页面跳转
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (null == fragments) {
                MobclickAgent.onPageStart(getPageName()); //统计页面跳转
            }
        }
        MobclickAgent.onResume(this); // 统计时长,默认也会统计跳转页面，如果MobclickAgent.openActivityDurationTrack(false)则需要自己统计跳转页面
    }

    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause " + page);
        isPause = true;

        if (LContext.isDebug) {
            Bugtags.onPause(this);
        }

        boolean activityDurationOpen = AnalyticsConfig.ACTIVITY_DURATION_OPEN;
        if (!activityDurationOpen) { //自己统计页面跳转
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (null == fragments) {
                MobclickAgent.onPageEnd(getPageName()); //统计页面跳转
            }
        }
        MobclickAgent.onPause(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop " + page);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy " + page);
        fixInputMethodManagerLeak(this);
        if (null != mBinder) {
            mBinder.unbind();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (LContext.isDebug) {
            Bugtags.onDispatchTouchEvent(this, ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        LogUtil.d(TAG, "onBackPressed "
                + this.getClass().getSimpleName());
        close();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(TAG, "onNewIntent " + page);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onRestoreInstanceState " + page);
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtil.d(TAG, "onSaveInstanceState " + page);
        //与Activity一起回收 页面没这么容易被回收
//        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (null != fragments) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    protected String getPageName() {
        return getClass().getSimpleName();
    }

    public void showSoftInput(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    public void hideSoftInput(View view) {
        InputMethodManager inputManger = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(this.getWindow().getDecorView()
                .getWindowToken(), 0);
    }

    public void hideSoftInput() {
        InputMethodManager inputManger = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(this.getWindow().getDecorView()
                .getWindowToken(), 0);
    }

    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void showToastShort(String s) {
        if (isFinishing() || isPause) {
            return;
        }
        // Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        // toast.show();

        ToastUtil.show(s);
    }

    public void showToastShort(int resId) {
        if (isFinishing() || isPause) {
            return;
        }
        // Toast toast = Toast
        // .makeText(this, getString(resId), Toast.LENGTH_SHORT);
        // toast.show();
        ToastUtil.show(resId);
    }

    public void showToastLong(String s) {
        if (isFinishing() || isPause) {
            return;
        }
        // Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        // toast.show();
        ToastUtil.showLong(s);
    }

    public void showToastLong(int resId) {
        if (isFinishing() || isPause) {
            return;
        }
        // Toast toast = Toast.makeText(this, getString(resId),
        // Toast.LENGTH_LONG);
        // toast.show();
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
    protected <T> T addFragment(Class<T> cls, int id, Bundle args) {
        T fragment = (T) Fragment.instantiate(this, cls.getName(), args);
        ((Fragment) fragment).setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, (Fragment) fragment);
        ft.commitAllowingStateLoss();
        return fragment;
    }

    protected void addFragment(Fragment fragment, int id) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment);
        ft.commitAllowingStateLoss();
    }

    public void removeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();
    }

    /**
     * 部分手机重写了系统的动画，这里用原生的动画
     *
     * @param cls
     */
    protected void startActWithSysAnim(Class<?> cls) {
        startAct(cls);
        overridePendingTransition(com.wlb.common.R.anim.activity_open_enter,
                com.wlb.common.R.anim.activity_open_exit);
    }

    protected void startAct(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startAct(intent);
    }

    protected void startAct(String action) {
        Intent intent = new Intent(action);
        startAct(intent);
    }

    protected void startAct(Intent intent) {
        startActivity(intent);
    }

    public abstract int getLayoutId();

    public abstract void init(Bundle savedInstanceState);

    public abstract void close();

    public void closeWithSysAnim() {
        finish();
        overridePendingTransition(com.wlb.common.R.anim.activity_close_enter,
                com.wlb.common.R.anim.activity_close_exit);
    }

}
