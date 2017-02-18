package com.dldzkj.app.renxing.customview;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ArticleModel;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ArticlePostView extends LinearLayout {
	private ArticleModel mModel;
	private SlideShowView cView;
	private int mPosition;
	private TextView titleText,likeCount,contentText,mTime;
	private ImageView mImage,mMask;
	public Context mContext;
	private BitmapUtils mUtils;
	private RelativeLayout mLayout;

	public ArticlePostView(Context context,int position) {
		this(context,position, null);
	}
	public ArticlePostView(Context context,int position, AttributeSet attrs) {
		this(context, position, null, 0);
	}


	@SuppressLint("NewApi")
	public ArticlePostView(Context context,int position, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		mPosition=position;
		initView();
	}
	
	public void setData(ArticleModel model,int position,BitmapUtils bitmapUtils){
		mModel=model;
		mPosition=position;
		mUtils=bitmapUtils;
		initData();
		/*if(mPosition>0)
		{
			initData();
		}*/
		
	}
	public void initView(){
		/*if(mPosition==0){
			LayoutInflater.from(getContext()).inflate(R.layout.cycle_image_layout, this);
			cView=(SlideShowView)findViewById(R.id.cycle_view);
			cView.setPicsLinkData(null);
			return;
		}*/
	//	LayoutInflater.from(getContext()).inflate(R.layout.article_list_item, this);
		LayoutInflater.from(getContext()).inflate(R.layout.articles_item, this);
		titleText=(TextView)findViewById(R.id.ali_title);
		likeCount=(TextView)findViewById(R.id.ali_likeCount);
		mImage=(ImageView) findViewById(R.id.ali_image);
		mMask=(ImageView)findViewById(R.id.ali_image_bg);
		mLayout = (RelativeLayout)findViewById(R.id.title_layout);
	//	contentText=(TextView)findViewById(R.id.ali_content);
		mTime=(TextView)findViewById(R.id.ali_time);
	}
	public void initData(){
			//	int i= (mPosition+1)%mModel.getListImageUri().length;
			//	mUtils.display(mImage, WebUtils.RENXING_WEB+mModel.getImg_Url());
	//	mLayout.getBackground().setAlpha(50);
		if(mModel.getImg_Url().isEmpty()){
			mImage.setVisibility(View.GONE);
		}else{
			mImage.setVisibility(View.VISIBLE);
//			mUtils.display(mImage, WebUtils.RENXING_WEB+mModel.getImg_Url());
			ImageLoader.getInstance().displayImage( WebUtils.RENXING_WEB+mModel.getImg_Url(),mImage, MyApplication.img_option);
			//计算图片高
			float i =(float)(BmpUtils.getwindowsWidth()-BmpUtils.dip2px(mContext,20))*((float)434/(float)1024);
			Log.d("lyBIn",i+"");
			mImage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(int)i));
			mMask.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(int)i));
		}
				titleText.setText(mModel.getTitle());
				likeCount.setText(mModel.getFavor_Count() + "");
		//		contentText.setText(mModel.getSummary());
				mTime.setText(mModel.getAdd_Time());
	}
}
