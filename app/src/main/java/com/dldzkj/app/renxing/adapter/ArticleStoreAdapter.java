package com.dldzkj.app.renxing.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.ArticleActivity;
import com.dldzkj.app.renxing.activity.MyFavoritesActivity;
import com.dldzkj.app.renxing.bean.ArticleModel;
import com.dldzkj.app.renxing.bean.BBSModel;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.customview.PostStore;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

public class ArticleStoreAdapter extends UltimateViewAdapter<ArticleStoreAdapter.SimpleAdapterViewHolder> {
	
	private Context mContext;
	private List<ArticleModel> mList;
	private boolean showCheck=false;
	private ArrayList<ArticleModel> deleteList;
	private boolean isDelete = false;

	private OnSelect mSelect;
	
	public void setData(Context c,List<ArticleModel> list){
		mList=list;
		mContext=c;
		deleteList = new ArrayList<ArticleModel>();
	}
	public void setSelect(OnSelect select){
		mSelect = select;
	}
	public interface OnSelect{
		public void onSelect(boolean bool);
	}
	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	public ArrayList<ArticleModel> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(ArrayList<ArticleModel> deleteList) {
		this.deleteList = deleteList;
	}

	public void clearDeleteList(){

		this.deleteList.clear();
	}
	public void deleteItem(){
		if(deleteList.size()>0){
			for(int i=0;i<deleteList.size();i++){
				ArticleModel _Model = deleteList.get(i);
				mList.remove(_Model);
			}
			deleteList.clear();
		}
	}

	@Override
	public ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
		return null;
	}

	@Override
	public void onBindHeaderViewHolder(ViewHolder viewHolder, int i) {

	}


	@Override
	public int getAdapterItemCount() {
		return mList.size();
	}

	@Override
	public long generateHeaderId(int i) {
		return 0;
	}

	@Override
	public void onBindViewHolder( final SimpleAdapterViewHolder arg0,  final int position) {
	//	View _View=(View)mList.get(arg1);
		if (position < getItemCount() && (customHeaderView != null ? position <= mList.size() : position < mList.size()) &&
				(customHeaderView != null ? position > 0 : true)) {
			int i = customHeaderView != null ? position - 1 : position;
			final ArticleModel _Model = (ArticleModel) mList.get(i);
			checkConcrol(arg0,_Model);
			arg0.titleText.setText(_Model.getTitle());
			arg0.mTime.setText(_Model.getAdd_Time());
			arg0.likeCount.setText(_Model.getFavor_Count() + "");
			arg0._layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isDelete){
						if (!deleteList.contains(_Model)) {
							deleteList.add(_Model);
							arg0._Check.setChecked(true);
						} else {
							deleteList.remove(_Model);
							arg0._Check.setChecked(false);
						}
						if(deleteList.size()>0){
							mSelect.onSelect(true);
						}else{
							mSelect.onSelect(false);
						}
						return;
					}
					Intent _Intent = new Intent();
					_Intent.setClass(mContext, ArticleActivity.class);
					Bundle _Bundle = new Bundle();
					_Bundle.putInt("", Integer.parseInt(_Model.getUFID()));
					Log.i("lyBin","收藏，ID："+Integer.parseInt(_Model.getUFID()));
					_Intent.putExtras(_Bundle);
					mContext.startActivity(_Intent);
				}
			});
		}
		
	}
	private void checkConcrol(SimpleAdapterViewHolder holder,ArticleModel model){
		if(isDelete){
			holder._Check.setVisibility(View.VISIBLE);
		}else{
			holder._Check.setVisibility(View.GONE);
		}
		holder._Check.setChecked(false);
		if(holder._Check.getVisibility()==View.VISIBLE&&deleteList.size()>0){
			if(deleteList.contains(model)){
				holder._Check.setChecked(true);
			}
		}
	}

	@Override
	public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup arg0) {

		View _View=LayoutInflater.from(mContext).inflate(R.layout.article_store_item,
				arg0,false);
		return new SimpleAdapterViewHolder(_View,true);
	}


	@Override
	public SimpleAdapterViewHolder getViewHolder(View view) {
		return new SimpleAdapterViewHolder(view,false);
	}


	public class SimpleAdapterViewHolder extends ViewHolder{
		public TextView titleText,likeCount,mTime;
		public CheckBox _Check;
		public LinearLayout _layout;
		public SimpleAdapterViewHolder(View arg0,boolean isItem) {
			super(arg0);
			if(isItem){
				_layout=(LinearLayout) arg0;
				_Check = (CheckBox)_layout.findViewById(R.id.asi_checkBox);
				titleText=(TextView)_layout.findViewById(R.id.asi_title);
				likeCount=(TextView)_layout.findViewById(R.id.asi_like_count);
				mTime=(TextView)_layout.findViewById(R.id.asi_time);
			}
		}
	}
}
