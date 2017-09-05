package common.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import common.widget.R;

public class CustomDialog extends Dialog implements DialogInterface {

	private LinearLayout mContentView;// 内容布局
	private RelativeLayout mRootView;// 根布局

	private boolean isCancelable = true;

	public CustomDialog(Context context) {
		super(context);
		init(context);

	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

	}

	private void init(Context context) {

		View mDialogView = View.inflate(context, R.layout.common_dialog, null);

		mRootView = (RelativeLayout) mDialogView.findViewById(R.id.main);
		mContentView = (LinearLayout) mDialogView.findViewById(R.id.container);
		setContentView(mDialogView);

		// 根布局
		mRootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isCancelable) dismiss();
			}
		});
	}

	public void setCustomView(View view) {
		if (mContentView.getChildCount() > 0) {
			mContentView.removeAllViews();
		}
		mContentView.addView(view);
	}

	public View getRootView() {
		return mRootView;
	}

	public View getContentView() {
		return mContentView;
	}

	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		super.setCanceledOnTouchOutside(cancel);
		this.isCancelable = cancel;
	}

	@Override
	public void setCancelable(boolean flag) {
		super.setCancelable(flag);
		this.isCancelable = flag;
	}
}
