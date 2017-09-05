package component.update;

import java.io.Serializable;

/**
 * APP升级版本信息
 * 
 * @author 张全
 */
public class AppVersion implements Serializable, Cloneable {
	private static final long serialVersionUID = -375235818163750548L;
	/**
	 * versionCode
	 */
	public int versionCode;
	/**
	 * versionName
	 */
	public String versionName;
	/**
	 * 升级描述信息
	 */
	public String desc;
	/**
	 * 下载路径
	 */
	public String downloadUrl;
	/**
	 * 已下载大小
	 */
	public long downloadSize;
	/**
	 * 下载包大小
	 */
	public long totalSize;

	@Override
	protected AppVersion clone() throws CloneNotSupportedException {
		return (AppVersion) super.clone();
	}

	@Override
	public String toString() {
		return "UmengVersion [versionCode=" + versionCode + ", versionName=" + versionName + ", desc=" + desc
				+ ", downloadUrl=" + downloadUrl + ", downloadSize=" + downloadSize + ", totalSize=" + totalSize + "]";
	}

}
