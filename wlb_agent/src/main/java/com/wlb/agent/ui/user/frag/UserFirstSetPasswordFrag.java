package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.util.ext.ToastUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.main.TabAct;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置登录密码
 *
 * @author Berfy
 */
public class UserFirstSetPasswordFrag extends SimpleFrag implements View.OnClickListener {

    @BindView(R.id.edit_pwd)
    EditText mEditPwd;
    @BindView(R.id.edit_confirm_pwd)
    EditText mEditConfirmPwd;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.find_pwd_hint_pwd, UserFirstSetPasswordFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_first_set_password;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @OnClick({R.id.btn_submit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                String pwd = StringUtil.trim(mEditPwd);//原始密码
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.show(R.string.tip_pwd_null);
                    return;
                }
                String confirmPwd = StringUtil.trim(mEditConfirmPwd);
                if (TextUtils.isEmpty(confirmPwd)) {
                    ToastUtil.show(R.string.tip_confirm_pwd_null);
                    return;
                }
                if (!confirmPwd.equals(pwd)) {
                    ToastUtil.show(R.string.tip_pwd_not_same);
                    return;
                }
                TabAct.start(mContext);
                ToastUtil.show("设置成功");
                close();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
