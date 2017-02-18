package com.dldzkj.app.renxing.utils;

/**
 * 
 * @author CY
 * @since 2014-03-08
 */
public interface OnRequestListener {
	public void onRequestStart(int tag);

	public void onRequestFail(int errorCode, int tag,Object map);

	public void onRequestSuccess(String result, int tag);
}
