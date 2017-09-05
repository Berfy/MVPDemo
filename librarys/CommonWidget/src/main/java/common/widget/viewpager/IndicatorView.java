package common.widget.viewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;
import common.widget.R;

/**
 * 给ViewPager设置的Indicator
 * 
 * @author zhangquan
 * 
 */
public class IndicatorView extends FrameLayout {
	private LinearLayout mIndictorGroup;
	private ImageView mIndicatorView;
	private int mInterval;

	public IndicatorView(Context context) {
		this(context, null);
	}

	public IndicatorView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		View.inflate(getContext(), R.layout.common_indicator_layout, this);
		mIndictorGroup = (LinearLayout) findViewById(R.id.indictorGroup);
		mIndicatorView = (ImageView) findViewById(R.id.indicator_sel);
	}

	/**
	 * 设置实心圆indicator
	 * 
	 * @param pageCount
	 *            ViewPager页数
	 * @param mInterval
	 *            两个indicator之间的距离,单位：dip
	 * @param norIndicatorRes
	 *            正常显示的indicator图片资源id
	 * @param selIndicatorRes
	 *            移动的indicator图片资源id
	 */
	public void setIndictor(int pageCount, int intervalValue, int norIndicatorRes, int selIndicatorRes) {
		Drawable norDrawable = getResources().getDrawable(norIndicatorRes);
		Drawable selDrawable = getResources().getDrawable(selIndicatorRes);
		setIndictorDrawable(pageCount, intervalValue, norDrawable, selDrawable);
	}

	/**
	 * 设置indicator
	 * 
	 * @param pageCount
	 *            ViewPager页数
	 * @param intervalValue
	 *            两个indicator之间的距离,单位：dip
	 * @param norColor
	 *            正常显示的indicator颜色
	 * @param selColor
	 *            移动显示的indicator颜色
	 * @param indicatorSize
	 *            圆形indicator直径，单位 dp
	 */
	public void setIndictorColor(int pageCount, int intervalValue, int norColor, int selColor, int indicatorSize) {

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);

		indicatorSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorSize,
				getContext().getResources().getDisplayMetrics());

		// 正常图片
		paint.setColor(norColor);
		Bitmap norIndicator = Bitmap.createBitmap(indicatorSize, indicatorSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(norIndicator);
		canvas.drawCircle(indicatorSize / 2, indicatorSize / 2, indicatorSize / 2, paint);

		// 选中图片
		paint.setColor(selColor);
		Bitmap selIndicator = Bitmap.createBitmap(indicatorSize, indicatorSize, Config.ARGB_8888);
		canvas = new Canvas(selIndicator);
		canvas.drawCircle(indicatorSize / 2, indicatorSize / 2, indicatorSize / 2, paint);

		setIndictorBitmap(pageCount, intervalValue, norIndicator, selIndicator);
	}

	/**
	 * 设置Indicator
	 * 
	 * @param pageCount
	 *            ViewPager页数
	 * @param intervalValue
	 *            两个indicator之间的距离,单位：dip
	 * @param strokeColor
	 *            空心圆边框颜色
	 * @param solidColor
	 *            实心圆颜色
	 * @param indicatorSize
	 *            圆的直径 单位dip
	 */
	public void setIndictorStrokeColor(int pageCount, int intervalValue, int strokeColor, int solidColor,
			int indicatorSize) {

		indicatorSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorSize,
				getContext().getResources().getDisplayMetrics());

		// ----------空心圆
		GradientDrawable norDrawable = new GradientDrawable();
		norDrawable.setShape(GradientDrawable.OVAL);
		norDrawable.setUseLevel(false);
		norDrawable.setDither(true);
		norDrawable.setCornerRadius(indicatorSize / 2 + 0.5f);
		norDrawable.setSize(indicatorSize, indicatorSize);

		//
		norDrawable.setColor(Color.TRANSPARENT);
		norDrawable.setStroke(1, strokeColor);

		// ----------实心圆
		GradientDrawable selDrawable = new GradientDrawable();
		selDrawable.setShape(GradientDrawable.OVAL);
		selDrawable.setUseLevel(false);
		selDrawable.setDither(true);
		selDrawable.setCornerRadius(indicatorSize / 2 + 0.5f);
		selDrawable.setSize(indicatorSize, indicatorSize);
		//
		selDrawable.setColor(solidColor);

		setIndictorDrawable(pageCount, intervalValue, norDrawable, selDrawable);

	}

	/**
	 * 设置indicator
	 * 
	 * @param pageCount
	 * @param intervalValue
	 * @param norIndicator
	 * @param selIndicator
	 */
	public void setIndictorBitmap(int pageCount, int intervalValue, Bitmap norIndicator, Bitmap selIndicator) {
		BitmapDrawable norDrawable = new BitmapDrawable(getResources(), norIndicator);
		BitmapDrawable selDrawable = new BitmapDrawable(getResources(), selIndicator);
		setIndictorDrawable(pageCount, intervalValue, norDrawable, selDrawable);
	}

	/**
	 * 设置indicator
	 * 
	 * @param pageCount
	 *            ViewPager页数
	 * @param intervalValue
	 *            两个indicator之间的距离,单位：dip
	 * @param norIndicator
	 *            正常显示的indicator
	 * @param selIndicator
	 *            移动显示的indicator
	 */
	public void setIndictorDrawable(int pageCount, int intervalValue, Drawable norIndicator, Drawable selIndicator) {
		mIndictorGroup.removeAllViews();
		mInterval = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, intervalValue,
				getContext().getResources().getDisplayMetrics());
		LinearLayout.LayoutParams layoutParams = null;
		for (int i = 0; i < pageCount; i++) {
			layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ImageView imageView = new ImageView(getContext());
			imageView.setImageDrawable(norIndicator);
			if (i > 0) {
				layoutParams.leftMargin = mInterval;
			}
			mIndictorGroup.addView(imageView, layoutParams);
		}
		mIndicatorView.setImageDrawable(selIndicator);
	}

	/**
	 * 给ViewPager设置Indicator
	 * 
	 * @param viewPager
	 * @param pageChangeListener
	 */
	public void setViewPager(ViewPager viewPager, final OnPageChangeListener pageChangeListener) {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int page) {
				if (null != pageChangeListener) {
					pageChangeListener.onPageSelected(page);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (null != pageChangeListener) {
					pageChangeListener.onPageScrolled(arg0, arg1, arg2);
				}
				updateIndicator(arg0, arg1);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (null != pageChangeListener) {
					pageChangeListener.onPageScrollStateChanged(arg0);
				}
			}
		});
	}

	/**
	 * 移动Indicator
	 * 
	 * @param page
	 * @param per
	 */
	private void updateIndicator(int page, float per) {
		float x = (page + per) * (mInterval + mIndicatorView.getWidth());
		ViewHelper.setTranslationX(mIndicatorView, x);
	}
}
