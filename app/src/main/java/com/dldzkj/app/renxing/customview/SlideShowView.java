package com.dldzkj.app.renxing.customview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.HomeImg;
import com.dldzkj.app.renxing.bean.SlideViewModel;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * ViewPager
 */

public class SlideShowView extends FrameLayout {
    ArrayList<SlideViewModel> imgsList;
    private final static int IMAGE_COUNT = 5;
    private final static boolean isAutoPlay = true;


    private String[] imageUrls;
    private List<ImageView> imageViewsList;
    private List<View> dotViewsList;

    private ViewPager viewPager;
    private int currentItem = 0;
    private ScheduledExecutorService scheduledExecutorService;
    private Context context;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }

    };

    public SlideShowView(Context context) {
        this(context, null);
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setLocalData(new String[]{"assets/top11.jpg", "assets/top22.jpg"});
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initData();
        if (isAutoPlay) {
            startPlay();
        }

    }

    private void startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4,
                TimeUnit.SECONDS);
    }

    private void stopPlay() {
        scheduledExecutorService.shutdown();
    }

    private void initData() {
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();

    }

    private void initUI(Context context) {
        if (imageUrls == null || imageUrls.length == 0)
            return;
        LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
                true);
        LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();
        dotViewsList.clear();
        imageViewsList.clear();

        for (int i = 0; i < imageUrls.length; i++) {
            ImageView view = new ImageView(context);
            view.setTag(imageUrls[i]);
            view.setScaleType(ScaleType.FIT_XY);
            imageViewsList.add(view);
            ImageView dotView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 4;
            params.rightMargin = 4;
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);

        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            if (null != imageViewsList) {
                if (imageViewsList.size() > position) {
                    if (null != imageViewsList.get(position))
                        ((ViewPager) container).removeView(imageViewsList
                                .get(position));
                }
            }
        }

        @Override
        public Object instantiateItem(View container, final int position) {
            ImageView imageView = imageViewsList.get(position);
            if (imgsList != null && imgsList.size() > 0 && lis != null) {
                imageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        lis.OnUrlItemClick(position, arg0);
                    }
                });
            }
            String path = imageView.getTag() + "";
            ImageLoader.getInstance().displayImage(imageView.getTag() + "", imageView, MyApplication.img_option);
            ((ViewPager) container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);

        }

        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }

    }


    private class MyPageChangeListener implements OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (viewPager.getCurrentItem() == viewPager.getAdapter()
                            .getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }

                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager
                                .setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {

            currentItem = pos;
            for (int i = 0; i < dotViewsList.size(); i++) {
                if (i == pos) {
                    ((View) dotViewsList.get(pos))
                            .setBackgroundResource(R.drawable.dot_focus);
                } else {
                    ((View) dotViewsList.get(i))
                            .setBackgroundResource(R.drawable.dot_blur);
                }
            }
        }

    }


    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    }


    private void destoryBitmaps() {
        for (int i = 0; i < IMAGE_COUNT; i++) {
            ImageView imageView = imageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                drawable.setCallback(null);
            }
        }
    }

    private UrlItemClickLis lis;

    public interface UrlItemClickLis {
        void OnUrlItemClick(int p, View v);
    }

    public void setOnItemclickLis(UrlItemClickLis lis) {
        this.lis = lis;
    }

    private void getDate() {
        imageUrls = new String[imgsList.size()];
        for (int i = 0; i < imgsList.size(); i++) {
            imageUrls[i] = WebUtils.RENXING_WEB + imgsList.get(i).getImg_Url();
        }

    }

    public void setNetData(ArrayList<SlideViewModel> data) {
        imgsList = data;
        getDate();
        initUI(context);
    }

    public void setLocalData(String[] data) {
        imageUrls = data;
    }

}