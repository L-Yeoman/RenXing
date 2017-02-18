package com.dldzkj.app.renxing.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.adapter.ScoreAdapter;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.ScoreModel;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.utils.AppDbUtils;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 我的积分
 *
 * @author Administrator
 */
public class PersonScoreActivity extends BaseActivity {

    @InjectView(R.id.img)
    CircleImageView img;
    @InjectView(R.id.score)
    TextView score;
    @InjectView(R.id.list)
    RecyclerView list;
    @InjectView(R.id.header)
    RecyclerViewHeader header;
    private ArrayList<ScoreModel> datas;
    ScoreAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jifen_detail);
        ButterKnife.inject(this);
        initToolBar();
        getLoginUserData();
        initData();
    }

    private void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setTitle("我的积分");
        score.setText("积分：" + getIntent().getStringExtra("scoreValue"));
    }
    private void getLoginUserData() {
        AppDbUtils util = new AppDbUtils();
         User  u = util.getLoginUser(SPUtils.getLoginId(this));
        if(u==null)return;
        if (u.getPortrait() != null && !u.getPortrait().isEmpty()) {
            Glide.with(getBaseContext()).load(WebUtils.RENXING_WEB+u.getPortrait()).into(img);
        }
    }
    private void initData() {
        datas = new ArrayList<ScoreModel>();
        adapter = new ScoreAdapter(this, datas);
        list.setLayoutManager(new LinearLayoutManager(this));
        header.attachTo(list,true);
        list.setAdapter(adapter);
        //请求网络
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("UserID", SPUtils.getLoginId(this));
        WebUtils.sendPost(this, ConstantValue.GetPointHistory, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess){
                    List<ScoreModel> result=JSON.parseArray(data,ScoreModel.class);
                    //倒序排列依次
                    for (int i = result.size()-1; i >=0 ; i--) {
                        datas.add(result.get(i));
                    }
//                    datas.addAll(result);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(PersonScoreActivity.this,msg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.score_rule_action, menu);
//        return super.onCreateOptionsMenu(menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_score_rule:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
