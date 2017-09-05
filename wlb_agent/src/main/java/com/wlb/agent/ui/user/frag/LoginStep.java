package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.view.PhotoPickSheet;
import com.wlb.agent.ui.main.TabAct;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.imgloader.ImageFactory;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * @author 张全
 */

public class LoginStep extends SimpleFrag {

    @BindView(R.id.user_head_img)
    ImageView iv_head;
    @BindView(R.id.user_nickname)
    EditText et_nickname;

    private DisplayImageOptions imageOptions;
    private PhotoPickSheet photoPickSheet;

    private Task getUserInfoTask;

    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("快捷设置", LoginStep.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_login_step_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getTitleBar().setRightText("跳过");
        getTitleBar().setOnRightTxtClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardMainPage();
            }
        });

        photoPickSheet = new PhotoPickSheet(this);
        photoPickSheet.setReqSize(150);
        photoPickSheet.uploadPhoto(true);
        photoPickSheet.setOnPhotoUploadListener(onPhotoUploadListener);

        imageOptions = ImageFactory.getImageOptions(R.drawable.sliding_head);

        doGetUserInfo();
    }

    PhotoPickSheet.OnPhotoUploadListener onPhotoUploadListener = new PhotoPickSheet.OnPhotoUploadListener() {

        @Override
        public void uploadSuccess(View view, String localUrl, String url, Bitmap bitmap) {
            if (!url.isEmpty()) {
                ImageFactory.getUniversalImpl().getImg(url, iv_head,
                        null, imageOptions);
            } else {
                iv_head.setImageResource(R.drawable.sliding_head);
            }
        }

        @Override
        public void local(String localUrl, Bitmap bitmap) {

        }
    };

    private void setUserHead() {
        UserResponse loginedUser = UserClient.getLoginedUser();
        if (!TextUtils.isEmpty(loginedUser.avatar)) {
            ImageFactory.getUniversalImpl().getImg(loginedUser.avatar, iv_head, null, imageOptions);
        }
    }

    @OnClick(R.id.user_head_img)
    public void changeHead(View v) {
        photoPickSheet.show();
    }

    @OnClick(R.id.commit)
    public void commit(View v) {
        String nickName = et_nickname.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            ToastUtil.show("请输入姓名");
            return;
        }
        UserResponse loginedUser = UserClient.getLoginedUser();
        loginedUser.nick_name = nickName;
        doUpdateUserInfo(loginedUser);
    }

    private void forwardMainPage() {
        TabAct.start(mContext);
        close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (photoPickSheet.isShowing()) {
            photoPickSheet.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photoPickSheet.release();
        onPhotoUploadListener = null;
        if (null != updateTask) updateTask.stop();
        if (null != getUserInfoTask) getUserInfoTask.stop();
    }

    private boolean isUpdateUserInfo;
    private Task updateTask;

    private void doUpdateUserInfo(final UserResponse user) {
        if (isUpdateUserInfo) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "请稍后...");
        updateTask = UserClient.doUpdateUserInfo(user, new ICallback() {
            @Override
            public void success(Object data) {
                UserResponse response = (UserResponse) data;
                if (response.isSuccessful()) {
                    forwardMainPage();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isUpdateUserInfo = true;
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isUpdateUserInfo = false;
                dialog.dissmiss();
            }
        });
    }

    private boolean isGetUserInfo;

    private void doGetUserInfo() {
        if (isGetUserInfo) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            return;
        }

        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "请稍后...");
        getUserInfoTask = UserClient.doGetUserInfo(new ICallback() {

            @Override
            public void success(Object data) {
                UserResponse response = (UserResponse) data;
                if (response.isSuccessful()) {
                    setUserHead();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isGetUserInfo = true;
            }

            @Override
            public void failure(NetException e) {
            }

            @Override
            public void end() {
                isGetUserInfo = false;
                dialog.dissmiss();
            }
        });
    }
}
