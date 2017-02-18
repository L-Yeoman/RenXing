package com.dldzkj.app.renxing.bean;

import java.io.Serializable;


public class ArticleCommentModel implements Serializable {
	
	private int ID;
	private int Article_ID;
	private int Parent_ID;
	private int User_ID;
	private String User_Name;
	private String User_IP;
	private String Content;
	private int Is_Lock;
	private String Add_Time;
	private int Is_Reply;
	private String Reply_Content;
	private String Reply_Time;
	
	private String headUri;

	private String NicName;
	private String Portrait;
	/**
	 * RNum : 3
	 */
	private String RNum;

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getArticle_ID() {
		return Article_ID;
	}
	public void setArticle_ID(int article_ID) {
		Article_ID = article_ID;
	}
	public int getParent_ID() {
		return Parent_ID;
	}
	public void setParent_ID(int parent_ID) {
		Parent_ID = parent_ID;
	}
	public int getUser_ID() {
		return User_ID;
	}
	public void setUser_ID(int user_ID) {
		User_ID = user_ID;
	}
	public String getUser_Name() {
		return User_Name;
	}
	public void setUser_Name(String user_Name) {
		User_Name = user_Name;
	}
	public String getUser_IP() {
		return User_IP;
	}
	public void setUser_IP(String user_IP) {
		User_IP = user_IP;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public int getIs_Lock() {
		return Is_Lock;
	}
	public void setIs_Lock(int is_Lock) {
		Is_Lock = is_Lock;
	}
	public String getAdd_Time() {
		return Add_Time;
	}
	public void setAdd_Time(String add_Time) {
		Add_Time = add_Time;
	}
	public int getIs_Reply() {
		return Is_Reply;
	}
	public void setIs_Reply(int is_Reply) {
		Is_Reply = is_Reply;
	}
	public String getReply_Content() {
		return Reply_Content;
	}
	public void setReply_Content(String reply_Content) {
		Reply_Content = reply_Content;
	}
	public String getReply_Time() {
		return Reply_Time;
	}
	public void setReply_Time(String reply_Time) {
		Reply_Time = reply_Time;
	}
	public String getHeadUri() {
		return headUri;
	}
	public void setHeadUri(String headUri) {
		this.headUri = headUri;
	}


	public void setNicName(String NicName) {
		this.NicName = NicName;
	}

	public void setPortrait(String Portrait) {
		this.Portrait = Portrait;
	}

	public String getNicName() {
		return NicName;
	}

	public String getPortrait() {
		return Portrait;
	}

	public void setRNum(String RNum) {
		this.RNum = RNum;
	}

	public String getRNum() {
		return RNum;
	}
}
