package com.dldzkj.app.renxing.bean;

/**
 * Created by Administrator on 2015/7/20.
 */
public class MyPostBBS {
    /**
     * Enabled : 1
     * PID : 21
     * UserID : 8
     * BoardID : 31
     * UserName : 张三
     * Title : %E6%88%91%E7%9A%84%E5%B8%96%E5%AD%90111
     * AddTime : 2015-07-20 13:22:46
     */
    private String Enabled;
    private String PID;
    private String UserID;
    private String BoardID;
    private String UserName;
    private String Title;
    private String AddTime;

    public void setEnabled(String Enabled) {
        this.Enabled = Enabled;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public void setBoardID(String BoardID) {
        this.BoardID = BoardID;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setAddTime(String AddTime) {
        this.AddTime = AddTime;
    }

    public String getEnabled() {
        return Enabled;
    }

    public String getPID() {
        return PID;
    }

    public String getUserID() {
        return UserID;
    }

    public String getBoardID() {
        return BoardID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getTitle() {
        return Title;
    }

    public String getAddTime() {
        return AddTime;
    }//不存数据库，只是方便接收数据

}
