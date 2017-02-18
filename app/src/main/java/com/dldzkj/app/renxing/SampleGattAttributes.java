/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file ecept in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either epress or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dldzkj.app.renxing;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class SampleGattAttributes {

	private static HashMap<String, String> attributes = new HashMap<String, String>();
//	private static HashMap<Integer, byte[]> orders = new HashMap<Integer, byte[]>();
	private static HashMap<String, byte[]> orderstr = new HashMap<String, byte[]>();
	public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	// 通知特�? 电量和产品编号通道2
	public static String CHARACT_NOTIFY = "0000FFE2-0000-1000-8000-00805f9b34fb";
	// 通知服务
	public static String SERVICE_NOTIFY = "0000FFE5-0000-1000-8000-00805f9b34fb";
	// 通知介绍
	public static String DESCRIPTION_NOTIFY = "00002902-0000-1000-8000-00805f9b34fb";
	// 写入服务
	public static String SERVICE_WRITE = "0000FFE5-0000-1000-8000-00805f9b34fb";
	// 写入特�? 按键指令通道1
	public static String CHARACT_WRITE = "0000FFE9-0000-1000-8000-00805f9b34fb";

	public static String GOD_1 = "00000524-0000-1000-8000-00805f9b34fb"; // 战神设备1
	public static String GOD_2 = "00000524-0000-1000-8000-00805f9b34fb"; // 战神设备2
	public static String GOD_3 = "00000524-0000-1000-8000-00805f9b34fb"; // 战神设备3

	static {
		// Sample Services.
		attributes.put("0000180d-0000-1000-8000-00805f9b34fb",
				"Heart Rate Service");
		attributes.put("0000180a-0000-1000-8000-00805f9b34fb",
				"Device Information Service");

		// Sample Characteristics.
		attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
		attributes.put("00002a29-0000-1000-8000-00805f9b34fb",
				"Manufacturer Name String");

		// 数据通道 Services
		attributes.put("0000FFE5-0000-1000-8000-00805f9b34fb", "写入服务");
		attributes.put("0000FFE5-0000-1000-8000-00805f9b34fb", "通知服务");

		// 执行操作 Characteristics
		attributes.put("0000FFE9-0000-1000-8000-00805f9b34fb", "写入操作");
		attributes.put("0000FFE2-0000-1000-8000-00805f9b34fb", "通知操作");

		/*********** 指令集合 **************/
/*
		orders.put(205, new byte[] { 0, (byte) 205 });// 握手指令
		orders.put(210, new byte[] { 0, (byte) 210 });// UI就绪
		orders.put(211, new byte[] { 0, (byte) 211 });// 重声UI
		orders.put(250, new byte[] { 0, (byte) 250 });// Heigh
		orders.put(255, new byte[] { 0, (byte) 255 });// 设备关机

		orders.put(0, new byte[] { 0, 0 });
		orders.put(1, new byte[] { 0, 1 });
		orders.put(2, new byte[] { 0, 2 });
		orders.put(3, new byte[] { 0, 3 });
		orders.put(4, new byte[] { 0, 4 });
		orders.put(5, new byte[] { 0, 5 });
		orders.put(6, new byte[] { 0, 6 });
		orders.put(7, new byte[] { 0, 7 });
		orders.put(8, new byte[] { 0, 8 });
		orders.put(9, new byte[] { 0, 9 });
		orders.put(10, new byte[] { 0, 10 });

		orders.put(20, new byte[] { 0, 20 });
		orders.put(21, new byte[] { 0, 21 });
		orders.put(22, new byte[] { 0, 22 });
		orders.put(23, new byte[] { 0, 23 });
		orders.put(24, new byte[] { 0, 24 });
		orders.put(25, new byte[] { 0, 25 });
		orders.put(26, new byte[] { 0, 26 });
		orders.put(27, new byte[] { 0, 27 });
		orders.put(28, new byte[] { 0, 28 });
		orders.put(29, new byte[] { 0, 29 });
		
		orders.put(30, new byte[] { 0, 30 });
		orders.put(31, new byte[] { 0, 31 });
		orders.put(32, new byte[] { 0, 32 });
		orders.put(33, new byte[] { 0, 33 });

		orders.put(40, new byte[] { 0, 40 });
		orders.put(41, new byte[] { 0, 41 });
		orders.put(42, new byte[] { 0, 42 });
		orders.put(43, new byte[] { 0, 43 });
		orders.put(44, new byte[] { 0, 44 });
		orders.put(45, new byte[] { 0, 45 });
		orders.put(46, new byte[] { 0, 46 });
		orders.put(47, new byte[] { 0, 47 });
		orders.put(48, new byte[] { 0, 48 });
		orders.put(49, new byte[] { 0, 49 });
		orders.put(50, new byte[] { 0, 50 });
		
		orders.put(60, new byte[] { 0, 60 });
		orders.put(61, new byte[] { 0, 61 });
		//敲敲乐
		orders.put(240, new byte[] { 0, (byte) 240 });
		orders.put(241, new byte[] { 0, (byte) 241 });
		orders.put(242, new byte[] { 0, (byte) 242 });
		orders.put(243, new byte[] { 0, (byte) 243 });
		orders.put(244, new byte[] { 0, (byte) 244 });
		orders.put(245, new byte[] { 0, (byte) 245 });
		orders.put(246, new byte[] { 0, (byte) 246 });
		orders.put(247, new byte[] { 0, (byte) 247 });
		orders.put(248, new byte[] { 0, (byte) 248 });

		orders.put(90, new byte[] { 0, 90 });
		orders.put(91, new byte[] { 0, 91 });
		orders.put(92, new byte[] { 0, 92 });
		orders.put(93, new byte[] { 0, 93 });
		orders.put(94, new byte[] { 0, 94 });
		orders.put(95, new byte[] { 0, 95 });
		orders.put(95, new byte[] { 0, 95 });*/
		
		orderstr.put("WNDS",hexStringToBytes("574E4453"));//产品编号数据
		orderstr.put("VOLT", hexStringToBytes("564F4C54"));//电量数据
		orderstr.put("PRES",hexStringToBytes(""));//压力数据
		orderstr.put("TEMP", hexStringToBytes(""));//温度数据
	}
	//将蓝牙4.0指令格式更改为蓝牙3.0指令格式
	public static byte[]	sendOrderChractOne(int key){
		/*key>>8,key转二进制，右移八位，此处为0*/
		byte[] order=new byte[]{65,84,58,(byte) (key>>8),(byte) key};
		int length=order.length;
		byte[] order1=new byte[length+3];
		for (int i = 0; i < length; i++) {
			order1[i]=order[i];
		}	
		order1[length]=0x01;
		order1[length+1]=0x0D;
		order1[length+2]=0x0A;
		/*Log.d("zxl", "order1****：");
    	for (byte b : order1) {
    		System.out.println(Integer.toHexString(b & 0xFF));
    	}*/
		return order1;
	}
	public static byte[]	sendOrderChractTwo(String key){
		String Blue3Str="AT:"+key;
		byte[] order=new byte[Blue3Str.length()];
		try {
			order = Blue3Str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int length=order.length;
		byte[] order1=new byte[length+3];
		for (int i = 0; i < length; i++) {
			order1[i]=order[i];
		}
		order1[length]=0x02;
		order1[length+1]=0x0D;
		order1[length+2]=0x0A;
		return order1;
	}
	
	public static byte[] getOrder(int order) {
		return  new byte[] { (byte) (order >> 8), (byte) order };
	}
	
	public static byte[] getOrder(String str) {
		return orderstr.get(str);
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
