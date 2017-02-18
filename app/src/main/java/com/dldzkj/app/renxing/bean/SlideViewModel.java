package com.dldzkj.app.renxing.bean;

/**
 * Created by Administrator on 2015/7/25.
 */
public class SlideViewModel {
    /**
     * TOPID : 4408帖子或者文章id
     * Img_Url : /upload/201507/18/thumb_201507181052114514.jpg
     * Type : 1帖子或者文章类型
     */
    private int TOPID;
    private String Img_Url;
    private int Type;

    public void setTOPID(int TOPID) {
        this.TOPID = TOPID;
    }

    public void setImg_Url(String Img_Url) {
        this.Img_Url = Img_Url;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public int getTOPID() {
        return TOPID;
    }

    public String getImg_Url() {
        return Img_Url;
    }

    public int getType() {
        return Type;
    }
}
