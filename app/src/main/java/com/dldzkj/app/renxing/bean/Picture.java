package com.dldzkj.app.renxing.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/7/25.
 */
public class Picture implements Serializable{
    /**
     * TOPID : 4408
     * Img_Url : /upload/201507/18/thumb_201507181052114514.jpg
     * Type : 1
     */
    private String TOPID;
    private String Img_Url;
    private String Type;
    /**
     * ImgUrl : /Upload/201507/25/20150725143058390.jpg
     * UploadTime : 2015-07-25 14:30:58
     * ID : 783
     * ImgRemark :
     * AlbumsID : 5
     * ThumbImgUrl : /Upload/201507/25/small_20150725143058390.jpg
     */
    private String ImgUrl;
    private String UploadTime;
    private String ID;
    private String ImgRemark;
    private String AlbumsID;
    private String ThumbImgUrl;

    public void setTOPID(String TOPID) {
        this.TOPID = TOPID;
    }

    public void setImg_Url(String Img_Url) {
        this.Img_Url = Img_Url;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getTOPID() {
        return TOPID;
    }

    public String getImg_Url() {
        return Img_Url;
    }

    public String getType() {
        return Type;
    }

    public void setImgUrl(String ImgUrl) {
        this.ImgUrl = ImgUrl;
    }

    public void setUploadTime(String UploadTime) {
        this.UploadTime = UploadTime;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setImgRemark(String ImgRemark) {
        this.ImgRemark = ImgRemark;
    }

    public void setAlbumsID(String AlbumsID) {
        this.AlbumsID = AlbumsID;
    }

    public void setThumbImgUrl(String ThumbImgUrl) {
        this.ThumbImgUrl = ThumbImgUrl;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public String getUploadTime() {
        return UploadTime;
    }

    public String getID() {
        return ID;
    }

    public String getImgRemark() {
        return ImgRemark;
    }

    public String getAlbumsID() {
        return AlbumsID;
    }

    public String getThumbImgUrl() {
        return ThumbImgUrl;
    }
}
