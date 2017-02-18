package com.dldzkj.app.renxing.bbs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.BBSModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.customview.AppPopWindow;
import com.dldzkj.app.renxing.customview.BadgeView;
import com.dldzkj.app.renxing.customview.DividerItemDecoration;
import com.dldzkj.app.renxing.customview.OnRefreshListener;
import com.dldzkj.app.renxing.customview.RefreshListView;
import com.dldzkj.app.renxing.customview.ToShare;
import com.dldzkj.app.renxing.customview.ZxlDividerItemDecoration;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.ScreenUtils;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.util.CommonUtils;

import org.kobjects.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class BBSDetailsActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener {

    @InjectView(R.id.list)
    RefreshListView list;
    @InjectView(R.id.like)
    ImageView like;
    @InjectView(R.id.btn1)
    ImageView btn1;
    @InjectView(R.id.btn2)
    ImageView btn2;
    @InjectView(R.id.btn3)
    ImageView btn3;
    @InjectView(R.id.aaa)
    RelativeLayout bottomLay;
    private ArrayList<Category> datas;
    private BBSCommentAdapter adapter;
    private int pid;//帖子id;
    private BadgeView bv_likeNum, bv_commentNum;
    private View headerView;
    CircleImageView auth_avater;
    TextView bbstitle;
    TextView bbscontent, bbsName, bbsTime;
    private BBSModel headerModel;
    private LinearLayout imgViewLayout;
    private final int TYPE_NUM = 10;
//    private int refreshRid, refreshType;
    private int refreshPageIndex=1,hotindex=1;
    private boolean isRefresh = false;
    private Category categoryOne, categoryTwo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_detail_activity);
        ScreenUtils.addTranslaterView(this, "isFirstBBSDetail", R.layout.teach_detail_bbs, R.id.teach_btn_main_home);
        ButterKnife.inject(this);
        pid = getIntent().getIntExtra("pid", 0);
        initToolBar();
        WebRequst(pid);
        initBadgeView();
        initData();
        WebRequstGetHotPostsRepyList(hotindex);
        initEvents();
        //判断是否点赞

        // 是否收藏
