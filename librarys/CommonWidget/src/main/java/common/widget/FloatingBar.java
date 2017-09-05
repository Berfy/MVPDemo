package common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 显示浮层
 *
 * @author zhangquan
 */
public class FloatingBar extends RelativeLayout implements OnClickListener, AnimationListener {
	private long duration = 300;
	private boolean show;
	private View floatingBg, floatingBar;
	private LinearLayout floatingContent;
	private Animation bottomInAnim, bottomOutAnim;
	private Animation fadeOut, fadeIn;
	private boolean isAnim;
	private DissmissListener dissmissListener;

	public FloatingBar(Context context) {
		this(context, null);
	}

	public FloatingBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FloatingBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		Context context = getContext();
		View.inflate(context, R.layout.common_floatingbar, this);

		floatingBar = findViewById(R.id.floatingBar);
		floatingBg = findViewById(R.id.floatingBar_bg);
		floatingBg.setOnClickListener(this);
		floatingContent = (LinearLayout) findViewById(R.id.floatingBar_content);

		bottomInAnim = AnimationUtils.loadAnimation(context,
				R.anim.anim_bottom_in);
		bottomOutAnim = AnimationUtils.loadAnimation(context,
				R.anim.anim_bottom_out);
		fadeOut = AnimationUtils.loadAnimation(context,
				R.anim.anim_fade_out);
		fadeIn = AnimationUtils.loadAnimation(context,
				R.anim.anim_fade_in);

		bottomInAnim.setDuration(duration);
		bottomInAnim.setAnimationListener(this);

		bottomOutAnim.setDuration(duration);
		bottomOutAnim.setAnimationListener(this);

		fadeOut.setDuration(duration);
		fadeOut.setAnimationListener(this);

		fadeIn.setDuration(duration);
		fadeIn.setAnimationListener(this);
	}

	public void addContentView(View view) {
		floatingContent.addView(view);
	}

	public void addContentView(View view, LinearLayout.LayoutParams params) {
		floatingContent.addView(view, params);
	}

	public void addContentView(int contentResId) {
		View.inflate(getContext(), contentResId, floatingContent);
	}

	public boolean isShowing() {
		return floatingBar.getVisibility() == View.VISIBLE;
	}

	public void setOnDissmissListener(DissmissListener dissmissListener) {
		this.dissmissListener = dissmissListener;
	}

	public void show() {
		if (isAnim) {
			return;
		}
		floatingBar.setVisibility(View.VISIBLE);
		show = true;
		floatingBg.startAnimation(fadeOut);
		floatingContent.startAnimation(bottomInAnim);
	}

	public void hide() {
		if (isAnim) {
			return;
		}
		show = false;
		floatingBg.startAnimation(fadeIn);
		floatingContent.startAnimation(bottomOutAnim);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		isAnim = true;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		isAnim = false;
		if (show) {
			floatingBar.setVisibility(View.VISIBLE);
		} else {
			floatingBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.floatingBar_bg) {
			hide();
			if(null!=dissmissListener)dissmissListener.dismiss();
		}
	}

	/**
	 * 是否处理返回键
	 *
	 * @return
	 */
	public boolean handleBackPressed() {
		if (isShowing()) {
			hide();
			return true;
		}
		return false;
	}

	public interface DissmissListener {
		void dismiss();
	}

}
