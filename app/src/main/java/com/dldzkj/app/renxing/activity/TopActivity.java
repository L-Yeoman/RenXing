package com.dldzkj.app.renxing.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;


public class TopActivity extends FragmentActivity{
	TextView centerTittle;
	View bottomLine;
	TextView righttext;
	ImageView back,right_img;
	RelativeLayout container;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.top_layout_activity);
		initTopView();
	}
	
	public void initTopView(){
		centerTittle = (TextView) findViewById(R.id.center_little);
		righttext = (TextView) findViewById(R.id.rigte_text);
		back = (ImageView) findViewById(R.id.lift_back);
		bottomLine= (View) findViewById(R.id.title_line);
		right_img = (ImageView) findViewById(R.id.rigte_img);
		container = (RelativeLayout) findViewById(R.id.container);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	public void addView(int  id){
		container.addView(LayoutInflater.from(this).inflate(id, null),LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}
	
	public  void setCenterTittle(String str){
		centerTittle.setText(str);
	}
	
	public void setRightText(String str){
		righttext.setVisibility(View.VISIBLE);
		right_img.setVisibility(View.GONE);
		righttext.setText(str);
		
	}
	public void setRightImgRes(int  resId){
		righttext.setVisibility(View.GONE);
		right_img.setVisibility(View.VISIBLE);
		right_img.setImageResource(resId);
	}
	public TextView getRightText(){
		return righttext;
	}
	public ImageView getRightImg(){
		return right_img;
	}
	public void needBottomLine(boolean isNeed){
		if (isNeed) {
			bottomLine.setVisibility(View.VISIBLE);
		}else {
			bottomLine.setVisibility(View.GONE);
		}
	}
	
	public ImageView getBack(){
		return back;
	}

	
}
