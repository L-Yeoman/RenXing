package com.dldzkj.app.renxing.bbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.BBSModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.PlantListModel;
import com.dldzkj.app.renxing.customview.OnRefreshListener;
import com.dldzkj.app.renxing.customview.RefreshListView;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;


public class TypeEveryTabFragment extends Fragment implements OnRefreshListener {
    private RefreshListView list;
    private TypeEveryListAdapter adapter;
    private List<PlantListModel> datas;
    private int IndexPager, BroadId;
    private final int TYPE_NUM = 10;
//    private int pid, refreshType;
    private int pagerIndex=1;

    private boolean isRefresh = false;
    private int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.demo_fragment_tab,
                container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        IndexPager = getArguments().getInt("type", 0);
        BroadId = getArguments().getInt("broad", 0);
        list = (RefreshListView) v;
        if(IndexPager!=2)
            WebRequst(1);

        initData();
//        WebRequst(pid, refreshType);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(IndexPager==2)
        WebRequst(1);
    }

    private void initData() {
        datas = new ArrayList<PlantListModel>();
        adapter = new TypeEveryListAdapter(getActivity(), datas);
        list.setAdapter(adapter);
        list.setOnRefreshListener(this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0||position==adapter.getCount()+1) return;
                TypeEveryTabFragment.this.getActivity().startActivity(new Intent(TypeEveryTabFragment.this.getActivity(), BBSDetailsActivity.class)
                        .putExtra("pid", Integer.parseInt(datas.get(position - 1).getPID())));
            }
        });
    }

    public static TypeEveryTabFragment newInstance(int type, int Broad) {
        TypeEveryTabFragment tabFragment = new TypeEveryTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("broad", Broad);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

//    private void WebRequst(int PostsID, final int Type) {
private void WebRequst(final int pageIndex) {
        /****
         *GetLatestPostsList 获取最新
         GetPostsListByBoard获取全部
         GetEssencePostsListByBoard 获取精华
         板块ID：BoardID
         帖子ID：PostsID
         获取方式：Type(0:首次加载1:下拉获取2:上拉获取)
         获取的记录数：Num
         */
        String method = "";
        Log.d("zxl", "当前index:" + IndexPager);
        switch (IndexPager) {
            case 0:
                method = ConstantValue.GetPostsListByBoard;
                break;
            case 1:
                method = ConstantValue.GetEssencePostsListByBoard;
                break;
            case 2:
                method = ConstantValue.GetLatestPostsList;
                break;
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("BoardID", BroadId);

        map.put("PageIndex", pageIndex);
        map.put("PageSize", TYPE_NUM);
//        map.put("PostsID", PostsID);
//        map.put("Type", Type);
//        map.put("Num", TYPE_NUM);
        WebUtils.sendPostNoProgress(this.getActivity(), method, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                isRefresh = false;

                if (isSuccess) {
                    JSONArray arr = JSON.parseArray(data);
                    ArrayList<PlantListModel> webData = new ArrayList<PlantListModel>();
                    for (int i = 0; i < arr.size(); i++) {
                        PlantListModel model = JSON.parseObject(arr.get(i).toString(), PlantListModel.class);
                        webData.add(model);
                    }
//                    if (Type == 2) {//加载更多
                    if (pageIndex > 1){
                        //如果是加载更多，就加到最后，否则加到最前面
                        datas.addAll(datas.size(), webData);
                    } else {
                        datas.clear();
                        datas.addAll(0, webData);
                    }
                    webData = null;
                    adapter.notifyDataSetChanged();
                } else {
//                    if(count>0)
//                    Toast.makeText(TypeEveryTabFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
                count++;
                list.hideFooterView();
                list.hideHeaderView();

            }

            @Override
            public void onFailed(HttpException error, String msg) {
                count++;
                isRefresh = false;
                list.hideFooterView();
                list.hideHeaderView();

            }
        });
    }

    @Override
    public void onDownPullRefresh() {
        if (datas.size() == 0) {
//            WebRequst(pid, refreshType);
            WebRequst(pagerIndex);
            return;
        }
        if (isRefresh) {
            list.hideHeaderView();
        } else {
//            pid = Integer.parseInt(datas.get(0).getPID());
//            refreshType = 1;
            pagerIndex=1;
            isRefresh = true;
//            WebRequst(pid, refreshType);
            WebRequst(pagerIndex);
        }

    }

    @Override
    public void onLoadingMore() {
        if (datas.size() == 0) {
//            WebRequst(pid, refreshType);
            WebRequst(pagerIndex);
            return;
        }
        if (isRefresh) {
            list.hideFooterView();
        } else {
//            pid = Integer.parseInt(datas.get(datas.size() - 1).getPID());
//            refreshType = 2;
            pagerIndex++;
            isRefresh = true;
//            WebRequst(pid, refreshType);
            WebRequst(pagerIndex);
        }
    }
}
