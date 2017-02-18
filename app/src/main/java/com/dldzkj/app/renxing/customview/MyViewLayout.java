package com.dldzkj.app.renxing.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;

public class MyViewLayout extends FrameLayout {
    private ImageView topImg, bottomImg;
    private View lineImg;//遮盖层
    private int[] resImgs = new int[]{R.drawable.ic_scan_img1, R.drawable.ic_scan_img2};
    private int lineH, screenW, screenH, lineY;
    private final int SPEED = 5;//滚动速度
    private boolean toTop = false,FirstOnce=true;//滚动方向
    public boolean stopAnim=false;
 /*   private Bitmap bottomBmp;
    private Canvas mCanvas;
    protected Path mPath ;
    private Bitmap mBitmap;*/

    public MyViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Display d = ((Activity) context).getWindowManager().getDefaultDisplay();
        screenW = d.getWidth();
        screenH = d.getHeight();
        Bitmap mBackBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_icon_light1);
        Bitmap bottomBmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_scan_img2);
        lineH = mBackBitmap.getHeight();
        lineY = 0 - lineH;
//        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
        // 初始化bitmap
//        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        mCanvas = new Canvas(mBitmap);
//        mCanvas.drawBitmap(bottomBmp,0,0,null);
    }

    /**
     * 设置menu item的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        topImg = (ImageView) findViewById(R.id.img_top);
        bottomImg = (ImageView) findViewById(R.id.img_bottom);
        lineImg = findViewById(R.id.img_line);
        bottomImg.layout(0, 0, screenW, screenH);
        topImg.layout(0, 0, screenW, screenH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(stopAnim){
            return;
        }
        if (toTop && lineY <= 0) {//上滑至屏幕顶部换图换方向换坐标
            lineImg.layout(0, -lineH, screenW, 0);
            lineImg.setBackgroundResource(R.drawable.ic_icon_light1);
            topImg.setVisibility(View.VISIBLE);
            bottomImg.setVisibility(View.GONE);
            lineY = -lineH;
            toTop = false;
        } else if (!toTop && lineY + lineH >= screenH) {//下滑至屏幕底部换图换方向换坐标
            lineImg.setBackgroundResource(R.drawable.ic_icon_light2);
            lineImg.layout(0, screenH, screenW, screenH + lineH);
            bottomImg.setVisibility(View.VISIBLE);
            topImg.setVisibility(View.GONE);
            lineY = screenH;
            toTop = true;
           /* if(FirstOnce) {
                FirstOnce=false;
            }*/
        }
//        ******位移**********
        if (!toTop) {
            lineY += SPEED;
            //只有在这个区域内的才显示
            canvas.clipRect(0,0,screenW,lineY+lineH);
           /* if(FirstOnce) {
                canvas.clipRect(0, 0, screenW, lineY + lineH);
            }*/
        } else {
            lineY -= SPEED;
            canvas.clipRect(0,lineY,screenW,screenH);

        }
        lineImg.layout(0, lineY, screenW, lineY + lineH);
        invalidate();
    }


}

