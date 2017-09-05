package common.widget.dialog.loading;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import common.widget.R;
import common.widget.dialog.DialogView;

/**
 * @author 张全
 */
public class LoadingLoadingView extends DialogView {

	private ImageView mLoadingView;
//	private TextView mLoadingTxt;
	private String loadingTxt;

	public LoadingLoadingView(Context ctx) {
		super(ctx);
	}

	public LoadingLoadingView(Context ctx, String loadingTxt) {
		super(ctx);
		this.loadingTxt = loadingTxt;
	}

	@Override
	protected void initView(View view) {
//		mLoadingView = (ImageView) findViewById(R.id.loadingbar);
//		mLoadingTxt = (TextView) findViewById(R.id.loadingbar_txt);
//		if (!TextUtils.isEmpty(loadingTxt)) {
//			mLoadingTxt.setText(loadingTxt);
//		}
//		RotateAnimation anim = new RotateAnimation(0, 360,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		anim.setInterpolator(new LinearInterpolator());
//		anim.setDuration(1000);
//		anim.setRepeatCount(Animation.INFINITE);
//		anim.setRepeatMode(Animation.RESTART);
//		mLoadingView.startAnimation(anim);
	}

//	public void setText(String text) {
//		mLoadingTxt.setText(text);
//	}

	@Override
	protected int getLayoutId() {
		return R.layout.dialog_loading;
	}

}
