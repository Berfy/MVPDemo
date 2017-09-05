package com.wlb.agent.ui.common.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Button;

import com.android.util.log.LogUtil;

/**
 * Created by Berfy on 2017/7/20.
 * 倒计时按钮
 */

public class CountButton extends Button {

    private final String TAG = "CountButton";

    private int mTimes;
    private String mLeftText = "", mRightText = "s后重发";
    private Context mContext;

    public CountButton(Context context) {
        super(context);
        init(context);
    }

    public CountButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
    }

    /**
     * 设置倒计时左右的文字
     *
     * @param leftText  左侧文字
     * @param rightText 右侧文字
     */
    public void setText(String leftText, String rightText) {
        mLeftText = leftText;
        mRightText = rightText;
    }

    public void start(int count) {
        mTimes = count;
        setEnabled(false);
        mHandler.sendEmptyMessage(0);
    }

    public void stop() {
        mTimes = 0;
        setEnabled(true);
        mHandler.removeMessages(0);
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 0:
                    if (mTimes != 0) {
                        LogUtil.e(TAG, "倒计时" + mTimes + "");
                        setText(mLeftText + mTimes + mRightText);
                        --mTimes;
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    } else {
                        mTimes = 59;
                        setEnabled(true);
                        setText("获取验证码");
                    }
                    break;
            }

        }
    };
}
