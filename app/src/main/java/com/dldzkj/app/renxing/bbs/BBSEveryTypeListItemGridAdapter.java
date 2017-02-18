package com.dldzkj.app.renxing.bbs;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dldzkj.app.renxing.R;
import com.lidroid.xutils.BitmapUtils;

public class BBSEveryTypeListItemGridAdapter extends Adapter<BBSEveryTypeListItemGridAdapter.MyViewHolder> {

private ArrayList<String> datas;
private Context c;
private BitmapUtils bmpUtils;
	public BBSEveryTypeListItemGridAdapter(Context c,ArrayList<String> datas,BitmapUtils bmpUtils) {
		this.c=c;
		this.datas=datas;
		this.bmpUtils=bmpUtils;
	}
	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
		 MyViewHolder holder = new MyViewHolder(LayoutInflater.from(c).inflate(R.layout.fragment_bbseverytype_grid_item, parent,
                 false));
		return holder;
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		bmpUtils.display(holder.img, datas.get(position));
	}
	class MyViewHolder extends RecyclerView.ViewHolder {

		private ImageView img;

		public MyViewHolder(View itemView) {
			super(itemView);
			img=(ImageView) itemView.findViewById(R.id.img);
		}

	}
}
