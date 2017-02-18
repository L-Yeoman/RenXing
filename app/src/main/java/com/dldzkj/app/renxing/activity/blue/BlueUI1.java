package com.dldzkj.app.renxing.activity.blue;

import java.util.ArrayList;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseActivity;
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


public class BlueUI1 extends BaseOtherActivity implements OnClickListener {
	private MyApplication mApp;
	private Context mAct;
	private ImageView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7, tv_8;
	private ImageView  iv_back, iv_h, iv_off;
	private ImageView currentSelector;
	private TextView iv_battery;
	private ImageView ui1_bg;
	private ImageView logo;
	private ArrayList<ImageView> list;
	private Logo_Bg bg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blue1);
		mApp = (MyApplication) getApplication();
		mAct=BlueUI1.this;
		initView();
		GetLogoAdress.getFlagValue(this);

		if (GetLogoAdress.UIStr != null) {// 判断如果内存中读取到数据，就设置默认下载图片
			ui1_bg.setBackgroundDrawable(new BitmapDrawable(BmpUtils
					.Bytes2Bimap(GetLogoAdress.UIStr)));
		}
		if (GetLogoAdress.logoStr != null) {
			logo.setImageBitmap(BmpUtils.Bytes2Bimap(GetLogoAdress.logoStr));
		}
		GetLogoAdress.getAdress(mApp.getProductValue(), BlueUI1.this,
					handler);
		

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		ui1_bg = (ImageView) findViewById(R.id.UI1_bg);
		logo = (ImageView) findViewById(R.id.ui1_logo);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		
		tv_1 = (ImageView) findViewById(R.id.tv_1);
		tv_2 = (ImageView) findViewById(R.id.tv_2);
		tv_3 = (ImageView) findViewById(R.id.tv_3);
		tv_4 = (ImageView) findViewById(R.id.tv_4);
		tv_5 = (ImageView) findViewById(R.id.tv_5);
		tv_6 = (ImageView) findViewById(R.id.tv_6);
		tv_7 = (ImageView) findViewById(R.id.tv_7);
		tv_8 = (ImageView) findViewById(R.id.tv_8);
		iv_battery = (TextView) findViewById(R.id.iv_battery);
		if (mApp.blueServer != null) {
			iv_battery.setText(String.valueOf(mApp.blueServer.getBatteryValue()));
		}else if (mApp.mChatService!=null) {
			iv_battery.setText(String.valueOf(mApp.mChatService.batterData));
		}
		list = new ArrayList<ImageView>();
		list.add(tv_1);
		list.add(tv_2);
		list.add(tv_3);
		list.add(tv_4);
		list.add(tv_5);
		list.add(tv_6);
		list.add(tv_7);
		list.add(tv_8);

		iv_h = (ImageView) findViewById(R.id.iv_h);
		iv_off = (ImageView) findViewById(R.id.iv_off);

		tv_1.setOnClickListener(this);
		tv_2.setOnClickListener(this);
		tv_3.setOnClickListener(this);
		tv_4.setOnClickListener(this);
		tv_5.setOnClickListener(this);
		tv_6.setOnClickListener(this);
		tv_7.setOnClickListener(this);
		tv_8.setOnClickListener(this);
		iv_off.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_h.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iv_h.setImageResource(R.drawable.ui1_baofa_press);
					isDown = true;
					handler.sendEmptyMessage(0);
					break;
				case MotionEvent.ACTION_UP:
					iv_h.setImageResource(R.drawable.ui1_baofa);
					isDown = false;
					break;
				default:
					break;
				}
				return true;
			}
		});

	}

	public void setImager() {
		if (GetLogoAdress.sp.getString("uiAdress", "").equals(
				bg.getUIBackground())) {
			return;
		}

		if (bg.getUILogo() != null && !bg.getUILogo().equals("")) {

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
					GetLogoAdress.saveObjByKey(
							BmpUtils.Bitmap2Bytes1(bitmap), "bg_logo");
				}

				@Override
				public void onLoadingCancelled(String s, View view) {

				}
			});
		}
		if (bg.getUIBackground() != null && !bg.getUIBackground().equals("")) {
			ImageLoader.getInstance().displayImage(
					MyApplication.HOST_URL + bg.getUIBackground(), ui1_bg,
					new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String s, View view) {

						}

						@Override
						public void onLoadingFailed(String s, View view, FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String s, View view, Bitmap bitmap) {
							GetLogoAdress.saveObjByKey(
									BmpUtils.Bitmap2Bytes1(bitmap), "bg_UI");
							GetLogoAdress.edit.putString("uiAdress",
									bg.getUIBackground());
							GetLogoAdress.edit.putString("logoAdress",
									bg.getUILogo());
							GetLogoAdress.edit.commit();
						}

						@Override
						public void onLoadingCancelled(String s, View view) {

						}
					});
		}

	}

	private boolean isDown = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (isDown) {
					PromptManager.showLog("bluessss", "H");
					mApp.sendBlueOrder(250);
					sendEmptyMessageDelayed(0, 50);
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

	private BroadcastReceiver receiver;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_1:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(1));
			mApp.sendBlueOrder(1);
			currentSelector = (ImageView) v;
			setBack((ImageView) v);
			break;
		case R.id.tv_2:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(2));
			mApp.sendBlueOrder(2);
			currentSelector = (ImageView) v;
			setBack((ImageView) v);
			break;
		case R.id.tv_3:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(3));
			mApp.sendBlueOrder(3);
			currentSelector = (ImageView) v;
			setBack((ImageView) v);
			break;
		case R.id.tv_4:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(4));
			mApp.sendBlueOrder(4);
			currentSelector = (ImageView) v;
			setBack((ImageView) v);
			break;
		case R.id.tv_5:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(5));
			mApp.sendBlueOrder(5);
			currentSelector = (ImageView) v;
			setBack((ImageView) v);
			break;
		case R.id.tv_6:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(6));
			mApp.sendBlueOrder(6);
			currentSelector = (ImageView) v;
			setBack((ImageView) v);
			break;
		case R.id.tv_7:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(7));
			mApp.sendBlueOrder(7);
			currentSelector = (ImageView) v;
			setBack((ImageView) v);
			break;
		case R.id.tv_8:
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(8));
			mApp.sendBlueOrder(8);
			currentSelector = (ImageView) v;
			setBack((ImageView) v);
			break;
		case R.id.iv_off:
			setDefaultStyle();
