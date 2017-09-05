package com.android.util.os;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.util.log.LogUtil;

/**
 * 一些测量view的方法
 *
 * @author Administrator
 */
public class ViewUtils {

    private static final String TAG = "ViewUtils";

    public static int getTitleBarHeight(Activity activity) {
        // TODO Auto-generated method stub
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  //屏幕宽
        int height = dm.heightPixels;  //屏幕高
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;  //状态栏高
        int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight; //标题栏高
        return statusBarHeight;
    }

    /**
     * 获取屏幕的宽高
     *
     * @param activity
     * @return
     */
    public static int[] getScreenWH(Activity activity) {
        int[] wh = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        wh[0] = dm.widthPixels;
        wh[1] = dm.heightPixels;
        return wh;
    }

    /**
     * 获取屏幕的宽高
     *
     * @param view
     * @return
     */
    public static int[] getViewWH(View view) {
        int[] wh = new int[2];
        view.getLocationInWindow(wh);
        return wh;
    }

    /**
     * 设置View的高度
     *
     * @param height
     * @param v
     */
    public static void setViewHeight(int height, View v) {
        LayoutParams lp = v.getLayoutParams();
        lp.height = height;
        v.setLayoutParams(lp);
    }

    /**
     * 设置View的宽度
     *
     * @param width
     * @param v
     */
    public static void setViewWidth(int width, View v) {
        LayoutParams lp = v.getLayoutParams();
        lp.width = width;
        v.setLayoutParams(lp);
    }

    /**
     * 测量view的高度
     *
     * @param view
     * @return
     */
    public static int measureHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    /**
     * 测量view的宽度
     *
     * @param view
     * @return
     */
    public static int measureWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }

    /**
     * get Screen Density
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static float getScreenScaledDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * get Display
     *
     * @param context
     * @return
     */
    private static Display getDisplay(Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display;
    }

    /**
     * get Screen Width
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        if (null == context) {
            return 0;
        }
        int width = getDisplay(context).getWidth();
        LogUtil.e(TAG, "屏幕宽度" + width);
        return width;
    }

    /**
     * get Screen Heght
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context) {
        if (null == context) {
            return 0;
        }
        int height = getDisplay(context).getHeight();
        LogUtil.e(TAG, "屏幕高度" + height);
        return height;
    }

    /**
     * get Screen Density
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return dm.densityDpi;
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * (getScreenDensity(context) / 160f) + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) ((pxValue * 160) / getScreenDensity(context));
    }

    public static float sp2px(Context context, float sp) {
        return sp * getScreenScaledDensity(context);
    }

    public static float px2sp(Context context, float px) {
        return px / getScreenScaledDensity(context) + 0.5f;
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v
                    .getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    //设置全屏
    public static void fillScree(Activity activity) {
        // 无title
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static ViewGroup findRootView(View view) {
        do {
            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    return (ViewGroup) view;
                }
            }
            if (null != view) {
                ViewParent viewParent = view.getParent();
                view = viewParent instanceof View ? (View) viewParent : null;
            }
        } while (null != view);
        LogUtil.e("未发现Framelayout", "aaa");
        return null;
    }
}
