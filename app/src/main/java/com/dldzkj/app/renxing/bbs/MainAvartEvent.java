package com.dldzkj.app.renxing.bbs;

/**
 * Created by Administrator on 2015/6/26.
 * 用于发帖页面传递消息
 */
public class MainAvartEvent {
    private String path;
    /********用于选择照片传递头像路径*********/
    public MainAvartEvent(String path) {
        // TODO Auto-generated constructor stub
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
