package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 第三方绑定信息
 * Created by JiaHongYa
 */
public class ThirdParty implements Serializable {
    /**
     * 第三方账号类型
     */
    public int type;
    /**
     * 是否绑定
     */
    private int is_band;
    public boolean isBind(){
        return is_band==1;
    }
    public void setBind(boolean isBind){
        is_band=isBind?1:0;
    }
}
