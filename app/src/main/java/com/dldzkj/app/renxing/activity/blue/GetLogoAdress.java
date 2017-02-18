package com.dldzkj.app.renxing.activity.blue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.PromptManager;
import com.dldzkj.app.renxing.bean.BaseModel;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class GetLogoAdress {
	static Logo_Bg bg;
	public static SharedPreferences sp;
	public static SharedPreferences.Editor edit;
	public static String logoAdress, UIAdress;
	public static byte[] logoStr, UIStr;

	public static void getFlagValue(Context c) {
		sp = c.getSharedPreferences("UIData", 0);
		edit = sp.edit();
		logoStr = readObjByKey(c, "bg_logo");
		UIStr = readObjByKey(c, "bg_UI");
	}

	public static void saveObjByKey(byte[] bmp, String key) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(bmp);
			String base64Str = Base64.encodeToString(baos.toByteArray(),
					Base64.DEFAULT);
			edit.putString(key, base64Str).commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] readObjByKey(Context context, String key) {
		String base64Str = sp.getString(key, null);
		if (base64Str == null) {
			return null;
		}
		byte[] objData = Base64.decode(base64Str, Base64.DEFAULT);

		ByteArrayInputStream bais = new ByteArrayInputStream(objData);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (byte[]) ois.readObject();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void getAdress(String puroductcode, final Context con,
			final Handler han) {
		Log.d("zxl", "产品编号："+puroductcode);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ProductNo", puroductcode);
		HttpUtils httpu=new HttpUtils();
		RequestParams pa=new RequestParams();
		HashMap<String, Object> jsons=StringUtils.toJson(map);
		for (String key:jsons.keySet()) {
			pa.addBodyParameter(key, String.valueOf(jsons.get(key)));
		}
		httpu.send(HttpRequest.HttpMethod.POST, MyApplication.WEB_SERVER_URL + "?" + "GetProductUIInfoByNo", pa, new RequestCallBack<Object>() {
			@Override
			public void onSuccess(ResponseInfo<Object> responseInfo) {
			Object result=responseInfo.result;

				if (result == null) {
					return;
				}
				BaseModel baseModel = JSON.parseObject(
						result.toString(), BaseModel.class);
				switch (baseModel.getErrNum()) {

					case MyApplication.SUCCESS_CODE:
						bg = JSON.parseArray(baseModel.getData(),
								Logo_Bg.class).get(0);
						Message message = han.obtainMessage(1, bg);
						han.sendMessage(message);
						break;
					case MyApplication.FAILURE_CODE:
						PromptManager.showToast(con, baseModel.getErrMsg());
						break;
					case MyApplication.OPERATION_CODE:
						PromptManager.showToast(con, baseModel.getErrMsg());
						break;

					default:
						break;
				}

			}

			@Override
			public void onFailure(HttpException e, String s) {

			}
		});


	}

}
