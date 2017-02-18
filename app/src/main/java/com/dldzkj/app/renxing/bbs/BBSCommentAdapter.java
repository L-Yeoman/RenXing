package com.dldzkj.app.renxing.bbs;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class BBSCommentAdapter extends BaseAdapter {

    private static final int TYPE_CATEGORY_ITEM = 0;
    private static final int TYPE_ITEM = 1;

    private ArrayList<Category> mListData;
    private LayoutInflater mInflater;
    private Context context;

    public BBSCommentAdapter(Context context, ArrayList<Category> pData) {
        mListData = pData;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int count = 0;

        if (null != mListData) {
            //  所有分类中item的总和是ListVIew  Item的总个数
            for (Category category : mListData) {
                count += category.getItemCount();
            }
        }

        return count;
    }

    public int hasComment() {//判断是否有评论
        int count = 0;
        if (null != mListData) {
            //  所有分类中item的总和是ListVIew  Item的总个数
            if(mListData.size()==1){
                count = mListData.get(0).getItemCount()-1;
            }else{
                count = mListData.get(1).getItemCount()-1;
            }

        }
        return count;
    }

    public int getFirstDataRid() {
        int pos=0;
        if(mListData.size()==1){
            pos = 1;
        }else{
            pos = mListData.get(0).getItemCount() + 1;
        }
        BBSCommentModel model = (BBSCommentModel) getItem(pos);
        Log.d("zxl", "第一条：" + model.getContent());
        return Integer.parseInt(model.getRID());
    }

    public int getLastDataRid() {
        int pos=0;
        if(mListData.size()==1){
            pos = mListData.get(0).getItemCount()-1;
        }else{
            pos = mListData.get(0).getItemCount()+mListData.get(1).getItemCount()-1;
        }

        BBSCommentModel model = (BBSCommentModel) getItem(pos);
        Log.d("zxl", "最后一条：" + model.getContent());
        return Integer.parseInt(model.getRID());
    }

    @Override
    public Object getItem(int position) {

        // 异常情况处理
        if (null == mListData || position < 0 || position > getCount()) {
            return null;
        }

        // 同一分类内，第一个元素的索引值
        int categroyFirstIndex = 0;

        for (Category category : mListData) {
            int size = category.getItemCount();
            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;
            // item在当前分类内
            if (categoryIndex < size) {
                return category.getItem(categoryIndex);
            }

            // 索引移动到当前分类结尾，即下一个分类第一个元素索引
            categroyFirstIndex += size;
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        // 异常情况处理
        if (null == mListData || position < 0 || position > getCount()) {
            return TYPE_ITEM;
        }


        int categroyFirstIndex = 0;

        for (Category category : mListData) {
            int size = category.getItemCount();
            // 在当前分类中的索引值
            int categoryIndex = position - categroyFirstIndex;
            if (categoryIndex == 0) {
                return TYPE_CATEGORY_ITEM;
            }

            categroyFirstIndex += size;
        }

        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_CATEGORY_ITEM:
                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.listview_item_type, null);
                }

                TextView textView = (TextView) convertView.findViewById(R.id.count_type);
                String itemValue = (String) getItem(position);
                textView.setText(itemValue);
                break;

            case TYPE_ITEM:
                ViewHolder viewHolder = null;
//                if (null == convertView) {

                convertView = mInflater.inflate(R.layout.bbs_comment_item, null);

                viewHolder = new ViewHolder();
                viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.count = (TextView) convertView.findViewById(R.id.floor);
                viewHolder.like = (ImageView) convertView.findViewById(R.id.like);
                viewHolder.avart = (CircleImageView) convertView.findViewById(R.id.avater);
                viewHolder.pics = (LinearLayout) convertView.findViewById(R.id.imgs);
                convertView.setTag(viewHolder);
//                } else {
//                    viewHolder = (ViewHolder) convertView.getTag();
//                }

                // 绑定数据
                final BBSCommentModel model = (BBSCommentModel) getItem(position);
//              viewHolder.content.setText(model.getContent());
                viewHolder.content.setText(StringUtils.setFaceToTextView(context, model.getContent(),viewHolder.content));
                viewHolder.name.setText(model.getNicName());
                viewHolder.time.setText(model.getReplyTime());
                if (model.getFavor_Count() == 0) {
                } else {
                    viewHolder.count.setText(model.getFavor_Count() + "");
                }
                if(Boolean.parseBoolean(model.getIsPraise().toLowerCase())){
                    viewHolder.like.setImageResource(R.drawable.ic_home_like_press);
                }else{
                    viewHolder.like.setImageResource(R.drawable.ic_home_like);
                }

                Glide.with(context).load(WebUtils.RENXING_WEB_PICS + model.getPortrait())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_picture_loadfailed)
                        .into(viewHolder.avart);
