package component.update;

import java.io.Serializable;

/**
 * APP升级版本信息
 * 
 * @author 张全
 */
public class AppH5Version implements Serializable, Cloneable {
	private static final long serialVersionUID = -375235818163750548L;
	/**
	 * versionCode
	 */
	public int versionCode;
	/**
	 * 升级描述信息
	 */
	public String description;
	/**
	 * 下载路径
	 */
	public String url;
	/**
	 * 已下载大小
	 */
	public long downloadSize;
	/**
	 * 下载包大小
	 */
	public long totalSize;

	@Override
	protected AppH5Version clone() throws CloneNotSupportedException {
		return (AppH5Version) super.clone();
	}

	@Override
	public String toString() {
		return "UmengVersion [versionCode=" + versionCode + ", desc=" + description
				+ ", downloadUrl=" + url + ", downloadSize=" + downloadSize + ", totalSize=" + totalSize + "]";
	}

}
