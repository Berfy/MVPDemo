package com.wlb.agent.ui.main.frag;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.util.LContext;
import com.android.util.device.DeviceUtil;
import com.android.util.device.TimeUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.image.ImageUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.BuildConfig;
import com.wlb.agent.core.data.DataConfig;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.entity.BannerEntity;
import com.wlb.agent.core.data.agentservice.response.BannerResponse;
import com.wlb.agent.core.data.agentservice.response.MessageResponse;
import com.wlb.agent.core.data.agentservice.response.VersionResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.common.view.AlertDialogView;
import com.wlb.agent.ui.common.view.BadgeView;
import com.wlb.agent.ui.common.view.CommonPopupWindow;
import com.wlb.agent.ui.user.frag.UserLoginFrag;
import com.wlb.agent.ui.user.frag.UserNotifyFrag;
import com.wlb.agent.ui.user.helper.BackEventListener;
import com.wlb.agent.util.FileUtils;
import com.wlb.agent.util.GpsUtil;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.agent.util.UrlHandlerResult;
import com.wlb.agent.util.WebUrlHandler;
import com.wlb.agent.util.ZipUtils;
import com.wlb.common.BaseFragment;
import com.wlb.common.imgloader.ImageFactory;
import com.wlb.common.imgloader.ImageLoaderImpl;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.CommonWebView;
import common.widget.LoadingBar;
import common.widget.dialog.EffectDialogBuilder;
import common.widget.scaleable.ScaleableImageView;
import common.widget.webview.AutoWebView;
import component.update.AppDownloadCallBack;
import component.update.AppH5Version;
import component.update.AppH5VersionDB;
import component.update.Downloader;
import rx.functions.Action1;

/**
 * 首页
 *
 * @author 张全(2.8.2老版本)
 */
public class HomePageFrag extends BaseFragment implements OnClickListener, BackEventListener {

    private Task unReadMsgTask, popBannerTask;
    private final String TAG = "HomePageFrag";
    @BindView(R.id.home_title_layout)
    LinearLayout mLlTitle;
    @BindView(R.id.title_left_button)
    ScaleableImageView mBtnLeft;
    @BindView(R.id.title_right_button)
    ScaleableImageView mBtnRight;
    @BindView(R.id.loadingBar)
    LoadingBar mLoadingBar;
    @BindView(R.id.layout_left)
    LinearLayout mLlLeft;
    @BindView(R.id.layout_web)
    LinearLayout mLlWeb;
    @BindView(R.id.layout_margin)
    LinearLayout mLlMargin;
    @BindView(R.id.auto_webview)
    AutoWebView mAutoWebView;

    private WebView mWebView;
    private BadgeView mNoticeUnReadNum;
    private CommonPopupWindow popWindow;
    private BannerEntity adEntity;
    private RxPermissions mRxPermissions;

    private PopupWindowUtil mPopupWindowUtil;

    private GpsUtil mGpsUtil;

    private String H5_DIR = "";
    private String ZIP_DIR = "";

    @Override
    protected int getLayoutId() {
        return R.layout.homepage_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        initData();
        mGpsUtil = new GpsUtil(mContext);
        mGpsUtil.setListener(new GpsUtil.OnGpsListener() {
            @Override
            public void onReceiveLocation(double lat, double lng, String province, String city, String distrct, String address) {
                UserClient.saveGPSCity(city);
            }

            @Override
            public void onError() {

            }
        });
        mGpsUtil.startGps();
    }

