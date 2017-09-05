package com.wlb.agent.ui.team.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.group.GroupClient;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * Created by JiaHongYa
 * <p>
 * 团队名称
 */

public class TeamNameFrag extends SimpleFrag implements View.OnClickListener {

    private Task doUploadTeamNameTask;
    @BindView(R.id.team_create_name)
    TextView et_teamName;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("团队名称",
                TeamNameFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.team_name_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
    }

    private boolean isUploadTeamName;

    private void doModifyTeamName(final String teamName) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isUploadTeamName) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "上传中...");
        doUploadTeamNameTask = GroupClient.doModifyTeamName(teamName, new ICallback() {
            @Override
            public void start() {
                isUploadTeamName = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new PushEvent(Constant.IntentAction.TEAM_NAME));
                    ToastUtil.show("修改成功");
                    close();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isUploadTeamName = false;
                dialog.dissmiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != doUploadTeamNameTask) {
            doUploadTeamNameTask.stop();
        }
    }

    @OnClick(R.id.submit)
    public void onClick(View v) {
        String teamName = StringUtil.trim(et_teamName);
        if (TextUtils.isEmpty(teamName)) {
            ToastUtil.show("团队名称不能为空");
            return;
        } else if (teamName.length() < 2 || teamName.length() > 20) {
            ToastUtil.show("请输入2-20位字母或中文名称");
            return;
        }
        doModifyTeamName(teamName);
    }
}
