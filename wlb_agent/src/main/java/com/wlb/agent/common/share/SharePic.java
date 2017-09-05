
package com.wlb.agent.common.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;

import com.android.util.LContext;
import com.android.util.log.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 分享图片
 * <ul>
 * <ol>1、将bitmap保存为图片</ol>
 * <ol>2、压缩图片</ol>
 * </ul>
 *
 *
 * @author 张全
 */
public class SharePic {
    private static String SD_DIR = "share";
    /**
     * 手机截屏
     *
     * @return 截图
     */
    public static Bitmap shot(Activity ctx) {
        try {
            View view = ctx.getWindow().getDecorView();
            DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
            view.layout(0, 0, dm.widthPixels, dm.heightPixels);
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * View截图
     */
    public static Bitmap shotView(View shotView) {
        try {
            shotView.setDrawingCacheEnabled(true);
            shotView.destroyDrawingCache();
            Bitmap bmp = Bitmap.createBitmap(shotView.getDrawingCache());
            shotView.setDrawingCacheEnabled(false);
            return bmp;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除图片文件
     */
    public void deletePicFile(File file) {
        if (null != file && file.exists())
            file.delete();
    }

    public void deletePicFiles(ArrayList<File> fileLists) {
        if (null == fileLists || fileLists.isEmpty())
            return;
        for (File file : fileLists) {
            deletePicFile(file);
        }
    }

    private File getShareFile(String fileName) {
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory(), SD_DIR);
            if (!dir.exists())
                dir.mkdirs();
            file = new File(dir, fileName);
        } else {
            file = new File( LContext.getContext().getFilesDir(), fileName);
        }
        return file;
    }

    private static void delShareImg(File file) {
        if (null == file)
            return;
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (null != listFiles && listFiles.length > 0) {
                for (File item : listFiles) {
                    delShareImg(item);
                    item.delete();
                }
            }
        } else
            file.delete();
    }

    /**
     * 将bitmap储存到图片文件中
     *
     * @param bitmap 储存的图片
     */
    private File savePicToFile(Bitmap bitmap, int index) {
        OutputStream outStream = null;
        try {
            String fileName = "sharepic_" + System.currentTimeMillis()+".jpg";
            File file = getShareFile(fileName);

            // 重新分享时 删除上一次的分享图片
            if (index == 0) {
                delShareImg(file.getParentFile());
            }

            outStream = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, outStream);
            outStream.close();

            // 压缩图片 
            long kb = file.length() / 1024;
            LogUtil.d("SharePic..图片大小为 " + kb);
            final int maxSize = 400;
            int be = 1;
            if (kb > maxSize) {
                be = Math.round(kb * 1.0f / maxSize);
            }
            if (be != 1) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = be;
                LogUtil.d("SharePic..压缩图片  inSampleSize=" + be);
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

                //重新生成文件
                outStream = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 100, outStream);
                outStream.close();
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public List<String> files(List<Bitmap> bitmapList) {
        if (null == bitmapList || bitmapList.isEmpty())
            return null;
        List<String> fileList = new ArrayList<String>();
        int size = bitmapList.size();
        for (int i = 0; i < size; i++) {
            Bitmap bitmap = bitmapList.get(i);
            if (null != bitmap) {
                File picFile = savePicToFile(bitmap, i);
                if (null != picFile)
                    fileList.add(picFile.getAbsolutePath());
            }

        }
        return fileList;
    }

}
