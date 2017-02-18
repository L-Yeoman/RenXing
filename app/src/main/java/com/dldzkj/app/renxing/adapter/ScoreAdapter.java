package com.dldzkj.app.renxing.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ScoreModel;

public class ScoreAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<ScoreModel> datas;
    private Context context;
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    public ScoreAdapter(Context context, List<ScoreModel> datas) {
        super();
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0)
            return TYPE_RIGHT;
        return TYPE_LEFT;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        setItemData((MyViewHolder) holder, position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        if (viewType == TYPE_LEFT) {
            holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_jifen_detail_list_item_right, parent,
                    false));
        } else {
            holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_jifen_detail_list_item_left, parent,
                    false));
        }
        return holder;
    }


    class MyViewHolder extends ViewHolder {
        TextView reason;
        TextView value;
        TextView time;

        public MyViewHolder(View view) {
            super(view);
            reason = (TextView) view.findViewById(R.id.reason);
            time = (TextView) view.findViewById(R.id.time);
            value = (TextView) view.findViewById(R.id.score);
        }
    }

    private void setItemData(final MyViewHolder holder, int position) {
        ScoreModel item = datas.get(position);

        holder.time.setText(item.getAddTime());
        holder.reason.setText(item.getOperateitem());
        holder.value.setText("+" + item.getPointNum());
    }
}