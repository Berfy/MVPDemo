package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.util.ext.ToastUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Berfy on 2017/7/25.
 * 编辑用户资料
 */
public class UserInfoEditFrag extends SimpleFrag implements View.OnClickListener {

    private static final String PARAM_TYPE = "type";//1昵称 2邮箱
    private static final String PARAM_TITLE = "title";//标题
    private static final String PARAM_HINT = "hint";//Edittext hint
    private static final String PARAM_REQUESTCODE = "requestCode";//回传代码
    public static final String PARAM_CONTENT = "content";//修改的内容

    public static final int TYPE_NICK = 1;//1昵称
    public static final int TYPE_EMAIL = 2;//2邮箱
    public static final int TYPE_CITY = 3;//城市

    private int mType = 1;//1昵称 2邮箱

    private int mRequestCode;//回传代码

    @BindView(R.id.edit)
    EditText mEdit;

    /**
     * 编辑
     *
     * @param type 1昵称 2邮箱
     */
    public static void start(Context context, int type, String title, String hint, int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, type);
        bundle.putString(PARAM_TITLE, title);
        bundle.putString(PARAM_HINT, hint);
        bundle.putInt(PARAM_REQUESTCODE, requestCode);
        SimpleFragAct.startForResult(context, new SimpleFragAct.SimpleFragParam(title, UserInfoEditFrag.class, bundle), requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_info_edit_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (null != getArguments()) {
            Bundle bundle = getArguments();
            mType = bundle.getInt(PARAM_TYPE);
            if (!TextUtils.isEmpty(bundle.getString(PARAM_TITLE)))
                getTitleBar().setTitleText(bundle.getString(PARAM_TITLE));
            if (!TextUtils.isEmpty(bundle.getString(PARAM_HINT)))
                mEdit.setHint(bundle.getString(PARAM_HINT));
            mRequestCode = bundle.getInt(PARAM_REQUESTCODE);
            UserResponse userResponse = UserClient.getLoginedUser();
            if (null != userResponse) {
                switch (mType) {
                    case TYPE_NICK:
                        if (!TextUtils.isEmpty(userResponse.nick_name)) {
                            mEdit.setText(userResponse.nick_name);
                            mEdit.setSelection(userResponse.nick_name.length());
                        }
                        break;
                    case TYPE_EMAIL:
                        if (!TextUtils.isEmpty(userResponse.email)) {
                            mEdit.setText(userResponse.email);
                            mEdit.setSelection(userResponse.email.length());
                        }
                        break;
                }
            }
        }
    }

    @OnClick({R.id.btn_submit})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                String content = mEdit.getText().toString().trim();
                switch (mType) {
                    case TYPE_NICK:
                        if (TextUtils.isEmpty(content)) {
                            ToastUtil.show(R.string.user_info_nick_hint);
                            return;
                        }
                        break;
                    case TYPE_EMAIL:
                        if (TextUtils.isEmpty(content)) {
                            ToastUtil.show(R.string.user_info_email_hint);
                            return;
                        } else if (!UserUtil.isEmail(content)) {
                            ToastUtil.show(R.string.user_info_email_tip);
                            return;
                        }
                        break;
                }
                Intent intent = new Intent();
                intent.putExtra(PARAM_CONTENT, content);
                setResult(mRequestCode, intent);
                finish();
                break;
        }
    }
}
