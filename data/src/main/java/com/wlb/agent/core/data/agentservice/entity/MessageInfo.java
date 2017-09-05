package com.wlb.agent.core.data.agentservice.entity;

import java.io.Serializable;

/**
 * @author 张全
 */
public class MessageInfo implements Serializable{
    public long msgId;
    /**
     * 1：佣金；2：保单；3：公告
     */
    public int msgType;
    /**
     * 是否已读
     */
    private int isRead;
    /**
     * 消息标题
     */
    public String title;
    /**
     * 消息详情
     */
    public String summary;
    /**
     * 消息链接，（公告打开articleUrl）
     */
    public String articleUrl;
    /**
     * 保单类消息使用articleId做跳转到详情
     */
    public long articleId;
    /**
     * 时间戳
     */
    public long timestamp;

    @Override
    public String toString() {
        return "MessageInfo{" +
                "msgId=" + msgId +
                ", msgType=" + msgType +
                ", isRead=" + isRead +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", articleUrl='" + articleUrl + '\'' +
                ", articleId=" + articleId +
                ", timestamp=" + timestamp +
                '}';
    }

    public boolean isReaded() {
        return isRead==1;
    }
    public void setReaded(boolean isReaded){
        this.isRead=isReaded?1:0;
    }
}
