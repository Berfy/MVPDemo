package component.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * APP下载服务
 *
 * @author 张全
 */
public class AppDownloadService extends Service {
    private Builder mBuilder;
    private NotificationManager mNotifyManager;
    // 通知id
    private int notifyApkId, notifyH5Id;
    // 开始下载
    public static final String ACTION_DOWNLOAD_APK_START = "ACTION_DOWNLOAD_START";
    // 清除通知
    public static final String ACTION_CLEAR_NOTIFICATION = "ACTION_CLEAR_NOTIFICATION";
    // 检测版本升级
    public static final String ACTION_CHECK_VERSION = "ACTION_CHECK_VERSION";
    // 下载最新H5
    public static final String ACTION_DOWNLOAD_H5_START = "ACTION_DOWNLOAD_H5_START";

    @Override
    public void onCreate() {
        super.onCreate();
        mBuilder = new Builder(this);
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (null == intent || TextUtils.isEmpty(action))
            return Service.START_NOT_STICKY;

        if (ACTION_DOWNLOAD_APK_START.equals(action)) {
            // 下载
            startDownloadApk();
        } else if (ACTION_CHECK_VERSION.equals(action)) {
            AppDownloadClient.doCheckVersion(null);
        } else if (ACTION_CLEAR_NOTIFICATION.equals(action)) {
            // 清除通知
            mNotifyManager.cancelAll();
            stopSelf();
        } else if (ACTION_DOWNLOAD_H5_START.equals(action)) {
            // 下载
            startDownloadH5();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotifyManager.cancelAll();
    }

    private boolean isDownloading;

    private void startDownloadApk() {
        AppVersion version = AppDownloadClient.getFromDB();
        notifyApkId = version.downloadUrl.hashCode();
        if (!AppDownloadUtil.isNetworkAvailable(this)) {
            mNotifyManager.cancelAll();
            notifyStart();
            notifyError(notifyApkId);
            return;
        }
        if (null == version) {
            mNotifyManager.cancelAll();
            stopSelf();
            return;
        }
        if (isDownloading) {
            return;
        }
        isDownloading = true;
        AppDownloadClient.getInstance().downloadApk(version,
                new AppDownloadCallBack() {

                    @Override
                    public void downloadStart() {
                        notifyStart();
                    }

                    @Override
                    public void downloadSuccess() {
                        isDownloading = false;
                        notifyFinish(notifyApkId);
                        Toast.makeText(getBaseContext(), "下载成功,正在准备安装...",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void downloadProgress(long downloadSize,
                                                 long totalSize, int percent) {
                        notifyProgress(notifyApkId, percent);
                    }

                    @Override
                    public void downloadError(String errorMsg) {
                        isDownloading = false;
                        notifyError(notifyApkId);
                        Toast.makeText(getBaseContext(), "下载失败,请点击通知栏重试...",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void startDownloadH5() {
        AppVersion version = AppDownloadClient.getFromDB();
        notifyH5Id = version.downloadUrl.hashCode();
        if (!AppDownloadUtil.isNetworkAvailable(this)) {
            mNotifyManager.cancelAll();
            notifyStart();
            notifyError(notifyH5Id);
            return;
        }
        if (null == version) {
            mNotifyManager.cancelAll();
            stopSelf();
            return;
        }
        if (isDownloading) {
            return;
        }
        isDownloading = true;
        AppDownloadClient.getInstance().downloadApk(version,
                new AppDownloadCallBack() {

                    @Override
                    public void downloadStart() {
                        notifyStart();
                    }

                    @Override
                    public void downloadSuccess() {
                        isDownloading = false;
                        notifyFinish(notifyH5Id);
                        Toast.makeText(getBaseContext(), "下载成功,正在准备安装...",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void downloadProgress(long downloadSize,
                                                 long totalSize, int percent) {
                        notifyProgress(notifyH5Id, percent);
                    }

                    @Override
                    public void downloadError(String errorMsg) {
                        isDownloading = false;
                        notifyError(notifyH5Id);
                        Toast.makeText(getBaseContext(), "下载失败,请点击通知栏重试...",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }

    // ###########################

    private void notifyStart() {
        AppVersionConfiguration configuration = AppDownloadClient.getConfiguration();
        mBuilder.setContentTitle(configuration.appName).setContentText("正在下载新版本")
                .setSmallIcon(configuration.appIcon);
    }

    /**
     * 刷新下载进度
     *
     * @param url
     * @param progress
     * @param total
     */
    private int showProgress;

    private void notifyProgress(int notifyId, int progress) {
        if (showProgress == progress) {
            return;
        }
        showProgress = progress;
        // 部分手机Notification没有 contentIntent会报错
        Intent localIntent = new Intent(this, getClass());
        PendingIntent localPendingIntent = PendingIntent.getService(this, 0,
                localIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentText("正在下载新版本 " + progress + "%");
        mBuilder.setProgress(100, progress, false);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notification.contentIntent = localPendingIntent;
        mNotifyManager.notify(notifyId, notification);
    }

    /**
     * 下载完毕
     */
    private void notifyFinish(int notifyId) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(AppDownloadClient.getUpdateFile()),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);
        boolean successful = AppDownloadClient.installAPK();
        if (!successful) {
            mNotifyManager.cancel(notifyId);
            return;
        }

        Notification notification = mBuilder.setContentText("下载完毕点击安装")
                .setProgress(0, 0, false).build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.contentIntent = PendingIntent.getActivity(this, 0, intent,
                0);
        mNotifyManager.notify(notifyId, notification);
    }

    /**
     * 下载错误
     */
    private void notifyError(int notifyId) {
        Intent localIntent = new Intent(this, getClass());
        localIntent.setAction(ACTION_DOWNLOAD_APK_START);

        Notification notification = mBuilder.setContentText("下载失败,点击重试")
                .setProgress(0, 0, false).build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.contentIntent = PendingIntent.getService(this, 0,
                localIntent, 0);
        mNotifyManager.notify(notifyId, notification);

    }
}
