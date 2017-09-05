package common.widget.scaleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import common.widget.R;

/**
 * 点击可缩放的ImageView
 * 
 * @author zhangquan
 * 
 */
public class ScaleableImageView extends ImageView {
	private ScaleEffect scaleEffect;
	private static final float SCALE_RATIO = 0.8F;

	public ScaleableImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScaleableImageView(Context context) {
		this(context, null);
	}

	public ScaleableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		float scaleRatio = SCALE_RATIO;
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.scaleable);
		if (null != a) {
			if (a.hasValue(R.styleable.scaleable_scaleRatio)) {
				scaleRatio = a.getFloat(R.styleable.scaleable_scaleRatio,
						scaleRatio);
			}
			a.recycle();
		}
		init(scaleRatio);
	}

	private void init(float scaleRatio) {
		scaleEffect = new ScaleEffect();
		scaleEffect.setAnimateView(this);
		scaleEffect.setScaleRatio(scaleRatio);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(l);
		scaleEffect.setOnClickListener(l);
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return scaleEffect.onTouchEvent(event, ScaleableImageView.this);
			}
		});
	}

	/**
	 * 设置缩放比例
	 * 
	 * @param scaleRatio
	 */
	public void setScaleRatio(float scaleRatio) {
		scaleEffect.setScaleRatio(scaleRatio);
	}

	/**
	 * 获取缩放比例
	 * 
	 * @return
	 */
	public float getScaleRation() {
		return scaleEffect.getScaleRation();
	}
}
