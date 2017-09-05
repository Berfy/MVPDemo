package common.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 加载进度条
 *
 * @author 张全
 */
public class LoadingBar extends RelativeLayout {

    private ImageView mIvEmpty;
    private RelativeLayout mRlLoading;
    private LinearLayout mLlGif;
    private GifView mGifView;
    private TextView tv_loading;
    private LoadingStatus status;
    private LinearLayout mLlDefault;//默认提示布局
    private LinearLayout mLlContent;//自定义提示布局
    private boolean mIsShowCustomTipView;//是否显示自定义提示

    private Button mBtn;

    public LoadingBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LoadingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.common_loadingbar, this);
        mLlDefault = (LinearLayout) findViewById(R.id.layout_default);
        mLlContent = (LinearLayout) findViewById(R.id.layout_content);
        mIvEmpty = (ImageView) findViewById(R.id.loading_img);
        mRlLoading = (RelativeLayout) findViewById(R.id.layout_loading);
        mLlGif = (LinearLayout) findViewById(R.id.layout_gif);
        tv_loading = (TextView) findViewById(R.id.loading_text);
        mBtn = (Button) findViewById(R.id.btn);
    }

    Animation getAnim() {
        RotateAnimation anim = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(500);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.RESTART);
        return anim;
    }

    public void showCustomTip(View view) {
        mIsShowCustomTipView = true;
        mLlDefault.setVisibility(View.GONE);
        mLlContent.setVisibility(View.VISIBLE);
        mLlContent.addView(view);
    }

    public void showCustomTip(int layoutId) {
        mIsShowCustomTipView = true;
        mLlDefault.setVisibility(View.GONE);
        mLlContent.setVisibility(View.VISIBLE);
        mLlContent.addView(View.inflate(getContext(), layoutId, null));
    }

    public void showDefaultTip() {
        mIsShowCustomTipView = false;
        mLlDefault.setVisibility(View.VISIBLE);
        mLlContent.setVisibility(View.GONE);
    }


    /**
     * 设置加载状态
     *
     * @param status 加载状态
     */
    public void setLoadingStatus(LoadingStatus status) {
        setLoadingStatus(status, -1, null, null, 0, 0, "");
    }

    /**
     * 设置加载状态
     *
     * @param status 加载状态
     * @param imgRes 显示的图片
     */
    public void setLoadingStatus(LoadingStatus status, int imgRes) {
        setLoadingStatus(status, imgRes, null, null, 0, 0, "");
    }

    /**
     * 设置加载状态
     *
     * @param status 加载状态
     * @param text   显示的文字
     */
    public void setLoadingStatus(LoadingStatus status, String text) {
        setLoadingStatus(status, -1, text, null, 0, 0, "");
    }

    /**
     * 设置加载状态
     *
     * @param status 加载状态
     * @param imgRes 显示的图片
     * @param text   显示的文字
     */
    public void setLoadingStatus(LoadingStatus status, int imgRes, String text, OnClickListener onClickListener, int btnBgRes,
                                 int btnTextColor, String btnText) {
        try {
            if (null != this.status && this.status == status) {
                return;
            }

            if (status == LoadingStatus.SUCCESS) {
                loadSuccess();
                return;
            }

            this.status = status;
            setVisibility(View.VISIBLE);
            tv_loading.setVisibility(View.GONE);
            mBtn.setVisibility(View.GONE);
            // 设置文字
            String loadingText = null == text ? getContext().getString(status.text) : text;
            tv_loading.setText(loadingText);
            if (!TextUtils.isEmpty(loadingText)) {
                tv_loading.setVisibility(View.VISIBLE);
            }

            // 设置图片
            if (imgRes > 0) {
                mIvEmpty.setImageResource(imgRes);
//                mIvEmpty.setVisibility(View.VISIBLE);
            } else {
                mIvEmpty.setImageResource(R.drawable.ic_order_null);
            }

            if (null != onClickListener) {
                mBtn.setVisibility(View.VISIBLE);
                mBtn.setOnClickListener(onClickListener);
                if (btnBgRes > 0) {
                    mBtn.setBackgroundResource(btnBgRes);
                }
                if (btnTextColor > 0) {
                    mBtn.setTextColor(getResources().getColor(btnTextColor));
                }
                if (!TextUtils.isEmpty(btnText)) {
                    mBtn.setText(btnText);
                }
            }

            switch (status) {
                case START:// 加载中...
                    mRlLoading.setVisibility(View.VISIBLE);
                    mLlDefault.setVisibility(View.GONE);
                    showGif();
                    break;
                case RELOAD:// 重新加载
                    mRlLoading.setVisibility(View.GONE);
                    mLlDefault.setVisibility(View.VISIBLE);
                    dismissGif();
                    break;
                case NOCONNECTION:// 无网络连接
                    mRlLoading.setVisibility(View.GONE);
                    mLlDefault.setVisibility(View.VISIBLE);
                    dismissGif();
                    break;
                case SUCCESS:// 加载成功
                    setVisibility(View.GONE);
                    break;
                case EMPTY: // 无数据
                    mRlLoading.setVisibility(View.GONE);
                    mLlDefault.setVisibility(View.VISIBLE);
                    dismissGif();
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showGif() {
        if (null == mGifView) {
            mGifView = new GifView(getContext());
            mGifView.setMovieResource(R.drawable.carload);
            mLlGif.addView(mGifView, new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200,
                    getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200,
                    getResources().getDisplayMetrics())));
        } else {
            mLlGif.removeAllViews();
            mGifView = null;
        }
    }

    private void dismissGif() {
        if (null != mGifView) {
            mLlGif.removeAllViews();
            mGifView = null;
        }
    }

    public LoadingStatus getLoadingStatus() {
        return this.status;
    }

    /**
     * 是否正在加载
     *
     * @return
     */
    public boolean isLoading() {
        return getVisibility() == View.VISIBLE && null != status
                && (status == LoadingStatus.START);
    }

    /**
     * 加载成功
     */
    public void loadSuccess() {
        this.status = LoadingStatus.SUCCESS;
        setVisibility(View.GONE);
    }

    /**
     * 是否能够加载
     *
     * @return
     */
    public boolean canLoading() {
        if (null == status) return false;
        if (status == LoadingStatus.START || status == LoadingStatus.EMPTY || status == LoadingStatus.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * 加载状态
     *
     * @author zhangquan
     */
    public enum LoadingStatus {
        START(R.string.loadingbar_start), SUCCESS(R.string.loadingbar_success), NOCONNECTION(R.string.loadingbar_noconnection),
        RELOAD(R.string.loadingbar_reload), EMPTY(R.string.loadingbar_empty);

        public int text;

        private LoadingStatus(int text) {
            this.text = text;
        }
    }

    // --------------------------------
    public void setTextView(TextView textView) {
        this.tv_loading = textView;
    }
}
