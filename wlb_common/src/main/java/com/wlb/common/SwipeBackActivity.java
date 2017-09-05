package com.wlb.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import common.widget.SwipeBackLayout;

/**
 * 想要实现向右滑动删除Activity效果只需要继承SwipeBackActivity即可，如果当前页面含有ViewPager
 * 只需要调用SwipeBackLayout的setViewPager()方法即可
 *
 * @author xiaanming
 *
 */
public class SwipeBackActivity extends FragmentActivity {

	protected SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (CommonApp.swipeFinish) {
//			layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
//					R.layout.base, null);
//			layout.attachToActivity(this);
//		}

	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		if (CommonApp.swipeFinish) {
			overridePendingTransition(R.anim.base_slide_right_in,
					R.anim.base_slide_remain);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	@Override
	public void finish() {
		super.finish();
		if (CommonApp.swipeFinish) {
			overridePendingTransition(0, R.anim.base_slide_right_out);
		}
	}

}
