package com.dldzkj.app.renxing.bean;

/**
 * Created by Administrator on 2015/7/17.
 */
public class CodeModel {

    /**
     * status : 000000
     * statusMsg : 成功
     * Code : 8166
     */
    private String status;
    private String statusMsg;
    private String Code;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public String getCode() {
        return Code;
    }
}
