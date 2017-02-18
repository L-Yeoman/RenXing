package com.dldzkj.app.renxing.bean;

import java.io.Serializable;

public class BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1267009360087056084L;

	private int ErrNum;

	private String ErrMsg;

	private int TotalPage;

	private String data;

	public int getErrNum() {
		return ErrNum;
	}

	public void setErrNum(int errNum) {
		ErrNum = errNum;
	}

	public String getErrMsg() {
		return ErrMsg;
	}

	public void setErrMsg(String errMsg) {
		ErrMsg = errMsg;
	}

	public int getTotalPage() {
		return TotalPage;
	}

	public void setTotalPage(int totalPage) {
		TotalPage = totalPage;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
