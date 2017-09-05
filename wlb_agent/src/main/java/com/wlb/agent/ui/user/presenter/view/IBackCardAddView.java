package com.wlb.agent.ui.user.presenter.view;

import com.wlb.agent.ui.common.BaseView;

/**
 * 添加银行卡
 * @author 张全
 */
public interface IBackCardAddView  extends BaseView{

    /**
     * 添加失败显示的浮层
     * @param errorMsg
     */
     void showErrorBar(String errorMsg) ;
}
