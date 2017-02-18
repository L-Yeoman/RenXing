package com.dldzkj.app.renxing.activity;

import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ArticleModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.customview.BadgeView;
import com.dldzkj.app.renxing.customview.ToShare;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.ScreenUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.photoselector.util.AnimationUtil;
import com.polites.Animator;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.ShareSDK;

public class ArticleActivity extends BaseOtherActivity {
    @InjectView(R.id.acl_titleImage)
    ImageView mTitleImage;
    @InjectView(R.id.acl_title)
    TextView mTitle;
    @InjectView(R.id.acl_time)
    TextView mTime;
    @InjectView(R.id.acl_like_count)
    TextView mLikeCount;
    @InjectView(R.id.acl_collection)
    ImageButton mCollection;
    @InjectView(R.id.scrollView)
    ScrollView mScrollView;
    @InjectView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @InjectView(R.id.acl_like_layout)
    RelativeLayout likeLayout;
    @InjectView(R.id.acl_con_layout)
    LinearLayout mConLayout;
    private ImageView backImage, likeImage, commentImage, shareImage;
    private WebView mWeb;
    private PopupWindow mWindow;
    private ArticleModel  currentModel;
    private boolean isLike = false;
    private boolean isCollection = false;
    private BadgeView bv_likeNum, bv_commentNum;
    private Animation fadeIn,fadeOut;
    private RelativeLayout mRelative;

    @Override
    protected void onDestroy() {
        mWeb.clearCache(true);
        mWeb.destroy();
        super.onDestroy();
    }

