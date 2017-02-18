package com.dldzkj.app.renxing.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;


import android.os.Handler;
import android.os.Message;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.bean.ConstantValue;

/**
 * 
 * @author CY
 * @since 2014-03-08
 */
public class Request {

	private static final int REQUEST_TIMEOUT = 1000 * 60;
	private OnRequestListener mOnRequestListener;
	private int mTag;
	public Handler mHandler;

	public Request(OnRequestListener onRequestListener, int tag) {
		mOnRequestListener = onRequestListener;
		mTag = tag;
		initHandler();
	}

	public void initHandler() {
		if (mOnRequestListener == null) {
			return;
		}
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case HANDLER_REQUEST_SUCCESS:
						if (mOnRequestListener != null) {
							mOnRequestListener.onRequestSuccess(msg.obj.toString(), mTag);
						}
						break;
					case HANDLER_REQUEST_FAIL:
						if (mOnRequestListener != null) {
							mOnRequestListener.onRequestFail(msg.arg1, mTag,msg.obj);
						}
						break;
					case HANDLER_REQUEST_START:
						if (mOnRequestListener != null) {
							mOnRequestListener.onRequestStart(mTag);
						}
						break;

					default:
						break;
				}
			}

		};
	}

	/**
	 * @param url
	 * @param postData
	 */
	public String postUrlByConnection(String url, byte[] postData) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			if (mOnRequestListener != null) {
				mHandler.sendEmptyMessage(HANDLER_REQUEST_START);
			}
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			// conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setReadTimeout(REQUEST_TIMEOUT);
			if (postData != null) {
				conn.setRequestProperty("Content-Length", postData.length + "");
				OutputStream os = conn.getOutputStream();
				os.write(postData);
				os.flush();
				os.close();
			}
			int ResponseCode = conn.getResponseCode();
			if (ResponseCode == HttpURLConnection.HTTP_OK) {

				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int index = -1;
				byte data[] = new byte[1024];
				while ((index = is.read(data)) != -1) {
					baos.write(data, 0, index);
				}
				String content = new String(baos.toByteArray());
				baos.close();
				is.close();
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestSuccess(content, mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_SUCCESS;
					message.obj = content;
					mHandler.sendMessage(message);
				}
				Log.e("Request", content);
				return content;
			} else {
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestFail(ResponseCode, mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_FAIL;
					message.arg1 = ResponseCode;
					mHandler.sendMessage(message);
				}
			}
			conn.disconnect();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mOnRequestListener != null) {
			// mOnRequestListener.onRequestFail(0, mTag);
			Message message = new Message();
			message.what = HANDLER_REQUEST_FAIL;
			message.arg1 = 0;
			mHandler.sendMessage(message);
		}
		return null;
	}

	/**
	 * @param url
	 * @param listdata
	 * @return
	 */
	public String postUrlByHttpPost(String url, List<NameValuePair> listdata) {
		try {
			HttpPost httpPost = new HttpPost(url);
			if (listdata != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(listdata, "UTF-8"));
			}
			if (mOnRequestListener != null) {
				mHandler.sendEmptyMessage(HANDLER_REQUEST_START);
			}
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestSuccess(result, mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_SUCCESS;
					message.obj = result;
					mHandler.sendMessage(message);
				}
				return result;
			} else {
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestFail(httpResponse
					// .getStatusLine().getStatusCode(), mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_FAIL;
					message.arg1 = httpResponse.getStatusLine().getStatusCode();
					mHandler.sendMessage(message);
				}
				return null;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// httpPost.setEntity(new UrlEncodedFormEntity(parameters));
		// post.setEntity(entity)
		// String url = "http://192.168.2.112:8080/JustsyApp/Applet";
		// HttpPost httpPost = new HttpPost(url);
		//
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("action", "downloadAndroidApp"));
		// params.add(new BasicNameValuePair("packageId",
		// "89dcb664-50a7-4bf2-aeed-49c08af6a58a"));
		// params.add(new BasicNameValuePair("uuid", "test_ok1"));
		//
		// HttpResponse httpResponse = null;
		// try {
		// httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		// httpResponse = new DefaultHttpClient().execute(httpPost);
		// // System.out.println(httpResponse.getStatusLine().getStatusCode());
		// if (httpResponse.getStatusLine().getStatusCode() == 200) {
		// String result = EntityUtils.toString(httpResponse.getEntity());
		// System.out.println("result:" + result);
		// T.displayToast(HttpURLActivity.this, "result:" + result);
		// }
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mOnRequestListener != null) {
			// mOnRequestListener.onRequestFail(0, mTag);
			Message message = new Message();
			message.what = HANDLER_REQUEST_FAIL;
			message.arg1 = 0;
			mHandler.sendMessage(message);
		}
		return null;
	}

	/**
	 * @param url
	 * @return
	 */
	public String getDataByHttpGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		try {
			if (mOnRequestListener != null) {
				mHandler.sendEmptyMessage(HANDLER_REQUEST_START);
			}
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestSuccess(result, mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_SUCCESS;
					message.obj = result;
					mHandler.sendMessage(message);

				}
				return result;
			} else {
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestFail(statusCode, mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_FAIL;
					message.arg1 = statusCode;
					mHandler.sendMessage(message);
				}
				return null;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mOnRequestListener != null) {
			// mOnRequestListener.onRequestFail(0, mTag);
			Message message = new Message();
			message.what = HANDLER_REQUEST_FAIL;
			message.arg1 = 0;
			mHandler.sendMessage(message);
		}
		return null;
	}

	/**
	 * @param url
	 */
	public String getDataByConnection(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			if (mOnRequestListener != null) {
				mHandler.sendEmptyMessage(HANDLER_REQUEST_START);
			}
			conn.setRequestMethod("GET");
			conn.setReadTimeout(REQUEST_TIMEOUT);
			int ResponseCode = conn.getResponseCode();
			if (ResponseCode == HttpURLConnection.HTTP_OK) {// ����ɹ�
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int index = -1;
				byte data[] = new byte[1024];
				while ((index = is.read(data)) != -1) {
					baos.write(data, 0, index);
				}
				String content = new String(baos.toByteArray());
				baos.close();
				is.close();
				conn.disconnect();
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestSuccess(content, mTag);

					Message message = new Message();
					message.what = HANDLER_REQUEST_SUCCESS;
					message.obj = content;
					mHandler.sendMessage(message);
				}
				Log.e("Request", content);
				return content;
			} else {
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestFail(ResponseCode, mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_FAIL;
					message.arg1 = ResponseCode;
					mHandler.sendMessage(message);
				}
			}
			conn.disconnect();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mOnRequestListener != null) {
			// mOnRequestListener.onRequestFail(0, mTag);
			Message message = new Message();
			message.what = HANDLER_REQUEST_FAIL;
			message.arg1 = 0;
			mHandler.sendMessage(message);
		}
		return null;
	}
	public synchronized String soapWebService(String methodName, Map<String, Object> map) {
		SoapObject soapObject = new SoapObject(NAME_SPACE, methodName);

		if (map != null && !map.isEmpty()) {

			Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				soapObject.addProperty(entry.getKey(), entry.getValue());
			}
		}
		Log.d("net", "soapObject:" + soapObject.toString());
		// FileUtils.createFile(Config.AppDirTemp+"temp.txt",
		// soapObject.toString().getBytes());
		// soapObject.addProperty(name, value)

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		(new MarshalBase64()).register(envelope);
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);
		envelope.encodingStyle = "UTF-8";
		// envelope.addMapping(NAME_SPACE, name, clazz)
		if (RequestHelper.Config.WEB_SERVICE_URL == null) {
			RequestHelper.Config.WEB_SERVICE_URL =WebUtils.RENXING_WEB+"WebService/RXWebService.asmx";
//			throw new NullPointerException("NullPointerException:RequestHelper.Config.WEB_SERVICE_URL is null");
		}
		Log.d("zxl","WEB_SERVICE_URL:"+RequestHelper.Config.WEB_SERVICE_URL);
		HttpTransportSE transportSE = new HttpTransportSE(RequestHelper.Config.WEB_SERVICE_URL,REQUEST_TIMEOUT);
		try {
			if (mOnRequestListener != null) {
				mHandler.sendEmptyMessage(HANDLER_REQUEST_START);
			}
			transportSE.call(NAME_SPACE + methodName, envelope);
			if (envelope.getResponse() != null) {
				Object object = envelope.getResponse();
				String result = object.toString();
				Log.d("zxl","result:"+result);
				if (mOnRequestListener != null) {
					Message message = new Message();
					message.what = HANDLER_REQUEST_SUCCESS;
					message.obj = result;
					mHandler.sendMessage(message);
				}
				return result;
			} else {
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestFail(0, mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_FAIL;
					message.obj = map;
					mHandler.sendMessage(message);
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			if (mOnRequestListener != null) {
				// mOnRequestListener.onRequestFail(0, mTag);
				Message message = new Message();
				message.what = HANDLER_REQUEST_FAIL;
				message.obj = map;
				mHandler.sendMessage(message);
			}
		}
		return null;
	}

	public synchronized Object publicMethod(String methodName, HashMap<String, Object> args) {
		try {
			SoapObject request = new SoapObject(NAME_SPACE, methodName);
			if (args != null && args.size() > 0) {
				Iterator<Map.Entry<String, Object>> it = args.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					request.addProperty(entry.getKey(), entry.getValue());
					Log.e("Memory", entry.getKey() + "== 00  =" + entry.getValue());
				}
			}
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.encodingStyle = "UTF-8";
			envelope.setOutputSoapObject(request);
			if (RequestHelper.Config.WEB_SERVICE_URL == null) {
				throw new NullPointerException("NullPointerException:RequestHelper.Config.WEB_SERVICE_URL is null");
			}
			HttpTransportSE androidHttpTransport = new HttpTransportSE(RequestHelper.Config.WEB_SERVICE_URL);
			Log.e("Memory", NAME_SPACE + methodName);
			androidHttpTransport.call(NAME_SPACE + methodName, envelope);
			Object obj = envelope.getResponse();

			if (obj != null) {
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestSuccess(obj.toString(),
					// mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_SUCCESS;
					message.obj = obj.toString();
					mHandler.sendMessage(message);
				}
			} else {
				if (mOnRequestListener != null) {
					// mOnRequestListener.onRequestFail(0, mTag);
					Message message = new Message();
					message.what = HANDLER_REQUEST_FAIL;
					message.arg1 = 0;
					mHandler.sendMessage(message);
				}
			}
			// Log.e("Data", URL + "  " + methodName + "   " + "    TTTT  " +
			// obj);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mOnRequestListener != null) {
			// mOnRequestListener.onRequestFail(0, mTag);
			Message message = new Message();
			message.what = HANDLER_REQUEST_FAIL;
			message.arg1 = 0;
			mHandler.sendMessage(message);
		}
		return null;
	}



	public final int HANDLER_REQUEST_SUCCESS = 100;
	public final int HANDLER_REQUEST_FAIL = 101;
	public final int HANDLER_REQUEST_START = 102;
	public static final String NAME_SPACE = "http://tempuri.org/";


}
