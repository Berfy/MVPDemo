package common.widget.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 在ScrollView中使用GridView
 * 
 * @note 虽然不影响GridView滑动，但是ListView的缓存机制会失效，即所有item都会绘制
 */
public class GridViewForScrollView extends GridView {

	public GridViewForScrollView(Context context) {
		super(context);
	}

	public GridViewForScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewForScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
