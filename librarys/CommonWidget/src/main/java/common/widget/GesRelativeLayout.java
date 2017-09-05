package common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 可拦截手势的RelativeLayout
 */
public class GesRelativeLayout extends RelativeLayout {
	private GestureDetector gestureDetector;

	public GesRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GesRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GesRelativeLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (gestureDetector == null) {
			return super.onInterceptTouchEvent(ev);
		}
		return gestureDetector.onTouchEvent(ev);
	}

	public void setSimpleOnGestureListener(GestureDetector.SimpleOnGestureListener simpleOnGestureListener) {
		this.gestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);
	}

}
