package com.wlb.common.service.task;

/**
 *
 * 定位
 *
 * @author zhangquan
 */
//public class LocationScheduleTask extends ScheduleTask {
//	private final long TIME = AlarmManager.INTERVAL_HALF_HOUR;
//	private String KEY = LocationScheduleTask.class.getSimpleName();
//	private int errorConnect;// 失败重连次数
//	private boolean isUpdate;
//
//	@Override
//	public long getScheduleTime() {
//		return TIME;
//	}
//
//	@Override
//	public void onNetChange() {
//		doTask();
//	}
//
//	@Override
//	public void doTask() {
//		location();
//		// long duration = System.currentTimeMillis() - SPUtil.getLong(KEY, 0);
//		// if (duration > TIME) {
//		// location();
//		// }
//	}
//
//	private void location() {
//		if (isUpdate) {
//			return;
//		}
//		BDLocClient.getInstance(LContext.getContext()).requestLocationAsync(new LocationCallBack() {
//
//			@Override
//			public void success(LocationEntity location) {
//				isUpdate = false;
//				releaseWackLock();
//				errorConnect = 0;
//				SPUtil.setLong(KEY, System.currentTimeMillis());
//			}
//
//			@Override
//			public void start() {
//				getWackLock();
//				isUpdate = true;
//			}
//
//			@Override
//			public void fail() {
//				errorConnect++;
//				isUpdate = false;
//				releaseWackLock();
//				if (NetworkUtil.isNetworkAvailable(LContext.getContext())) {
//					if (errorConnect < 2) {
//						location();
//					} else {
//						errorConnect = 0;
//					}
//				} else {
//					errorConnect = 0;
//				}
//			}
//
//		});
//	}
//}
