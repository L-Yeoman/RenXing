package com.dldzkj.app.renxing.bbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.BBSTypeModel;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.utils.ScreenUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import github.chenupt.dragtoplayout.DragTopLayout;


/*********
 * 每个大类界面，包含新帖，热帖，精华帖等等
 *******/
public class BBSEveryTypeActivity extends BaseOtherActivity {

    @InjectView(R.id.id_stickynavlayout_topview)
    RelativeLayout idStickynavlayoutTopview;
    @InjectView(R.id.drag_layout)
    DragTopLayout dragLayout;
    @InjectView(R.id.id_stickynavlayout_viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.bbs_describe)
    TextView bbsDescribe;
    @InjectView(R.id.bbs_name)
    TextView bbsName;
    @InjectView(R.id.header_img)
    ImageView background;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.send)
    ImageView send;
    @InjectView(R.id.tabs)
    TabLayout tabLayout;
    private BBSTypeModel broad;
    private String[] mTitles = new String[]{"全部", "精华", "新帖"};
    private FragmentPagerAdapter mAdapter;
    private Fragment[] mFragments = new TypeEveryTabFragment[mTitles.length];
    private boolean toSendBBS = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_activity_everytypelist);
        ScreenUtils.addTranslaterView(this, "isFirstPlant", R.layout.teach_plant_bbs, R.id.teach_btn_main_home);
        ButterKnife.inject(this);
        broad = getIntent().getParcelableExtra("broad");
        Log.d("zxl", "当前板块id:" + broad.getBoardID());
        initToolBar();
        //获取板块详细信息；
        initDatas();
        initEvents();
    }

    private void initToolBar() {
        bbsName.setText(broad.getBoardName());
        bbsDescribe.setText(broad.getDescription());
        //背景图
        if (broad.getBackImage() != null && !broad.getBackImage().isEmpty()) {
            Glide.with(getBaseContext()).load(WebUtils.RENXING_WEB_PICS + broad.getBackImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_picture_loadfailed)
                    .into(background);
//            ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB+broad.getBackImage(),background, MyApplication.img_option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(toSendBBS){
            mViewPager.setCurrentItem(2);
            toSendBBS=false;
        }
    }

    private void initDatas() {
        for (int i = 0; i < mTitles.length; i++) {
            mFragments[i] = TypeEveryTabFragment.newInstance(i, Integer.parseInt(broad.getBoardID()));
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
        };
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void initEvents() {

        dragLayout.setOverDrag(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSendBBS=true;
                startActivity(new Intent(BBSEveryTypeActivity.this, SendBBSActivity.class).putExtra("boardId", Integer.parseInt(broad.getBoardID())).putExtra("type", false));
            }
        });
    }


    private void WebRequst(int BoardID) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("BoardID", BoardID);
        WebUtils.sendPost(this, ConstantValue.GetPostsBoardDetail, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }
}
