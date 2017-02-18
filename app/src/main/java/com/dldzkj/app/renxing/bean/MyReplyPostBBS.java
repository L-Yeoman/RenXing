package com.dldzkj.app.renxing.bean;

/**
 * Created by Administrator on 2015/7/20.
 */
public class MyReplyPostBBS {//不存入数据库，只作为接收使用

    /**
     * PContent : dHdv5pG45pG45pG4
     * PID : 21
     * ReplyTime : 2015-07-20 17:21:33
     * PTitle : %E6%88%91%E7%9A%84%E5%B8%96%E5%AD%90111
     * RID : 55
     * BoardID : 0
     * Content : 16698
     */
    private String PContent;
    private String PID;
    private String ReplyTime;
    private String PTitle;
    private String RID;
    private String BoardID;
    private String Content;

    public void setPContent(String PContent) {
        this.PContent = PContent;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public void setReplyTime(String ReplyTime) {
        this.ReplyTime = ReplyTime;
    }

    public void setPTitle(String PTitle) {
        this.PTitle = PTitle;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public void setBoardID(String BoardID) {
        this.BoardID = BoardID;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getPContent() {
        return PContent;
    }

    public String getPID() {
        return PID;
    }

    public String getReplyTime() {
        return ReplyTime;
    }

    public String getPTitle() {
        return PTitle;
    }

    public String getRID() {
        return RID;
    }

    public String getBoardID() {
        return BoardID;
    }

    public String getContent() {
        return Content;
    }
}
