package com.dldzkj.app.renxing.bbs;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.PromptManager;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.cropview.InternalStorageContentProvider;
import com.dldzkj.app.renxing.customview.AppSelectPicsDialog;
import com.dldzkj.app.renxing.customview.FaceLayout;
import com.dldzkj.app.renxing.utils.FileUtils;
import com.dldzkj.app.renxing.utils.OnRequestListener;
import com.dldzkj.app.renxing.utils.RequestHelper;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class SendBBSActivity extends BaseActivity implements View.OnClickListener,FaceLayout.OnFaceItem,OnRequestListener {


    private static final int SELECT_IMAGE_CODE = 0x02;
    @InjectView(R.id.toggle)
    CheckBox toggle;
    @InjectView(R.id.et_send_title)
    EditText etSendTitle;
    @InjectView(R.id.et_send_content)
    EditText etSendContent;
    @InjectView(R.id.pics_btn)
    ImageView picsBtn;
    @InjectView(R.id.pics_listview)
    RecyclerView picsListview;
    @InjectView(R.id.pics_tips)
    TextView picsTips;

    SendPicsAdapter adapter;
    @InjectView(R.id.need_pics)
    RelativeLayout needPics;
    List<String> datas;
    int selectedCount = 0;
    @InjectView(R.id.iv_noDate)
    ImageView ivNoDate;
    @InjectView(R.id.scroll)
    ScrollView mScrollView;
    @InjectView(R.id.acl_face_layout)
    FaceLayout faceLayout;
    @InjectView(R.id.bottom_layout)
    FrameLayout bottomLayout;
    @InjectView(R.id.faces_btn)
    ImageView facesBtn;
    private Handler mHandler = new Handler();
    InputMethodManager imm;//判断输入法
    private boolean isReply = false;
    private boolean isNiming = false;
    private int pid;
    private int boardId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_send_activity);
        ButterKnife.inject(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EventBus.getDefault().register(this);//注册EventBus
        isReply = getIntent().getBooleanExtra("type", false);
        if (isReply) {
            pid = getIntent().getIntExtra("pid", 0);
            etSendTitle.setEnabled(false);
            etSendTitle.setText(getIntent().getStringExtra("title"));
            etSendContent.setFocusable(true);
            etSendContent.requestFocus();
        } else {
            boardId = getIntent().getIntExtra("boardId", 0);
        }
        faceLayout.setOnFace(this);
        initToolBar();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        picsBtn.setOnClickListener(this);
        facesBtn.setOnClickListener(this);
        ivNoDate.setOnClickListener(this);
        etSendTitle.setOnClickListener(this);
        etSendContent.setOnClickListener(this);
        etSendTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    bottomLayout.setVisibility(View.GONE);
                }
            }
        });

        etSendContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    bottomLayout.setVisibility(View.GONE);
                }
            }
        });
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isNiming = isChecked;
            }
        });
    }


    private void initDatas() {
        datas = new ArrayList<String>();
        adapter = new SendPicsAdapter(this, datas);
        picsListview.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        picsListview.setItemAnimator(new DefaultItemAnimator());
        picsListview.setAdapter(adapter);

    }

    private void initToolBar() {
        if (!isReply) {
            setTitle("发表帖子");
        } else {
            setTitle("回复帖子");

        }
        toolbar.setNavigationIcon(R.drawable.ic_back);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_bbs_send:
                String title = etSendTitle.getText().toString();
                String content = etSendContent.getText().toString();
                Log.d("zxl","content:"+content);
                if (content == null || content.isEmpty()) {
                    Toast.makeText(getBaseContext(), "请填写内容", Toast.LENGTH_SHORT).show();
                    return true;
                }
                content = StringUtils.EncodeBase64Str(content);
                Log.d("zxl","contentBase64:"+content);
                if (isReply) {
                    PromptManager.showProgressDialog(this);
                    WebRequstReplyText(content, pid);
                } else {
                    if (title == null || title.isEmpty()) {
                        Toast.makeText(getBaseContext(), "请填写标题", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    PromptManager.showProgressDialog(this);
                    WebRequstSendText(title, content, boardId, isNiming ? "匿名用户" : "张三");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

    @Subscribe
    public void onEvent(SendBBSEvent event) {
        if (event.getMsg() == 500) {//表示点击加号
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
            return;
        }
        if (event.getMsg() > 0) picsListview.scrollToPosition(adapter.getItemCount() - 1);
        selectedCount += event.getMsg();
        if (selectedCount == 0) {
            adapter.notifyDataSetChanged();
            picsListview.setVisibility(View.GONE);
            ivNoDate.setVisibility(View.VISIBLE);
            picsTips.setText("最多可以添加10张图");
            return;
        }
        picsTips.setText("已选" + selectedCount + "张,还可添加" + (10 - selectedCount) + "张");

    }

    AppSelectPicsDialog dialog;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.pics_btn:
                //如果输入法已经打开，就隐藏掉
                //判断如果face打开就隐藏
                View focusV = getCurrentFocus();
//                focusV.clearFocus();
                imm.hideSoftInputFromWindow(focusV.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                faceLayout.setVisibility(View.GONE);
                if (needPics.getVisibility() == View.GONE) {
                    bottomLayout.setVisibility(View.VISIBLE);
                    needPics.setVisibility(View.VISIBLE);
                } else {
                    bottomLayout.setVisibility(View.GONE);
                    needPics.setVisibility(View.GONE);
                }
                break;
            case R.id.et_send_title:
            case R.id.et_send_content:
                bottomLayout.setVisibility(View.GONE);
                break;
            case R.id.faces_btn:
                View focusView = getCurrentFocus();
                if(focusView==etSendTitle){
                    Toast.makeText(getBaseContext(),"标题不可插入表情",Toast.LENGTH_SHORT).show();
                    return;
                }
//                focusView.clearFocus();
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                needPics.setVisibility(View.GONE);
                if (faceLayout.getVisibility() == View.GONE) {
                    bottomLayout.setVisibility(View.VISIBLE);
                    faceLayout.setVisibility(View.VISIBLE);
                } else {
                    bottomLayout.setVisibility(View.GONE);
                    faceLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_noDate:
                dialog = new AppSelectPicsDialog(this, R.style.dialog);
                dialog.show();
                dialog.setDialogListner(new AppSelectPicsDialog.dialogListenner() {

                    @Override
                    public void setOnCameraLis(Dialog d, View v) {
                        d.dismiss();
                        takePicture();
                    }

                    @Override
                    public void setOnGalleryLis(Dialog d, View v) {
                        d.dismiss();
                        Intent intent = new Intent(SendBBSActivity.this, PhotoSelectorActivity.class);
                        intent.putExtra(PhotoSelectorActivity.KEY_MAX, 10 - datas.size());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        SendBBSActivity.this.startActivityForResult(intent, SELECT_IMAGE_CODE);
                       /* adapter.addFirstPics();
                        picsListview.setVisibility(View.VISIBLE);
                        ivNoDate.setVisibility(View.GONE);*/
                    }

                    @Override
                    public void setOnCancelLis(Dialog d, View v) {
                        d.dismiss();
                    }
                });
                break;

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case SELECT_IMAGE_CODE:
                if (data != null && data.getExtras() != null) {
                    @SuppressWarnings("unchecked")
                    List<PhotoModel> photos = (List<PhotoModel>) data.getExtras().getSerializable("photos");
                    if (photos == null || photos.isEmpty() || photos.size() == 0) {

                    } else {

                        List<String> paths = new ArrayList<String>();
                        for (PhotoModel model : photos) {
                            paths.add(model.getOriginalPath());
                        }
                        picsListview.setVisibility(View.VISIBLE);
                        ivNoDate.setVisibility(View.GONE);
                        datas.addAll(paths);
                        adapter.notifyDataSetChanged();
                        EventBus.getDefault().post(
                                new SendBBSEvent(paths.size()));
                        paths = null;
                    }
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                String path = mFileTemp.getPath();
                picsListview.setVisibility(View.VISIBLE);
                ivNoDate.setVisibility(View.GONE);
                datas.add(path);
                adapter.notifyDataSetChanged();
                EventBus.getDefault().post(
                        new SendBBSEvent(1));
                break;
        }
    }

    /********
     * 发帖和回帖的网络请求
     *******/

    private void WebRequstSendText(String Title, String Content, int BoardID, String NicName) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserID", SPUtils.getLoginId(this));
        map.put("Title", Title);
        map.put("Content", Content);
        map.put("BoardID", BoardID);
        map.put("NicName", NicName);
        WebUtils.sendPost(this, ConstantValue.AddPosts, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {//需要判断是否包含图片
                    String pid = JSON.parseObject(JSON.parseArray(data).get(0).toString()).getString("PID");
                    Log.d("zxl", "要上传的图片张数:" + datas.size());
                    if (datas.size() == 0) {
                        PromptManager.closeProgressDialog();
                        Toast.makeText(SendBBSActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {//上传图片
                        PromptManager.getDialog().setMessage("开始上传图片...");
                        for (int i = 0; i < datas.size(); i++) {
                            WebRequstSendImg(Integer.parseInt(pid), datas.get(i), i + 1);
                        }
                    }
                } else {
                    Toast.makeText(SendBBSActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

    private void WebRequstSendImg(int PostsID, String path, int pos) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("PostID", PostsID);
        String json = JSON.toJSONString(map);
        map.clear();
        map.put("Json", json);
        map.put("Img", FileUtils.Bitmap2Bytes(bitmap));
        bitmap.recycle();
        RequestHelper.requestDataBySoap(ConstantValue.AddPostsImg, map, this, pos);
    }

    private void WebRequstReplyText(String Content, int PostID) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserID", SPUtils.getLoginId(this));
        map.put("Content", Content);
        map.put("PostID", PostID);
        WebUtils.sendPost(this, ConstantValue.AddPostsRepy, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    String rid = JSON.parseObject(JSON.parseArray(data).get(0).toString()).getString("RID");
                    if (datas.size() == 0) {
                        PromptManager.closeProgressDialog();
                        Toast.makeText(SendBBSActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {//上传图片
                        PromptManager.getDialog().setMessage("开始上传图片...");
                        for (int i = 0; i < datas.size(); i++) {
                            WebRequstReplyImg(Integer.parseInt(rid), datas.get(i), i + 1);
                        }
                    }
                } else {
                    Toast.makeText(SendBBSActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

    private void WebRequstReplyImg(int RID, String path, int pos) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("RID", RID);
        String json = JSON.toJSONString(map);
        map.clear();
        map.put("Json", json);
        map.put("Img", FileUtils.Bitmap2Bytes(bitmap));
        bitmap.recycle();
        RequestHelper.requestDataBySoap(ConstantValue.AddPostsRepyImg, map, this, pos);
    }

    @Override
    public void onRequestStart(int tag) {

    }

    @Override
    public void onRequestFail(int errorCode, int tag, Object map) {

        if (tag == datas.size()) {
            PromptManager.closeProgressDialog();
            Toast.makeText(SendBBSActivity.this, "第" + tag + "张：失败", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            PromptManager.getDialog().setMessage("第" + tag + "张：失败");
        }
    }

    @Override
    public void onRequestSuccess(String result, int tag) {
        if (tag == datas.size()) {
            Toast.makeText(SendBBSActivity.this, "上传完毕", Toast.LENGTH_SHORT).show();
            PromptManager.closeProgressDialog();
            finish();
        } else {
            PromptManager.getDialog().setMessage("第" + tag + "张：成功");
        }
    }

    /************
     * 拍照功能
     **********/
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x07;
    private File mFileTemp;

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mFileTemp = new File(Environment.getExternalStorageDirectory(),
                        "temp_crop_" + System.currentTimeMillis() + ".jpg");
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                mFileTemp = new File(getFilesDir(), "temp_crop_" + System.currentTimeMillis() + ".jpg");
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFace(SpannableString s) {
        if(s==null){
            Spanned _Spanned=etSendContent.getEditableText();
            ImageSpan[] _Images=_Spanned.getSpans(0, _Spanned.length(), ImageSpan.class);
            Editable edt= etSendContent.getEditableText();
            if(_Images.length==0){
                etSendContent.setText("");
                return;
            }
            int start=_Spanned.getSpanStart(_Images[_Images.length - 1]);
            int end=_Spanned.getSpanEnd(_Images[_Images.length - 1]);
            edt.delete(start, end);
            etSendContent.invalidate();
            return;
        }
        etSendContent.append(s);
    }
}
