package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.AuthStatus;
import com.wlb.agent.core.data.user.entity.IdAuthInfo;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 实名认证
 * <p>
 * Created by 曹泽琛.
 */

public class ProveNameFrag extends SimpleFrag {

    @BindView(R.id.prove_head)
    ImageView imgHead;
    @BindView(R.id.prove_text)
    TextView tv_hint;
    @BindView(R.id.prove_text_tip)
    TextView tv_hint_tip;
    @BindView(R.id.prove_name)
    TextView tv_name;
    @BindView(R.id.prove_card)
    TextView tv_card;
    @BindView(R.id.prove_uploading)
    TextView tv_commit;
    @BindView(R.id.layout_upload)
    RelativeLayout layoutUpload;
    private IdAuthInfo id_auth_info;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.prove_name,
                ProveNameFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.prove_name_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView(){
        UserResponse userInfo = UserClient.getLoginedUser();
        id_auth_info = userInfo.id_auth_info;
        if (null != id_auth_info) {
            tv_name.setText(id_auth_info.real_name);
            tv_card.setText(id_auth_info.certificate_no);
            setView(id_auth_info.getAuthStatus());
        }
        tv_name.setGravity(Gravity.RIGHT);
        tv_card.setGravity(Gravity.RIGHT);
    }

    private void setView(AuthStatus authStatus) {
        imgHead.setImageDrawable(getResources().getDrawable(
                R.drawable.prove_head_fail));
        if(TextUtils.isEmpty(id_auth_info.tip)){
            tv_hint_tip.setVisibility(View.GONE);
        }else{
            tv_hint_tip.setVisibility(View.VISIBLE);
            tv_hint_tip.setText("( " + id_auth_info.tip + " )");
        }
        switch (authStatus) {
            case AUTH_NOT:
                tv_hint.setText("未实名认证");
                setEnabled(true);
                break;
            case AUTHING:
                tv_hint.setText("审核中");
                setEnabled(false);
                break;
            case AUTH_FAIL:
                tv_hint.setText("审核失败");
                tv_hint_tip.setText("( " + id_auth_info.tip + " )");
                setEnabled(true);
                break;
            case AUTH_SUCCESS:
                imgHead.setImageDrawable(getResources().getDrawable(
                        R.drawable.prove_head_succeed));
                tv_hint.setText("您已通过实名认证");
                setEnabled(false);
                break;
        }
    }

    private void setEnabled(boolean enable) {
        if (enable) {//可编辑
            tv_name.setEnabled(true);
            tv_card.setEnabled(true);
            tv_commit.setEnabled(true);
            layoutUpload.setVisibility(View.VISIBLE);
        } else {//不可编辑
            tv_name.setEnabled(false);
            tv_card.setEnabled(false);
            tv_commit.setEnabled(false);
            layoutUpload.setVisibility(View.GONE);
            //加密姓名和身份证
            String encryptionName = StringUtil.trim(tv_name);
            if (null != encryptionName) {
                String nameSub = encryptionName.substring(0, 1);
                encryptionName = encryptionName.replace(nameSub, "*");
                tv_name.setText(encryptionName);
            }
            String encryptionCard = StringUtil.trim(tv_card);
            if (null != encryptionCard) {
                String cardSub = encryptionCard.substring(2, encryptionCard.length() - 2);
                encryptionCard = encryptionCard.replace(cardSub, "***************");
                tv_card.setText(encryptionCard);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.prove_uploading)
    public void onClick() {
        String nameR = tv_name.getText().toString();
        if (StringUtil.isEmpty(nameR)) {//检查姓名
            ToastUtil.show("请输入姓名");
            return;
        }
        String certificateNoR = tv_card.getText().toString();
        if (!UserUtil.checkShowCardNo(certificateNoR)) {//检查身份证号
            return;
        }
        ProveUploadingFrag.start(mContext, nameR, certificateNoR);
    }
}
