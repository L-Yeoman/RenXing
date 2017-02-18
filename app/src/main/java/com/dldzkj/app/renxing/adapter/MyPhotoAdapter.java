package com.dldzkj.app.renxing.adapter;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.dldzkj.app.renxing.R;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.MyViewHolder> {
	Activity act;

	public MyPhotoAdapter(Activity act) {
		// TODO Auto-generated constructor stub
		this.act = act;
	}

	// 这里指设置 单个的iten监听
	private OnItemClickLitener mOnItemClickLitener;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
		
		this.mOnItemClickLitener = mOnItemClickLitener;
	
	}

	public interface OnItemClickLitener {
		void onItemClick(View view, int position);

		void onItemLongClick(View view, int position);
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return 30;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		MyViewHolder holder = new MyViewHolder(LayoutInflater.from(act)
				.inflate(R.layout.myphoto_list_item, arg0, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int arg1) {
		// TODO Auto-generated method stub

		if (mOnItemClickLitener != null) {
			holder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int pos = holder.getLayoutPosition();
					mOnItemClickLitener.onItemClick(holder.itemView, pos);
				}
			});

			holder.itemView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					int pos = holder.getLayoutPosition();
					mOnItemClickLitener.onItemLongClick(holder.cb, pos);
					return false;
				}
			});
		}
	}



	class MyViewHolder extends ViewHolder {
		ImageView img;
		CheckBox cb;

		public MyViewHolder(View view) {
			super(view);
			img = (ImageView) view.findViewById(R.id.photo_imageView1);
			cb = (CheckBox) view.findViewById(R.id.photo_checkBox1);
		}
	}

}
