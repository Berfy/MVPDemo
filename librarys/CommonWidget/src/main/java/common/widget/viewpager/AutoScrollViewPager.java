package common.widget.viewpager;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 一、功能
 * <ol>
 * <li>支持无限滚动，调用startAutoScroll开始滚动</li>
 * <li>支持无限左右滑动切换页面</li>
 * </ol>
 * 二、使用
 * 
 * <pre>
 * AutoScrollViewPager mViewPager = (AutoScrollViewPager) findViewById(R.id.viewPager);
 * ViewPagerAdapter adapter = new ViewPagerAdapter(context, dataList, R.layout.viewpager_item);
 * mViewPager.setAutoScrollAdapter(adapter);// 设置自动滚动adapter
 * mViewPager.setInterval(3 * 1000);// 设置滚动间隔时长
 * mViewPager.setScrollDurationFactor(4.0f);// 设置自动滚动的Scroller时长因子
 * mViewPager.startAutoScroll(4 * 1000);// 延迟4秒开始自动滚动
 * </pre>
 * 
 * 三、注意
 * <ol>
 * <li>为避免内存浪费，请在Activity或Fragment的onRause中暂停滚动，onResume中继续滚动</li>
 * </ol>
 * 
 * @author zhangquan
 * 
 */
public class AutoScrollViewPager extends common.widget.viewpager.ViewPager {
	public static final int DEFAULT_INTERVAL = 1500;
	private boolean started;
	private TimerHandler timerHandler;
	private long interval = DEFAULT_INTERVAL;
	private int startPage;
	private float touchX = 0f, downX = 0f;
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	public AutoScrollViewPager(Context context) {
		super(context);
		init();
	}

	public AutoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		timerHandler = new TimerHandler();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev);
		touchX = ev.getX();

		if ((action == MotionEvent.ACTION_DOWN)) {
			// 暂停滚动
			downX = touchX;
			pauseAutoScroll();
		} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			// 继续滚动
			resumeAutoScroll();
		}

		int currentItem = getCurrentItem();
		PagerAdapter adapter = getAdapter();
		int pageCount = adapter == null ? 0 : adapter.getCount();
		if ((currentItem == 0 && downX <= touchX) || (currentItem == pageCount - 1 && downX >= touchX)) {
			// 第一页往左滑动 或者最后一页往右滑动 则恢复到开始页
			if (pageCount > 1) {
				setCurrentItem(startPage, false);
			}
			getParent().requestDisallowInterceptTouchEvent(true);
			return super.dispatchTouchEvent(ev);
		}

		// -----------是否拦截手势
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
		}
		// getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 开始自动滚动
	 */
	public void startAutoScroll() {
		startAutoScroll(0);
	}

	/**
	 * 延迟delayTimeInMills秒后开始自动滚动
	 * 
	 * @param delayTime
	 */
	@SuppressWarnings("unchecked")
	public <E> void startAutoScroll(long delayTime) {
		PagerAdapter adapter = getAdapter();
		if (null != adapter && adapter.getCount() > 1 && getCurrentItem() == 0) {
			setAutoScrollStartPage((AutoScrollPagerAdapter<E>) adapter);
		}
		started = true;
		timerHandler.sendScrollMsg(delayTime);
	}

	/**
	 * 停止滚动
	 */
	public void stopAutoScroll() {
		started = false;
		timerHandler.pause();
	}

	/**
	 * 暂停滚动
	 */
	public void pauseAutoScroll() {
		if (isStarted()) {
			timerHandler.pause();
		}
		mScroller.resetDurationFactor();
	}

	/**
	 * 继续滚动
	 */
	public void resumeAutoScroll() {
		if (isStarted()) {
			timerHandler.resume();
		}
	}

	/**
	 * 是否已开始滚动
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * 两次滚动之间的时间间隔
	 * 
	 * @return
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * 设置两次滚动之间的时间间隔
	 * 
	 * @param interval
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * 设置自动滚动适配器
	 * 
	 * @param adapter
	 */
	public <E> void setAutoScrollAdapter(AutoScrollPagerAdapter<E> adapter) {
		setAdapter(adapter);
		setAutoScrollStartPage(adapter);
	}

	/**
	 * 设置开始滚动的页面
	 * 
	 * @param adapter
	 */
	private <E> void setAutoScrollStartPage(AutoScrollPagerAdapter<E> adapter) {
		// 从中间开始循环
		int count = adapter.getCount();
		if (count > 1 && getCurrentItem() == 0) {
			int dataSize = adapter.getList().size();
			startPage = (count / 2) - (count / 2 % dataSize);
			setCurrentItem(startPage, false);
		}
	}

	/**
	 * 刷新adapter
	 * 
	 * @param list
	 *            刷新数据
	 */
	@SuppressWarnings("unchecked")
	public <E> void refresh(List<E> list) {
		AutoScrollPagerAdapter<E> adapter = (AutoScrollPagerAdapter<E>) getAdapter();
		int oldSize = adapter.getList().size();
		adapter.refresh(list);
		if (oldSize == 0 && list.size() > 1) {
			setAutoScrollStartPage(adapter);
		}
	}

	/**
	 * 定时器
	 * 
	 * @author zhangquan
	 * 
	 */
	@SuppressLint("HandlerLeak")
	private class TimerHandler extends Handler {
		private final int msg_start = 1;
		private boolean paused;// 是否处于暂停

		public TimerHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case msg_start:
				if (paused) {
					return;
				}
				mScroller.setScrollDurationFactor(mScroller.getScrollDuraionFactor());
				int currentItem = getCurrentItem();
				currentItem += 1;
				int pageCount = getAdapter().getCount();
				// long delayTime = interval + mScroller.getDuration();
				long delayTime = interval + 500;
				if (currentItem < pageCount) {
					if (!paused) {
						setCurrentItem(currentItem, true);
						sendScrollMsg(delayTime);
					}
				} else {
					// 已到最后一页，则恢复到初始页
					if (!paused) {
						setCurrentItem(startPage, false);
						sendScrollMsg(delayTime);
					}
				}
				break;
			default:
				break;
			}
		}

		/**
		 * 继续
		 */
		public synchronized void resume() {
			paused = false;
			sendScrollMsg(interval);
		}

		/**
		 * 暂停
		 */
		public synchronized void pause() {
			paused = true;
			if (hasMessages(msg_start)) {
				removeMessages(msg_start);
			}
		}

		public void sendScrollMsg(long delayTime) {
			if (hasMessages(msg_start)) {
				removeMessages(msg_start);
			}
			sendMessageDelayed(obtainMessage(msg_start), delayTime);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		pauseAutoScroll();
	}
}
