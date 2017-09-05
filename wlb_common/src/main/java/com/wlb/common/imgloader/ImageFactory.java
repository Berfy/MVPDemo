package com.wlb.common.imgloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

public class ImageFactory {
	private static ImageTool mUniversalLoader;
	private static ImageTool mVolleyLoader;

	public interface ImageTool {
		public void getImg(String url, ImageView imageView, ImageLoadingListener listener);

		public void getImg(String url, ImageView imageView);

		public void getImg(String url, ImageView imageView, ImageLoadingListener listener, DisplayImageOptions options);

		public void displayLocalImage(String url, ImageView imageView);

		public void displayNoCacheLocalImage(String url, ImageView imageView);

		public void displayLocalImage(String url, ImageView imageView, int defaultRes);

		public void displayLocalImage(String url, ImageView imageView, ImageLoadingListener listener);

		public void displayLocalImage(String url, ImageView imageView, ImageLoadingListener listener, int defaultRes);

		public void displayLocalImage(String url, ImageView imageView, DisplayImageOptions options);

		public void displayLocalImage(String url, ImageView imageView, ImageLoadingListener listener,
				DisplayImageOptions options);

		public void loadImage(String url, ImageLoadingListener listener);

		public Bitmap getDiskCacheBitmap(String url);

		public void loadImage(String url, ImageLoadingListener listener, DisplayImageOptions options);

		public File getDiskCacheFile(String url);

		public Bitmap getMemoryCacheFile(String url);

		public void stop();
	}

	public static ImageTool getUniversalImpl() {
		if (null == mUniversalLoader) {
			mUniversalLoader = new ImageLoaderImpl();
		}
		return mUniversalLoader;
	}
	public static DisplayImageOptions getImageOptions(int defaultLogoRes) {
		return new DisplayImageOptions.Builder()
				.showImageForEmptyUri(defaultLogoRes)//
				.showImageOnFail(defaultLogoRes)//
				.showImageOnLoading(defaultLogoRes)//
				.cacheOnDisk(true)//
				.cacheInMemory(true)//
				.decodingOptions(ImageLoaderImpl.getBitmapOptions())//
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
}
