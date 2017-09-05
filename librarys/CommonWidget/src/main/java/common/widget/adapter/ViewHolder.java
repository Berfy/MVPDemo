package common.widget.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter通用ViewHolder
 * 
 * @author 张全
 */
public class ViewHolder {
	private int mPos;
	private View mConvertView;
	private SparseArray<View> mViewMap;

	public ViewHolder(Context ctx, int position, View convertView) {
		this.mPos = position;
		this.mConvertView = convertView;
		mViewMap = new SparseArray<View>();
		convertView.setTag(this);
	}

	public static ViewHolder getView(Context ctx, int position,
			View convertView, int layoutId) {
		if (convertView == null) {
			return new ViewHolder(ctx, position, View.inflate(ctx, layoutId, null));
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPos = position;
			return holder;
		}
	}

	public View getConvertView() {
		return mConvertView;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int resId) {
		View view = mViewMap.get(resId);
		if (null == view) {
			view = mConvertView.findViewById(resId);
			mViewMap.put(resId, view);
		}
		return (T) view;
	}

	public int getPos() {
		return mPos;
	}

	// ------------------------------------------------------------------
	// ###############View############
	public ViewHolder setTag(int resId, Object tag) {
		View view = getView(resId);
		view.setTag(tag);
		return this;
	}

	public ViewHolder setSelected(int resId, boolean selected) {
		View view = getView(resId);
		view.setSelected(selected);
		return this;
	}

	public ViewHolder setEnabled(int resId, boolean enabled) {
		View view = getView(resId);
		view.setEnabled(enabled);
		return this;
	}

	public ViewHolder setBackgroundResource(int resId, int imgId) {
		View view = getView(resId);
		view.setBackgroundResource(imgId);
		return this;
	}

	public ViewHolder setBackgroundColor(int resId, int color) {
		View view = getView(resId);
		view.setBackgroundColor(color);
		return this;
	}

	@SuppressWarnings("deprecation")
	public ViewHolder setBackgroundDrawable(int resId, Drawable background) {
		View view = getView(resId);
		view.setBackgroundDrawable(background);
		return this;
	}

	public ViewHolder setVisibility(int resId, int visibility) {
		View view = getView(resId);
		view.setVisibility(visibility);
		return this;
	}

	// ###############TextView############
	public ViewHolder setText(int resId, int textId) {
		TextView textView = (TextView) getView(resId);
		textView.setText(textId);
		return this;
	}

	public ViewHolder setText(int resId, CharSequence text) {
		TextView textView = (TextView) getView(resId);
		textView.setText(text);
		return this;
	}

	public ViewHolder setTextColor(int resId, int color) {
		TextView textView = (TextView) getView(resId);
		textView.setTextColor(color);
		return this;
	}

	// ###############ImageView############
	public ViewHolder setImageResource(int resId, int imgId) {
		ImageView imageView = (ImageView) getView(resId);
		imageView.setImageResource(imgId);
		return this;
	}

	public ViewHolder setImageBitmap(int resId, Bitmap bitmap) {
		ImageView imageView = (ImageView) getView(resId);
		imageView.setImageBitmap(bitmap);
		return this;
	}

	public ViewHolder setImageDrawable(int resId, Drawable drawable) {
		ImageView imageView = (ImageView) getView(resId);
		imageView.setImageDrawable(drawable);
		return this;
	}
}
