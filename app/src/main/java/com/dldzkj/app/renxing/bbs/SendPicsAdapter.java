package com.dldzkj.app.renxing.bbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import de.greenrobot.event.EventBus;

public class SendPicsAdapter extends RecyclerView.Adapter<SendPicsAdapter.MyViewHolder> {
    private List<String> datas;
    private Context context;
    public SendPicsAdapter(Context context,List<String> datas) {
        super();
        this.context = context;
        this.datas = datas;
    }

    /*public void addFirstPics(){
        notifyItemInserted(0);
        datas.add(0, "http://news.sctv.com/shxw/sjwx/201112/W020111220616190317362.jpg");
        EventBus.getDefault().post(
                new SendBBSEvent(1));
    }
*/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.send_bbs_pic_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if (position==datas.size()){//最后一个
            holder.pic.setImageResource(R.drawable.ic_add_photo2);
            holder.delete.setVisibility(View.GONE);
            holder.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(datas.size()>=10){
                        final AppDialog dialog = new AppDialog(context, R.style.dialog,AppDialog.TIP_TYPE);
                        dialog.show();
                        dialog.setTitle("最多选择10张");
                        dialog.setSureText("我知道了");
                        return;
                    }
                    EventBus.getDefault().post(
                            new SendBBSEvent(500));
                }
            });
        }else{
            holder.delete.setVisibility(View.VISIBLE);
            Glide.with(context).load(datas.get(position)).placeholder(R.drawable.ic_picture_loading).error(R.drawable.ic_picture_loadfailed).into(holder.pic);
//            ImageLoader.getInstance().displayImage("file://" + datas.get(position),holder.pic, MyApplication.img_option);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemRemoved(position);
                datas.remove(position);
                notifyItemRangeChanged(position, getItemCount());//需要刷新position之后的数据，保证position更改
                EventBus.getDefault().post(
                        new SendBBSEvent(-1));
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return datas.size()+1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView pic;
        ImageView delete;

        public MyViewHolder(View convertView) {
            super(convertView);
            pic = (ImageView) convertView.findViewById(R.id.item_pic);
            delete = (ImageView) convertView.findViewById(R.id.item_delete);

        }
    }

}
