package com.wlb.common.imgloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wlb.common.R;
import com.wlb.common.imgloader.ImageFactory.ImageTool;

import java.io.File;

public class ImageLoaderImpl implements ImageTool {

	public static DisplayImageOptions cacheDiskOptions;// 分别保存到内存和硬盘
	public static DisplayImageOptions cacheMemoryOptions;// 保存到内存中
	public static DisplayImageOptions options;// 分别保存到内存和硬盘中,但下载失败没做默认图片处理
	static {
		BitmapFactory.Options opt = getBitmapOptions();

		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).decodingOptions(opt).bitmapConfig(Bitmap.Config.RGB_565).build();

		cacheDiskOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.inno_img_def).showImageOnFail(R.drawable.inno_img_def).showImageOnLoading(R.drawable.inno_img_def)
				.cacheOnDisk(true).cacheInMemory(true).build();

		cacheMemoryOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.inno_img_def).showImageOnFail(R.drawable.inno_img_def).showImageOnLoading(R.drawable.inno_img_def)
				.cacheInMemory(true).decodingOptions(opt).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public static BitmapFactory.Options getBitmapOptions() {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		return opt;
	}

	@Override
	public void getImg(String url, ImageView imageView, ImageLoadingListener listener) {
		ImageLoader.getInstance().displayImage(url, imageView, cacheDiskOptions, listener);
	}

	@Override
	public void getImg(String url, ImageView imageView) {
		ImageLoader.getInstance().displayImage(url, imageView, cacheDiskOptions);
	}

	@Override
	public void displayLocalImage(String url, ImageView imageView) {
		ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(url), imageView, cacheDiskOptions);
	}

	@Override
	public void displayLocalImage(String url, ImageView imageView, ImageLoadingListener listener) {
		ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(url), imageView, cacheDiskOptions, listener);
	}

	@Override
	public void displayNoCacheLocalImage(String url, ImageView imageView) {
		ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(url), imageView, cacheMemoryOptions);
	}

	@Override
	public void getImg(String url, ImageView imageView, ImageLoadingListener listener, DisplayImageOptions options) {
		ImageLoader.getInstance().displayImage(url, imageView, options, listener);
	}

	@Override
	public void displayLocalImage(String url, ImageView imageView, ImageLoadingListener listener, int defaultRes) {
		DisplayImageOptions cacheDiskOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(defaultRes).showImageOnFail(R.drawable.download_fail).showImageOnLoading(defaultRes)
				.cacheInMemory(true).decodingOptions(getBitmapOptions()).bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(url), imageView, cacheDiskOptions, listener);
	}

	@Override
	public void displayLocalImage(String url, ImageView imageView, int defaultRes) {
		DisplayImageOptions cacheDiskOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(defaultRes).showImageOnFail(R.drawable.download_fail).showImageOnLoading(defaultRes)
				.cacheInMemory(true).decodingOptions(getBitmapOptions()).bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(url), imageView, cacheDiskOptions);
	}

	@Override
	public void loadImage(String url, ImageLoadingListener listener) {
		ImageLoader.getInstance().loadImage(url, listener);

	}

	@Override
	public File getDiskCacheFile(String url) {
		return ImageLoader.getInstance().getDiskCache().get(url);
	}

	@Override
	public Bitmap getDiskCacheBitmap(String url) {
		File cacheFile = ImageLoader.getInstance().getDiskCache().get(url);
		if (null != cacheFile) {
			return BitmapFactory.decodeFile(url);
		}
		return null;
	}

	@Override
	public void loadImage(String url, ImageLoadingListener listener,
			DisplayImageOptions options) {
		ImageLoader.getInstance().loadImage(url, options, listener);
	}

	@Override
	public Bitmap getMemoryCacheFile(String url) {
		return ImageLoader.getInstance().getMemoryCache().get(url);

	}

	@Override
	public void displayLocalImage(String url, ImageView imageView,
			DisplayImageOptions options) {
		ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(url), imageView, options);
	}

	@Override
	public void stop() {
		ImageLoader.getInstance().stop();
	}

	@Override
	public void displayLocalImage(String url, ImageView imageView,
			ImageLoadingListener listener, DisplayImageOptions options) {
		ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(url), imageView, options, listener);
	}
}
