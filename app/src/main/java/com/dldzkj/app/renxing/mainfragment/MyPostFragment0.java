/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dldzkj.app.renxing.mainfragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bbs.BBSDetailsActivity;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.MyPostBBS;
import com.dldzkj.app.renxing.customview.OnRefreshListener;
import com.dldzkj.app.renxing.customview.RefreshListView;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MyPostFragment0 extends Fragment implements OnRefreshListener,View.OnClickListener {


    @InjectView(R.id.list)
    RefreshListView recyclerview;
    @InjectView(R.id.select_all)
    TextView selectAll;
    @InjectView(R.id.delete_btn)
    TextView deleteBtn;
    @InjectView(R.id.bottom)
    LinearLayout bottom;
    private ArrayList<MyPostBBS> datas;
    private BBSMainAdapter2 adapter;
    private String paramsPid = "0", paramsType = "0";
    private boolean isRefresh = false;
    private final int paramsNum = 10;
    private boolean btnEnable = true;
    private Map<Integer, Boolean> isCheckedMap;
    private MenuItem menuDelete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_only_recyclerview, container, false);
        ButterKnife.inject(this, v);

        initDatas();
        deleteBtn.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    private void initDatas() {
        isCheckedMap = new HashMap<Integer, Boolean>();
        datas = new ArrayList<MyPostBBS>();
       /* for (int i = 0; i < 30; i++) {
            BBSModel model = new BBSModel();
            model.setBoardID(i / 7);
            model.setTitle("帖子标题" + i);
            model.setAddTime(i + "分钟前");
            model.setStates(i%2);
            data.add(model);
        }*/
        adapter = new BBSMainAdapter2(getActivity(), datas);
        recyclerview.setAdapter(adapter);
        recyclerview.setOnRefreshListener(this);
        recyclerview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == adapter.getCount() + 1)
                    return;//排除headerView和下拉刷新以及上拉加载更多
                if (!adapter.isDeleteMode()) {
                    startActivity(new Intent(MyPostFragment0.this.getActivity(), BBSDetailsActivity.class).putExtra("pid", Integer.parseInt(datas.get(position - 1).getPID())));
                }
            }
        });
        WebRequst(paramsPid, paramsType);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sample_actions, menu);
        menuDelete = menu.findItem(R.id.action_settings);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (!btnEnable) {
                    return true;
                }
                if (datas.size() == 0) {
                    Toast.makeText(this.getActivity(), "暂无帖子", Toast.LENGTH_SHORT).show();
                } else {
                    if (adapter.isDeleteMode()) {//点击取消
                        item.setIcon(R.drawable.ic_delede);
                        item.setTitle("删除");
                        bottom.setVisibility(View.GONE);
                        adapter.setDeleteMode(false);
                    } else {//点击垃圾桶
                        item.setIcon(null);
                        item.setTitle("取消");
                        bottom.setVisibility(View.VISIBLE);
                        adapter.setDeleteMode(true);
                    }
                    adapter.notifyDataSetChanged();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void WebRequst(String PostsID, final String Type) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserID", SPUtils.getLoginId(getActivity()));
        map.put("PostsID", PostsID);
        map.put("Type", Type);
        map.put("Num", paramsNum);
        WebUtils.sendPostNoProgress(this.getActivity(), ConstantValue.GetMyPostsList, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                isRefresh = false;
                if (isSuccess) {
                    JSONArray arr = JSON.parseArray(data);
                    ArrayList<MyPostBBS> webData = new ArrayList<MyPostBBS>();
                    for (int i = 0; i < arr.size(); i++) {
                        MyPostBBS model = JSON.parseObject(arr.get(i).toString(), MyPostBBS.class);
                        isCheckedMap.put(Integer.parseInt(model.getPID()), false);
                        webData.add(model);
                    }
                    if (Type.equals("2")) {//加载更多
                        //如果是加载更多，就加到最后，否则加到最前面
                        datas.addAll(datas.size(), webData);
                    } else {
                        datas.addAll(0, webData);
                    }
                    webData = null;
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyPostFragment0.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
                if (Type.equals("2")) {//加载更多
                    recyclerview.hideFooterView();
                } else {
                    recyclerview.hideHeaderView();
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {
                isRefresh = false;
                recyclerview.hideFooterView();
                recyclerview.hideHeaderView();

            }
        });
    }
    private MyPostBBS getModelByRid(int rid){
        for (MyPostBBS item:datas) {
            if(Integer.parseInt(item.getPID())==rid){
                return item;
            }
        }
        return null;
    }
    private void WebRequstDeletePost(String PostsID) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("PostsID", PostsID);
        WebUtils.sendPostNoProgress(this.getActivity(), ConstantValue.DeleteMyPosts, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                btnEnable = true;
                menuDelete.setIcon(R.drawable.ic_delede);
                menuDelete.setTitle("删除");
                adapter.setDeleteMode(false);
                bottom.setVisibility(View.GONE);
                if (isSuccess) {
                    if(deleteList!=null&&deleteList.size()>0){
                        for (Integer pid:deleteList) {
                            MyPostBBS tempModel=getModelByRid(pid);
                            if(tempModel!=null) {
                                datas.remove(tempModel);
                                isCheckedMap.remove(pid);
                            }
                        }
                        deleteList.clear();
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MyPostFragment0.this.getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyPostFragment0.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {
                btnEnable = true;
                menuDelete.setIcon(R.drawable.ic_delede);
                menuDelete.setTitle("删除");
                adapter.setDeleteMode(false);
                bottom.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDownPullRefresh() {
        if (adapter.isDeleteMode()) {
            recyclerview.hideHeaderView();
            Toast.makeText(this.getActivity(), "请先退出编辑模式", Toast.LENGTH_SHORT).show();
            return;
        }
        if (datas.size() == 0) {
            WebRequst(paramsPid, paramsType);
            return;
        }
        if (isRefresh) {
            recyclerview.hideHeaderView();
        } else {
            paramsPid = datas.get(0).getPID();
            paramsType = "1";
            isRefresh = true;
            WebRequst(paramsPid, paramsType);
        }
    }

    @Override
    public void onLoadingMore() {
        if (adapter.isDeleteMode()) {
            recyclerview.hideFooterView();
            Toast.makeText(this.getActivity(), "请先退出编辑模式", Toast.LENGTH_SHORT).show();
            return;
        }
        if (datas.size() == 0) {
            WebRequst(paramsPid, paramsType);
            return;
        }
        if (isRefresh) {
            recyclerview.hideFooterView();
        } else {
            paramsPid = datas.get(datas.size() - 1).getPID();
            paramsType = "2";
            isRefresh = true;
            WebRequst(paramsPid, paramsType);
        }
    }
    private List<Integer> deleteList;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_btn:
                if (deleteList == null)
                    deleteList = new ArrayList<Integer>();
                for (Integer key : isCheckedMap.keySet()) {
                    if (isCheckedMap.get(key)) {
                        deleteList.add(key);
                    }
                }
                if (deleteList.size() == 0) {//同点击取消
                    menuDelete.setIcon(R.drawable.ic_delede);
                    menuDelete.setTitle("删除");
                    bottom.setVisibility(View.GONE);
                    adapter.setDeleteMode(false);
                    return;
                }
                StringBuffer sb = new StringBuffer();
                for (Integer key : deleteList) {
                    sb.append(key + "#");
                }
                String pids = sb.substring(0, sb.length() - 1);
                Log.d("zxl", "删除的pids:" + pids);
                btnEnable = false;
                WebRequstDeletePost(pids);

                break;
            case R.id.select_all:
                Set<Integer> set = isCheckedMap.keySet();
                Iterator<Integer> iterator = set.iterator();
                if (selectAll.getText().equals("全选")) {
                    while (iterator.hasNext()) {
                        Integer keyId = iterator.next();
                        isCheckedMap.put(keyId, true);
                    }
                    selectAll.setText("全不选");
                    deleteBtn.setTextColor(Color.parseColor("#eb5959"));
                } else {
                    while (iterator.hasNext()) {
                        Integer keyId = iterator.next();
                        isCheckedMap.put(keyId, false);
                    }
                    selectAll.setText("全选");
                    deleteBtn.setTextColor(Color.parseColor("#666666"));
                }
                adapter.notifyDataSetChanged();
                break;

        }
    }

    class BBSMainAdapter2 extends BaseAdapter {
        private List<MyPostBBS> datas;
        private Context context;
        private boolean isDeleteMode;
        public BBSMainAdapter2(Context context,List<MyPostBBS> datas) {
            super();
            this.context=context;
            this.datas=datas;
        }

        public boolean isDeleteMode() {
            return isDeleteMode;
        }

        public void setDeleteMode(boolean isDeleteMode){
            this.isDeleteMode=isDeleteMode;
        }
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            MyViewHolder holder = null;
            if (convertView == null) {
                convertView=LayoutInflater.from(context).inflate(R.layout.bbs_main_item, parent,
                        false);
                holder=new MyViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (MyViewHolder)convertView.getTag();
            }
            setItemData(holder,position);
            return convertView;
        }



        class MyViewHolder {
            TextView tv_itemTitle;
            TextView tv_itemTime;
            TextView tv_itemState;
            CheckBox cb;

            public MyViewHolder(View convertView) {
                cb= (CheckBox) convertView.findViewById(R.id.checkbox);
                tv_itemTitle = (TextView) convertView.findViewById(R.id.bbs_item_title);
                tv_itemTime = (TextView) convertView.findViewById(R.id.bbs_item_time);
                tv_itemState = (TextView) convertView.findViewById(R.id.bbs_item_state);
            }
        }

        private void setItemData(final MyViewHolder holder, final int position) {

            final MyPostBBS mContent = datas.get(position);
            holder.tv_itemTitle.setText(StringUtils.DecodeURLStr(mContent.getTitle()));
            holder.tv_itemTime.setText(mContent.getAddTime());
            final int id=Integer.parseInt(mContent.getPID());
            switch (Integer.parseInt(mContent.getEnabled())) {
                case 1:
                    holder.tv_itemState.setText("已审核");
                    break;
                case 0:
                    holder.tv_itemState.setText("未审核");
                    holder.tv_itemState.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_audited_not), null, null, null);
                    holder.tv_itemState.setTextColor(context.getResources().getColor(R.color.app_gray_font));
                    break;
            }

            if (isDeleteMode){
                holder.cb.setVisibility(View.VISIBLE);
                holder.cb.setChecked(isCheckedMap.get(id));
                holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            isCheckedMap.put(id, true);
                        } else {
                            isCheckedMap.put(id, false);
                        }
                    }
                });
            }else{
                holder.cb.setVisibility(View.GONE);
            }
        }

    }

}
