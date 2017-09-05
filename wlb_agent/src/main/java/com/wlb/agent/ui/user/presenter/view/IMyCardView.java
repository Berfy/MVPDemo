package com.wlb.agent.ui.user.presenter.view;

import com.wlb.agent.core.data.user.response.CardInfoResponse;
import com.wlb.agent.ui.common.BaseView;

/**
 * 我的名片
 *
 * Created by 曹泽琛.
 */

public interface IMyCardView extends BaseView {
    /**
     * 设置名片内容
     * @param cardInfo
     */
    void setCardView(CardInfoResponse cardInfo);
}
