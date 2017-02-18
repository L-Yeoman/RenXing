package com.dldzkj.app.renxing.blelib.services;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.Playlist;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

/**
 * 
 * Copyright © 2013 ZhouFu.All rights reserved.
 * 
 * @Title: PlayAdapter.java
 * @Package cn.com.zhoufu.aogu.adapter
 * @Description: 播放列表适配器
 * @author: 高磊
 * @date: 2013-12-19 下午1:31:03  
 * @version V1.0
 */
public class PlaylistAdapter extends BaseListAdapter<Playlist> {
	private int flag;
	private MyApplication mApp;

	private int selectedPosition = -1;// 选中的位置
	private Context mContext;

	public PlaylistAdapter(Context context, int flag, MyApplication mApp) {
		super(context);
		mContext=context;
		this.flag = flag;
		this.mApp = mApp;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
	//	if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_playlist,parent,false);
			viewHolder = new ViewHolder();
			viewHolder.itemPosition=position;
			viewHolder.conView=convertView;
			viewHolder.dot = (TextView) convertView.findViewById(R.id.dot);
			viewHolder.dot1=(ImageView)convertView.findViewById(R.id.dot1);
			viewHolder.dot1.setImageResource(R.drawable.item_playing);
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.author = (TextView) convertView
					.findViewById(R.id.tv_author);
			viewHolder.fav = (ImageView) convertView.findViewById(R.id.iv_fav);
			if(viewHolder.itemPosition%2==0){
				convertView.setBackgroundResource(R.drawable.list_item);
				convertView.getBackground().setAlpha(50);
			}else{
				convertView.setBackgroundResource(R.drawable.list_item);
				convertView.getBackground().setAlpha(0);
			}
		//	convertView.setTag(viewHolder);
	/*	} else {
			viewHolder = (ViewHolder) convertView.getTag();
			if(viewHolder.itemPosition%2==0){
				convertView.setBackgroundResource(R.drawable.list_item);
			}
		}*/
		
		Playlist playlist = mList.get(position);

		// selectedPosition = position;
		if(position<9){
			viewHolder.dot.setText("0"+(position+1));
			
		}else{
			viewHolder.dot.setText(position+1+"");
		}
		viewHolder.name.setText(playlist.getTitle());
		viewHolder.author.setText(playlist.getArtist());
		// 如果当前音乐是收藏音乐
		
		if (playlist.getIsFav() == 1) {
			viewHolder.fav.setImageResource(R.drawable.icon_fav_pressed);
			viewHolder.fav.setOnClickListener(deleteFavClickListener);
		} else {
			viewHolder.fav.setImageResource(R.drawable.icon_fav_normal);
			viewHolder.fav.setOnClickListener(addFavClickListener);
		}
		if (mApp.mMusicServer.getCurrentPlay() != null
				&& playlist.get_mid() == mApp.mMusicServer.getCurrentPlay()
						.get_mid()
				&& playlist.getTitle().equals(
						mApp.mMusicServer.getCurrentPlay().getTitle())) {
			if (mApp.mMusicServer.isPlaying()){
				viewHolder.dot.setVisibility(View.GONE);
				viewHolder.dot1.setVisibility(View.VISIBLE);
				viewHolder.dot.setText("");
				//viewHolder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				viewHolder.author.setTextColor(mContext.getResources().getColor(
						R.color.select_item_text));
				viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.select_item_text));
			}
			else{
				
				viewHolder.dot1.setVisibility(View.GONE);
				viewHolder.dot.setVisibility(View.VISIBLE);
				if(position<9){
					
					viewHolder.dot.setText("0"+(position+1));
					
				}else{
					
					viewHolder.dot.setText(position+1+"");
				}
				
			}
				
			
			/*else
				viewHolder.dot.setText(position);*/
			
				
			/*viewHolder.name.setTextColor(mContext.getResources().getColor(
					R.color.text_gray));
			viewHolder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			viewHolder.author.setTextColor(mContext.getResources().getColor(
					R.color.text_gray));
			viewHolder.author.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);*/
		}
		 else {
		//	viewHolder.dot.setImageResource(R.drawable.music_listnormai);
			 if(position<9){
					viewHolder.dot.setText("0"+(position+1));
					
				}else{
					viewHolder.dot.setText(position+1+"");
				}
			/*viewHolder.name.setTextColor(mContext.getResources().getColor(
					R.color.black));
			viewHolder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			viewHolder.author.setTextColor(mContext.getResources().getColor(
					R.color.black));
			viewHolder.author.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);*/
		}
		viewHolder.fav.setTag(playlist);
		return convertView;
	}

	private View.OnClickListener addFavClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Playlist playlist = (Playlist) v.getTag();
			ImageView imageView = (ImageView) v;
			// 把音乐添加到数据库
			try {
				playlist.setIsFav(1);
				dbUtils.save(playlist);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageView.setImageResource(R.drawable.icon_fav_pressed);
			// 把音乐添加到对方列表
			otherList.add(playlist);
			notifyDataSetChanged();
		}
	};

	private View.OnClickListener deleteFavClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Playlist playlist = (Playlist) v.getTag();
			ImageView imageView = (ImageView) v;
			// 从数据库删除收藏列表
			playlist.setIsFav(0);
			try {
				dbUtils.delete(Playlist.class, WhereBuilder.b("_mid", "=",  playlist.get_mid()));
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			//如果这个列表是收藏列表
			if (flag == 1) {
				remove(playlist);
			} else {
				imageView.setImageResource(R.drawable.icon_fav_normal);
			}
			// 把另一列表的对应音乐设置为非收藏
			// 遍历列表，若音乐一样，删掉，把此音乐替换进去
			int t = otherList.size();
			for (int i = 0; i < t; i++) {
				if (otherList.get(i).equals(playlist)) {
					otherList.remove(i);
					if (flag == 1)
						otherList.add(i, playlist);
					return;
				}
			}
			notifyDataSetChanged();
		}
	};

	static class ViewHolder {
		 int itemPosition;
		TextView dot;
		TextView name;
		TextView author;
		ImageView fav,dot1;
		View conView;
	}
}
