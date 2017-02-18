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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dldzkj.app.renxing.EnterActivity;
import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.ActivityBleReady;
import com.dldzkj.app.renxing.activity.ActivityBleScan;
import com.dldzkj.app.renxing.activity.LoginActivity;
import com.dldzkj.app.renxing.activity.blue.BlueBeforeUIActivity;
import com.dldzkj.app.renxing.bbs.BBSEveryTypeActivity;
import com.dldzkj.app.renxing.bbs.BBSTypeAdapter;
import com.dldzkj.app.renxing.bean.BBSTypeModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
/*蓝牙开始配对前*/
public class FragmentNewBLE extends Fragment {

    TextView btn_start,btn_login;
    private int currentIndex=0;
    private int[] imgIds=new int[]{R.drawable.ic_scan_img1,R.drawable.ic_scan_img2};
private ViewFlipper vf;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.fragment_new_ble, container, false);
        btn_start = (TextView) v.findViewById(R.id.btn_startBle);
        btn_login = (TextView) v.findViewById(R.id.btn_goLogin);
        vf= (ViewFlipper) v.findViewById(R.id.img);
        initDatas();
        vf.startFlipping();
        return v;
    }


    private void initDatas() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityBleScan.class));
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isNoUser= (boolean) SPUtils.get(FragmentNewBLE.this.getActivity(), EnterActivity.BLUE_NOUSER_MODEL_KEY, false);
                if(isNoUser){
                    startActivity(new Intent(FragmentNewBLE.this.getActivity(), LoginActivity.class));
                }else{
                    startActivity(new Intent(FragmentNewBLE.this.getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                FragmentNewBLE.this.getActivity().finish();
            }
        });
    }

    private void WebRequst() {

    }

}
