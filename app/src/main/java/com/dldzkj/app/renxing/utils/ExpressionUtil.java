package com.dldzkj.app.renxing.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;


public class ExpressionUtil {
    /** 保存于内存中的表情HashMap */
    private static HashMap<String, String> emojiMap = new HashMap<String, String>();

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 */

    private static  void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start,TextView tv) throws Exception {
    	Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            String value = emojiMap.get(key);
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            int resId = context.getResources().getIdentifier(value, "drawable",
                    context.getPackageName());
//            Field field = R.drawable.class.getDeclaredField(key);
//			int resId = Integer.parseInt(field.get(null).toString());
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                int size = (int) tv.getTextSize()*2;
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                ImageSpan imageSpan = new ImageSpan(context,scaleBitmap);
                int end = matcher.start() + key.length();					
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);	
                if (end < spannableString.length()) {						
                    dealExpression(context,spannableString,  patten, end,tv);
                }
                break;
            }
        }
    }
    
    public  static SpannableString getExpressionString(Context context,String str,String zhengze,TextView tv){
    	SpannableString spannableString = new SpannableString(str);
    	//启用不区分大小写的匹配。
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);		
        try {
            dealExpression(context, spannableString, sinaPatten, 0,tv);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }



    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    private static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<String>();
            InputStream in = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(in,
                    "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getFileText(Context context) {
        ParseData(getEmojiFile(context), context);
    }

    /**
     * 解析字符
     *
     * @param data
     */
    private static void ParseData(List<String> data, Context context) {
        if (data == null) {
            return;
        }
        for (String str : data) {
                String[] text = str.split(",");
                String fileName = text[0]
                        .substring(0, text[0].lastIndexOf("."));
                emojiMap.put(text[1], fileName);
            }
    }
}