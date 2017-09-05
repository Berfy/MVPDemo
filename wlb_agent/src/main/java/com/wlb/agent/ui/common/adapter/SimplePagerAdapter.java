package com.wlb.agent.ui.common.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import java.util.List;

public class SimplePagerAdapter extends PagerAdapter {

	private List<View> mViews;

	public SimplePagerAdapter() {
	}

	public void setData(List<View> views) {
		mViews = views;
	}

	@Override
	public int getCount() {
		return null == mViews ? 0 : mViews.size();
	}

	@Override
	public View instantiateItem(ViewGroup container, final int position) {
		container.addView(mViews.get(position), LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		return mViews.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}