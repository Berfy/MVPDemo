package com.wlb.agent.ui.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.util.device.DeviceUtil;
import com.android.util.log.LogUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.adapter.Pwd6KeyBoardGridListAdapter;
import com.wlb.agent.util.DataParseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Berfy on 2017/7/24.
 * 6位密码支付键盘
 */
public class Pwd6KeyBorad extends RelativeLayout {

    private final String TAG = "Pwd6KeyBorad";
    private Context mContext;
    private View mView;
    private Pwd6KeyBoardGridListAdapter mAdapter;
    private GridView mGridView;
    private LinearLayout mLlAnim, mLlTouch;
    private boolean mIsAnimming;//是否正在动画
    private TranslateAnimation mAnim_open = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
    private TranslateAnimation mAnim_close = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
    private boolean mIsShowPassword;//是否明文显示密码
    private List<String> mPwds = new ArrayList<>();
    private StringBuffer mPwd = new StringBuffer();

    private OnPwd6SelectListener mOnPwd6SelectListener;

    public Pwd6KeyBorad(Context context) {
        super(context);
        init(context);
    }

    public Pwd6KeyBorad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Pwd6KeyBorad(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setListener(OnPwd6SelectListener onPwd6SelectListener) {
        mOnPwd6SelectListener = onPwd6SelectListener;
    }

    private void init(Context context) {
        mContext = context;
        mAnim_open.setDuration(300);
        mAnim_open.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimming = true;
                LogUtil.e(TAG, "mAnim_open  onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimming = false;
                LogUtil.e(TAG, "mAnim_open  onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnim_close.setDuration(300);
        mAnim_close.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimming = true;
                LogUtil.e(TAG, "mAnim_close  onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimming = false;
                LogUtil.e(TAG, "mAnim_close  onAnimationEnd");
                mLlTouch.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mView = View.inflate(mContext, R.layout.view_pwd6_key_board, null);
        addView(mView, getChildCount() - 1,
                new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mAdapter = new Pwd6KeyBoardGridListAdapter(mContext, DeviceUtil.dip2px(mContext, 20),
                DeviceUtil.dip2px(mContext, 2), 3, DeviceUtil.dip2px(mContext, 60));
        mGridView = (GridView) mView.findViewById(R.id.gridView);
        mLlAnim = (LinearLayout) mView.findViewById(R.id.layout_anim);
        mLlTouch = (LinearLayout) mView.findViewById(R.id.layout_touch);
        mGridView.setAdapter(mAdapter);
        mAdapter.refresh(DataParseUtil.getInstance(mContext).getPwdKeyBoradDatas());
        mGridView.setNumColumns(3);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mGridView.getLayoutParams();
        layoutParams.width = DeviceUtil.getScreenWidthPx(mContext) - DeviceUtil.dip2px(mContext, 20);
        mGridView.setLayoutParams(layoutParams);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String data = mAdapter.getList().get(i);
                if (data.equals("delete")) {
                    int size = mPwds.size();
                    if (size > 0) {
                        mPwd.delete(mPwd.length() - 1, mPwd.length());
                        mPwds.remove(size - 1);
                        mOnPwd6SelectListener.delete(mPwds, mPwd.toString());
                    }
                } else if (data.equals(" ")) {

                } else if (data.equals("确定")) {

                } else {
                    try {
                        if (mPwds.size() < 6) {
                            mPwd.append(data.toUpperCase());
                            if (mIsShowPassword) {
                                mPwds.add(data.toUpperCase());
                            } else {
                                mPwds.add("*");
                            }
                        }
                        if (mPwds.size() == 6) {
                            dismiss();
                            mOnPwd6SelectListener.pwd(mPwd.toString());
                        }
                        mOnPwd6SelectListener.input(mPwds);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mLlTouch.setOnClickListener(new

                                            OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dismiss();
                                                }
                                            });
    }

    public String getPwd() {
        return mPwd.toString();
    }

    public void showPassword(boolean isShowPassword) {

    }

    public void show() {
        if (!mIsAnimming) {
            mLlAnim.startAnimation(mAnim_open);
            mLlTouch.setVisibility(View.VISIBLE);
        }
    }

    public void dismiss() {
        if (!mIsAnimming) {
            mLlAnim.startAnimation(mAnim_close);
        }
    }

    public interface OnPwd6SelectListener {
        void input(List<String> pwds);

        void pwd(String pwd);

        void delete(List<String> pwds, String pwd);
    }
}
