package com.wlb.common;

import android.view.View;

import com.wlb.common.ui.TitleBarView;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author 张全
 */
public abstract class SimpleFrag extends BaseFragment {

    public TitleBarView titleBarView;
    public View mTitleBarShadow;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public void setTitleBar(TitleBarView titleBarView) {
        this.titleBarView = titleBarView;
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//            titleBarView.setPadding(0, DeviceUtil.dip2px(mContext, 20), 0, 0);
//        }
    }

    public void setTitleBarShdow(View mTitleBarShadow) {
        this.mTitleBarShadow = mTitleBarShadow;
    }

    protected View getmTitleBarShadow() {
        return mTitleBarShadow;
    }

    protected void hideTitleBarShdow() {
        if (null != mTitleBarShadow) {
            mTitleBarShadow.setVisibility(View.GONE);
        }
    }

    public TitleBarView getTitleBar() {
        return this.titleBarView;
    }

    /**
     * 返回键
     *
     * @return
     */
    public boolean onBackPressed() {
        return false;
    }

    protected void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != compositeSubscription && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }
}
