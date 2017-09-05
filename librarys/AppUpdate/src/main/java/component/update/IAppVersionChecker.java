package component.update;

/**
 * 版本升级检测
 * 
 * @author zhangquan
 * 
 */
public interface IAppVersionChecker {

	/**
	 * 版本检测
	 * @param updateListener 回调函数
     */
	void doVersionCheck(VersionUpdateListener updateListener);

	/**
	 * 停止版本检测
	 */
	void stopVersionCheck();
}
