package com.wlb.agent.util;

import android.content.Context;
import android.text.TextUtils;

import com.android.util.encode.Base64;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.core.data.base.RequestConfig;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.order.frag.OrderFrag;
import com.wlb.agent.ui.order.pay.PayInfo;
import com.wlb.agent.ui.team.frag.TeamManagerFrag;
import com.wlb.agent.ui.team.frag.TeamOrderListFrag;
import com.wlb.agent.ui.user.frag.CarFrag;
import com.wlb.agent.ui.user.frag.UserBindPhoneFrag;
import com.wlb.agent.ui.user.frag.UserLoginFrag;
import com.wlb.agent.ui.user.frag.WalletFrag;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.Map;

public class WebUrlHandler {
    //分享
    public static final String SHARE = "wlb://share";
    //客服电话
    public static final String HOT_LINE = "wlb://servicePhone";
    //用户注册登录页面
    public static final String USER_REGIST = "wlb://user/regist";
    //我的钱包
    public static final String USER_WALLET = "wlb://user/wallet";
    //添加车辆
    public static final String CAR_ADD = "wlb://vehicle/add";
    //车辆列表
    public static final String CAR_LIST = "wlb://car/list";
    public static final String CAR_LIST2 = "wlb://vehicle/list";//wlb://vehicle/add?closePage=&licenseNo=&owner=&region=&renewVehicle=
    //关闭页面
    public static final String CLOSE_PAGE = "wlb://closePage";
    //行驶证识别
    public static final String OCR = "wlb://vehicle/ocr";
    //打开webView
    public static final String OPEN_WEBVIEW = "wlb://openPage/webView";
    //获取城市
    public static final String GAIN_CITY = "wlb://cityPicker";
    //订单列表
    public static final String ORDER_LIST = "wlb://insuranceOrder";
    //订单详情
    public static final String ORDER_DETAIL = "wlb://orderDetail";
    //下单
    public static final String ORDER_CREATE = "wlb://createInsuranceOrder";
    //阿里支付
    public static final String ALI_PAY = "wlb://aliPay";
    //微信支付
    public static final String WX_PAY = "wlb://wxinPay?prepayId=";
    //打开团队管理
    public static final String TEAM_SUMMARY = "wlb://team/summary";
    //打开团队管理-待支付
    public static final String TEAM_ORDER = "wlb://team/bePayOrder";
    //请求3DES加密
    public static final String ENCRYPT_DES = "wlb://encrypt/des";
    //日期选择
    public static final String UI_TIMESPINNER = "wlb://openUI/timeSpinner";
    //选择文件
    public static final String FILE = "wlb://uploadFile";
    //报价页面
    public static final String PAGE_OFFER = "price.html";
    //核保页面
    public static final String PAGE_OFFER_SUC = "checkSuccess.html";
    //报价页面分享点击
    public static final String OFFER_SHARE = "wlb://priceShare";
    //核保结果页-跳转订单
    public static final String OFFER_SUC_ORDER_LIST = "wlb://insuranceOrder";
    //核保结果页-跳转订单详情
    public static final String OFFER_SUC_ORDER_DETAIL = "wlb://orderDetail";