//			mApp.blueServer.sendOrder(SampleGattAttributes.getOrder(255));
			mApp.sendBlueOrder(255);
			break;
		case R.id.iv_back:
			finish();
			break;
	
		}
	}

	private void setDefaultStyle() {
		if (currentSelector == null) {
			return;
		}
		setSelectorIvBg(currentSelector, false);
	}
	
	private void setSelectorIvBg(ImageView v,boolean checked){
		
		switch (v.getId()) {
		case R.id.tv_1:v.setBackgroundResource(checked?R.drawable.ui_btn_bg_p_1:R.drawable.ui_btn_bg_1);break;
		case R.id.tv_2:v.setBackgroundResource(checked?R.drawable.ui_btn_bg_p_2:R.drawable.ui_btn_bg_2);break;
		case R.id.tv_3:v.setBackgroundResource(checked?R.drawable.ui_btn_bg_p_3:R.drawable.ui_btn_bg_3);break;
		case R.id.tv_4:v.setBackgroundResource(checked?R.drawable.ui_btn_bg_p_4:R.drawable.ui_btn_bg_4);break;
		case R.id.tv_5:v.setBackgroundResource(checked?R.drawable.ui_btn_bg_p_5:R.drawable.ui_btn_bg_5);break;
		case R.id.tv_6:v.setBackgroundResource(checked?R.drawable.ui_btn_bg_p_6:R.drawable.ui_btn_bg_6);break;
		case R.id.tv_7:v.setBackgroundResource(checked?R.drawable.ui_btn_bg_p_7:R.drawable.ui_btn_bg_7);break;
		case R.id.tv_8:v.setBackgroundResource(checked?R.drawable.ui_btn_bg_p_8:R.drawable.ui_btn_bg_8);break;
		}
	}
	
	private void setOtherNormalIvBg(ImageView v){
		for (ImageView iv : list) {
			setSelectorIvBg(iv, false);
		}
	}
		private void setBack(ImageView v) {
			setOtherNormalIvBg(v);
			setSelectorIvBg(v,true);
			
		}
}