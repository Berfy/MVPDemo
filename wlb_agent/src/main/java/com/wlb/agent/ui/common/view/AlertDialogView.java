package com.wlb.agent.ui.common.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wlb.agent.R;

import common.widget.dialog.DialogView;

public class AlertDialogView extends DialogView implements OnClickListener {
    private TextView tv_content, tv_content2;
    private LinearLayout contentBar;
    private int contentGravity1 = Gravity.CENTER, contentGravity2 = Gravity.CENTER;
    private TextView btn_right, btn_left;
    private TextView btn_single;
    private String title;
    private AlertDialogClickListener clickListener;
    private OnClickListener mLeftClickListener;
    private OnClickListener mRightClickListener;
    private OnClickListener mSingleClickListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    private int leftBtnColor = -1;
    private int rightBtnColor = -1;

    private String content, content2, leftBtnText, rightBtnText, singleBtnText;

    public AlertDialogView(Context ctx) {
        super(ctx);
    }

    @Override
    protected void initView(View view) {
        contentBar = (LinearLayout) findViewById(R.id.dialog_content_bar);
        tv_content = (TextView) findViewById(R.id.dialog_content);
        tv_content2 = (TextView) findViewById(R.id.dialog_content2);
        tv_content.setGravity(contentGravity1);
        tv_content2.setGravity(contentGravity2);
        btn_left = (TextView) findViewById(R.id.btn_left);
        btn_right = (TextView) findViewById(R.id.btn_right);
        btn_single = (TextView) findViewById(R.id.btn_single);

        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_single.setOnClickListener(this);

        if (leftBtnColor != -1) {
            btn_left.setTextColor(leftBtnColor);
        }
        if (rightBtnColor != -1) {
            btn_right.setTextColor(rightBtnColor);
        }

        // 标题
        if (!TextUtils.isEmpty(title)) {
            findViewById(R.id.dialog_titlebar).setVisibility(View.VISIBLE);
            TextView tv_title = (TextView) findViewById(R.id.dialog_title);
            tv_title.setText(title);
        }
        // 内容
        tv_content.setText(content);
        if (!TextUtils.isEmpty(content2)) {
            tv_content2.setVisibility(View.VISIBLE);
            tv_content2.setText(content2);
        }

        if (!TextUtils.isEmpty(singleBtnText)) {
            btn_single.setText(singleBtnText);
            btn_single.setVisibility(View.VISIBLE);
        } else {
            if (!TextUtils.isEmpty(leftBtnText)) {
                btn_left.setText(leftBtnText);
                btn_left.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(rightBtnText)) {
                btn_right.setText(rightBtnText);
                btn_right.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_alert;
    }

    public AlertDialogView setClickListener(AlertDialogClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public AlertDialogView setTitle(int title) {
        return setTitle(getContext().getString(title));
    }

    public AlertDialogView setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertDialogView setContent(int text) {
        return setContent(getContext().getString(text));
    }

    public AlertDialogView setContent(String text) {
        return setContent(text, Gravity.CENTER);
    }

    public AlertDialogView setContent(String text, int gravity) {
        this.content = text;
        this.contentGravity1 = gravity;
        return this;
    }

    public AlertDialogView setContent2(int text) {
        return setContent2(getContext().getString(text));
    }

    public AlertDialogView setContent2(String text) {
        return setContent2(text, Gravity.CENTER);
    }

    public AlertDialogView setContent2(String text, int gravity) {
        this.content2 = text;
        this.contentGravity2 = gravity;
        return this;
    }

    public AlertDialogView setSingleBtn(String text) {
        setSingleBtn(text, null);
        return this;
    }

    public AlertDialogView setSingleBtn(String text, OnClickListener l) {
        this.singleBtnText = text;
        this.mSingleClickListener = l;
        return this;
    }

    public AlertDialogView setLeftBtn(String leftBtnTxt) {
        setLeftBtn(leftBtnTxt, null);
        return this;
    }

    public AlertDialogView setLeftBtn(String leftBtnTxt, OnClickListener l) {
        this.leftBtnText = leftBtnTxt;
        this.mLeftClickListener = l;
        return this;
    }

    public AlertDialogView setLeftBtnColor(int leftBtnColor) {
        this.leftBtnColor = leftBtnColor;
        return this;
    }

    public AlertDialogView setRightBtnColor(int rightBtnColor) {
        this.rightBtnColor = rightBtnColor;
        return this;
    }

    public AlertDialogView setRightBtn(String rightBtnTxt) {
        setRightBtn(rightBtnTxt, null);
        return this;
    }

    public AlertDialogView setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    public AlertDialogView setRightBtn(String rightBtnTxt, OnClickListener l) {
        this.rightBtnText = rightBtnTxt;
        this.mRightClickListener = l;
        return this;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (null != mOnDismissListener) {
            mOnDismissListener.onDismiss(null);
        }
        if (v == btn_left) {
            if (null != mLeftClickListener) {
                mLeftClickListener.onClick(v);
            } else if (null != clickListener) {
                clickListener.onLeftClick();
            }
        } else if (v == btn_right) {
            if (null != mRightClickListener) {
                mRightClickListener.onClick(v);
            } else if (null != clickListener) {
                clickListener.onRightClick();
            }

        } else if (v == btn_single) {
            if (null != mSingleClickListener) {
                mSingleClickListener.onClick(v);
            } else if (null != clickListener) {
                clickListener.onSingleClick();
            }
        }
    }

    public interface AlertDialogClickListener {
        void onLeftClick();

        void onRightClick();

        void onSingleClick();
    }

}
