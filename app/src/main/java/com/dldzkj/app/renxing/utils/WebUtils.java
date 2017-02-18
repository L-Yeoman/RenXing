package com.dldzkj.app.renxing.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import com.dldzkj.app.renxing.bean.BaseModel;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/7/6.
 */
public class WebUtils {
    //http://www.dldouqu.com:8000/WebService/RXWebService.asmx
    public static final String RENXING_WEB = "http://www.dldouqu.com:8000/";
    public static final String RENXING_WEB_PICS = "http://www.dldouqu.com:8000";
  //  public static final String RENXING_WEB = "http://www.dldouqu.com:8000/";
    public static final String RENXING_WEB_URL_TEST = RENXING_WEB + "WebService/RXWebService.asmx/";
    public static final String RENXING_WEB_URL = RENXING_WEB + "WebService/RXWebService.asmx/";

    private static ProgressDialog pb;
    private static boolean showProgress = true;

    public interface OnWebListenr {
        void onSuccess(boolean isSuccess, String msg, String data);

        void onFailed(HttpException error, String msg);
    }

    //不显示等待进度条，为了不影响之前代码，重写
    public static void sendPostNoProgress(final Context c, String method, HashMap<String, Object> map, final OnWebListenr listen) {
        showProgress = false;
        sendPost(c, method, map, listen);
    }

    public static void sendPost(final Context c, String method, HashMap<String, Object> map, final OnWebListenr listen) {
        RequestParams params = new RequestParams();
        if (map != null) {
            String json = (String) StringUtils.toJson(map).get("Json");
            Log.d("zxl", "json参数:" + json);
            params.addBodyParameter("Json", json);
        }
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                RENXING_WEB_URL_TEST + method,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        if (showProgress) {
                            pb = ProgressDialog.show(c, "", "请稍候。。。");
                            pb.setCanceledOnTouchOutside(true);
                        }
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (isUploading) {

                        } else {

                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (pb != null) {
                            pb.dismiss();
                        }
                        if (responseInfo == null) {
                            Toast.makeText(c, "服务器出现故障", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String result = responseInfo.result.substring(responseInfo.result.indexOf("{"), responseInfo.result.lastIndexOf("</string>"));
                        Log.d("zxl", "result:" + responseInfo.result);
                        BaseModel baseModel = JSON.parseObject(
                                result, BaseModel.class);
                        if (listen != null) {
                            listen.onSuccess(baseModel.getErrNum() == 0 ? true : false, baseModel.getErrMsg(), baseModel.getData());
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (pb != null) {
                            pb.dismiss();
                        }
                        Log.d("zxl", "  请求错误:" + msg);
                        Toast.makeText(c, "请确认网络连接是否正常", Toast.LENGTH_SHORT).show();
                        if (listen != null) {
                            listen.onFailed(error, msg);
                        }
                    }
                });
    }

    /*******
     * 上传文件
     ***********/
    public static void sendPostWithFile(final Context c, String method, HashMap<String, Object> map, ArrayList<String> pathList, final boolean showPgs, final OnWebListenr listen) {
        if (pathList == null || pathList.size() == 0) {
            sendPost(c, method, map, listen);
            return;
        }
        RequestParams params = new RequestParams();
        params.addBodyParameter("Json", (String) StringUtils.toJson(map).get("Json"));
        for (int i = 0; i < pathList.size(); i++) {
            try {
                params.addBodyParameter("Img", FileUtils.encodeBase64File(pathList.get(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                RENXING_WEB_URL_TEST + method,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        if (showPgs) {
                            pb = ProgressDialog.show(c, "", "请稍候。。。");
                            pb.setCanceledOnTouchOutside(true);
                        }

                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (isUploading) {

                        } else {

                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (pb != null) {
                            pb.dismiss();
                        }
                        if (responseInfo == null) {
                            Toast.makeText(c, "服务器出现故障", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String result = responseInfo.result.substring(responseInfo.result.indexOf("{"), responseInfo.result.lastIndexOf("</string>"));
                        Log.d("zxl", "result:" + responseInfo.result);
                        BaseModel baseModel = JSON.parseObject(
                                result, BaseModel.class);
                        if (listen != null) {
                            listen.onSuccess(baseModel.getErrNum() == 0 ? true : false, baseModel.getErrMsg(), baseModel.getData());
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (pb != null) {
                            pb.dismiss();
                        }
                        Log.d("zxl", "  请求错误:" + msg);
                        Toast.makeText(c, "请确认网络连接是否正常", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public static boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {

        }
        return false;
    }
}
