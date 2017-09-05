package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wlb.agent.R;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

/**
 * 提现申请结果
 * <p/>
 * Created by 曹泽琛.
 */
public class WalletWithdrawResultFrag extends SimpleFrag {

    public static final String WITHDRAW_MSG = "withdraw_msg";

    public static void start(Context context, String msg) {
        Bundle bundle = new Bundle();
        bundle.putString(WITHDRAW_MSG, msg);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("提现申请",
                WalletWithdrawResultFrag.class, bundle);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.wallet_withdraw_result_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        String msg = getArguments().getString(WITHDRAW_MSG);

        TextView tv_msg = findViewById(R.id.success_withdraw_text1);
        tv_msg.setText(msg);

        Button btn_sure = findViewById(R.id.withdraw_result_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

}
