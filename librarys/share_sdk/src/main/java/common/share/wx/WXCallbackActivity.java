package common.share.wx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import common.share.PlatformConfig;
import common.share.PlatformConfig.Weixin;
import common.share.ShareApi;
import common.share.SharePlatform;

public abstract class WXCallbackActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;
	private Weixin weixin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("---------onCreate");
		weixin = (Weixin) PlatformConfig.getPlatform(SharePlatform.WEIXIN);
		api = WXAPIFactory.createWXAPI(this, weixin.appId, false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		System.out.println("---------onNewIntent");
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		System.out.println("---------onReq");
	}

	@Override
	public void onResp(BaseResp resp) {
		WXShareHelper weixin = (WXShareHelper) ShareApi.getInstance().getSharePlatform();
		if (resp instanceof SendAuth.Resp) { // 授权
			SendAuth.Resp authResp = (SendAuth.Resp) resp;
			weixin.auth(authResp);
		} else { // 分享
			weixin.handleResponse(resp);
		}
		finish();
	}
}
