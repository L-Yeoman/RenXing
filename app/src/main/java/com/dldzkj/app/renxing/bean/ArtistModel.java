package com.dldzkj.app.renxing.bean;

import java.io.Serializable;
import java.util.ArrayList;
/*****每一篇社区帖子的实体类*****/
public class ArtistModel implements Serializable {
	private int id;
	private int articleId;
	private int authId;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	private String title;
	private String avarter;
	private String name;
	private String time;
	private int likeCounts;
	private int commentCounts;
	private boolean isLike;
	private ArrayList<String> pics;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getPics() {
		return pics;
	}
	public void setPics(ArrayList<String> pics) {
		this.pics = pics;
	}
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public int getAuthId() {
		return authId;
	}
	public void setAuthId(int authId) {
		this.authId = authId;
	}
	public String getAvarter() {
		return avarter;
	}
	public void setAvarter(String avarter) {
		this.avarter = avarter;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getLikeCounts() {
		return likeCounts;
	}
	public void setLikeCounts(int likeCounts) {
		this.likeCounts = likeCounts;
	}
	public int getCommentCounts() {
		return commentCounts;
	}
	public void setCommentCounts(int commentCounts) {
		this.commentCounts = commentCounts;
	}
	public boolean isLike() {
		return isLike;
	}
	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}
}