package com.dldzkj.app.renxing;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.activity.LoginActivity;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.SqlInfoBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;

public class SplashAct extends BaseOtherActivity {
    private long startTime;
    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long endTime = System.currentTimeMillis();
            if (endTime - startTime < 3000) {
                sendEmptyMessageDelayed(msg.what, startTime + 3000 - endTime);
                return;
            }
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent(SplashAct.this, SplashActivity.class);
                    startActivity(intent);
                    //activity 切换 动画，必需紧挨着startActivity()或者finish()函数之后调用
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
                    finish();
                    break;
                case 1:
                    Intent intent1 = new Intent(SplashAct.this, MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    //activity 切换 动画，必需紧挨着startActivity()或者finish()函数之后调用
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
                    finish();
                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        startTime = System.currentTimeMillis();
        //判断是否有登录信息
        if (SPUtils.contains(getBaseContext(), SPUtils.LOGIN_Name)) {
            String uname = (String) SPUtils.get(getBaseContext(), SPUtils.LOGIN_Name, "");
            String upwd = (String) SPUtils.get(getBaseContext(), "loginUpwd", "");
            loginMethod(uname, upwd);
        } else {
            mMainHandler.sendEmptyMessage(0);
        }
    }

    public void loginMethod(final String Mname, String Mpwd) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserName", Mname);
        map.put("Password", Mpwd);
        WebUtils.sendPostNoProgress(this, "Login", map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {//取数据
                    User user = JSON.parseObject(JSON.parseArray(data).get(0).toString(), User.class);
                    ConstantValue.Id = user.getUser_ID();
                    ConstantValue.User_Name = Mname;
                    SPUtils.saveLoginName(getBaseContext(), Mname);
                    //保存该登陆ID倒内存中
                    SPUtils.saveLoginId(getBaseContext(), user.getUser_ID());
                    SPUtils.put(getBaseContext(), EnterActivity.BLUE_NOUSER_MODEL_KEY, false);
                    try {
                        SqlInfo si = SqlInfoBuilder.buildReplaceSqlInfo(MyApplication.getInstance().getDbUtils(), user);
                        MyApplication.getInstance().getDbUtils().saveOrUpdate(user);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    mMainHandler.sendEmptyMessage(1);
                } else {//展示错误信息
                    Toast.makeText(SplashAct.this, msg, Toast.LENGTH_SHORT).show();
                    mMainHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {
                mMainHandler.sendEmptyMessage(0);
            }
        });
    }
}
