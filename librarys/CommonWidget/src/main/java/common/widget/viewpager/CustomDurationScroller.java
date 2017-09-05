package common.widget.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class CustomDurationScroller extends Scroller {

	private float mScrollFactor = 1.0f;
	private float mFactor = mScrollFactor;

	public CustomDurationScroller(Context context) {
		super(context);
	}

	public CustomDurationScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	@SuppressLint("NewApi")
	public CustomDurationScroller(Context context, Interpolator interpolator, boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	/**
	 * Set the factor by which the duration will change
	 */
	public void setScrollDurationFactor(float scrollFactor) {
		mFactor = mScrollFactor = scrollFactor;
	}

	public void resetDurationFactor() {
		mFactor = 1.0f;
	}

	public float getScrollDuraionFactor() {
		return mScrollFactor;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, (int) (duration * mFactor));
	}

}
