package com.wlb.agent.ui.order.helper;

import android.content.Context;

import com.wlb.agent.Constant;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.common.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 车险模块广播
 * 
 * @author zhangquan
 */
public class InsuranceReceiver {
	private Context ctx;
	private ArrayList<String> actionList = new ArrayList<>();

	public InsuranceReceiver(Context ctx) {
		this.ctx = ctx;
	}

	public InsuranceReceiver registClosePage() {
		actionList.add(Constant.IntentAction.ORDER_BUY_SUCCESS);
		actionList.add(Constant.IntentAction.ORDER_BUY_FAIL);

		EventBus.getDefault().register(this);
		return this;
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(PushEvent event) {
		String action = event.getAction();
		if (actionList.contains(action)) {
			((BaseActivity) (ctx)).close();
		}
	}

	public void unregist() {
		EventBus.getDefault().unregister(this);
	}
}
