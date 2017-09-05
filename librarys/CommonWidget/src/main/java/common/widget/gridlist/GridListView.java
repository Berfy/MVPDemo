package common.widget.gridlist;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by JiaHongYa
 */
public class GridListView extends ScrollView {
    private GridListAdapter mAdapter;
    private MyDataSetObserver dataSetObserver = new MyDataSetObserver();
    private int numColumns = 1; //列数
    private int mHorizontalSpacing; //行间距
    private int mVerticalSpacing; //列间距
    private ItemClickListener mItemClickListener;
    private LinearLayout contentView;

    public GridListView(Context context) {
        super(context);
        init();
    }

    public GridListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            setOverScrollMode(OVER_SCROLL_NEVER);
        }
        setVerticalScrollBarEnabled(false);

        contentView = new LinearLayout(getContext());
        contentView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(contentView, layoutParams);
    }

    private void addViews() {
        final int count = mAdapter.getCount();
        if (count == 0) {
            return;
        }
        final int columns = getNumColumns();
        final int row = (count - 1) / columns + 1;
        for (int i = 0; i < row; i++) {
            LinearLayout itemGroup = new LinearLayout(getContext());
            itemGroup.setOrientation(LinearLayout.HORIZONTAL);
            itemGroup.setWeightSum(columns);//按列数平分

            for (int j = 0; j < columns; j++) {
                int pos = i * columns + j;
                LinearLayout.LayoutParams layoutParams = getItemLayoutParams(j);
                if (pos < count) {
                    View childView = mAdapter.getView(pos, itemGroup, this);
                    addChildViewClickListener(childView, pos);
                    itemGroup.addView(childView, layoutParams);
                } else {
                    View view = new View(getContext());//占位，让列平分
                    view.setVisibility(View.INVISIBLE);
                    itemGroup.addView(view, layoutParams);
                }
            }
            if (i > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = getVerticalSpacing();
                contentView.addView(itemGroup, layoutParams);
            } else {
                contentView.addView(itemGroup);
            }
        }
    }

    private LinearLayout.LayoutParams getItemLayoutParams(int column) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        if (column > 0) {
            layoutParams.leftMargin = getHorizontalSpacing();//
        }
        return layoutParams;
    }

    private void resetList() {
        contentView.removeAllViews();
    }

    private void addChildViewClickListener(View childView, final int pos) {
        childView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mItemClickListener)
                    mItemClickListener.onItemClick(GridListView.this, v, pos);
            }
        });
    }

    //----------------对外提供的方法 START----------------------
    public void setAdapter(GridListAdapter adapter) {
        if (null == adapter) throw new NullPointerException("adapter==null");
        if (null != mAdapter && null != dataSetObserver) {
            mAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        resetList();
        this.mAdapter = adapter;
        mAdapter.registerDataSetObserver(dataSetObserver);
        addViews();
    }

    public GridListAdapter getAdapter() {
        return mAdapter;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.mHorizontalSpacing = horizontalSpacing;
    }

    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.mVerticalSpacing = verticalSpacing;
    }

    public int getVerticalSpacing() {
        return mVerticalSpacing;
    }

    //------------------对外提供的方法 END----------------------

    private class MyDataSetObserver extends GridListDataSetObserver {
        @Override
        public void onChanged() {
            //全部需要重新绘制
            resetList();
            addViews();
        }

        @Override
        public void onItemChanged(int pos) {
            //获取指定pos的子View，并重新赋值只需要重新绘制指定pos的View
            final int columns = getNumColumns();
            final int row = pos / columns;
            final int column = pos - (row * 3);
            ViewGroup itemGroup = (ViewGroup) contentView.getChildAt(row);
            View childView = mAdapter.getView(pos, itemGroup, GridListView.this);
            addChildViewClickListener(childView, pos);
            LinearLayout.LayoutParams layoutParams = getItemLayoutParams(column);
            itemGroup.removeViewAt(column);//移除old childview
            itemGroup.addView(childView, column, layoutParams); //重新添加新的childview

        }
    }
}
