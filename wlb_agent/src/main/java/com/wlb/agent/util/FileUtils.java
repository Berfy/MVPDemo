package com.wlb.agent.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.android.util.log.LogUtil;
import com.wlb.agent.Constant;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 创建文件 删除文件 下载文件
 *
 * @author yuepengfei
 */
public class FileUtils {

    private static final String TAG = "FileUtils";
    private static final String FD_FLASH = "-ext";
    private static long filesize = 0;
    public static boolean mBl_stop = false;

    /**
     * 下载文件
     *
     * @param loadUrl
     * @param sdcardPath
     * @param saveName
     * @return
     */
    public static boolean download(String loadUrl, String sdcardPath,
                                   String saveName) {
        try {
            LogUtil.i(TAG, "下载" + loadUrl);
            URL url = new URL(loadUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(sdcardPath + saveName);
            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                LogUtil.i(TAG, "下载中" + count);
                output.write(data, 0, count);
                output.flush();
            }
            output.close();
            input.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将bitmap存为字节
     *
     * @param character 品质 1-100
     */
    public static byte[] bitmap2Bytes(Bitmap bm, int character) {
        if (bm != null) {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, character, bas);
            byte[] byteArray = bas.toByteArray();
            try {
                bas.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return byteArray;
        }
        return null;
    }

    public static byte[] fileToBytes(String path) {
        try {
            File file = new File(path);
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存照相图片到系统默认相册
     *
     * @param path   :图片名称
     * @param bitmap :图片对象
     * @return
     */
    public static String saveBitmapToFile(String path, Bitmap bitmap) {
        try {
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                // 将图片保存到系统默认相册
                File myFile = new File(path);
                if (!myFile.exists()) {
                    myFile.createNewFile();
                }
                FileOutputStream fOut = null;
                fOut = new FileOutputStream(myFile);
                int jiema = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, jiema, fOut);
                fOut.flush();
                fOut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * @param context
     * @param dir
     * @param url
     * @return
     */

    // dir为路径如�?aa/bb）url为下载地�?
    public static String getFilePath(Context context, String dir, String url) {
        String filename = (url.substring((url.lastIndexOf("/") + 1)));
        if (!dir.startsWith("/", dir.length() - 1)) {
            dir = dir + "/";
        }
        return getFilePath(context, dir + filename);
    }

    // derectory为想用存储的完整自定义路径如(/aa/bb/cc.apk)
    // 此方法可以判断是存储在sdk下还是data/data/包名
    public static String getFilePath(Context context, String derectory) {
        // if (TextUtils.isEmpty(derectory)) {
        // return "";
        // }

        if (derectory.startsWith("/")) {
            derectory = derectory.substring(derectory.indexOf("/") + 1);
        }

        String path = "";
        if (avaiableMedia()) {
            if (isHasFlashMemory()) {
                path = Environment.getExternalStorageDirectory().getPath()
                        + FD_FLASH + "/" + derectory;
            } else {
                path = Environment.getExternalStorageDirectory().getPath()
                        + "/" + derectory;
            }
        } else {
            if (null != context) {
                path = context.getCacheDir().getPath() + "/" + derectory;
            }
        }
        return path;
    }

    /**
     * 判断位置可用空间
     *
     * @param path
     * @return
     */
    public static long getAvaiableSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long blockCount = stat.getBlockCount();
        return blockCount * blockSize;
    }

    /**
     * 获取文件夹大小 包含里面文件
     */
    public static long getDirSize(String dir) {
        filesize = 0;
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                getDirFileSize(file2, filesize);
            }
        } else if (file.isFile()) {
            filesize = getFileSize(dir);
        }
        return filesize;
    }

    private static void getDirFileSize(File dir, long size) {
        File[] files = dir.listFiles();
        for (File file2 : files) {
            if (file2.isFile()) {
                filesize += getFileSize(file2.getAbsolutePath());
                LogUtil.e("文件大小",
                        "==========>" + getFileSize(file2.getAbsolutePath()));
            } else if (file2.isDirectory()) {
                getDirFileSize(file2, size);
            }
        }
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    /**
     * @return
     */
    public static boolean avaiableMedia() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return
     */
    private static boolean isHasFlashMemory() {
        String path = Environment.getExternalStorageDirectory().getPath()
                + FD_FLASH;
        File file = new File(path);
        if (file.exists()) {
            if (getAvaiableSize(path) == 0) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    // 创建文件
    public static boolean createFile(File file) {
        LogUtil.i("创建文件", "===========>" + file.getPath());
        try {
            if (file.exists()) {
                return true;
            } else {
                if (file.getParentFile().isDirectory()) {
                    file.getParentFile().mkdirs();
                }
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }

            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 创建文件(根目录缓存)
     *
     * @param context
     * @param fileName
     * @return
     */
    public static File createFile(Context context, String fileName) {
        try {

            if (TextUtils.isEmpty(fileName)) {
                fileName = System.currentTimeMillis() + ".jpg";
            }

            File targetFile = new File(context.getExternalCacheDir(), fileName);

            if (!targetFile.createNewFile()) {
                return null;
            } else {
                return targetFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 只创建文�?
    public static boolean createFolder(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.exists()) {
                return true;
            } else {
                File parentFile = file.getParentFile();
                LogUtil.d("目录地址", "========>" + parentFile);
                if (!parentFile.exists()) {
                    LogUtil.d("目录", "========>" + parentFile);
                    parentFile.mkdirs();
                }
                file.mkdir();
            }
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 只创建文�?
    public static boolean createFolder(File file) {
        try {
            if (file.exists()) {
                return true;
            } else {
                File parentFile = file.getParentFile();
                LogUtil.d("目录地址", "========>" + parentFile);
                if (!parentFile.exists()) {
                    LogUtil.d("目录", "========>" + parentFile);
                    parentFile.mkdirs();
                }
                file.mkdir();
            }
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 创建文件并写入字节
    public static boolean createFile(File file, byte[] buffer) {
        if (createFile(file)) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                fos.write(buffer);
                fos.flush();
                fos.close();
                buffer = null;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 文件存储 创建文件并写入流
     */
    public static boolean createFile(File file, InputStream inputStream) {
        if (createFile(file)) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                byte buffer[] = new byte[1024];
                while (true) {
                    int stream = inputStream.read(buffer);
                    if (stream == -1) {
                        break;
                    }
                    fos.write(buffer, 0, stream);
                }
                fos.flush();
                fos.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文件存在
     */
    public static boolean exists(String imagePath) {
        if (imagePath != null && !imagePath.equals("")) {
            File file = new File(imagePath);
            boolean isExist = exists(file);
            LogUtil.i(TAG, "文件是否存在  " + imagePath + "  " + isExist);
            return isExist;
        }
        LogUtil.i(TAG, "文件不存在  " + imagePath);
        return false;
    }

    /**
     * 判断文件存在
     */
    public static boolean exists(File file) {
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static byte[] streamToBytes(FileInputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (Exception e) {
        } catch (OutOfMemoryError e2) {
            System.gc();
        }
        return os.toByteArray();
    }

    /**
     * 删除指定文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        try {
            if (file.exists())
                return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件�?
     *
     * @param path
     * @return
     */
    public static void deleteAllFile(String path) {
        LogUtil.d("删除文件", "=========>" + path);
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                deleteAllFile(path + "/" + tempList[i]);
                deleteFolder(path + "/" + tempList[i]);
            }
        }
    }

    /**
     * 只搜索根目录
     *
     * @param searchPath 要搜索的目录
     * @param searchKey  查询关键�?搜索包含关键字的文件)
     */
    public static ArrayList<File> searchFile(Context context,
                                             String searchPath, String searchKey) {
        ArrayList<File> files = new ArrayList<File>();
        File dirFile = new File(searchPath);
        if (null != dirFile.list()) {
            for (String path : dirFile.list()) {
                File file = new File(FileUtils.getFilePath(context, path));
                if (!file.isDirectory() && file.getPath().contains(searchKey)) {
                    files.add(file);
                }
            }
        }
        return files;

    }

    public static void deleteFolder(String folderPath) {
        try {
            deleteAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 通知系统sd卡有新文件，请刷新 扫描指定文件，刷新后系统可识别出来，包括相册
     *
     * @param path 文件地址
     */
    public static void notifyFile(Context context, String path) {
        Uri localUri = Uri.fromFile(new File(path));
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                localUri);
        context.sendBroadcast(localIntent);
    }

    public static String getSportJson(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("sports.json");
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputReader.close();
            inputStream.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static File createTmpFile(Context context) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 已挂载
            File pic = Environment.getExternalStorageDirectory();//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_" + timeStamp + "";
            File tmpFile = new File(pic, fileName + ".jpg");
            return tmpFile;
        } else {
            File cacheDir = context.getCacheDir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_" + timeStamp + "";
            File tmpFile = new File(cacheDir, fileName + ".jpg");
            return tmpFile;
        }

    }

    /**
     * 读取文件（非目录）
     *
     * @param filePath 要读取的源文件
     * @return
     */
    public static String getFileContent(String filePath) {
        try {
            InputStream streamFrom = new FileInputStream(filePath);
            byte buffer[] = new byte[102];
            int len;
            StringBuffer sb = new StringBuffer();
            while ((len = streamFrom.read(buffer)) > 0) {
                sb.append(new String(buffer, "UTF-8"));
            }
            streamFrom.close();
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 读取文件（非目录）
     *
     * @param filePath 要读取的源文件
     * @return
     */
    public static boolean saveFileContent(String filePath, String content) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            return createFile(file, content.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 复制文件目录
     *
     * @param srcDir  要复制的源目录 eg:/mnt/sdcard/DB
     * @param destDir 复制到的目标目录 eg:/mnt/sdcard/db/
     * @return
     */
    public static boolean copyDir(String srcDir, String destDir) {
        File sourceDir = new File(srcDir);
        //判断文件目录是否存在
        if (!sourceDir.exists()) {
            return false;
        }
        //判断是否是目录
        if (sourceDir.isDirectory()) {
            File[] fileList = sourceDir.listFiles();
            File targetDir = new File(destDir);
            //创建目标目录
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            //遍历要复制该目录下的全部文件
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {//如果如果是子目录进行递归
                    copyDir(fileList[i].getPath() + "/",
                            destDir + fileList[i].getName() + "/");
                } else {//如果是文件则进行文件拷贝
                    copyFile(fileList[i].getPath(), destDir + fileList[i].getName());
                }
            }
            return true;
        } else {
            copyFileToDir(srcDir, destDir);
            return true;
        }
    }


    /**
     * 复制文件（非目录）
     *
     * @param srcFile  要复制的源文件
     * @param destFile 复制到的目标文件
     * @return
     */
    private static boolean copyFile(String srcFile, String destFile) {
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * 把文件拷贝到某一目录下
     *
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean copyFileToDir(String srcFile, String destDir) {
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String destFile = destDir + "/" + new File(srcFile).getName();
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * 移动文件目录到某一路径下
     *
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean moveFile(String srcFile, String destDir) {
        //复制后删除原目录
        if (copyDir(srcFile, destDir)) {
            deleteFile(new File(srcFile));
            return true;
        }
        return false;
    }

    /**
     * 删除文件（包括目录）
     *
     * @param delFile
     */
    public static void deleteFile(File delFile) {
        //如果是目录递归删除
        if (delFile.isDirectory()) {
            File[] files = delFile.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
        } else {
            delFile.delete();
        }
        //如果不执行下面这句，目录下所有文件都删除了，但是还剩下子目录空文件夹
        delFile.delete();
    }

    //path - asset下文件（夹）名称  destinationPath - 目的路径
    public static void copyAssetFileOrDir(Context context, String path, String destinationPath) {
        AssetManager assetManager = context.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() " + path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                Constant.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        copyAssetFile(context, path, destinationPath);
                    }
                });
            } else {
                String fullPath = destinationPath + path;
                Log.i("tag", "path=" + fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir " + fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";
                    if (!path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                        copyAssetFileOrDir(context, p + assets[i], destinationPath);
                }
            }
        } catch (
                IOException ex)

        {
            Log.e("tag", "I/O Exception", ex);
        }

    }

    public static void copyAssetFile(Context context, String filename, String destinationPath) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() " + filename);
            in = assetManager.open(filename);
            newFileName = destinationPath + filename;
            out = new FileOutputStream(newFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of " + newFileName);
            Log.e("tag", "Exception in copyFile() " + e.toString());
        }
    }

}
