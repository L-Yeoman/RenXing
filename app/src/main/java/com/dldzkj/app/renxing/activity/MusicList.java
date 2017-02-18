package com.dldzkj.app.renxing.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.Playlist;
import com.dldzkj.app.renxing.blelib.services.MusicUtil;
import com.dldzkj.app.renxing.blelib.services.PlaylistAdapter;
import com.dldzkj.app.renxing.blelib.services.ResolverSer;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

/**
 * 音乐播放列表
 * @author Administrator
 *
 */
public class MusicList extends	Activity implements OnItemClickListener,OnClickListener {
	private ArrayList<Playlist> currentList; // 当前播放的音乐列表
	private ArrayList<Playlist> list; // 音乐列表
	private ArrayList<Playlist> favList; // 收藏列表
	private ArrayList<Playlist> searchList; // 搜索列表
	private PlaylistAdapter listAdapter;
	private PlaylistAdapter favoritAdapter;
	private ListView mLvPlay;
	private Playlist playlist;
	private DbUtils dbUtils;

	private int currentPosition = 0;// 当前播放位置

	private int position;// 位置

	/**
	 * 定义查找音乐信息数组，1.标题，2音乐时间,3.艺术家,4.音乐id，5.显示名字,6.数据。
	 */
	String[] media_info = new String[] { MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
			MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID };

	private List<View> views; // Tab页面
	private List<TextView> titles; // 标题

