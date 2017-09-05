package common.share;


public interface CShareListener {
	void onResult(SharePlatform platform);

	void onError(SharePlatform platform, Throwable e);

	void onCancel(SharePlatform platform);
}