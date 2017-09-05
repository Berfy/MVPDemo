package com.wlb.agent.util;

import android.text.TextUtils;

import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.order.pay.PayInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张全
 */
public class UrlHandlerResult {
    public String url;
    public boolean closePage; //是否关闭webview
    public boolean handled;//是否拦截url
    public Map<String,String> paramMap=new HashMap<>();//url 参数map
    public WebViewFrag.ShareInfo shareInfo=null;//分享信息
    public PayInfo payInfo; //支付信息
    public boolean doLicenseOcr;//行驶证识别
    public String encryptData ;//加密数据
    public String tag;//请求标识



    public void handleClosePage(String closePageStr){
        if(TextUtils.isEmpty(closePageStr)){
            closePage=false;
            return;
        }
        try {
            Integer value = Integer.valueOf(closePageStr);
            closePage=value==1?true:false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
