package com.dldzkj.app.renxing;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.activity.ActivityBleReady;
import com.dldzkj.app.renxing.activity.ActivityBleScan;
import com.dldzkj.app.renxing.activity.GetVerficationCodeActivity;
import com.dldzkj.app.renxing.activity.LoginActivity;
import com.dldzkj.app.renxing.activity.RegistActivity;
import com.dldzkj.app.renxing.activity.blue.ScanActivity;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.SqlInfoBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;

public class EnterActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn1, btn2;
public static String  BLUE_NOUSER_MODEL_KEY="sp_is_no_user";
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MyApplication.getInstance().pushActivity(this);
        setContentView(R.layout.activity_enter);
        btn1 = (ImageView) findViewById(R.id.btn_one);
        btn2 = (ImageView) findViewById(R.id.btn_two);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        SPUtils.remove(this,BLUE_NOUSER_MODEL_KEY);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_one:
                startActivity(new Intent(this, ActivityBleReady.class));
                SPUtils.put(this, BLUE_NOUSER_MODEL_KEY, true);
                break;
            case R.id.btn_two:
                Intent intent=new Intent(this, LoginActivity.class);
//                intent.putExtra("type", GetVerficationCodeActivity.FROM_TYPE_REGIST);
                startActivity(intent);
                break;
        }
    }
}
