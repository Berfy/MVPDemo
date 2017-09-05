package common.widget.webview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.util.device.DeviceUtil;
import com.android.util.keyboard.SoftKeyboardUtil;
import com.android.util.log.LogUtil;

import common.widget.CommonWebView;
import common.widget.R;

/**
 * Created by Berfy on 2017/8/17.
 * 避免键盘遮挡自动计算输入框位置
 * Webview
 */
public class AutoWebView extends LinearLayout {

    private final String TAG = "AutoWebView";
    private Context mContext;
    private SoftKeyboardUtil mSoftKeyboardUtil;
    private LinearLayout mLlKeyboardPadding;
    private View mView;
    private CommonWebView mWebView;
    private int mClickY;//键盘高度  用户点击位置

    public AutoWebView(Context context) {
        super(context);
        init(context);
    }

    public AutoWebView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mView = View.inflate(context, R.layout.common_auto_webview, null);
        mLlKeyboardPadding = (LinearLayout) mView.findViewById(R.id.layout_keyboard_padding);
        mWebView = (CommonWebView) mView.findViewById(R.id.web_content);
        addView(mView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSoftKeyboardUtil = new SoftKeyboardUtil((Activity) mContext, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible) {
                if (visible) {
                    int height = DeviceUtil.getScreenHeightPx(mContext) - mClickY - softKeybardHeight;
                    LogUtil.e(TAG, "键盘弹起" + visible + "  键盘高度" + softKeybardHeight
                            + "  点击位置" + mClickY + "  移动位置" + height);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLlKeyboardPadding.getLayoutParams();
                    layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = softKeybardHeight - DeviceUtil.dip2px(mContext, 50);
                    mLlKeyboardPadding.setLayoutParams(layoutParams);
                    mLlKeyboardPadding.setVisibility(View.VISIBLE);
//                    mWebView.scrollBy(0, -height);
                } else {
                    LogUtil.e(TAG, "键盘落下" + visible + "  键盘高度" + softKeybardHeight + "  点击位置" + mClickY);
                    mLlKeyboardPadding.setVisibility(View.GONE);
                }
            }
        });
//        mWebView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                SoftInputUtil.hideSoftInput((Activity) mContext);
//                LogUtil.e(TAG, "点击位置" + motionEvent.getY());
//                mClickY = (int) motionEvent.getRawY();
//                return false;
//            }
//        });
    }

    public void setOnTouchScrollListener(CommonWebView.OnTouchScrollListener onTouchScrollListener) {
        mWebView.setOnTouchScrollListener(onTouchScrollListener);
    }

    public CommonWebView getWebView() {
        return mWebView;
    }
}
