package com.android.util.ext;

import android.widget.Toast;

import com.android.util.LContext;

public class ToastUtil {
	private static Toast mToast;

	public static void show(int resId) {
		show(LContext.getString(resId));
	}

	public static void show(CharSequence text) {
		// Toast.makeText(LContext.getContext(), text,
		// Toast.LENGTH_SHORT).show();

		if (mToast != null) {
			mToast.cancel();
			// mToast.setText(text);
			// mToast.setDuration(Toast.LENGTH_SHORT);
			mToast = null;
		}

		if (mToast == null) {
			mToast = Toast.makeText(LContext.getContext(), text,
					Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public static void showLong(int resId) {
		showLong(LContext.getString(resId));
	}

	public static void showLong(CharSequence text) {
		// Toast.makeText(LContext.getContext(), text,
		// Toast.LENGTH_LONG).show();

		if (mToast != null) {
			mToast.cancel();
			// mToast.setText(text);
			// mToast.setDuration(Toast.LENGTH_LONG);
			mToast = null;
		}

		if (mToast == null) {
			mToast = Toast.makeText(LContext.getContext(), text,
					Toast.LENGTH_LONG);
		}
		mToast.show();
	}
}
