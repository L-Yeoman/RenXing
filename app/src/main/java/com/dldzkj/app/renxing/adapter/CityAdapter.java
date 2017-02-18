package com.dldzkj.app.renxing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;

import java.util.List;

public class CityAdapter<T> extends BaseAdapter {
	private Context context;

	protected List<T> mList;

	protected List<T> otherList;

	protected Context mContext;

	protected LayoutInflater mInflater;



	public CityAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
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
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

			convertView=LayoutInflater.from(context).inflate(R.layout.area_item_layout,null);
			TextView _View= (TextView) convertView.findViewById(R.id.ail_text);
			String s=(String)mList.get(position);
			convertView.setTag(s);
			_View.setText(s);
			return convertView;
	}
}
