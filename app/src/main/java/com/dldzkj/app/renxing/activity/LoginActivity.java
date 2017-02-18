package com.dldzkj.app.renxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.EnterActivity;
import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.SqlInfoBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/6/30.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, WebUtils.OnWebListenr {
    @InjectView(R.id.et_send_title)
    EditText etPhone;
    @InjectView(R.id.et_send_content)
    EditText etPwd;
    @InjectView(R.id.btn_login)
    TextView btnLogin;
    @InjectView(R.id.btn_forgit_pwd)
    TextView btnForgitPwd;
    @InjectView(R.id.btn_regist)
    TextView btnRegist;
    private String PassWord;
    private String UserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication.getInstance().pushActivity(this);
        ButterKnife.inject(this);
        initToolBar();
        //判断如果包含用户名，就自动填写，否则不填写
        if (SPUtils.contains(getBaseContext(), SPUtils.LOGIN_Name)) {
            String uname = (String) SPUtils.get(getBaseContext(), SPUtils.LOGIN_Name, "");
            etPhone.setText(uname);
        }
        initEvents();
    }

    private void initEvents() {
        btnLogin.setOnClickListener(this);
        btnForgitPwd.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
    }

    private void initToolBar() {
        setTitle("登陆");
        toolbar.setNavigationIcon(R.drawable.ic_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String phone = etPhone.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                PassWord = pwd;
                UserName = phone;
                if (phone.isEmpty() || pwd.isEmpty()) {
                    Toast.makeText(this, "用户名或密码不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginMethod(phone, pwd);
                break;
            case R.id.btn_forgit_pwd:
                startActivity(new Intent(this, GetVerficationCodeActivity.class).putExtra("type", GetVerficationCodeActivity.FROM_TYPE_FORGETPWD));
                break;
            case R.id.btn_regist:
                Intent intent=new Intent(this, GetVerficationCodeActivity.class);
                intent.putExtra("type", GetVerficationCodeActivity.FROM_TYPE_REGIST);
                startActivity(intent);
                break;
        }
    }

    public void loginMethod(String Mname, String Mpwd) {
        SPUtils.remove(getBaseContext(), SPUtils.LOGIN_Name);
        SPUtils.remove(getBaseContext(), "loginUpwd");
        SPUtils.put(getBaseContext(), SPUtils.LOGIN_Name, Mname);
        SPUtils.put(getBaseContext(), "loginUpwd", Mpwd);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserName", Mname);
        map.put("Password", Mpwd);
        WebUtils.sendPost(LoginActivity.this, "Login", map, LoginActivity.this);
    }

    @Override
    public void onSuccess(boolean isSuccess, String msg, String data) {
        if (isSuccess) {//取数据
            User user = JSON.parseObject(JSON.parseArray(data).get(0).toString(), User.class);
            user.setPassWord(PassWord);
            ConstantValue.Id = user.getUser_ID();
            //保存该登陆ID倒内存中
            SPUtils.saveLoginId(getBaseContext(), user.getUser_ID());
            ConstantValue.User_Name = UserName;
            SPUtils.saveLoginName(getBaseContext(),UserName);
            SPUtils.put(this, EnterActivity.BLUE_NOUSER_MODEL_KEY, false);
            try {
                SqlInfo si = SqlInfoBuilder.buildReplaceSqlInfo(MyApplication.getInstance().getDbUtils(), user);
                MyApplication.getInstance().getDbUtils().saveOrUpdate(user);
                MyApplication.getInstance().finishAct(EnterActivity.class);

            } catch (DbException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        } else {//展示错误信息
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailed(HttpException error, String msg) {
//        ConstantValue.User_Name = " ";
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
}
