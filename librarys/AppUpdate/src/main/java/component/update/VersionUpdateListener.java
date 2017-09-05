package component.update;

/**
 * 版本升级检测回调
 * 
 * @author zhangquan
 * 
 */
public interface VersionUpdateListener {
	/**
	 * 有新版本
	 * 
	 * @param appVersion
	 *            新版本
	 */
	void onNewVersionReturned(AppVersion appVersion);

	/**
	 * 无新版本
	 */
	void onNoVersionReturned();

	void fail();
}
