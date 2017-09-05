package com.wlb.agent.ui.common.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.util.device.DeviceUtil;
import com.android.util.log.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wlb.agent.R;
import com.wlb.common.imgloader.ImageLoaderImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 广告
 *
 * @author heiyue heiyue623@126.com
 * @ClassName: BannerView
 * @Description:
 * @date 2014-10-27 下午1:07:31
 */
public class BannerView extends RelativeLayout {

    private Context mContext;
    private View mView;
    private float mRawX, mRawY;
    private long mRawTime;

    /**
     * 广告图的自动滚动时间
     */
    public static final int AUTO_SCROLL_TIME = 1000 * 3;
    private ViewPager mViewPager;
    private LinearLayout mIndicatorGroup;
    private SamplePagerAdapter mAdapter;
    private Handler mHandler;

    public BannerView(Context context) {
        super(context);
        initViews(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public BannerView(Context context, AttributeSet attrs, int in) {
        super(context, attrs, in);
        initViews(context);
    }

    private void initViews(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.view_include_bannerview, this);
        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        mIndicatorGroup = (LinearLayout) mView.findViewById(R.id.bannerImgIndictor);
        mAdapter = new SamplePagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int size  = mAdapter.getData().size();
                if (size == 0) {
                    return;
                }
                setSelectedIndictor(position % size);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    LogUtil.e("抬起", event.getX() + "," + event.getY());
                    if (Math.abs(event.getY() - mRawY) < 100) {// 计算左划右划手势
                        if (event.getX() - mRawX < -50
                                && mViewPager.getCurrentItem() == mAdapter
                                .getCount() - 1) {// 左划
                            mHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    mViewPager.setCurrentItem(0, true);
                                }
                            }, 500);
                        } else if (event.getX() - mRawX > 50
                                && mViewPager.getCurrentItem() == 0) {// 右划
                            PagerAdapter adapter = mViewPager.getAdapter();
                            if (adapter != null) {
                                final int childCount = adapter.getCount();
                                mHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        mViewPager.setCurrentItem(
                                                childCount - 1, true);
                                    }
                                }, 500);
                            }
                        }
                    }
                    isScroll = true;
                    setUpAutoScroll();
                }
                return false;
            }
        });
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, AUTO_SCROLL_TIME);
    }

    /**
     * 设置页面指示器的位置
     */
    public void setPageIndicatorGravity(int gravity) {
        ((LinearLayout) mView.findViewById(R.id.layout)).setGravity(gravity);
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacks(mRunnable);
        LogUtil.e("页面销毁", "fff");
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        if (isAutoScroll) {
            LogUtil.e("自动播放", "轮播");
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, AUTO_SCROLL_TIME);
        }
        super.onAttachedToWindow();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isScroll = false;
                mHandler.removeCallbacks(mRunnable);
                mRawTime = System.currentTimeMillis();
                mRawX = event.getX();
                mRawY = event.getY();
                LogUtil.e("按下", event.getX() + "," + event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.e("移动", event.getX() + "," + event.getY());
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private boolean isScroll = true;
    private boolean isAutoScroll = true;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.e("自动播放1", "轮播");
            if (mViewPager != null && isScroll) {
                PagerAdapter adapter = mViewPager.getAdapter();
                if (adapter != null) {
                    int childCount = adapter.getCount();
                    int currentItem = mViewPager.getCurrentItem();
                    if (childCount > 1) {
                        if (currentItem == childCount - 1) {
                            mViewPager.setCurrentItem(0);
                        } else {
                            mViewPager.setCurrentItem(currentItem + 1);
                        }
                    }
                }
            }
            mHandler.postDelayed(mRunnable, AUTO_SCROLL_TIME);
        }
    };

    public void setData(List<? extends Banner> imagePaths) {
        mAdapter.setData(imagePaths);
        if (imagePaths != null && imagePaths.size() > 1) {
//            mIndicator.setVisibility(View.VISIBLE);
        } else {
//            mIndicator.setVisibility(View.GONE);
        }
        setUpAutoScroll();
        addIndictorGroup(imagePaths.size());
    }

    private void setUpAutoScroll() {
        if (isAutoScroll) {
            LogUtil.e("自动播放", "轮播");
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, AUTO_SCROLL_TIME);
        }
    }

    public void setAutoScrollEnable(boolean isEnable) {
        LogUtil.e("设置自动播放", "轮播" + isEnable);
        isAutoScroll = isEnable;
    }

    /**
     * item的点击回调
     *
     * @param bannerItemClick
     */
    public void setOnItemClickListener(OnBannerItemClick bannerItemClick) {
        mAdapter.setOnItemClickListener(bannerItemClick);
    }

    /**
     * banner单项点击事件
     *
     * @author heiyue heiyue623@126.com
     * @ClassName: OnBannerItemClick
     * @Description:
     * @date 2014-6-30 下午1:47:20
     */
    public interface OnBannerItemClick {
        /**
         * 当前点击项
         *
         * @param item
         * @param position
         */
        void onItemClick(Banner item, int position);
    }

    class SamplePagerAdapter extends PagerAdapter {
        private List<Banner> images;
        private OnBannerItemClick bannerItemClick;

        public SamplePagerAdapter() {
            this.images = new ArrayList<Banner>();
        }

        public void setOnItemClickListener(OnBannerItemClick bannerItemClick) {
            this.bannerItemClick = bannerItemClick;
        }

        public void setData(List<? extends Banner> images) {
            this.images.clear();
            if (images != null) {
                this.images.addAll(images);
            }
            notifyDataSetChanged();
        }

        public List<? extends Banner> getData() {
            return images;
        }

        @Override
        public int getCount() {
            return null == images ? 0 : Integer.MAX_VALUE;
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            ImageView photoView = new ImageView(container.getContext());
            if (images.size() == 0) {
                return photoView;
            }
            int truePosition = position % images.size();
            photoView.setImageResource(R.drawable.banner_def);// 默认图
            photoView.setScaleType(ScaleType.CENTER_CROP);
            container.addView(photoView, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            Banner banner = this.images.get(truePosition);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bannerItemClick != null) {
                        bannerItemClick.onItemClick(images.get(truePosition),
                                truePosition);
                    }
                }
            });
            if (null != banner) if (banner.getUrl() != null) {
                ImageLoader.getInstance().displayImage(banner.getUrl(), photoView, ImageLoaderImpl.cacheDiskOptions);
            }
            return photoView;
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

    /**
     * banner广告指示器
     *
     * @param size
     */
    private void addIndictorGroup(int size) {
        mIndicatorGroup.removeAllViews();
        if (size == 1) {
            // 如果只有一张banner图 则不显示底部圆圈
            return;
        }
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DeviceUtil.dip2px(mContext, 8), DeviceUtil.dip2px(mContext, 8));
            imageView.setBackgroundResource(R.drawable.btn_banner_indictor_check);
            if (i == 0) {
                imageView.setSelected(true);
            } else {
                imageView.setSelected(false);
            }
            if (i > 0) {
                params.leftMargin = DeviceUtil.dip2px(mContext, 6);
            }
            mIndicatorGroup.addView(imageView, params);
        }
    }

    private void setSelectedIndictor(int pos) {
        LogUtil.e("轮播指示", pos + "");
        int size = mAdapter.getData().size();
        if (size == 1) {
            // 如果只有一张banner图 则不显示底部圆圈
            return;
        }
        for (int i = 0; i < size; i++) {
            ImageView mImageView = (ImageView) mIndicatorGroup.getChildAt(i);
            mImageView.setSelected(i == pos ? true : false);
        }
    }

    public void setCurrentItem(int position) {
    }

    public void setOnPageChangeListener(
            final OnPageChangeListener onPageChangeListener) {
    }

}
