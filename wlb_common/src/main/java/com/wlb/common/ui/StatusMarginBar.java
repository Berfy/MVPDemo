package com.wlb.common.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.android.util.LContext;
import com.android.util.device.DeviceUtil;
import com.wlb.common.R;

/**
 * Created by Berfy on 2017/8/15.
 * 状态栏间隔布局
 */

public class StatusMarginBar extends LinearLayout {

    public StatusMarginBar(Context context) {
        super(context);
    }

    public StatusMarginBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusMarginBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        init();
    }

    private void init() {
        try {
            if (LContext.isTranslucent) {
                setBackgroundResource(R.color.transparent_black);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    layoutParams.height = DeviceUtil.getStatusBarHeight(getContext());
                    setLayoutParams(layoutParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