    @OnClick({R.id.loadingBar, R.id.title_left_button, R.id.title_right_button})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_button://消息
                UserResponse info = UserClient.getLoginedUser();
                if (null != info) {
                    UserNotifyFrag.start(mContext);
                } else {
                    UserLoginFrag.start(mContext, UserLoginFrag.LoginTargetPage.MESSAGE, null);
                }
                break;
            case R.id.title_right_button:
                mPopupWindowUtil.showHotLine();
                break;
            case R.id.loadingBar:
                if (v == mLoadingBar && mLoadingBar.canLoading()) {
                    if (!NetworkUtil.isNetworkAvailable(mContext)) {
                        ToastUtil.show(R.string.net_noconnection);
                        return;
                    }
                    loadWebViewUrl();
                }
                break;
            case R.id.homePopImg:
                popWindow.dismiss();
                if (TextUtils.isEmpty(adEntity.webUrl)) return;
                UrlHandlerResult urlHandlerResult = WebUrlHandler.handleUrlLink(mContext, adEntity.webUrl);
                if (!urlHandlerResult.handled) {
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url = adEntity.webUrl;
                    WebViewFrag.start(mContext, webViewParam);
                }
                break;
            case R.id.homePopClose:
                popWindow.dismiss();
                break;
        }
    }

    private void initView() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mBtnLeft.setBackgroundResource(R.drawable.ic_user_msg);
//            mBtnRight.setBackgroundResource(R.drawable.phone_white);
//        } else {
//            mBtnLeft.setBackgroundResource(R.drawable.msg_icon_red);
//            mBtnRight.setBackgroundResource(R.drawable.service_phone);
//        }
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mNoticeUnReadNum = new BadgeView(mContext);
        mNoticeUnReadNum.setTargetView(mLlLeft);
        mNoticeUnReadNum.setTextColor(Color.WHITE);
        mNoticeUnReadNum.setBackground(10, Color.RED);
        mNoticeUnReadNum.setBadgeMargin(0, 5, 5, 0);

        H5_DIR = getContext().getFilesDir() + "/wlb/h5/";
        ZIP_DIR = getContext().getFilesDir() + "/wlb/zip/";

        addAction(Constant.IntentAction.MSG_READ);
//       addAction(Constant.IntentAction.USER_LOGIN);
        mWebView = mAutoWebView.getWebView();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.supportZoom();
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//设置不缓存
        mWebView.clearCache(true);

        mWebView.setWebChromeClient(new DWebChromeClient());
        mWebView.setWebViewClient(new DWebViewClient());
