package com.android.util.image.blur;

import android.graphics.Bitmap;

/**
 * Created by QIUJUER on 2014/4/19.
 */
public class ImageBlur {
	static {
		System.loadLibrary("JNI_ImageBlur");
	}

	public static native void blurIntArray(int[] pImg, int w, int h, int r);

	/**
	 * 模糊
	 * 
	 * @param bitmap
	 * @param r
	 *            模糊程度
	 */
	public static native void blurBitMap(Bitmap bitmap, int r);

}
