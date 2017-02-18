package com.dldzkj.app.renxing.customview;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.PromptManager;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.BaseModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.utils.FileUtils;
import com.dldzkj.app.renxing.utils.OnRequestListener;
import com.dldzkj.app.renxing.utils.RequestHelper;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;


public class AvaterDialogActivity extends Activity implements OnClickListener {
    public static final String PHOTO_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/com.dldzkj.app.renxing/avatar/";
    public static Bitmap photo;
    private String small_path = PHOTO_PATH + "small_photo.jpg";
    private ProgressDialog updialog;
    private TextView tv1, tv2, tv3;
    public static final int TAKE_PICTURE_FROMCAMERA = 21;
    public static final int CUT_PHOTO_REQUEST_CODE = 20;
    private Uri imageUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_selector_pics_activity);
        tv1 = (TextView) findViewById(R.id.dialog_title);
        tv2 = (TextView) findViewById(R.id.dialog_sure);
        tv3 = (TextView) findViewById(R.id.dialog_cancel);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.dialog_title:
                Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/image.jpg"));
                intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent_camera, TAKE_PICTURE_FROMCAMERA);
                break;
            case R.id.dialog_sure:
                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/image.jpg"));
                final Intent intent_gallery = getCropImageIntent();
                startActivityForResult(intent_gallery, CUT_PHOTO_REQUEST_CODE);
                break;
            case R.id.dialog_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE_FROMCAMERA:
                    try {
                        cropImageUriByTakePhoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CUT_PHOTO_REQUEST_CODE:
                    if (data != null) {
                        try {
                            setPicToView(data);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cropImageUriByTakePhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
    }

    public Intent getCropImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", "JPEG");
        return intent;
    }

    private void setPicToView(Intent data) throws IOException {
        photo = null;
        File dirFile = new File(PHOTO_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(small_path);
        if (imageUri != null) {
            photo = decodeUriAsBitmap(imageUri);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            photo.recycle();
        }
        //上传服务器之前先进行判断当前网络是否可用  saveFile(photo);
        updialog = ProgressDialog.show(this, null, "正在上传，请稍候...");
        insertBitmapToDB(small_path);

    }

    public void insertBitmapToDB(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserName", SPUtils.getLoginName(this));
        String json = JSON.toJSONString(map);
        map.clear();
        map.put("Json", json);
        map.put("Img", FileUtils.Bitmap2Bytes(bitmap));
        bitmap.recycle();
        RequestHelper.requestDataBySoap(ConstantValue.UpPortrait, map, new OnRequestListener() {
            @Override
            public void onRequestStart(int tag) {

            }

            @Override
            public void onRequestFail(int errorCode, int tag, Object map) {
                PromptManager.showToast(getBaseContext(), "亲，您的网络不是很给力哦！");
                updialog.dismiss();
                finish();
            }

            @Override
            public void onRequestSuccess(String result, int tag) {
                updialog.dismiss();
                if (result == null) {
                    PromptManager.showToast(getBaseContext(), "亲，您的网络不是很给力哦！");
                    return;
                }
                BaseModel baseModel = JSON.parseObject(
                        result.toString(), BaseModel.class);
                switch (baseModel.getErrNum()) {
                    case 0:
                        String picUrl = JSON.parseObject(JSON.parseArray(baseModel.getData()).get(0).toString()).getString("ImgPath");
                        User u = null;
                        try {
                            u = MyApplication.getInstance().getDbUtils().findById(User.class, SPUtils.getLoginId(getBaseContext()));
                            u.setPortrait(picUrl);
                            MyApplication.getInstance().getDbUtils().saveOrUpdate(u);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        finish();
                        break;
                    default:
                        PromptManager.showToast(getBaseContext(),
                                baseModel.getErrMsg());
                        finish();
                        break;
                }
            }
        }, 0xff);
    }

    private void showConnectFailDialog() {

    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

}