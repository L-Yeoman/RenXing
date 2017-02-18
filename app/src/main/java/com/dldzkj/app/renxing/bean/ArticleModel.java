package com.dldzkj.app.renxing.bean;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "ArticleModels")
public class ArticleModel implements Serializable{
	@Id
	@NoAutoIncrement
	private int ID;
	private int Category_ID;
	private String Img_Url;
	private String Goods_Url;
	private String Summary;
	private String Author;
	private String Source;
	private String Content;
	private int Sort_ID;
	private int Clicks;
	private int Status;
	private int Is_Reply;
	private int Is_Top;
	private int Is_Red;
	private int Is_Essence;
	private int Is_Hot;
	private int Favor_Count;
	private int Comment_Count;
	private String User_Name;
	private String Add_Time;
	private String Update_Time;
	private String Title;
	/**
	 * UFID : 36
	 * Type : 0
	 * CreateTime : 2015-07-24 16:47:24
	 * FavoriteID : 15
	 */
	private String UFID;
	private String Type;
	private String CreateTime;
	private String FavoriteID;
	/**
	 * IsFavorite : True
	 * IsPraise : True
	 */
	private String IsFavorite;
	private String IsPraise;

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getCategory_ID() {
		return Category_ID;
	}
	public void setCategory_ID(int category_ID) {
		Category_ID = category_ID;
	}
	public String getImg_Url() {
		return Img_Url;
	}
	public void setImg_Url(String img_Url) {
		Img_Url = img_Url;
	}
	public String getGoods_Url() {
		return Goods_Url;
	}
	public void setGoods_Url(String goods_Url) {
		Goods_Url = goods_Url;
	}
	public String getSummary() {
		return Summary;
	}
	public void setSummary(String summary) {
		Summary = summary;
	}
	public String getAuthor() {
		return Author;
	}
	public void setAuthor(String author) {
		Author = author;
	}
	public String getSource() {
		return Source;
	}
	public void setSource(String source) {
		Source = source;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public int getSort_ID() {
		return Sort_ID;
	}
	public void setSort_ID(int sort_ID) {
		Sort_ID = sort_ID;
	}
	public int getClicks() {
		return Clicks;
	}
	public void setClicks(int clicks) {
		Clicks = clicks;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	public int getIs_Reply() {
		return Is_Reply;
	}
	public void setIs_Reply(int is_Reply) {
		Is_Reply = is_Reply;
	}
	public int getIs_Top() {
		return Is_Top;
	}
	public void setIs_Top(int is_Top) {
		Is_Top = is_Top;
	}
	public int getIs_Red() {
		return Is_Red;
	}
	public void setIs_Red(int is_Red) {
		Is_Red = is_Red;
	}
	public int getIs_Essence() {
		return Is_Essence;
	}
	public void setIs_Essence(int is_Essence) {
		Is_Essence = is_Essence;
	}
	public int getIs_Hot() {
		return Is_Hot;
	}
	public void setIs_Hot(int is_Hot) {
		Is_Hot = is_Hot;
	}
	public int getFavor_Count() {
		return Favor_Count;
	}
	public void setFavor_Count(int favor_Count) {
		Favor_Count = favor_Count;
	}
	public int getComment_Count() {
		return Comment_Count;
	}
	public void setComment_Count(int comment_Count) {
		Comment_Count = comment_Count;
	}
	public String getUser_Name() {
		return User_Name;
	}
	public void setUser_Name(String user_Name) {
		User_Name = user_Name;
	}
	public String getAdd_Time() {
		return Add_Time;
	}
	public void setAdd_Time(String add_Time) {
		Add_Time = add_Time;
	}
	public String getUpdate_Time() {
		return Update_Time;
	}
	public void setUpdate_Time(String update_Time) {
		Update_Time = update_Time;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
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

	public String getFavoriteID() {
		return FavoriteID;
	}

	public void setIsFavorite(String IsFavorite) {
		this.IsFavorite = IsFavorite;
	}

	public void setIsPraise(String IsPraise) {
		this.IsPraise = IsPraise;
	}

	public String getIsFavorite() {
		return IsFavorite;
	}

	public String getIsPraise() {
		return IsPraise;
	}
}
