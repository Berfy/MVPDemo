package common.share;

import android.content.Intent;

public interface ILogin {

	/**
	 * 登录
	 * 
	 * @param loginCallBack
	 */
	public void login(LoginCallBack loginCallBack);

	/**
	 * 回调
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data);

	/**
	 * 回收资源
	 */
	public void release();
}
