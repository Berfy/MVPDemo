package component.update;

/**
 * 下载回调
 * 
 * @author 张全
 */
public interface AppDownloadCallBack {
	/**
	 * 开始下载
	 */
	void downloadStart();

	/**
	 * 下载失败
	 */
	void downloadError(String errorMsg);

	/**
	 * 下载进度
	 * 
	 * @param downloadSize
	 *            已下载长度
	 * @param totalSize
	 *            文件总长度
	 * @param percent
	 *            下载百分比
	 */
	void downloadProgress(long downloadSize, long totalSize, int percent);

	/**
	 * 下载成功
	 */
	void downloadSuccess();
}
