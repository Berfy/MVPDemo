package common.widget.ratingbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import common.widget.R;

/**
 * 自定义RatingBar
 * <ol>
 * <li>支持设置星星个数(numStars)，星星大小(targetW、targetH)，星星之间的距离(starPadding)，自定义星星图片,
 * 以及stepSize
 * <li>xml中使用详例
 * 
 * <pre class="prettyprint">
 *    <common.widget.ratingbar.CommonRationBar
 *       xmlns:rb="http://schemas.android.com/apk/res-auto"
 *       android:layout_width="wrap_content"
 *       android:layout_height="wrap_content"
 *       rb:stepSize="0.5"
 *       rb:numStars="5"
 *       rb:rating="3.5"
 *       rb:targetW="25dp"
 *       rb:targetH="25dp"
 *       rb:starPadding="10dp"
 *       rb:normalStar="@drawable/details_stars_icon_gray"
 *       rb:selStar="@drawable/details_stars_icon"
 *       />
 * </pre>
 * 
 * </li>
 * </ol>
 * 
 * @author zhangquan
 * 
 */
public class CommonRationBar extends View {
	private Paint mPaint;
	private int starPadding;// 两颗星之间的距离
	private float rating; // 当前评分
	private int mNumStars = 5;// 总分
	private float stepSize = 0.5f;//
	private Bitmap normalBitmap, selBitmap;
	private boolean touchable;
	private RatingBarChangeListener l;
	private int mScaledTouchSlop;
	private RectF rectF = new RectF();

	public CommonRationBar(Context context) {
		this(context, null);
	}

