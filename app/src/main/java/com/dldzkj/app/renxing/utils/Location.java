package com.dldzkj.app.renxing.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.dldzkj.app.renxing.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location {
	

	public static final String DB_ADDRESS = "/data/data/com.dldzkj.app.renxing/databases";

	public static Map<String, String> getLocation(Context context,
			String columnName, int codeLen, String codeStart) {
		Map<String, String> map = new HashMap<String, String>();
		String path = DB_ADDRESS + "/city.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		Cursor code =null;
		if ("0".equals(codeStart)) {
			code = db.rawQuery("select Code ," + columnName
					+ " from sys_City where LENGTH(Code)=" + codeLen, null);
		} else {
			String sql="select Code ," + columnName
					+ " from sys_City where LENGTH(Code)=" + codeLen
					+ " and Code like '" + codeStart + "%'";
			
			code = db.rawQuery(sql, null);
		}
		while (code.moveToNext()) {
			map.put(code.getString(0), code.getString(1));
		}
		code.close();
		db.close();
		return map;
	}
	
	public static Map<String, String> getAddress(String i){
		Map<String, String> map = new HashMap<String, String>();
		String p="";
		String city="";
		String c="";
		String path = DB_ADDRESS + "/city.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		Cursor code =null;
		code=db.rawQuery("select * from sys_City where Code = "+"'"+i+"'", null);
		while(code.moveToNext()){
			p=code.getString(1);
			city=code.getString(2);
			c=code.getString(3);
		}
		map.put("province", p);
		map.put("city", city);
		map.put("country", c);
		code.close();
		db.close();
		Log.i("data", map.get("province") + map.get("city") + map.get("country"));
		return map;
	}
	
	//根据ID查询城市名称
	public static String getEachAddress(String id){
		CopyRegon();
		String address=null;
		String path =DB_ADDRESS + "/city.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		Cursor c=null;
		c=db.rawQuery("select Name from MR_SiteData where id="+id, null);
		if(c.moveToNext()){
			address=c.getString(0);
		}
		c.close();
		db.close();
		return address;
	}
	
	//根据parentID查询城市列表
	public static List<String> getCityList(String id){
		List<String> list=new ArrayList<String>();
		String path =DB_ADDRESS + "/city.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		Cursor c=null;
		c=db.rawQuery("select Name from MR_SiteData where ParentID="+id, null);
		while(c.moveToNext()){
			list.add(c.getString(0));
		}
		c.close();
		db.close();
		return list;
	}
	
	//根据城市名称查询ID
	public static String getAddressID(String city,boolean isArea){
		String id=null;
		String id2=null;
		String path = DB_ADDRESS + "/city.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		Cursor c=null;
		c=db.rawQuery("select ID from MR_SiteData where Name='"+city+"'", null);
		if(c.moveToNext()){
			id=c.getString(0);
		}
		if(c.moveToNext()){
			id2=c.getString(0);
		}
		c.close();
		db.close();
		
		if(isArea&&!TextUtils.isEmpty(id2)){
			return Integer.parseInt(id2)>Integer.parseInt(id)?id2:id;
		}
		return id;
	}

	public static void CopyRegon() {
		File file = new File(Location.DB_ADDRESS, "city.db");
		if (!file.exists()) {
			CopyDB.copyDB(MyApplication.getInstance().getBaseContext(), "city.db", file.getAbsolutePath());

			Log.e("CopyRegion", "文件不存在");
		}else{
			Log.e("CopyRegion", "文件已存在");
		}
	}

}
