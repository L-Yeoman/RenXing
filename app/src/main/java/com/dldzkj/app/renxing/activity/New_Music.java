package com.dldzkj.app.renxing.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.Playlist;
import com.dldzkj.app.renxing.blelib.services.MusicService;
import com.dldzkj.app.renxing.blelib.services.MusicUtil;

import de.hdodenhof.circleimageview.CircleImageView;


public class New_Music extends BaseOtherActivity implements OnClickListener{

	private CircleImageView iv_album; //
	private ImageView iv_back; // 
	private ImageView iv_list; // 
	private  boolean volume0=false; // 
	private ImageView volume100; // 
	private ImageView iv_random; //
//	private ImageView iv_circle; // 
	private int model=1;
	private final int IS_RANDOM=1;
	private final int RANDOM_NO=2;
	private final int IS_CIRCLE=3;
	private ImageView iv_last; // 
	public ImageView iv_next; // 
	private SeekBar cb_progress; // 
	private ImageView musicPlays; // 
	private TextView tv_time; // 
	private TextView totle_time;
	private TextView music_name; // 
	private TextView singer; //
	public static boolean isStart;
	private RotateAnimation animation;
	// private SeekBar sb_volume; // 
	private AudioManager mAudioManager;//控制音量大小
//	public static  PlayFlashView flashView;
	private MyApplication mApp;
	private  Bitmap bmp;
	private Handler h=new Handler(){
		@Override
			public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				setStatus();
				break;
			case 1:
				musicPlays.setImageResource(R.drawable.play);
				break;
			case 2:
				if (mApp.mMusicServer!=null&&mApp.mMusicServer.isPlaying()) {
					tv_time.setText(toTime(mApp.mMusicServer.getCurrentTime()));
					cb_progress.setMax(mApp.mMusicServer.getDuration());
					cb_progress.setProgress(mApp.mMusicServer.getCurrentTime());
				}
				h.sendEmptyMessageDelayed(2,500);
				break;
			default:
				break;
			}
				super.handleMessage(msg);
			}	
		};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_music);
		mApp=(MyApplication) getApplication();
		mApp.mStack.add(this);
		if(mApp.mMusicServer == null) {
			startMusicService();
		}
		init();
		initAnim();
	}
	public void startImgRotate(){
		iv_album.startAnimation(animation);
	}
	public void stopImgRotate(){
		iv_album.clearAnimation();// 清除此ImageView身上的动画
	}
	private void initAnim(){
		animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(5000);
		LinearInterpolator lin = new LinearInterpolator();  
		animation.setInterpolator(lin);
		animation.setRepeatCount(Animation.INFINITE);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		if(keyCode==KeyEvent.KEYCODE_VOLUME_UP&&volume0){
			volume100.setImageResource(R.drawable.music_volume_100);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void init() {
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		iv_album = (CircleImageView) findViewById(R.id.iv_album);
		iv_album.setBorderColor(Color.rgb(108, 62, 62));
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_list = (ImageView) findViewById(R.id.iv_list);
	//	volume0 = (ImageView) findViewById(R.id.volume0);
		volume100 = (ImageView) findViewById(R.id.volume);
		iv_random = (ImageView) findViewById(R.id.iv_random_change);
	//	iv_circle = (ImageView) findViewById(R.id.iv_circle);
		iv_last = (ImageView) findViewById(R.id.iv_last);
		iv_next = (ImageView) findViewById(R.id.iv_next);
		cb_progress = (SeekBar) findViewById(R.id.cb_progress);
		cb_progress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					cb_progress.setProgress(progress);
					mApp.mMusicServer.setMediaPlayTime(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});
		// cb_progress.setmApp(mApp);
		musicPlays = (ImageView) findViewById(R.id.musicPlays);
		tv_time = (TextView) findViewById(R.id.tv_time);
		totle_time = (TextView) findViewById(R.id.tv_time_s);
		music_name = (TextView) findViewById(R.id.music_name);
		singer = (TextView) findViewById(R.id.singer);
//		flashView=(PlayFlashView) findViewById(R.id.play_layout1_flashView);

		iv_back.setOnClickListener(this);
		iv_list.setOnClickListener(this);
	//	volume0.setOnClickListener(this);
		volume100.setOnClickListener(this);
		iv_random.setOnClickListener(this);
	//	iv_circle.setOnClickListener(this);
		iv_last.setOnClickListener(this);
		iv_next.setOnClickListener(this);
		musicPlays.setOnClickListener(this);
		if (mApp.mMusicServer!=null) {
			mApp.mMusicServer.setHandler(h);
			h.sendEmptyMessage(2);
			mApp.mMusicServer.setMusicContext(this);
		}
	}

private  void setStatus() {
	//	Intent _Intent=new Intent(TNotify.RECEIVER_CHANGE);
	if (mApp.mMusicServer == null||!mApp.mMusicServer.isPlaying()) {
		return;
	}
		if (mApp.mMusicServer.getCurrentPlay() != null) {
			String song=mApp.mMusicServer.getCurrentPlay().getTitle();
			totle_time.setText(toTime(mApp.mMusicServer.getDuration()));
			singer.setText(mApp.mMusicServer.getCurrentPlay().getArtist());
			music_name.setText(song);
			tv_time.setText(toTime(mApp.mMusicServer.getCurrentTime()));
			cb_progress.setMax(mApp.mMusicServer.getDuration());
			cb_progress.setProgress(mApp.mMusicServer.getCurrentTime());
			if ( New_Music.this != null) {
				bmp = MusicUtil.getArtwork(New_Music.this,
						mApp.mMusicServer.getCurrentPlay().get_mid(),
						mApp.mMusicServer.getCurrentPlay().getAlbum_id(), true);
				iv_album.setImageBitmap(bmp);
				Log.e("new music", "broadcast");
			}
		}
	};

	private void initView() {
		if (mApp.mMusicServer != null) {
			if (mApp.mMusicServer.isPlaying())
				musicPlays.setImageResource(R.drawable.pause);
			else
				musicPlays.setImageResource(R.drawable.play);
			if (mApp.mMusicServer.isRandom()&&!mApp.mMusicServer.isLoop()) {
			//	iv_circle.setImageResource(R.drawable.circle_no);
				iv_random.setImageResource(R.drawable.random);
			}  else if(!mApp.mMusicServer.isRandom()&&!mApp.mMusicServer.isLoop()){
				//	iv_circle.setImageResource(R.drawable.all_circle);
					iv_random.setImageResource(R.drawable.random_no);
				}else if(mApp.mMusicServer.isLoop()) {
					iv_random.setImageResource(R.drawable.circle); 
				}
			}
	}
	@Override
	public void onPause() {
		super.onPause();
		isStart=false;
		if (mApp.mMusicServer!=null&&mApp.mMusicServer.isPlaying()) {
			stopImgRotate();
		}
	}

	@Override
	public void onResume() {
		isStart=true;
		iv_list.setEnabled(true);
		initView();
		setStatus();
		if (mApp.mMusicServer!=null&&mApp.mMusicServer.isPlaying()) {
			startImgRotate();
		}
		//home键监听
	    super.onResume();
	}

	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_list:
			startActivity(new Intent(this, MusicList.class));
			v.setEnabled(false);
			break;
	
		case R.id.volume:
			volume0=!volume0;
			if(!volume0){
				mAudioManager
				.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				volume100.setImageResource(R.drawable.music_volume_100);
			}else{
				mAudioManager
				.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
				volume100.setImageResource(R.drawable.down);
			}
			
			break;
		case R.id.iv_random_change:
			
			if (mApp.mMusicServer != null) {
			
				if(model==IS_RANDOM){
					Log.e("model","随机" );
					mApp.mMusicServer.setRandom(true);
					mApp.mMusicServer.setLoop(false);
				
					model=RANDOM_NO;
				}
				else if(model==IS_CIRCLE){
					Log.e("model","单曲" );
					mApp.mMusicServer.setLoop(true);
					model=IS_RANDOM;
				}
				else if(model==RANDOM_NO){
					Log.e("model","循环" );
					mApp.mMusicServer.setRandom(false);
					mApp.mMusicServer.setLoop(false);
					model=IS_CIRCLE;
				}
			}
			Log.e("modle", mApp.mMusicServer.isRandom+"isRandom");
			Log.e("modle", mApp.mMusicServer.isLoop+"isLoop");
			initView();
			break;
		case R.id.iv_last:
			if (mApp.mMusicServer != null) {
				Log.e("modle", mApp.mMusicServer.isRandom()+"isRandom");
				Log.e("modle", mApp.mMusicServer.isLoop()+"isLoop");
				mApp.mMusicServer.last();
				setStatus();
				mApp.mMusicServer.updateNotification(bmp);
			}
			initView();
			break;
		case R.id.iv_next:
			Log.e("modle", mApp.mMusicServer.isRandom()+"isRandom");
			Log.e("modle", mApp.mMusicServer.isLoop()+"isLoop");
			if (mApp.mMusicServer != null) {
				mApp.mMusicServer.next();
				setStatus();
				mApp.mMusicServer.updateNotification(bmp);
			}
			initView();
			break;
		case R.id.musicPlays:
			if (mApp.mMusicServer.isPlaying()) {
				musicPlays.setImageResource(R.drawable.play); 
				mApp.mMusicServer.pause();
				mApp.mMusicServer.cancelNotification();
			} else {
				if (mApp.mMusicServer.getPlayList() == null) {//没有播放列表
					List<Playlist> list = MusicUtil.getMp3List(this,
							MyApplication.getInstance().getDbUtils());
					if (list == null || list.size() <= 0) {
						Toast.makeText(
								mApp,
								getResources().getString(R.string.music_no_one),
								Toast.LENGTH_SHORT).show();
						return;
					}
					mApp.mMusicServer.setCurrentPlayList(list);
					mApp.mMusicServer.setCurrentPlayMusic(list.get(0));
				}
				musicPlays.setImageResource(R.drawable.pause);
				mApp.mMusicServer.play();
				setStatus();
				mApp.mMusicServer.updateNotification(bmp);
			}
			// initView();
			break;
		default:
			break;
		}
	}
	private void startMusicService() {
		Intent intent = new Intent(this, MusicService.class);
		bindService(intent, mConnection, BIND_AUTO_CREATE);
	
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mApp.mBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
			mApp.mMusicServer = binder.getService();
			mApp.mBound = true;
			initMusic();
		}
	};
	private HashMap<String, ArrayList<Playlist>> musicMap;

	private void initMusic() {

		new Thread() {
			@Override
			public void run() {

				super.run();
				if (musicMap == null) {
					musicMap = MusicUtil.getMp3Lists(New_Music.this, MyApplication.getInstance().getDbUtils());
					if (musicMap != null) {
						mApp.musicList = musicMap.get("musicList"); 
						mApp.favList = musicMap.get("favList");
						mApp.mMusicServer.setCurrentPlayList(mApp.musicList);

						mApp.mMusicServer
								.setCurrentPlayMusic(mApp.musicList.get(0));
					}

				}
			}

		}.start();

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("dddd", "onDestroy()");
		super.onDestroy();
	}
}