//        WebRequstGetReplyList(refreshRid, refreshType);

    }

    @Override
    protected void onResume() {
        super.onResume();
        WebRequstGetReplyList(1);
    }

    private void initBadgeView() {
        bv_likeNum = new BadgeView(this, like);
        bv_commentNum = new BadgeView(this, btn2);
        bv_likeNum.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        bv_commentNum.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        bv_likeNum.setBadgeBackgroundColor(Color.parseColor("#4ECDC4"));
        bv_commentNum.setBadgeBackgroundColor(Color.parseColor("#4ECDC4"));
        bv_likeNum.setTextSize(12);
        bv_commentNum.setTextSize(12);
    }

    private float mStartY = 0, mLastY = 0, mLastDeltaY;
    private void initEvents() {
        btn2.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn3.setOnClickListener(this);
        like.setOnClickListener(this);
        list.setOnRefreshListener(this);
        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final float y = event.getY();
                float translationY = bottomLay.getTranslationY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        Log.v(TAG, "Down");
                        mStartY = y;
                        mLastY = mStartY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float mDeltaY = y - mLastY;//间距<0表示向上滑动
                        float newTansY = translationY - mDeltaY;
                        if (newTansY <= bottomLay.getHeight() && newTansY >= 0) {
                            bottomLay.setTranslationY(newTansY);
                        }
                        mLastY = y;
                        mLastDeltaY = mDeltaY;
//                        Log.v(TAG, "Move");
                        break;
                    case MotionEvent.ACTION_UP:
                        ObjectAnimator animator = null;

                        if (mLastDeltaY < 0 && list.getFirstVisiblePosition() >= 1) {

                            animator = ObjectAnimator.ofFloat(bottomLay, "translationY", bottomLay.getTranslationY(), bottomLay.getHeight());
                        } else {
                            animator = ObjectAnimator.ofFloat(bottomLay, "translationY", bottomLay.getTranslationY(), 0);
                        }
                        animator.setDuration(100);
                        animator.start();
                        animator.setInterpolator(AnimationUtils.loadInterpolator(BBSDetailsActivity.this, android.R.interpolator.linear));
//                        Log.v(TAG, "Up");
                        break;
                }
                return false;
            }
        });
    }

    private void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setTitle("帖子详情");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.bbs_details_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_details_more:
                AppPopWindow addPopWindow = new AppPopWindow(this);
                addPopWindow.showPopupWindow(toolbar);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    private void WebRequst(int PostsID) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("PostsID", PostsID);
        map.put("CurUserID", SPUtils.getLoginId(this));
        WebUtils.sendPost(this, ConstantValue.GetPostsDetail, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    headerModel = JSON.parseObject(JSON.parseArray(data).get(0).toString(), BBSModel.class);

                    bbstitle.setText(StringUtils.setFaceToTextView(getBaseContext(), headerModel.getTitle(),bbstitle));
                    String contentTemp = StringUtils.DecodeBase64Str(headerModel.getContent());
                    String contentStr = contentTemp;
                    if (contentTemp.startsWith("<pre")) {
                        contentStr=contentTemp.substring(contentTemp.indexOf(">")+1,contentTemp.lastIndexOf("</pre>"));
                    }
                    bbscontent.setText(StringUtils.setFaceToTextView(getBaseContext(), contentStr,bbscontent));
                    bbsName.setText(headerModel.getNicName());
                    bbsTime.setText(headerModel.getAddTime());
                    if (Boolean.parseBoolean(headerModel.getIsPraise().toLowerCase())) {//true
                        like.setImageResource(R.drawable.ic_article_details_icon1_press);
                    }
                    if (Boolean.parseBoolean(headerModel.getIsFavorite().toLowerCase())) {//true
                        btn1.setImageResource(R.drawable.ic_article_details_icon2_press);
                    }
                    //点赞和评论数赋值
                    int likeCount = headerModel.getFavor_Count();
                    if (likeCount != 0) {
                        bv_likeNum.setText(likeCount + "");
                        bv_likeNum.show();
                    }
                    int comCount = headerModel.getReplyCount();
                    if (comCount != 0) {
                        bv_commentNum.setText(comCount + "");
                        bv_commentNum.show();
                        categoryTwo.setmCategoryName("回帖(" + comCount + ")");

                    }
//                    ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + headerModel.getPortrait(),auth_avater, MyApplication.img_option);
                    Glide.with(getBaseContext())
                            .load(WebUtils.RENXING_WEB_PICS + headerModel.getPortrait())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.ic_picture_loadfailed)
                            .into(auth_avater);
                    List<BBSModel.ImgListEntity> pics = headerModel.getImgList();
                    //动态布局
                    CreateImgViewList(pics);
                } else {
                    Toast.makeText(BBSDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

    //根据pics数量动态添加布局倒imgViewLayout
    private void CreateImgViewList(List<BBSModel.ImgListEntity> pics) {
        if (pics == null || pics.size() == 0) return;
        ArrayList<PhotoModel> ImgeUrls = new ArrayList<PhotoModel>();
        for (BBSModel.ImgListEntity item : pics) {
            PhotoModel model = new PhotoModel();
            model.setChecked(true);
            model.setOriginalPath(WebUtils.RENXING_WEB_PICS + item.getOriginal_Path());
            ImgeUrls.add(model);
        }
        int line = (pics.size() - 1) / 3 + 1;
        for (int i = 0; i < line; i++) {//行数0,1,2
            //每一行创建一个水平布局，包含3个ImageView。最后一行取余
            LinearLayout ll = new LinearLayout(getBaseContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                    LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.topMargin = BmpUtils.dip2px(getBaseContext(), 2);
            for (int j = 0; j < 3; j++) {
                ImageView iv = new ImageView(getBaseContext());
                iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                LinearLayout.LayoutParams imgparam = new LinearLayout.LayoutParams(LinearLayout.
                        LayoutParams.MATCH_PARENT, BmpUtils.dip2px(getBaseContext(), 110), 1.0f);
                if (j == 1) {
                    imgparam.leftMargin = BmpUtils.dip2px(getBaseContext(), 2);
                    imgparam.rightMargin = BmpUtils.dip2px(getBaseContext(), 2);
                }
                //判断如果是最后一行，不赋值
                if (i == line - 1 && j >= (pics.size() % 3 == 0 ? 3 : pics.size() % 3)) {//无图的imgview
                    iv.setEnabled(false);
                } else {
                    Glide.with(getBaseContext())
                            .load(WebUtils.RENXING_WEB_PICS + pics.get(i * 3 + j).getThumb_Path())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.ic_picture_loadfailed)
                            .into(iv);
//                    ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + pics.get(i * 3 + j).getThumb_Path(), iv, MyApplication.img_option);
                    iv.setOnClickListener(new ImagClickListen(3 * i + j, ImgeUrls));
                }
                ll.addView(iv, imgparam);
            }
            imgViewLayout.addView(ll, param);
        }
    }

    //增加收藏，点赞接口，以及分享方法
    private void WebRequstPostsLike(int PostsID) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("PostsID", PostsID);
        map.put("UserID", SPUtils.getLoginId(this));
        WebUtils.sendPostNoProgress(this, ConstantValue.AddPostsLike, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    if (!bv_likeNum.isShown()) bv_likeNum.show();
                    headerModel.setFavor_Count(headerModel.getFavor_Count() + 1);
                    bv_likeNum.setText(headerModel.getFavor_Count()+ "");
                    like.setImageResource(R.drawable.ic_article_details_icon1_press);
                    headerModel.setIsPraise("true");
                    Toast.makeText(BBSDetailsActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BBSDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }
    private void WebRequstDeletePostsLike(int PostsID) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("EntityID", PostsID);
        map.put("Type", 1);
        map.put("UserID", SPUtils.getLoginId(this));
        WebUtils.sendPostNoProgress(this, "DeleteLike", map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    headerModel.setFavor_Count(headerModel.getFavor_Count() - 1);
                    bv_likeNum.setText(headerModel.getFavor_Count()+ "");
                    like.setImageResource(R.drawable.ic_article_details_icon1);
                    headerModel.setIsPraise("false");
                    Toast.makeText(BBSDetailsActivity.this, "取消赞成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BBSDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

    private void WebRequstAddMyCollection(int CID) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserID", SPUtils.getLoginId(this));
        map.put("CID", CID);
        map.put("CType", 1);
        WebUtils.sendPostNoProgress(this, ConstantValue.AddMyCollection, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    btn1.setImageResource(R.drawable.ic_article_details_icon2_press);
                    Toast.makeText(BBSDetailsActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BBSDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

    private void WebRequstGetHotPostsRepyList(int hotIndex) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("CurUserID", SPUtils.getLoginId(this));
        map.put("PostsID", pid);
        map.put("PageIndex", hotIndex);
        map.put("PageSize", TYPE_NUM);
        WebUtils.sendPostNoProgress(this, ConstantValue.GetHotPostsRepyList, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    JSONArray arr = JSON.parseArray(data);

                    for (int i = 0; i < arr.size(); i++) {
                        BBSCommentModel model = JSON.parseObject(arr.get(i).toString(), BBSCommentModel.class);
                            if (categoryOne == null) {
                                categoryOne = new Category("热门回帖(0)");
                                datas.add(0, categoryOne);
                            }
                            categoryOne.addItem(model);
                    }
                 categoryOne.setmCategoryName("热门回帖(" + (categoryOne.getItemCount() - 1) + ")");
                } else {
//                    Toast.makeText(BBSDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

//    private void WebRequstGetReplyList(int RID, final int Type) {
        private void WebRequstGetReplyList(final int pageIndex) {
        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("RID", RID);
//        map.put("Type", Type);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", TYPE_NUM);
        map.put("PostsID", pid);
//        map.put("Num", TYPE_NUM);
        map.put("CurUserID", SPUtils.getLoginId(this));
        WebUtils.sendPost(this, ConstantValue.GetPostsRepyList, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                isRefresh = false;
                if (isSuccess) {
                    JSONArray arr = JSON.parseArray(data);
                    ArrayList<BBSCommentModel> webData = new ArrayList<BBSCommentModel>();
                    for (int i = 0; i < arr.size(); i++) {
                        BBSCommentModel model = JSON.parseObject(arr.get(i).toString(), BBSCommentModel.class);
                        /*if (model.getFavor_Count() >= 10) {
                            if (categoryOne == null) {
                                categoryOne = new Category("热门回帖(0)");
                                datas.add(0, categoryOne);
                            }
                            categoryOne.addItem(model);
                        }*/
                        webData.add(model);
                    }

//                    if (categoryOne != null) {
//                        categoryOne.setmCategoryName("热门回帖(" + (categoryOne.getItemCount() - 1) + ")");
//                    }
                    if (pageIndex > 1) {//加载更多
                        //如果是加载更多，就加到最后，否则加到最前面
                        categoryTwo.addItemAll(categoryTwo.getItemCount() - 1, webData);
                    } else {
                        categoryTwo.removeItemAll();
                        categoryTwo.addItemAll(0, webData);
                    }
                    webData = null;
                    adapter.notifyDataSetChanged();
                } else {
//                    Toast.makeText(BBSDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                if(list!=null) {
                    list.hideFooterView();
                    list.hideHeaderView();
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {
                isRefresh = false;
                if(list!=null) {
                    list.hideFooterView();
                    list.hideHeaderView();
                }
            }
        });
    }

    private void initData() {
        datas = new ArrayList<Category>();
        categoryTwo = new Category("回帖(0)");
        datas.add(categoryTwo);
        headerView = getLayoutInflater().inflate(R.layout.bbs_details_comment_header, null);
        auth_avater = (CircleImageView) headerView.findViewById(R.id.avater);
        bbstitle = (TextView) headerView.findViewById(R.id.title);
        bbscontent = (TextView) headerView.findViewById(R.id.content);
        bbsName = (TextView) headerView.findViewById(R.id.name);
        bbsTime = (TextView) headerView.findViewById(R.id.time);
        imgViewLayout = (LinearLayout) headerView.findViewById(R.id.imgs);
        adapter = new BBSCommentAdapter(getBaseContext(), datas);
        list.addHeaderView(headerView);
        list.setAdapter(adapter);
//        list.setOnItemClickListener(new ItemClickListener());
    }

    private void setAdapterDate() {
        //获取热门回帖，和 回帖数据

    }

    @Override
    public void onClick(View v) {
        if (headerModel == null) {
            Toast.makeText(getBaseContext(), "帖子信息获取中，请稍后操作",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.btn2://需要先判断是否获取成功，在判断改帖子是否可用回复
                Intent intent = new Intent(BBSDetailsActivity.this, SendBBSActivity.class);
                intent.putExtra("type", true);
                intent.putExtra("title", headerModel.getTitle());
                intent.putExtra("pid", headerModel.getPID());
                startActivity(intent);
                break;
            case R.id.btn1://收藏
                WebRequstAddMyCollection(pid);
                break;
            case R.id.btn3://分享
                startActivity(new Intent(getBaseContext(), ToShare.class));
                break;
            case R.id.like://点赞
                if(Boolean.parseBoolean(headerModel.getIsPraise().toLowerCase())) {//true
                    WebRequstDeletePostsLike(pid);
                }else{
                    WebRequstPostsLike(pid);
                }
                break;
        }
    }

    @Override
    public void onDownPullRefresh() {
        if (adapter.hasComment() == 0) {
//            WebRequstGetReplyList(refreshRid, refreshType);
            WebRequstGetReplyList(refreshPageIndex);
            return;
        }
        if (isRefresh&&list!=null) {
            list.hideHeaderView();
        } else {
//            refreshRid = adapter.getFirstDataRid();
//            refreshType = 1;
            refreshPageIndex=1;
            isRefresh = true;
//            WebRequstGetReplyList(refreshRid, refreshType);
            WebRequstGetReplyList(refreshPageIndex);
        }
    }

    @Override
    public void onLoadingMore() {
        if (adapter.hasComment() == 0) {
//            WebRequstGetReplyList(refreshRid, refreshType);
            WebRequstGetReplyList(refreshPageIndex);
            return;
        }
        if (isRefresh&&list!=null) {
            list.hideFooterView();
        } else {
//            refreshRid = adapter.getLastDataRid();
//            refreshType = 2;
            refreshPageIndex++;
            isRefresh = true;
//            WebRequstGetReplyList(refreshRid, refreshType);
            WebRequstGetReplyList(refreshPageIndex);
        }
    }

    /*private class ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (position == 0 || position == 1 || position == adapter.getCount() + 2)
                return;//排除headerView和下拉刷新以及上拉加载更多
            BBSCommentModel item = (BBSCommentModel) adapter.getItem(position - 2);
            Toast.makeText(getBaseContext(), item.getContent(),
                    Toast.LENGTH_SHORT).show();
        }

    }*/

    private class ImagClickListen implements View.OnClickListener {
        private int position;
        private ArrayList<PhotoModel> pics;

        public ImagClickListen(int position, ArrayList<PhotoModel> pics) {
            this.position = position;
            this.pics = pics;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("photos", pics);
            bundle.putInt("position", position);
            Intent intent = new Intent(getBaseContext(), PhotoPreviewActivity.class);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getBaseContext().startActivity(intent);
        }
    }
}
