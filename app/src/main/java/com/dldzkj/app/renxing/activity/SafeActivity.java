package com.dldzkj.app.renxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lyBin on 2015/7/3.
 */
public class SafeActivity extends BaseActivity implements View.OnClickListener{

    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.sl_name)
    TextView slName;
    @InjectView(R.id.sl_content)
    TextView slContent;
    @InjectView(R.id.sl_key_name)
    TextView slKeyName;
    @InjectView(R.id.changePhone)
    RelativeLayout changePhone;
    @InjectView(R.id.changeKey)
    RelativeLayout changeKey;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safe_layout);
        ButterKnife.inject(this);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        String s = data.getAction().toString();
        slContent.setText(s);
    }

    public void init() {
        setTitle(getResources().getString(R.string.info_chengeKey));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        changePhone.setOnClickListener(this);
        changeKey.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.changePhone:
                startActivityForResult(new Intent(this, ChangePhoneActivity.class), 1);
                break;
            case R.id.changeKey:

                break;
        }
    }
}
