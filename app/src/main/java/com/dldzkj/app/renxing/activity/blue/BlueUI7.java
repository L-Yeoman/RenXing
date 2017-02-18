package com.dldzkj.app.renxing.activity.blue;

import uk.co.alt236.bluetoothlelib.util.ByteUtils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;


public class BlueUI7 extends  Activity implements OnClickListener{

	private TextView tv_1;
	private TextView tv_2;
	private TextView tv_3;
	private TextView tv_4;
	private Logo_Bg bg;
	private ImageView iv_off;//关机�?	
	private TextView iv_battery;//电量
	private ImageView iv_back;//返回�?	
	private ImageView iv_logo;//logo
	private ImageView iv_bg;//背景�?	
	private ImageView btn1_add,btn1_manus,btn2_add,btn2_manus,btn3_add,btn3_manus,btn4_add,btn4_manus;
	private int value1,value2,value3,value4;
	private MyApplication mApp;
	private Context mAct;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blue7);
		mApp = (MyApplication) getApplication();
		mAct=BlueUI7.this;
		initView();
		GetLogoAdress.getFlagValue(mAct);
		if (GetLogoAdress.UIStr!=null) {//判断如果内存中读取到数据，就设置默认下载图片
			iv_bg.setBackgroundDrawable(new BitmapDrawable(BmpUtils.Bytes2Bimap(GetLogoAdress.UIStr)));
		}
		if (GetLogoAdress.logoStr!=null) {
			iv_logo.setImageBitmap(BmpUtils.Bytes2Bimap(GetLogoAdress.logoStr));
		}
		GetLogoAdress.getAdress(mApp.blueServer.getProductValue(),
				 BlueUI7.this, handler);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	private void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_1 = (TextView) findViewById(R.id.tv_model1);
		tv_2 = (TextView) findViewById(R.id.tv_model2);
		tv_3 = (TextView) findViewById(R.id.tv_model3);
		tv_4 = (TextView) findViewById(R.id.tv_model4);
		iv_battery = (TextView) findViewById(R.id.iv_battery);
		if (mApp.blueServer != null) {
			iv_battery.setText(String.valueOf(mApp.blueServer.getBatteryValue()));
		}else if (mApp.mChatService!=null) {
			iv_battery.setText(String.valueOf(mApp.mChatService.batterData));
		}
		iv_logo= (ImageView) findViewById(R.id.iv_logo);
		iv_bg= (ImageView) findViewById(R.id.bg_layout);
		
		btn1_add= (ImageView) findViewById(R.id.btn_model1_up);
		btn1_manus= (ImageView) findViewById(R.id.btn_model1_down);
		btn2_add= (ImageView) findViewById(R.id.btn_model2_up);
		btn2_manus= (ImageView) findViewById(R.id.btn_model2_down);
		btn3_add= (ImageView) findViewById(R.id.btn_model3_up);
		btn3_manus= (ImageView) findViewById(R.id.btn_model3_down);
		btn4_add= (ImageView) findViewById(R.id.btn_model4_up);
		btn4_manus= (ImageView) findViewById(R.id.btn_model4_down);

		iv_off = (ImageView) findViewById(R.id.iv_off);

		iv_off.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		btn1_manus.setOnClickListener(this);
		btn1_add.setOnClickListener(this);
		btn2_manus.setOnClickListener(this);
		btn2_add.setOnClickListener(this);
		btn3_manus.setOnClickListener(this);
		btn3_add.setOnClickListener(this);
		btn4_manus.setOnClickListener(this);
		btn4_add.setOnClickListener(this);
		

	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				
				break;
			case 1:
				bg = (Logo_Bg) msg.obj;

				if (bg != null) {
					setImager();
				}
				break;
			case 2:
				Toast.makeText(mApp, mAct.getResources().getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
				startActivity(new Intent(BlueUI7.this, MainActivity.class));
				finish();
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(mApp, mApp.getResources().getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
				startActivity(new Intent(mAct, MainActivity.class));
				finish();
//				 tv.setText("连接已断开");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				final byte[] dataArr = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_RAW);
				String dataStr=ByteUtils.byteArrayToHexString(dataArr);
				String[] data=BlueBeforeUIActivity.String2Array(dataStr);
				System.out.println("数据"+dataStr);
				 if("00".equals(data[1].trim())&&"00".equals(data[2].trim())&&"00".equals(data[3].trim())){//电量数据
					 int value=Integer.valueOf(data[0].trim(),16);
					 iv_battery.setText(value+"");
						if (value<=10) {
							Toast.makeText(mApp, R.string.low_power, Toast.LENGTH_SHORT).show();
						}
				}
			}
		}
	};
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_model1_up:
			if (value1==10) {
				return;
			}else {
				value1++;
			}
			tv_1.setText(value1+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(value1));
			mApp.sendBlueOrder(value1);
			
			break;
		case R.id.btn_model1_down:
			if (value1==0) {
				return;
			}else {
				value1--;
			}
			tv_1.setText(value1+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(value1));
			mApp.sendBlueOrder(value1);
			break;
		case R.id.btn_model2_up:
			if (value2==1) {
				return;
			}else {
				value2++;
			}
			tv_2.setText(value2+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(value2+20));
			mApp.sendBlueOrder(value2+20);
		
			break;
		case R.id.btn_model2_down:
			if (value2==0) {
				return;
			}else {
				value2--;
			}
			tv_2.setText(value2+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(value2+20));
			mApp.sendBlueOrder(value2+20);
			break;
		case R.id.btn_model3_up:
			if (value3==10) {
				return;
			}else {
				value3++;
			}
			tv_3.setText(value3+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(value3+40));
			mApp.sendBlueOrder(value3+40);
			
			break;
		case R.id.btn_model3_down:
			if (value3==0) {
				return;
			}else {
				value3--;
			}
			tv_3.setText(value3+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(value3+40));
			mApp.sendBlueOrder(value3+40);
			break;
		case R.id.btn_model4_up:
			if (value4==1) {
				return;
			}else {
				value4++;
			}
			tv_4.setText(value4+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(value4+60));
			mApp.sendBlueOrder(value4+60);
			break;
		case R.id.btn_model4_down:
			if (value4==0) {
				return;
			}else {
				value4--;
			}
			tv_4.setText(value4+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(value4+60));
			mApp.sendBlueOrder(value4+60);
		
			break;

		case R.id.iv_off:
			value1=value2=value3=value4=0;
			tv_1.setText(0+"");
			tv_2.setText(0+"");
			tv_3.setText(0+"");
			tv_4.setText(0+"");
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(255));
			mApp.sendBlueOrder(255);
			break;
		
		case R.id.iv_back:
			finish();
			break;
		}
	}
	public void setImager() {
		if (GetLogoAdress.sp.getString("uiAdress", "").equals(bg.getUIBackground())) {
			return;
		}
		if(bg.getUILogo() != null&& !bg.getUILogo().equals("")){

			ImageLoader.getInstance().displayImage(MyApplication.HOST_URL + bg.getUILogo(), iv_logo, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {

				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {

				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					iv_logo.setImageBitmap(bitmap);
					GetLogoAdress.saveObjByKey(BmpUtils.Bitmap2Bytes1(bitmap), "bg_logo");
				}

				@Override
				public void onLoadingCancelled(String s, View view) {

				}
			});
			}
		if(bg.getUIBackground() != null&& !bg.getUIBackground().equals("")){
			ImageLoader.getInstance().displayImage(MyApplication.HOST_URL + bg.getUIBackground(), iv_bg, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {

				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {

				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					iv_bg.setBackgroundDrawable(new BitmapDrawable(bitmap));
					GetLogoAdress.saveObjByKey(BmpUtils.Bitmap2Bytes1(bitmap), "bg_UI");
					GetLogoAdress.edit.putString("uiAdress", bg.getUIBackground());
					GetLogoAdress.edit.putString("logoAdress", bg.getUILogo());
					GetLogoAdress.edit.commit();
				}

				@Override
				public void onLoadingCancelled(String s, View view) {

				}
			});
		}

	}

}