//              ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + model.getPortrait(), viewHolder.avart, MyApplication.img_option);
                CreateImgViewList(model.getImgList(), viewHolder.pics);
                viewHolder.like.setOnClickListener(new likeClickListen(model));
                break;
        }

        return convertView;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != TYPE_CATEGORY_ITEM;
    }


    class ViewHolder {
        TextView content;
        TextView name;
        TextView time;
        TextView count;
        ImageView like;
        CircleImageView avart;
        LinearLayout pics;
    }

    private void WebRequstPostsReplyLike(final BBSCommentModel model) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("RID", model.getRID());
        map.put("UserID", SPUtils.getLoginId(context));
        WebUtils.sendPostNoProgress(context, ConstantValue.AddPostsReplyLike, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    Toast.makeText(context, "评论点赞成功", Toast.LENGTH_SHORT).show();
                    model.setFavor_Count(model.getFavor_Count() + 1);
                    model.setIsPraise("true");
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }
    private void WebRequstPostsDeleteReplyLike(final BBSCommentModel model) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("EntityID", model.getRID());
        map.put("Type", 2);
        map.put("UserID", SPUtils.getLoginId(context));
        WebUtils.sendPostNoProgress(context, "DeleteLike", map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    Toast.makeText(context, "评论取消赞成功", Toast.LENGTH_SHORT).show();
                    model.setFavor_Count(model.getFavor_Count() -1);
                    model.setIsPraise("false");
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }
    //根据pics数量动态添加布局倒imgViewLayout
    private void CreateImgViewList(List<BBSCommentModel.ImgListEntity> pics, LinearLayout layout) {
        if (pics == null || pics.size() == 0) return;
        ArrayList<PhotoModel> ImgeUrls = new ArrayList<PhotoModel>();
        for (BBSCommentModel.ImgListEntity item : pics) {
            PhotoModel model = new PhotoModel();
            model.setChecked(true);
            model.setOriginalPath(WebUtils.RENXING_WEB_PICS + item.getOriginal_Path());
            ImgeUrls.add(model);
        }
        int line = (pics.size() - 1) / 3 + 1;
        for (int i = 0; i < line; i++) {//行数0,1,2
            //每一行创建一个水平布局，包含3个ImageView。最后一行取余
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                    LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            param.topMargin = BmpUtils.dip2px(context, 2);
            for (int j = 0; j < 3; j++) {
                ImageView iv = new ImageView(context);
                iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                LinearLayout.LayoutParams imgparam = new LinearLayout.LayoutParams(LinearLayout.
                        LayoutParams.MATCH_PARENT, BmpUtils.dip2px(context, 110), 1.0f);
                if (j == 1) {
                    imgparam.leftMargin = BmpUtils.dip2px(context, 2);
                    imgparam.rightMargin = BmpUtils.dip2px(context, 2);
                }
                //判断如果是最后一行，不赋值
                if (i == line - 1 && j >= (pics.size() % 3 == 0 ? 3 : pics.size() % 3)) {//无图的imgview
                    iv.setEnabled(false);
                } else {
                    Glide.with(context)
                            .load(WebUtils.RENXING_WEB_PICS + pics.get(i * 3 + j).getThumb_Path())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.ic_picture_loadfailed)
                            .into(iv);
//                    ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + pics.get(i * 3 + j).getThumb_Path(), iv, MyApplication.img_option);
                    iv.setOnClickListener(new ImagClickListen(3 * i + j, ImgeUrls));
                }
                ll.addView(iv, imgparam);
            }
            layout.addView(ll, param);
        }
    }

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
            Intent intent = new Intent(context, PhotoPreviewActivity.class);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private class likeClickListen implements View.OnClickListener {
        private BBSCommentModel model;

        public likeClickListen(BBSCommentModel model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            if(Boolean.parseBoolean(model.getIsPraise().toLowerCase())){
                WebRequstPostsDeleteReplyLike(model);
            }else{
                WebRequstPostsReplyLike(model);
            }

        }
    }
}
