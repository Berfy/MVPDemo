package com.wlb.common.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;

import com.android.util.log.LogUtil;
import com.android.util.scheduler.task.ScheduleTaskManager;

import java.util.Calendar;

/**
 * 后台服务
 * <P>
 * <ul>
 * <li>1、通常每个APP都会拥有一个CoreService，负责后台定时任务、锁屏服务、IM服务等</li>
 * <li>2、ProguardReceiver作为守护广播，通过监听一些系统广播动作来检测开启CoreService，也监听了断网重连动作
 * 当有需要断网重连业务时，可创建后台任务，具体请参考
 * </ul>
 * </P>
 * 
 * @author zhangquan
 */
public class CoreService extends Service {
	private String actionStr = getClass().getName();
	private PowerManager.WakeLock wl = null;
	private AlarmManager am;
	private PendingIntent intentSender;

	private static final String ACTION = "com.wlb.intent.action";
	/* Service被系统杀死时 发送广播 激活Service */
	public static final String PROGURAD = ACTION + ".proguard";
	/* 激活Service */
	public static final String ACTIVE = ACTION + ".active";
	/* 网络变化 */
	public static final String NETCHAGE = ACTION + ".netchage";

	public static void startCoreService(Context ctx, String action) {
		Intent intentService = new Intent(ctx, CoreService.class);
		intentService.setAction(action);
		ctx.startService(intentService);
	}
	@Override
	public void onCreate() {
		super.onCreate();

		// 开启所有注册过的定时任务
//		ScheduleTaskManager.getInstance().startAll();

		// 定时器
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		scheduleSelf();

		// 锁
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, actionStr);
		wl.setReferenceCounted(false);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d("CoreService................onStartCommand");
		getWackLock();
		ScheduleTaskManager.getInstance().startAll();
		dispatch(intent);
		releaseWackLock();
		return START_STICKY;
	}

	private void dispatch(Intent intent) {
		String action = null;
		if (null == intent) {
			action = ACTIVE;
		} else
			action = intent.getAction();

		if (TextUtils.isEmpty(action)) {
			action = ACTIVE;
		}

		LogUtil.d("intent=" + intent + ",action=" + action);

		if (action.equals(ACTIVE)) {// 激活

		} else if (action.equals(NETCHAGE)) {// 网络变化监听
			// 更新数据
			ScheduleTaskManager.getInstance().onNetChange();
		}
	}

	/**
	 * 定时开启自己
	 */
	public void scheduleSelf() {
		intentSender = PendingIntent.getService(this, 0, new Intent(this,
				CoreService.class), PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, intentSender);
	}

	/**
	 * 取消定时服务
	 */
	public void cancelTimerService() {
		if (null != am && null != intentSender)
			am.cancel(intentSender);
	}

	public void getWackLock() {
		wl.acquire();
	}

	public void releaseWackLock() {
		if (wl != null && wl.isHeld())
			wl.release();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			releaseWackLock();
			ScheduleTaskManager.getInstance().release();
			cancelTimerService();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sendBroadcast(new Intent(PROGURAD));
		}
	}
}
