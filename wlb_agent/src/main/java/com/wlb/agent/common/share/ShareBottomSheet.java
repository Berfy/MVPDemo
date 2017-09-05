package com.wlb.agent.common.share;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.android.util.ext.ToastUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;

import common.share.CShareListener;
import common.share.ShareApi;
import common.share.SharePlatform;

/**
 * @author 张全
 */

public class ShareBottomSheet implements View.OnClickListener, CShareListener {
    private Context context;
    private BottomSheetDialog dialog;
    private ShareContentListener contentListener;
    private boolean operate;

    public ShareBottomSheet(Context context) {
        this.context = context;
        View contentView = View.inflate(context, R.layout.common_sharebar, null);
        contentView.findViewById(R.id.share_weixin).setOnClickListener(this);
        contentView.findViewById(R.id.share_weixin_friend).setOnClickListener(this);
        contentView.findViewById(R.id.share_qqspace).setOnClickListener(this);
        contentView.findViewById(R.id.share_qq).setOnClickListener(this);
        contentView.findViewById(R.id.share_sina).setOnClickListener(this);

        dialog = new BottomSheetDialog(context);
        dialog.setContentView(contentView);
    }

    @Override
    public void onClick(View v) {
        operate = true;
        int id = v.getId();
        if (id == R.id.share_weixin) {// 分享到微信
            share(SharePlatform.WEIXIN);
        } else if (id == R.id.share_weixin_friend) {// 分享到朋友圈
            share(SharePlatform.WEIXIN_CIRCLE);
        } else if (id == R.id.share_qqspace) {// 分享到QQ空间
            share(SharePlatform.QZONE);
        } else if (id == R.id.share_qq) {// 分享到QQ
            share(SharePlatform.QQ);
        } else if (id == R.id.share_sina) { //分享到新浪微博
            share(SharePlatform.SINA);
        }
    }

    // #########################

    private void share(SharePlatform platform) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        ready2ShareContent(platform);
        ShareHelper.getInstance().share2Platform((Activity) context, platform, this);
    }

    private void ready2ShareContent(SharePlatform platform) {
        ShareHelper shareHelper = ShareHelper.getInstance();
        shareHelper.reset();
        contentListener.setShareContent(platform, shareHelper);
    }

    public void addShareContentListener(ShareContentListener contentListener) {
        this.contentListener = contentListener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        dialog.setOnCancelListener(onCancelListener);
    }
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener){
        dialog.setOnDismissListener(onDismissListener);
    }

    public void release() {
        ShareHelper.getInstance().release();
        ShareApi.getInstance().release();
    }

    public void show() {
        operate = false;
        dialog.show();
    }

    public void hide() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareApi.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    //------------------------分享回调---------------------------------
    @Override
    public void onResult(SharePlatform platform) {
        ToastUtil.show("分享成功啦");
    }

    @Override
    public void onError(SharePlatform platform, Throwable t) {
        ToastUtil.show("分享失败啦");
    }

    @Override
    public void onCancel(SharePlatform platform) {
        ToastUtil.show("分享取消了");
    }


}
