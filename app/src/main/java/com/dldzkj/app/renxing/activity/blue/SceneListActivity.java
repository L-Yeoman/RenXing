package com.dldzkj.app.renxing.activity.blue;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.MainActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.blelib.services.BluetoothLeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.Inflater;

import uk.co.alt236.bluetoothlelib.util.ByteUtils;


public class SceneListActivity extends BaseActivity {
    private ListView list;
    List<Map<String, Object>> data;
    private int[] resId = new int[]{R.drawable.ic_scene001, R.drawable.ic_scene002, R.drawable.ic_scene003, R.drawable.ic_scene004, R.drawable.ic_scene005, R.drawable.ic_scene006};
    private String[] titleArr = new String[]{"海边", "森林", "流云", "宇宙", "爱情酒店", "停车zuo爱"};
    private String[] subtitleArr = new String[]{"马尔代夫·天堂岛", "加拿大·圣劳伦斯", "冰岛·华纳达尔", "仙女座·星云", "土耳其·特洛伊", "北京 北清路"};
    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            list.removeHeaderView(header);
        }
    };
    private TextView header;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_list);
        initView();
    }

    private void initView() {
        setTitle("沉浸模式");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        header = (TextView) getLayoutInflater().inflate(R.layout.header_scene_list, null);

        list = (ListView) findViewById(R.id.list);
        data = new ArrayList<>();
        for (int i = 0; i < resId.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", resId[i]);
            map.put("title", titleArr[i]);
            map.put("info", subtitleArr[i]);
            data.add(map);
        }
        list.addHeaderView(header);
        MyAdapter adapter = new MyAdapter(this);
        list.setAdapter(adapter);
        h.sendEmptyMessageDelayed(0, 3000);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getBaseContext(), HappyClickActivity.class).putExtra("pos", position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView info;
    }


    class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.activity_scene_item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.bg);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.info = (TextView) convertView.findViewById(R.id.subtitle);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.img.setImageResource((Integer) data.get(position).get("img"));
            holder.title.setText((String) data.get(position).get("title"));
            holder.info.setText((String) data.get(position).get("info"));
            return convertView;
        }

    }
}
