package com.wlb.agent.ui.user.helper;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.wlb.agent.ui.user.util.UserUtil;

public class GetCodeTimer extends Handler {
		private final int msg_what = 1;
		private boolean isStoped;
		private TextView btn_getCode;
		private String text;
		public GetCodeTimer(TextView btnView){
			this.btn_getCode=btnView;
			this.text = btnView.getText().toString();
		}

		@Override
		public void handleMessage(Message msg) {
			if (isStoped) {
				return;
			}
			switch (msg.what) {
			case msg_what:
				long dissTime = UserUtil.getNextTime()
						- System.currentTimeMillis();
				if (dissTime < 1000) {
					UserUtil.setNextTime(-1);
					if(null!=btn_getCode){
						btn_getCode.setEnabled(true);
						btn_getCode.setText(text);
					}
					removeMessages(msg_what);
				} else {
					setSecond(dissTime);
					sendMessageDelayed(obtainMessage(msg_what), 1 * 1000);
				}
				break;
			default:
				break;
			}
		}

		public void start() {
			long dissTime = UserUtil.getNextTime()
					- System.currentTimeMillis();
			setSecond(dissTime);
			btn_getCode.setEnabled(false);
			sendMessageDelayed(obtainMessage(msg_what), 1 * 1000);
		}

		public void stop() {
			isStoped = true;
			if (hasMessages(msg_what)) {
				removeMessages(msg_what);
			}
			btn_getCode=null;
		}

		private void setSecond(long dissTime) {
			dissTime /= 1000;// 秒
			int second = (int) (dissTime % 60);
			btn_getCode.setText(second + "秒");
		}
	}