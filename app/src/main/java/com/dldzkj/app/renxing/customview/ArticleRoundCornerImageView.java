package com.dldzkj.app.renxing.customview;

import com.lidroid.xutils.bitmap.core.AsyncDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ArticleRoundCornerImageView extends ImageView {
	
	private Context mContext;
	private  int LineTop;//虚线距离顶部的距离
	private final int radio=10;//圆角半径和缺角半径

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	public ArticleRoundCornerImageView(Context context) {
		super(context);
		mContext = context;
		LineTop=dip2px(mContext, 40);
	}

	public ArticleRoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LineTop=dip2px(mContext, 40);
	}

	public ArticleRoundCornerImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		LineTop=dip2px(mContext, 40);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		this.measure(0, 0);
		if (drawable.getClass() == NinePatchDrawable.class)
			return;
		Bitmap b = null;
		if(drawable instanceof BitmapDrawable){
	    	b =  ((BitmapDrawable)drawable).getBitmap() ;
	    }else if(drawable instanceof AsyncDrawable){
	    	b = Bitmap 
	    			.createBitmap( 
	    			getWidth(), 
	    			getHeight(), 
	    			drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
	    			: Config.RGB_565);
	    			Canvas canvas1 = new Canvas(b); 
	    			// canvas.setBitmap(bitmap); 
	    			drawable.setBounds(0, 0, getWidth(), 
	    			getHeight()); 
	    			drawable.draw(canvas1); 
	    }
		
		
		if (b == null)
			return;

		Bitmap bitmap = b.copy(Config.ARGB_8888, true);
		float XScale=((float)getWidth()/bitmap.getWidth());
		float YScale=((float)getHeight()/bitmap.getHeight());
		Matrix mm=new Matrix();
		mm.postScale(XScale, YScale);  
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),   
				bitmap.getHeight(),mm,true);  
		
		Bitmap roundBitmap = createRoundConerImage(resizeBmp, radio);
		canvas.drawBitmap(roundBitmap, 0, 0, null);
	}
	private Bitmap createRoundConerImage(Bitmap source,int mRadius)
	{
		int mWidth=getWidth();
		int mHeight=getHeight();
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		
		RectF rect = new RectF(0, 0, mWidth, mHeight);
		//圆角矩形
		canvas.drawRoundRect(rect, mRadius, mRadius, paint);
		
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0,0, paint);
		//以指定高度的左端为圆心，得到半圆
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		canvas.drawCircle(0, LineTop, mRadius, paint);
		//以右端
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		canvas.drawCircle(mWidth, LineTop, mRadius, paint);
		
		 Paint p = new Paint();
		 p.setStrokeWidth(1.5f);
		 p.setColor(Color.parseColor("#50ffffff"));
		 //绘制长度为4的实线，再绘长4的空白
		PathEffect effects = new DashPathEffect(new float[] { 4, 4}, 1);  
		p.setPathEffect(effects);  
		canvas.drawLine(0+mRadius, LineTop, mWidth-mRadius, LineTop, p);
		return target;
	}

}
