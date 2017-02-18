package com.dldzkj.app.renxing.bean;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bbs.SendBBSActivity;
import com.dldzkj.app.renxing.customview.SlideShowView;
import com.dldzkj.app.renxing.utils.StringUtils;

public class BBSMainAdapter2 extends BaseAdapter {
	private List<MyPostBBS> datas;
	private Context context;
	private boolean isDeleteMode;
	private List<String> boolList = new ArrayList<String>();
	public BBSMainAdapter2(Context context,List<MyPostBBS> datas) {
		super();
		this.context=context;
		this.datas=datas;
	}

	public boolean isDeleteMode() {
		return isDeleteMode;
	}

	public void setDeleteMode(boolean isDeleteMode){
		this.isDeleteMode=isDeleteMode;
	}
	public List<String> getBoolList() {
		return boolList;
	}

	public void setBoolList(List<String> boolList) {
		this.boolList = boolList;
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
		if (convertView == null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.bbs_main_item, parent,
					false);
			holder=new MyViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (MyViewHolder)convertView.getTag();
		}
		setItemData(holder,position);
		return convertView;
	}



	class MyViewHolder {
		TextView tv_itemTitle;
		TextView tv_itemTime;
		TextView tv_itemState;
		CheckBox cb;

        public MyViewHolder(View convertView) {
			cb= (CheckBox) convertView.findViewById(R.id.checkbox);
			tv_itemTitle = (TextView) convertView.findViewById(R.id.bbs_item_title);
			tv_itemTime = (TextView) convertView.findViewById(R.id.bbs_item_time);
			tv_itemState = (TextView) convertView.findViewById(R.id.bbs_item_state);
        }
    }

	private void setItemData(final MyViewHolder holder, final int position) {

		final MyPostBBS mContent = datas.get(position);
		holder.tv_itemTitle.setText(StringUtils.DecodeURLStr(mContent.getTitle()));
		holder.tv_itemTime.setText(mContent.getAddTime());
		switch (Integer.parseInt(mContent.getEnabled())) {
			case 1:
				holder.tv_itemState.setText("已审核");
				break;
			case 0:
				holder.tv_itemState.setText("未审核");
				holder.tv_itemState.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_audited_not), null, null, null);
				holder.tv_itemState.setTextColor(context.getResources().getColor(R.color.app_gray_font));
				break;
		}

		if (isDeleteMode){
			holder.cb.setVisibility(View.VISIBLE);
			holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						boolList.add(position+"");
					} else {
						boolList.remove(position+"");
					}
				}
			});
		}else{
			holder.cb.setVisibility(View.GONE);
		}
	}

}
