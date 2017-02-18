package com.dldzkj.app.renxing.bbs;

import com.dldzkj.app.renxing.utils.StringUtils;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.List;

/************
 * 社区帖子评论实体类
 ************/
@Table(name = "BBSReply")
public class BBSCommentModel implements Serializable {
    /**
     * ImgList : [{"Thumb_Path":"/Upload/201507/25/small_20150725111546723.jpg","ImgID":"2","ImgAddTime":"2015/7/25 11:15:46","Original_Path":"/Upload/201507/25/20150725111546723.jpg"}]
     * ReplyCount : 0
     * PID : 4453
     * "IsPraise":"False"
     * ReplyTime : 2015/7/25 11:15:37
     * IP : 1.192.72.178
     * NicName : 习大大
     * RID : 989
     * Portrait : /Upload/201507/23/20150723185558096.jpg
     * Is_Show : 1
     * UserID : 293
     * UserName : 18530938140
     * Content : 监控
     * Favor_Count : 0
     * Title :
     */
    private List<ImgListEntity> ImgList;
    private int ReplyCount;
    private String PID;
    private String ReplyTime;
    private String IP;
    private String NicName;
    private String IsPraise;
    private String RID;
    private String Portrait;
    private String Is_Show;
    private String UserID;
    private String UserName;
    private String Content;
    private int Favor_Count;
    private String Title;

    public String getIsPraise() {
        return IsPraise;
    }

    public void setIsPraise(String isPraise) {
        IsPraise = isPraise;
    }

    public void setImgList(List<ImgListEntity> ImgList) {
        this.ImgList = ImgList;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public void setReplyTime(String ReplyTime) {
        this.ReplyTime = ReplyTime;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setNicName(String NicName) {
        this.NicName = NicName;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public void setPortrait(String Portrait) {
        this.Portrait = Portrait;
    }

    public void setIs_Show(String Is_Show) {
        this.Is_Show = Is_Show;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public List<ImgListEntity> getImgList() {
        return ImgList;
    }

    public String getPID() {
        return PID;
    }

    public String getReplyTime() {
        return ReplyTime;
    }

    public String getIP() {
        return IP;
    }

    public String getNicName() {
        return NicName;
    }

    public String getRID() {
        return RID;
    }

    public String getPortrait() {
        return Portrait;
    }

    public String getIs_Show() {
        return Is_Show;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getContent() {
        return StringUtils.DecodeBase64Str(Content);
    }

    public int getReplyCount() {
        return ReplyCount;
    }

    public void setReplyCount(int replyCount) {
        ReplyCount = replyCount;
    }

    public int getFavor_Count() {
        return Favor_Count;
    }

    public void setFavor_Count(int favor_Count) {
        Favor_Count = favor_Count;
    }

    public String getTitle() {
        return Title;
    }

    public static class ImgListEntity {
        /**
         * Thumb_Path : /Upload/201507/25/small_20150725111546723.jpg
         * ImgID : 2
         * ImgAddTime : 2015/7/25 11:15:46
         * Original_Path : /Upload/201507/25/20150725111546723.jpg
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