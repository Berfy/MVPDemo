package com.wlb.agent.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.wlb.agent.R;

public class DialogUtil {

    private final String TAG = "PopupWindowUtil";
    private Context mContext;
    private Dialog mDialog;
    private TranslateAnimation mAnim_open = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
    private TranslateAnimation mAnim_close = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);

    public DialogUtil(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext, R.style.Dialog_untran);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mAnim_open.setDuration(300);
    }

    public void showVerifyCode(View.OnClickListener onClickListener) {
        final View dialogView = View.inflate(mContext, R.layout.dialog_verify_code, null);
        mDialog.setContentView(dialogView, new ViewGroup.LayoutParams(DeviceUtil.getScreenWidthPx(mContext),
                DeviceUtil.getScreenHeightPx(mContext)));
        EditText edit_code = ((EditText) dialogView.findViewById(R.id.edit_code));
        Button btn_ok = (Button) dialogView.findViewById(R.id.btn_ok);
        ImageView iv_random_code = ((ImageView) dialogView.findViewById(R.id.iv_verify_code));
        edit_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    btn_ok.performClick();
                }
                return false;
            }
        });
        iv_random_code.setImageBitmap(VerifyCode.getInstance().createBitmap());
        iv_random_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_random_code.setImageBitmap(VerifyCode.getInstance().createBitmap());
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edit_code.getText().toString().trim();
                if (VerifyCode.getInstance().getCode().equals(code)) {
                    if (null != onClickListener)
                        onClickListener.onClick(view);
                    dismiss();
                } else {
                    ToastUtil.show(R.string.tip_verify_code_error);
                }
            }
        });
        show(Gravity.CENTER);
    }

    public void showHotline(OnDialogClickListener onDialogClickListener) {
        final View dialogView = View.inflate(mContext, R.layout.common_dialog, null);
        mDialog.setContentView(dialogView, new ViewGroup.LayoutParams(DeviceUtil.getScreenWidthPx(mContext),
                DeviceUtil.getScreenHeightPx(mContext)));
    }

    private boolean isCanShow() {
        try {
            if (!((Activity) mContext).isFinishing() && null != mDialog && !mDialog.isShowing()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void show(int gravity) {
        try {
            if (isCanShow()) {
                LogUtil.e(TAG, "显示");
                mDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        try {
            if (!((Activity) mContext).isFinishing() && null != mDialog && mDialog.isShowing()) {
                LogUtil.e(TAG, "隐藏");
                mDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnDialogClickListener {
        void ok();

        void cancel();
    }

    public interface OnPopInputListener {
        void ok(String text);
    }
}
