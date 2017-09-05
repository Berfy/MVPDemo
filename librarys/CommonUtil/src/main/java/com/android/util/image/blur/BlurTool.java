package com.android.util.image.blur;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.format.Formatter;

import com.android.util.LContext;
import com.android.util.encode.MD5;
import com.android.util.log.LogUtil;

/**
 * 高斯模糊图片工具
 * 
 * @author zhangquan
 */
public class BlurTool {

	public static String blurDir = "img_blur";// 高斯模糊图片保存文件夹
	private static final int BLUR_RADIUS = 20; // 模糊程度 值越大，图片越模糊
	static {
		initBlurImgDir();
	}

	/**
	 * 获取高斯模糊图片文件夹
	 * 
	 * @return
	 */
	private static File initBlurImgDir() {
		File dir = new File(blurDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 高斯模糊图片
	 * 
	 * @param imgs
	 *            用户保存的图片路径
	 */
	public static synchronized void blurImgs(List<String> imgs) {
		if (null == imgs || imgs.isEmpty())
			return;
		final List<String> lockImgs = new ArrayList<String>(imgs);
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (String oneImg : lockImgs) {
					blur(oneImg);
				}
			}
		}).start();
	}

	public static synchronized Bitmap blurBitmap(Bitmap bitmap) {
		Bitmap src = bitmap.copy(bitmap.getConfig(), true);
		Bitmap blurBitmap = null;
		int scale = getBitmapScale(src);
		if (scale > 1) {
			Bitmap scaleBitmap = scaleBitmap(src, scale);
			if (null == scaleBitmap)
				return null;
			blurBitmap = BlurOperater.fastblur(LContext.getContext(),
					scaleBitmap, BLUR_RADIUS);
			recycleBitmap(scaleBitmap);
		} else {
			blurBitmap = BlurOperater.fastblur(LContext.getContext(), src,
					BLUR_RADIUS);
			recycleBitmap(src);
		}
		return blurBitmap;
	}

	public static Bitmap getBlurBitmap(String path) {
		if (null == path) {
			return null;
		}
		String fileName = MD5.MD5Encode(path);
		File file = new File(blurDir, fileName);
		if (null != file && file.exists()) {
			LogUtil.d("存在BlurBitmap.......file.size="
					+ Formatter.formatFileSize(LContext.getContext(),
							file.length()));

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			float size = (float) file.length() / (float) 1024;
			if (size > 100) {
				options.inSampleSize = Math.round((size / 100.0f));
			}
			return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		}
		return null;
	}

	public static File getBlurFile(String filePath) {
		String fileName = MD5.MD5Encode(filePath);
		return new File(blurDir, fileName);
	}

	private static synchronized void blur(String path) {
		if (path == null) {
			return;
		}
		try {
			Context ctx = LContext.getContext();
			if (null == ctx)
				return;
			File file = null;
			Bitmap src;
			String fileName = MD5.MD5Encode(path);
			file = new File(blurDir, fileName);
			if (file.exists()) {
				return;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			options.inJustDecodeBounds = false;
			int heightRatio = (int) Math.ceil(options.outHeight / (float) 1000);
			int widthRatio = (int) Math.ceil(options.outWidth / (float) 800);
			options.inSampleSize = 1;
			LogUtil.d("[outWidth=" + options.outWidth + ",widthRatio="
					+ widthRatio + ";outHeight=" + options.outHeight
					+ ",heightRatio=" + heightRatio + "]");
			if (heightRatio > 1 && widthRatio > 1) {
				options.inSampleSize = heightRatio > widthRatio ? heightRatio
						: widthRatio;
			}

			src = BitmapFactory.decodeFile(path, options);
			// }

			Bitmap blurBitmap = null;
			int scale = getBitmapScale(src);
			if (scale > 1) {
				// blurBitmap = scaleBitmap(src, scale);
				// ImageBlur.blurBitMap(blurBitmap, BLUR_RADIUS);

				Bitmap scaleBitmap = scaleBitmap(src, scale);
				if (null == scaleBitmap)
					return;
				blurBitmap = BlurOperater.fastblur(LContext.getContext(),
						scaleBitmap, BLUR_RADIUS);
				recycleBitmap(scaleBitmap);

			} else {
				// blurBitmap = src.copy(src.getConfig(), true);
				// src=null;
				// ImageBlur.blurBitMap(blurBitmap, BLUR_RADIUS+5);

				blurBitmap = BlurOperater.fastblur(LContext.getContext(), src,
						BLUR_RADIUS);
				recycleBitmap(src);
			}

			if (null != blurBitmap) {
				boolean successful = saveToLocal(file, blurBitmap);
				recycleBitmap(blurBitmap);
				LogUtil.d("blur" + (successful ? "成功" : "失败")
						+ "   blurBitmap=" + blurBitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 保存到本地
	 * 
	 * @param file
	 * @param bitmap
	 * @return
	 */
	private static boolean saveToLocal(File file, Bitmap bitmap) {
		FileOutputStream fos = null;
		try {
			file.delete();
			file.createNewFile();
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (null != file)
				file.delete();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	private static int getBitmapScale(Bitmap bitmap) {
		float size = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			size = bitmap.getByteCount();
		}
		size = bitmap.getRowBytes() * bitmap.getHeight();

		int scale = (int) Math.round((size / (float) (3 * 1000 * 1000)));
		return scale;
	}

	private static Bitmap scaleBitmap(Bitmap src, int scale) {
		Bitmap blurBitmap = null;
		// copy
		File tempFile = null;
		try {
			File filesDir = LContext.getContext().getFilesDir();
			tempFile = new File(filesDir, "temp");
			boolean successful = saveToLocal(tempFile, src);
			recycleBitmap(src);
			if (successful) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inDither = true;
				options.inSampleSize = scale;
				options.inJustDecodeBounds = false;
				blurBitmap = BitmapFactory.decodeFile(
						tempFile.getAbsolutePath(), options);
			}
		} finally {
			if (null != tempFile) {
				tempFile.delete();
			}
		}
		return blurBitmap;
	}

	private static void recycleBitmap(Bitmap bitmap) {
		try {
			if (null != bitmap && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bitmap = null;
		}
	}

}
