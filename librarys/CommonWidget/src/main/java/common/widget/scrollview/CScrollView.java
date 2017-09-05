package common.widget.scrollview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 能够兼容ViewPager的ScrollView
 * 
 * @Description: 解决了ViewPager在ScrollView中的滑动反弹问题
 */
public class CScrollView extends ScrollView {
	private int lastTop;
	private View contentView;
	private DetectScrollListener scrollListener;
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	public CScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
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

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(lastTop!=t){
			if(t==0){
				if (null != scrollListener) {
					scrollListener.scrollToTop();
				}
			}else {
				if (null == contentView) contentView = getChildAt(getChildCount() - 1);
				int bottom = contentView.getBottom();
				if (bottom <= (getHeight() + getScrollY())) {
					if (null != scrollListener) {
						scrollListener.scrollToBottom();
					}
				}
			}
		}
		lastTop=t;
	}

	public void setOnScrollListener(DetectScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	public interface DetectScrollListener {
		void scrollToBottom();
		void scrollToTop();
	}

	/**
	 * 滚动到底部
	 */
	public  void scrollToBottom() {
		Handler mHandler = new Handler();
		mHandler.post(new Runnable() {
			public void run() {
				View contentView = getChildAt(0);
				int offset = contentView.getMeasuredHeight() -getHeight();
				if (offset < 0) {
					offset = 0;
				}
				scrollTo(0, offset);
			}
		});
	}
}