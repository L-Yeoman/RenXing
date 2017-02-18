package com.dldzkj.app.renxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lyBin on 2015/7/2.
 */
public class SignActivity extends BaseActivity {
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.sl_text)
    EditText slText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_layout);
        ButterKnife.inject(this);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.in_save:
                String s = slText.getText().toString();
                if(s.equals("")){
                    Toast.makeText(getApplicationContext(), "请填写签名", Toast.LENGTH_SHORT).show();
                    break;
                }
                setResult(1,new Intent(s));
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_nick,menu);
        return true;
    }

    public void init() {
        setTitle(getResources().getString(R.string.info_sign));
        toolbar.setNavigationIcon(R.drawable.ic_back);
    }
}
