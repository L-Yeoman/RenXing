package com.dldzkj.app.renxing.mainfragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.MyFavoritesActivity;
import com.dldzkj.app.renxing.adapter.ArticleStoreAdapter;
import com.dldzkj.app.renxing.bean.ArticleModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 我收藏的帖子
 * @author Administrator
 *
 */
public class FavoriteArticleFragment extends Fragment implements UltimateRecyclerView.OnLoadMoreListener,OnRefreshListener{
	private UltimateRecyclerView mRecycler;
	public ArticleStoreAdapter mAdapter;
	private List<ArticleModel> mList;
	private List<ArticleModel>mDeleteItems;
	private boolean isRefresh = false;
	private boolean isLoad = false;
	private boolean isSend = false;
	private boolean isNotify = false;
	private View footerView;
	private SwipeRefreshLayout mRefreshLayout;
	private MyFavoritesActivity mAc;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mAc = (MyFavoritesActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			 View v=inflater.inflate(R.layout.favorite_store_layout, container, false);
			 init(v);
			 initData();
			return v;
		}
		public void init(View v){
			mAdapter=new ArticleStoreAdapter();
			mRecycler=(UltimateRecyclerView) v.findViewById(R.id.fal_recyclerView);
			mRecycler.enableLoadmore();
			mRecycler.setOnLoadMoreListener(this);
			mRecycler.setDefaultOnRefreshListener(this);
			mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),
					LinearLayoutManager.VERTICAL, false));
			footerView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_footer, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			footerView.setLayoutParams(lp);
			mAdapter.setSelect(mAc);
		//	mAdapter.setCustomLoadMoreView(footerView);
		/*	mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
					DividerItemDecoration.VERTICAL_LIST, 8));*/
		}
		
		public void initData(){
			mList=new ArrayList<ArticleModel>();
			/*for(int i=0;i<=9;i++){
				View _view	= LayoutInflater.from(getActivity()).inflate(R.layout.article_layout_store, null);
				_view.setTag(i);
				mList.add(_view);
			}*/
			isSend = true;
			handler.sendEmptyMessage(4);
			mAdapter.setData(getActivity(), mList);
			mRecycler.setAdapter(mAdapter);
		}
	public void editItem(){
		mAdapter.setIsDelete(true);
		mAdapter.notifyDataSetChanged();
	}
	public void cancelDelete(){
		mAdapter.setIsDelete(false);
		mAdapter.clearDeleteList();
		mAdapter.notifyDataSetChanged();
	}
	public void allSelected(){
		mDeleteItems = mAdapter.getDeleteList();
		if(mDeleteItems.size()==mList.size()){
			mDeleteItems.clear();
		}else{
			for(int i=0;i<mList.size();i++){
				ArticleModel _Model = mList.get(i);
				if(!mDeleteItems.contains(_Model)){
					mDeleteItems.add(_Model);
				}
			}
		}

		mAdapter.notifyDataSetChanged();
	}
		private int currentId = 0;
		public Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				HashMap<String, Object> _Map;
				ArticleModel _Model;
				switch (msg.what) {
				case 0:
					if(mList.size()==0){
						mRecycler.setRefreshing(false);
						return;
					}
					_Map= new HashMap<String, Object>();
					 _Model = mList.get(0);
					_Map.put("UserID", SPUtils.getLoginId(getActivity()));
					_Map.put("CID",_Model.getFavoriteID());
					_Map.put("Type", "1");
					_Map.put("Num", "5");
					callService(_Map, ConstantValue.GetArticleCollection);
				//	mRefreshLayout.setRefreshing(false);
					break;
					case 1:
						 _Map = new HashMap<String, Object>();
						 _Model = mList.get(mList.size()-1);
						int i = Integer.parseInt(_Model.getFavoriteID());
						if(i>=currentId&&currentId!=0){
							handler.sendEmptyMessageDelayed(5,500);
							return;
						}else{
							currentId=i;
						}
						_Map.put("UserID",SPUtils.getLoginId(getActivity()));
						_Map.put("CID",_Model.getFavoriteID());
						_Map.put("Type","2");
						_Map.put("Num","5");
						callService(_Map, ConstantValue.GetArticleCollection);
					break;
					case 3:
						if(mAdapter.getDeleteList().size()==0){
							mAdapter.setIsDelete(false);
							mAdapter.notifyDataSetChanged();
						}
						_Map = new HashMap<String, Object>();
						_Map.put("FavoriteID", deleteItem());
						webDeleteItems(_Map, ConstantValue.DeleteMyCollection);
					break;
					case 4:
						 _Map = new HashMap<String, Object>();
						_Map.put("UserID",SPUtils.getLoginId(getActivity()));
						_Map.put("CID","0");
						_Map.put("Type","0");
						_Map.put("Num","8");
						callService(_Map, ConstantValue.GetArticleCollection);
						break;
					case 5:
						loadMoreView(false);
						isNotify=true;
						mAdapter.notifyDataSetChanged();
						break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	public void webDeleteItems(HashMap<String,Object> _Map,String method){
		WebUtils.sendPostNoProgress(getActivity(), method, _Map, new WebUtils.OnWebListenr() {
			@Override
			public void onSuccess(boolean isSuccess, String msg, String data) {
				if(isSuccess){
					mAdapter.deleteItem();
					mAdapter.setIsDelete(false);
					mAdapter.notifyDataSetChanged();
					return;
				}
				Log.i("lyBin","删除失败"+msg);
				mAdapter.setIsDelete(false);
				mAdapter.notifyDataSetChanged();
				/*mList.clear();
				handler.sendEmptyMessage(4);*/
			}

			@Override
			public void onFailed(HttpException error, String msg) {
				Log.i("lyBin","onFailed"+msg);
				mAdapter.setIsDelete(false);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	public String deleteItem(){
		mDeleteItems = mAdapter.getDeleteList();
		if(mDeleteItems.size()==0){
			return "";
		}
		String s = "";
		for(int i=0;i<mDeleteItems.size();i++){
			ArticleModel _Model = mDeleteItems.get(i);
			s+=(_Model.getFavoriteID()+"#");
		}
	//	cancelDelete();
		return s;
	}

	@Override
	public void loadMore(int i, int i1) {
	/*	isRefresh = false;
		loadMoreView(true);
		mAdapter.notifyDataSetChanged();*/
		Log.i("loadMore", "isLoad");
		isRefresh = false;
		loadMoreView(true);
		mAdapter.notifyDataSetChanged();
		/*if(isSend&&mList.size()<=6||isNotify){
			isSend = false;
			isNotify = false;
			loadMoreView(false);
			mAdapter.notifyDataSetChanged();
			return;
		}*/
		handler.sendEmptyMessage(1);

	}
	//控制加载条显示
	public void loadMoreView(boolean b){
		isLoad = b;
		if(b&&mList.size()>6){
			mAdapter.setCustomLoadMoreView(footerView);
			Log.i("loadMoreView", "加载");
		}else if(mAdapter.getCustomLoadMoreView()!=null){
			mAdapter.setCustomLoadMoreView(null);
			Log.i("loadMoreView", "加载空");
		}
	}
	@Override
	public void onRefresh() {
		isRefresh = true;
		handler.sendEmptyMessage(0);
	}
	public void showDialog( final int position) {
		final AppDialog dialog = new AppDialog(getActivity(), R.style.dialog, AppDialog.OVER_TYPE);
		dialog.show();
		dialog.setTitle("移除收藏");
		dialog.setSureText("确定");
		dialog.setSureClickListner(new AppDialog.dialogListenner() {
			@Override
			public void setOnSureLis(Dialog d, View v) {
				/*mList.remove(position);
				notifyDataSetChanged();*/
				Message msg = new Message();
				msg.what = 3;
				msg.arg1 = position;
				handler.sendMessage(msg);
				dialog.cancel();
			}
		});
	}
	/*public void test(String s){
		Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
	}*/

	private void callService(HashMap<String,Object> _Map,String method){
		WebUtils.sendPostNoProgress(getActivity(), method, _Map, new WebListener());
	}

	public class WebListener implements WebUtils.OnWebListenr{
		@Override
		public void onSuccess(boolean isSuccess, String msg, String data) {
			if(isSuccess){
				ArrayList<ArticleModel> _List;
				_List=(ArrayList<ArticleModel>) JSON.parseArray(data, ArticleModel.class);
				refreshData(_List);
				return;
			}
			if(isRefresh){
				Log.i("lyBIn","isRefresh");
				isRefresh = false;
				mRecycler.setRefreshing(false);
				mAdapter.notifyDataSetChanged();
				return;
			}
			if(isLoad){
				Log.i("lyBIn","isLoad");
				handler.sendEmptyMessageDelayed(5,500);
			}
		}
		@Override
		public void onFailed(HttpException error, String msg) {
		}
	}
	//刷新数据
	public void refreshData(ArrayList<ArticleModel> list){
		//上拉刷新
		if(isRefresh){
			for(int i=list.size()-1;i>=0;i--){
				ArticleModel _Model = list.get(i);
				mList.add(0, _Model);
			}
			mRecycler.setRefreshing(false);
			mAdapter.notifyDataSetChanged();
			return;
		}
		for(int i=0;i<list.size();i++){
			ArticleModel _Model=list.get(i);
			mList.add(_Model);
		}
		mAdapter.notifyDataSetChanged();
	}
}
