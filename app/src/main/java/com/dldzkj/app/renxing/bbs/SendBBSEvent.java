package com.dldzkj.app.renxing.bbs;

/**
 * Created by Administrator on 2015/6/26.
 * 用于发帖页面传递消息
 */
public class SendBBSEvent {
    private int count;
    /********用于发帖*********/
    public SendBBSEvent(int  count) {
        // TODO Auto-generated constructor stub
        this.count = count;
    }
    public int getMsg(){
        return count;
    }

}
