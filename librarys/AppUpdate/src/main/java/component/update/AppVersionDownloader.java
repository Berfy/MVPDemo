package component.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 版本下载线程
 *
 * @author 张全
 */
public class AppVersionDownloader extends Handler implements Runnable {
    private AppVersion version;
    private List<AppDownloadCallBack> callBacks = Collections
            .synchronizedList(new ArrayList<AppDownloadCallBack>());
    private final int connectTimeOut = 6 * 1000;// 连接超时
    private final int readTimeOut = 8 * 1000;// 读取超时
    private final int msg_error = 2;
    private final int msg_success = 3;
    private final int msg_progress = 4;
    private boolean isPaused;

    private boolean isDownloading;
    private final static String TAG = AppVersionDownloader.class
            .getSimpleName();

    public AppVersionDownloader() {
        super(Looper.getMainLooper());
    }

    public void start() {
        isPaused = false;
        isDownloading = true;
        new Thread(this).start();
    }

    public void start(AppVersion version) {
        isPaused = false;
        isDownloading = true;
        this.version = version;
        new Thread(this).start();
    }

    public void stop() {
        isPaused = true;
        isDownloading = false;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                for (AppDownloadCallBack callBack : callBacks) {
                    if (null != callBack) {
                        callBack.downloadError("下载失败");
                    }
                }
                callBacks.clear();
            }
        }, 100);
    }

    public boolean isDownloading() {
        return this.isDownloading;
    }

    public void setVersion(AppVersion version) {
        this.version = version;
    }

    public AppVersion getVersion() {
        return this.version;
    }

    public void addCallBack(AppDownloadCallBack callBack) {
        if (!callBacks.contains(callBack)) {
            callBack.downloadStart();
            callBacks.add(callBack);
        }
    }

    public void removeCallback(AppDownloadCallBack callBack) {
        callBacks.remove(callBack);
    }

    @Override
    public void handleMessage(Message msg) {
        if (isPaused) {
            return;
        }
        switch (msg.what) {
            // case msg_start:
            // for (DownloadCallBack callBack : callBacks) {
            // if (null != callBack) {
            // callBack.downloadStart();
            // }
            // }
            // break;
            case msg_error:// 下载失败
                isDownloading = false;
                AppDownloadUtil.d(TAG, "DownloadTask... 下载失败");
                for (AppDownloadCallBack callBack : callBacks) {
                    if (null != callBack) {
                        callBack.downloadError(null != msg.obj ? (String) msg.obj
                                : "下载失败，请检测网络");
                    }
                }
                callBacks.clear();
                break;
            case msg_success:// 下载成功
                isDownloading = false;
                AppDownloadUtil.d(TAG, "DownloadTask... 下载完成");
                for (AppDownloadCallBack callBack : callBacks) {
                    if (null != callBack) {
                        callBack.downloadSuccess();
                    }
                }
                callBacks.clear();
                break;
            case msg_progress:// 下载中
                for (AppDownloadCallBack callBack : callBacks) {
                    if (null != callBack) {
                        callBack.downloadProgress(version.downloadSize,
                                version.totalSize, (Integer) (msg.obj));
                    }
                }
                break;
        }
    }

    private OutputStream getOutputStream(long breakPoint) throws Exception {
        OutputStream fos = null;
        File updateFile = AppDownloadClient.getUpdateFile();
        if (breakPoint == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fos = new FileOutputStream(updateFile);
            } else {
                fos = AppDownloadClient.getContext().openFileOutput(
                        updateFile.getName(),
                        Context.MODE_WORLD_WRITEABLE
                                | Context.MODE_WORLD_READABLE);
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fos = new FileOutputStream(updateFile, true);
            } else {
                fos = AppDownloadClient.getContext().openFileOutput(
                        updateFile.getName(),
                        Context.MODE_WORLD_WRITEABLE
                                | Context.MODE_WORLD_READABLE
                                | Context.MODE_APPEND);
            }
        }
        return fos;
    }

    @SuppressLint({"WorldWriteableFiles", "WorldReadableFiles"})
    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        InputStream inputStream = null;
        DataOutputStream outputStream = null;
        try {
            // 下载路径
            String downloadUrl = version.downloadUrl;

            System.out.println(downloadUrl);
            // 获取文件大小
            if (version.totalSize <= 0) {
                long totalSize = getFileLenghtByUrl(downloadUrl);
                if (totalSize > 0) {
                    version.totalSize = totalSize;
                    AppDownloadClient.save2DB(version);
                }
            }

            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeOut);
            conn.setReadTimeout(readTimeOut);
            conn.setDoInput(true);

            File updateFile = AppDownloadClient.getUpdateFile();
            if (null == updateFile) {
                sendMessage(obtainMessage(msg_error, "无法创建下载文件"));
                return;
            }

            // 检测是否需要断点下载
            long breakPoint = updateFile.length();
            if (breakPoint > 0) {
                // 检测是否已下载完成
                if (breakPoint == version.totalSize) {
                    sendMessage(obtainMessage(msg_success));
                    return;
                }
                // 设置断点
                conn.setRequestProperty("Range", "bytes=" + (breakPoint) + "-" + (version.totalSize - 1));
                AppDownloadUtil.d(TAG, "DownloadTask...文件断点下载...fileSize=" + breakPoint
                        + "Range..........bytes=" + breakPoint + "-");
            } else {
                version.downloadSize = 0;
                AppDownloadClient.save2DB(version);
            }

            int responseCode = conn.getResponseCode();
            AppDownloadUtil.d(TAG, "DownloadTask...responseCode=" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK
                    || responseCode == HttpURLConnection.HTTP_PARTIAL) {

                inputStream = conn.getInputStream();
                outputStream = new DataOutputStream(getOutputStream(breakPoint));

                if (isPaused) {
                    return;
                }

                int lastPercent = 0;
                byte[] buffer = new byte[1024 * 10];
                int readSize = 0;
                while ((readSize = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readSize);
                    // 更新数据库
                    version.downloadSize = updateFile.length();

                    int percent = (int) (version.downloadSize * 100.0f / version.totalSize);// 下载百分比
                    if (lastPercent != percent) {
                        lastPercent = percent;
                        sendMessage(obtainMessage(msg_progress, percent));
                        AppDownloadUtil.d(TAG, "DownloadTask...percent=" + percent + ",downloadedSize="
                                + version.downloadSize + ",totalSize=" + version.totalSize);
                    }

                    if (isPaused) {
                        return;
                    }
                }

                if (updateFile.length() == version.totalSize) {
                    sendMessage(obtainMessage(msg_success));
                } else {
                    AppDownloadClient.delUpdateFile();
                    sendMessage(obtainMessage(msg_error, "下载失败"));
                }
            } else {
                sendMessage(obtainMessage(msg_error, "下载失败,请检查apk下载地址"));
                AppDownloadClient.delUpdateFile();
                AppDownloadClient.save2DB(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(obtainMessage(msg_error, "下载失败,请检测网络"));
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取下载文件大小
     *
     * @param path
     * @return
     * @throws Exception
     */
    long getFileLenghtByUrl(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                long fileLength = conn.getContentLength();
                return fileLength;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            conn.disconnect();
        }
        return 0l;
    }
}