//        webView.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");

        mAutoWebView.setOnTouchScrollListener(new CommonWebView.OnTouchScrollListener() {
            @Override
            public void scroll(float y) {
                if (y / DeviceUtil.dip2px(mContext, 50) <= 1) {
                    mLlTitle.setVisibility(View.VISIBLE);
                    mLlTitle.setAlpha(1 - y / DeviceUtil.dip2px(mContext, 50));
                } else {
                    mLlTitle.setVisibility(View.GONE);
                }
            }
        });
        mRxPermissions = new RxPermissions(getActivity());
        loadWebViewUrl();
    }

    public void checkUpdateAndGetH5Data() {
        //检查本地是否存在h5 zip压缩包  存在则直接提示解压缩覆盖，无需下载，因为上次用户拒绝了，允许覆盖后会删除zip
        if (FileUtils.exists(ZIP_DIR + "h5.zip")) {
            unZipH5();
        } else {
            //检查H5版本更新
            AgentServiceClient.doCheckUpdate(new ICallback() {
                @Override
                public void start() {

                }

                @Override
                public void success(Object data) {
                    VersionResponse response = (VersionResponse) data;
                    AppH5Version appH5Version = new AppH5Version();
                    appH5Version.url = response.url;
                    appH5Version.description = response.description;
                    appH5Version.versionCode = response.versionCode;
                    AppH5Version dbVersion = AppH5VersionDB.getVersion();
                    LogUtil.i(TAG, "版本检查" + dbVersion.versionCode);
                    if (dbVersion.versionCode < response.versionCode || !FileUtils.exists(H5_DIR + "wlb3.0/wlb_html")) {//下载 //版本不需要更新的情况下，判断下文件夹是否存在，不存在也下载
                        AlertDialogView dialogView = new AlertDialogView(mContext)
                                .setTitle("提示")
                                .setContent("HTML页面有更新，是否下载")//
                                .setRightBtn("确定", new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //下载接口
                                        downLoadH5(appH5Version);
                                    }
                                })
                                .setLeftBtn("取消");

                        new EffectDialogBuilder(mContext)
                                .setCancelable(false)
                                .setCancelableOnTouchOutside(false)
                                .setContentView(dialogView).show();
                    }
                }

                @Override
                public void failure(NetException e) {

                }

                @Override
                public void end() {

                }
            });
        }
    }

    private void downLoadH5(AppH5Version appH5Version) {
        mRxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            //下载H5更新包zip
//                            Constant.EXECUTOR.execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    FileUtils.download(appH5Version.url, ZIP_DIR, "h5.zip");
//                                }
//                            });
                            Downloader downloader = new Downloader();
                            downloader.addCallBack(new AppDownloadCallBack() {
                                @Override
                                public void downloadStart() {
                                    LogUtil.i(TAG, "开始下载");
                                }

                                @Override
                                public void downloadError(String errorMsg) {
                                    LogUtil.i(TAG, "下载错误");
                                }

                                @Override
                                public void downloadProgress(long downloadSize, long totalSize, int percent) {
                                    LogUtil.i(TAG, "下载中" + percent);
                                }

                                @Override
                                public void downloadSuccess() {
                                    LogUtil.i(TAG, "下载成功" + FileUtils.getFileSize(ZIP_DIR + "h5.zip"));
                                    //完成后提示
                                    //解压缩
                                    unZipH5();
                                }
                            });
                            downloader.start(appH5Version);
                        } else {
                            ToastUtil.show(R.string.permission_failed);
                        }
                    }
                });
    }

    private void unZipH5() {
        AlertDialogView dialogView = new AlertDialogView(mContext)
                .setTitle("提示")
                .setContent("HTML页面下载完毕，是否覆盖并刷新当前页面")
                .setRightBtn("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRxPermissions
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                                .subscribe(new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean granted) {
                                        if (granted) {
                                            Constant.EXECUTOR.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LogUtil.e(TAG, "测试压缩目录" + FileUtils.createFolder(H5_DIR));
                                                    LogUtil.e(TAG, "测试压缩  zip目录" + FileUtils.createFolder(ZIP_DIR));
//                                    LogUtil.e(TAG, "测试解压缩压缩目录" + FileUtils.createFolder(getContext().getFilesDir() + "/wlb/unzip/"));
                                                    LogUtil.e(TAG, "压缩文件");
                                                    long time = System.currentTimeMillis();
                                                    FileUtils.copyAssetFileOrDir(mContext, "wlb3.0", H5_DIR);
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastUtil.showLong("复制时间" + TimeUtil.timeFormat((System.currentTimeMillis() - time), "mm:ss"));
                                                        }
                                                    });
                                                    List<File> files = new ArrayList<>();
                                                    files.add(new File(H5_DIR));
                                                    try {
//                                        LogUtil.e(TAG, "压缩文件");
                                                        File zip = new File(ZIP_DIR + "demo.zip");
                                                        ZipUtils.zipFiles(files, zip);

                                                        LogUtil.e("压缩文件位置", zip.getAbsolutePath());
                                                        LogUtil.e(TAG, "解压缩文件");
                                                        ZipUtils.upZipFile(zip, H5_DIR);
                                                        //解压缩后删除压缩包
                                                        FileUtils.deleteFile(ZIP_DIR + "h5.zip");
                                                        //配置H5 config
                                                        updateH5Config();
                                                        mHandler.sendEmptyMessageDelayed(0, 0);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } else {
                                            ToastUtil.show(R.string.permission_failed);
                                        }
                                    }
                                });
                    }
                })
                .setLeftBtn("取消");

        new EffectDialogBuilder(mContext)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .setContentView(dialogView).show();
    }

    private void updateH5Config() {
        String fileContent = FileUtils.getFileContent(H5_DIR + "wlb3.0/wlb_html/config/server.js");
        LogUtil.i("原始文件内容" + fileContent);
        String[] contentSplit = fileContent.split(";");
        for (String line : contentSplit) {
            if (line.indexOf("WEB_ADDR") > -1) {
                LogUtil.i("WEB_ADDR内容" + line);
                String[] addrSplit = line.split("=");
                if (addrSplit.length == 2) {
                    fileContent = fileContent.replace(addrSplit[1],
                            " 'file://" + H5_DIR + "wlb3.0/wlb_html/" + "'");
                }
                LogUtil.i("WEB_ADDR修改后文件内容" + fileContent);
            } else if (line.indexOf("API_ADDR") > -1 && !line.startsWith("//")) {
                LogUtil.i("API_ADDR内容" + line);
                String[] addrSplit = line.split("=");
                if (addrSplit.length == 2) {
                    fileContent = fileContent.replace(addrSplit[1],
                            " '" + BuildConfig.apiHost + "'");
                }
                LogUtil.i("API_ADDR修改后文件内容" + fileContent);
            }
        }
        LogUtil.i("保存文件" + FileUtils.saveFileContent(H5_DIR + "wlb3.0/wlb_html/config/server.js", fileContent));
        LogUtil.i("验证文件" + H5_DIR + "wlb3.0/wlb_html/config/server.js");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    loadWebViewUrl();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        doGetUnreadMsgCount();
    }

    private void initData() {
        doGetPopBanner();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (containsAction(action)) {
            doGetUnreadMsgCount();
        }
    }

    private void loadWebViewUrl() {
        if (null == mWebView) {
            return;
        }
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            startPage();
        } else {
            mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.NOCONNECTION);
        }
    }

    private void startPage() {
//        mRxPermissions
//                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//                .subscribe(new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean granted) {
//                        if (granted) {
//                            if (FileUtils.exists(H5_DIR + "wlb3.0/wlb_html/")) {//本地目录存在
//                                DataConfig.h5Host = "file://" + H5_DIR + "wlb3.0/";
//                            } else {//服务器
//                                DataConfig.h5Host = BuildConfig.h5Host;
//                            }
//                        } else {
//                            ToastUtil.show(R.string.permission_failed);
//                        }
//                    }
//                });
        DataConfig.h5Host = BuildConfig.h5Host;
        mWebView.clearHistory();
        mWebView.clearCache(true);
        H5.reset();
        String url = H5.checkUrl(H5.HOMEPAGE);
        mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.START);
        LogUtil.e("第一次请求H5" + url);
        mWebView.loadUrl(url);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != unReadMsgTask) {
            unReadMsgTask.stop();
        }
        if (null != popBannerTask) {
            popBannerTask.stop();
        }
        releaseWebView();
        mGpsUtil.stopGps();
    }

    private boolean isGetMsgCount;

    /**
     * 未读消息
     */

    private void doGetUnreadMsgCount() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isGetMsgCount) {
            return;
        }
        isGetMsgCount = true;
        unReadMsgTask = AgentServiceClient.doGetUnreadMessageCount(new SimpleCallback() {
            @Override
            public void success(Object data) {
                MessageResponse response = (MessageResponse) data;
                if (response.isSuccessful()) {
                    int unreadMessageCount = response.tipCount;
                    if (unreadMessageCount > 0) {
                        mNoticeUnReadNum.setVisibility(View.VISIBLE);
                        mNoticeUnReadNum.setBadgeCount(unreadMessageCount);
                    } else {
                        mNoticeUnReadNum.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void end() {
                isGetMsgCount = false;
            }
        });

    }

    /**
     * 分享
     */
    @Override
    public boolean handleBackEvent() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }


    /**
     * 辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
     */
    private final class DWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
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
            return true;
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
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
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            LogUtil.i(TAG, "标题" + title);
            if (title.startsWith("404") || title.startsWith("502")) {
                LogUtil.i(TAG, "onReceivedError");
                loadStatus = LOAD_ERROR;
                mWebView.setVisibility(View.INVISIBLE);
                //显示上层的错误页面
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.NOCONNECTION);
                } else {
                    mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD);
                }
            }
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            LogUtil.i(TAG, "加载进度" + newProgress);
        }
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
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.i(TAG, "  加载地址  " + url);
            LogUtil.i(TAG, "onPageStarted");
            super.onPageStarted(view, url, favicon);
            loadStatus = LOAD_START;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            LogUtil.i(TAG, "onPageFinished");
            super.onPageFinished(view, url);
            if (loadStatus != LOAD_ERROR) {
                LogUtil.e(TAG, (mWebView.getVisibility() == View.VISIBLE) + "");
                if (mWebView.getVisibility() != View.VISIBLE) {
                    LogUtil.e(TAG, "可以显示");
                    mWebView.setVisibility(View.VISIBLE);
                }
                mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.SUCCESS);
                loadStatus = LOAD_FINISHED;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            LogUtil.i(TAG, "onReceivedError");
            loadStatus = LOAD_ERROR;
            mWebView.setVisibility(View.INVISIBLE);
            //显示上层的错误页面
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.NOCONNECTION);
            } else {
                mLoadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            // super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return urlResult(url);
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
    }

    private boolean urlResult(String url) {
        UrlHandlerResult handleResult = WebUrlHandler.handleUrlLink(mContext, url);
        LogUtil.i(TAG, "  urlResult加载地址  " + url + "  " + handleResult.encryptData);
        //加密回调
        if (!TextUtils.isEmpty(handleResult.encryptData)) {
            mWebView.loadUrl("javascript:desEncryptRsult('" + handleResult.encryptData + "')");
            return true;
        }
        if (url.startsWith(WebUrlHandler.HOT_LINE)) {
            mPopupWindowUtil.showHotLine();
            return true;
        }
        LogUtil.e("是否拦截地址", handleResult.handled + "");
        return handleResult.handled;
    }

    class JavaScriptInterface {
        public void getContentHeight(String value) {
            if (value != null) {
                LogUtil.d(TAG, "页面高度" + Integer.parseInt(value));
            }
        }
    }

    private void releaseWebView() {
        try {
            if (null != mWebView) {
                mWebView.stopLoading();
                mWebView.removeAllViews();
                mWebView.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isGetBanner;

    private void doGetPopBanner() {
        if (isGetBanner) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            return;
        }
        popBannerTask = AgentServiceClient.doGetBanner(0, new SimpleCallback() {
            @Override
            public void start() {
                isGetBanner = true;
            }

            @Override
            public void success(Object data) {
                BannerResponse response = (BannerResponse) data;
                if (response.isSuccessful() && !response.getList().isEmpty()) {
                    adEntity = response.getList().get(0);
                    if (!TextUtils.isEmpty(adEntity.thumbImage)) {
                        initShowPop();
                    }
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isGetBanner = false;
            }
        });
    }

    private void initShowPop() {
        popWindow = new CommonPopupWindow(mContext, R.layout.homepage_ad,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView imgPop = (ImageView) popWindow.findViewById(R.id.homePopImg);
        popWindow.findViewById(R.id.homePopClose).setOnClickListener(this);
        imgPop.setOnClickListener(this);
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//                .showImageForEmptyUri(R.drawable.inno_img_def)//
//                .showImageOnFail(R.drawable.inno_img_def)//
//                .showImageOnLoading(R.drawable.inno_img_def)//
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .decodingOptions(ImageLoaderImpl.getBitmapOptions())
                .preProcessor(new BitmapProcessor() {

                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        Bitmap scaleBitmap = ImageUtil.scaleBySW(
                                LContext.getContext(), bitmap);
                        if (scaleBitmap != bitmap && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                        return scaleBitmap;
                    }
                })
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageFactory.getUniversalImpl().getImg(adEntity.thumbImage,
                imgPop, null, imageOptions);
        popWindow.showAtLocation(popWindow.getContentView(), Gravity.BOTTOM, 0, 0);
    }
}
