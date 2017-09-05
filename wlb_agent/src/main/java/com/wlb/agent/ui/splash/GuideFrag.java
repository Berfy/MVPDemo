package com.wlb.agent.ui.splash;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.util.LContext;
import com.android.util.ext.SPUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.main.TabAct;
import com.wlb.common.SimpleFrag;

import java.util.ArrayList;
import java.util.List;

import common.widget.viewpager.IndicatorView;

/**
 * 引导页
 * 
 * @author caozechen
 * 
 */
public class GuideFrag extends SimpleFrag {

	private ViewPager viewPager;
	private int curPage;
	private boolean interruptable;

	// 引导图片
	private static final int[] pics = { R.drawable.guide_first, R.drawable.guide_second,
			R.drawable.guide_third };

	/**
	 * 是否需要显示引导页
	 * 
	 * @return
	 */
	public static boolean hasShowGuide() {
		return SPUtil.getBoolean(LContext.versionName, false);
	}

	/**
	 * 保存引导状态
	 */
	public static void saveShowGuide() {
		SPUtil.setBoolean(LContext.versionName, true);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.guide_frag;
	}

	@Override
	protected void init(Bundle arg0) {
		List<View> views = new ArrayList<View>();
		final int pageCount = pics.length;
		for (int i = 0; i < pageCount; i++) {
			View view = View.inflate(mContext, R.layout.guide_item, null);
			views.add(view);
			final View imgContainer = view.findViewById(R.id.img_container);
			final ImageView imageView = (ImageView) view.findViewById(R.id.item_img);
			imageView.setImageResource(pics[i]);
			if (i == pageCount - 1) {
				View btnView = view.findViewById(R.id.item_btn);
				btnView.setVisibility(View.VISIBLE);
				btnView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startTabAct();
					}
				});
			}
			imgContainer.post(new Runnable() {

				@Override
				public void run() {

					int vh = imgContainer.getMeasuredHeight();
					int bw = imageView.getDrawable().getIntrinsicWidth();
					int bh = imageView.getDrawable().getIntrinsicHeight();
					float vw = bw * vh * 1.0f / bh;
					int viewW = Math.round(vw);
					imageView.getLayoutParams().width = viewW;

				}
			});
		}
		viewPager = findViewById(R.id.viewPager);
		viewPager.setOffscreenPageLimit(pageCount);
		ViewPageAdapter viewPagerAdapter = new ViewPageAdapter(views);
		viewPager.setAdapter(viewPagerAdapter);
		OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int page) {
				curPage = page;
			}

			@Override
			public void onPageScrolled(int page, float per, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int status) {
				if (curPage == pageCount - 1) {
					if (status == 0) {
						if (interruptable) {
							startTabAct();
						}
						interruptable = true;
					}
				} else {
					interruptable = false;
				}
			}
		};

		IndicatorView indicatorView = findViewById(R.id.indicator);
		indicatorView.setIndictorStrokeColor(pageCount, 8, getColor(R.color.c_EE2112), getColor(R.color.c_EE2112), 8);
		indicatorView.setViewPager(viewPager, onPageChangeListener);
	}

	private void startTabAct() {
		saveShowGuide();
		TabAct.start(mContext);
		finish();
	}

	private class ViewPageAdapter extends PagerAdapter {
		private List<View> views;

		public ViewPageAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = views.get(position);
			container.addView(view);
			return view;

		}
	}
}
