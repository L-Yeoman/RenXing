package com.dldzkj.app.renxing.activity.blue;

import uk.co.alt236.bluetoothlelib.util.ByteUtils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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


public class BlueUI6New extends BaseOtherActivity implements OnClickListener {
	private TextView tv_strong;
	private ImageView iv_back, btn_add, btn_manus;
	private TextView iv_battery;
	public static int strong_value = 0;
	public static boolean btnCanClick = false;
	private Context mAct;
	private MyApplication mApp;
	private CircleMenuLayout mCircleMenuLayout;
	private String[] mItemTexts = new String[] { "", "", "", "", "", "", "",
			"", "" };
	private int[] mItemImgs = new int[] { R.drawable.ui_6u_1,
			R.drawable.ui_6u_2, R.drawable.ui_6u_3, R.drawable.ui_6u_4,
			R.drawable.ui_6u_5, R.drawable.ui_6u_6, R.drawable.ui_6u_7,
			R.drawable.ui_6u_8 };
	private ImageView prePressedIv;
	private int prePos = -1;
	public static int ScreenW;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blue6_new);
		Display mDisplay = getWindowManager().getDefaultDisplay();
		ScreenW = mDisplay.getWidth();
		mApp = (MyApplication) getApplication();
		mAct = BlueUI6New.this;
		initView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_strong = (TextView) findViewById(R.id.tv_power);
		btn_manus = (ImageView) findViewById(R.id.btn_manus);
		btn_add = (ImageView) findViewById(R.id.btn_add);
		iv_battery = (TextView) findViewById(R.id.iv_battery);
		if (mApp.blueServer != null) {
			iv_battery.setText(String.valueOf(mApp.blueServer.getBatteryValue()));
		}else if (mApp.mChatService!=null) {
			iv_battery.setText(String.valueOf(mApp.mChatService.batterData));
		}
		iv_back.setOnClickListener(this);
		btn_manus.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.rs);
		mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);

		mCircleMenuLayout
				.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

					@Override
					public void itemClick(View view, int pos) {
						((ImageView) view).setImageResource(R.drawable.ui_6us_1
								+ pos);
						if (prePos != -1) {
							setLastPressedDefaltBitmap();
						}
						// doSth
						btnCanClick = true;
						strong_value = 0;
						tv_strong.setText("0");
						mApp.sendBlueOrder(pos + 1);
						prePressedIv = (ImageView) view;
						prePos = pos;
					}

					@Override
					public void itemCenterClick(View view) {
						if (prePos != -1) {
							setLastPressedDefaltBitmap();
						}
						btnCanClick = false;
						strong_value = 0;
						tv_strong.setText("0");
						mApp.sendBlueOrder(255);
					}

					@Override
					public boolean itemCenterTouch(View view, MotionEvent event) {
							return false;
					}

				});
	}

	private void setLastPressedDefaltBitmap() {
		prePressedIv.setImageResource(R.drawable.ui_6u_1 + prePos);
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
							Toast.makeText(mApp,R.string.low_power, Toast.LENGTH_SHORT).show();
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
		case R.id.btn_add:
			if (!btnCanClick) {
				Toast.makeText(this,
						mAct.getResources().getString(R.string.select_model),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (strong_value == 6) {
				return;
			} else {
				strong_value++;
			}
			tv_strong.setText(strong_value + "");
			mApp.sendBlueOrder(strong_value + 20);
			break;
		case R.id.btn_manus:
			if (!btnCanClick) {
				Toast.makeText(this,
						mAct.getResources().getString(R.string.select_model),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (strong_value == 0) {
				return;
			} else {
				strong_value--;
			}
			tv_strong.setText(strong_value + "");
			mApp.sendBlueOrder(strong_value + 20);
			break;
		case R.id.iv_back:
			finish();
			break;
		}
	}
}
