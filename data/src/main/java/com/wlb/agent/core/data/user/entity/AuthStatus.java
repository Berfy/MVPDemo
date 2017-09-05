package com.wlb.agent.core.data.user.entity;

/**
 * 认证状态
 * 张全
 */

public enum  AuthStatus {
    /**
     * 未认证
     */
    AUTH_NOT(0),
    /**
     * 认证中
     */
    AUTHING(1),
    /**
     * 认证失败
     */
    AUTH_FAIL(2),
    /**
     * 认证成功
     */
    AUTH_SUCCESS(3);
    public int statusCode;
    private AuthStatus(int status){
        this.statusCode=status;
    }
    public static AuthStatus getAuthStatus(int status){
        AuthStatus[] values = AuthStatus.values();
        for(AuthStatus item:values){
            if(item.statusCode==status){
                return item;
            }
        }
        return null;
    }
}
