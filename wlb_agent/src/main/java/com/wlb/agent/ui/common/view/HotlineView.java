package com.wlb.agent.ui.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wlb.agent.R;
import com.wlb.agent.util.PopupWindowUtil;

/**
 * Created by Berfy on 2017/7/25.
 * 客服电话公共View
 */
public class HotlineView extends LinearLayout {

    private Context mContext;
    private View mView;
    private PopupWindowUtil mPopupWindowUtil;

    public HotlineView(Context context) {
        super(context);
        init(context);
    }

    public HotlineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HotlineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mView = View.inflate(mContext, R.layout.view_hotline, null);
        addView(mView, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mView.findViewById(R.id.layout_hotline).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindowUtil.showHotLine();
            }
        });
    }
}
