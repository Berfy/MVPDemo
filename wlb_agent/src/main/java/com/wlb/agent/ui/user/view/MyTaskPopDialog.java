package com.wlb.agent.ui.user.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.wlb.agent.R;

import common.widget.dialog.DialogView;

/**
 * Created by 曹泽琛.
 */

public class MyTaskPopDialog extends DialogView {
    private String tip;

    public MyTaskPopDialog(Context ctx, String tip) {
        super(ctx);
        this.tip = tip;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mytask_loading;
    }

    @Override
    protected void initView(View view) {
        TextView tvTip = (TextView) findViewById(R.id.task_tip);
        tvTip.setText(tip);
        TextView tvSure = (TextView) findViewById(R.id.task_pop_close);
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
