package common.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 扩展的ViewPager
 * <ul>
 * <li>支持3.0以下的PageTransformer</li>
 * <li>支持是否滑动切换页面:setPagingEnabled()</li>
 * </ul>
 * 
 * @author zhangquan
 */
public class ViewPager extends android.support.v4.view.ViewPager {
	private Field mPageTransformerField;
	private Field mDrawingOrderField;
	private Method setChildrenDrawingOrderEnabledCompatMethod;
	private Method populateMethod;
	protected CustomDurationScroller mScroller = null;
	public static final float SWIPE_SCROLLER_FACTOR = 1.5f;
	public static final float MANUAL_SCROLLER_FACTOR = 3.0f;

	public ViewPager(Context context) {
		super(context);
		initCustomScroller();
	}

	public ViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCustomScroller();
	}

	@Override
	public void setPageTransformer(boolean reverseDrawingOrder,
			PageTransformer transformer) {
		try {
			Class<android.support.v4.view.ViewPager> cls = android.support.v4.view.ViewPager.class;
			if (mPageTransformerField == null) {
				mPageTransformerField = cls
						.getDeclaredField("mPageTransformer");
			}
			mPageTransformerField.setAccessible(true);
			Object lastPageTrans = mPageTransformerField.get(this);

			final boolean hasTransformer = transformer != null;
			final boolean needsPopulate = hasTransformer != (lastPageTrans != null);
			mPageTransformerField.set(this, transformer);
			mPageTransformerField.setAccessible(false);
			if (setChildrenDrawingOrderEnabledCompatMethod == null) {
				setChildrenDrawingOrderEnabledCompatMethod = cls.getMethod(
						"setChildrenDrawingOrderEnabledCompat", boolean.class);
			}
			setChildrenDrawingOrderEnabledCompatMethod.invoke(this, hasTransformer);
			if (mDrawingOrderField == null) {
				mDrawingOrderField = cls
						.getDeclaredField("mDrawingOrder");
			}
			mDrawingOrderField.setAccessible(true);
			if (hasTransformer) {
				mDrawingOrderField.set(this, reverseDrawingOrder ? 2 : 1);
			} else {
				mDrawingOrderField.set(this, 0);
			}
			mDrawingOrderField.setAccessible(false);
			if (needsPopulate) {
				if (populateMethod == null)
					populateMethod = cls.getMethod("populate");
				populateMethod.invoke(this);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 设置自定义的Scroller，用来控制滑动时长
	 */
	private void initCustomScroller() {
		try {
			Class<?> viewpager = android.support.v4.view.ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			Field interpolator = viewpager.getDeclaredField("sInterpolator");
			interpolator.setAccessible(true);

			mScroller = new CustomDurationScroller(getContext(),
					(Interpolator) interpolator.get(null));
			scroller.set(this, mScroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置滚动时长因子
	 * 
	 * @param scrollFactor
	 */
	public void setScrollDurationFactor(float scrollFactor) {
		if (null != mScroller) {
			mScroller.setScrollDurationFactor(scrollFactor);
		}
	}

	// ##################################################
	private boolean pagerEnabled = true;// 是否可以滑动翻页
	private boolean smoothScroll = true;

	/**
	 * 是否可以滑动翻页
	 * 
	 * @param pagerEnabled
	 */
	public void setPagingEnabled(boolean pagerEnabled)
	{
		this.pagerEnabled = pagerEnabled;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (pagerEnabled) {
			return super.onTouchEvent(event);
		} else {
			return false;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (pagerEnabled) {
			return super.onInterceptTouchEvent(event);
		} else {
			return false;
		}

	}

	@Override
	public void setCurrentItem(int paramInt)
	{
		if (this.smoothScroll)
		{
			super.setCurrentItem(paramInt);
			return;
		}
		super.setCurrentItem(paramInt, false);
	}

	public void setSmoothScroll(boolean smoothScroll)
	{
		this.smoothScroll = smoothScroll;
	}

}