	private ViewPager viewpager;
	private TextView tv_list;
	private TextView tv_favorite;
	private MyApplication mApp;
	private Context mAct=MusicList.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musiclist);
		View ln=findViewById(R.id.music_list_layout);
		init();
		setListener();
	}

	// 初始化
	protected void init() {
		dbUtils = MyApplication.getInstance().getDbUtils();
		mApp=(MyApplication) getApplication();
		mApp.mStack.add(this);
		list = mApp.musicList;
		favList = mApp.favList;
		searchList = new ArrayList<Playlist>();
		iv_listback = (ImageView) findViewById(R.id.iv_listback);
		iv_flush = (ImageView)findViewById(R.id.iv_listsearch);
		initViewpager();
		initViewTitles();
		initData();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (receiver == null) {
			receiver = new MusicBroadCast();
			IntentFilter filter = new IntentFilter();
			filter.addAction(MyApplication.ACTION_CHANGE_MUSIC);
			mAct.registerReceiver(receiver, filter);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (receiver != null) {
			mAct.unregisterReceiver(receiver);
			receiver = null;
		}
	}

	// 音乐改变广播接收者
	private class MusicBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 收到广播，刷新界面
			favoritAdapter.notifyDataSetChanged();
			listAdapter.notifyDataSetChanged();

		}

	}

	// 初始化Viewpager两个标签
	private void initViewTitles() {
		titles = new ArrayList<TextView>();
		tv_list = (TextView) findViewById(R.id.tv_musicList);
		tv_favorite = (TextView) findViewById(R.id.tv_favoriteList);
		
		titles.add(tv_list);
		titles.add(tv_favorite);

	}

	// 初始化Viewpager页面
	private void initViewpager() {
		viewpager = (ViewPager) findViewById(R.id.vp_music);
		lv_music = new ListView(MusicList.this);
		lv_favorite = new ListView(MusicList.this);
		lv_favorite.setBackgroundColor(Color.TRANSPARENT);
		lv_music.setBackgroundColor(Color.TRANSPARENT);
		lv_favorite.setSelector(new ColorDrawable(Color.TRANSPARENT));
		lv_music.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		/*lv_music.setCacheColorHint(Color.TRANSPARENT);
		lv_favorite.setCacheColorHint(Color.TRANSPARENT);*/
		views = new ArrayList<View>();
		views.add(lv_music);
		views.add(lv_favorite);

		viewpager.setAdapter(new MyViewPagerAdapter(views));
		viewpager.setCurrentItem(0);
		viewpager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	// 初始化音乐列表
	private void initData() {
		if (list == null)
			showMp3List();

		listAdapter = new PlaylistAdapter(MusicList.this, 0, mApp);
		// 当前显示的列表、另一个列表
		listAdapter.setList(list, favList);
		lv_music.setAdapter(listAdapter);

		favoritAdapter = new PlaylistAdapter(MusicList.this, 1, mApp);
		favoritAdapter.setList(favList, list);
		lv_favorite.setAdapter(favoritAdapter);

		position = mApp.musicPlayPosition;
		listAdapter.setSelectedPosition(position);
		lv_music.setSelection(position);

		listAdapter.notifyDataSetChanged();

		// 默认当前播放列表为音乐列表
		currentList = list;
	}

	// 设置监听
	protected void setListener() {
		tv_list.setOnClickListener(this);
		tv_favorite.setOnClickListener(this);
		lv_music.setOnItemClickListener(this);
		lv_favorite.setOnItemClickListener(this);
		iv_listback.setOnClickListener(this);
		iv_flush.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		
		// 当点击音乐列表时，设置标签的变化
		case R.id.tv_musicList:
			viewpager.setCurrentItem(0);
			allTitleNoSelect();
			tv_list.setTextColor(mApp.getResources().getColor(R.color.select_item_text));
			tv_favorite.setTextColor(mApp.getResources().getColor(R.color.selected_item_text));
			tv_favorite.setBackgroundColor(Color.TRANSPARENT);
			tv_list.setBackgroundColor(Color.TRANSPARENT);
			break;
		case R.id.tv_favoriteList:
			viewpager.setCurrentItem(1);
			allTitleNoSelect();
			tv_favorite.setTextColor(mApp.getResources().getColor(
					R.color.select_item_text));
			tv_list.setTextColor(mApp.getResources().getColor(R.color.selected_item_text));
			tv_favorite.setBackgroundColor(Color.TRANSPARENT);
			tv_list.setBackgroundColor(Color.TRANSPARENT);
			break;
		case R.id.iv_listback:
			finish();
			break;
		case R.id.iv_listsearch:
			initData();
			break;
		}
	}

	// 当点击音乐列表音乐时的操作
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.position = position;
		// 根据所点击选项卡来判断是哪个列表，刷新该列表。
		// 把当前播放列表改为点击音乐所在列表
		if (viewpager.getCurrentItem() == 0) {
			listAdapter.setSelectedPosition(position);
			listAdapter.notifyDataSetChanged();
			currentList = list;
		} else {
			favoritAdapter.setSelectedPosition(position);
			favoritAdapter.notifyDataSetChanged();
			currentList = favList;
		}
		// 得到当前选中的音乐，播放
		// 若当前播放的音乐id和所点击的一样，且正在播放，则暂停，否则播放
		mApp.mMusicServer.setCurrentPlayList(currentList);
		Playlist playlist = (Playlist) parent.getItemAtPosition(position);
		if (mApp.mMusicServer.getCurrentPlay() != null
				&& playlist.get_mid() == mApp.mMusicServer.getCurrentPlay()
						.get_mid() && mApp.mMusicServer.isPlaying()) {
		//	mApp.mMusicServer.pause();
			onClick(iv_listback);
		} else {
			mApp.mMusicServer.setCurrentPlayMusic(playlist);
			mApp.mMusicServer.play();
			mApp.mMusicServer.updateNotification(MusicUtil.getArtwork(MusicList.this,
					playlist.get_mid(), playlist.getAlbum_id(), true));
		}
	}

	// Viewpager适配器
	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	// viewpager页面改变监听
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			allTitleNoSelect();
			titles.get(arg0).setTextColor(
					mApp.getResources().getColor(R.color.select_item_text));
		//	titles.get(arg0).setBackgroundResource(R.drawable.text_bg_purple);

			if (arg0 != 0) {
				// favList.clear();
				// showMp3List();
				favoritAdapter.notifyDataSetChanged();
			} else {
				// list.clear();
				// showMp3List();
				listAdapter.notifyDataSetChanged();
			}

		}

	}

	/**
	 * 
	 * showMp3List
	 * 
	 * @Description: 歌曲列表
	 */
	private void showMp3List() {
		Cursor cursor =  new ResolverSer().getResolverSer().getResover(MusicList.this).query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, media_info, "",
				null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		/**
		 * 判断游标是否为空，有些地方即使没有音乐也会报异常。而且游标不稳定。稍有不慎就出错了,其次，如果用户没有音乐的话，
		 * 不妨可以告知用户没有音乐让用户添加进去
		 */
		if (cursor != null && cursor.getCount() == 0) {
			final AlertDialog xfdialog = new AlertDialog.Builder(MusicList.this)
					.setTitle("Tips:").setMessage(getResources().getString(R.string.music_no_one))
					.setPositiveButton(getResources().getString(R.string.is_positive), null).create();
			xfdialog.show();
			return;
		}
	while(cursor.moveToNext()){
			int id = cursor.getInt(cursor
					.getColumnIndex(BaseColumns._ID));
			int album_id = cursor.getInt(cursor
					.getColumnIndex(AudioColumns.ALBUM_ID));
			String title = cursor.getString(cursor
					.getColumnIndex(MediaColumns.TITLE));
			String artist = cursor.getString(cursor
					.getColumnIndex(AudioColumns.ARTIST));
			int duration = cursor.getInt(cursor
					.getColumnIndex(AudioColumns.DURATION));
			int isFav = 0;
			try {
				if (dbUtils.findFirst(Playlist.class, WhereBuilder.b("_mid", "=", id)) != null) {
					isFav = 1;
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Playlist playlist = new Playlist(id, title, duration, artist,
					isFav, album_id);
			list.add(playlist);
			if (isFav == 1) {
				favList.add(playlist);
			}
		}
		cursor.close();
	}

	private ListView lv_music;
	private ListView lv_favorite;
	private MusicBroadCast receiver;
	private ImageView iv_listback,iv_flush;

	// 设置所有标签初始值
	private void allTitleNoSelect() {
		for (TextView title : titles) {
			title.setTextColor(mApp.getResources().getColor(R.color.selected_item_text));
		//	title.setBackgroundResource(R.drawable.text_bg_white);
		}
	}

}