package com.dldzkj.app.renxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.customview.DateSelectorWheelView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lyBin on 2015/7/3.
 */
public class BirthdayActivity extends BaseActivity {
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.bl_date)
    DateSelectorWheelView blDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday_layout);
        ButterKnife.inject(this);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.in_save:
                setResult(2, new Intent(blDate.getDate()));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_nick, menu);
        return true;
    }

    public void init() {
        setTitle(getResources().getString(R.string.info_birthday));
        toolbar.setNavigationIcon(R.drawable.ic_back);
    }
}
