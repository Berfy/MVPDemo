package com.wlb.agent.ui.common;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.task.Task;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.common.share.ShareBottomSheet;
import com.wlb.agent.common.share.ShareContentListener;
import com.wlb.agent.common.share.ShareHelper;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.offer.entity.OfferShareChooseData;
import com.wlb.agent.core.data.pay.alipay.AliPay;
import com.wlb.agent.core.data.pay.alipay.PayResult;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.view.AlertDialogView;
import com.wlb.agent.ui.common.view.PhotoPickSheet;
import com.wlb.agent.ui.common.view.UploadPhoto;
import com.wlb.agent.ui.offer.frag.OfferShareFrag;
import com.wlb.agent.ui.order.frag.OrderDetailFrag;
import com.wlb.agent.ui.order.frag.OrderFrag;
import com.wlb.agent.ui.order.pay.InsurancePay;
import com.wlb.agent.ui.order.pay.InsurancePayAct;
import com.wlb.agent.ui.user.helper.GetPhotoUtil;
import com.wlb.agent.ui.user.helper.ocr.OcrConstant;
import com.wlb.agent.ui.user.helper.ocr.parser.DataParser;
import com.wlb.agent.ui.user.helper.ocr.parser.DrivingLicense;
import com.wlb.agent.ui.user.helper.ocr.utils.HttpUtil;
import com.wlb.agent.util.GsonUtil;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.agent.util.UrlHandlerResult;
import com.wlb.agent.util.WebUrlHandler;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.SimpleFragAct.SimpleFragParam;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import common.share.ShareApi;
import common.share.SharePlatform;
import common.widget.LoadingBar;
import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;
import common.widget.webview.AutoWebView;
import rx.functions.Action1;

/**
 * WebView页面
 *
 * @author zhangquan
 */
public class WebViewFrag extends SimpleFrag implements OnClickListener {

    private final String TAG = "WebViewFrag";
    public static final String PARAM = "PARAM";
    private static final String PHONE_PREFIX = "wtai://wp/mc;";
    private static final String TEL_SCHEME = "tel:";
    private AutoWebView mAutoWebView;
    private WebView mWebView;
    private ProgressBar progressBar;
    private LoadingBar loadingBar;
    private String curPageTitle;
    private WebViewParam webViewParam;
    private PopupWindowUtil mPopupWindowUtil;
    private String loadFailUrl;
    private Task uploadTask;
    // 分享
    private ShareBottomSheet shareBottomSheet;
    private ShareInfo shareInfo = new ShareInfo();

    //支付
    private InsurancePay insurancePay;

    private UploadPhoto uploadPhoto;//行驶证识别

    private boolean mIsClose;

    public static class WebViewParam implements Serializable {

        public WebViewParam() {
        }

        public WebViewParam(String title, boolean shouldResetTitle, String url, ShareInfo shareInfo) {
            this.title = title;
            this.shouldResetTitle = shouldResetTitle;
            this.url = url;
            this.shareInfo = shareInfo;
        }

        //标题
        public String title;
        //是否需要重置title
        public boolean shouldResetTitle = true;
        //链接URL
        public String url;
        //分享
        public ShareInfo shareInfo;
    }

    public static class ShareInfo implements Serializable {
        public String title;
        public String content;
        public String link;
        public int type;
    }

    public static Bundle getParamBundle(WebViewParam param) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, param);
        return bundle;
    }

    public static SimpleFragParam getStartParam(WebViewParam param) {
        return new SimpleFragParam(param.title,
                WebViewFrag.class, WebViewFrag.getParamBundle(param));
    }

    public static void start(Context ctx, WebViewParam param) {
        SimpleFragAct.start(ctx, getStartParam(param));
    }

    public static void start(Context ctx, SimpleFragParam param) {
        SimpleFragAct.start(ctx, param);
    }

    @Override
    public int getLayoutId() {
        return R.layout.common_webview;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 网络无连接
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            close();
            return;
        }
        if (null != getArguments())
            webViewParam = (WebViewParam) getArguments().getSerializable(PARAM);

        if (null == webViewParam) {
            close();
            return;
        }
        String url = null;
        if (null != webViewParam) url = webViewParam.url;
        //请求无链接
        if (TextUtils.isEmpty(url)) {
//            showToastShort("请求无链接");
            close();
            return;
        }
        LogUtil.d("WebView", url + "");

