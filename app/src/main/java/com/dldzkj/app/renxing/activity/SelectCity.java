package com.dldzkj.app.renxing.activity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.adapter.CityAdapter;
import com.dldzkj.app.renxing.utils.CopyDB;
import com.dldzkj.app.renxing.utils.Location;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.BackingStoreException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/7/3.
 */
public class SelectCity extends BaseActivity {

    private CityAdapter adapter;
    private ListView lv_city;
    private Application mApp;

    // 省
    public String provinceID = "";
    // 市
    public String cityID = "";
    // 区
    public String areaID = "";


    // 地址
    public String address = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcity);
        ButterKnife.inject(this);
        CopyRegon();
        init();
        setListener();
    }

    protected void init() {
        setTitle(getResources().getString(R.string.info_area));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        lv_city = (ListView) findViewById(R.id.lv_city);
        adapter = new CityAdapter(this);
        // 查询省份填充数据
        adapter.removeAll();
        fillProvince();
    }


    private void fillProvince() {
        List<String> pro = Location.getCityList("1");
        adapter.setList(pro);
        lv_city.setAdapter(adapter);

    }



    protected void setListener() {
        lv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private String add;
            private String address;
            private HashMap<String, String> map = new HashMap<String, String>();
            private Intent intent = new Intent();

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                intent.putExtra("data", map);

                address = (String) view.getTag();

                if (provinceID.equals("")) {
                    add = Location.getAddressID((String) view.getTag(), false);
                    provinceID = add;
                    SelectCity.this.address = address + " ";
                    adapter.removeAll();
                    adapter.setList(Location.getCityList(provinceID));
                    map.put("provinceID", provinceID);
                    map.put("address", SelectCity.this.address);
                } else if (cityID.equals("")) {

                    add = Location.getAddressID((String) view.getTag(), true);

                    cityID = add;
                    SelectCity.this.address = SelectCity.this.address + address + " ";
                    adapter.removeAll();
                    map.put("cityID", cityID);
                    map.put("address", SelectCity.this.address);
                    List<String> l = Location.getCityList(cityID);
                    if (l.size() != 0)
                        adapter.setList(l);
                    else {
                        setResult(3, intent);
                        finish();
                    }

                } else if (areaID.equals("")) {

                    add = Location.getAddressID((String) view.getTag(), false);

                    areaID = add;
                    SelectCity.this.address = SelectCity.this.address + address;
                    map.put("areaID", areaID);
                    map.put("address", SelectCity.this.address);
                    setResult(3, intent);
                    finish();
                }

            }
        });
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
    public void CopyRegon() {
        File file = new File(Location.DB_ADDRESS, "city.db");
        if (!file.exists()) {
            CopyDB.copyDB(this, "city.db", file.getAbsolutePath());
            Log.e("CopyRegion", "文件不存在");
        }else{
            Log.e("CopyRegion", "文件已存在");
        }
    }

}
