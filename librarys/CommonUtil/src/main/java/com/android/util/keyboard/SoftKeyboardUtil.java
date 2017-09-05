package com.android.util.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.android.util.log.LogUtil;
import com.android.util.os.ViewUtils;

public class SoftKeyboardUtil {

    private Activity mActivity;
    private int mKeyboardHeight;

    public SoftKeyboardUtil(Activity activity, final OnSoftKeyboardChangeListener listener) {
        mActivity = activity;
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - displayHeight;
                mKeyboardHeight = keyboardHeight;
                if (previousKeyboardHeight != keyboardHeight) {
                    previousKeyboardHeight = keyboardHeight;
                    boolean hide = (double) displayHeight / height > 0.8;
                    listener.onSoftKeyBoardChange(keyboardHeight, !hide);
                }
            }
        });
    }

    public void autoScroll(Context context, ListView view, View clickView) {
        view.setSmoothScrollbarEnabled(true);
        int[] location = new int[2];
        clickView.getLocationOnScreen(location);
        LogUtil.e("计算键盘位置", location[1] + "    " + clickView.getHeight());
        location[1] = location[1] + clickView.getHeight();

        int topHeight = ViewUtils.getScreenHeight(context) - mKeyboardHeight - ViewUtils.dip2px(context, 40);
        //点击在键盘区域，列表向上滑动，点击在上方区域，向下滑动
        LogUtil.e("计算键盘位置", location[1] + "    " + topHeight);
        view.smoothScrollBy(location[1] - topHeight, 200);
    }

    public void autoScroll(Context context, ScrollView view, View clickView) {
        int[] location = new int[2];
        clickView.getLocationOnScreen(location);
        LogUtil.e("计算键盘位置", location[1] + "    " + clickView.getHeight());
        location[1] = location[1] + clickView.getHeight();

        int topHeight = ViewUtils.getScreenHeight(context) - mKeyboardHeight - ViewUtils.dip2px(context, 40);
        //点击在键盘区域，列表向上滑动，点击在上方区域，向下滑动
        LogUtil.e("计算键盘位置", location[1] + "    " + topHeight);
        view.smoothScrollBy(location[1] - topHeight, 200);
    }

    /**
     * 自动移动视图，避免被键盘挡住
     *
     * @param clickY 点击的位置Y坐标
     */
    public void autoScroll(Context context, WebView view, int clickY) {
        if (clickY > 0) {
            int topHeight = ViewUtils.getScreenHeight(context) - mKeyboardHeight - ViewUtils.dip2px(context, 80);
            //点击在键盘区域，列表向上滑动，点击在上方区域，向下滑动
            int moveY = clickY - topHeight;
            LogUtil.e("计算键盘移动距离  clickY = ", clickY + "   topHeight = " + topHeight + "   " + moveY);
            view.scrollBy(0, moveY);
        }
    }

    /**
     * 自动移动视图，避免被键盘挡住
     *
     * @param clickY 点击的位置Y坐标
     */
    public void autoScroll(Context context, ScrollView view, int clickY) {
        if (clickY > 0) {
            int topHeight = ViewUtils.getScreenHeight(context) - mKeyboardHeight - ViewUtils.dip2px(context, 80);
            //点击在键盘区域，列表向上滑动，点击在上方区域，向下滑动
            int moveY = clickY - topHeight;
            LogUtil.e("计算键盘移动距离  clickY = ", clickY + "   topHeight = " + topHeight + "   " + moveY);
            view.scrollBy(0, moveY);
        }
    }

    /**
     * 自动移动视图，避免被键盘挡住
     *
     * @param clickY 点击的位置Y坐标
     */
    public void autoScroll(Context context, LinearLayout view, int clickY) {
        if (clickY > 0) {
            int topHeight = ViewUtils.getScreenHeight(context) - mKeyboardHeight - ViewUtils.dip2px(context, 80);
            //点击在键盘区域，列表向上滑动，点击在上方区域，向下滑动
            int moveY = clickY - topHeight;
            LogUtil.e("计算键盘移动距离  clickY = ", clickY + "   topHeight = " + topHeight + "   " + moveY);
            view.scrollBy(0, moveY);
        }
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible);
    }
}  