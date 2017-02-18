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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.PromptManager;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class BlueUI4 extends BaseOtherActivity implements OnClickListener {

	private MyApplication mApp;
	private ImageView iv_back;
	private ImageView btn_model_up;
	private ImageView btn_model_down;
	private ImageView btn_power_up;
	private ImageView btn_power_down;
	private ImageView iv_on_off;
	private ImageView iv_h;

	private int model = 0;
	private int power = 0;
	private TextView tv_model;
	private TextView tv_power;
	private TextView iv_battery;
	private Logo_Bg bg;
	private ImageView ui4_bg;
	private ImageView logo;
	private Context mAct;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blue4);
		mApp = (MyApplication) getApplication();
		mAct=BlueUI4.this;
		initView();
		GetLogoAdress.getFlagValue(mAct);
		
		if (GetLogoAdress.UIStr!=null) {//判断如果内存中读取到数据，就设置默认下载图片
			ui4_bg.setBackgroundDrawable(new BitmapDrawable(BmpUtils.Bytes2Bimap(GetLogoAdress.UIStr)));
		}
		if (GetLogoAdress.logoStr!=null) {
			logo.setImageBitmap(BmpUtils.Bytes2Bimap(GetLogoAdress.logoStr));
		}
		GetLogoAdress.getAdress(mApp.blueServer.getProductValue(),
				 BlueUI4.this, handler);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	private void initView() {
		ui4_bg = (ImageView) findViewById(R.id.UI4_bg);
		logo = (ImageView) findViewById(R.id.iv_logo);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		btn_model_up = (ImageView) findViewById(R.id.btn_model_up);
		btn_model_down = (ImageView) findViewById(R.id.btn_model_down);
		btn_power_up = (ImageView) findViewById(R.id.btn_power_up);
		btn_power_down = (ImageView) findViewById(R.id.btn_power_down);
		iv_on_off = (ImageView) findViewById(R.id.iv_on_off);
		iv_h = (ImageView) findViewById(R.id.iv_h);
		tv_model = (TextView) findViewById(R.id.tv_model);
		tv_power = (TextView) findViewById(R.id.tv_power);
		iv_battery = (TextView) findViewById(R.id.iv_battery);
		if (mApp.blueServer != null) {
			iv_battery.setText(String.valueOf(mApp.blueServer.getBatteryValue()));
		}else if (mApp.mChatService!=null) {
			iv_battery.setText(String.valueOf(mApp.mChatService.batterData));
		}
		
		iv_back.setOnClickListener(this);
		btn_model_up.setOnClickListener(this);
		btn_model_down.setOnClickListener(this);
		btn_power_up.setOnClickListener(this);
		btn_power_down.setOnClickListener(this);
		iv_on_off.setOnClickListener(this);
		iv_h.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iv_h.setImageResource(R.drawable.pao_press);
					isDown = true;
					handler.sendEmptyMessage(0);
					break;
				case MotionEvent.ACTION_UP:
					iv_h.setImageResource(R.drawable.pao);
					isDown = false;
					break;
				default:
					break;
				}
				return true;
			}
		});

	}

	private boolean isDown = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (isDown) {
					PromptManager.showLog("bluessss", "H");
//					mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(250));
					mApp.sendBlueOrder(250);
					sendEmptyMessageDelayed(0, 100);
				}
				break;

			case 1:
				bg = (Logo_Bg) msg.obj;

				if (bg != null) {
					setImager();
				}
				break;
			case 2:
				Toast.makeText(mApp, mAct.getResources().getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
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
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_power_down:
			if (power <= 0) {
				power = 0;
			} else {
				power--;
			}
			tv_power.setText(String.valueOf(power));
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(power + 20));
			mApp.sendBlueOrder(power+20);
			break;
		case R.id.btn_power_up:
			if (power >= 10) {
				power = 10;
			} else {
				power++;
			}
			tv_power.setText(String.valueOf(power));
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(power + 20));
			mApp.sendBlueOrder(power+20);
			break;
		case R.id.btn_model_down:
			if (model <= 0) {
				model = 0;
			} else {
				model--;
			}
			tv_model.setText(String.valueOf(model));
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(model));
			mApp.sendBlueOrder(model);
			break;
		case R.id.btn_model_up:
			if (model >= 10) {
				model = 10;
			} else {
				model++;
			}
			tv_model.setText(String.valueOf(model));
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(model));
			mApp.sendBlueOrder(model);
			break;
		case R.id.iv_on_off:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(255));
			mApp.sendBlueOrder(255);
			power = 0;
			model = 0;
			tv_model.setText(String.valueOf(model));
			tv_power.setText(String.valueOf(power));
			break;
		default:
			break;
		}

	}

	/**
	 * 綁定圖片
	 */
	public void setImager() {
		if (GetLogoAdress.sp.getString("uiAdress", "").equals(bg.getUIBackground())) {
			return;
		}
		if(bg.getUILogo() != null&& !bg.getUILogo().equals("")){

			ImageLoader.getInstance().displayImage(MyApplication.HOST_URL + bg.getUILogo(), logo, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {

				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {

				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					logo.setImageBitmap(bitmap);
					GetLogoAdress.saveObjByKey(BmpUtils.Bitmap2Bytes1(bitmap), "bg_logo");
				}

				@Override
				public void onLoadingCancelled(String s, View view) {

				}
			});
			}
		if(bg.getUIBackground() != null&& !bg.getUIBackground().equals("")){
			ImageLoader.getInstance().displayImage(MyApplication.HOST_URL + bg.getUIBackground(), ui4_bg, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {

				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {

				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					ui4_bg.setBackgroundDrawable(new BitmapDrawable(bitmap));
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
