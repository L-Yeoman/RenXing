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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bbs.BBSEveryTypeActivity;
import com.dldzkj.app.renxing.bbs.BBSTypeAdapter;
import com.dldzkj.app.renxing.bean.BBSTypeModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BBSFragment extends Fragment {
    @InjectView(R.id.list)
    GridView mRecyclerView;

    private BBSTypeAdapter myAdapter;

    private List<BBSTypeModel> actors;

    private List<BBSTypeModel> allServerData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.bbs_plate_list, container, false);
        ButterKnife.inject(this, v);

        initDatas();
        initEvents();
        WebRequst();
        return v;
    }

    private void initEvents() {
        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BBSTypeModel item=actors.get(position);
                Intent intent=new Intent(BBSFragment.this.getActivity(), BBSEveryTypeActivity.class);
                //传递id，描述，背景，名字
                intent.putExtra("broad",item);
                startActivity(intent);
            }
        });

    }

    private void initDatas() {
        actors = new ArrayList<BBSTypeModel>();
        allServerData = new ArrayList<BBSTypeModel>();
        // 初始化自定义的适配器
        myAdapter = new BBSTypeAdapter(this.getActivity(), actors);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(myAdapter);
    }

    private void WebRequst() {
        WebUtils.sendPostNoProgress(this.getActivity(), ConstantValue.GetPostsBoardList, null, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    JSONArray arrDatas = JSON.parseArray(data);
                    for (int i = 0; i < arrDatas.size(); i++) {
                        BBSTypeModel model = JSON.parseObject(arrDatas.get(i).toString(), BBSTypeModel.class);
                        allServerData.add(model);
                        actors.add(model);
                    }
                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
