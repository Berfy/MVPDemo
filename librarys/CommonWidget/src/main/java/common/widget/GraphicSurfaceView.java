package common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * SurfaceView容器
 */
@SuppressLint("NewApi")
public class GraphicSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private GLThread mThread;// 刷帧线程
	private Canvas mCanvas;// 画布
	private Object mLock = new Object();// 线程锁
	private boolean transluent;// 背景是否透明
	private final long SLEEP_TIME = 6;

	public GraphicSurfaceView(Context context) {
		super(context);
		initSurface();
	}

	public GraphicSurfaceView(Context context, boolean transluent) {
		super(context);
		this.transluent = transluent;
		initSurface();
	}

	public GraphicSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initSurface();
	}

	public GraphicSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSurface();
	}

	private void initSurface() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		mHolder = getHolder();
		// 背景透明
		if (transluent) {
			setZOrderOnTop(true);
			mHolder.setFormat(PixelFormat.TRANSLUCENT);
		}

		mHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (null != mThread)
			mThread.surfaceCreated();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (null != mThread)
			mThread.surfaceDestroyed();
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (null != mThread)
			mThread.onWindowFocusChanged(hasWindowFocus);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		onDestroy();
	}

	class GLThread extends Thread {
		private volatile boolean isPaused;// 暂停
		private volatile boolean isRunning;// 运行
		private volatile boolean hasFocus;// 焦点
		private Render render;// 渲染器
		private volatile boolean isDetached;

		public GLThread(Render render) {
			super();
			this.render = render;
		}

		@Override
		public synchronized void start() {
			isRunning = true;
			super.start();
		}

		@Override
		public void run() {
			while (isRunning) {
				if (isPaused || !hasFocus) {
					try {
						synchronized (mLock) {
							mLock.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					if (isRunning) {
						doDraw();
					}
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void onWindowFocusChanged(boolean hasFocus) {
			this.hasFocus = hasFocus;
			synchronized (mLock) {
				mLock.notifyAll();
			}
		}

		public void onPause() {
			isPaused = true;
		}

		public void onResume() {
			isPaused = false;
			synchronized (mLock) {
				mLock.notifyAll();
			}
		}

		public void onDestroy() {
			isDetached = true;
			isRunning = false;
			synchronized (mLock) {
				mLock.notifyAll();
			}
			removeRender();
		}

		public void removeRender() {
			render = null;
		}

		public void surfaceCreated() {
			hasFocus = true;
			isPaused = false;
			synchronized (mLock) {
				mLock.notifyAll();
			}
			if (null != render) {
				render.onSurfaceCreated(GraphicSurfaceView.this);
			}
		}

		public void surfaceDestroyed() {
			hasFocus = false;
			isPaused = true;
			synchronized (mLock) {
				mLock.notifyAll();
			}
		}

		private void doDraw() {
			try {
				mCanvas = mHolder.lockCanvas();
				if (null != mCanvas) {
					synchronized (mHolder) {
						if (isRunning && hasFocus && !isPaused && null != render) {
							// 背景透明
							if (transluent) {
								mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
							}
							render.onDrawFrame(mCanvas);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != mCanvas)
						mHolder.unlockCanvasAndPost(mCanvas);
				} catch (Exception e) {

				}
			}
		}
	}

	public void setRender(Render render) {
		checkRenderThreadState();
		mThread = new GLThread(render);
		mThread.start();
		synchronized (mLock) {
			mLock.notifyAll();
		}
	}

	public void removeRender() {
		if (null != mThread) {
			mThread.removeRender();
		}
	}

	private void checkRenderThreadState() {
		if (mThread != null) {
			throw new IllegalStateException("setRenderer has already been called for this instance.");
		}
	}

	public void onPause() {
		if (null != mThread)
			mThread.onPause();
	}

	public void onResume() {
		if (null != mThread)
			mThread.onResume();
	}

	public void onDestroy() {
		if (null != mThread && !mThread.isDetached)
			mThread.onDestroy();
	}

	public interface Render {

		void onSurfaceCreated(SurfaceView surfaceView);

		void onDrawFrame(Canvas canvas);
	}
}
