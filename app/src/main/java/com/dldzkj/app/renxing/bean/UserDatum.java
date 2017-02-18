package com.dldzkj.app.renxing.bean;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;
@Table(name="userDatum")
public class UserDatum {
	@Id
	@NoAutoIncrement
	private int UserID;//用户id
	private String Background;//个人中心背景
	private String Signature;//个性签名
	private long Logintude;//经度
	private long Latitude;//维度
	private int  PostCount;//发帖数
	private int  ReplyCount;//回帖数
	private int  BestPostCount;//精华贴数
	private int  LastTopicID;//最后发帖 id
	private int  LastReplyID;//最后回帖ID
	public int getUserID() {
		return UserID;
	}
	public void setUserID(int userID) {
		UserID = userID;
	}
	public String getBackground() {
		return Background;
	}
	public void setBackground(String background) {
		Background = background;
	}
	public String getSignature() {
		return Signature;
	}
	public void setSignature(String signature) {
		Signature = signature;
	}
	public long getLogintude() {
		return Logintude;
	}
	public void setLogintude(long logintude) {
		Logintude = logintude;
	}
	public long getLatitude() {
		return Latitude;
	}
	public void setLatitude(long latitude) {
		Latitude = latitude;
	}
	public int getPostCount() {
		return PostCount;
	}
	public void setPostCount(int postCount) {
		PostCount = postCount;
	}
	public int getReplyCount() {
		return ReplyCount;
	}
	public void setReplyCount(int replyCount) {
		ReplyCount = replyCount;
	}
	public int getBestPostCount() {
		return BestPostCount;
	}
	public void setBestPostCount(int bestPostCount) {
		BestPostCount = bestPostCount;
	}
	public int getLastTopicID() {
		return LastTopicID;
	}
	public void setLastTopicID(int lastTopicID) {
		LastTopicID = lastTopicID;
	}
	public int getLastReplyID() {
		return LastReplyID;
	}
	public void setLastReplyID(int lastReplyID) {
		LastReplyID = lastReplyID;
	}
	@Override
	public String toString() {
		return "UserDatum [UserID=" + UserID + ", Background=" + Background
				+ ", Signature=" + Signature + ", Logintude=" + Logintude
				+ ", Latitude=" + Latitude + ", PostCount=" + PostCount
				+ ", ReplyCount=" + ReplyCount + ", BestPostCount="
				+ BestPostCount + ", LastTopicID=" + LastTopicID
				+ ", LastReplyID=" + LastReplyID + "]";
	}
	
	
}
