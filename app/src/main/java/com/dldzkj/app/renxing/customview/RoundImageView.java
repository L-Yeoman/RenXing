package com.dldzkj.app.renxing.customview;




import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.utils.BmpUtils;

public class RoundImageView extends ImageView {
	private int mBorderThickness =0;
	private Context mContext;
	private int defaultColor = 0xFFFFFFFF;
	private int mBorderOutsideColor = 0;
	private int mBorderInsideColor =0;
	private int defaultWidth = 0;
	private int defaultHeight = 0;

	public RoundImageView(Context context) {
		super(context);
		mContext = context;
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setCustomAttributes(attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		setCustomAttributes(attrs);
	}
	public void setOutLineColor(int r,int  g,int b){
		/*mBorderOutsideColor=Color.rgb(r, g, b);
		mBorderInsideColor=Color.rgb(r, g, b+1);*/
		mBorderOutsideColor=Color.TRANSPARENT;
		mBorderInsideColor=Color.TRANSPARENT;
	}
	private void setCustomAttributes(AttributeSet attrs) {
		TypedArray a = mContext.obtainStyledAttributes(attrs,
				R.styleable.roundedimageview);
		mBorderThickness = a.getDimensionPixelSize(
				R.styleable.roundedimageview_border_thickness, BmpUtils.dip2px(mContext, 1));
		mBorderOutsideColor = a
				.getColor(R.styleable.roundedimageview_border_outside_color,
						Color.rgb(248, 248, 248));//	
		mBorderInsideColor = a.getColor(
				R.styleable.roundedimageview_border_inside_color, Color.rgb(247, 247, 247));
		a.recycle();
	
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
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		if(b==null)
			return;
		
		Bitmap bitmap = b.copy(Config.ARGB_8888, true);
		if (defaultWidth == 0) {
			defaultWidth = getWidth();

		}
		if (defaultHeight == 0) {
			defaultHeight = getHeight();
		}
		// 
		// if (defaultWidth != 0 && defaultHeight != 0) {
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		// defaultWidth, defaultHeight);
		// setLayoutParams(params);
		// }
		int radius = 0;
		if (mBorderInsideColor != defaultColor
				&& mBorderOutsideColor != defaultColor) {// 
			radius = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - 2 * mBorderThickness;
			// 鐢诲唴鍦�		
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderInsideColor);
			// 鐢诲鍦�			
			drawCircleBorder(canvas, radius + mBorderThickness
					+ mBorderThickness / 2, mBorderOutsideColor);
		} else if (mBorderInsideColor != defaultColor
				&& mBorderOutsideColor == defaultColor) {//
			radius = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - mBorderThickness;
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderInsideColor);
		} else if (mBorderInsideColor == defaultColor
				&& mBorderOutsideColor != defaultColor) {
			radius = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - mBorderThickness;
			drawCircleBorder(canvas, radius + mBorderThickness / 2,
					mBorderOutsideColor);
		} else {// 
			radius = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2;
		}
		//得到圆形
		Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
		//float left，图像显示的左边位置
		//float top, 图像显示的上边位置
		canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight
				/ 2 - radius, null);
	}

	/**
	 * 
	 * @param radius 鍗婂緞
	 * @param bmp 婧愬浘
	 */
	public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;

		// 
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth) {// 
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			y = (bmpHeight - bmpWidth) / 2;
			// 	
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else if (bmpHeight < bmpWidth) {// 
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else {
			squareBitmap = bmp;
		}

		if (squareBitmap.getWidth() != diameter
				|| squareBitmap.getHeight() != diameter) {//
			scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
					diameter, true);

		} else {
			scaledSrcBmp = squareBitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
				scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
				paint);
		//设置两张图片相交时的模式,只显示相交的区域
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;
	}

	/**
	 * 
	 */
	private void drawCircleBorder(Canvas canvas, int radius, int color) {
		Paint paint = new Paint();
		/**/
		//http://www.cnblogs.com/-OYK/archive/2011/10/25/2223624.html（paint详细）
		//抗锯齿
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		//* 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰  
		paint.setDither(true);
		paint.setColor(color);
		/* */
		paint.setStyle(Paint.Style.STROKE);
		/* */
		//当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度  
		paint.setStrokeWidth(mBorderThickness);
		//参数：x,y（圆心坐标）,半径，画笔
		canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
	}

}
