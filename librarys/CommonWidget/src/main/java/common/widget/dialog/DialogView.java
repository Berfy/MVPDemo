package common.widget.dialog;

import android.content.Context;
import android.view.View;

/**
 * @author 张全
 */
public abstract class DialogView {
	protected Context ctx;
	protected View contentView;
	protected EffectDialogBuilder dialogBuilder;

	public DialogView(Context ctx) {
		this.ctx = ctx;

	}

	public Context getContext() {
		return this.ctx;
	}

	public void setEffecctDialogBuilder(EffectDialogBuilder dialogBuilder) {
		this.dialogBuilder = dialogBuilder;
	}

	public EffectDialogBuilder getBuilder() {
		return this.dialogBuilder;
	}

	public void dismiss() {
		getBuilder().dismiss();
	}

	public View inflateView(int layoutId) {
		return View.inflate(ctx, layoutId, null);
	}

	protected View getContentView() {
		contentView = inflateView(getLayoutId());
		initView(contentView);
		return contentView;
	}

	protected View findViewById(int id) {
		return contentView.findViewById(id);
	}

	protected abstract void initView(View view);

	protected abstract int getLayoutId();
}
