package com.dldzkj.app.renxing.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.utils.FileUtils;
import com.dldzkj.app.renxing.utils.StringUtils;


public class UpdateSevice extends Service {
	public static final String Install_Apk = "Install_Apk";
	/******** download progress step *********/
	private static final int down_step_custom = 3;
	private static final int TIMEOUT = 10 * 1000;// 
	private static String down_url;
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;
	private String app_name;
	private MediaPlayer mp;
	private NotificationManager notificationManager;
	private Notification notification;
	private Intent updateIntent;
	private PendingIntent pendingIntent;
	private RemoteViews contentView;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		app_name = intent.getStringExtra("Key_App_Name");
		 down_url = intent.getStringExtra("Key_Down_Url");
		Log.e("", "======================================");
		FileUtils.createFileApk(app_name);
		if (FileUtils.isCreateFileSucess == true) {
			createNotification();
			createThread();
		} else {
			Toast.makeText(this, R.string.insert_card, Toast.LENGTH_SHORT)
					.show();
			/*************** stop service ************/
			stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	/********* update UI ******/
	private final Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_OK:
//				notificationManager.cancel(R.layout.notification_item);
//				mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);
//				mp.start();
//				mp.setOnCompletionListener(new OnCompletionListener() {
//					@Override
//					public void onCompletion(MediaPlayer mp) {
//						mp.release();
//					}
//				});
				
				SoundPool soundPool= new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
				soundPool.load(UpdateSevice.this,R.raw.beep,1);
				soundPool.play(1,1, 1, 0, 0, 1);
				// 
				// Uri uri = Uri.fromFile(FileUtils.updateFile);
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setDataAndType(uri,
				// "application/vnd.android.package-archive");
				// pendingIntent = PendingIntent.getActivity(UpdateSevice.this,
				// 0,
				// intent, 0);
				// notification.flags = Notification.FLAG_AUTO_CANCEL;
				// notification.setLatestEventInfo(UpdateSevice.this, app_name,
				// getString(R.string.down_sucess), pendingIntent);
				// //
				// notification.setLatestEventInfo(UpdateService.this,app_name,
				// // app_name + getString(R.string.down_sucess), null);
				// notificationManager.notify(R.layout.notification_item,
				// notification);
				/***** *****/
				clearNotify();
				/***** ****/
				installApk();
				/*** stop service *****/
				stopSelf();
				break;

			case DOWN_ERROR:
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				Intent intent = new Intent(UpdateSevice.this,
						UpdateSevice.class);
				intent.putExtra("Key_App_Name", "AoGu");
				intent.putExtra("Key_Down_Url", MyApplication.SERVER_CONFIG_LOADPATH);
				pendingIntent = PendingIntent.getService(UpdateSevice.this, 0,
						intent, 0);
				notification.setLatestEventInfo(UpdateSevice.this, app_name,
						getString(R.string.down_fail), pendingIntent);
				notificationManager.notify(R.layout.notification_item,
						notification);
				/*** stop service *****/
				// onDestroy();
				stopSelf();
				break;

			default:
				// stopService(updateIntent);
				/****** Stop service ******/
				// stopService(intentname)
				// stopSelf();
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		if (mp != null)
			mp.release();
	};
	private void clearNotify() {
		notificationManager.cancel(R.layout.notification_item);
		
	}
	private void installApk() {
		// TODO Auto-generated method stub
		Uri uri = Uri.fromFile(FileUtils.updateFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		UpdateSevice.this.startActivity(intent);
	}

	public void createThread() {
		new DownLoadThread().start();
	}

	private class DownLoadThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			try {
				long downloadSize = downloadUpdateFile(down_url,
						FileUtils.updateFile.toString());
				if (downloadSize > 0) {
					// down success
					message.what = DOWN_OK;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWN_ERROR;
				handler.sendMessage(message);
			}
		}
	}

	public void createNotification() {
		notification = new Notification(R.drawable.ic_launcher,// 
				app_name + getString(R.string.is_downing),
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		contentView = new RemoteViews(getPackageName(),
				R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, app_name
				+ getString(R.string.is_downing));
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(R.layout.notification_item, notification);
	}

	/***
	 * down file
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file)
			throws Exception {

		int totalSize;// 
		int downloadCount = 0;// 
		int updateCount = 0;// 
		InputStream inputStream;
		OutputStream outputStream;

		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		totalSize = httpURLConnection.getContentLength();

		if (httpURLConnection.getResponseCode() == 404) {
			contentView.setTextViewText(R.id.notificationPercent,
					 StringUtils.getStrFromRes(getApplicationContext(), R.string.app_update_toast));
			throw new Exception("fail!");
			}

		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// 

		byte buffer[] = new byte[1024];
		int readsize = 0;
		Log.d("zxl", "totalSize="+totalSize);
		while ((readsize = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;// 	
			int ps=downloadCount*100/totalSize;
			if (updateCount == 0
					|| (ps - down_step_custom) >= updateCount) {
				updateCount += down_step_custom;
				
				contentView.setTextViewText(R.id.notificationPercent,
						ps + "%");
				contentView.setProgressBar(R.id.notificationProgress, 100,
						ps, false);
				notification.contentView = contentView;
				notificationManager.notify(R.layout.notification_item,
						notification);
			}
		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();

		return downloadCount;
	}
}
