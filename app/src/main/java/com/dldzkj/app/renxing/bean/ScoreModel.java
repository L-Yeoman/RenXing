package com.dldzkj.app.renxing.bean;

public class ScoreModel {

    /**
     * operateitem : 注册
     * OpID : 1
     * ID : 2
     * UserName : 18530938140
     * PointNum : 10
     * AddTime : 2015-07-11 14:54:27
     */
    private String operateitem;
    private String OpID;
    private String ID;
    private String UserName;
    private String PointNum;
    private String AddTime;

    public void setOperateitem(String operateitem) {
        this.operateitem = operateitem;
    }

    public void setOpID(String OpID) {
        this.OpID = OpID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setPointNum(String PointNum) {
        this.PointNum = PointNum;
    }

    public void setAddTime(String AddTime) {
        this.AddTime = AddTime;
    }

    public String getOperateitem() {
        return operateitem;
    }

    public String getOpID() {
        return OpID;
    }

    public String getID() {
        return ID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPointNum() {
        return PointNum;
    }

    public String getAddTime() {
        return AddTime;
    }
}
