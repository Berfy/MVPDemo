package common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import com.android.util.log.LogUtil;

public class CommonWebView extends WebView {

    private final String TAG = "CommonWebView";
    private OnTouchScrollListener mOnTouchScrollListener;
    private ZoomButtonsController zoomController = null;

    public CommonWebView(Context context) {
        super(context);
        disableZoomController();
    }

    public CommonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        disableZoomController();
    }

    public CommonWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        disableZoomController();
    }

    /**
     * 使得控制按钮不可用
     */
    @SuppressLint("NewApi")
    private void disableZoomController() {
        // API version 大于11的时候，SDK提供了屏蔽缩放按钮的方法
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            this.getSettings().setBuiltInZoomControls(true);
            this.getSettings().setDisplayZoomControls(false);
        } else {
            // 如果是11- 的版本使用JAVA中的映射的办法
            // getControlls();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LogUtil.i(TAG, "WebView滑动位置  " + l + "," + t + "," + oldl + "," + oldt);
        if (null != mOnTouchScrollListener) {
            mOnTouchScrollListener.scroll(getScrollY());
        }
    }

    // private void getControlls() {
    // try {
    // Class webview = Class.forName("android.webkit.WebView");
    // Method method = webview.getMethod("getZoomButtonsController");
    // zoomController = (ZoomButtonsController) method.invoke(this, null);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    //
    // @Override
    // public boolean onTouchEvent(MotionEvent ev) {
    // super.onTouchEvent(ev);
    // if (zoomController != null) {
    // // 隐藏按钮
    // zoomController.setVisible(false);
    // }
    // return true;
    // }

    public void setOnTouchScrollListener(OnTouchScrollListener onTouchScrollListener) {
        mOnTouchScrollListener = onTouchScrollListener;
    }

    public interface OnTouchScrollListener {
        void scroll(float y);
    }

}