    private View myView = null;
    private int ID ;
    private WebChromeClient.CustomViewCallback myCallBack = null;
    float y1 = 0;
    float y2 = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);
        setContentView(R.layout.article_content_layout);
        ButterKnife.inject(this);
        ScreenUtils.addTranslaterView(this, "isFirstArticle", R.layout.teach_article_detail, R.id.teach_btn_main_home);
        initView();
        initBadgeView();
        getData();
    }

    private void initBadgeView() {
        bv_likeNum = new BadgeView(this, likeImage);
        bv_commentNum = new BadgeView(this, commentImage);
        bv_likeNum.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        bv_commentNum.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        bv_likeNum.setBadgeBackgroundColor(Color.parseColor("#4ECDC4"));
        bv_commentNum.setBadgeBackgroundColor(Color.parseColor("#4ECDC4"));
        bv_likeNum.setTextSize(12);
        bv_commentNum.setTextSize(12);
    }

    private void initView() {
        mRelative = (RelativeLayout)findViewById(R.id.RelativeLayout);
        shareImage = (ImageView) findViewById(R.id.acl_share);
        backImage = (ImageView) findViewById(R.id.acl_return);
        likeImage = (ImageView) findViewById(R.id.acl_like);
        commentImage = (ImageView) findViewById(R.id.acl_comment);
        mWeb = (WebView) findViewById(R.id.acl_webView);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        fadeOut = AnimationUtils.loadAnimation(this,R.anim.abc_fade_out);
        mCollection.setOnClickListener(new ClickListener());
        backImage.setOnClickListener(new ClickListener());
        likeImage.setOnClickListener(new ClickListener());
        commentImage.setOnClickListener(new ClickListener());
        shareImage.setOnClickListener(new ClickListener());

        mWeb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("ACTION_DOWN", event.getY()+ "");
                        y1 = event.getRawY();
                        Log.d("y1", y1 + "");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("mWeb",  "ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("mWeb",  "ACTION_UP");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.d("mWeb",  "ACTION_CANCEL");
                        y2 = event.getRawY();
                        Log.d("y2", y2 + "");
                        Log.d("y1-y2", y1 - y2 + "");
                        Log.d("y2-y1", y2 - y1 + "");
                        if (y1 - y2 >= 10&&likeLayout.getVisibility()==View.VISIBLE) {
                            bottomLayout.setVisibility(View.GONE);
                            bottomLayout.startAnimation(fadeOut);
                            likeLayout.setVisibility(View.GONE);
                            likeLayout.startAnimation(fadeOut);
                        } else if (y2 - y1 >= 10&&likeLayout.getVisibility()==View.GONE) {
                            bottomLayout.setVisibility(View.VISIBLE);
                            bottomLayout.startAnimation(fadeIn);
                            likeLayout.setVisibility(View.VISIBLE);
                            likeLayout.startAnimation(fadeIn);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void bindView(ArticleModel model) {
        if (model.getIsPraise().equals("True")) {
            likeImage.setImageResource(R.drawable.ic_article_details_icon1_press);
        } else {
            likeImage.setImageResource(R.drawable.ic_article_details_icon1);
        }
        if (model.getIsFavorite().equals("True")) {
            mCollection.setImageResource(R.drawable.ic_article_details_icon2_press);
        } else {
            mCollection.setImageResource(R.drawable.ic_article_details_icon2);
        }
        //刷新点赞
        if (isLike) {
            bv_likeNum.setText(model.getFavor_Count() + "");
            bv_likeNum.show();
            isLike = false;
            return;
        }
        Glide.with(this)
                .load(WebUtils.RENXING_WEB + model.getImg_Url())
                .into(mTitleImage);
      /*  BitmapUtils _Utils = new BitmapUtils(this);
        _Utils.display(mTitleImage, WebUtils.RENXING_WEB + model.getImg_Url());*/
        mTitle.setText(model.getTitle());
        mTime.setText(model.getAdd_Time());
//        mLikeCount.setText(model.getFavor_Count() + "");
        bv_likeNum.setText(model.getFavor_Count() + "");
        bv_likeNum.show();
//        mCommentCount.setText(model.getComment_Count() + "");
        if (model.getComment_Count() > 0) {
            bv_commentNum.setText(model.getComment_Count() + "");
            bv_commentNum.show();
        }
        if (model.getImg_Url().equals("")) {
            mTitleImage.setVisibility(View.GONE);
        }
        String content = new String(Base64.decode(model.getContent(), Base64.DEFAULT));
        //   String s = Html.fromHtml(content).toString();
        //   mWeb.loadData(s,"text/html", "utf-8");
        mWeb.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
        //       mWeb.setWebChromeClient(new WebChromeClient());
        //支持javascript
        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.setWebChromeClient(new WebChromeClient());
        //       mWeb.getSettings().setPluginState(WebSettings.PluginState.ON);
        // 设置可以支持缩放
  //      mWeb.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        //       mWeb.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
    //    mWeb.getSettings().setUseWideViewPort(true);
        //自适应屏幕
    /*    mWeb.getSettings().setDefaultFixedFontSize(100);
        mWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWeb.getSettings().setLoadWithOverviewMode(true);*/
        // setHeight(mWeb);
    }
    public class MyChromeClient extends WebChromeClient{

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if(myView != null){
                callback.onCustomViewHidden();
                return;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mRelative.removeAllViews();
            mRelative.addView(view);
            myView = view;
            myCallBack = callback;
        }

        @Override
        public void onHideCustomView() {
            if(myView == null){
                return;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mRelative.removeView(myView);
            myView = null;
            mRelative.addView(mWeb);
            myCallBack.onCustomViewHidden();
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            // TODO Auto-generated method stub
            Log.d("ZR", consoleMessage.message()+" at "+consoleMessage.sourceId()+":"+consoleMessage.lineNumber());
            return super.onConsoleMessage(consoleMessage);
        }
    }


    @Override
    protected void onPause() {
        Log.i("lyBIn","onPause()");
        try {
            mWeb.getClass().getMethod("onPause").invoke(mWeb,(Object[])null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }


    /* private void setHeight(View view){
             ViewGroup.LayoutParams params=view.getLayoutParams();
                     view.measure(0, 0);
                     int height=view.getMeasuredHeight();
             params.height=height;
             view.setLayoutParams(params);
         }*/
    private void getData() {
            ID = getIntent().getExtras().getInt("");
        Log.i("lyBin","id:"+ID);
        HashMap<String, Object> _Map = new HashMap<String, Object>();
     //   String id = mModel.getID() + "";
        /*if (mModel.getID() == 0) {
            id = mModel.getUFID();
        }*/
        _Map.put("ArticleID", ID);
        _Map.put("CurUserID", SPUtils.getLoginId(this));
        callService(_Map, ConstantValue.GetArticle);
    }

    private void praise() {
        if (currentModel != null) {
            isLike = true;
            HashMap<String, Object> _Map = new HashMap<String, Object>();
            //    String currentMethod  = "";
         /*   if(currentModel.getIsPraise().equals("True")){
                showToast("取消点赞");
                _Map.put("UserID",ConstantValue.Id);
                _Map.put("EntityID",currentModel.getID());
                _Map.put("Type",0);
                currentMethod=ConstantValue.DeleteLike;
            }else{*/
            _Map.put("ArticleID", currentModel.getID());
            _Map.put("UserID", SPUtils.getLoginId(this));
            //       currentMethod =ConstantValue.AddArticleLike;
            //    }
            WebUtils.sendPostNoProgress(this, ConstantValue.AddArticleLike, _Map, new WebUtils.OnWebListenr() {
                @Override
                public void onSuccess(boolean isSuccess, String msg, String data) {
                    if (isSuccess) {
                        showToast(msg);
                        getData();
                        return;
                    }
                    showToast(msg);
                }

                @Override
                public void onFailed(HttpException error, String msg) {
//                    showToast(msg);
                }
            });
        }
    }

    private void collection() {
        isCollection = true;
        if (currentModel != null) {
            HashMap<String, Object> _Map = new HashMap<String, Object>();
            _Map.put("UserID", SPUtils.getLoginId(this));
            _Map.put("CID", currentModel.getID());
            _Map.put("CType", "0");
            callService(_Map, ConstantValue.AddMyCollection);
        }
    }

    private void callService(HashMap<String, Object> hashMap, String method) {
        WebUtils.sendPostNoProgress(this, method, hashMap, new WebListener());
    }

    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private class WebListener implements WebUtils.OnWebListenr {
        @Override
        public void onSuccess(boolean isSuccess, String msg, String data) {
            if (isSuccess) {
                if (isCollection) {
                    isCollection = false;
                    showToast("收藏成功");
                    mCollection.setImageResource(R.drawable.ic_article_details_icon2_press);
                    return;
                }
                ArticleModel _Model = JSON.parseObject(JSON.parseArray(data).get(0).toString(), ArticleModel.class);
                currentModel = _Model;
                bindView(_Model);
            }
            if (isCollection) {
                isCollection = false;
                showToast(msg);
            }
        }

        @Override
        public void onFailed(HttpException error, String msg) {
        }
    }

    public class ClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.acl_return:
                    ArticleActivity.this.finish();
                    break;
                case R.id.acl_collection:
                    collection();
                    break;
                //点赞
                case R.id.acl_like:
                    praise();
                    break;
                case R.id.acl_comment:
                    Intent _Intent = new Intent();
                    _Intent.setClass(ArticleActivity.this, CommentActivity.class);
                    Bundle _Bundle = new Bundle();
                    _Bundle.putInt("", ID);
                    _Intent.putExtras(_Bundle);
                    startActivity(_Intent);
                    break;
                case R.id.acl_share:
                    startActivity(new Intent(ArticleActivity.this, ToShare.class));
                    break;
                default:
                    break;
            }
        }
    }

}
