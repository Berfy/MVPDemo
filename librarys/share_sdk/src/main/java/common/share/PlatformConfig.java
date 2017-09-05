package common.share;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

public class PlatformConfig {
	public static Map<SharePlatform, PlatformConfig.Platform> configs = new HashMap<SharePlatform, Platform>();
	static {
		configs.put(SharePlatform.QQ, new PlatformConfig.QQZone(SharePlatform.QQ));
		configs.put(SharePlatform.QZONE, new PlatformConfig.QQZone(SharePlatform.QZONE));
		configs.put(SharePlatform.WEIXIN, new PlatformConfig.Weixin(SharePlatform.WEIXIN));
		configs.put(SharePlatform.WEIXIN_CIRCLE, new PlatformConfig.Weixin(SharePlatform.WEIXIN_CIRCLE));
		configs.put(SharePlatform.SINA, new PlatformConfig.SinaWeibo());
	}

	public static PlatformConfig.Platform getPlatform(SharePlatform platform) {
		return (PlatformConfig.Platform) configs.get(platform);
	}

	public static void setWeixin(String appId, String appSecret) {
		// 微信
		PlatformConfig.Weixin weixin = (PlatformConfig.Weixin) configs.get(SharePlatform.WEIXIN);
		weixin.appId = appId;
		weixin.appSecret = appSecret;
		// 微信朋友圈
		PlatformConfig.Weixin weixinCircle = (PlatformConfig.Weixin) configs.get(SharePlatform.WEIXIN_CIRCLE);
		weixinCircle.appId = appId;
		weixinCircle.appSecret = appSecret;
	}

	public static void setSinaWeibo(String appKey, String appSecret, String redirectUrl) {
		PlatformConfig.SinaWeibo sinaWeibo = (PlatformConfig.SinaWeibo) configs.get(SharePlatform.SINA);
		sinaWeibo.appKey = appKey;
		sinaWeibo.appSecret = appSecret;
		sinaWeibo.redirectUrl = redirectUrl;
	}

	public static void setQQ(String appId, String appKey) {
		// QQ空间
		PlatformConfig.QQZone qZone = (PlatformConfig.QQZone) configs.get(SharePlatform.QZONE);
		qZone.appId = appId;
		qZone.appKey = appKey;
		// qq
		PlatformConfig.QQZone qq = (PlatformConfig.QQZone) configs.get(SharePlatform.QQ);
		qq.appId = appId;
		qq.appKey = appKey;
	}

	public interface Platform {
		SharePlatform getName();

		void parse(JSONObject var1);

		boolean isConfigured();

		boolean isAuthrized();
	}

	public static class Weixin implements PlatformConfig.Platform {
		private final SharePlatform media;
		public String appId = null;
		public String appSecret = null;

		public SharePlatform getName() {
			return this.media;
		}

		public Weixin(SharePlatform platform) {
			this.media = platform;
		}

		public void parse(JSONObject var1) {
		}

		public boolean isConfigured() {
			return !TextUtils.isEmpty(this.appId) && !TextUtils.isEmpty(this.appSecret);
		}

		public boolean isAuthrized() {
			return false;
		}
	}

	public static class SinaWeibo implements PlatformConfig.Platform {
		public String appKey = null;
		public String appSecret = null;
		public String redirectUrl = null;
		/**
		 * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
		 * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利 选择赋予应用的功能。
		 * 
		 * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
		 * 
		 * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
		 * 
		 * 有关哪些 OpenAPI
		 * 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI 关于 Scope
		 * 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
		 */
		public static final String SCOPE =
				"email,direct_messages_read,direct_messages_write,"
						+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
						+ "follow_app_official_microblog," + "invitation_write";

		public SinaWeibo() {
		}

		public SharePlatform getName() {
			return SharePlatform.SINA;
		}

		public void parse(JSONObject var1) {
			this.appKey = var1.optString("key");
			this.appSecret = var1.optString("secret");
		}

		public boolean isConfigured() {
			return !TextUtils.isEmpty(this.appKey) && !TextUtils.isEmpty(this.appSecret);
		}

		public boolean isAuthrized() {
			return false;
		}
	}

	public static class QQZone implements PlatformConfig.Platform {
		private final SharePlatform media;
		public String appId = null;
		public String appKey = null;

		public QQZone(SharePlatform var1) {
			this.media = var1;
		}

		public SharePlatform getName() {
			return this.media;
		}

		public void parse(JSONObject var1) {
			this.appId = var1.optString("key");
			this.appKey = var1.optString("secret");
		}

		public boolean isConfigured() {
			return !TextUtils.isEmpty(this.appId) && !TextUtils.isEmpty(this.appKey);
		}

		public boolean isAuthrized() {
			return false;
		}
	}

}
