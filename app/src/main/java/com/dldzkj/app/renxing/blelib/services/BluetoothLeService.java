/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dldzkj.app.renxing.blelib.services;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dldzkj.app.renxing.MainActivity;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_GATT_SERVICES_FAILD = "com.example.bluetooth.le.ACTION_GATT_SERVICES_FAILD";
	public final static String ACTION_GATT_SCAN_OVER = "com.example.bluetooth.le.ACTION_GATT_SCAN_OVER";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String ACTION_GATT_SERVICES_TIMEOUT = "com.example.bluetooth.le.ACTION_GATT_SERVICES_TIMEOUT";
	public final static String EXTRA_DATA_RAW = "com.example.bluetooth.le.EXTRA_DATA_RAW";
	public final static String EXTRA_UUID_CHAR = "com.example.bluetooth.le.EXTRA_UUID_CHAR";
	public final static String ACTION_SERVICES_FAILD_CODE = "com.example.bluetooth.le.ACTION_SERVICES_FAILD_CODE";
	// Implements callback methods for GATT events that the app cares about.  For example,
	// connection change and services discovered.

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.d("zxl", "设备已连接");
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				if (timer!=null) {
					timer.cancel();
				}
				setUiValue(0);
				disconnect();
			
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d("zxl", "服务被发现");
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
//				initCharact();
			} else {
				broadcastUpdate(ACTION_GATT_SERVICES_FAILD,status);
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			// 读操作
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};
	public boolean isConnected(){
		if (mConnectionState==STATE_CONNECTED) {
			return true;
		}else return false;
	}
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}
	private void broadcastUpdate(final String action,int status) {
		final Intent intent = new Intent(action);
		intent.putExtra(ACTION_SERVICES_FAILD_CODE, status);
		sendBroadcast(intent);
	}
	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		intent.putExtra(EXTRA_UUID_CHAR, characteristic.getUuid().toString());

		// Always try to add the RAW value
		final byte[] data = characteristic.getValue();
		if (data != null && data.length > 0) {
			intent.putExtra(EXTRA_DATA_RAW, data);
		}

		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that BluetoothGatt.close() is called
		// such that resources are cleaned up properly.  In this particular example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address The device address of the destination device.
	 *
	 * @return Return true if the connection is initiated successfully. The connection result
	 *         is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device.  Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {

			Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				sendBroadcast(new Intent(ACTION_GATT_SERVICES_TIMEOUT));
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The disconnection result
	 * is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		mBluetoothDeviceAddress=null;
		MainActivity.targetdevice=null;
		mConnectionState = STATE_DISCONNECTED;
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
		close();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure resources are
	 * released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
	 * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 *
	 * @param characteristic The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}
	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		 mBluetoothGatt.writeCharacteristic(characteristic);
	}
	/**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic Characteristic to act on.
	 * @param enabled If true, enable notification.  False otherwise.
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This should be
	 * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
	 *
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null) return null;

		return mBluetoothGatt.getServices();
	}
	/***************zxl**************/
	private BluetoothGattService  service;
	private BluetoothGattCharacteristic write_charact;
	private BluetoothGattCharacteristic notify_charact;
	public void initCharact() {
		if (getSupportedGattServices()!=null&&getSupportedGattServices().size() != 0) {
			// PromptManager.showToast(mApp, "开启指令通道");
			service = getSupportedGattService(MainActivity.SERVICE);
			write_charact = service.getCharacteristic(UUID
					.fromString(MainActivity.CHARACT_WRITE));
			notify_charact = service.getCharacteristic(UUID
					.fromString(MainActivity.CHARACT_NOTIFY));
			sendOrderCharact2(hexStringToBytes("574E4453"));//WNDS
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			sendOrderCharact2(hexStringToBytes("564F4C54"));//VOLT
			// 发送握手指令
			readCharacteristic(notify_charact);
		}
	}
	private Timer timer;
	private String productValue="";//产品号
	private int uiValue=0;//模板号
	private int  batteryValue=0;//电量值
	public int getBatteryValue() {
		return batteryValue;
	}
	public void setBatteryValue(int batteryValue) {
		this.batteryValue = batteryValue;
	}
	public String getProductValue() {
		return productValue;
	}
	public void setProductValue(String productValue) {
		this.productValue = productValue;
	}
	public int getUiValue() {
		return uiValue;
	}
	public void setUiValue(int uiValue) {
		this.uiValue = uiValue;
	}
	public void BatteryTimer(){
		if (timer!=null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				sendOrderCharact2(hexStringToBytes("564F4C54"));//VOLT
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				readCharacteristic(notify_charact);
			}
		}, 500,1000*30);
	}
	public BluetoothGattService getSupportedGattService(String uuid) {
		Log.e("UUID.fromString(uuid)", UUID.fromString(uuid) + "dsdsdsd");
		if (mBluetoothGatt == null)
			return null;
		return mBluetoothGatt.getService(UUID.fromString(uuid));
	}
	public void sendOrderCharact1(byte[] b) {
		if (write_charact != null) {
			int prop = write_charact.getProperties();
			if ((prop | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
				write_charact.setValue(b);
				write_charact.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
				writeCharacteristic(write_charact);
			}
		}
	}
	public void sendOrderCharact2(byte[] b) {
		if (notify_charact != null) {
			int prop = notify_charact.getProperties();
			if ((prop | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
				notify_charact.setValue(b);
				notify_charact.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
				writeCharacteristic(notify_charact);
				Log.d("zxl", "发送ui或电量");
			}
		}
	}
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	} 
	 private static byte charToByte(char c) {  
		    return (byte) "0123456789ABCDEF".indexOf(c);  
		}
}