//        webViewParam.url="https://test.wolaibao.com/wlb_official/h5/app/wlb_html/NewWlb/index.html";
        webViewParam.url = H5.checkUrl(webViewParam.url);
        init();
    }

    @Override
    public void onClick(View v) {
        if (v == loadingBar && loadingBar.canLoading()) {
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                ToastUtil.show(R.string.net_noconnection);
                return;
            }
            mWebView.reload();
//            if (!TextUtils.isEmpty(loadFailUrl)) {
//                webView.loadUrl(loadFailUrl);
//            } else {
//                webView.loadUrl(webViewParam.url);
//            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Method onResume = mWebView.getClass().getMethod("onResume");
            if (null != onResume) onResume.invoke(mWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginCallback();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            Method onPause = mWebView.getClass().getMethod("onPause");
            if (null != onPause) onPause.invoke(mWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (handleBackEvent()) {
            return true;
        }
        return super.onBackPressed();
    }

    private boolean handleBackEvent() {
        if (null != mWebView && mWebView.canGoBack() && !mIsClose) {
            mWebView.goBack();
            doLicenseOcr = false;//行驶证识别
            LogUtil.i(TAG, "返回");
            return true;
        }
        LogUtil.i(TAG, "webview不可返回");
        return false;
    }

    protected void init() {
        mPopupWindowUtil = new PopupWindowUtil(getContext());
        uploadPhoto = new UploadPhoto(this);
        uploadPhoto.setReqSize(700);
        uploadPhoto.setPhotoChooser(new UploadPhoto.PhotoChooser() {
            @Override
            public void onPhotoChoose(String localPath) {
                try {
                    Bitmap bitmap = GetPhotoUtil.resizeBitmap(localPath, 700);
                    imgBytes = UploadPhoto.getBytes(bitmap);
                    LogUtil.d("NetClient", "行驶证拍照 图片大小=" + (imgBytes.length * 1.0f / 1024 / 1024) + "MB");
                    if (!(imgBytes.length > (1000 * 1024 * 4))) {
                        ocrTask = new MyAsynTask();
                        ocrTask.execute();
                    } else {
                        ToastUtil.show("图片大于4M，请选择小于4M的图片。");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.show("图片处理失败");
                }
            }
        });
        mAutoWebView = findViewById(R.id.auto_webview);
        mWebView = mAutoWebView.getWebView();
        progressBar = findViewById(R.id.pb);
        loadingBar = findViewById(R.id.loadingBar);
        loadingBar.setOnClickListener(this);

        getTitleBar().setOnLeftBtnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleBackEvent()) {
                    return;
                }
                close();
            }
        });
        getTitleBar().setOnLeftSecondTxtClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        //分享按钮
        if (null != webViewParam.shareInfo) {
            shareInfo = webViewParam.shareInfo;
            getTitleBar().setRightBtnDrawable(R.drawable.share);
            getTitleBar().mRightButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareBottomSheet.show();
                }
            });
        }

        shareBottomSheet = new ShareBottomSheet(mContext);
        shareBottomSheet.addShareContentListener(new ShareContentListener() {

            @Override
            public void setShareContent(SharePlatform platform, ShareHelper shareHelper) {
                if (null == shareInfo) {
                    shareBottomSheet.hide();
                    return;
                }
                Bitmap shareBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.share_action);
                shareHelper.setShareTitle(shareInfo.title)
                        .setShareContent(shareInfo.content)
                        .setShareImage(shareBitmap)
                        .setLinkUrl(shareInfo.link);
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        mWebView.addJavascriptInterface(new JavaInterface(), "nativeApp");

        //不使用缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.clearCache(true);

        mWebView.loadUrl(webViewParam.url);
        mWebView.setWebChromeClient(new DWebChromeClient());
        mWebView.setWebViewClient(new DWebViewClient());
        mWebView.setDownloadListener(new DWebViewDownLoadListener());

        addAction(Constant.IntentAction.ORDER_BUY_SUCCESS);
        addAction(Constant.IntentAction.ORDER_BUY_FAIL);
    }

    private void showShareTitle(boolean isShow, OnClickListener onClickListener) {
        getTitleBar().setRightText(isShow ? getString(R.string.share) : "");
        getTitleBar().setOnRightTxtClickListener(onClickListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(PushEvent event) {
        String action = event.getAction();
        if (containsAction(action)) {
            if (action.equals(Constant.IntentAction.ORDER_BUY_SUCCESS)) {
                mWebView.loadUrl("javascript:payResult('2')");
            } else if (action.equals(Constant.IntentAction.ORDER_BUY_FAIL)) {
                mWebView.loadUrl("javascript:payResult('1')");
            }
        }
    }

    private void loginCallback() {
        //回调token
        UserResponse loginedUser = UserClient.getLoginedUser();
        if (null != loginedUser) {
            mWebView.loadUrl("javascript:lgoinComplete('" + loginedUser.token + "')");
        }
    }

    /**
     * 辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
     */
    private final class DWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialogView dialogView = new AlertDialogView(mWebView.getContext())
                    .setTitle("提示")
                    .setContent(message)
                    .setSingleBtn("确定", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            result.confirm();
                        }
                    });
            new EffectDialogBuilder(mWebView.getContext())
                    .setCancelable(false)
                    .setCancelableOnTouchOutside(false)
                    .setContentView(dialogView).show();
//            return super.onJsAlert(view, url, message, result);
            return true;
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialogView dialogView = new AlertDialogView(mWebView.getContext())
                    .setContent(message)//
                    .setRightBtn("确定", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            result.confirm();
                        }
                    })
                    .setLeftBtn("取消", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            result.cancel();
                        }
                    });
            new EffectDialogBuilder(mWebView.getContext())
                    .setCancelable(false)
                    .setCancelableOnTouchOutside(false)
                    .setContentView(dialogView).show();
