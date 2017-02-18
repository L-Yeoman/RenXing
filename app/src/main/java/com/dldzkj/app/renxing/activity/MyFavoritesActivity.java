package com.dldzkj.app.renxing.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.adapter.ArticleStoreAdapter;
import com.dldzkj.app.renxing.customview.PostStore;
import com.dldzkj.app.renxing.mainfragment.FavoriteArticleFragment;
import com.dldzkj.app.renxing.mainfragment.FavoritePostFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 我的收藏
 *
 * @author Administrator
 */
public class MyFavoritesActivity extends BaseActivity implements OnPageChangeListener,PostStore.IsSelect,ArticleStoreAdapter.OnSelect {
    @InjectView(R.id.tabLayout)
    TabLayout tabLayout;
    @InjectView(R.id.asl_all_button)
    TextView allButton;
    @InjectView(R.id.asl_delete_button)
    TextView delButton;
    @InjectView(R.id.asl_edit_layout)
    LinearLayout delView;
    private TextView mArticleText;
    private TextView mPostText;
    FavoriteArticleFragment fragment1;
    FavoritePostFragment fragment2;
    private ViewPager mPager;
    private List<Fragment> mList;
    private StoreAdapter mAdapter;
    private FragmentManager mManager;
    private Fragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_stroe_layout);
        ButterKnife.inject(this);
        inits();
        setdata();
    }

    public void inits() {
        setTitle("我的收藏");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mArticleText = (TextView) findViewById(R.id.mla_article_text);
        mPostText = (TextView) findViewById(R.id.mla_post_text);
        mPager = (ViewPager) findViewById(R.id.mla_viewpager);
        fragment2 = new FavoritePostFragment();
        fragment1 = new FavoriteArticleFragment();
        currentFragment=fragment1;
        mArticleText.setOnClickListener(new OnClick());
        mPostText.setOnClickListener(new OnClick());
        delButton.setOnClickListener(new OnClick());
        allButton.setOnClickListener(new OnClick());
        mArticleText.setTextColor(getResources().getColor(R.color.store_tab_select));

    }


    private void setdata() {
        mList = new ArrayList<Fragment>();
        mList.add(fragment1);
        mList.add(fragment2);
        mManager = getSupportFragmentManager();
        mAdapter = new StoreAdapter(mManager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);
        mPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(mPager);
    }

    @Override
    public void isSelect(Boolean bool) {
        if(bool){
            delButton.setTextColor(getResources().getColor(R.color.bbs_bg_send_normal));
        }else{
            delButton.setTextColor(getResources().getColor(R.color.app_content_font));
        }
    }

    @Override
    public void onSelect(boolean bool) {
        if(bool){
            delButton.setTextColor(getResources().getColor(R.color.bbs_bg_send_normal));
        }else{
            delButton.setTextColor(getResources().getColor(R.color.app_content_font));
        }
    }

    public class StoreAdapter extends FragmentPagerAdapter {
        public StoreAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return mList.get(arg0);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }

    private void titleChange(int i) {
        cancelDelete();
        if (i == 0) {
            currentFragment = fragment1;
            mArticleText.setTextColor(getResources().getColor(R.color.store_tab_select));
            mPostText.setTextColor(getResources().getColor(R.color.store_tab));
        } else if (i == 1) {
            currentFragment = fragment2;
            mArticleText.setTextColor(getResources().getColor(R.color.store_tab));
            mPostText.setTextColor(getResources().getColor(R.color.store_tab_select));
        }
    }
    boolean isDelete = false;
    private class OnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.mla_article_text:
                    mPager.setCurrentItem(0);
                    break;
                case R.id.mla_post_text:
                    mPager.setCurrentItem(1);
                    break;
                case R.id.asl_delete_button:
                    delView.setVisibility(View.GONE);
                    delButton.setTextColor(getResources().getColor(R.color.app_content_font));
                    currentMenu = MenuEdit;
                    invalidateOptionsMenu();
                    if(currentFragment==fragment1){
                        fragment1.handler.sendEmptyMessage(3);
                    }else if(currentFragment==fragment2){
                        fragment2.handler.sendEmptyMessage(4);
                    }
                    break;
                case R.id.asl_all_button:
                    if(!isDelete){
                        isDelete = true;
                        allButton.setTextColor(getResources().getColor(R.color.bbs_bg_send_normal));
                    }else{
                        isDelete = false;
                        allButton.setTextColor(getResources().getColor(R.color.app_content_font));
                    }
                    if(currentFragment==fragment1){
                        fragment1.allSelected();
                    }else if(currentFragment==fragment2){
                        fragment2.allSelected();
                    }
                    break;

		/*	case R.id.rigte_text:
				if("编辑".equals(mEditor.getText())){
					mEditor.setText("删除");
					if(currentFragment==fragment2){
						 fragment2.showChecked(true);
						 break;
					}
					fragment1.mAdapter.setCheck(true);
				}else{
					mEditor.setText("编辑");
					if(currentFragment==fragment2){
						fragment2.showChecked(false);
						fragment2.deleteItems();
						break;
					}
					fragment1.deleteItem(fragment1.mAdapter.getDeleteItems());
					fragment1.mAdapter.setCheck(false);
					fragment1.mAdapter.clearDeleteItems();
				}
				fragment1.mAdapter.notifyDataSetChanged();
				break;*/

                default:
                    break;

            }
        }


    }

    private final int MenuEdit = R.menu.photo_menu;
    private final int MenuCancel = R.menu.photo_cancel;
    private int currentMenu = MenuEdit;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(currentMenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.pm_edit:
                edit();
                break;
            case R.id.pm_cancel:
                cancelDelete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void edit() {
	/*	deleteView.setVisibility(View.VISIBLE);
		mAdapter.notifyDataSetChanged();*/
        delView.setVisibility(View.VISIBLE);
        currentMenu = MenuCancel;
        if(currentFragment==fragment1){
            fragment1.editItem();
        }else if(currentFragment==fragment2){
            fragment2.editItem();
        }
        invalidateOptionsMenu();
    }

    //取消删除
    public void cancelDelete() {
        delView.setVisibility(View.GONE);
        isDelete = false;
        allButton.setTextColor(getResources().getColor(R.color.app_content_font));
        delButton.setTextColor(getResources().getColor(R.color.app_content_font));
        currentMenu = MenuEdit;
        if(currentFragment==fragment1){
            fragment1.cancelDelete();
        }else if(currentFragment==fragment2){
            fragment2.cancelDelete();
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        titleChange(arg0);
    }

}
