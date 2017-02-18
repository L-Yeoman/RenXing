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

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.blelib.utils.XMLParser;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.mainfragment.BBSFragment;
import com.dldzkj.app.renxing.mainfragment.FragmentNewBLE;
import com.dldzkj.app.renxing.mainfragment.MainArticleFragment;
import com.dldzkj.app.renxing.service.UpdateSevice;
import com.dldzkj.app.renxing.utils.AppDbUtils;
import com.dldzkj.app.renxing.utils.BmpUtils;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.ScreenUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;

/**
 * TODO
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    /**
     * ********
     * 蓝牙相关参数
     * *********
     */
    public static String GOD_1 = "00000524-0000-1000-8000-00805f9b34fb";
    public static String CHARACT_NOTIFY = "0000FFE2-0000-1000-8000-00805f9b34fb";
    public static String SERVICE = "0000FFE5-0000-1000-8000-00805f9b34fb";
    public static String CHARACT_WRITE = "0000FFE9-0000-1000-8000-00805f9b34fb";
    public static BluetoothLeDevice targetdevice;
    /**
     * **********
     * 蓝牙3.0相关参数
     * ****************
     */
    // Debugging
    private final String BLUETOOTH_DEVICE_ADRESS = "00:00:00:06:78:AB";
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST_FAIL = 5;
    public static final int MESSAGE_TOAST_DISCONNECT = 7;
    public static final int MESSAGE_OK = 6;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x10;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


    @InjectView(R.id.btn_main_home)
    LinearLayout btnMainHome;
    @InjectView(R.id.nonImg)
    ImageView nonImg;
    @InjectView(R.id.btn_main_blue)
    ImageView btnMainBlue;
    @InjectView(R.id.btn_main_bbs)
    LinearLayout btnMainBbs;

    @InjectView(R.id.viewpager)
    ViewPager viewPager;
    @InjectView(R.id.tabs)
    TabLayout tabLayout;
  /*  @InjectView(R.id.main_content)
    CoordinatorLayout mainContent;*/


    /*
    暂时注释掉main.xml里面的drawlayout，自定义实现*/
  /*  @InjectView(R.id.nav_view)
    NavigationView navigationView;*/
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.ilv_text_one)
    TextView mTextOne;
    @InjectView(R.id.ilv_text_three)
    TextView mTextThree;
    private User u;
    public MainActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScreenUtils.addTranslaterView(this, "isFirstMain", R.layout.teach_main_home, R.id.teach_btn_main_home);
        ButterKnife.inject(this);
        initToolBar();
        MyApplication.getInstance().initU13ImgLoader();
        initDates();
        initEvents();
//        updateSelf();//检查版本更新
    }

    public MainActivity getInstance() {
        return this;
    }

    private void initToolBar() {
        setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_bbs_icons);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoginUserData();
    }

    public void getLoginUserData() {
        AppDbUtils util = new AppDbUtils();
        u = util.getLoginUser(SPUtils.getLoginId(this));
        if (u == null) return;
        if (u.getPortrait() != null && !u.getPortrait().isEmpty()) {
            ImageLoader.getInstance().displayImage(WebUtils.RENXING_WEB + u.getPortrait(), nonImg, MyApplication.img_option, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(bitmap, BmpUtils.dip2px(getBaseContext(), 36), BmpUtils.dip2px(getBaseContext(), 36));
                    toolbar.setNavigationIcon(new CircleImageDrawable(resizeBmp, false, 0));
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });

        }
    }

    private void initDates() {
       /* if (navigationView != null) {
            setupDrawerContent(navigationView);
        }*/
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(2);
            setupViewPager(viewPager);
        }
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initEvents() {
        btnMainHome.setOnClickListener(clickListener);
        btnMainBlue.setOnClickListener(clickListener);
        btnMainBbs.setOnClickListener(clickListener);
        initTabLayoutEvents();
        /********menu逻辑事件处理********/
        initDrawLayoutEvents();
    }


    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(btnMainHome)) {
                mTextOne.setTextColor(getResources().getColor(R.color.send_text_color));
                mTextThree.setTextColor(getResources().getColor(R.color.White));
                viewPager.setCurrentItem(0);
            } else if (v.equals(btnMainBlue)) {
                mTextThree.setTextColor(getResources().getColor(R.color.White));
                mTextOne.setTextColor(getResources().getColor(R.color.White));
                viewPager.setCurrentItem(1);
            } else if (v.equals(btnMainBbs)) {
                mTextOne.setTextColor(getResources().getColor(R.color.White));
                mTextThree.setTextColor(getResources().getColor(R.color.send_text_color));
                viewPager.setCurrentItem(2);
            } else return;
        }
    };

    public void OpenLeftMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.LEFT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
                OpenLeftMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        adapter.addFragment(new MainArticleFragment(), "Home");
        adapter.addFragment(new FragmentNewBLE(), "Ble");
