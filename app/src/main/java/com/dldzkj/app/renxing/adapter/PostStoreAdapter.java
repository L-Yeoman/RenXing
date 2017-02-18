package com.dldzkj.app.renxing.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.MyFavoritesActivity;
import com.dldzkj.app.renxing.bbs.BBSDetailsActivity;
import com.dldzkj.app.renxing.bean.ArticleModel;
import com.dldzkj.app.renxing.bean.BBSModel;
import com.dldzkj.app.renxing.customview.PostStore;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class PostStoreAdapter extends UltimateViewAdapter<PostStoreAdapter.SimpleAdapterViewHolder>  {

	private List<BBSModel> mList;
	private Context mContext;
	private ArrayList<BBSModel> deleteList;
	private boolean isDelete = false;
	private MyFavoritesActivity mActivity;

	public ArrayList<BBSModel> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(ArrayList<BBSModel> deleteList) {
		this.deleteList = deleteList;
	}

	public void clearDeleteList(){
		this.deleteList.clear();
	}
	public void deleteItem(){
		if(deleteList.size()>0){
			for(int i=0;i<deleteList.size();i++){
				BBSModel _Model = deleteList.get(i);
				mList.remove(_Model);
			}
			deleteList.clear();
		}
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	public void setData(List<BBSModel>list, Context c,MyFavoritesActivity obj){
		mList=list;
		mContext=c;
		deleteList = new ArrayList<BBSModel>();
		mActivity = obj;
	}



	@Override
	public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
		return null;
	}

	@Override
	public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

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
	public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
		PostStore _View=new PostStore(mContext);
		SimpleAdapterViewHolder holder = new SimpleAdapterViewHolder(_View,true);
		return holder;
	}

	@Override
	public void onBindViewHolder(SimpleAdapterViewHolder holder, final int position) {
		if (position < getItemCount() && (customHeaderView != null ? position <= mList.size() : position < mList.size()) &&
				(customHeaderView != null ? position > 0 : true)) {
			final BBSModel _Model = (BBSModel) mList.get(position);
			holder._View.setData(_Model, isDelete, deleteList);
			holder._View.setSelect(mActivity);

		}
	}

	@Override
	public SimpleAdapterViewHolder getViewHolder(View view) {
		return new SimpleAdapterViewHolder(view,false);
	}


	public class SimpleAdapterViewHolder extends RecyclerView.ViewHolder {
        PostStore _View;
		RelativeLayout _Layout;
		public SimpleAdapterViewHolder(View view,boolean isItem) {
			super(view);
			if(isItem){
				_View=(PostStore)view;
				_Layout = (RelativeLayout) _View.findViewById(R.id.psi_layout);
			}
		}
	}
}
