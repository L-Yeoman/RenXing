package com.dldzkj.app.renxing.mainfragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.MyFavoritesActivity;
import com.dldzkj.app.renxing.adapter.PostStoreAdapter;
import com.dldzkj.app.renxing.bean.ArtistModel;
import com.dldzkj.app.renxing.bean.BBSModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 我收藏的帖子
 *
 * @author Administrator
 */
public class FavoritePostFragment extends Fragment implements UltimateRecyclerView.OnLoadMoreListener, OnRefreshListener{
    UltimateRecyclerView mRecycler;
    private PostStoreAdapter mAdapter;
    private boolean isRefresh = false;
    private boolean isLoad = false;;
    View footerView;
    List<BBSModel> mList;
    MyFavoritesActivity mAc;
    //private SwipeRefreshLayout mRefreshLayout;
    private ArrayList<BBSModel> mDeleteItems;

    @Override
    public void onAttach(Activity activity) {
        mAc = (MyFavoritesActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorite_store_layout, container, false);
        ButterKnife.inject(this, v);
        init(v);
        initData();
        return v;
    }

    public void init(View v) {
        /*	mRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.fsl_swipe);
			mRefreshLayout.setOnRefreshListener(this);*/
        mAdapter = new PostStoreAdapter();
        mRecycler = (UltimateRecyclerView) v.findViewById(R.id.fal_recyclerView);
        mRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mRecycler.enableLoadmore();
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_footer, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        footerView.setLayoutParams(lp);
    //    mAdapter.setCustomLoadMoreView(footerView);
        mRecycler.setOnLoadMoreListener(this);
        mRecycler.setDefaultOnRefreshListener(this);
		/*	 mRecycler.addItemDecoration(new DividerItemDecoration(this.getActivity(), R.color.text_gray,
					 DividerItemDecoration.VERTICAL_LIST, 20));*/
    }


