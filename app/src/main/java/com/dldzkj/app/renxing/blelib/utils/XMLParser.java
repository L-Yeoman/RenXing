/**   
 * @Title: XMLParser.java 
 * @Package cn.com.zhoufu.mouth.utils 
 * @Description: TODO(用一句话描述该文件做�?��) 
 * @author 王小�?  
 * @date 2014-2-20 上午9:15:21
 * @version V1.0   
 */

package com.dldzkj.app.renxing.blelib.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Xml;

public class XMLParser {

	public Map<String, String> ParseUpdateInfo(String str) {

		Map<String, String> map = new HashMap<String, String>();

		String str1 = "<update" + " xmlns=\"http://tempuri.org/\"" + str.substring("<update".length() + 1, str.length());

		StringReader sr = new StringReader(str1);
		try {
			XmlPullParser xml = Xml.newPullParser();
			xml.setInput(sr);
			int type = xml.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {

				String TagName = xml.getName();

				switch (type) {
				case XmlPullParser.START_TAG:
					if ("androidversion".equals(TagName)) {
						map.put("androidversion", xml.nextText());
					} else if ("androidcontent".equals(TagName)) {
						map.put("androidcontent", xml.nextText());
					} else if ("androidurl".equals(TagName)) {
						map.put("androidurl", xml.nextText());
					}
					break;
				}
				type = xml.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Getting XML from URL making HTTP request
	 * 
	 * @param url
	 *            string
	 * */
	public String getXmlFromUrl(String url) {
		String xml = null;
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new HttpPost(url);
			HttpGet httpget = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpget);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return XML
		return xml;
	}


	public Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
//			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
//			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
//			Log.e("Error: ", e.getMessage());
			return null;
		}

		return doc;
	}

	/**
	 * Getting node value
	 * 
	 * @param elem
	 *            element
	 */
	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}


	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	public static int getVersionCode(Context context) throws Exception {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		int version = packInfo.versionCode;
		return version;
	}

	public static String getVersionName(Context context) throws Exception {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		return packInfo.versionName;
	}
	

	/**
	 * �?��是否有网络连�?
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isConnected()) {
						return true;
					}

				}
			}
		}
		return false;
	}

}
