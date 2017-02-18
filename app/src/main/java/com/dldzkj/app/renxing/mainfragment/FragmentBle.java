package com.dldzkj.app.renxing.mainfragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.PromptManager;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.blue.BlueBeforeUIActivity;
import com.dldzkj.app.renxing.bean.Playlist;
import com.dldzkj.app.renxing.blelib.contains.BluetoothLeDeviceStore;
import com.dldzkj.app.renxing.blelib.services.BluetoothChatService;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;
import com.dldzkj.app.renxing.blelib.services.MusicService;
import com.dldzkj.app.renxing.blelib.services.MusicUtil;
import com.dldzkj.app.renxing.blelib.utils.BluetoothLeScanner;
import com.dldzkj.app.renxing.blelib.utils.BluetoothUtils;
import com.dldzkj.app.renxing.customview.MyViewLayout;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;
/*设备扫描页面*/
public class FragmentBle extends Fragment {
    private MyApplication mApp;
    /********
     * 动画相关参数
     ***********/
    protected static final int SCAN_LODING = 1;
    protected static final int FINSH_SCAN = 2;
    /*private ImageView im_scan;
    private ImageView im_dian;
    private ImageView btn;*/
    private TextView tv_count;
    private MyViewLayout layout;
    private RotateAnimation animation;
    private AlphaAnimation animation2;

