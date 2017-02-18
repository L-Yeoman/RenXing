package com.dldzkj.app.renxing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.Picture;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends  BaseAdapter {
	
	List<Picture> photos;
	Context mContext;
	BitmapUtils mUtils;
//	GridView mGridView;
	 PhotoClickListener mListener;
	 List<Picture> deleteItems;
	private  Boolean ISDELETE=false;
	
	 
	public interface PhotoClickListener{
		public void OnClick(View view);
		public void OnLongClick(View v);
		public void OnChickedClick(CompoundButton button, Boolean b);
	};
	public void setData(List<Picture> photos,Context c,BitmapUtils utils,PhotoClickListener listener){
		this.photos=photos;
		mContext=c;
		mUtils=utils;
	//	mGridView=gView;
		mListener=listener;
		deleteItems=new ArrayList<Picture>();
	}
	
	public List<Picture> getDeleteItems(){
		return deleteItems;
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return photos.size()+1;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	public Boolean isDelete(){
		return ISDELETE;
	}
	
	public void setDelete(Boolean b){
		ISDELETE=b;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		 view=(View)LayoutInflater.from(mContext).inflate(R.layout.myphoto_list_item, null);
		ImageView img;
		final CheckBox cb;
			img = (ImageView) view.findViewById(R.id.photo_imageView1);
			cb = (CheckBox) view.findViewById(R.id.photo_checkBox1);
			if(photos.size()==0||position==photos.size()){
				img.setImageResource(R.drawable.ic_add_photogrape);
			}else{
			//	mUtils.display(img,photos.get(position).getImgUrl());
				Glide.with(mContext)
						.load(photos.get(position).getImgUrl())
						.into(img);
			}
			cb.setTag(position);
			img.setTag(position);
			view.setTag(position);
			if(isDelete()&&position!=(photos.size())){
				cb.setVisibility(View.VISIBLE);
			/*	String s= photos.get(position).getImgUrl();
				if(deleteItems!=null&&deleteItems.contains(s)){
				//	cb.setChecked(true);
				}*/
			}
			
			view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.OnClick(view);
			}
		});
			view.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					mListener.OnLongClick(arg0);
					return false;
				}
			});
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton button, boolean b) {
					mListener.OnChickedClick(button, b);
				}
			});
			
		return view;
	}

}
