package com.dldzkj.app.renxing.bean;

import java.util.Date;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="UserPraise")
public class UserPraise {//本地点赞数据库
	@Id
	@NoAutoIncrement
private int ID;
private int UserID;
private int Type;//赞的类型（0文章，1帖子）
private int EntityID;//文章或帖子ID
private Date AddTime;//添加时间
public int getUserID() {
	return UserID;
}
public void setUserID(int userID) {
	UserID = userID;
}
public int getID() {
	return ID;
}
public void setID(int iD) {
	ID = iD;
}
public int getType() {
	return Type;
}
public void setType(int type) {
	Type = type;
}
public int getEntityID() {
	return EntityID;
}
public void setEntityID(int entityID) {
	EntityID = entityID;
}
public Date getAddTime() {
	return AddTime;
}
public void setAddTime(Date addTime) {
	AddTime = addTime;
}
}
