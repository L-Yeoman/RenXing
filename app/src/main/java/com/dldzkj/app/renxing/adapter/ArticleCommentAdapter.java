package com.dldzkj.app.renxing.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.dldzkj.app.renxing.bean.ArticleCommentModel;
import com.dldzkj.app.renxing.customview.ArticleCommentView;
import com.lidroid.xutils.BitmapUtils;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.List;

public class ArticleCommentAdapter extends UltimateViewAdapter<ArticleCommentAdapter.SimpleAdapterViewHolder> {
	private Context mContext;
	private List mList;
	private BitmapUtils mUtils;
//	private int HEAD=0,ITEM=1;
	public void setData(Context c, List<ArticleCommentModel> list,BitmapUtils bUtils){
		mContext=c;
		mList=list;
		mUtils=bUtils;
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
	public void onBindViewHolder(SimpleAdapterViewHolder VH, int position) {
		if (position < getItemCount() && (customHeaderView != null ? position <= mList.size() : position < mList.size()) &&
				(customHeaderView != null ? position > 0 : true)) {
			int i = customHeaderView != null ? position - 1 : position;
			VH._View.setData((ArticleCommentModel) mList.get(i), position, mUtils);
		}
	}

	@Override
	public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup arg0) {
		return new SimpleAdapterViewHolder(new ArticleCommentView(mContext),true);
	}

	@Override
	public SimpleAdapterViewHolder getViewHolder(View view) {
		return new SimpleAdapterViewHolder(view,false);
	}

	public class SimpleAdapterViewHolder extends ViewHolder {
		private ArticleCommentView _View;
		public SimpleAdapterViewHolder(View v,boolean isItem) {
			super(v);
			if(isItem){
				_View=(ArticleCommentView) v;
			}
		}
	}


}
