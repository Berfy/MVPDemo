package com.wlb.agent.common.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.wlb.agent.common.service.BackgroundTaskService;
import com.wlb.agent.core.data.user.entity.ScoreReportType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import common.share.CShareListener;
import common.share.ShareAction;
import common.share.ShareApi;
import common.share.ShareImage;
import common.share.SharePlatform;

/**
 * 分享辅助类
 *
 * @author 张全
 */
public class ShareHelper {
    private SharePic sharePic;
    private static ShareHelper instance;
    private static final String TAG = "ShareHelper";
    private boolean onlyImage;//纯图分享
    private volatile Bitmap shareBitmap; //分享的bitmap
    private volatile List<String> imgList;
    private String shareContent;//分享的内容
    private String shareTitle;//分享的标题
    private String linkUrl; //分享的链接
    private ShareAction shareAction;
    private ProxyPlatformActionListener proxyListener;

    private ShareHelper() {
        this.sharePic = new SharePic();
    }

    public static synchronized ShareHelper getInstance() {
        if (null == instance) {
            instance = new ShareHelper();
        }
        return instance;
    }

    /**
     * 分享到第三方平台
     */
    public void share2Platform(Activity ctx, SharePlatform platform,
                               final CShareListener listener) {
        this.share2Platform(ctx, platform, listener, false);
    }

    public void share2Platform(Activity ctx, SharePlatform platform,
                               final CShareListener listener, boolean shareReport) {
        if (null == platform) {// 没有指定分享平台
            return;
        }
        if (platform == SharePlatform.WEIXIN || platform == SharePlatform.WEIXIN_CIRCLE) {
            boolean isInstalled = ShareApi.isWeiXinInstalled(ctx);
            if (!isInstalled) {
                ToastUtil.show("请先安装微信");
                return;
            }
        } else if (platform == SharePlatform.QQ || platform == SharePlatform.QZONE) {
            boolean isInstalled = ShareApi.isQQInstalled(ctx);
            if (!isInstalled) {
                ToastUtil.show("请先安装QQ");
                return;
            }
        }

        String log = toString();
        LogUtil.w(TAG, log);

        List<String> imgFiles = getShareBitmapFile();
        // 检测是否有分享数据
        boolean hasImg = imgFiles != null && !imgFiles.isEmpty();
        if (TextUtils.isEmpty(shareTitle) && TextUtils.isEmpty(shareContent) && !hasImg) {
            return;
        }


        this.proxyListener = new ProxyPlatformActionListener(ctx, platform,
                listener);
        this.proxyListener.setShareReport(shareReport);

        String title = shareTitle;
        String content = shareContent;

        shareAction = new ShareAction();
        shareAction.setPlatform(platform);//设置平台
        shareAction.setCallback(proxyListener);//分享回调
        if (!TextUtils.isEmpty(title)) { //标题
            shareAction.setTitle(title);
        }
        if (!TextUtils.isEmpty(content)) {//内容
            shareAction.setText(content);
        }
        if (!TextUtils.isEmpty(linkUrl)) {//链接
            shareAction.setTargetUrl(linkUrl);
        }
        if (imgFiles != null && !imgFiles.isEmpty()) {// 有图片
            ShareImage image = new ShareImage();
            image.addImg(imgFiles.get(0));
            shareAction.setMedia(image);
        }
        if (onlyImage) shareAction.onlyImage();

        shareAction.share(ctx);
    }

    private final static class ProxyPlatformActionListener extends Handler
            implements CShareListener {

        private final int ERROR = 0x01;
        private final int COMPLETE = 0x02;
        private final int CANCLE = 0x03;
        private CShareListener listener;
        private SharePlatform platformName;
        private Context ctx;
        private boolean shareReport;

        public ProxyPlatformActionListener(Context ctx, SharePlatform platformName, CShareListener listener) {
            super(Looper.getMainLooper());
            this.ctx = ctx;
            this.platformName = platformName;
            this.listener = listener;
        }

        public void setShareReport(boolean shareReport) {
            this.shareReport = shareReport;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listener == null) {
                return;
            }
            switch (msg.what) {
                case CANCLE:
                    listener.onCancel(platformName);
                    break;
                case COMPLETE:
                    listener.onResult(platformName);
                    break;
                case ERROR:
                    listener.onError(platformName, null);
                    break;
            }
        }

