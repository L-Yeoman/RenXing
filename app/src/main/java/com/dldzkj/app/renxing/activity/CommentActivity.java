package com.dldzkj.app.renxing.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.adapter.ArticleCommentAdapter;
import com.dldzkj.app.renxing.bean.ArticleCommentModel;
import com.dldzkj.app.renxing.bean.ArticleModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.customview.DividerItemDecoration;
import com.dldzkj.app.renxing.customview.FaceLayout;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lyBin on 2015/6/19.
 */
public class CommentActivity extends BaseActivity implements FaceLayout.OnFaceItem,
        UltimateRecyclerView.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener {
    private ImageView faceImage;
    private TextView sendText;
    private EditText commentContent;
    private UltimateRecyclerView mRecycler;
    private List<ArticleCommentModel> mList;
    private ArticleCommentAdapter mAdapter;
    private FaceLayout mLayout;
    private boolean isLoad = false;
    private int ID;
    //防止数据变动时下拉
    private boolean isSendPost = false;
    private boolean isSend = false;
    private View footerView;
    private int PageIndex = 2;
    //item间距
    private int ITEM_DECORATION=2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_comment_layout);
        initView();
    }
    public void initView(){
        mAdapter=new ArticleCommentAdapter();
        setTitle(getResources().getString(R.string.comment_title));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mRecycler=(UltimateRecyclerView)findViewById(R.id.acl_recycler);
        faceImage=(ImageView)findViewById(R.id.acl_face);
        sendText=(TextView)findViewById(R.id.acl_send);
        commentContent=(EditText)findViewById(R.id.acl_commentText);
        mLayout=(FaceLayout)findViewById(R.id.acl_face_layout);
        bindData();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.enableLoadmore();
        mRecycler.setOnLoadMoreListener(this);
        mRecycler.setDefaultOnRefreshListener(this);
        footerView = LayoutInflater.from(this).inflate(R.layout.listview_footer, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        footerView.setLayoutParams(lp);
        //底部加载
        mAdapter.setCustomLoadMoreView(footerView);
      /*  mRecycler.addItemDecoration(new DividerItemDecoration(this, R.color.text_gray,
                DividerItemDecoration.VERTICAL_LIST, ITEM_DECORATION));*/

        faceImage.setOnClickListener(new ClickListener());
        sendText.setOnClickListener(new ClickListener());
        commentContent.setOnClickListener(new ClickListener());
        mLayout.setOnFace(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindData( ){
        if(mList==null){
            mList=new ArrayList<ArticleCommentModel>();
        }
       /* for(int i=0;i<9;i++){
            ArticleCommentModel _Model=new ArticleCommentModel();
            _Model.setContent("老爷，老爷,夫人生了");
            _Model.setUser_Name("王妈");
            _Model.setAdd_Time("很久以前");
            _Model.setHeadUri("http://img3.imgtn.bdimg.com/it/u=2255570005,2491899768&fm=21&gp=0.jpg");
            mList.add(_Model);
        }*/
         ID =  getIntent().getExtras().getInt("");
        Log.i("Lybin","comment,ID:"+ID);
        HashMap<String,Object> _Map = new HashMap<String,Object>();
       /* _Map.put("CID","0");
        _Map.put("ArticleID", mModel.getID());
        _Map.put("Type", "0");
        _Map.put("Num", "10");*/
        _Map.put("ArticleID",ID);
        _Map.put("PageIndex","1");
        _Map.put("PageSize","10");

        isSend = true;
        callService(_Map, ConstantValue.GetArticleCommmentList);
        if(mList!=null){
            BitmapUtils _Utils=new BitmapUtils(this);
            mAdapter.setData(CommentActivity.this, mList, _Utils);
            mRecycler.setAdapter(mAdapter);
        }
    }
    private void sendComment(){
        isSend = true;
        Date _Date=new Date();
        SimpleDateFormat _Format=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String _Time=_Format.format(_Date);
    //    String s= Base64.encodeToString((commentContent.getText().toString()).getBytes(), Base64.DEFAULT);
        String s = StringUtils.EncodeBase64Str(commentContent.getText().toString());
        if(s.equals("")){
            return;
        }
        commentContent.setText("");

        HashMap<String,Object> _Map = new HashMap<String, Object>();
        _Map.put("ArticleID", ID);
        _Map.put("Content", s);
        _Map.put("UserID", SPUtils.getLoginId(this));
        _Map.put("UserName",SPUtils.getLoginName(this));
        callService(_Map, ConstantValue.AddArticleComment);
        mHandler.sendEmptyMessageDelayed(0,1000);
      /*  ArticleCommentModel _Model=new ArticleCommentModel();
        _Model.setContent(s);
        _Model.setNicName("美不美");
        _Model.setPortrait("http://cdnweb.b5m.com/web/cmsphp/article/201505/c6e16e8d82e1cb4b985edf8a2e9107be.jpg");
        _Model.setAdd_Time(_Time);*/
       /* mList.add(0, _Model);*/
       /* loadMoreView(false);
        mAdapter.notifyDataSetChanged();*/
    }
    private boolean tag=false;
    private void creatFace(){
        InputMethodManager input= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(!tag){

           // commentContent.setInputType(InputType.TYPE_NULL);
            input.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
            mLayout.setVisibility(View.VISIBLE);
            tag=true;
        }else{
         //   commentContent.setInputType(InputType.TYPE_CLASS_TEXT);
            mLayout.setVisibility(View.GONE);
            tag=false;
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(mLayout.getVisibility()==View.VISIBLE){
            creatFace();
            return true;
        }else{
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onFace(SpannableString s) {
        if(s==null){
            Spanned _Spanned=commentContent.getEditableText();
            ImageSpan[] _Images=_Spanned.getSpans(0, _Spanned.length(), ImageSpan.class);
            Editable edt= commentContent.getEditableText();
            if(_Images.length==0){
                commentContent.setText("");
                return;
            }
            int start=_Spanned.getSpanStart(_Images[_Images.length - 1]);
            int end=_Spanned.getSpanEnd(_Images[_Images.length - 1]);
            edt.delete(start, end);
            commentContent.invalidate();
            return;
        }
        commentContent.append(s);
    }
    //控制加载条显示
    public void loadMoreView(boolean b){
        isLoad = b;
        if(b&&mList.size()>6){
            mAdapter.setCustomLoadMoreView(footerView);
        }else if(mAdapter.getCustomLoadMoreView()!=null){
                mAdapter.setCustomLoadMoreView(null);
        }
    }

    @Override
    public void loadMore(int i, int i1) {
        loadMoreView(true);
        mAdapter.notifyDataSetChanged();
        if(isSend&&mList.size()<=6||isSendPost){
            isSend = false;
            isSendPost = false;
            /*  loadMoreView(false);
            mAdapter.notifyDataSetChanged();*/
            return;
        }
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessage(0);
    }
    /*public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }*/
    private void callService(HashMap<String,Object>hashMap,String method){
        WebUtils.sendPost(this,method,hashMap,new WebListener());
    }
    private class WebListener implements WebUtils.OnWebListenr{
        @Override
        public void onSuccess(boolean isSuccess, String msg, String data) {
            if(isSuccess){
                ArrayList<ArticleCommentModel> list= (ArrayList<ArticleCommentModel>) JSON.parseArray(data,ArticleCommentModel.class);
                refreshData(list);
                return;
            }
            if(isLoad){
                loadMoreView(false);
                mAdapter.notifyDataSetChanged();
            }
            mRecycler.setRefreshing(false);
        }
        @Override
        public void onFailed(HttpException error, String msg) {
            if(isLoad){
                loadMoreView(false);
                mAdapter.notifyDataSetChanged();
            }
            mRecycler.setRefreshing(false);
        }
    }
    private int currentID = 0;
    private void refreshData(ArrayList<ArticleCommentModel> list){
       /* ArticleCommentModel _Model = list.get(0);
        if(_Model.getID()<=currentID&&currentID!=0){
            mRecycler.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
            return;
        }else{
            currentID = _Model.getID();
        }*/
       /* if(isLoad) {
            loadMoreView(false);
            for(int i=list.size()-1;i>=0;i--){
                 _Model = list.get(i);
                mList.add( _Model);
            }
            mAdapter.notifyDataSetChanged();
            return;
        }*/
        for(int i=list.size()-1;i>=0;i--){
            ArticleCommentModel model = list.get(i);
            mList.add(0,model);
        }
        mRecycler.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }
    public void onLoadData( HashMap<String,Object>hashMap,String method){
            WebUtils.sendPost(this, method, hashMap, new WebUtils.OnWebListenr() {
                @Override
                public void onSuccess(boolean isSuccess, String msg, String data) {
                    if(isSuccess){
                        ArrayList<ArticleCommentModel> list= (ArrayList<ArticleCommentModel>) JSON.parseArray(data,ArticleCommentModel.class);
                       /* ArticleCommentModel _Model = list.get(list.size()-1);
                        int i = Integer.parseInt(_Model.getRNum());
                        Log.i("_Model.getRNum()",_Model.getRNum());
                        Log.i("currentRNum",currentRNum+"");
                        Log.i("PageIndex",PageIndex+"");
                        if(i<=currentRNum&&!_Model.getRNum().equals("")){
                            loadMoreView(false);
                            isSendPost=true;
                            mAdapter.notifyDataSetChanged();
                            return;
                        }else{
                            currentRNum = i;
                        }*/
                        loadMoreView(false);
                        /*for(int ii=list.size()-1;ii>=0;ii--){
                           ArticleCommentModel model = list.get(ii);
                            mList.add(model);
                        }*/
                        for(int ii=0;ii<list.size();ii++){
                            ArticleCommentModel model = list.get(ii);
                            mList.add(model);
                        }
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                    loadMoreView(false);
                    isSendPost=true;
                    //刷新数据会触发loadMore()
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailed(HttpException error, String msg) {
                    loadMoreView(false);
                    isSendPost=true;
                    mAdapter.notifyDataSetChanged();
                }
            });
    }

    private void clearData(){
        PageIndex = 2;
        mList.clear();
    }
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            ArticleCommentModel _Model;
            HashMap<String, Object> _Map;
            switch(msg.what){
                case 0:
                    if(mList.size()==0){
                        _Model =new ArticleCommentModel();
                    }else{
                        _Model = mList.get(0);
                    }

                    _Map = new HashMap<String, Object>();
                   /* _Map.put("CID",_Model.getID());
                    _Map.put("ArticleID",mModel.getID());
                    _Map.put("Type","1");
                    _Map.put("Num", "10");*/
                    clearData();
                    _Map.put("ArticleID",ID);
                    _Map.put("PageIndex","1");
                    _Map.put("PageSize","10");
                    callService(_Map, ConstantValue.GetArticleCommmentList);
                    break;
                case 1:
                   /* loadMoreView(false);
                    mAdapter.notifyDataSetChanged();*/
                    if(mList.size()==0){
                        _Model =new ArticleCommentModel();
                    }else{
                        _Model = mList.get(mList.size()-1);
                    }
                    _Map = new HashMap<String, Object>();
                    /*_Map.put("CID",_Model.getID());
                    _Map.put("ArticleID",_Model.getID());
                    _Map.put("Type","2");
                    _Map.put("Num", "10");*/
                    _Map.put("ArticleID",ID);
                    _Map.put("PageIndex",PageIndex);
                    _Map.put("PageSize","10");
                    PageIndex++;
                    onLoadData(_Map, ConstantValue.GetArticleCommmentList);
                    break;
            }
        };
    };


    public class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.acl_commentText:
                    if(tag==true){
                        creatFace();
                    }
                    break;
                case R.id.acl_face:
                    creatFace();
                    break;
                case R.id.acl_send:
                    sendComment();
                    break;
                default:
                    break;
            }
        }
    }

}
