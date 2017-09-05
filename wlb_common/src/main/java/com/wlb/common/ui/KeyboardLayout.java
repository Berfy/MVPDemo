package com.wlb.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.android.util.log.LogUtil;

/**
 * 监测键盘弹出与否的布局
 */
public class KeyboardLayout extends RelativeLayout {

    private final String TAG = "KeyboardLayout";
    public static final byte KEYBOARD_STATE_SHOW = -3;//弹出
    public static final byte KEYBOARD_STATE_HIDE = -2;//关闭
    public static final byte KEYBOARD_STATE_INIT = -1;//初始化,不用管

    private boolean mHasInit = false;
    private boolean mHasKeyboard = false;
    private int mHeight;

    private IOnKeyboardStateChangedListener onKeyboardStateChangedListener;

    public KeyboardLayout(Context context) {
        super(context);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnKeyboardStateChangedListener(
            IOnKeyboardStateChangedListener onKeyboardStateChangedListener) {
        this.onKeyboardStateChangedListener = onKeyboardStateChangedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        System.out.println("***b=" + b);
        if (!mHasInit) {
            mHasInit = true;
            mHeight = b;
            System.out.println("mHeight= " + b);
            if (onKeyboardStateChangedListener != null) {
                LogUtil.e(TAG,"init");
                onKeyboardStateChangedListener
                        .onKeyboardStateChanged(KEYBOARD_STATE_INIT);
            }
        } else {
            mHeight = mHeight < b ? b : mHeight;
        }

        if (mHasInit && mHeight > b) { // mHeight代表键盘的真实高度 ,b代表在窗口中的高度 mHeight>b
            // 说明键盘隐藏
            mHasKeyboard = true;
            if (onKeyboardStateChangedListener != null) {
                LogUtil.e(TAG,"show");
                onKeyboardStateChangedListener
                        .onKeyboardStateChanged(KEYBOARD_STATE_SHOW);
            }
        }
        if (mHasInit && mHasKeyboard && mHeight == b) { // mHeight = b 说明已经弹出
            mHasKeyboard = false;
            if (onKeyboardStateChangedListener != null) {
                LogUtil.e(TAG,"hide");
                onKeyboardStateChangedListener
                        .onKeyboardStateChanged(KEYBOARD_STATE_HIDE);
            }
        }
    }

    public int getKeyBoardHeight() {
        return mHeight;
    }

    public interface IOnKeyboardStateChangedListener {
        public void onKeyboardStateChanged(int state);
    }
}
