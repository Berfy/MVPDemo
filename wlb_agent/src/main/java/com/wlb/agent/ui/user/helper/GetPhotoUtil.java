package com.wlb.agent.ui.user.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.util.file.SDUtil;
import com.wlb.agent.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class GetPhotoUtil {
    private static final String TAG = "GetPhotoUtil";

    public static Bitmap reduceImage(String originalPath, int reqSize) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
//		o.inPurgeable = true;
//		o.inInputShareable = true;
        BitmapFactory.decodeFile(originalPath, o);
        o.inJustDecodeBounds = false;

        final int REQUIRED_SIZE = reqSize;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        Log.d(TAG, "width_src=" + width_tmp + ",height_src=" + height_tmp);

        int scale = 1;
        if (width_tmp <= 0 || height_tmp <= 0) {
            scale = 4;
        } else {
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
        }
        Log.d(TAG, "UploadPhoto ------------scale=" + scale);
        BitmapFactory.Options o2 = new BitmapFactory.Options();
//		o2.inPurgeable = true;
//		o2.inInputShareable = true;
        o2.inSampleSize = scale;
        Bitmap bitmapScaled = BitmapFactory.decodeFile(originalPath, o2);
        Log.d(TAG, "scale=" + scale + ",压缩后图片：width=" + bitmapScaled.getWidth()
                + ",height=" + bitmapScaled.getHeight());
        return bitmapScaled;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     *
     * @param uri
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap resizeBitmap(String path, int reqSize) {
        Bitmap bitmap = null;
        Log.d(TAG, "UploadPhoto ------------uriPath=" + path);
        try {
            // 获取压缩过的bitmap
            bitmap = reduceImage(path, reqSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     *
     * @param uri
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap resizeBitmap(Context context, Uri uri, int reqSize) {
        Log.d(TAG, "UploadPhoto ------------resizeBitmap,uri=" + uri);
        Bitmap bitmap = null;
        String uriPath = null;
        try {
            uriPath = UriUtil.getAbsolutePath(uri, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(uriPath)) {
            uriPath = uri.getPath();
        }
        Log.d(TAG, "UploadPhoto ------------uriPath,uriPath=" + uriPath);
        try {
            // 获取压缩过的bitmap
            bitmap = reduceImage(uriPath, reqSize);
            // if (bitmap == null) {
            // return null;
            // }
            // int degree = fixRotation(uriPath);
            // if (degree != 0) {// 旋转照片角度
            // bitmap = rotateBitmap(bitmap, degree);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 获取图片扩展名
     *
     * @param path
     * @return
     */
    public static String getExtensionByPath(String path) {
        if (path != null) {
            return path.substring(path.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static Bitmap compressImage(Context context, int reqWidth,
                                       int reqHeight, Uri uri) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri), null, options);

            int height = 0;
            int width = 0;
            if (options.outHeight > options.outWidth) {
                height = options.outHeight;
                width = options.outWidth;
            } else {
                height = options.outWidth;
                width = options.outHeight;
            }

            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {// 缩小原图
                int heightRatio = Math
                        .round((float) height / (float) reqHeight);
                int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio
                        : widthRatio;
            }
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public Bitmap scaleImg(Bitmap bmp, float scale) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        int dstWidth = (int) (bmpWidth * scale);
        int dstHeight = (int) (bmpHeight * scale);
        Bitmap resizeBmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight,
                true);
        return resizeBmp;
    }

    /**
     * 读取图片角度
     *
     * @param path
     * @return
     */
    public static int fixRotation(String path) {
        int rotate = 0;
        if (path == null) {
            return rotate;
        }
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    /**
     * 旋转图片角度
     *
     * @param bitmap
     * @param degress
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        Bitmap newBitMap = null;
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), m, true);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
                bitmap = null;
            }
            newBitMap = createBitmap;
        }
        return newBitMap;
    }

    public static boolean saveBitmap2File(Bitmap photoBitmap, File photoFile) {
        if (photoBitmap == null || !SDUtil.isSDValiable())
            return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(photoFile);
            if (photoBitmap.compress(CompressFormat.JPEG, 100, fos)) {
                fos.flush();
            }
            return true;
        } catch (Exception e) {
            photoFile.delete();
            e.printStackTrace();
        } finally {
            try {
                if (null != fos) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 按图片kb进行压缩
     *
     * @param context
     * @param uri
     * @param file
     * @return
     */
    public static Bitmap compressBySize(Context context, Uri uri, File file, int minKb) {
        InputStream inputStream = null;
        FileOutputStream fos = null;
        Bitmap bitmap = null;
        try {
            // String uriPath = UriUtil.getAbsolutePath(uri, context);
            // Bitmap bitmap = BitmapFactory.decodeFile(uriPath);
            // saveBitmap2File(bitmap, file);

            inputStream = context.getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024];
            fos = new FileOutputStream(file);
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            long kb = file.length() / 1024;
            Log.d(TAG, "原始大小=" + kb + "kb");
            int minSize = minKb;
            if (kb <= minSize) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            } else {

                BitmapFactory.Options opts = new BitmapFactory.Options();
                int be = Math.round(kb / minSize * 1.0f);
                opts.inSampleSize = be < 1 ? 1 : be;
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                Log.d(TAG, "缩放比例=" + opts.inSampleSize);

                fos = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 100, fos);
                fos.close();
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            file.delete();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Bitmap compressImage(String imageFile, int targetKB) {
        Bitmap image = BitmapFactory.decodeFile(imageFile);
        if (null == image) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > targetKB) {  //循环判断如果压缩后图片是否大于targetKB,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
