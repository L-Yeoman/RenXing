package com.dldzkj.app.renxing.activity.blue;

import uk.co.alt236.bluetoothlelib.util.ByteUtils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;


public class HappyClickActivity extends BaseOtherActivity {
	private int[] resId = new int[]{R.drawable.scene_bg1, R.drawable.scene_bg2, R.drawable.scene_bg3, R.drawable.scene_bg4, R.drawable.scene_bg5, R.drawable.scene_bg6};
	private String[] titleArr=new String[]{"海边","森林","流云","宇宙","爱情酒店","停车zuo爱"};
	private String[] subtitleArr=new String[]{"马尔代夫·天堂岛","加拿大·圣劳伦斯","冰岛·华纳达尔","仙女座·星云","土耳其·特洛伊","北京 北清路"};
	private String[] contentArr=new String[]{"惬意诱人的椰林沙滩，旖旎的热带海洋风光，性感的心灵总有一段令人忘怀的记忆。我在海边惊涛拍浪，随潮汐的澎湃，那尤能湿脚的胚胎 给你一个宽宽敞敞的世界",
			"千岛圣地，山水相影， 湖泊相通，大小岛屿，星罗棋布，数不胜数。如置身世外桃源，搭乘游船，饱览圣劳伦斯河迷人风光，就我和你，沉浸于大自然的怀抱之中.",
			"冰火两重天的极限之旅，回到最初地球生命的开始，在最靠近北极圈的国度感受火山及冰河世界的完美结合。进入维京人的奇妙世界，维京人的时期虽然已经过去，但维京人从未走远。",
			"浩瀚宇宙，闭眼冥思。我看见你薄纱朦胧，星云围绕，飞驰而来。飞过所有荒凉和繁荣，去感知那些神不能感知的东西",
			"千年古城特洛伊的城堡迎来落日的最后一丝余晖，达达尼尔海峡吹来的和煦海风轻轻拂动着你的发梢。刹那间，时空扭转，斗转星移，仿佛你就是让希腊城邦大动干戈的海伦，城外万千英雄奋力厮杀只为一窥你倾世的容颜。",
			"是非成败转头空，青山依旧在，几度夕阳红。历史的沉重总是像一把大锤，砸在人们的心中，这座城市的兴替，总是让我们感慨万千，位于北京市郊的北清路，虽然比五环多半环，然而毕竟是在皇城之下，处处可循的历史遗迹仍然值得我们去探寻。"};

	private MyApplication mApp;
	private MyFramlayout mfl_cicle;//有水波纹效果
	private TextView title,subtitle,content;
	RelativeLayout layout;
	ImageView btn_back;
	int position;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_click);
		position=getIntent().getIntExtra("pos",0);
		initView();
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
				startActivity(new Intent(HappyClickActivity.this, MainActivity.class));
				finish();
//				 tv.setText("连接已断开");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				final byte[] dataArr = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_RAW);
				String dataStr=ByteUtils.byteArrayToHexString(dataArr);
				String[] data=BlueBeforeUIActivity.String2Array(dataStr);
				System.out.println("数据"+dataStr);
				 if("00".equals(data[1].trim())&&"00".equals(data[2].trim())&&"00".equals(data[3].trim())){//电量数据
					 int value=Integer.valueOf(data[0].trim(),16);
//					 iv_battery.setText(value+"");
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
public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode==KeyEvent.KEYCODE_BACK) {
		finish();
	}
	return true;
}
	private void initView() {
		mApp =  (MyApplication) getApplication();
		mApp.mStack.add(this);
		mfl_cicle = (MyFramlayout) findViewById(R.id.mfl_cicle);
		mfl_cicle.init(this, mApp);
		layout = (RelativeLayout) findViewById(R.id.ll_show);
		title = (TextView) findViewById(R.id.scene_title);
		subtitle = (TextView) findViewById(R.id.scene_subtitle);
		content = (TextView) findViewById(R.id.scene_content);
		btn_back=(ImageView) findViewById(R.id.iv_back);
		layout.setBackgroundResource(resId[position]);
		title.setText(titleArr[position]);
		subtitle.setText(subtitleArr[position]);
		content.setText(contentArr[position]);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
