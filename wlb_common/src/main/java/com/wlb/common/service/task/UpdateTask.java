package com.wlb.common.service.task;

import android.app.AlarmManager;

import com.android.util.LContext;
import com.android.util.os.NetworkUtil;
import com.android.util.scheduler.task.ScheduleTask;

import component.update.AppDownloadClient;
import component.update.AppVersion;
import component.update.VersionUpdateListener;

/**
 * 检测版本更新任务
 * 
 * @author 张全
 */
public class UpdateTask extends ScheduleTask {

	@Override
	public long getScheduleTime() {
		return AlarmManager.INTERVAL_HOUR;
	}

	@Override
	public void doTask() {
		checkUpdate();
	}

	private boolean isCheckUpdate;

	private void checkUpdate() {
		if (isCheckUpdate) {
			return;
		}
		if (NetworkUtil.isNetworkAvailable(LContext.getContext())) {
			isCheckUpdate = true;
			AppDownloadClient.doCheckVersion(new VersionUpdateListener() {

				@Override
				public void onNoVersionReturned() {
					isCheckUpdate = false;
				}

				@Override
				public void fail() {

				}

				@Override
				public void onNewVersionReturned(AppVersion appVersion) {
					isCheckUpdate = false;
				}
			});
		}
	}

}
