package com.dldzkj.app.renxing.mainfragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.ArticleActivity;
import com.dldzkj.app.renxing.adapter.ArticleListAdapter;
import com.dldzkj.app.renxing.bbs.BBSDetailsActivity;
import com.dldzkj.app.renxing.bean.ArticleModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.SlideViewModel;
import com.dldzkj.app.renxing.customview.DividerItemDecoration;
import com.dldzkj.app.renxing.customview.SlideShowView;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainArticleFragment extends android.support.v4.app.Fragment implements
		UltimateRecyclerView.OnLoadMoreListener,OnRefreshListener{
	private UltimateRecyclerView mRecyclerView;
	private ArticleListAdapter mAdapter;
	private List<ArticleModel> models;
	private BitmapUtils mUtils;
	private final float ITEM_DECORATION=10;
	private boolean isRefresh = false;
	private boolean isLoad = false;
	private boolean isSend = false;
	private boolean isNotify = false;
	private int PageIndex = 2;
	View footerView;

	@Override
	public void onResume() {
		super.onResume();
		Log.i("lyBin", "onResume");
		Log.i("lyBin",""+models.size());
		/*if(models.size()>0){
			int i = models.size()/10;
			int y = models.size()%10;
			if(y>=1){
				i++;
			}
			Log.i("lyBin",""+i);
			clearData();
			for(int ii=1;ii<=3;ii++){
				HashMap<String, Object> _Map = new HashMap<String, Object>();
				_Map.put("PageIndex",ii);
				_Map.put("PageSize", 10);
				callService(_Map, ConstantValue.GetArticleList);
			}
		}*/
	}

	private SlideShowView slide;
	private ArrayList<SlideViewModel> arrlist;

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i("Lybin", "onDestroyView");
		clearData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fragment_at, container, false);
		initView(v);
		WebRequstSlideView();
		return v;
	}
	private void initView(View v) {
		mAdapter=new ArticleListAdapter();
		mRecyclerView=(UltimateRecyclerView)v.findViewById(R.id.article_cycle);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		View _View = LayoutInflater.from(getActivity()).inflate(
				R.layout.cycle_image_layout, mRecyclerView.mRecyclerView, false);
		setAdapter();
		mRecyclerView.enableLoadmore();
		mRecyclerView.setOnLoadMoreListener(this);
		mRecyclerView.setDefaultOnRefreshListener(this);
		footerView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_footer, null);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		footerView.setLayoutParams(lp);
		//底部加载
		mAdapter.setCustomLoadMoreView(footerView);
		//轮播图
		View headerView=LayoutInflater.from(getActivity()).inflate(
				R.layout.cycle_image_layout, mRecyclerView.mRecyclerView, false);
		mRecyclerView.setNormalHeader(headerView);
		float i =(float)(BmpUtils.getwindowsWidth())*((float)460/(float)1080);
		headerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)i));
		slide= (SlideShowView) headerView.findViewById(R.id.cycle_view);
	}
	
	public void setAdapter(){
		models=new ArrayList<ArticleModel>();
		SimpleDateFormat _DateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Date _Date=new Date(System.currentTimeMillis());
		String _Time=_DateFormat.format(_Date);
		/*for(int i=0;i<=10;i++){
			ArticleModel _Model=new ArticleModel();
			_Model.setTitle("I love U , Baby");
			_Model.setSummary("我感到快乐,我感觉好热,爆满的能量就快要沸腾,关不住自由的快门 不管反正,这一刻 我要high 不能等");
			_Model.setFavor_Count(666);
			_Model.setImg_Url("http://img2.ph.126.net/m9YVUibvmu47Bwg7HEnPrQ==/6630146374955628446.jpg");
			_Model.setAdd_Time(_Time);
			models.add(_Model);
		}*/
		//得到网络数据
		getData();
		if(models!=null){
			mUtils=new BitmapUtils(getActivity());
			mAdapter.setData(models, getActivity(),mUtils);
			mRecyclerView.setAdapter(mAdapter);
		}
		int i = BmpUtils.dip2px(getActivity(),ITEM_DECORATION);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.color.White,
				DividerItemDecoration.VERTICAL_LIST, i));
	//	setListHeight();

	}
	//�����б�߶�
	/*private void setListHeight(){
		int _height=20;
		Log.i("item", mAdapter.getItemCount()+"");
		ViewGroup.LayoutParams params=mRefreshLayout.getLayoutParams();
		for(int i=0;i<mAdapter.getItemCount();i++){
			View _item = mAdapter.getView(i);
			if(_item!=null){
				_item.measure(0, 0);
				_height+=_item.getMeasuredHeight();
			}
		}
		params.height=_height+ITEM_DECORATION*(mAdapter.getItemCount()-1);
	
		mRefreshLayout.setLayoutParams(params);
	}*/

	public void getData(){
		HashMap<String, Object> _Map = new HashMap<String, Object>();
		isSend = true;
		//文章id刷新或加载时使用
	/*	_Map.put("ArticleID","0");
		_Map.put("Type","0");
		_Map.put("Num","8");*/
		_Map.put("PageIndex","1");
		_Map.put("PageSize","10");
		callService(_Map, ConstantValue.GetArticleList);
	}

	private void clearData(){
		models.clear();
		PageIndex = 2;
	}

	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			ArticleModel _Model;
			HashMap<String, Object> _Map;
			switch(msg.what){
				case 0:
				//	 _Model = models.get(0);
					 _Map = new HashMap<String, Object>();
					/*_Map.put("ArticleID",_Model.getID());
					_Map.put("Type","1");
					_Map.put("Num", "5");*/
					clearData();
					_Map.put("PageIndex","1");
					_Map.put("PageSize","10");
					callService(_Map, ConstantValue.GetArticleList);
					break;
				case 5:
					loadMoreView(false);
					isNotify=true;
					mAdapter.notifyDataSetChanged();
					break;
			}
		};
	};
	public void callService(HashMap<String,Object> _Map,String method){
		    WebUtils.sendPost(getActivity(), method, _Map, new WebListener());
	}

	//控制加载条显示
	public void loadMoreView(boolean b){
		isLoad = b;
		if(b&&models.size()>6){
			mAdapter.setCustomLoadMoreView(footerView);
		}else if(mAdapter.getCustomLoadMoreView()!=null){
			mAdapter.setCustomLoadMoreView(null);
		}
	}
	@Override
	public void loadMore(int i, int i1) {
		Log.i("lyBin","加载");
		loadMoreView(true);
		mAdapter.notifyDataSetChanged();
		if(isSend&&models.size()<=6||isNotify){
			isSend = false;
			isNotify = false;
			return;
		}
		onLoad();

	}


	@Override
	public void onRefresh() {
		if(models.size()>0){
			isRefresh = true;
			mHandler.sendEmptyMessage(0);
		}
	}
	private int currentId = 0;
	public void onLoad(){
		ArticleModel _Model;
		if(models.size()>0){
			 _Model = models.get(models.size()-1);
		}else{
			 _Model = new ArticleModel();
		}
		//防止重复刷新
		Log.v("_Model.getID()",_Model.getID()+"");
		Log.v("currentId",currentId+"");
		Log.v("PageIndex",PageIndex+"");
	/*	if(_Model.getID()>=currentId&&currentId!=0){
			mHandler.sendEmptyMessageDelayed(5,500);
			return;
		}else{
			currentId=_Model.getID();
		}*/
		HashMap<String, Object> _Map = new HashMap<String, Object>();
	/*	_Map.put("ArticleID",_Model.getID());
		_Map.put("Type","2");
		_Map.put("Num", "5");*/
		_Map.put("PageIndex",PageIndex);
		_Map.put("PageSize","10");
		PageIndex++;

			WebUtils.sendPost(getActivity(), ConstantValue.GetArticleList, _Map, new WebUtils.OnWebListenr() {
				@Override
				public void onSuccess(boolean isSuccess, String msg, String data) {
					if (isSuccess) {
						ArrayList<ArticleModel> _List;
						_List = (ArrayList<ArticleModel>) JSON.parseArray(data, ArticleModel.class);
						refreshData(_List);
						return;
					}
					mHandler.sendEmptyMessageDelayed(5, 500);
				}

				@Override
				public void onFailed(HttpException error, String msg) {
					isNotify = true;
				}
			});
	}

	public class WebListener implements WebUtils.OnWebListenr{
		@Override
		public void onSuccess(boolean isSuccess, String msg, String data) {
			if(isSuccess){
				Log.i("lyBin","onSuccess");
				ArrayList<ArticleModel> _List;
				_List=(ArrayList<ArticleModel>)JSON.parseArray(data, ArticleModel.class);
				refreshData(_List);
				return;
			}
		//	mHandler.sendEmptyMessageDelayed(5,1000);
		/*	if(isLoad){
				loadMoreView(false);
				isNotify = true;
				mAdapter.notifyDataSetChanged();
			//	mHandler.sendEmptyMessageDelayed(5,1000);
			}*/
			if(isRefresh){
				mRecyclerView.setRefreshing(false);
				mAdapter.notifyDataSetChanged();
				return;
			}
			loadMoreView(false);
			isNotify=true;
			mAdapter.notifyDataSetChanged();
		}
		@Override
		public void onFailed(HttpException error, String msg) {
		}
	}
	//刷新数据
	public void refreshData(ArrayList<ArticleModel> list){
	/*	ArticleModel model = list.get(0);
		Log.v("isRefresh",isRefresh+"");
		Log.v("currentId",currentId+"");
		Log.v("model.getID()",model.getID()+"");
		if(model.getID()<=currentId&&currentId!=0&&isRefresh){
			isRefresh = false;
			mRecyclerView.setRefreshing(false);
			mAdapter.notifyDataSetChanged();
			return;
		}else if(model.getID()>currentId){
			currentId = model.getID();
		}*/
		/*//上拉刷新
		if(isRefresh){
			for(int i=list.size()-1;i>=0;i--){
				ArticleModel _Model = list.get(i);
				models.add(0, _Model);
				mRecyclerView.setRefreshing(false);
				mAdapter.notifyDataSetChanged();
			}
			return;
		}*/
		for(int i=0;i<list.size();i++){
			ArticleModel _Model=list.get(i);
			models.add(_Model);
		}
		mAdapter.notifyDataSetChanged();
	}
	public  void showToast(String s){
		makeText(getActivity(), s, LENGTH_SHORT).show();
	}

	private void WebRequstSlideView() {
		WebUtils.sendPostNoProgress(MainArticleFragment.this.getActivity(), ConstantValue.GetHomeHot, null, new WebUtils.OnWebListenr() {
			@Override
			public void onSuccess(boolean isSuccess, String msg, String data) {
				if (isSuccess) {
					JSONArray arr=JSON.parseArray(data);
					arrlist=new ArrayList<SlideViewModel>();
					for (int i = 0; i < arr.size(); i++) {
						SlideViewModel model=JSON.parseObject(arr.get(i).toString(),SlideViewModel.class);
						arrlist.add(model);
					}
					slide.setNetData(arrlist);
					slide.setOnItemclickLis(new SlideShowView.UrlItemClickLis() {
						@Override
						public void OnUrlItemClick(int p, View v) {
							SlideViewModel item=arrlist.get(p);
							if(item.getType()==1){
								startActivity(new Intent(MainArticleFragment.this.getActivity(), BBSDetailsActivity.class).putExtra("pid",item.getTOPID()));
							}else if(item.getType()==0){
								startActivity(new Intent(MainArticleFragment.this.getActivity(), ArticleActivity.class).putExtra("",item.getTOPID()));
							}
						}
					});

				} else {
					makeText(MainArticleFragment.this.getActivity(), msg, LENGTH_SHORT).show();
				}
			}
			@Override
			public void onFailed(HttpException error, String msg) {

			}
		});
	}

}
