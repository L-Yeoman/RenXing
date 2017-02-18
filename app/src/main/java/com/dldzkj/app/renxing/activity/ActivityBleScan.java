package com.dldzkj.app.renxing.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.mainfragment.FragmentBle;

/**
 * Created by Administrator on 2015/8/8.
 */
public class ActivityBleScan extends BaseActivity {
    private FragmentBle frag;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ble);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        frag=new FragmentBle();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0x89){
            if(resultCode==RESULT_OK){
                frag.clickToStart();
            }else{
                finish();
            }
        }
    }
}
