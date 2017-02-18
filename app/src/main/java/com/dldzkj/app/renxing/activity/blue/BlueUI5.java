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
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;
import com.dldzkj.app.renxing.customview.CircleMenuLayout;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class BlueUI5 extends BaseOtherActivity implements OnClickListener {
	private ImageView iv_back, btn_bottom;
	private TextView iv_battery;
	private Context mAct;
	private MyApplication mApp;
	private CircleMenuLayout mCircleMenuLayout;
	private String[] mItemTexts = new String[] { "", "", "", "", "", "", "",
			"", "" };
	private int[] mItemImgs = new int[] { R.drawable.ui1_btn_bg_1,
			R.drawable.ui1_btn_bg_2, R.drawable.ui1_btn_bg_3,
			R.drawable.ui1_btn_bg_4, R.drawable.ui1_btn_bg_5,
			R.drawable.ui1_btn_bg_6, R.drawable.ui1_btn_bg_7,
			R.drawable.ui1_btn_bg_8 };
	private ImageView prePressedIv;
	private int prePos = -1;
	public static int ScreenW;
	private boolean isDown = false;
	private ImageView ui1_bg;
	private ImageView logo;
	private Logo_Bg bg;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (isDown) {
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
			default:
				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blue5);
		Display mDisplay = getWindowManager().getDefaultDisplay();
		ScreenW = mDisplay.getWidth();
		mApp = (MyApplication) getApplication();
		mAct = BlueUI5.this;
		initView();
		/********** 获取背景图资源 ********/
		GetLogoAdress.getFlagValue(this);

		if (GetLogoAdress.UIStr != null) {// 判断如果内存中读取到数据，就设置默认下载图片
			ui1_bg.setBackgroundDrawable(new BitmapDrawable(BmpUtils
					.Bytes2Bimap(GetLogoAdress.UIStr)));
		}
		if (GetLogoAdress.logoStr != null) {
			logo.setImageBitmap(BmpUtils.Bytes2Bimap(GetLogoAdress.logoStr));
		}
		GetLogoAdress.getAdress(mApp.getProductValue(), BlueUI5.this, handler);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		ui1_bg = (ImageView) findViewById(R.id.UI2_bg);
		logo = (ImageView) findViewById(R.id.ui2_logo);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		btn_bottom = (ImageView) findViewById(R.id.btn_bottom);
		iv_battery = (TextView) findViewById(R.id.iv_battery);
		if (mApp.blueServer != null) {
			iv_battery
					.setText(String.valueOf(mApp.blueServer.getBatteryValue()));
		} else if (mApp.mChatService != null) {
			iv_battery.setText(String.valueOf(mApp.mChatService.batterData));
		}
		iv_back.setOnClickListener(this);
		btn_bottom.setOnClickListener(this);
		mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.rs);
		mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);

		mCircleMenuLayout
				.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

					@Override
					public void itemClick(View view, int pos) {
						((ImageView) view)
								.setImageResource(R.drawable.ui1_btn_bg_p_1
										+ pos);
						if (prePos != -1) {
							setLastPressedDefaltBitmap();
						}
						// doSth
						mApp.sendBlueOrder(pos + 1);
						prePressedIv = (ImageView) view;
						prePos = pos;
					}

					@Override
					public void itemCenterClick(View view) {
					}

					@Override
					public boolean itemCenterTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							isDown = true;
							if (prePos != -1) {
								setLastPressedDefaltBitmap();
							}
							handler.sendEmptyMessage(0);
							break;
						case MotionEvent.ACTION_UP:
							isDown = false;
							break;
						default:
							break;
						}
						return true;
					}
				});
	}

	private void setLastPressedDefaltBitmap() {
		prePressedIv.setImageResource(R.drawable.ui1_btn_bg_1 + prePos);
	}

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
				Toast.makeText(mApp,
						mApp.getResources().getString(R.string.ble_disconnect),
						Toast.LENGTH_SHORT).show();
				startActivity(new Intent(mAct, MainActivity.class));
				finish();
				// tv.setText("连接已断开");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				final byte[] dataArr = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_RAW);
				String dataStr = ByteUtils.byteArrayToHexString(dataArr);
				String[] data = BlueBeforeUIActivity.String2Array(dataStr);
				System.out.println("数据" + dataStr);
				if ("00".equals(data[1].trim()) && "00".equals(data[2].trim())
						&& "00".equals(data[3].trim())) {// 电量数据
					int value = Integer.valueOf(data[0].trim(), 16);
					iv_battery.setText(value + "");
					if (value <= 10) {
						Toast.makeText(mApp, R.string.low_power,
								Toast.LENGTH_SHORT).show();
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
		case R.id.btn_bottom:
			if (prePos != -1) {
				setLastPressedDefaltBitmap();
			}
			mApp.sendBlueOrder(255);
			break;

		case R.id.iv_back:
			finish();
			break;
		}
	}

	public void setImager() {
		if (GetLogoAdress.sp.getString("uiAdress", "").equals(
				bg.getUIBackground())) {
			return;
		}

		if (bg.getUILogo() != null && !bg.getUILogo().equals("")) {

			ImageLoader.getInstance().displayImage(
					MyApplication.HOST_URL + bg.getUILogo(), logo,
					new ImageLoadingListener() {
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
									BmpUtils.Bitmap2Bytes1(bitmap),
									"bg_logo");
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
							ui1_bg.setBackgroundDrawable(new BitmapDrawable(
									bitmap));
							GetLogoAdress.saveObjByKey(
									BmpUtils.Bitmap2Bytes1(bitmap),
									"bg_UI");
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
}