    /**
     * 处理URL跳转
     *
     * @param context
     * @param url     请求的url
     * @return
     */
    public static UrlHandlerResult handleUrlLink(Context context, String url) {
        final UrlHandlerResult urlHandlerResult = new UrlHandlerResult();
        urlHandlerResult.url = url;
        if (!url.startsWith("wlb")) {
            return urlHandlerResult;
        }
        UserResponse loginedUser = UserClient.getLoginedUser();

        //解析参数
        parseParm(urlHandlerResult);

        boolean handled = false;
        boolean bindPhone = false;
        Map<String, String> paramMap = urlHandlerResult.paramMap;
        if (paramMap.containsKey("bindPhoneFlag")) {
            String bindPhoneFlag = paramMap.get("bindPhoneFlag");
            if ("1".equals(bindPhoneFlag)) {
                bindPhone = true;
            }
        }

        if (bindPhone) { //需要綁定手機號
            if (loginedUser.isUnBind()) {
                handled = true;
                urlHandlerResult.handled = handled;
                UserBindPhoneFrag.start(context, null);
                return urlHandlerResult;
            }
        }
        urlHandlerResult.tag = paramMap.get("tag");
        if (url.startsWith(USER_REGIST)) { //用户登录
            handled = true;
            UserLoginFrag.start(context, null, null);
        } else if (url.startsWith(USER_WALLET)) {//钱包
            handled = true;
//             loginedUser = OfferClient.getLoginedUser();
//            if (loginedUser.isUnBind()) {
//                UserBindPhoneFrag.start(context,UserBindPhoneFrag.BindTargetPage.USER_WALLET);
//            } else {
            WalletFrag.start(context);
//            }
        } else if (url.startsWith(WebUrlHandler.SHARE)) {//分享
            handled = true;
            urlHandlerResult.shareInfo = handleShareUrl(url);
        } else if (url.startsWith(CLOSE_PAGE)) { //关闭页面
            handled = true;
            urlHandlerResult.closePage = true;
        } else if (url.startsWith(OPEN_WEBVIEW)) { //请求打开webview
            handled = true;
            if (!paramMap.isEmpty()) {
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.url = paramMap.get("url");
                webViewParam.title = paramMap.get("title");

                String value = paramMap.get("showTitleBar");
                boolean coverTitleBar = false;
                if (!TextUtils.isEmpty(value)) {
                    coverTitleBar = Integer.valueOf(value) == 1 ? true : false;
                }

                String showShareBtn = paramMap.get("showShareBtn");
                String shareContent = paramMap.get("shareContent");
                if (!TextUtils.isEmpty(showShareBtn) && !TextUtils.isEmpty(shareContent)) {
                    WebViewFrag.ShareInfo shareInfo = new WebViewFrag.ShareInfo();
                    shareInfo.title = webViewParam.title;
                    shareInfo.content = shareContent;
                    shareInfo.link = webViewParam.url;
                    webViewParam.shareInfo = shareInfo;
                }
                SimpleFragAct.SimpleFragParam startParam = WebViewFrag.getStartParam(webViewParam);
                startParam.coverTilteBar(coverTitleBar);
                WebViewFrag.start(context, startParam);
            }
        } else if (url.startsWith(GAIN_CITY)) { //获取城市
            handled = true;
            if (!paramMap.isEmpty()) {
                String city = paramMap.get("cityName");
                if (!TextUtils.isEmpty(city)) {
                    if (NetworkUtil.isNetworkAvailable(context)) {
                        PushEvent pushEvent = new PushEvent(Constant.IntentAction.USER_CITY);
                        pushEvent.setData(city);
                        EventBus.getDefault().post(pushEvent);
                        urlHandlerResult.closePage = true;
                    }
                }
            }
        } else if (url.startsWith(CAR_ADD)) { //添加车辆
            handled = true;
        } else if (url.startsWith(CAR_LIST) || url.startsWith(CAR_LIST2)) { //车辆列表页面
            handled = true;
//            if(loginedUser.isUnBind()){
//               UserBindPhoneFrag.start(context, UserBindPhoneFrag.BindTargetPage.CAR_LIST);
//            }else {
            CarFrag.start(context);
//            }

        } else if (url.startsWith(ALI_PAY)) { //支付宝支付
            handled = true;
            if (!paramMap.isEmpty()) {
                try {
                    PayInfo payInfo = new PayInfo();
                    payInfo.prepayId = paramMap.get("prepayId");
                    payInfo.subject = paramMap.get("subject");
                    payInfo.desc = paramMap.get("desc");
                    payInfo.price = paramMap.get("price");
                    urlHandlerResult.payInfo = payInfo;
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.show("请求失败");
                }
            }
        } else if (url.startsWith(WX_PAY)) {
            handled = true;
            if (!paramMap.isEmpty()) {
                try {
                    PayInfo payInfo = new PayInfo();
                    payInfo.prepayId = paramMap.get("prepayId");
                    payInfo.isWxPay = true;
                    urlHandlerResult.payInfo = payInfo;
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.show("请求失败");
                }
            }
        } else if (url.startsWith(ORDER_CREATE)) { //下单
            handled = true;
        } else if (url.startsWith(ORDER_LIST)) { //订单列表
            handled = true;
//            if(loginedUser.isUnBind()){
//                UserBindPhoneFrag.start(context, UserBindPhoneFrag.BindTargetPage.USER_INSURANCEORDER);
//            }else {
            OrderFrag.start(context);
//            }

        } else if (url.startsWith(ORDER_DETAIL)) { //订单详情
            handled = true;
            if (!paramMap.isEmpty()) {
                try {
                    //要修改
                    Long orderId = Long.valueOf(paramMap.get("orderId"));
//                    InsuranceOrderDetailFrag.start(context, orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (url.startsWith(OCR)) { //行驶证识别
            handled = true;
            urlHandlerResult.doLicenseOcr = true;

        } else if (url.startsWith(TEAM_SUMMARY)) { //团队管理
            handled = true;
//            if(loginedUser.isUnBind()){
//               UserBindPhoneFrag.start(context,UserBindPhoneFrag.BindTargetPage.TEAM_SUMMARY);
//            }else {
            TeamManagerFrag.start(context);
//            }
        } else if (url.startsWith(TEAM_ORDER)) { //团队管理-待支付
            handled = true;
            try {
                String member_user_id = paramMap.get("member_user_id");
                TeamOrderListFrag.start(context, member_user_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (url.startsWith(ENCRYPT_DES)) {
            handled = true;
            String data = paramMap.get("data");
            LogUtil.e("ENCRYPT_DES", "获取加密信息" + data);
            if (!TextUtils.isEmpty(data)) {
                try {
                    byte[] encrypt = RequestConfig.encrypt(data);
                    String result = Base64.encodeToString(encrypt, Base64.DEFAULT);
                    urlHandlerResult.encryptData = result;
                    LogUtil.e("ENCRYPT_DES", "获取加密信息1" + urlHandlerResult.encryptData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        urlHandlerResult.handled = handled;

        return urlHandlerResult;
    }

    /**
     * 处理分享链接URL
     *
     * @param url
     * @return
     */
    public static WebViewFrag.ShareInfo handleShareUrl(String url) {
        if (url.startsWith(WebUrlHandler.SHARE)) {
            WebViewFrag.ShareInfo shareInfo = new WebViewFrag.ShareInfo();
            try {
                String query = url.substring(url.indexOf("?") + 1);
                String[] params = query.split("&");
                final String title = "title";
                final String link = "url";
                final String content = "content";
                final String type = "type";
                for (String param : params) {
                    String[] item = param.split("=");
                    String key = item[0];
                    String value = null;
                    if (item.length > 1) {
                        value = item[1];
                    }
                    if (!TextUtils.isEmpty(value)) {
                        value = URLDecoder.decode(value);
                    }
                    if (key.equals(title)) {
                        shareInfo.title = value;
                    } else if (key.equals(link)) {
                        shareInfo.link = value;
                    } else if (key.equals(content)) {
                        shareInfo.content = value;
                    } else if (key.equals(type)) {
                        if (!TextUtils.isEmpty(value))
                            shareInfo.type = Integer.valueOf(value);
                    }
                }
                return shareInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void parseParm(UrlHandlerResult urlHandlerResult) {
        String url = urlHandlerResult.url;
        if (url.contains("?")) {
            String query = url.substring(url.indexOf("?") + 1);
            String[] params = query.split("&");
            for (String param : params) {
                String[] item = param.split("=");
                if (item.length == 1) {
                    continue;
                }
                String key = item[0];
                String value = item[1];
                if (!TextUtils.isEmpty(value)) {
                    try {
                        value = URLDecoder.decode(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (key.equalsIgnoreCase("closePage")) {
                    urlHandlerResult.handleClosePage(value);
                }
                urlHandlerResult.paramMap.put(key, value);
            }
        }
    }
}
