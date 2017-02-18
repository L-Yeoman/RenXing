package com.dldzkj.app.renxing.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.ArticleActivity;
import com.dldzkj.app.renxing.activity.PersonalInfo;
import com.dldzkj.app.renxing.bean.ArticleModel;
import com.dldzkj.app.renxing.bean.Picture;
import com.dldzkj.app.renxing.customview.ArticlePostView;
import com.dldzkj.app.renxing.customview.SlideShowView;
import com.lidroid.xutils.BitmapUtils;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.stickyheadersrecyclerview.caching.HeaderViewCache;

import java.util.List;

public class ArticleListAdapter extends UltimateViewAdapter<ArticleListAdapter.SimpleAdapterViewHolder> {
	private List mList;
	private Context mContext;
	private BitmapUtils mUtils;
	private List picList;
	public void setData(List<ArticleModel> list,Context c,BitmapUtils bitmapUtils){
		mList=list;
		mContext=c;
		mUtils=bitmapUtils;
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
	public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup view) {
		View _View = new ArticlePostView(mContext,0);
		SimpleAdapterViewHolder _Holder =  new SimpleAdapterViewHolder(_View,true);
		return _Holder;
	}
	@Override
	public void onBindViewHolder(SimpleAdapterViewHolder view,  int position) {
		if (position < getItemCount() && (customHeaderView != null ? position <= mList.size() : position < mList.size()) &&
				(customHeaderView != null ? position > 0 : true)) {
			int i = customHeaderView != null ? position - 1 : position;
			final ArticleModel _Model = (ArticleModel) mList.get(i);
			view._View.setData(_Model, i, mUtils);
			view._View.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent _Intent = new Intent();
					_Intent.setClass(mContext, ArticleActivity.class);
					Bundle _Bundle = new Bundle();
					_Bundle.putInt("", _Model.getID());
					_Intent.putExtras(_Bundle);
					mContext.startActivity(_Intent);
				}
			});
		}
	}
	@Override
	public SimpleAdapterViewHolder getViewHolder(View view) {
		return new SimpleAdapterViewHolder(view,false);
	}

	public class SimpleAdapterViewHolder extends ViewHolder {
		public ArticlePostView _View;
		public SimpleAdapterViewHolder(View arg0,boolean isItem) {
			super(arg0);
			if(isItem){
				_View=(ArticlePostView)arg0;
			}
		}
	}

}
