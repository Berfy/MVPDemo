package common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 虚线View eg: <com.android.view.DashLine
 * xmlns:dl="http://schemas.android.com/apk/res-auto"
 * android:layout_width="match_parent" android:layout_height="match_parent"
 * dl:strokeColor="@color/red" dl:strokeWidth="@dimen/txtSize" />
 * 
 * @note paint必须设置为Stroke style
 */
public final class DashLine extends View {

	private Paint mPaint;
	private Path mPath;
	private int mViewWidth;
	private int mStrokeWidth;
	private int mStrokeColor;
	private final int defWidth = 0;
	private final int defColor = Color.GRAY;

	public DashLine(Context context) {
		super(context);
		init();
	}

	public DashLine(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DashLine(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DashLine);
		mStrokeWidth = a.getDimensionPixelSize(R.styleable.DashLine_strokeWidth2, defWidth);
		mStrokeColor = a.getColor(R.styleable.DashLine_strokeColor2, defColor);
		a.recycle();
		init();
	}

	private void init() {

		mPaint = new Paint();
		mPaint.setColor(mStrokeColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(mStrokeWidth);

		mPath = new Path();

		// float数组,必须是偶数长度,且>=2,指定了多少长度的实线之后再画多少长度的空白
		float[] intervals = new float[] { 5.0f, 5.0f };

		DashPathEffect dashPathEffect = new DashPathEffect(intervals, 0);

		mPaint.setPathEffect(dashPathEffect);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(mPath, mPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
		mPath.reset();
		mPath.moveTo(0, 0);
		mPath.lineTo(mViewWidth, 0);
		// this.setMeasuredDimension(mViewWidth, mViewHeight);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
