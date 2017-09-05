package com.wlb.agent.ui.common;

public class PushEvent {
    private String action;
    private Object data;

    public PushEvent(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
    public void setData(Object data){
        this.data=data;
    }
    public Object getData(){
        return this.data;
    }
}