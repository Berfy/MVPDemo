package com.wlb.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.util.LContext;
import com.android.util.log.LogUtil;
import com.wlb.common.ui.TitleBarView;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张全
 */
public class SimpleFragAct extends BaseActivity {
    private static final String FRAG_PARAM = "FRAG_PARAM";//
    protected static final String PARAM = "key_param";
    protected static final String BUNDLE = "key_bundle";
    private SimpleFragParam fragParam;

    /**
     * 传递数据
     */
    public static class SimpleFragParam implements Serializable {
        private static final long serialVersionUID = 8769472745632311186L;
        public String title;// 标题
        public String targetCls;// 目标Class
        public Bundle paramBundle;// 参数bundle
        public int outEnterAnim, outExitAnim;// 关闭页面时的动画
        private boolean coverTitleBar;// 可覆盖标题栏
        private boolean hideStatusBar;// 隐藏状态栏 全屏显示
        private boolean translucentBg;// 透明背景

        public SimpleFragParam(int titleId, Class<? extends Fragment> cls) {
            this.title = LContext.getString(titleId);
            this.targetCls = cls.getName();
        }

        public SimpleFragParam(Class<? extends Fragment> cls) {
            this.targetCls = cls.getName();
            coverTitleBar = true;
        }

        public SimpleFragParam(String title, Class<? extends Fragment> cls) {
            this.title = title;
            this.targetCls = cls.getName();
        }

        public SimpleFragParam(int titleId, Class<? extends Fragment> cls,
                               Bundle bundle) {
            this(LContext.getString(titleId), cls, bundle);
        }

        public SimpleFragParam(String title, Class<? extends Fragment> cls,
                               Bundle bundle) {
            this.title = title;
            this.targetCls = cls.getName();
            this.paramBundle = bundle;
        }

        public SimpleFragParam coverTilteBar(boolean coverTitleBar) {
            this.coverTitleBar = coverTitleBar;
            return this;
        }

        /**
         * 隐藏状态栏
         *
         * @param hide
         * @return
         */
        public SimpleFragParam hideStatusBar(boolean hide) {
            this.hideStatusBar = hide;
            return this;
        }

        /**
         * 设置透明背景
         *
         * @return
         */
        public SimpleFragParam setTranslucentBg(boolean transulcent) {
            this.translucentBg = transulcent;
            return this;
        }

        public SimpleFragParam setExitAnim(int outEnterAnim, int outExitAnim) {
            this.outEnterAnim = outEnterAnim;
            this.outExitAnim = outExitAnim;
            return this;
        }

        @Override
        public String toString() {
            return "SimpleFragParam [title=" + title + ", targetCls=" + targetCls + ", paramBundle=" + paramBundle
                    + ", outEnterAnim=" + outEnterAnim + ", outExitAnim=" + outExitAnim + ", coverTitleBar="
                    + coverTitleBar + ", hideStatusBar=" + hideStatusBar + ", translucentBg=" + translucentBg + "]";
        }

    }

    public static void start(Context ctx, SimpleFragParam param) {
        LogUtil.i("Activity动作", "跳转页面  " + param.title + "  " + param.targetCls);
        Intent intent = getStartIntent(ctx, param);
        ctx.startActivity(intent);
    }

    public static void start(Activity ctx, SimpleFragParam param,
                             int inEnterAnim, int inExitAnim) {
        LogUtil.i("Activity动作", "跳转页面  " + param.title + "  " + param.targetCls);
        Intent intent = getStartIntent(ctx, param);
        ctx.startActivity(intent);
        ctx.overridePendingTransition(inEnterAnim, inExitAnim);
    }

    public static void startForResult(Context ctx, SimpleFragParam param, int requestCode) {
        LogUtil.i("Activity动作", "跳转页面  " + param.title + "  " + param.targetCls);
        Intent intent = getStartIntent(ctx, param);
        ((Activity) ctx).startActivityForResult(intent, requestCode);
    }

    public static void start(Context ctx, SimpleFragParam param, int requestCode) {
        LogUtil.i("Activity动作", "跳转页面  " + param.title + "  " + param.targetCls);
        Intent intent = getStartIntent(ctx, param);
        ((Activity) ctx).startActivityForResult(intent, requestCode);
    }

    public static Intent getStartIntent(Context ctx, SimpleFragParam param) {
        Intent intent = new Intent(ctx, SimpleFragAct.class);
        // 给SimpleFragAct传递的数据
        intent.putExtra(PARAM, param);
        // 给fragment传递的数据
        if (null != param.paramBundle) {
            intent.putExtra(BUNDLE, param.paramBundle);
        }
        param.paramBundle = null;
        return intent;
    }

    @Override
    public int getLayoutId() {
        fragParam = (SimpleFragParam) getIntent()
                .getSerializableExtra(PARAM);
        // 设置全屏
        if (fragParam.hideStatusBar) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        // 透明背景
        if (fragParam.translucentBg) {
            setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        }
        return R.layout.common_act;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        SimpleFragParam fragParam = null;
        if (null != savedInstanceState) {
            fragParam = (SimpleFragParam) savedInstanceState.getSerializable(FRAG_PARAM);
        } else {
            fragParam = (SimpleFragParam) getIntent()
                    .getSerializableExtra(PARAM);
        }
        if (null == fragParam) {
            close();
            return;
        }

        TitleBarView mTitleBarView = (TitleBarView) findViewById(R.id.titlebar);
        View shadowView = findViewById(R.id.titlebar_shadow);

        // 覆盖标题栏
        if (fragParam.coverTitleBar || fragParam.hideStatusBar) {
            mTitleBarView.findViewById(R.id.title_view).setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(fragParam.title)) {
            // 设置标题
            mTitleBarView.setTitleText(fragParam.title);
//			mTitleBarView.setLeftText(fragParam.title);
        }

        if (null != savedInstanceState) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            System.out.println(fragments);
            if (null != fragments) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof SimpleFrag) {
                        SimpleFrag simpleFrag = (SimpleFrag) fragment;
                        simpleFrag.setTitleBarShdow(shadowView);
                        simpleFrag.setTitleBar(mTitleBarView);
                    }
                }
            }
        } else {
            // 加载fragment
            try {
                Class<?> frag = getClassLoader().loadClass(fragParam.targetCls);
                Bundle bundle = (Bundle) getIntent().getParcelableExtra(BUNDLE);
                if (null != bundle) {
                    bundle = new Bundle(bundle);
                }
                Fragment fragment = (Fragment) addFragment(frag, R.id.content,
                        bundle);
                if (fragment instanceof SimpleFrag) {
                    simpleFrag = (SimpleFrag) fragment;
                    simpleFrag.setTitleBarShdow(shadowView);
                    simpleFrag.setTitleBar(mTitleBarView);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                showToastShort("打开页面失败");
                close();
            }
        }
    }

    @Override
    public void close() {
        finish();
        if (fragParam.outEnterAnim > 0 && fragParam.outExitAnim > 0) {
            overridePendingTransition(fragParam.outEnterAnim, fragParam.outExitAnim);
        }
    }

    @Override
    public void onBackPressed() {
        if (null != simpleFrag && simpleFrag.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(FRAG_PARAM, fragParam);
    }

    private SimpleFrag simpleFrag;
}
