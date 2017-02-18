package com.dldzkj.app.renxing.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/6/30.
 * Change by Lybin on 7/9
 */
public class ModifyPwdActivity extends BaseActivity implements View.OnClickListener {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.et_send_title)
    EditText etSendTitle;
    @InjectView(R.id.et_send_content)
    EditText etSendContent;
    @InjectView(R.id.btn_login)
    TextView btn_next;
    @InjectView(R.id.et_now_key)
    EditText oldKey;
    @InjectView(R.id.et_key_layout)
    LinearLayout mKeyLayout;
    private Boolean isChangeKey=false;
    private String phoneStr;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        MyApplication.getInstance().pushActivity(this);
        ButterKnife.inject(this);
        sourceActivity();
        initToolBar();
        initEvents();
    }

    private void initEvents() {
        btn_next.setOnClickListener(this);
    }
    private void sourceActivity() {

        if(getIntent().getAction()==null){
            return;
        }
        String s = getIntent().getAction();
        phoneStr=getIntent().getStringExtra("phoneStr");
        if (s.equals("changeKey")) {
            isChangeKey=true;
            mKeyLayout.setVisibility(View.VISIBLE);
        }
    }
    public void testKey(){

        String key = etSendTitle.getText().toString().trim();
        String againKey = etSendContent.getText().toString().trim();
        String oldKey = this.oldKey.getText().toString().trim();
        if(key.equals("")||isChangeKey&&oldKey.equals("")){
            showDialog("密码不能为空","确定");
            return;
        }
        //输入当前密码再修改
        if(isChangeKey){
            User user = null;
            try {
                user = MyApplication.getInstance().getDbUtils().findById(User.class, SPUtils.getLoginId(this));
            } catch (DbException e) {
                e.printStackTrace();
            }
            if(!oldKey.equals(user.getPassWord())){
                showDialog("密码错误","确定");
                return;
            }
        }
        //直接修改
            if(key.equals(againKey)){
                changeKey(key);
            }else{
                showDialog("重置密码不一致", "确定");
            }
    }

    public void changeKey(String s){

        HashMap<String,Object> _Map = new HashMap<String,Object>();
        _Map.put("UserName",  SPUtils.getLoginName(this));
        _Map.put("Password", s);
        WebUtils.sendPost(this, ConstantValue.UpdPassword, _Map, new WebListener());
    }


    public void showDialog(String content,final String button){
        final AppDialog dialog = new AppDialog(this, R.style.dialog, AppDialog.OVER_TYPE);
        dialog.show();
        dialog.setTitle(content);
        dialog.setSureText(button);
        dialog.setSureClickListner(new AppDialog.dialogListenner() {
            @Override
            public void setOnSureLis(Dialog d, View v) {
                if(button.equals("前往")){
                    startActivity(new Intent(ModifyPwdActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    MyApplication.getInstance().popAllActivityExceptOne(null);
                }else{
                    dialog.cancel();
                }

            }
        });
    }

    private void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setTitle("修改密码");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                testKey();
                //修改密码逻辑
                HashMap<String,Object> _Map = new HashMap<String,Object>();
                _Map.put("UserName",phoneStr);
                _Map.put("Password",etSendContent.getText().toString().trim());
                WebUtils.sendPost(this, ConstantValue.UpdPassword, _Map, new WebListener());
                break;
        }
    }

    public class WebListener implements WebUtils.OnWebListenr{

        @Override
        public void onSuccess(boolean isSuccess, String msg, String data) {
            if(isSuccess){
                showDialog("密码已修改，请重新登录","前往");
            }else{
                Toast.makeText(ModifyPwdActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailed(HttpException error, String msg) {
//            showDialog(msg,"确定");
        }
    }
}
