package com.dldzkj.app.renxing.blelib.services;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.Playlist;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
public class MusicUtil {
	public static String[] media_info = new String[] {
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.ALBUM_ID };
	public static List<Playlist> getMp3List(Context context, DbUtils dbUtils) {
		
		List<Playlist> list = new ArrayList<Playlist>();
		Cursor cursor =  new ResolverSer().getResolverSer().getResover(context).query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, media_info, "",
				null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor != null && cursor.getCount() == 0) {
			final AlertDialog xfdialog = new AlertDialog.Builder(context)
					.setTitle("Tips:").setMessage(context.getResources().getString(R.string.music_no_one))
					.setPositiveButton(context.getResources().getString(R.string.is_positive), null).create();
			xfdialog.show();
			cursor.close();
			return null;
		}
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			int album_id = cursor.getInt(cursor
					.getColumnIndex(AudioColumns.ALBUM_ID));
			String title = cursor.getString(cursor
					.getColumnIndex(MediaColumns.TITLE));
			String artist = cursor.getString(cursor
					.getColumnIndex(AudioColumns.ARTIST));
			int duration = cursor.getInt(cursor
					.getColumnIndex(AudioColumns.DURATION));
			if (duration>30000) {
				int isFav = 0;
				Playlist playlist = new Playlist(id, title, duration, artist,
						isFav, album_id);
				list.add(playlist);
			}
		}
		cursor.close();
		return list;
	}


	public static HashMap<String, ArrayList<Playlist>> getMp3Lists(
			Context context, DbUtils dbUtils) {
		HashMap<String, ArrayList<Playlist>> map = new HashMap<String, ArrayList<Playlist>>();
		ArrayList<Playlist> list = new ArrayList<Playlist>();
		ArrayList<Playlist> favList = new ArrayList<Playlist>();
		Cursor cursor = new ResolverSer().getResolverSer().getResover(context).query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, media_info, "",
				null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor != null && cursor.getCount() == 0) {
			cursor.close();
			return null;
		}
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			int album_id = cursor.getInt(cursor
					.getColumnIndex(AudioColumns.ALBUM_ID));
			String title = cursor.getString(cursor
					.getColumnIndex(MediaColumns.TITLE));
			String artist = cursor.getString(cursor
					.getColumnIndex(AudioColumns.ARTIST));
			int duration = cursor.getInt(cursor
					.getColumnIndex(AudioColumns.DURATION));
			if (duration>30000) {
				int isFav = 0;
				try {
					if (dbUtils.findFirst(Playlist.class, WhereBuilder.b("_mid", "=", id))!= null) {//数据库只存喜欢的
						isFav = 1;
						Log.d("zxl", "数据库找到了："+title);
					}
				} catch (DbException e) {
					e.printStackTrace();
				}
				Playlist playlist = new Playlist(id, title, duration, artist,
						isFav, album_id);
				Log.d("zxl", "歌曲："+title+"  _mid: "+id+"  "+isFav);
				list.add(playlist);
				if (isFav == 1) {
					favList.add(playlist);
				}
			}
		}
		map.put("musicList", list);
		map.put("favList", favList);
		cursor.close();
		return map;
	}
	public static Bitmap getArtwork(Context context, long song_id,
			long album_id, boolean allowdefault) {
		if (album_id < 0) {
			// This is something that is not in the database, so get the album
			// art directly
			// from the file.
			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			if (allowdefault) {
				return getDefaultArtwork(context);
			}
			return null;
		}

		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, sBitmapOptions);
			} catch (FileNotFoundException ex) {
				// The album art thumbnail does not actually exist. Maybe the
				// user deleted it, or
				// maybe it never existed to begin with.
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtwork(context);
						}
					}
				} else if (allowdefault) {
					bm = getDefaultArtwork(context);
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}

	private static Bitmap getArtworkFromFile(Context context, long songid,
			long albumid) {
		Bitmap bm = null;
		byte[] art = null;
		String path = null;
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException(
					"Must specify an album or a song id");
		}
		try {
			if (albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
					try {
						pfd.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
					try {
						pfd.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException ex) {

		}
		if (bm != null) {
			mCachedBit = bm;
		}
	
		return bm;
	}

	private static Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeResource(context.getResources(),R.drawable.music_default_bg, opts);
	}

	private static final Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");
	private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	private static Bitmap mCachedBit = null;
}
