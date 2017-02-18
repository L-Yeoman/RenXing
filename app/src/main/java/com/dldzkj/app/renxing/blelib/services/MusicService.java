package com.dldzkj.app.renxing.blelib.services;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import uk.co.alt236.bluetoothlelib.util.ByteUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.New_Music;
import com.dldzkj.app.renxing.activity.blue.BlueBeforeUIActivity;
import com.dldzkj.app.renxing.bean.Playlist;


public class MusicService extends Service {
	private MyApplication mApp;
	// private static int SAMPLE_RATE_IN_HZ = 8000;
	private MediaPlayer mMediaPlayer;
	private AudioManager mAudioManager;
	private List<Playlist> playingList;

	private Playlist currentMusic;
	private RemoteViews rv;
	private Playlist lastMusic;
	private static final String PAUSE_BROADCAST_NAME = "com.dlkj.music.pause.broadcast";
	private static final String NEXT_BROADCAST_NAME = "com.dlkj.music.next.broadcast";
	private static final String PRE_BROADCAST_NAME = "com.dlkj.music.pre.broadcast";
	private static final int PAUSE_FLAG = 0x1;
	private static final int NEXT_FLAG = 0x2;
	private static final int PRE_FLAG = 0x3;
	public boolean isLoop = false;
	public boolean isRandom = false;
	// 通知栏
	private NotificationManager mNotificationManager;
	private int NOTIFICATION_ID = 0x1;
	private ControlBroadcast mConrolBroadcast;
	public final IBinder mBinder = new MusicBinder();
	private Random r = new Random();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// playNext();
				if (c != null) {
					c.iv_next.performClick();
				}
			}
		});
		mApp = (MyApplication) getApplication();
		handler.sendEmptyMessageDelayed(1002, 200);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mConrolBroadcast = new ControlBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PAUSE_BROADCAST_NAME);
		filter.addAction(NEXT_BROADCAST_NAME);
		filter.addAction(PRE_BROADCAST_NAME);
		filter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		registerReceiver(mConrolBroadcast, filter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public class MusicBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	public void setCurrentPlayList(List<Playlist> list) {
		playingList = list;
	}

	public void setCurrentPlayMusic(Playlist music) {
		if (mMediaPlayer == null) {
			return;
		}
		if (currentMusic == null || !music.equals(currentMusic)) {
			Uri uri = Uri.withAppendedPath(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					"" + music.get_mid());
			try {
				mMediaPlayer.reset();//
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setDataSource(this, uri);//
				mMediaPlayer.setLooping(isLoop);
				mMediaPlayer.prepare();
				this.currentMusic = music;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				mMediaPlayer.prepare();
				mMediaPlayer.seekTo(0);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Playlist getCurrentPlay() {
		return currentMusic;
	}

	public void play() {
		Log.e("play", "is play");
		if (!mMediaPlayer.isPlaying()) {
			mMediaPlayer.start();
		}
		// 更新播放动画
		c.startImgRotate();
//		startFlashUpdate();
		sendBroadcast(new Intent(MyApplication.ACTION_CHANGE_MUSIC));
	}

	public void pause() {
		// 暂停跟新GIF动画
		if (New_Music.isStart) {
			c.stopImgRotate();
//			New_Music.flashView.updatePicthNum(0);
		}
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
		}
		handler.sendEmptyMessageDelayed(1003, 1000);
	}

	public void stop() {
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
	}

	public void next() {
		Log.i("service", "next");
		if (playingList == null) {
			return;
		}
		stop();
		playNext();

	}

	public void last() {
		if (playingList == null) {
			return;
		}
		stop();

		if (!isRandom) {
			if (playingList != null) {
				int index = playingList.indexOf(currentMusic);
				if (index != -1 && index != 0)
					lastMusic = playingList.get(index - 1);
				else
					lastMusic = playingList.get(playingList.size() - 1);
				// lastMusic = playingList.get(playingList.size() - 1);
			}
		} else {
			Log.e("last", "随机");
			lastMusic = playingList.get(getRandomIndex(playingList.size()));
		}
		setCurrentPlayMusic(lastMusic);
		currentMusic = lastMusic;
		play();

	}

	private void playNext() {

		if (playingList != null) {
			int index = playingList.indexOf(currentMusic);
			lastMusic = currentMusic;
			Playlist tempMusic;
			if (!isRandom) {
				if (index < playingList.size() - 1 && playingList.size() != 0) {
					tempMusic = playingList.get(index + 1);
				} else {
					tempMusic = playingList.get(0);
				}
			} else {
				tempMusic = playingList.get(getRandomIndex(playingList.size()));
			}
			if (tempMusic != null) {
				setCurrentPlayMusic(tempMusic);
				currentMusic = tempMusic;
				// status();
				play();
			}
		}
	}

	public boolean isPlaying() {
		if (mMediaPlayer != null)
			return mMediaPlayer.isPlaying();
		else
			return false;
	}

	public void setLoop(boolean isLoop) {
		// setRandom(false);
		mMediaPlayer.setLooping(isLoop);
		this.isLoop = isLoop;
	}

	public boolean isLoop() {
		return isLoop;
	}

	public void setRandom(boolean isRandom) {
		mMediaPlayer.setLooping(false);
		this.isRandom = isRandom;
		Log.e("isRandom", "" + isRandom);
	}

	public boolean isRandom() {
		return isRandom;
	}

	public int getCurrentTime() {
		return mMediaPlayer.getCurrentPosition();
	}

	public void setMediaPlayTime(int progress) {
		mMediaPlayer.seekTo(progress);
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	public int getAudioVolume() {
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void setAudioVolume(int progress) {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
	}

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	public List<Playlist> getPlayList() {
		return playingList;
	}

	private int getRandomIndex(int size) {
		return new Random().nextInt(size);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if (mConrolBroadcast != null) {
			unregisterReceiver(mConrolBroadcast);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1001:
				break;

			case 1002:
				if (mApp.BlueServiceIsConnnected() && mApp.mMusicServer != null
						&& mApp.mMusicServer.isPlaying()) {
					int a = 400 + r.nextInt(300);
					mApp.sendBlueOrder(a);
				}
				handler.sendEmptyMessageDelayed(1002, 50);
				break;
			case 1003:
				if (mApp.BlueServiceIsConnnected()) {
					mApp.sendBlueOrder(400);
					// stopVolume();
				}
				break;
			}

		};
	};

	/**
	 * 更新屏幕歌曲动画
	 */
	/*private void startFlashUpdate() {
		 flahHandler.post(flahsRun);
	}

	Handler flahHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x0f) {
				New_Music.flashView.invalidate();
			}
		};
	};
	int timer = 0;
	Runnable flahsRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			timer++;
			if (timer >= 30) {
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()
						&& New_Music.isStart) {
					if (mMediaPlayer.getCurrentPosition() > 10000
							&& mMediaPlayer.getDuration()
									- mMediaPlayer.getCurrentPosition() > 10000) {
						New_Music.flashView.updatePicthNum(100);
					} else {
						New_Music.flashView.updatePicthNum(50);
					}
					timer = 0;
				}
			}
			if (New_Music.isStart) {
				Message msg = new Message();
				msg.what = 0x0f;
				flahHandler.sendMessage(msg);
			}
			flahHandler.postDelayed(flahsRun, 250);
		}
	};*/
	private Handler h;

	public void setHandler(Handler h) {
		this.h = h;
	}

	private New_Music c;

	public void setMusicContext(Context c) {
		this.c = (New_Music) c;
	}

	/************* 以下为更新通知栏方法 *************/
	public void updateNotification(Bitmap bitmap) {
		if (currentMusic==null) {
			return;
		}
		Intent intent = new Intent(getApplicationContext(), New_Music.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
				0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv = new RemoteViews(this.getPackageName(), R.layout.notification);
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = currentMusic.getTitle();
		notification.contentIntent = pi;
		notification.contentView = rv;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		if (bitmap != null) {
			rv.setImageViewBitmap(R.id.image, bitmap);
		} else {
			rv.setImageViewResource(R.id.image, R.drawable.img_album_background);
		}
		rv.setTextViewText(R.id.title, currentMusic.getTitle());
		rv.setTextViewText(R.id.text, currentMusic.getArtist());
		// mNotificationManager.notify(NOTIFICATION_ID, mNotification);

		// 此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
		Intent pauseIntent = new Intent(PAUSE_BROADCAST_NAME);
		pauseIntent.putExtra("FLAG", PAUSE_FLAG);
		PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0,
				pauseIntent, 0);
		rv.setOnClickPendingIntent(R.id.iv_pause, pausePIntent);

		Intent nextIntent = new Intent(NEXT_BROADCAST_NAME);
		nextIntent.putExtra("FLAG", NEXT_FLAG);
		PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0,
				nextIntent, 0);
		rv.setOnClickPendingIntent(R.id.iv_next, nextPIntent);

		Intent preIntent = new Intent(PRE_BROADCAST_NAME);
		preIntent.putExtra("FLAG", PRE_FLAG);
		PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0,
				preIntent, 0);
		rv.setOnClickPendingIntent(R.id.iv_previous, prePIntent);

		startForeground(NOTIFICATION_ID, notification);
	}

	private class ControlBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mApp.finishAct(BlueBeforeUIActivity.class);
				Toast.makeText(mApp,
						mApp.getResources().getString(R.string.ble_disconnect),
						Toast.LENGTH_SHORT).show();
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				final byte[] dataArr = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_RAW);
				String dataStr = ByteUtils.byteArrayToHexString(dataArr);
				String[] data = BlueBeforeUIActivity.String2Array(dataStr);
				if ("40".equals(data[2].trim())) {// ui数据

				} else if ("00".equals(data[1].trim())
						&& "00".equals(data[2].trim())
						&& "00".equals(data[3].trim())) {// 电量数据
					int value = Integer.valueOf(data[0].trim(), 16);
					if (value <= 10) {
						Toast.makeText(mApp, R.string.low_power, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
			int flag = intent.getIntExtra("FLAG", -1);
			switch (flag) {
			case PAUSE_FLAG:
				pause();
				h.sendEmptyMessage(1);
				cancelNotification();
				break;
			case NEXT_FLAG:
				next();
				h.sendEmptyMessage(0);
				updateNotification(MusicUtil.getArtwork(c,
						currentMusic.get_mid(), currentMusic.getAlbum_id(), true));
				break;
			case PRE_FLAG:
				last();
				h.sendEmptyMessage(0);
				updateNotification(MusicUtil.getArtwork(c,
						currentMusic.get_mid(), currentMusic.getAlbum_id(), true));
				break;
			}
		}
	}

	public void cancelNotification() {
		stopForeground(true);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

}
