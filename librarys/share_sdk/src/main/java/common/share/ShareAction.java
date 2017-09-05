package common.share;

import android.app.Activity;

import java.io.Serializable;

public class ShareAction implements Serializable {
	private static final long serialVersionUID = 1387064341414850672L;
	private String appName;
	private SharePlatform mPlatform;
	private String mTitle;
	private String mText;
	private String mTargetUrl;
	private ShareImage mShareImage;
	private CShareListener mCallBack;
	private boolean onlyImage;

	public ShareAction setAppName(String appName) {
		this.appName = appName;
		return this;
	}

	public ShareAction setPlatform(SharePlatform platform) {
		this.mPlatform = platform;
		return this;
	}

	public ShareAction setCallback(CShareListener callBack) {
		this.mCallBack = callBack;
		return this;
	}

	public ShareAction setTitle(String title) {
		this.mTitle = title;
		return this;
	}

	public ShareAction setText(String text) {
		this.mText = text;
		return this;
	}

	public ShareAction setTargetUrl(String targetUrl) {
		this.mTargetUrl = targetUrl;
		return this;
	}

	public ShareAction setMedia(ShareImage shareImage) {
		this.mShareImage = shareImage;
		return this;
	}

	public ShareAction onlyImage() {
		this.onlyImage = true;
		return this;
	}

	public void share(Activity ctx) {
		// CommonShareAct.start(ctx, this);
		ShareApi.getInstance().share(ctx, this);
	}

	public void release(){
		mCallBack=null;
	}

	// ----------------------------------------------

	public SharePlatform getPlatform() {
		return mPlatform;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getText() {
		return mText;
	}

	public String getTargetUrl() {
		return mTargetUrl;
	}

	public ShareImage getShareImage() {
		return mShareImage;
	}

	public CShareListener getCallBack() {
		return mCallBack;
	}

	public boolean isOnlyImage() {
		return onlyImage;
	}

	public String getAppName() {
		return appName;
	}

}