//        adapter.addFragment(new FragmentBle(), "Ble");
        adapter.addFragment(new BBSFragment(), "BBS");
        viewPager.setAdapter(adapter);
        btnMainHome.setSelected(true);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.onelayout:
                Toast.makeText(this, " hahahah", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    private void initDrawLayoutEvents() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
//                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT")) {
                   /* 暂时注释掉尺寸偏移
                    float leftScale = 1 - 0.3f * scale;
                    mMenu.setScaleX(leftScale);
                    mMenu.setScaleY(leftScale);*/
                    mMenu.setAlpha(0.6f + 0.4f * (1 - scale));
                    mContent.setTranslationX(mMenu.getMeasuredWidth() * (1 - scale));
                   /* mContent.setPivotX(0);
                    mContent.setPivotY(mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    mContent.setScaleX(rightScale);
                    mContent.setScaleY(rightScale);*/
                } else {
                    mContent.setTranslationX(-mMenu.getMeasuredWidth() * slideOffset);
                   /* mContent.setPivotX(mContent.getMeasuredWidth());
                    mContent.setPivotY(mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    mContent.setScaleX(rightScale);
                    mContent.setScaleY(rightScale);*/
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }

    private void initTabLayoutEvents() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        mTextOne.setTextColor(getResources().getColor(R.color.send_text_color));
                        mTextThree.setTextColor(getResources().getColor(R.color.White));
                        btnMainHome.setSelected(true);
                        break;
                    case 1:
                        mTextOne.setTextColor(getResources().getColor(R.color.White));
                        mTextThree.setTextColor(getResources().getColor(R.color.White));
                        btnMainBlue.setSelected(true);
                        break;
                    case 2:
                        mTextOne.setTextColor(getResources().getColor(R.color.White));
                        mTextThree.setTextColor(getResources().getColor(R.color.send_text_color));
                        btnMainBbs.setSelected(true);
                        ScreenUtils.addTranslaterView(MainActivity.this, "isFirstMainBBS", R.layout.teach_main_bbs, R.id.teach_btn_main_home);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        btnMainHome.setSelected(false);
                        break;
                    case 1:
                        btnMainBlue.setSelected(false);
                        break;
                    case 2:
                        btnMainBbs.setSelected(false);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    //检查软件新版本

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    /**
     * **********
     * 版本更新服务
     * ************
     */
    private String androidUrl;

    private void buidlDialog() {
        final AppDialog dialog = new AppDialog(this, R.style.dialog, AppDialog.ASK_TYPE);
        dialog.show();
        dialog.setTitle("检测到新版本，是否更新");

        dialog.setSureClickListner(new AppDialog.dialogListenner() {
            @Override
            public void setOnSureLis(Dialog d, View v) {
                MyApplication.SERVER_CONFIG_LOADPATH = androidUrl;
                Intent intent = new Intent(MainActivity.this,
                        UpdateSevice.class);
                intent.putExtra("Key_App_Name", "AoGu");
                intent.putExtra("Key_Down_Url", androidUrl);
                startService(intent);
                dialog.dismiss();
            }
        });

    }

    private void updateSelf() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                XMLParser parser = new XMLParser();
                String xml = parser.getXmlFromUrl(MyApplication.UPDATE_VERSION_URL);
                if (xml == null) {
                    return;
                }
                HashMap<String, String> map = (HashMap<String, String>) parser
                        .ParseUpdateInfo(xml);
                String serverCode = map.get("androidversion");
                androidUrl = map.get("androidurl");
                String androidcontent = map.get("androidcontent");
                int versionCode = 0;
                try {
                    versionCode = XMLParser.getVersionCode(MainActivity.this);
                    if (Integer.parseInt(serverCode) > versionCode) {
                        mHandler.sendEmptyMessage(0);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    buidlDialog();
                    break;
            }
        }
    };

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
