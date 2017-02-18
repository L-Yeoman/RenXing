package com.dldzkj.app.renxing.blelib.utils;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;
@SuppressLint("NewApi")
public class BluetoothLeScanner {
	private final Handler mHandler;
	private final BluetoothAdapter.LeScanCallback mLeScanCallback;
	private final BluetoothUtils mBluetoothUtils;
	private boolean mScanning;
	private Context c;
	public BluetoothLeScanner(Context c,BluetoothAdapter.LeScanCallback leScanCallback, BluetoothUtils bluetoothUtils){
		this.c=c;
		mHandler = new Handler();
		mLeScanCallback = leScanCallback;
		mBluetoothUtils = bluetoothUtils;
	}
	
	public boolean isScanning() {
		return mScanning;
	}
	/*搜索设备*/
	public void scanLeDevice(final int duration, final boolean enable) {
        if (enable) {
        	if(mScanning){return;}
        	Log.d("TAG", "~ Starting Scan");
            // Stops scanning after a pre-defined scan period.
        	if(duration > 0){
	            mHandler.postDelayed(new Runnable() {
	                @Override
	                public void run() {
	                	Log.d("TAG", "~ Stopping Scan (timeout)");
	                    mScanning = false;
	                    Intent intent = new Intent(BluetoothLeService.ACTION_GATT_SCAN_OVER);
	                    c.sendBroadcast(intent);
	                    mBluetoothUtils.getBluetoothAdapter().stopLeScan(mLeScanCallback);
	                }
	            }, duration);
        	}
            mScanning = true;
            mBluetoothUtils.getBluetoothAdapter().startLeScan(new UUID[] { UUID
					.fromString(MainActivity.GOD_1)},mLeScanCallback);
        } else {
        	Log.d("TAG", "~ Stopping Scan");
            mScanning = false;
            mBluetoothUtils.getBluetoothAdapter().stopLeScan(mLeScanCallback);
        }
    }
}