    private void initData() {
        BitmapUtils _Utils = new BitmapUtils(getActivity());
        mList = new ArrayList<BBSModel>();
			/*	for(int i=0;i<6;i++){*/
        //	ArrayList<BBSModel> _List = new ArrayList<BBSModel>();
					/*for(int ii=0;ii<2;ii++){
						BBSModel _Model = new BBSModel();
						_Model.setTitle("如何做一个精致的女生");
						_Model.setCreateTime("3分钟前");
					//	_Model.setNicName("多乐滋");
						_Model.setReplyCount(11);
						_Model.setBoardName("爱的色放");
						mList.add(_Model);
					}*/
				/*	mList.add(_List);
				}*/
        handler.sendEmptyMessage(3);
        mAdapter.setData(mList, getActivity(),mAc);
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

    public void showDialog(final int position) {
        final AppDialog dialog = new AppDialog(getActivity(), R.style.dialog, AppDialog.OVER_TYPE);
        dialog.show();
        dialog.setTitle("移除收藏");
        dialog.setSureText("确定");
        dialog.setSureClickListner(new AppDialog.dialogListenner() {
            @Override
            public void setOnSureLis(Dialog d, View v) {
                Message msg = new Message();
                msg.what = 4;
                msg.arg1 = position;
                handler.sendMessage(msg);
                dialog.cancel();
            }
        });
    }
    private int currentId = 0;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            HashMap<String, Object> _Map;
            BBSModel _Model;
            //			ArrayList<BBSModel> _List;
            switch (msg.what) {
                case 0:
                    if (mList.size()>0) {
                        _Model = mList.get(0);
                    }else{
                        _Model = new BBSModel();
                    }
                    _Map = new HashMap<String, Object>();
                    _Map.put("UserID", SPUtils.getLoginId(getActivity()));
                    _Map.put("CID", _Model.getFavoriteID());
                    _Map.put("Type", "1");
                    _Map.put("Num", "5");
                    callService(_Map, ConstantValue.GetPostsCollection);
                    break;
                case 1:
                    _Model = mList.get(mList.size() - 1);
                    int i = Integer.parseInt(_Model.getFavoriteID());
                    if(i>=currentId&&currentId!=0){
                        handler.sendEmptyMessageDelayed(5,500);
                        return;
                    }else{
                        currentId=i;
                    }
                    _Map = new HashMap<String, Object>();
                    _Map.put("UserID",SPUtils.getLoginId(getActivity()));
                    _Map.put("CID", _Model.getFavoriteID());
                    _Map.put("Type", "2");
                    _Map.put("Num", "5");
                    callService(_Map, ConstantValue.GetPostsCollection);
                    break;
                case 3:
                    _Map = new HashMap<String, Object>();
                    _Map.put("UserID", SPUtils.getLoginId(getActivity()));
                    _Map.put("CID", "0");
                    _Map.put("Type", "0");
                    _Map.put("Num", "8");
                    callService(_Map, ConstantValue.GetPostsCollection);
                    break;
                case 4:
                    if(mAdapter.getDeleteList().size()==0){
                        mAdapter.setIsDelete(false);
                        mAdapter.notifyDataSetChanged();
                    }
                    _Map = new HashMap<String, Object>();
                  //  _Model = mList.get(msg.arg1);
                    _Map.put("FavoriteID", deleteItem());
                    webDeleteItems(_Map, ConstantValue.DeleteMyCollection);
                 /*   mList.clear();
                    handler.sendEmptyMessage(3);*/
                    break;
                case 5:
                    loadMoreView(false);
                    mAdapter.notifyDataSetChanged();
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
                Log.i("lyBin", "删除失败" + msg);
                mAdapter.setIsDelete(false);
                mAdapter.notifyDataSetChanged();
				/*mList.clear();
				handler.sendEmptyMessage(4);*/
            }

            @Override
            public void onFailed(HttpException error, String msg) {
                mAdapter.setIsDelete(false);
                mAdapter.notifyDataSetChanged();
                Log.i("lyBin", "onFailed" + msg);
            }
        });
    }


    @Override
    public void loadMore(int i, int i1) {
        isRefresh = false;
        loadMoreView(true);
        mAdapter.notifyDataSetChanged();
    }
    //控制加载条显示
    public void loadMoreView(boolean b){
        isLoad = b;
        if(b&&mList.size()>6){
            mAdapter.setCustomLoadMoreView(footerView);
            handler.sendEmptyMessage(1);
        }else if(mAdapter.getCustomLoadMoreView()!=null){
            mAdapter.setCustomLoadMoreView(null);
        }
    }
    @Override
    public void onRefresh() {
        isRefresh = true;
        handler.sendEmptyMessage(0);
    }

    private void callService(HashMap<String, Object> _Map, String method) {
        WebUtils.sendPostNoProgress(getActivity(), method, _Map, new WebListener());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

 /*   public class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fsl_delete_button:
                    handler.sendEmptyMessage(4);
                    break;
                case R.id.fsl_all_button:
                    allSelected();
                    break;
            }
        }
    }*/

    public class WebListener implements WebUtils.OnWebListenr {
        @Override
        public void onSuccess(boolean isSuccess, String msg, String data) {
            if (isSuccess) {
                ArrayList<BBSModel> _List;
                _List = (ArrayList<BBSModel>) JSON.parseArray(data, BBSModel.class);
                refreshData(_List);
                return;
            }
            if (isRefresh) {
                isRefresh = false;
                mRecycler.setRefreshing(false);
                mAdapter.notifyDataSetChanged();
                return;
            }
            if(isLoad){
                handler.sendEmptyMessageDelayed(5,500);
            }
        }

        @Override
        public void onFailed(HttpException error, String msg) {
        }
    }

    //刷新数据
    public void refreshData(ArrayList<BBSModel> list) {
        //上拉刷新
        if (isRefresh) {
            for (int i = list.size() - 1; i >= 0; i--) {
                BBSModel _Model = list.get(i);
                mList.add(0, _Model);
            }
            mRecycler.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            BBSModel _Model = list.get(i);
            mList.add(_Model);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void allSelected(){
        mDeleteItems = mAdapter.getDeleteList();
        if(mDeleteItems.size()==mList.size()){
            mDeleteItems.clear();
        }else{
            for(int i=0;i<mList.size();i++){
                BBSModel _Model = mList.get(i);
                if(!mDeleteItems.contains(_Model)){
                    mDeleteItems.add(_Model);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public String deleteItem(){
        mDeleteItems = mAdapter.getDeleteList();
        if(mDeleteItems.size()==0){
            return "";
        }
        String s = "";
        for(int i=0;i<mDeleteItems.size();i++){
            BBSModel _Model = mDeleteItems.get(i);
            s+=(_Model.getFavoriteID()+"#");
        }
    //    cancelDelete();
        return s;
    }
}
