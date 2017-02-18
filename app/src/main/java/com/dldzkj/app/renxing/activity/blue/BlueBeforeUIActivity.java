package com.dldzkj.app.renxing.activity.blue;

import uk.co.alt236.bluetoothlelib.util.ByteUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.EnterActivity;
import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.PromptManager;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.ActivityBleReady;
import com.dldzkj.app.renxing.activity.New_Music;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.service.UpdateSevice;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;

import java.util.HashMap;


public class BlueBeforeUIActivity extends BaseOtherActivity implements OnClickListener {
    private ImageView img_dis, logo;
    private RelativeLayout  img0, img1, img2;
    private MyApplication mApp;
    private BluetoothLeService mBluetoothLeService;
    private String product;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_blue);
        mApp = MyApplication.getInstance();
        mApp.mStack.add(this);
        img_dis = (ImageView) findViewById(R.id.setting_back);
        logo = (ImageView) findViewById(R.id.logo);
        img0 = (RelativeLayout) findViewById(R.id.img_code1);
        img1 = (RelativeLayout) findViewById(R.id.img_code2);
        img2 = (RelativeLayout) findViewById(R.id.img_code3);
        img0.setOnClickListener(this);
        img_dis.setOnClickListener(this);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        if (mApp.blueServer != null) {//蓝牙4.0
            product = mApp.blueServer.getProductValue();
            mBluetoothLeService = mApp.blueServer;
            mBluetoothLeService.initCharact();
        } else {//蓝牙3.0
            mApp.sendBlueOrder2("WNDS");
        }
        webRequest();
    }

    private void webRequest() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ProductNo", product);
        WebUtils.sendPostNoProgress(this, "GetProductUIInfoByNo", map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    String path = (String) JSON.parseObject(JSON.parseArray(data).get(0).toString()).get("UILogo");
                    Glide.with(getBaseContext()).load(WebUtils.RENXING_WEB_PICS + path).placeholder(R.drawable.ic_icon).into(logo);
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onBackPressed() {
            buidlDialog();

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(mApp, R.string.ble_disconnect, Toast.LENGTH_SHORT).show();
                boolean isNoUser= (boolean) SPUtils.get(BlueBeforeUIActivity.this, EnterActivity.BLUE_NOUSER_MODEL_KEY, false);
                if(isNoUser){
                    startActivity(new Intent(BlueBeforeUIActivity.this, ActivityBleReady.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else{
                    startActivity(new Intent(BlueBeforeUIActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                finish();
//				 tv.setText("连接已断开");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] dataArr = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_RAW);
                String dataStr = ByteUtils.byteArrayToHexString(dataArr);
                String[] data = String2Array(dataStr);
                System.out.println("数据" + dataStr);
                if ("40".equals(data[2].trim())) {//ui数据
//					tv.setText("接收到ui数据："+dataStr);
                    int zero = Integer.valueOf(data[0].trim(), 16);
                    int one = Integer.valueOf(data[1].trim(), 16);
                    if (mApp.blueServer != null) {
                        mBluetoothLeService.setUiValue(Integer.parseInt(data[3].trim()));
                        mBluetoothLeService.setProductValue(zero != 0 ? Integer.toHexString(zero) + Integer.toHexString(one) : Integer.toHexString(one));
                        mBluetoothLeService.BatteryTimer();
                    } else {
                        mApp.mChatService.uiData = Integer.parseInt(data[3].trim());
                        mApp.mChatService.productValue = zero != 0 ? Integer.toHexString(zero) + Integer.toHexString(one) : Integer.toHexString(one);
                        mApp.mChatService.BatteryTimer();
                    }
                } else if ("00".equals(data[1].trim()) && "00".equals(data[2].trim()) && "00".equals(data[3].trim())) {//电量数据
//					tv.setText("接收到电量数据："+dataStr);
                    int value = Integer.valueOf(data[0].trim(), 16);
                    if (value <= 10) {
                        Toast.makeText(mApp, R.string.low_power, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    public static String[] String2Array(String str) {
        String b = str.substring(1, str.length() - 1);
        String[] c = b.split(",");
        return c;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_back:
                buidlDialog();
                break;
            case R.id.img_code1:
                toUI();
                break;
            case R.id.img_code2:
                startActivity(new Intent(this, SceneListActivity.class));
                break;
            case R.id.img_code3:
                startActivity(new Intent(this, New_Music.class));
                break;
            default:
                break;
        }
    }
    private void buidlDialog() {
        final AppDialog dialog = new AppDialog(this, R.style.dialog, AppDialog.ASK_TYPE);
        dialog.show();
        dialog.setTitle("是否确认断开连接?");
        dialog.setSureClickListner(new AppDialog.dialogListenner() {
            @Override
            public void setOnSureLis(Dialog d, View v) {
                dialog.dismiss();
                mApp.disConnectBlue();
                Toast.makeText(mApp, R.string.ble_disconnect, Toast.LENGTH_SHORT).show();
                boolean isNoUser= (boolean) SPUtils.get(BlueBeforeUIActivity.this, EnterActivity.BLUE_NOUSER_MODEL_KEY, false);
                if(isNoUser){
                    startActivity(new Intent(BlueBeforeUIActivity.this, ActivityBleReady.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else{
                    startActivity(new Intent(BlueBeforeUIActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                finish();
            }
        });

    }
    private void toUI() {
        int uidata = 0;
        if (mApp.mChatService != null) {
            uidata = mApp.mChatService.uiData;
        }
        if (uidata == 0 && mBluetoothLeService != null) {
            uidata = mBluetoothLeService.getUiValue();
            mApp.blueServer = mBluetoothLeService;
        }
        switch (uidata) {
            case 1:
                startActivity(new Intent(this, BlueUI1.class));
                break;
            case 2:
                startActivity(new Intent(this, BlueUI2.class));
                break;
            case 3:
                startActivity(new Intent(this, BlueUI3.class));
                break;
            case 4:
                startActivity(new Intent(this, BlueUI4.class));
                break;
            case 5:
                startActivity(new Intent(this, BlueUI6New.class));
                break;
            case 6:
                startActivity(new Intent(this, BlueUI7.class));
                break;
            case 7:
                startActivity(new Intent(this, BlueUI5.class));
                break;
            default:
                PromptManager.showToast(mApp, R.string.ui_Abnormal);
                startActivity(new Intent(this, BlueUI2.class));
                break;
        }
    }
}
