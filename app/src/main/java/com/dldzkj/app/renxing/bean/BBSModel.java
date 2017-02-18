package com.dldzkj.app.renxing.bean;

import com.dldzkj.app.renxing.utils.StringUtils;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;
import java.util.List;

/*****
 * 每一篇帖子详情的实体类
 *****/
@Table(name = "BBSPosts")
public class BBSModel {
    /**
     * User_ID : 4
     * ReplyCount : 12
     * PID : 4453
     * BoardName : 丝情袜意
     * UserName : 唐僧
     * GroupID : 3
     * Favor_Count : 1
     * "IsPraise":"True"
     * "IsFavorite":"False"
     * Title : 晒一晒这两年收藏的丝袜
     * ImgList : []
     * Exp : 0
     * NicName : 唐僧
     * BoardID : 25
     * Portrait : /Files/upload/users/201507/24/201507241057372701.jpg
     * Content : PHByZSBzdHlsZT0nd29yZC1icmVhazpicmVhay1hbGw7d29yZC13cmFwOmJyZWFrLXdvcmQ7Jz7lkozogIHlhazmmK/lpKflrablkIzlrabvvIzliIbliIblkIjlkIjov5jmmK/lnKjkuIrljYrlubTnu5PlqZrkuobjgILku5bmmK/lub/lt57nmoTvvIzmiJHkuIrmtbfvvIzmr5XkuJrlkI7kuIDnm7TlvILlnLDjgILlm6DkuLrku5bmmK/kuKrkuJ3oopzmjqfvvIzmr4/mrKHop4HpnaLpg73nu5nmiJHkubDlkITnp43kuJ3oopznhLblkI7mi43nhafjgILnjrDlnKjnu5PlqZrkuobvvIzlhajpg6jmmZLlh7rmnaXjgIINCumbhum9kOS4g+iJsuS4neiinOWPr+S7pee7k+WpmueahO+8nzwvcHJlPg==
     * AddTime : 2015/7/24 11:01:53
     */
    private String User_ID;
    private int ReplyCount;
    @Id
    @NoAutoIncrement
    private int PID;
    private String BoardName;
    private String UserName;
    private String GroupID;
    private String IsPraise;
    private String IsFavorite;
    private int Favor_Count;
    private String Title;
    private List<ImgListEntity> ImgList;
    private String Exp;
    private String NicName;
    private int BoardID;
    private String Portrait;
    private String Content;
    private String AddTime;
	/**
	 * UFID : 4054
	 * Type : 1
	 * CreateTime : 2015-07-25 08:18:06
	 * UserID : 293
	 * FavoriteID : 37
	 */
	private String UFID;
	private String Type;
	private String CreateTime;
	private String UserID;
	private String FavoriteID;

    public String getIsPraise() {
        return IsPraise;
    }

    public void setIsPraise(String isPraise) {
        IsPraise = isPraise;
    }

    public String getIsFavorite() {
        return IsFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        IsFavorite = isFavorite;
    }

    public void setUser_ID(String User_ID) {
        this.User_ID = User_ID;
    }

    public void setReplyCount(int ReplyCount) {
        this.ReplyCount = ReplyCount;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public void setBoardName(String BoardName) {
        this.BoardName = BoardName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setGroupID(String GroupID) {
        this.GroupID = GroupID;
    }

    public void setFavor_Count(int Favor_Count) {
        this.Favor_Count = Favor_Count;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setImgList(List<ImgListEntity> ImgList) {
        this.ImgList = ImgList;
    }

    public void setExp(String Exp) {
        this.Exp = Exp;
    }

    public void setNicName(String NicName) {
        this.NicName = NicName;
    }

    public void setBoardID(int BoardID) {
        this.BoardID = BoardID;
    }

    public void setPortrait(String Portrait) {
        this.Portrait = Portrait;
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

    public int getReplyCount() {
        return ReplyCount;
    }

    public int getPID() {
        return PID;
    }

    public String getBoardName() {
        return BoardName;
    }

    public String getUserName() {
        return UserName;
    }

    public String getGroupID() {
        return GroupID;
    }

    public int getFavor_Count() {
        return Favor_Count;
    }

    public String getTitle() {
        return Title;
    }

    public List<ImgListEntity> getImgList() {
        return ImgList;
    }

    public String getExp() {
        return Exp;
    }

    public String getNicName() {
        return NicName;
    }

    public int getBoardID() {
        return BoardID;
    }

    public String getPortrait() {
        return Portrait;
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

	public void setUFID(String UFID) {
		this.UFID = UFID;
	}

	public void setType(String Type) {
		this.Type = Type;
	}

	public void setCreateTime(String CreateTime) {
		this.CreateTime = CreateTime;
	}

	public void setUserID(String UserID) {
		this.UserID = UserID;
	}

	public void setFavoriteID(String FavoriteID) {
		this.FavoriteID = FavoriteID;
	}

	public String getUFID() {
		return UFID;
	}

	public String getType() {
		return Type;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public String getUserID() {
		return UserID;
	}

	public String getFavoriteID() {
		return FavoriteID;
	}
}