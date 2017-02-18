/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dldzkj.app.renxing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * TODO
 */
public class SplashActivity extends Activity implements ViewPager.OnPageChangeListener {
    private ViewPager vp;
    private ImageView[] tips;
    private ImageView[] mImageViews;
    private int[] imgIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = this.getSharedPreferences("QingTuan", 0);
        SharedPreferences.Editor localEditor = settings.edit();
        boolean isFirst = settings.getBoolean("FirstGuidOpen", true);
        if (isFirst) {
            setContentView(R.layout.splush_activity);
            localEditor.putBoolean("FirstGuidOpen", false);
            localEditor.commit();
            initView();
        } else {
            startActivity(new Intent(this, EnterActivity.class));
            finish();
        }


    }

    private void initView() {
        vp = (ViewPager) findViewById(R.id.viewPager);
        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
        //载入图片资源ID
        imgIdArray = new int[]{R.drawable.w1, R.drawable.w2, R.drawable.w3};
        tips = new ImageView[imgIdArray.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.dot_focus);
            } else {
                tips[i].setBackgroundResource(R.drawable.dot_blur);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 15;
            layoutParams.rightMargin = 15;
            layoutParams.bottomMargin= 20;
            group.addView(imageView, layoutParams);
        }
        //将图片装载到数组中
        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(this);
            mImageViews[i] = imageView;
            imageView.setBackgroundResource(imgIdArray[i]);
        }
        mImageViews[mImageViews.length - 1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, EnterActivity.class));
                finish();
            }
        });

        //设置Adapter
        vp.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        vp.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        vp.setCurrentItem(0);

    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
//            ((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            try {
                ((ViewPager) container).addView(mImageViews[position], 0);
            } catch (Exception e) {
                //handler something
            }
            return mImageViews[position];
        }


    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        setImageBackground(arg0);
    }


    /**
     * 设置选中的tip的背景
     *
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                   tips[i].setBackgroundResource(R.drawable.dot_focus);
            } else {
                tips[i].setBackgroundResource(R.drawable.dot_blur);
            }
        }
    }

}