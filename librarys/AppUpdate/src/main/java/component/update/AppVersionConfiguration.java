package component.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 版本下载配置 :
 * 
 * <pre class="prettyprint">
 * AppVersionConfiguration configuration = null;
 * configuration = new AppVersionConfiguration.Builder(context)
 * 		.appIcon(应用图标)
 * 		.isDebug(测试模式)
 *      .setVersionChecker(你自己的版本检测实现，默认是友盟);
 * 		.build();
 * AppDownloadClient.getInstance().init(configuration);
 * </pre>
 * 
 * @author zhangquan
 * 
 */
public class AppVersionConfiguration {
	final Context ctx;
	final String appName;
	final int appIcon;
	final long versionCode;
	final String versionName;
	final boolean isDebug;
	final IAppVersionChecker versionChecker;

	private AppVersionConfiguration(final Builder builder) {
		ctx = builder.ctx;
		appName = builder.appName;
		appIcon = builder.appIcon;
		versionCode = builder.versionCode;
		versionName = builder.versionName;
		isDebug = builder.isDebug;
		versionChecker = builder.versionChecker;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public static class Builder {
		private Context ctx;
		private String appName;
		private int appIcon;
		private long versionCode;
		private String versionName;
		private boolean isDebug;
		private IAppVersionChecker versionChecker;

		public Builder(Context context) {
			this.ctx = context.getApplicationContext();
		}

		public Builder appIcon(int appIcon) {
			this.appIcon = appIcon;
			return this;
		}

		public Builder isDebug(boolean isDebug) {
			this.isDebug = isDebug;
			return this;
		}

		public Builder setVersionChecker(IAppVersionChecker versionChecker) {
			this.versionChecker = versionChecker;
			return this;
		}

		// --------------------------------
		public AppVersionConfiguration build() {
			initEmptyFieldsWithDefaultValues();
			return new AppVersionConfiguration(this);
		}

		public void initEmptyFieldsWithDefaultValues() {
			if (null == ctx) {
				throw new NullPointerException("ctx=null");
			}
			if (null == versionChecker) {
				throw new NullPointerException("versionChecker=null");
			}
			PackageManager pm = ctx.getPackageManager();
			try {
				PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
				appName = packageInfo.applicationInfo.loadLabel(pm).toString();
				versionName = packageInfo.versionName;
				versionCode = packageInfo.versionCode;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
