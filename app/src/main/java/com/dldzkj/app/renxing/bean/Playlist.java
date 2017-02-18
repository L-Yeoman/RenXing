package com.dldzkj.app.renxing.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="com_dlkj_douqu_bean_Playlist")
public class Playlist implements Serializable{
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Column(column = "id")
	private int id;
	 @Column(column = "_mid")
	private int _mid;
	 @Column(column = "title")
	private String title;
	 @Column(column = "duration")
	private int duration;
	 @Column(column = "artist")
	private String artist;
	 @Column(column = "isFav")
	private int isFav;
	 @Column(column = "album_id")
	private int album_id;

	public Playlist() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int get_mid() {
		return _mid;
	}

	public void set_mid(int _mid) {
		this._mid = _mid;
	}
	public int getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(int album_id) {
		this.album_id = album_id;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getIsFav() {
		return isFav;
	}

	public void setIsFav(int isFav) {
		this.isFav = isFav;
	}

	
	
	public Playlist(int _mid,String title, int duration, String artist,
			int isFav, int album_id) {
		super();
		this._mid=_mid;
		this.title = title;
		this.duration = duration;
		this.artist = artist;
		this.isFav = isFav;
		this.album_id = album_id;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o==null){
			return false;
		}
		if(((Playlist)o).get_mid()!=_mid){
			return false;
		}
		if(((Playlist)o).getDuration()!=duration){
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Playlist [id=" +getId() + ", title=" + title + ", duration="
				+ duration + ", artist=" + artist + ", isFav=" + isFav
				+ ", album_id=" + album_id + "]";
	}



}
