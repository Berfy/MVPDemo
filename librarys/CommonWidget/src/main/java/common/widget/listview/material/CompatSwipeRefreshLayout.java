package common.widget.listview.material;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不拦截横滑事件,避免内嵌ViewPager横滑出现反弹现象
 * 
 * @author zhangquan
 * 
 */
public class CompatSwipeRefreshLayout extends SwipeRefreshLayout {
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	public CompatSwipeRefreshLayout(Context context) {
		this(context, null);
	}

	public CompatSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				// 如果是横滑，则不拦截手势
				return false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	public void setRingColor(int[] colors)
	{
		setColorSchemeResources(colors);
	}

	/**
	 * 显示进度条
	 */
	public void showProgressDrawable(){
		post(new Runnable() {
			@Override
			public void run() {
				setRefreshing(true);
			}
		});
	}

}