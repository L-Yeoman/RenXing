package com.dldzkj.app.renxing.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

public class FileUtils {

	public static void saveBitmap(Bitmap bm, String picName, String path) {
		try {
			if (!isFileExist("", path)) {
				File tempf = createSDDir("", path);
			}
			File f = new File(path, picName + ".JPEG");
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File createSDDir(String dirName, String path)
			throws IOException {
		File dir = new File(path + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}
public static String getPathFronUri(Activity c,Uri uri){
	String[] proj = { MediaStore.Images.Media.DATA };

	Cursor actualimagecursor = c.managedQuery(uri, proj, null, null, null);

	int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

	actualimagecursor.moveToFirst();

	String img_path = actualimagecursor.getString(actual_image_column_index);
	return  img_path;
}
	public static boolean isFileExist(String fileName, String path) {
		File file = new File(path + fileName);
		file.isFile();
		return file.exists();
	}

	public static void delFile(String fileName, String path) {
		File file = new File(path + fileName);
		if (file.isFile()) {
			file.delete();
		}
		file.exists();
	}

	public static void deleteDir(String path) {
		File dir = new File(path);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除�?��文件
			else if (file.isDirectory())
				deleteDir(path); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * 从文件中获取字节
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] getByteFrom(File file) {

		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int index = -1;
			byte[] data = new byte[1024];
			while ((index = fis.read(data)) != -1) {
				baos.write(data, 0, index);
				baos.flush();
			}
			baos.close();
			fis.close();
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 创建文件 并且写入内容
	 * 
	 * @param path
	 *            绝对路径 不存在则创建
	 * @param data
	 *            数据
	 */
	public static void createFile(String path, byte data[]) {
		// File f = new File(Config.AppDirTemp + "123.txt");
		try {
			File file = new File(path);
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	/**
	 * 创建�?��空文�?
	 * 
	 * @param path
	 */
	public static void createFile(String path) {

		try {
			File file = new File(path);
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param path
	 * @param is
	 */
	public static boolean createFile(String path, InputStream is) {
		try {
			createFile(path);
			FileOutputStream fos = new FileOutputStream(path);
			int index = -1;
			byte data[] = new byte[1024];
			while ((index = is.read(data)) != -1) {
				fos.write(data, 0, index);
			}
			fos.close();
			is.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static boolean isCreateFileSucess;
	public static File updateDir = null;
	public static File updateFile = null;
	/*********** 保存升级APK的目�?***********/
	public static final String AoGu = "AoGu";
	/**
	 * 方法描述：createFile方法

	 */
	public static void createFileApk(String app_name) {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			isCreateFileSucess = true;
			updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/" + AoGu + "/");
			updateFile = new File(updateDir + "/" + app_name + ".apk");
			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {

					isCreateFileSucess = false;
					e.printStackTrace();
				}
			}
		} else {

			isCreateFileSucess = false;
		}
	}
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		return baos.toByteArray();
	}
	public static byte[] Bitmap2Bytes1(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
		return baos.toByteArray();
	}
	public static  byte[] getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();

		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);
		
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;

		float hh = 800f;
		float ww = 480f;
		//���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ����ݽ��м��㼴��
		int be = 1;//be=1��ʾ������
		if (w > h && w > ww) {//����ȴ�Ļ���ݿ�ȹ̶���С����
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//���߶ȸߵĻ���ݿ�ȹ̶���С����
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//�������ű���
		//���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);//ѹ���ñ����С���ٽ�������ѹ��
	}
	private static byte[] compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 80, baos);//����ѹ������������100��ʾ��ѹ������ѹ�������ݴ�ŵ�baos��
		int options = 100;
		while ( baos.toByteArray().length / 1024>100) {	//ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��		
			baos.reset();//����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ�������ݴ�ŵ�baos��
			options -= 10;//ÿ�ζ�����10
		}
//		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ��������baos��ŵ�ByteArrayInputStream��
//		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream������ͼƬ
		return baos.toByteArray();
	}
	public static byte[] File2byte(String filePath)  
    {  
        byte[] buffer = null;  
        try  
        {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream();  
            byte[] b = new byte[1024];  
            int n;  
            while ((n = fis.read(b)) != -1)  
            {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        }  
        catch (FileNotFoundException e)  
        {  
            e.printStackTrace();  
        }  
        catch (IOException e)  
        {  
            e.printStackTrace();  
        }  
        return buffer;  
    }

	public static String encodeBase64File(String path) throws Exception {
		File  file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int)file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return Base64.encodeToString(buffer, Base64.DEFAULT);
	}
	public static void decoderBase64File(String base64Code,String savePath) throws Exception {
		byte[] buffer =Base64.decode(base64Code, Base64.DEFAULT);
		FileOutputStream out = new FileOutputStream(savePath);
		out.write(buffer);
		out.close();
	}
	public static void byte2File(byte[] buf, String filePath, String fileName)  
    {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try  
        {  
            File dir = new File(filePath);  
            if (!dir.exists() && dir.isDirectory())  
            {  
                dir.mkdirs();  
            }  
            file = new File(filePath + File.separator + fileName);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(buf);  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
        finally  
        {  
            if (bos != null)  
            {  
                try  
                {  
                    bos.close();  
                }  
                catch (IOException e)  
                {  
                    e.printStackTrace();  
                }  
            }  
            if (fos != null)  
            {  
                try  
                {  
                    fos.close();  
                }  
                catch (IOException e)  
                {  
                    e.printStackTrace();  
                }  
            }  
        }  
    } 
}
