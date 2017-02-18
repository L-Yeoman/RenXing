package com.dldzkj.app.renxing.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.CodeModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/6/30.
 */
public class GetVerficationCodeActivity extends BaseActivity implements View.OnClickListener {

    private String sendCode;
    @InjectView(R.id.et_send_title)
    EditText etPhone;
    @InjectView(R.id.get_code)
    TextView getCode;
    @InjectView(R.id.et_send_content)
    EditText etCode;
    @InjectView(R.id.btn_login)
    TextView btn_next;
    @InjectView(R.id.btn_goLogin)
    TextView btnGoLogin;
    @InjectView(R.id.needLayout)
    LinearLayout needLayout;
    public static final int FROM_TYPE_FORGETPWD = 0;
    public static final int FROM_TYPE_REGIST = 1;
    private int fromType;
    private String phoneStr;
//    private int sex = 0;
//    private String nameStr, pwdStr, phoneStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        MyApplication.getInstance().pushActivity(this);

        ButterKnife.inject(this);
        fromType = getIntent().getIntExtra("type", FROM_TYPE_FORGETPWD);
 //       nameStr = getIntent().getStringExtra("nick");
 //       sex = getIntent().getIntExtra("sex", 0);
 //       pwdStr = getIntent().getStringExtra("pwd");

        initToolBar();
        initEvents();
    }

    private void initEvents() {
        btn_next.setOnClickListener(this);
        getCode.setOnClickListener(this);
    }

    private void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        switch (fromType) {
            case FROM_TYPE_FORGETPWD:
                setTitle("找回密码");
                needLayout.setVisibility(View.GONE);
                break;
            case FROM_TYPE_REGIST:
                setTitle("注册");
                needLayout.setVisibility(View.VISIBLE);
                btnGoLogin.setOnClickListener(this);
                break;
        }


    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册广播
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(smsReceiver);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                timer.cancel();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        phoneStr = etPhone.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_login:
                //-----------------------------------------------------------------------------------
                String codeStr = etCode.getText().toString().trim();
                if (codeStr.isEmpty() || phoneStr.isEmpty()) {
                    Toast.makeText(this, "手机号或验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtils.isMObileNO(phoneStr)) {
                    Toast.makeText(this, "手机号码格式有误，请核对", Toast.LENGTH_SHORT).show();
                    return;
                }
                //第一步判断验证码是否一致
                if (!codeStr.equals(sendCode)) {
                    Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (fromType) {
                    //找回密码
                    case FROM_TYPE_FORGETPWD:
                        startActivity(new Intent(this, ModifyPwdActivity.class).putExtra("phoneStr", phoneStr));
                        break;
                    //下一步
                    case FROM_TYPE_REGIST:
                        Intent intent=new Intent(this, RegistActivity.class);
                        intent.putExtra("phoneStr",phoneStr);
                        startActivity(intent);
                       /* HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("NicName", nameStr);
                        map.put("UserName", phoneStr);
                        map.put("Sex", sex);
                        map.put("Password", pwdStr);
                        WebUtils.sendPost(this, "Register", map, new WebUtils.OnWebListenr() {
                            @Override
                            public void onSuccess(boolean isSuccess, String msg, String data) {
                                if (isSuccess) {

                                    AppDialog dialog = new AppDialog(GetVerficationCodeActivity.this, R.style.dialog, AppDialog.OVER_TYPE);
                                    dialog.show();
                                    dialog.setTitle("注册成功，现在可以登陆了");
                                    dialog.setSureText("前往");
                                    dialog.setSureClickListner(new AppDialog.dialogListenner() {
                                        @Override
                                        public void setOnSureLis(Dialog d, View v) {
                                            startActivity(new Intent(GetVerficationCodeActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }
                                    });
                                } else {
                                    Toast.makeText(GetVerficationCodeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailed(HttpException error, String msg) {

                            }
                        });*/
                        break;
                }
               //-----------------------------------------------------------------------------------------
                break;
            case R.id.btn_goLogin:
                startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;
            //发送验证码
            case R.id.get_code:
                String method="";
                switch (fromType) {
                    case FROM_TYPE_FORGETPWD:
                        method=ConstantValue.RetPwdSenNo;
                        break;
                    case FROM_TYPE_REGIST:
                        method=ConstantValue.SendNo;
                        break;
                }
                if (phoneStr.isEmpty()) {
                    Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringUtils.isMObileNO(phoneStr)) {
                    Toast.makeText(this, "手机号码格式有误，请核对", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!WebUtils.isConnect(this)) {
                    Toast.makeText(this, "请打开网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                handler.sendEmptyMessage(0x02);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Mobile", phoneStr);
                WebUtils.sendPost(this, method, map, new WebUtils.OnWebListenr() {
                    @Override
                    public void onSuccess(boolean isSuccess, String msg, String data) {
                        if (isSuccess) {
                            String dataStr = JSON.parseArray(data).get(0).toString();
                            CodeModel code = JSON.parseObject(
                                    dataStr, CodeModel.class);
                            sendCode = code.getCode();
                            Log.d("zxl", "验证码：" + sendCode);
                        } else {
                            Toast.makeText(GetVerficationCodeActivity.this, msg, Toast.LENGTH_SHORT).show();
                            handler.sendEmptyMessage(0x01);
                        }
                    }

                    @Override
                    public void onFailed(HttpException error, String msg) {

                    }
                });
                break;
        }
    }

    CountDownTimer timer = new CountDownTimer(60 * 1000, 1 * 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            getCode.setText("(" + (millisUntilFinished / 1000) + ")秒");
        }

        @Override
        public void onFinish() {
            getCode.setText("获取验证码");
            getCode.setTextColor(getResources().getColor(R.color.bbs_bg_send_normal));
            getCode.setClickable(true);
        }
    };


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x01:
                    String codeStr = (String) msg.obj;
                    if (codeStr != null && !codeStr.isEmpty())
                        if (timer != null) timer.cancel();
                    etCode.setText(codeStr);
                    getCode.setText("获取验证码");
                    getCode.setTextColor(getResources().getColor(R.color.bbs_bg_send_normal));
                    getCode.setClickable(true);
                    break;
                case 0x02:
                    getCode.setTextColor(Color.WHITE);
                    getCode.setText("");
                    getCode.setClickable(false);
                    timer.start();
                    break;
            }
        }
    };



    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SmsMessage msg = null;
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object p : pdusObj) {
                    msg = SmsMessage.createFromPdu((byte[]) p);

                    String msgTxt = msg.getMessageBody();//短信内容

                    Date date = new Date(msg.getTimestampMillis());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String receiveTime = format.format(date);//接收时间

                    String senderNumber = msg.getOriginatingAddress();//发送者
                    Log.d("zxl", "发送者：" + senderNumber + "   接收时间：" + receiveTime);
                    Log.d("zxl", "短信内容：" + msgTxt);
                    //判断如果发送者以106开头，且短息内容包含鼎良
                    if (msgTxt.contains(StringUtils.getStrFromRes(GetVerficationCodeActivity.this, R.string.sms_key)) && senderNumber.startsWith("106")) {
                        String code = msgTxt.substring(msgTxt.length() - 4, msgTxt.length());
                        Message message = new Message();
                        message.what = 0x01;
                        message.obj = code;
                        handler.sendMessage(message);
                    } else {
                        return;
                    }
                }
                return;
            }
        }
    };

}