    /***********
     * 蓝牙相关参数
     ***********/
    public static String GOD_1 = "00000524-0000-1000-8000-00805f9b34fb";
    public static String CHARACT_NOTIFY = "0000FFE2-0000-1000-8000-00805f9b34fb";
    public static String SERVICE = "0000FFE5-0000-1000-8000-00805f9b34fb";
    public static String CHARACT_WRITE = "0000FFE9-0000-1000-8000-00805f9b34fb";
    //蓝牙4.0
    private BluetoothUtils mBluetoothUtils;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothLeScanner mScanner;
    private BluetoothLeDeviceStore mDeviceStore;
    public static BluetoothLeDevice targetdevice;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private boolean mIsBluetoothLePresent = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            mApp.blueServer = mBluetoothLeService;
            if (!mBluetoothLeService.initialize()) {
                Log.e("zxl", "Unable to initialize Bluetooth");
                tv_count.setText(R.string.Blue_useless_Blue);
                StopBLE();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(targetdevice.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mApp.mBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mApp.mMusicServer = binder.getService();
            mApp.mBound = true;
            initMusic();
        }
    };
    private HashMap<String, ArrayList<Playlist>> musicMap;
    public void initMusic() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (musicMap == null) {
                    musicMap = MusicUtil.getMp3Lists(FragmentBle.this.getActivity(), MyApplication.getInstance().getDbUtils());
                    if (musicMap != null) {
                        mApp.musicList = musicMap.get("musicList");
                        mApp.favList = musicMap.get("favList");
                        mApp.mMusicServer.setCurrentPlayList(mApp.musicList);
                        if (mApp.musicList.size() > 0) {
                            mApp.mMusicServer
                                    .setCurrentPlayMusic(mApp.musicList.get(0));
                        }
                    }
                }
            }
        }.start();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ble, container, false);
        setView(v);
        return v;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mApp = (MyApplication) getActivity().getApplication();
        Intent intent = new Intent(this.getActivity(), MusicService.class);
        getActivity().bindService(intent, mConnection, getActivity().BIND_AUTO_CREATE);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (VERSION.SDK_INT >= 18) {
            mScanner.scanLeDevice(-1, false);
            getActivity().unregisterReceiver(mGattUpdateReceiver);
        }
    }

    //时间判断
    private long startTime;

    @Override
    public void onResume() {
        super.onResume();
        if (VERSION.SDK_INT >= 18) {
            mIsBluetoothLePresent = mBluetoothUtils
                    .isBluetoothLeSupported();
            //注册广播
            getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
        startTime = System.currentTimeMillis();
        clickToStart();
//		tv_count.setText(R.string.ble_click_tip);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
            getActivity().unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
        getActivity().unbindService(mConnection);
    }

    public void clickToStart() {
        if (VERSION.SDK_INT < 18) {
            startAnim();
            tv_count.setText(R.string.Blue_UnsupportBlue);//走蓝牙3.0
            getActivity().finish();
//            Blue3Method();
            return;
        }
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        if (!mIsBluetoothOn) {
                Intent enableBlueIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                getActivity().startActivity(enableBlueIntent
//                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            getActivity().startActivityForResult(enableBlueIntent, 0x89);
            tv_count.setText(R.string.Blue_MakeSure_open_Blue);
            return;
        }
        if (!mIsBluetoothLePresent) {
            startAnim();
            tv_count.setText(R.string.Blue_UnsupportBlue);//走蓝牙3.0
//            Blue3Method();
            getActivity().finish();
            return;
        }
        if (!mScanner.isScanning()) {
            startBLE();
        }
    }


    /*	@Override
    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.im_dian:
			if (mApp.BlueServiceIsConnnected()) {
				startActivity(new Intent(FragmentBle.this.getActivity(),BlueBeforeUIActivity.class));
				return;
			}
		
			if (VERSION.SDK_INT<18) {
				startAnim();
				tv_count.setText(R.string.Blue_UnsupportBlue);//走蓝牙3.0
				Blue3Method();
				return;
			}
			final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
			if (!mIsBluetoothOn) {
				tv_count.setText(R.string.Blue_MakeSure_open_Blue);
				return;
			}
			if (!mIsBluetoothLePresent) {
				startAnim();
				tv_count.setText(R.string.Blue_UnsupportBlue);//走蓝牙3.0
				Blue3Method();
				return;
			}
			if (!mScanner.isScanning()) {
				startBLE();
			}
			break;

		default:
			break;
		}
	}*/

    public void startBLE() {
        startAnim();
        startScan();
    }

    public void StopBLE() {
        stopAnim();
        mScanner.scanLeDevice(-1, false);
    }

    /************
     * 以下均为本类供调用的私有方法
     *************/
    private boolean mConnected = false;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                tv_count.setText(R.string.Blue_launchBlueService);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                tv_count.setText(R.string.ble_disconnect);
                stopAnim();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                tv_count.setText(R.string.Blue_connected_ui);
                stopAnim();
                long t = System.currentTimeMillis();
                long temp=t - startTime;
                if (temp < 3000) {
                    mHandler.sendEmptyMessageDelayed(0x53, 3000 - temp);
                } else {
                    mHandler.sendEmptyMessage(0x53);
                }
//                startActivity(new Intent(FragmentBle.this.getActivity(), BlueBeforeUIActivity.class));
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_FAILD.equals(action)) {
                tv_count.setText(R.string.Blue_useless_Blue + "，code=" + intent.getIntExtra(BluetoothLeService.ACTION_SERVICES_FAILD_CODE, 0));
                stopAnim();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_TIMEOUT.equals(action)) {
                tv_count.setText(R.string.Blue_useless_connecd);
                stopAnim();
            } else if (BluetoothLeService.ACTION_GATT_SCAN_OVER.equals(action) && targetdevice == null) {
//				tv_count.setText("设备连接失败\n若设备已打开，请尝试重启");
//				StopBLE();
                tv_count.setText(R.string.Blue_connect4fail);
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.disconnect();
                }
                mApp.blueServer = null;
                PromptManager.showToast(getActivity(),"连接失败");
                getActivity().finish();
//                Blue3Method();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_FAILD);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SCAN_OVER);
        return intentFilter;
    }

    private void stopAnim() {
//		im_dian.setClickable(true);
//		im_scan.clearAnimation();// 清除此ImageView身上的动画
//		im_dian.clearAnimation();// 清除此ImageView身上的动画
//		getActivity().finish();
    }

    private void startAnim() {
//		im_dian.setClickable(false);
//		im_scan.startAnimation(animation);
//		im_dian.startAnimation(animation2);

    }

    private void startScan() {
        targetdevice = null;
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils
                .isBluetoothLeSupported();
        mDeviceStore.clear();
        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if (mIsBluetoothOn && mIsBluetoothLePresent) {
            tv_count.setText(R.string.Blue_Start_Scanner);
            mScanner.scanLeDevice(10000, true);
        }
    }

    private void initAnim() {
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        animation2 = new AlphaAnimation(0.0f, 1.0f);
        animation2.setDuration(3000);
        animation2.setRepeatCount(Animation.INFINITE);
    }

    private void setView(View v) {
//		im_scan = (ImageView) v.findViewById(R.id.im_scan);
//		im_dian = (ImageView) v.findViewById(R.id.im_dian);
        tv_count = (TextView) v.findViewById(R.id.tv_toast);
        layout= (MyViewLayout) v.findViewById(R.id.layout);
//		btn = (ImageView) v.findViewById(R.id.im_dian);
        if (VERSION.SDK_INT >= 18) {
            initBlue4Params();
        }
        initAnim();

//		btn.setOnClickListener(this);
    }

    @SuppressLint("NewApi")
    private void initBlue4Params() {
        mDeviceStore = new BluetoothLeDeviceStore();
        mBluetoothUtils = new BluetoothUtils(this.getActivity());
        //扫描后的回调
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi,
                                 byte[] scanRecord) {

                final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device,
                        rssi, scanRecord, System.currentTimeMillis());
                mDeviceStore.addDevice(deviceLe);
                // 判断扫描到设备则停止扫描连接设备
                final EasyObjectCursor<BluetoothLeDevice> c = mDeviceStore
                        .getDeviceCursor();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (c.getCount() > 0) {
                            mScanner.scanLeDevice(-1, false);
                            targetdevice = mDeviceStore.getDeviceList().get(0);
                            tv_count.setText(R.string.Blue_Scanner_connected +":" + targetdevice.getName());
                            if (mBluetoothLeService == null) {
                                final Intent gattServiceIntent = new Intent(FragmentBle.this.getActivity(), BluetoothLeService.class);
                                getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);
                            } else {
                                mBluetoothLeService.connect(targetdevice.getAddress());
                            }
                        } else {
                            tv_count.setText(R.string.Blue_Scanner_over_Nofind);
                            StopBLE();
                        }
                    }
                });
            }
        };
        mScanner = new BluetoothLeScanner(this.getActivity(), mLeScanCallback, mBluetoothUtils);

    }

    /*************
     * 蓝牙3.0相关参数
     ******************/
    // Debugging
    private final String BLUETOOTH_DEVICE_ADRESS = "00:00:00:06:78:AB";
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST_FAIL = 5;
    public static final int MESSAGE_TOAST_DISCONNECT = 7;
    public static final int MESSAGE_OK = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    /*******
     * 蓝牙3.0方法
     *******/
    public void Blue3Method() {
        Log.d("zxl", "开始3.0扫描");
        if (!mIsBluetoothLePresent) {
            tv_count.setText(getResources().getString(
                    R.string.Blue_Start_Scanner));
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            tv_count.setText(R.string.Blue_useless_Blue);
            stopAnim();
            return;
        }
        if (mChatService == null) setupChat();
        linkBluetooth();
    }

    private void linkBluetooth() {
        BluetoothDevice _Device = mBluetoothAdapter.getRemoteDevice(BLUETOOTH_DEVICE_ADRESS);
        if (mApp.mChatService == null) setupChat();
        mApp.mChatService.connect(_Device);
    }

    private void setupChat() {
        mApp.mChatService = new BluetoothChatService(this.getActivity(), mHandler);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case MESSAGE_DEVICE_NAME://设备已连接，准备进入界面
                    tv_count.setText(R.string.Blue_Scanner_connected);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("zxl", "3.0接收到数据：" + readMessage);
                    getActivity().sendBroadcast(new Intent(BluetoothLeService.ACTION_DATA_AVAILABLE).putExtra(BluetoothLeService.EXTRA_DATA_RAW, readBuf));
                    break;
                case MESSAGE_TOAST_DISCONNECT:
                    if (getActivity() != null)
                        getActivity().sendBroadcast(new Intent(BluetoothLeService.ACTION_GATT_DISCONNECTED));
                    break;
                case MESSAGE_TOAST_FAIL:
                    tv_count.setText(R.string.Blue_useless_Blue);
                    stopAnim();
                    break;
                case MESSAGE_OK:
                    tv_count.setText(R.string.Blue_connected_ui);
                    stopAnim();
                    long t = System.currentTimeMillis();
                    long temp=t - startTime;
                    if (temp < 3000) {
                        sendEmptyMessageDelayed(0x53, 3000 - temp);
                    } else {
                        sendEmptyMessage(0x53);
                    }
                    break;
                case 0x53:
                    layout.stopAnim=true;
                    startActivity(new Intent(getActivity(), BlueBeforeUIActivity.class));
                    break;
            }
        }
    };

}
