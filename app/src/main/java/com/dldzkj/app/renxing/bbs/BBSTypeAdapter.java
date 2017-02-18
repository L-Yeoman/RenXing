package com.dldzkj.app.renxing.bbs;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.BBSTypeModel;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BBSTypeAdapter extends BaseAdapter {

	private List<BBSTypeModel> actors;

	private Context mContext;



	public BBSTypeAdapter(Context context, List<BBSTypeModel> actors) {
		this.mContext = context;
		this.actors = actors;

	}

	@Override
	public int getCount() {
		return actors.size();
	}

	@Override
	public Object getItem(int position) {
		return actors.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
//		if (convertView == null) {
			convertView=LayoutInflater.from(mContext).inflate(R.layout.activity_bbs_type_list_item, parent,
					false);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
//		}else {
//			holder = (ViewHolder)convertView.getTag();
//		}
		BBSTypeModel p = actors.get(position);
		holder.title.setText(p.getBoardName());
		holder.subTitle.setText(p.getDescription());
		if (p.getBackImage()!=null&&!p.getBackImage().isEmpty()){
//			ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + p.getBackImage(), holder.mImageView, MyApplication.img_option);
			Glide.with(mContext).load(WebUtils.RENXING_WEB_PICS + p.getBackImage())
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.error(R.drawable.ic_picture_loadfailed)
					.into(holder.mImageView);
		}else{
			holder.mImageView.setBackgroundResource(R.color.app_theme);
		}
		return convertView;

	}

	// 重写的自定义ViewHolder
	public static class ViewHolder  {
		public TextView title;
		public TextView subTitle;
		public ImageView mImageView;
		public CardView layout;

		public ViewHolder(View v) {
			subTitle = (TextView) v.findViewById(R.id.sub_title);
			title = (TextView) v.findViewById(R.id.title);
			mImageView = (ImageView) v.findViewById(R.id.img_plate_icon);
			layout = (CardView) v.findViewById(R.id.layout);
		}
	}
}
