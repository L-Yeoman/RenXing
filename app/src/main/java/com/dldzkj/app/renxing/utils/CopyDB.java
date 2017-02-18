package com.dldzkj.app.renxing.utils;

import android.content.Context;
import android.util.Log;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CopyDB {
	private static final String TAG = "CopyDB";

	/**
	 * 拷贝asset中的文件到指定位置
	 * 
	 * @param context
	 *            上下文
	 * @param assetFilename
	 *            需要拷贝的文件名
	 * @param destPath
	 *            指定的路径
	 */
	public static void copyDB(Context context, String assetFilename, String destPath) {
		try {
			Log.e(TAG, "拷贝asset文件：" + assetFilename + "至" + destPath);
			makeRootDirectory(Location.DB_ADDRESS);
			InputStream is = context.getAssets().open(assetFilename);
			FileOutputStream fos = new FileOutputStream(destPath);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
		} catch (IOException e) {
			Log.e(TAG, "拷贝asset文件出错：" + assetFilename);
			e.printStackTrace();
		}
		Log.e(TAG, "完成拷贝asset文件：" + assetFilename);
	}
	
	 public static void makeRootDirectory(String filePath) {  
	        File file = null;  
	        try {  
	            file = new File(filePath);  
	            if (!file.exists()) {  
	                file.mkdir();  
	            }  
	        } catch (Exception e) {  
	  
	        }  
	    }  
}
