package com.dldzkj.app.renxing.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.ksoap2.serialization.SoapObject;

/**
 * 
 * @author CY
 * @since 2014-03-08
 */
public class RequestHelper {

	private static ThreadPoolExecutor mThreadPool;

	static {
		// 初始化线程池，最小线程数/�?��线程�?空闲线程保留时间/时间单位/线程数组缓冲队列
		mThreadPool = new ThreadPoolExecutor(1, 128, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
	}

	public static void requestData(String url, byte[] postData, OnRequestListener listener, int tag) {
		executeRequest(url, postData, listener, tag);
	}

	/**
	 * 请求数据，除了url其它都可以为null，默认�?，需�?���?
	 *
	 * @param url
	 * @param listdata
	 * @param listener
	 * @param tag
	 * @return
	 */
	public static String requestDataByHttpPost(String url, List<NameValuePair> listdata, OnRequestListener listener, int tag) {
		Request request = new Request(listener, tag);
		return request.postUrlByHttpPost(url, listdata);
	}

	/**
	 * 使用HttpGet 请求数据
	 *
	 * @param url
	 * @param onRequestListener
	 * @param tag
	 * @return
	 */
	public static String requestDataByHttpGet(String url, OnRequestListener onRequestListener, int tag) {
		Request request = new Request(onRequestListener, tag);
		return request.getDataByHttpGet(url);
	}

	/**
	 * 使用HttpUrlConnection Get 请求数据
	 *
	 * @param url
	 * @param onRequestListener
	 * @param tag
	 * @return
	 */
	public static String requestDataByURLConnectionGet(String url, OnRequestListener onRequestListener, int tag) {
		Request request = new Request(onRequestListener, tag);
		return request.getDataByConnection(url);
	}

	/**
	 * 使用HttpUrlConnection POST 请求数据
	 *
	 * @param url
	 * @param onRequestListener
	 * @param tag
	 * @return
	 */
	public static String requestDataByURLConnectionPost(String url, OnRequestListener onRequestListener, byte[] postdata, int tag) {
		Request request = new Request(onRequestListener, tag);
		return request.postUrlByConnection(url, postdata);
	}

	/**
	 * �?��执行POST请求
	 *
	 * @param url
	 * @param postData
	 * @param listener
	 * @param tag
	 */
	private static void executeRequest(final String url, final byte[] postData, final OnRequestListener listener, final int tag) {
		final Request request = new Request(listener, tag);
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				request.postUrlByConnection(url, postData);
			}
		});
	}

	/**
	 * 执行Soap请求
	 * 该方法写的不错
	 * @param methodName
	 * @param map
	 * @param listener
	 * @param tag
	 */
	private static void executeRequest(final String methodName, final Map<String, Object> map, final OnRequestListener listener, final int tag) {
		final Request request = new Request(listener, tag);
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				request.soapWebService(methodName, map);
			}
		});
	}

	/**
	 * �?��Soap请求
	 *
	 * @param methodName
	 * @param map
	 * @param listener
	 * @param tag
	 */
	public static void requestDataBySoap(String methodName, Map<String, Object> map, OnRequestListener listener, int tag) {
		executeRequest(methodName, map, listener, tag);
	}

	/**
	 * 请求数据，直接返�?
	 *
	 * @param methodName
	 * @param map
	 * @return
	 */
	public static String requestDataBySoap(String methodName, Map<String, Object> map) {
		Request request = new Request(null, 0);
		return request.soapWebService(methodName, map);
	}

	/**
	 * �?��Soap请求
	 *
	 * @param methodName
	 * @param map
	 * @param listener
	 * @param tag
	 */
	public static void requestDataBySoapOther(final String methodName, final Map<String, Object> map, final OnRequestListener listener, final int tag) {
		final Request request = new Request(listener, tag);
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				// request.soapWebService(methodName, map);
				request.publicMethod(methodName, (HashMap<String, Object>) map);
			}
		});
	}

	// 工具==================
	/**
	 * Map转URL字段 key=value&key=value 连接起来
	 *
	 * @param map
	 * @return
	 */
	public static String mapToUrlStr(HashMap<String, String> map) {
		// Set<java.util.Map.Entry<String, String>> set = map.entrySet();

		Set<String> keysSet = map.keySet();
		Object[] keys = keysSet.toArray();
		Collection<String> valuesCollection = map.values();
		Object[] values = valuesCollection.toArray();
		StringBuffer sb = new StringBuffer(0);
		for (int i = 0; i < values.length; i++) {
			if (keys[i] == null) {
				keys[i] = "";
			}
			if (values[i] == null) {
				values[i] = "";
			}
			String str = keys[i].toString() + "=" + values[i].toString();
			if (i != values.length - 1) {
				sb.append(str + "&");
			} else {
				sb.append(str);
			}
		}
		return sb.toString();
	}

	// /**
	// * 取消当前线程
	// */
	// public static void cancelCurrentThread() {
	// BlockingQueue<Runnable> runnableQueue = mThreadPool.getQueue();
	// // try {
	// mThreadPool.shutdownNow();
	// // Runnable runnable = runnableQueue.take();
	// // if (runnable != null) {
	// // mThreadPool.remove(runnable);
	// // }
	// Log.e("http", "取消线程");
	// // } catch (InterruptedException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// }

	public static class Config {

		public static String WEB_SERVICE_URL;
	}


}
