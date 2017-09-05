package common.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 文字实际高度的TextView,文字上下没有空隙
 */
public class ActualHeightTextView extends TextView {

	public ActualHeightTextView(Context context) {
		super(context);
	}

	public ActualHeightTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ActualHeightTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private int getAdjustHeight() {
		Paint paint = getPaint();
		int color = getTextColors().getDefaultColor();
		paint.setColor(color);

		Rect rect = new Rect();
		String text = getText().toString();
		paint.getTextBounds(text, 0, text.length(), rect);
		return rect.height();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getTextSize() != 0 && !TextUtils.isEmpty(getText().toString())) {
			setMeasuredDimension(getMeasuredWidth(), getAdjustHeight() + 4);
		} else
			setMeasuredDimension(getMeasuredWidth(), (int) getTextSize());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = getPaint();
		int color = getTextColors().getDefaultColor();
		paint.setColor(color);

		canvas.save();
		canvas.translate(0, 0);
		canvas.drawText(getText().toString(), 0, getAdjustHeight(), paint);
		canvas.restore();
	}

}
