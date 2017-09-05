package common.widget.listview.material;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import common.widget.R;
import common.widget.listview.swipelist.SwipeMenuCreator;
import common.widget.listview.swipelist.SwipeMenuListView;

/**
 * 下拉刷新ListView
 *
 * @author zhangquan
 */
public class SwipeRefreshListView extends CompatSwipeRefreshLayout {
    private SwipeMenuListView mListView;
    private SwipeMenuListView.OnTouchScrollListener mOnTouchScrollListener;

    public SwipeRefreshListView(Context context) {
        super(context);
        init();
    }

    public SwipeRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mListView = (SwipeMenuListView) View.inflate(getContext(), R.layout.refresh_listview, null);
        addView(mListView);
        setSwipeEnabled(false);
    }

    public SwipeMenuListView getListView() {
        return mListView;
    }

    // --------------------对外封装-------
    public void setAdapter(BaseAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }

    public void setOnTouchScrollListener(SwipeMenuListView.OnTouchScrollListener onTouchScrollListener) {
        mListView.setOnTouchScrollListener(onTouchScrollListener);
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        mListView.setOnScrollListener(onScrollListener);
    }

    public void addFooterView(View footView) {
        mListView.addFooterView(footView);
    }

    public void addHeaderView(View headView) {
        mListView.addHeaderView(headView);
    }

    public void setDivider(Drawable divider) {
        mListView.setDivider(divider);
    }

    public void setDividerHeight(int height) {
        mListView.setDividerHeight(height);
    }

    public void setSelector(Drawable sel) {
        mListView.setSelector(sel);
    }

    public void stopRefresh() {
        setRefreshing(false);
    }
    public void setPullRefreshEnabled(boolean refreshEnabled){
        setEnabled(refreshEnabled);
    }


    public void setDividerWithHeight(int resId, int dividerH, boolean headDividerEnabled) {
        setDividerWithHeight(getResources().getDrawable(resId), dividerH, headDividerEnabled);
    }
    public void setDividerWithHeight(Drawable drawable, int dividerH, boolean headDividerEnabled) {
        mListView.setDivider(drawable);
        DisplayMetrics displayMetrics = getListView().getContext().getResources().getDisplayMetrics();
       int dividerHeight= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dividerH,displayMetrics);
        mListView.setDividerHeight(dividerHeight);
        if (headDividerEnabled) {
            setHeaderDividerEnabled();
        }
    }

    public void setHeaderDividerEnabled() {
        SwipeMenuListView implListView = getListView();
        if (implListView.getHeaderViewsCount() > 0) {
            implListView.setHeaderDividersEnabled(true);
        } else {
            View headView = new View(implListView.getContext());
            headView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 1));
            headView.setBackgroundColor(Color.TRANSPARENT);
            getListView().addHeaderView(headView);
            implListView.setHeaderDividersEnabled(true);
        }
    }
    //------------横滑----------------------
    public void setSwipeEnabled(boolean swipeEnabled){
        this.mListView.setSwipeEnabled(swipeEnabled);
    }
    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mListView.setMenuCreator(menuCreator);
    }
    public void setOnMenuItemClickListener(SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener){
       this.mListView.setOnMenuItemClickListener(onMenuItemClickListener);
    }
}