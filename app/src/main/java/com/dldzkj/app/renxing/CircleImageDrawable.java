package com.dldzkj.app.renxing;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public class CircleImageDrawable extends Drawable  
{  
  
    private Paint mPaint;  
    private int mWidth,radioValue;
    private Bitmap mBitmap ;
    public CircleImageDrawable(Bitmap bitmap)
    {
        mBitmap = bitmap ;
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP,
                TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
        mWidth = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
        this.radioValue=mWidth / 2;
    }
    /**********图片，是否设置半径大小（不设置默认为宽度一半），默认半径大小***********/
    public CircleImageDrawable(Bitmap bitmap,boolean needOtherSize,int radioValue)
    {  
        mBitmap = bitmap ;   
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP,  
                TileMode.CLAMP);  
        mPaint = new Paint();  
        mPaint.setAntiAlias(true);  
        mPaint.setShader(bitmapShader);
        mWidth = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
        if(needOtherSize){
            this.radioValue=radioValue;
            return;
        }

        this.radioValue=mWidth / 2;
    }  
  
    @Override  
    public void draw(Canvas canvas)  
    {  
        canvas.drawCircle(mWidth / 2, mWidth / 2, radioValue, mPaint);
    }  
  
    @Override  
    public int getIntrinsicWidth()  
    {  
        return mWidth;  
    }  
  
    @Override  
    public int getIntrinsicHeight()  
    {  
        return mWidth;  
    }  
  
    @Override  
    public void setAlpha(int alpha)  
    {  
        mPaint.setAlpha(alpha);  
    }  
  
    @Override  
    public void setColorFilter(ColorFilter cf)  
    {  
        mPaint.setColorFilter(cf);  
    }  
  
    @Override  
    public int getOpacity()  
    {  
        return PixelFormat.TRANSLUCENT;  
    }  
  
}  