package com.wlb.agent.ui.common;

import android.content.Context;

/**
 * @author 张全
 */

public interface BaseView {
    /**
     * 关闭页面
     */
    void close();

    /**
     * 获取Context
     * @return
     */
    Context getContext();
}
