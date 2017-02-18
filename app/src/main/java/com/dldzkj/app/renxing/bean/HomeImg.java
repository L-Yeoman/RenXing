package com.dldzkj.app.renxing.bean;

import java.io.Serializable;

public class HomeImg implements Serializable{
	private String PicUrl;
	private String PicLinkUrl;
	
	
	public String getPicUrl() {
		return PicUrl;
	}


	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}


	public String getPicLinkUrl() {
		return PicLinkUrl;
	}


	public void setPicLinkUrl(String picLinkUrl) {
		PicLinkUrl = picLinkUrl;
	}


	@Override
	public String toString() {
		return "{PicUrl:" +"\""+ PicUrl +"\""+", PicLinkUrl:" +"\""+ PicLinkUrl +"\""+"}";
	}
	
}