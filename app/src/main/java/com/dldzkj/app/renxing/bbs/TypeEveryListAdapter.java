package com.dldzkj.app.renxing.bbs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.PlantListModel;
import com.dldzkj.app.renxing.utils.ExpressionUtil;
import com.dldzkj.app.renxing.utils.StringUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TypeEveryListAdapter extends BaseAdapter {
    private List<PlantListModel> datas;
    private Context context;
    private LayoutInflater mInflater;

    public TypeEveryListAdapter(Context context, List<PlantListModel> datas) {
        super();
        this.context = context;
        this.datas = datas;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;
//        if (convertView == null) {
        holder = new MyViewHolder();
        convertView = mInflater.inflate(R.layout.fragment_bbs_everytypelist_item, null);
        holder.avater = (ImageView) convertView.findViewById(R.id.avater);
        holder.name = (TextView) convertView.findViewById(R.id.name);
        holder.title = (TextView) convertView.findViewById(R.id.title);
        holder.time = (TextView) convertView.findViewById(R.id.time);
        holder.like = (TextView) convertView.findViewById(R.id.like);
        holder.like1 = (TextView) convertView.findViewById(R.id.like_jinghua);
        holder.like2 = (TextView) convertView.findViewById(R.id.like_hot);
        holder.comment = (TextView) convertView.findViewById(R.id.comment);
        holder.content = (TextView) convertView.findViewById(R.id.content);
        holder.grid = (LinearLayout) convertView.findViewById(R.id.gridview);
        holder.img1 = (ImageView) convertView.findViewById(R.id.img1);
        holder.img2 = (ImageView) convertView.findViewById(R.id.img2);
        holder.img3 = (ImageView) convertView.findViewById(R.id.img3);
        convertView.setTag(holder);
//        } else {
//            holder = (MyViewHolder) convertView.getTag();
//        }
        setItemData(holder, position);
        return convertView;
    }


    class MyViewHolder {
        ImageView avater;
        TextView name, title;
        TextView time;
        TextView like, like1, like2;
        TextView comment;
        TextView content;
        LinearLayout grid;
        ImageView img1, img2, img3;
    }

    private void setItemData(final MyViewHolder holder, final int position) {
        PlantListModel item = datas.get(position);
        holder.name.setText(item.getNicName());
        holder.title.setText(StringUtils.setFaceToTextView(context, item.getTitle(),holder.title));
        holder.time.setText(item.getAddTime());
        holder.comment.setText(item.getReplyCount());
        String content=StringUtils.DecodeBase64Str(item.getContent());
        Spanned contentStr = Html.fromHtml(content);
        holder.content.setText(StringUtils.setFaceToTextView(context, contentStr.toString(),holder.content));
        holder.like.setVisibility(("1".equals(item.getIs_Top()) ? View.VISIBLE : View.GONE));
        holder.like1.setVisibility(("1".equals(item.getIs_Essence()) ? View.VISIBLE : View.GONE));
        holder.like2.setVisibility(("1".equals(item.getIs_Hot()) ? View.VISIBLE : View.GONE));
        List<PlantListModel.ImgListEntity> pics = item.getImgList();
        if (pics.size() == 0) {
            holder.grid.setVisibility(View.GONE);
        } else {
            holder.grid.setVisibility(View.VISIBLE);
//            ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + pics.get(0).getThumb_Path(), holder.img1, MyApplication.img_option);
            Glide.with(context).load(WebUtils.RENXING_WEB_PICS + pics.get(0).getThumb_Path())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_picture_loadfailed)
                    .into(holder.img1);
            if (pics.size() > 1) {
                Glide.with(context).load(WebUtils.RENXING_WEB_PICS + pics.get(1).getThumb_Path())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_picture_loadfailed)
                        .into(holder.img2);
//                ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + pics.get(1).getThumb_Path(), holder.img2, MyApplication.img_option);
            }
            if (pics.size() > 2) {
                Glide.with(context).load(WebUtils.RENXING_WEB_PICS + pics.get(2).getThumb_Path())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_picture_loadfailed)
                        .into(holder.img3);
//                ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + pics.get(2).getThumb_Path(), holder.img3, MyApplication.img_option);
            }
        }
    }

}
