package com.wlb.agent.ui.common.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.PopupWindow;

import com.android.util.log.LogUtil;

/**
 * 通用PopupWindow
 * <p>
 * </p>
 *
 * @author caozechen
 */
@SuppressLint("NewApi")
public class CommonPopupWindow {

    private Context mContext;
    private PopupWindow mPopupWindow;
    private final String TAG = "CommonPopupWindow";

    public CommonPopupWindow(View contentView, int w, int h) {
        this(contentView, w, h, -1);
    }

    @SuppressWarnings("deprecation")
    public CommonPopupWindow(View contentView, int w, int h, int animStyle) {
        mContext = contentView.getContext();
        mPopupWindow = new PopupWindow(contentView, w, h, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 这样设置才能点击屏幕外dismiss窗口
        if (animStyle != -1) {
            mPopupWindow.setAnimationStyle(animStyle);
        }
    }

    public CommonPopupWindow(Context ctx, int layoutId, int w, int h) {
        this(View.inflate(ctx, layoutId, null), w, h, -1);
    }

    public CommonPopupWindow(Context ctx, int layoutId, int w, int h, int animStyle) {
        this(View.inflate(ctx, layoutId, null), w, h, animStyle);
    }

    public void setBackgroundResource(int resId) {
        Context context = getContentView().getContext();
        mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(resId));
    }

    public void setBackgroundDrawable(Drawable background) {
        mPopupWindow.setBackgroundDrawable(background);
    }

    public void setContentView(View contentView) {
        mPopupWindow.setContentView(contentView);
    }

    public View getContentView() {
        return mPopupWindow.getContentView();
    }

    public View findViewById(int id) {
        return getContentView().findViewById(id);
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        try {
            if (isCanShow()) {
                LogUtil.e("显示", "===");
                mPopupWindow.showAtLocation(parent, gravity, x, y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAsDropDown(View anchor) {
        try {
            if (isCanShow()) {
                LogUtil.e("显示", "===");
                showAsDropDown(anchor, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        try {
            if (isCanShow()) {
                LogUtil.e("显示", "===");
                mPopupWindow.showAsDropDown(anchor, xoff, yoff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        try {
            if (isCanShow()) {
                LogUtil.e(TAG, "显示");
                mPopupWindow.showAsDropDown(anchor, xoff, yoff, gravity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isCanShow() {
        try {
            if (!((Activity) mContext).isFinishing() && null != mPopupWindow && !mPopupWindow.isShowing()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void dismiss() {
        try {
            if (!((Activity) mContext).isFinishing() && null != mPopupWindow && mPopupWindow.isShowing()) {
                LogUtil.e(TAG, "隐藏");
                mPopupWindow.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
