package com.dldzkj.app.renxing.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.dldzkj.app.renxing.bean.ConstantValue;

public class StringUtils {
	public static HashMap<String, Object> toJson(Map<String, Object> requestMap) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();

		sb.append("{");
		for (String key : requestMap.keySet()) {
			sb.append("\"" + key + "\":\"" + requestMap.get(key) + "\",");
		}
		sb.replace(sb.length() - 1, sb.length(), "");
		sb.append("}");
		map.put("Json", sb.toString());
		return map;
	}

	public static String Double2String(double d) {
		DecimalFormat fnum = new DecimalFormat("##0.00");
		return fnum.format(d);
	}

	public static String getStrFromRes(Context c, int id) {
		return c.getResources().getString(id);
	}

	public static boolean isMObileNO(String phoneNumber) {
		boolean isValid = false;

		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";

		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);

		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}


	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	public static String getTodayStartStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date d = new Date();
		return sdf.format(d);
	}

	public static boolean isEmpty(String s) {
		return s == null || "".equals(s);
	}

	public static String getStrFronDate(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(d);
	}

	public static Date getDateFronStr(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			return sdf.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
/********对String进行加密解密******/

	public  static String EncodeBase64Str(String str){
		// 在这里使用的是encode方式，返回的是byte类型加密数据，可使用new String转为String类型
		String strBase64 = new String(Base64.encode(str.getBytes(), Base64.NO_WRAP));
		return  strBase64;
	}
	public  static String DecodeBase64Str(String strBase64){
		String str =new String(Base64.decode(strBase64.getBytes(), Base64.NO_WRAP));
		return  str;
	}
	public  static String EncodeURLStr(String str){
		// 在这里使用的是encode方式，返回的是byte类型加密数据，可使用new String转为String类型
		String strurl="";
		try {
			strurl= URLEncoder.encode(str,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return  strurl;
	}
	public  static String DecodeURLStr(String strBase64){
		String strurl="";
		try {
			strurl= URLDecoder.decode(strBase64, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return  strurl;
	}
public static SpannableString setFaceToTextView(Context c,String str,TextView tv){
	SpannableString spannableString=null;
	try {
		spannableString = ExpressionUtil
				.getExpressionString(c, str, ConstantValue.PATTER_FACE,tv);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return spannableString;
}
}
