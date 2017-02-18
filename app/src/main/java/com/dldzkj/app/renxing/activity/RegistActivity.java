package com.dldzkj.app.renxing.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/6/30.
 */
public class RegistActivity extends BaseActivity implements View.OnClickListener {


    @InjectView(R.id.et_send_title)
    EditText etSendTitle;
    @InjectView(R.id.et_send_content)
    EditText etSendContent;
    @InjectView(R.id.et_send_content2)
    EditText etSendContent2;
    @InjectView(R.id.btn_login)
    TextView btnNext;
    @InjectView(R.id.radioGroup)
    RadioGroup radioGroup;
    private int sex = 0;
    private String nameStr, pwdStr, pwdStr2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_info);
        MyApplication.getInstance().pushActivity(this);
        ButterKnife.inject(this);
        initToolBar();
        initEvents();
    }

    private void initEvents() {
        btnNext.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) RegistActivity.this.findViewById(checkedId);
                if (rb.getText().equals("男")) {
                    sex = 1;
                } else {
                    sex = 0;
                }
            }
        });
        etSendContent2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    btnNext.performClick();
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setTitle("注册");

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
                nameStr=etSendTitle.getText().toString();
                pwdStr=etSendContent.getText().toString();
                pwdStr2=etSendContent2.getText().toString();
                if (nameStr.isEmpty()){
                    Toast.makeText(this,"昵称不可为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwdStr.isEmpty()){
                    Toast.makeText(this,"密码不可为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwdStr.equals(pwdStr2)){
                    Toast.makeText(this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }
                String phoneStr = (String) getIntent().getStringExtra("phoneStr");
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("NicName", nameStr);
                map.put("UserName", phoneStr);
                map.put("Sex", sex);
                map.put("Password", pwdStr);
                WebUtils.sendPost(this, "Register", map, new WebUtils.OnWebListenr() {
                    @Override
                    public void onSuccess(boolean isSuccess, String msg, String data) {
                        if (isSuccess) {
                            AppDialog dialog = new AppDialog(RegistActivity.this, R.style.dialog, AppDialog.OVER_TYPE);
                            dialog.show();
                            dialog.setTitle("注册成功，现在可以登陆了");
                            dialog.setSureText("前往");
                            dialog.setSureClickListner(new AppDialog.dialogListenner() {
                                @Override
                                public void setOnSureLis(Dialog d, View v) {
                                    startActivity(new Intent(RegistActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }
                            });
                        } else {
                            Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(HttpException error, String msg) {

                    }
                });
                /*//判断是否为空
                Intent intent=new Intent(this, GetVerficationCodeActivity.class);
                intent.putExtra("type", GetVerficationCodeActivity.FROM_TYPE_REGIST);
                intent.putExtra("nick", nameStr);
                intent.putExtra("sex", sex);
                intent.putExtra("pwd", pwdStr);
                startActivity(intent);*/
                break;

        }
    }
}