//            return super.onJsConfirm(view, url, message, result);
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (title.startsWith("404") || title.startsWith("502")) {
                LogUtil.i(TAG, "onReceivedError");
                loadStatus = LOAD_ERROR;
                mWebView.setVisibility(View.INVISIBLE);
                //显示上层的错误页面
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.NOCONNECTION);
                } else {
                    loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD);
                }
            }
            curPageTitle = title;

            if (webViewParam.shouldResetTitle) {
                if (null != titleBarView) {
                    getTitleBar().setTitleText(title);
                }
            }

            if (null != shareInfo && TextUtils.isEmpty(shareInfo.title)) {
                shareInfo.title = curPageTitle;
            }
        }

//        // Andorid 4.1+
//        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
//            openFileChooser(uploadFile);
//        }
//
//        // Andorid 3.0 +
//        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
//            openFileChooser(uploadFile);
//        }
//
//        // Android 3.0
//        public void openFileChooser(ValueCallback<Uri> uploadFile) {
//        }
//
//        //Android5.0+
//        @Override
//        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//            return true;
//        }

    }

    /**
     * 主要帮助WebView处理各种通知、请求事件的
     */
    private final int LOAD_START = 1;
    private final int LOAD_ERROR = 2;
    private final int LOAD_FINISHED = 3;
    private int loadStatus = LOAD_FINISHED;

    private class DWebViewClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            progressBar.setProgress(view.getProgress());
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mIsClose = false;
            LogUtil.e(TAG, "  onPageStarted加载地址  " + url);
            loadStatus = LOAD_START;
            progressBar.setProgress(1);
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
            //报价页面
            if (url.lastIndexOf(WebUrlHandler.PAGE_OFFER) != -1) {//报价页
//                showShareTitle(true, new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mWebView.loadUrl("javascript:wlbShareData()");
//                    }
//                });
            } else {
                showShareTitle(false, null);
                if (url.lastIndexOf(WebUrlHandler.PAGE_OFFER_SUC) != -1) {//核保结果页
                    mIsClose = true;
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //页面加载完毕
            progressBar.setProgress(100);
            progressBar.setVisibility(View.GONE);
            if (loadStatus != LOAD_ERROR) {
                loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.SUCCESS);
            }
            loadStatus = LOAD_FINISHED;
            if (view.canGoBack()) {//有历史页面显示关闭按钮
                getTitleBar().setLeftSecondText(R.string.close);
            } else {
                getTitleBar().setLeftSecondText("");
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            loadStatus = LOAD_ERROR;
            loadFailUrl = failingUrl;
            //显示上层的错误页面
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.NOCONNECTION);
            } else {
                loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String url = request.getUrl().toString();
                LogUtil.i(TAG, "  21以上系统加载地址  " + url);
                return urlResult(url);
            }
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return urlResult(url);
        }
    }

    private boolean urlResult(String url){
        LogUtil.i(TAG, "  urlResult加载地址  " + url);
        String number = "";
        UrlHandlerResult urlHandlerResult = WebUrlHandler.handleUrlLink(mContext, url);
        if (url.lastIndexOf(WebUrlHandler.OFFER_SHARE) != -1) {//报价分享-点击分享
            try {
                OfferShareChooseData offerShareChooseData = GsonUtil.getInstance()
                        .toClass(urlHandlerResult.paramMap.get("data"), OfferShareChooseData.class);
                LogUtil.i(TAG, "参数" + urlHandlerResult.paramMap.get("data"));
//                ToastUtil.show("参数" + urlHandlerResult.paramMap.get("data"));
                if (null != offerShareChooseData.getCompany() && offerShareChooseData.getCompany().size() > 0) {
                    OfferShareFrag.start(mContext, offerShareChooseData);
                } else {
                    ToastUtil.show(R.string.offer_share_go_tip);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (url.lastIndexOf(WebUrlHandler.PAGE_OFFER_SUC) != -1) {//核保结果页
            mIsClose = true;
        } else if (url.lastIndexOf(WebUrlHandler.OFFER_SUC_ORDER_LIST) != -1) {//核保结果页-订单列表
            mIsClose = true;
            OrderFrag.start(mContext);
            close();
        } else if (url.lastIndexOf(WebUrlHandler.OFFER_SUC_ORDER_DETAIL) != -1) {//核保结果页-订单详情
            mIsClose = true;
            OrderDetailFrag.start(mContext, urlHandlerResult.paramMap.get("orderId"));
            close();
        } else if (url.startsWith(PHONE_PREFIX)) {
            number = url.substring(PHONE_PREFIX.length());
        } else if (url.startsWith(TEL_SCHEME)) {
            number = url.substring(TEL_SCHEME.length());
        }
        // 拨打电话
        if (!TextUtils.isEmpty(number)) {
            StringBuilder telNumber = new StringBuilder();
            telNumber.append(TEL_SCHEME).append(number.trim());
            Uri uri = Uri.parse(telNumber.toString());
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(dialIntent);
            return true;
        }
        doLicenseOcr = false;

        //-------------------支付宝网页支付----------------
        if (url.startsWith("alipays://platformapi/startApp")) {
//                //检测是否已安装支付宝APP
            boolean isAliPayAPP = DeviceUtil.isAppInstalled(mContext, "com.eg.android.AlipayGphone");
            if (isAliPayAPP) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        }
        // -----------------handle url---------------------
        if (url.startsWith(WebUrlHandler.HOT_LINE)) {
            mPopupWindowUtil.showHotLine();
            return true;
        }
        if (url.startsWith(WebUrlHandler.UI_TIMESPINNER)) {
            openTimeSpinner(url);
            return true;
        }
        //行驶证识别
        if (url.startsWith(WebUrlHandler.OCR)) {
            doLicenseOcr = true;
            showOcrPickView();
            return true;
        }

        if (url.startsWith(WebUrlHandler.FILE)) {
            String callback = urlHandlerResult.paramMap.get("callback");
            String box = urlHandlerResult.paramMap.get("box");
            showFilePicker(callback, box);
            return true;
        }

        //分享
        if (null != urlHandlerResult.shareInfo) {
            shareInfo = urlHandlerResult.shareInfo;
            if (!NetworkUtil.isNetworkAvailable(getContext())) {
                ToastUtil.show(R.string.net_noconnection);
                return true;
            }

            SharePlatform sharePlatform = null;
            switch (shareInfo.type) {
                case 0:
                    shareBottomSheet.show();
                    return true;
                case 1:
                    sharePlatform = SharePlatform.WEIXIN;
                    break;
                case 2:
                    sharePlatform = SharePlatform.WEIXIN_CIRCLE;
                    break;
                case 3:
                    sharePlatform = SharePlatform.QQ;
                    break;
                case 4:
                    sharePlatform = SharePlatform.QZONE;
                    break;
                case 5:
                    sharePlatform = SharePlatform.SINA;
                    break;
                case 6:
                    RxPermissions rxPermissions = new RxPermissions(getActivity());
                    rxPermissions
                            .request(Manifest.permission.SEND_SMS)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean granted) {
                                    if (granted) {
                                        Uri smsToUri = Uri.parse("smsto:");
                                        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                                        intent.putExtra("sms_body", "【我来保】  " + shareInfo.content + "  " + shareInfo.link);
                                        startActivity(intent);
                                    } else {
                                        ToastUtil.show("短信发送权限被拒绝");
                                    }
                                }
                            });

                    return true;
            }

            final SharePlatform sp = sharePlatform;
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    )
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean granted) {
                            if (granted) {
                                ShareHelper shareHelper = ShareHelper.getInstance();
                                shareHelper.reset();
                                Bitmap shareBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.share_action);
                                shareHelper.setShareTitle(shareInfo.title)
                                        .setShareContent(shareInfo.content)
                                        .setShareImage(shareBitmap)
                                        .setLinkUrl(shareInfo.link);

                                boolean shareReport = webViewParam.url.contains(H5.INVITE_NEW);
                                ShareHelper.getInstance().share2Platform((Activity) getContext(), sp, null, shareReport);
                            } else {
                                ToastUtil.show(R.string.permission_failed);
                            }
                        }
                    });
            return true;
        }

        //支付宝支付
        if (url.startsWith(WebUrlHandler.ALI_PAY) && null != urlHandlerResult.payInfo) {
            if (null == insurancePay) insurancePay = new InsurancePay(getActivity());
            insurancePay.openAlipayClient(urlHandlerResult.payInfo, new AliPay.AliPayCallBack() {
                @Override
                public void start() {

                }

                @Override
                public void end(PayResult payResult) {
                    if (isDestoryed) return;
                    int result = payResult.isSuccess() ? 2 : 1;
                    mWebView.loadUrl("javascript:payResult('" + result + "')");
                }
            });
        }
        //微信支付
        if (url.startsWith(WebUrlHandler.WX_PAY) && null != urlHandlerResult.payInfo) {
            InsurancePayAct.start(mContext, urlHandlerResult.payInfo);
        }

        //加密回调
        if (!TextUtils.isEmpty(urlHandlerResult.encryptData)) {
            LogUtil.e(TAG, "获取加密信息2" + urlHandlerResult.encryptData);
            mWebView.loadUrl("javascript:desEncryptRsult('" + urlHandlerResult.encryptData + "')");
            return true;
        }

        //关闭页面
        if (urlHandlerResult.closePage) {
            close();
            return true;
        }
        return urlHandlerResult.handled;
    }

    /**
     * 下载
     *
     * @author zhangquan
     */
    private class DWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != shareBottomSheet) shareBottomSheet.release();
        if (null != uploadTask) uploadTask.stop();
        if (null != filePicker) filePicker.release();
        if (null != ocrPickView) ocrPickView.release();
        if (null != insurancePay) insurancePay.release();
        try {
            if (null != ocrTask) {
                ocrTask.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        releaseWebView();
    }

    private void releaseWebView() {
        try {
            if (null != mWebView) {
                mWebView.setVisibility(View.GONE);
                mWebView.removeAllViews();
                mWebView.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OCR_IMPORT_PHOTO || requestCode == OCR_TAKE_PHOTO) {
            uploadPhoto.onActivityResult(requestCode, resultCode, data);
            //上传行驶证
//            if (requestCode == OCR_IMPORT_PHOTO) {
//                LogUtil.i(TAG, "行驶证识别OCR_IMPORT_PHOTO");
//                Uri uri = null;
//                if (null != data) {
//                    uri = data.getData();
//                }
//                if (uri == null) {
//                    ToastUtil.show("获取图片失败");
//                    return;
//                }
//                try {
//                    String uriPath = UriUtil.getAbsolutePath(uri, mContext);
//                    extension = GetPhotoUtil.getExtensionByPath(uriPath);
//
//                    Bitmap bitmap = GetPhotoUtil.reduceImage(uriPath, 1000);
//                    imgBytes = UploadPhoto.getBytes(bitmap);
//                    LogUtil.d("NetClient", "行驶证拍照 图片大小=" + (imgBytes.length * 1.0f / 1024 / 1024) + "MB");
//
//                    if (!(imgBytes.length > (1000 * 1024 * 4))) {
//                        ocrTask = new MyAsynTask();
//                        ocrTask.execute();
//                    } else {
//                        ToastUtil.show("图片大于5M，请选择小于5M的图片。");
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    ToastUtil.show("图片处理失败");
//                }
//            } else {
//                LogUtil.i(TAG, "行驶证识别OCR_TAKE_PHOTO");
//                if (null != data) {
//                    String result = data.getStringExtra("result");
//                    handleOcrResult(result);
//                }
//            }
        } else if (requestCode == UploadPhoto.GET_IMG_FROM_CAMERA || requestCode == UploadPhoto.GET_IMG_FROM_PHOTO_ALBUM) {
            //上传文件
            if (null != filePicker) {
                filePicker.dismiss();
                filePicker.onActivityResult(requestCode, resultCode, data);
                String uploadImg = filePicker.getUploadPhoto().getUploadImg();
                filePicker.release();
                doUploadFile(uploadImg);
            }
        } else {
            //分享
            if (null != shareInfo)
                ShareApi.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }


    //---------------------------行驶证上传---------------------------------------
    private PhotoPickSheet ocrPickView;
    private byte[] imgBytes;
    private static String extension;
    private final int OCR_IMPORT_PHOTO = 51;
    private final int OCR_TAKE_PHOTO = 52;
    private boolean doLicenseOcr;
    private MyAsynTask ocrTask;

    private void showOcrPickView() {
        if (null == ocrPickView) {
            ocrPickView = new PhotoPickSheet(this);
            ocrPickView.setOnOperateListener(new PhotoPickSheet.OnOperateListener() {
                @Override
                public void onTakePhoto() {
                    if (doLicenseOcr) {//行驶证识别
                        uploadPhoto.takePhotoAction(OCR_TAKE_PHOTO);
                    }
                    ocrPickView.dismiss();
                }

                @Override
                public void onPickPhoto() {
                    if (doLicenseOcr) { //行驶证识别
                        uploadPhoto.pickAlbumAction(OCR_IMPORT_PHOTO);
                    }
                    ocrPickView.dismiss();
                }

                @Override
                public void onDialogCancel() {
                }
            });
        }
        ocrPickView.show();
    }

    class MyAsynTask extends AsyncTask<Void, Void, String> {
        private LoadingDialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = LoadingDialog.showBackCancelableDialog(mContext, "正在加载请稍后!!!");
        }

        @Override
        protected String doInBackground(Void... params) {
            return startScan();
        }

        @Override
        protected void onPostExecute(String result) {
            if (loadingDialog.isShowing()) handleOcrResult(result);
            loadingDialog.dissmiss();

        }
    }

    public String startScan() {
        String xml = HttpUtil.getSendXML(OcrConstant.action, extension);
        LogUtil.d("NetClient", "行驶证提交xml=" + xml);
        String result = HttpUtil.send(xml, imgBytes);
        return result;
    }

    private void handleOcrResult(String result) {
        try {
            LogUtil.d("NetClient", "行驶证返回xml=" + result);
            DrivingLicense drivingLicense = DataParser.parseXml(result);
            if (null != drivingLicense) {
                mWebView.loadUrl("javascript:ocrScanResult(" +
                        "'" + drivingLicense.enginNo + "'," +
                        "'" + drivingLicense.vin + "'," +
                        "'" + drivingLicense.model + "'," +
                        "'" + drivingLicense.date + "'" +
                        ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("请求失败");
        }
    }

    private void openTimeSpinner(String url) {
        UrlHandlerResult urlHandlerResult = new UrlHandlerResult();
        urlHandlerResult.url = url;
        WebUrlHandler.parseParm(urlHandlerResult);
        final String tag = urlHandlerResult.paramMap.get("tag");
        long time = 0;
        try {
            String timeStr = urlHandlerResult.paramMap.get("time");
            if (!TextUtils.isEmpty(timeStr)) {
                time = Long.valueOf(timeStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (time == 0) {
            time = new Date().getTime();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view,
                                  int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                long timeInMillis = c.getTimeInMillis();
                mWebView.loadUrl("javascript:timeRsult('" + timeInMillis + "','" + tag + "')");
            }
        };
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        Dialog dialog = new DatePickerDialog(mContext,
                dateSetListener, year, month, day);
        dialog.show();
    }

    public class JavaInterface {
        @JavascriptInterface
        public void setContactInfo(String json) {
            /**
             * nativeApp.setContactInfo(json);
             * {"name":"姓名","idcard":"身份证号","phone":"手机号"}
             */
            PushEvent pushEvent = new PushEvent(Constant.IntentAction.CONTACTINFO);
            pushEvent.setData(json);
            EventBus.getDefault().post(pushEvent);
            close();
        }
    }

    //--------------------------------上传文件-------------------------
    private PhotoPickSheet filePicker;
    private String fileCallback;
    private String fileBox;

    private void showFilePicker(String callback, String box) {
        this.fileCallback = callback;
        this.fileBox = box;
        if (null == filePicker) {
            filePicker = new PhotoPickSheet(this);
            filePicker.setReqSize(700);
        }
        filePicker.show();
    }

    /**
     * 上传图片
     *
     * @param img
     */
    private void doUploadFile(String img) {
        String forwardUrl = "javascript:" + fileCallback + "('" + img + "','" + fileBox + "')";
        System.out.println("forwardUrl=" + forwardUrl);
        mWebView.loadUrl(forwardUrl);
        //作废，直接传给H5 base64
//        try {
//            if (TextUtils.isEmpty(img)) return;
//
//            if (!NetworkUtil.isNetworkAvailable(mContext)) {
//                ToastUtil.show(R.string.net_noconnection);
//                return;
//            }
//            final LoadingDialog uploadDialog = LoadingDialog.showCancelableDialog(mContext, "图片上传中");
//            uploadTask = AgentServiceClient.doUploadFile(img, new ICallback() {
//                @Override
//                public void start() {
//
//                }
//
//                @Override
//                public void success(Object data) {
//                    UploadImgReponse response = (UploadImgReponse) data;
//                    if (response.isSuccessful() && !response.getImgUrls().isEmpty()) {
//                        String imgUrl = response.getImgUrls().get(0);
//                        String forwardUrl = "javascript:" + fileCallback + "('" + imgUrl + "','" + fileBox + "')";
//                        System.out.println("forwardUrl=" + forwardUrl);
//                        webView.loadUrl(forwardUrl);
//                    }
//                }
//
//                @Override
//                public void failure(NetException e) {
//                    ToastUtil.show("图片上传失败");
//                }
//
//                @Override
//                public void end() {
//                    uploadDialog.dissmiss();
//                }
//            });
//
//
//        } catch (Exception e) {
//            ToastUtil.show("获取图片失败");
//        }
    }
}
