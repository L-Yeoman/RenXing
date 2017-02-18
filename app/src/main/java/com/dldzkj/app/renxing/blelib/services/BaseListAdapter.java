 package com.dldzkj.app.renxing.blelib.services;

import java.util.List;

import com.dldzkj.app.renxing.MyApplication;
import com.lidroid.xutils.DbUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;


/**
 * 
 * Copyright © 2013 ZhouFu.All rights reserved.
 * 
 * @Title: BaseListAdapter.java
 * @Package cn.com.zhoufu.aogu.adapter
 * @Description: BaseListAdapter
 * @author: 高磊
 * @date: 2013-12-19 上午11:58:30
 * @version V1.0
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

	protected List<T> mList;
	
	protected List<T> otherList;

	protected Context mContext;

	protected LayoutInflater mInflater;

	protected MyApplication application;


	
	protected DbUtils dbUtils;

	public BaseListAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		application = (MyApplication) context.getApplicationContext();
		dbUtils =MyApplication.getInstance().getDbUtils();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mList == null ? 0 : position;
	}

	public void setList(List<T> list) {
		mList = list;
		notifyDataSetChanged();
	}

	public List<T> getList() {
		return mList;
	}
	
	public void setListData(List<T> list) {
		if (mList != null) {
			mList.clear();
			mList.addAll(list);
			notifyDataSetChanged();
		}
	}
	
	public void setList(List<T> mList,List<T> otherList){
		this.mList=mList;
		this.otherList=otherList;
	}

	public void add(T t) {
		if (mList != null) {
			mList.add(t);
			notifyDataSetChanged();
		}
	}
	public void addListData(List<T> list) {
		if (list != null&&mList!=null) {
			mList.addAll(list);
		}
		if(mList==null){
			mList=list;
		}
		notifyDataSetChanged();
	}
	

	public void addAll(List<T> list) {
		if (mList != null) {
			mList.addAll(list);
			notifyDataSetChanged();
		}
	}

	public void addAll(List<T> list, int position) {
		if (mList != null) {
			mList.remove(position);
			notifyDataSetChanged();
		}
	}

	public void remove(int position) {
		if (mList != null) {
			mList.remove(position);
			notifyDataSetChanged();
		}
	}

	public void remove(T t) {
		if (mList != null) {
			mList.remove(t);
			notifyDataSetChanged();
		}
	}
	
	

	public void removeAll() {
		if (mList != null) {
			mList.clear();
			notifyDataSetChanged();
		}
	}
}
