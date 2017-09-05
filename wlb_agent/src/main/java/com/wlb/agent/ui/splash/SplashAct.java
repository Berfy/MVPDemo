package com.wlb.agent.ui.splash;

import android.os.Bundle;
import android.os.Handler;

import com.wlb.agent.R;
import com.wlb.agent.ui.main.TabAct;
import com.wlb.common.BaseActivity;
import com.wlb.common.SimpleFragAct;

/**
 * 启动页面
 *
 * @author 张全
 */
public class SplashAct extends BaseActivity {

    private Handler handler = new Handler();
    private final static int TIME = 1000;

    @Override
    public void init(Bundle savedInstanceState) {
        handler.postDelayed(runnable, TIME);
    }

    @Override
    public void close() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.splash;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != runnable) {
            handler.removeCallbacks(runnable);
        }
    }

    Runnable runnable = new Runnable() {
        public void run() {
            startAct();
        }
    };

    public void startAct() {
        if (!GuideFrag.hasShowGuide()) {
            SimpleFragAct.start(this, new SimpleFragAct.SimpleFragParam(GuideFrag.class));
        } else {
            TabAct.start(this);
        }
        finish();
    }
}
