package com.dldzkj.app.renxing.bean;

import com.dldzkj.app.renxing.utils.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/7/22.
 */
public class PlantListModel {
    /**
     * User_ID : 4
     * ReplyCount : 0
     * Is_Essence : 1
     * PID : 4453
     * Is_Hot : 0
     * UserName : 唐僧
     * Is_Top : 0
     * Title : 晒一晒这两年收藏的丝袜
     * ImgList : [{"Thumb_Path":"/upload/201507/24/thumb_201507241101377769.jpg","ImgID":"8903","ImgAddTime":"2015/7/24 11:01:53","Original_Path":"/upload/201507/24/201507241101377769.jpg"},{"Thumb_Path":"/upload/201507/24/thumb_201507241101378399.jpg","ImgID":"8904","ImgAddTime":"2015/7/24 11:01:53","Original_Path":"/upload/201507/24/201507241101378399.jpg"}]
     * NicName : 唐僧
     * Is_Red : 0
     * Content : PHByZSBzdHlsZT0nd29yZC1icmVhazpicmVhay1hbGw7d29yZC13cmFwOmJyZWFrLXdvcmQ7Jz7lkozogIHlhazmmK/lpKflrablkIzlrabvvIzliIbliIblkIjlkIjov5jmmK/lnKjkuIrljYrlubTnu5PlqZrkuobjgILku5bmmK/lub/lt57nmoTvvIzmiJHkuIrmtbfvvIzmr5XkuJrlkI7kuIDnm7TlvILlnLDjgILlm6DkuLrku5bmmK/kuKrkuJ3oopzmjqfvvIzmr4/mrKHop4HpnaLpg73nu5nmiJHkubDlkITnp43kuJ3oopznhLblkI7mi43nhafjgILnjrDlnKjnu5PlqZrkuobvvIzlhajpg6jmmZLlh7rmnaXjgIINCumbhum9kOS4g+iJsuS4neiinOWPr+S7pee7k+WpmueahO+8nzwvcHJlPg==
     * AddTime : 2015/7/24 11:01:53
     */
    private String User_ID;
    private String ReplyCount;
    private String Is_Essence;
    private String PID;
    private String Is_Hot;
    private String UserName;
    private String Is_Top;
    private String Title;
    private List<ImgListEntity> ImgList;
    private String NicName;
    private String Is_Red;
    private String Content;
    private String AddTime;

    public void setUser_ID(String User_ID) {
        this.User_ID = User_ID;
    }

    public void setReplyCount(String ReplyCount) {
        this.ReplyCount = ReplyCount;
    }

    public void setIs_Essence(String Is_Essence) {
        this.Is_Essence = Is_Essence;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public void setIs_Hot(String Is_Hot) {
        this.Is_Hot = Is_Hot;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setIs_Top(String Is_Top) {
        this.Is_Top = Is_Top;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setImgList(List<ImgListEntity> ImgList) {
        this.ImgList = ImgList;
    }

    public void setNicName(String NicName) {
        this.NicName = NicName;
    }

    public void setIs_Red(String Is_Red) {
        this.Is_Red = Is_Red;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public void setAddTime(String AddTime) {
        this.AddTime = AddTime;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public String getReplyCount() {
        return ReplyCount;
    }

    public String getIs_Essence() {
        return Is_Essence;
    }

    public String getPID() {
        return PID;
    }

    public String getIs_Hot() {
        return Is_Hot;
    }

    public String getUserName() {
        return UserName;
    }

    public String getIs_Top() {
        return Is_Top;
    }

    public String getTitle() {
        return Title;
    }

    public List<ImgListEntity> getImgList() {
        return ImgList;
    }

    public String getNicName() {
        return NicName;
    }

    public String getIs_Red() {
        return Is_Red;
    }

    public String getContent() {
        return Content;
    }

    public String getAddTime() {
        return AddTime;
    }

    public static class ImgListEntity {
        /**
         * Thumb_Path : /upload/201507/24/thumb_201507241101377769.jpg
         * ImgID : 8903
         * ImgAddTime : 2015/7/24 11:01:53
         * Original_Path : /upload/201507/24/201507241101377769.jpg
         */
        private String Thumb_Path;
        private String ImgID;
        private String ImgAddTime;
        private String Original_Path;

        public void setThumb_Path(String Thumb_Path) {
            this.Thumb_Path = Thumb_Path;
        }

        public void setImgID(String ImgID) {
            this.ImgID = ImgID;
        }

        public void setImgAddTime(String ImgAddTime) {
            this.ImgAddTime = ImgAddTime;
        }

        public void setOriginal_Path(String Original_Path) {
            this.Original_Path = Original_Path;
        }

        public String getThumb_Path() {
            return Thumb_Path;
        }

        public String getImgID() {
            return ImgID;
        }

        public String getImgAddTime() {
            return ImgAddTime;
        }

        public String getOriginal_Path() {
            return Original_Path;
        }
    }
}
