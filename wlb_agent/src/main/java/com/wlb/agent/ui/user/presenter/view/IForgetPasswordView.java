package com.wlb.agent.ui.user.presenter.view;

import android.widget.TextView;

import com.wlb.agent.ui.common.BaseView;

/**
 * 忘记密码
 * @author 张全
 */

public interface IForgetPasswordView extends BaseView {

    TextView getCodeView();
    void showFocus();
}