        @Override
        public void onResult(SharePlatform SharePlatform) {
            sendEmptyMessage(COMPLETE);
            if (shareReport && null != ctx) BackgroundTaskService.start(ctx, ScoreReportType.SHARE);
        }

        @Override
        public void onError(SharePlatform SharePlatform, Throwable throwable) {
            if (null != throwable) {
                throwable.printStackTrace();
            }
            sendEmptyMessage(ERROR);
        }

        @Override
        public void onCancel(SharePlatform SharePlatform) {
            sendEmptyMessage(CANCLE);
        }

        public void release() {
            ctx = null;
        }
    }

    public Bitmap shot(Activity ctx) {
        return SharePic.shot(ctx);
    }

    /**
     * 短信分享
     *
     * @param content 分享天气数据
     */
    public void sendSMS(Context ctx, String content) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setAction(Intent.ACTION_SENDTO);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.putExtra("sms_body", content);
        intent.setData(Uri.parse("smsto:"));
        ctx.startActivity(intent);

    }

    /**
     * 发送彩信
     */
    public void sendMMS(Context ctx, String content, File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("sms_body", content);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");
        ctx.startActivity(intent);
    }

    /**
     * 设置分享的图片
     *
     * @param bitmap
     * @return
     */
    public ShareHelper setShareImage(Bitmap bitmap) {
        this.shareBitmap = bitmap;
        imgList = null;
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();
        bitmapList.add(bitmap);
        imgList = sharePic.files(bitmapList);
        return this;
    }

    public ShareHelper setShareImage(String imgPath) {
        if (TextUtils.isEmpty(imgPath)) return this;
        imgList = null;
        imgList = new ArrayList<String>();
        imgList.add(imgPath);
        return this;
    }

    /**
     * 纯图分享
     *
     * @param onlyImage
     */
    public void setOnlyImageShare(boolean onlyImage) {
        this.onlyImage = onlyImage;
    }

    private List<String> getShareBitmapFile() {
        return imgList;
    }

    /**
     * 设置分享文字
     *
     * @param content
     * @return
     */
    public ShareHelper setShareContent(String content) {
        this.shareContent = content;
        return this;
    }

    /**
     * 设置分享标题
     *
     * @param title
     * @return
     */
    public ShareHelper setShareTitle(String title) {
        this.shareTitle = title;
        return this;
    }

    /**
     * 点击分享信息跳转的url
     *
     * @param linkUrl
     * @return
     */
    public ShareHelper setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
        return this;
    }

    public void recycleShareBitmap() {
        try {
            if (null != shareBitmap && !shareBitmap.isRecycled()) {
                shareBitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shareBitmap = null;
        }
    }

    public ShareHelper reset() {
        shareBitmap = null;
        imgList = null;
        shareContent = null;
        shareTitle = null;
        linkUrl = null;
        onlyImage = false;
        return instance;
    }

    public void release() {
        recycleShareBitmap();
        if(null!=shareAction)shareAction.release();
        if (null != proxyListener) proxyListener.release();
        proxyListener = null;
        sharePic = null;
        imgList = null;
        shareContent = null;
        linkUrl = null;
        shareTitle = null;
        instance = null;
    }

    @Override
    public String toString() {
        return "ShareHelper{" +
                "sharePic=" + sharePic +
                ", onlyImage=" + onlyImage +
                ", shareBitmap=" + shareBitmap +
                ", imgList=" + imgList +
                ", shareContent='" + shareContent + '\'' +
                ", shareTitle='" + shareTitle + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                '}';
    }
}
