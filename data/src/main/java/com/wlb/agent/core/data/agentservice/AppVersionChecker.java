package com.wlb.agent.core.data.agentservice;

import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.wlb.agent.core.data.DataConfig;
import com.wlb.agent.core.data.agentservice.response.VersionResponse;

import component.update.AppVersion;
import component.update.IAppVersionChecker;
import component.update.VersionUpdateListener;

/**
 * @author 张全
 */
public class AppVersionChecker implements IAppVersionChecker {

    private Task updateTask;
    private VersionUpdateListener mUpdateListener;

    @Override
    public void doVersionCheck(final VersionUpdateListener updateListener) {
        this.mUpdateListener = updateListener;
        updateTask = AgentServiceClient.doCheckUpdate(new ICallback() {
            @Override
            public void start() {

            }

            @Override
            public void success(Object data) {
                if (null == mUpdateListener) {
                    return;
                }
                VersionResponse response = (VersionResponse) data;
                if (response.isSuccessful()) {
                    if (DataConfig.versionCode < response.versionCode) {
                        AppVersion version = new AppVersion();
                        version.versionName = response.versionName;// 更新版本号
                        version.versionCode = response.versionCode;
                        version.desc = response.description;// 更新日志
                        version.downloadUrl = response.url;// 下载路径
//                    version.totalSize = Long.valueOf(updateInfo.target_size);// 下载包大小
                        if (null != mUpdateListener) {
                            mUpdateListener.onNewVersionReturned(version);
                        }
                    } else {
                        if (null != mUpdateListener) {
                            mUpdateListener.onNoVersionReturned();
                        }
                    }
                }
            }

            @Override
            public void failure(NetException e) {
                if (null != mUpdateListener) {
                    mUpdateListener.fail();
                }
            }

            @Override
            public void end() {

            }
        });
    }

    @Override
    public void stopVersionCheck() {
        mUpdateListener = null;
        if (null != updateTask) {
            updateTask.stop();
        }
    }
}
