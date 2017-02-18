package com.dldzkj.app.renxing.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.Picture;
import com.lidroid.xutils.BitmapUtils;

public class PhotoLook extends BaseOtherActivity {
	
	private TextView mTextView;
	private ViewPager mPager;
	private ArrayList<Picture> photos;
	private BitmapUtils mUtils;
	private int currentItem;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_look);
		initView();
		initData();
	}
	
	public void initView(){
		mTextView=(TextView) findViewById(R.id.apl_textView);
		mPager=(ViewPager) findViewById(R.id.apl_viewPager);
	}
	
	public void initData(){
		mUtils= new BitmapUtils(getApplicationContext());
		photos=(ArrayList<Picture>)getIntent().getExtras().getSerializable("");
	//	photos.remove(photos.size() - 1);
		currentItem=(Integer)getIntent().getExtras().get("currentItem");
		MyAdapter _Adapter=new MyAdapter();
		_Adapter.setData();
		mPager.setAdapter(_Adapter);
		mPager.setOnPageChangeListener(new OnPageChange());
		mPager.setCurrentItem(currentItem);
		mTextView.setText((currentItem+1)+"/"+(photos.size()));
	}
	
	private class OnPageChange implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			Log.i("onPageScrolled", arg0+"arg0---arg2"+arg2);
		}
		@Override
		public void onPageSelected(int arg0) {
			Log.i("onPageSelected", arg0+"");
			mTextView.setText((arg0+1)+"/"+photos.size());
		}
	}
	
	private class MyAdapter extends PagerAdapter{
		private List<View> views;	
		public void setData(){
			views=new ArrayList<View>();
			for(int i=0;i<photos.size();i++){
				ImageView _ImageView=new ImageView(getApplicationContext());
				_ImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			//	mUtils.display(_ImageView, photos.get(i).getImgUrl());
				Glide.with(PhotoLook.this)
						.load(photos.get(i).getImgUrl())
						.into(_ImageView);
				views.add(_ImageView);
			}			
		}
		
		
		@Override
		public int getCount() {
			return views.size();
		}

		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			((ViewPager)container).removeView(views.get(position));
		}

		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return view==arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			((ViewPager)view).addView(views.get(position));
			return views.get(position);
		}
		
	}

}
