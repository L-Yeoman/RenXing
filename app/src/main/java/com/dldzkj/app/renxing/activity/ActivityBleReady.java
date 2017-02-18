package com.dldzkj.app.renxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.EnterActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.mainfragment.FragmentBle;
import com.dldzkj.app.renxing.mainfragment.FragmentNewBLE;

/**
 * Created by Administrator on 2015/8/8.
 */
public class ActivityBleReady extends BaseActivity{
    private FragmentNewBLE frag;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ble);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        frag=new FragmentNewBLE();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.knock_layout_linear,frag)
                .commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
