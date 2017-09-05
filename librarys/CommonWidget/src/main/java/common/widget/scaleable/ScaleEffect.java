package common.widget.scaleable;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * 缩放效果
 * 
 * @author 张全
 */
public class ScaleEffect {
	private View view;
	private final int scaleDownTime = 100;// 按下动画时间
	private final int scaleUpTime = 150;// 手指抬起动画时间
	private float targetScaleRatio = 1.0f;// 目标缩放比例
	private OnClickListener listener;// 点击事件
	private ValueAnimator scaleDownAnimator, scaleUpAnimator;
	private boolean isScaleOut;// 放大
	private boolean isScaleIn;// 缩小
	private boolean isValidateTouch;// 点击是否有效

	public ScaleEffect() {
	}

	public ScaleEffect(View view) {
		this.view = view;
	}

	/**
	 * 设置动画的View
	 * 
	 * @param view
	 */
	public void setAnimateView(View view) {
		this.view = view;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param listener
	 */
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}

	/**
	 * 设置缩放比例
	 * 
	 * @param ratio
	 */
	public void setScaleRatio(float ratio) {
		targetScaleRatio = ratio;
	}

	public float getScaleRation() {
		return targetScaleRatio;
	}

	/**
	 * 手指抬起动画
	 */
	public void scaleUp() {
		clearAnim();

		float start = ViewHelper.getScaleX(view);
//		if (start == 1.0f) {
//			// 无需恢复状态
//			return;
//		}
		scaleUpAnimator = new ValueAnimator();
		scaleUpAnimator.setDuration(scaleUpTime);
		scaleUpAnimator.setInterpolator(new DecelerateInterpolator());
		scaleUpAnimator.setObjectValues(start, 1.0f);// 设置变化的值
		scaleUpAnimator.setEvaluator(new TypeEvaluator<Float>() {
			@Override
			public Float evaluate(float fraction, Float startValue,
					Float endValue) {
				return startValue + (endValue - startValue) * fraction;
			}

		});

		scaleUpAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animator) {
				isScaleOut = true;
			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				if (isValidateTouch && isScaleOut && null != listener) {
					listener.onClick(view);
				}
				isScaleOut = false;
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				isScaleOut = false;
			}
		});

		scaleUpAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if (isScaleOut) {
					float cVal = (Float) animation.getAnimatedValue();
					ViewHelper.setScaleX(view, cVal);
					ViewHelper.setScaleY(view, cVal);
				}
			}
		});

		scaleUpAnimator.start();
	}

	/**
	 * 手指按下动画
	 */
	public void scaleDown() {
		clearAnim();

		scaleDownAnimator = new ValueAnimator();
		scaleDownAnimator.setDuration(scaleDownTime);
		scaleDownAnimator.setObjectValues(1.0f, targetScaleRatio);// 设置变化的值
		scaleDownAnimator
				.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleDownAnimator.setEvaluator(new TypeEvaluator<Float>() {
			@Override
			public Float evaluate(float fraction, Float startValue,
					Float endValue) {
				return startValue + (endValue - startValue) * fraction;
			}

		});

		scaleDownAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animator) {
				isScaleIn = true;
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				isScaleIn = false;
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				isScaleIn = false;
			}
		});

		scaleDownAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if (isScaleIn) {
					float cVal = (Float) animation.getAnimatedValue();
					ViewHelper.setScaleX(view, cVal);
					ViewHelper.setScaleY(view, cVal);
				}
			}
		});

		scaleDownAnimator.start();
	}

	/**
	 * 恢复状态
	 */
	private void restore() {
		// view.clearAnimation();
		// isScaleIn = false;
		// isScaleOut = false;
		// ViewHelper.setScaleX(view, 1.0f);
		// ViewHelper.setScaleY(view, 1.0f);

		isValidateTouch = false;
		scaleUp();
	}

	/**
	 * 处理触摸事件
	 * 
	 * @param event
	 * @param touchView
	 * @return
	 */
	public boolean onTouchEvent(MotionEvent event, View touchView) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isValidateTouch = true;
			Log.e("faffffffffffffffff","按下");
			scaleDown();
			break;
		case MotionEvent.ACTION_MOVE:
			if (!((event.getX() <= touchView.getWidth() && event.getX() >= 0) && (event
					.getY() <= touchView.getHeight() && event.getY() >= 0))) {
				Log.e("faffffffffffffffff","无效");
				restore();
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.e("faffffffffffffffff","抬起");
			if ((event.getX() <= touchView.getWidth() && event.getX() >= 0)
					&& (event.getY() <= touchView.getHeight() && event.getY() >= 0)) {
				Log.e("faffffffffffffffff","有效");
				scaleUp();
			} else {
				restore();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.e("faffffffffffffffff","取消");
			restore();
			break;
		}
		return true;
	}

	/**
	 * 清除动画
	 */
	public void clearAnim() {
		isScaleIn = false;
		isScaleOut = false;
		if (null != scaleDownAnimator) {
			scaleDownAnimator.cancel();
		}
		if (null != scaleUpAnimator) {
			scaleUpAnimator.cancel();
		}
	}
}