	public CommonRationBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CommonRationBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.RationBar);
		if (null != a) {
			if (a.hasValue(R.styleable.RationBar_stepSize)) {
				float stepSize = a.getFloat(R.styleable.RationBar_stepSize,
						0.5f);
				setStepSize(stepSize);
			}
			if (a.hasValue(R.styleable.RationBar_numStars)) {
				int numStars = a.getInt(R.styleable.RationBar_numStars,
						5);
				setNumStars(numStars);
			}
			if (a.hasValue(R.styleable.RationBar_rating)) {
				float rating = a.getFloat(R.styleable.RationBar_rating,
						0f);
				setRating(rating);
			}
			if (a.hasValue(R.styleable.RationBar_starPadding)) {
				int starPadding = a.getDimensionPixelSize(R.styleable.RationBar_starPadding, 0);
				setStarPadding(starPadding);
			}

			int targetW = 0, targetH = 0;
			Bitmap normalBitmap = null, selBitmap = null;
			if (a.hasValue(R.styleable.RationBar_targetW)) {
				targetW = a.getDimensionPixelSize(R.styleable.RationBar_targetW, 0);
			}
			if (a.hasValue(R.styleable.RationBar_targetH)) {
				targetH = a.getDimensionPixelSize(R.styleable.RationBar_targetH, 0);
			}
			if (a.hasValue(R.styleable.RationBar_normalStar)) {
				Drawable normalDrawable = a.getDrawable(R.styleable.RationBar_normalStar);
				BitmapDrawable bd = (BitmapDrawable) normalDrawable;
				normalBitmap = bd.getBitmap();
			}
			if (a.hasValue(R.styleable.RationBar_selStar)) {
				Drawable selDrawable = a.getDrawable(R.styleable.RationBar_selStar);
				BitmapDrawable bd = (BitmapDrawable) selDrawable;
				selBitmap = bd.getBitmap();
			}
			setStarResource(normalBitmap, selBitmap, targetW, targetH);

			a.recycle();
		}
		init();
	}

	private void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setDither(true);
		mPaint.setFilterBitmap(true);
		mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public void setStarResource(int normal, int sel, int targetW, int targetH) {
		Bitmap normalBitmap = BitmapFactory.decodeResource(getResources(), normal);
		Bitmap selBitmap = BitmapFactory.decodeResource(getResources(), sel);
		setStarResource(normalBitmap, selBitmap, targetW, targetH);
	}

	public void setStarResource(Bitmap normalBitmap, Bitmap selBitmap, int targetW, int targetH) {
		if (null == normalBitmap || null == selBitmap) {
			return;
		}
		int bw = normalBitmap.getWidth();
		int bh = normalBitmap.getHeight();

		if (targetW == 0) {
			targetW = bw;
		}
		if (targetH == 0) {
			targetH = bh;
		}
		if (bw != targetW || bh != targetH) {
			normalBitmap = scaleBitmap(normalBitmap, targetW, targetH);
			selBitmap = scaleBitmap(selBitmap, targetW, targetH);
		}
		this.normalBitmap = normalBitmap;
		this.selBitmap = selBitmap;
	}

	public void setStepSize(float stepSize) {
		if (stepSize <= 0) {
			return;
		}
		this.stepSize = stepSize;
	}

	public void setStarPadding(int padding) {
		this.starPadding = padding;
	}

	public void setNumStars(int numStars) {
		if (numStars <= 0) {
			return;
		}
		this.mNumStars = numStars;
	}

	public int getNumStars() {
		return this.mNumStars;
	}

	public void setRating(float rating) {
		if (rating < 0) {
			rating = 0;
		} else if (rating > mNumStars) {
			rating = mNumStars;
		}
		this.rating = rating;
		invalidate();
	}

	public float getRating() {
		return this.rating;
	}

	public void setTouchable(boolean touchable) {
		this.touchable = touchable;
	}

	public void setOnRatingBarChangeListener(RatingBarChangeListener l) {
		this.l = l;
		setTouchable(true);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (null != normalBitmap && null != selBitmap) {
			int w = getPaddingLeft() + getPaddingRight() + mNumStars * normalBitmap.getWidth() + (mNumStars - 1)
					* starPadding;
			int h = normalBitmap.getHeight() + getPaddingTop() + getPaddingBottom();
			if (Build.VERSION.SDK_INT >= 11) {
				setMeasuredDimension(resolveSizeAndState(w, widthMeasureSpec, 0),
						resolveSizeAndState(h, heightMeasureSpec, 0));
			} else {
				setMeasuredDimension(w, h);
			}
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		int left = getPaddingLeft();
		int top = getPaddingTop();
		int bw = normalBitmap.getWidth();
		int bh = normalBitmap.getHeight();
		for (int i = 0; i < mNumStars; i++) {
			canvas.drawBitmap(normalBitmap, left, top, mPaint);
			if (rating == i + 0.5) {
				canvas.save();
				rectF.set(left, top, left + bw / 2.0f, top + bh);
				canvas.clipRect(rectF);
				canvas.drawBitmap(selBitmap, left, top, mPaint);
				canvas.restore();
			} else if (rating >= i + 1) {
				canvas.drawBitmap(selBitmap, left, top, mPaint);
			}
			left = left + starPadding + bw;
		}

	}

	private float mTouchDownX;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!touchable) {
			return super.onTouchEvent(event);
		}
		float x = event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchDownX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(x - mTouchDownX) > mScaledTouchSlop) {
				updateRating(event);
			}
			break;
		case MotionEvent.ACTION_UP:
			updateRating(event);
			break;
		}
		return true;
	}

	private int available = -1;
	private int mPaddingLeft, mPaddingRight;
	private float itemScale;
	private float lastRating;

	private void updateRating(MotionEvent event) {
		int width = getWidth();
		if (available == -1) {
			mPaddingLeft = getPaddingLeft();
			mPaddingRight = getPaddingRight();
			available = width - mPaddingLeft - mPaddingRight;
			itemScale = 1.0f / mNumStars;
		}

		int x = (int) event.getX();
		float rating = 0;
		if (x < mPaddingLeft) {
			rating = 0f;
		} else if (x > width - mPaddingRight) {
			rating = mNumStars;
		} else {
			float scale = (float) (x - mPaddingLeft) / (float) available;
			rating = scale / itemScale;
			if (rating < 0) {
				rating = 0;
			} else if (rating > mNumStars) {
				rating = mNumStars;
			}
			if (rating % 0.5f != 0) {
				float value = rating % 1.0f;
				if (value < 0.5f) {
					rating = Math.round(rating) + 0.5f;
				} else {
					rating = Math.round(rating);
				}
			}
		}
		// 刷新UI
		if (rating != lastRating) {
			setRating(rating);
			lastRating = rating;
			// 监听回调
			if (null != l) {
				l.onRatingChanged(this, rating);
			}
		}
	}

	public static interface RatingBarChangeListener {

		void onRatingChanged(CommonRationBar ratingBar, float rating);
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int dstW, int dstH) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = (float) dstW / w;
		float scaleHeight = (float) dstH / h;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}
}
