package com.wlb.agent.core.data.user.entity;

/**
 * Created by Berfy on 2017/7/25.
 * 积分
 */
public class Score {

    private String id;//积分id
    private String title;
    private String content;
    private String time;//时间
    private int type;//0扣除 1增加
    private int score;//积分

    public Score(String id, String title, String content, String time, int type, int score) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.type = type;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
