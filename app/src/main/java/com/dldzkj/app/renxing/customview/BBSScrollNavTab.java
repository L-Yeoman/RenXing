package com.dldzkj.app.renxing.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;

public class BBSScrollNavTab extends LinearLayout {

    private static int COLOR_TEXT_NORMAL;
    private static int COLOR_INDICATOR_COLOR;

    private String[] mTitles;
    private int mTabCount;
    private int mIndicatorColor;
    private float mTranslationX;
    private Paint mPaint = new Paint();
    private int mTabWidth;

    public BBSScrollNavTab(Context context) {
        this(context, null);
    }

    public BBSScrollNavTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        COLOR_TEXT_NORMAL = context.getResources().getColor(R.color.app_gray_font);
        COLOR_INDICATOR_COLOR = context.getResources().getColor(R.color.bbs_bg_send_normal);
        mIndicatorColor = COLOR_INDICATOR_COLOR;
        mPaint.setColor(mIndicatorColor);
        mPaint.setStrokeWidth(7.0F);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTabWidth = w / mTabCount;
    }

    private ViewPager vp;

    public void setTitles(String[] titles, ViewPager vp) {
        this.vp = vp;
        mTitles = titles;
        mTabCount = titles.length;
        generateTitleView();

    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(mTranslationX, getHeight() - 2);
        canvas.drawLine(0, 0, mTabWidth, 0, mPaint);
        canvas.restore();
    }

    public void scroll(int position, float offset) {
        /**
         * <pre>
         *  0-1:position=0 ;1-0:postion=0;
         * </pre>
         */
        mTranslationX = getWidth() / mTabCount * (position + offset);
        for (int i = 0; i < mTitles.length; i++) {
            TextView tv = (TextView) getChildAt(i);
            if (i == position) {
                tv.setTextColor(COLOR_INDICATOR_COLOR);
            } else {
                tv.setTextColor(COLOR_TEXT_NORMAL);
            }

        }


        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private void generateTitleView() {
        if (getChildCount() > 0)
            this.removeAllViews();
        int count = mTitles.length;

        setWeightSum(count);
        for (int i = 0; i < count; i++) {
            TextView tv = new TextView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    LayoutParams.MATCH_PARENT);
            lp.weight = 1;
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(COLOR_TEXT_NORMAL);
            tv.setText(mTitles[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setLayoutParams(lp);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;
                    if (tv.getText().toString().equals(mTitles[0])) {
                        vp.setCurrentItem(0);
                    } else if (tv.getText().toString().equals(mTitles[1])) {
                        vp.setCurrentItem(1);
                    } else vp.setCurrentItem(2);
                }
            });
            addView(tv);
        }
    }


}
