package com.wlb.agent.ui.user.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.CardInfoResponse;
import com.wlb.agent.ui.common.BasePresenter;
import com.wlb.agent.ui.user.presenter.view.IMyCardView;
import com.zxing.support.library.qrcode.QRCodeEncode;

import static com.android.util.LContext.getColor;

/**
 * 我的名片
 * <p>
 * Created by 曹泽琛.
 */

public class MyCardPresenter implements BasePresenter {
    private IMyCardView mView;
    private Task getCardTask;

    public MyCardPresenter(IMyCardView view) {
        this.mView = view;
    }

    private boolean isGetInfo;

    public void getCardInfo() {
        if (!NetworkUtil.isNetworkAvailable(mView.getContext())) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isGetInfo) return;
        getCardTask = UserClient.doGetCardInfo(new ICallback() {
            @Override
            public void start() {
                isGetInfo = true;
            }

            @Override
            public void success(Object data) {
                CardInfoResponse response = (CardInfoResponse) data;
                if (response.isSuccessful()) {
                    mView.setCardView(response);
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
                isGetInfo = false;
            }
        });
    }

    /**
     * 是否显示团队人数
     *
     * @return
     */
    public boolean showTeamCount() {
        return UserClient.showTeamCount();
    }

    /**
     * 是否显示服务客户人数
     *
     * @return
     */
    public boolean showCustomCount() {
        return UserClient.showCustomCount();
    }

    /**
     * 是否显示业绩
     *
     * @return
     */
    public boolean showAchievement() {
        return UserClient.showAchievement();
    }

    /**
     * 是否显示等级
     *
     * @return
     */
    public boolean showGrade() {
        return UserClient.showGrade();
    }

    /**
     * 获取二维码
     *
     * @return
     */
    public Bitmap getQrcodeBitmap() {
        int size = DeviceUtil.dip2px(mView.getContext(), 150);
        QRCodeEncode.Builder builder = new QRCodeEncode.Builder();
        builder.setBackgroundColor(getColor(R.color.white))
                .setOutputBitmapWidth(size)
                .setOutputBitmapHeight(size)
                .setOutputBitmapPadding(1)
//                .setCodeColor(getColor(R.color.c_00a0ea));
                .setLogo(BitmapFactory.decodeResource(mView.getContext().getResources(), R.drawable.qrcode_logo));
        String url = UserClient.getUserInviteUrl();
        return builder.build().encode(url);
    }
//-----------------------------------这是条完美的分割线---------------------------------------

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestory() {
        if (null != getCardTask) {
            getCardTask.stop();
        }
    }
}
