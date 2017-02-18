package com.dldzkj.app.renxing.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.customview.DividerItemDecoration;
import com.dldzkj.app.renxing.customview.PersanolInfoItem;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Lybin on 2015/6/30.
 */
public class PersonalInfo extends BaseActivity {
    @InjectView(R.id.pil_recyclerView)
    RecyclerView pilRecyclerView;
    @InjectView(R.id.pil_cancel)
    TextView cancelBut;
    private MyAdapter mAdapter;
    private final int NickActivityresult=0;
    private final int SignActivityresult=1;
    private final int BirthdayActivityresult=2;
    private final int AreaActivityresult=3;
    private List mList;
    private DbUtils mUtils;
    private HashMap<String,Object> mHashMap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.persanol_info_layout);
        ButterKnife.inject(this);
        init();
        loadData();
    }
    public void setData(User user){
        mList = new ArrayList();
        mList.add(user.getNicName());
        mList.add(SPUtils.getLoginName(this));
        mList.add(user.getGroupID());
        mList.add(user.getMySex());
        mList.add(user.getBirthday());
        String s = user.getMyArea();
        if(s.contains("null")){
            s="";
        }
        mList.add(s);
        mList.add(user.getSignature());
        mList.add("修改密码");
        mAdapter.notifyDataSetChanged();
    }

    public void init() {
        setTitle(getResources().getString(R.string.info_title));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        pilRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pilRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.color.text_gray, DividerItemDecoration.VERTICAL_LIST, 3));
        mAdapter = new MyAdapter();
        pilRecyclerView.setAdapter(mAdapter);
        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SPUtils.contains(getBaseContext(),SPUtils.LOGIN_Name)){
                    SPUtils.remove(getBaseContext(),SPUtils.LOGIN_Name);
                }
                Intent _Intent = new Intent(PersonalInfo.this,LoginActivity.class);
                _Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(_Intent);
                finish();
            }
        });
    }
    //下载个人信息
    public void loadData(){
             mUtils=MyApplication.getInstance().getDbUtils();
            HashMap<String,Object> _Map = new HashMap<String,Object>();
            _Map.put("UserName",  SPUtils.getLoginName(this));
            WebUtils.sendPost(PersonalInfo.this, ConstantValue.UserDatum, _Map, new WebListener());
    }

    public void callService(HashMap<String,Object> hashMap,String method){
        WebUtils.sendPost(PersonalInfo.this, method, hashMap, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    test("修改成功");
                }
            }
            @Override
            public void onFailed(HttpException error, String msg) {
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch(item.getItemId()){
           case android.R.id.home:
               finish();
               break;
       }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        String s;
        User user = null;
        HashMap<String,Object> _Map=new HashMap<String,Object>();
        try {
            user = mUtils.findById(User.class, SPUtils.getLoginId(this));
        } catch (DbException e) {
            e.printStackTrace();
        }
        switch (resultCode){
            case NickActivityresult:
                s = data.getAction().toString();
                    user.setNicName(s);
                mAdapter.currentView.mInfoItem.setContent(s);
                _Map.put("UserName", SPUtils.getLoginName(this));
                _Map.put("NicName",s);
                callService(_Map,ConstantValue.UpdateNicName);
                break;
            case SignActivityresult:
                s = data.getAction().toString();
                user.setSignature(s);
                mAdapter.currentView.mInfoItem.setContent(s);
                _Map.put("UserName",  SPUtils.getLoginName(this));
                _Map.put("Signature",s);
                callService(_Map,ConstantValue.UpdateSignature);
                break;
            case BirthdayActivityresult:
                String ss=data.getAction().toString();
                user.setBirthday(ss);
                mAdapter.currentView.mInfoItem.setContent(ss);
                _Map.put("UserName",  SPUtils.getLoginName(this));
                _Map.put("Birthday",ss);
                callService(_Map, ConstantValue.UpdateBirthday);
                break;
            case AreaActivityresult:
               _Map = getArea(data,user);
                callService(_Map,ConstantValue.UpdateArea);
                break;
        }
        try {
            mUtils.saveOrUpdate(user);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public HashMap<String,Object> getArea(Intent intent,User user)
    {
        HashMap<String,Object>_Map= new HashMap<String,Object>();
        HashMap<String,Object> map= (HashMap<String, Object>) intent.getSerializableExtra("data");
        String province = (String) map.get("address");
        mAdapter.currentView.mInfoItem.setContent(province);
        String provinceID = (String) map.get("provinceID");
        String cityID = (String) map.get("cityID");
        String areaID = (String) map.get("areaID");
        user.setProvince_ID(provinceID);
        user.setCity_ID(cityID);
        user.setArea_ID(areaID);
        _Map.put("UserName", SPUtils.getLoginName(this));
        _Map.put("Province_ID",provinceID);
        _Map.put("City_ID",cityID);
        _Map.put("Area_ID",areaID);
        return _Map;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        public MyViewHolder currentView;
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            return new MyViewHolder(new PersanolInfoItem(PersonalInfo.this, position));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            String s = "";
            if(mList!=null&&position<mList.size()){
                s =mList.get(position)+"";
            }
            holder.mInfoItem.bindData(s);
            holder.mInfoItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentView=holder;
                    switch(position) {
                        case 0:
                            startActivityForResult(new Intent(PersonalInfo.this, NickActivity.class), 0);
                            break;
                        case 4:
                            startActivityForResult(new Intent(PersonalInfo.this,BirthdayActivity.class),0);
                            break;
                        case 5:
                            startActivityForResult(new Intent(PersonalInfo.this,SelectCity.class),0);
                            break;
                        case 6:
                            startActivityForResult(new Intent(PersonalInfo.this,SignActivity.class),0);
                            break;
                        case 7:
                            Intent _Intent = new Intent(PersonalInfo.this,ModifyPwdActivity.class);
                            _Intent.setAction("changeKey");
                            startActivity(_Intent);
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return 8;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PersanolInfoItem mInfoItem;

            public MyViewHolder(View itemView) {
                super(itemView);
                mInfoItem = (PersanolInfoItem) itemView;
            }
        }
    }
    public void test(String s){
        Toast.makeText(PersonalInfo.this, s,Toast.LENGTH_SHORT).show();
    }

    public class WebListener implements WebUtils.OnWebListenr{
        @Override
        public void onSuccess(boolean isSuccess, String msg, String data) {
            User user = JSON.parseObject(JSON.parseArray(data).get(0).toString(), User.class);
            //表中Id名不同，重新赋值id
//            user.setID(ConstantValue.Id);
            Log.i("onSuccess", data);
            setData(user);
            try {
                //貌似更新多条数据会默认覆盖
                mUtils.update(user,"Birthday","Sex","GroupID","Province_ID","City_ID","Area_ID");
           //    mUtils.saveOrUpdate(user);
            } catch (DbException e) {
                e.printStackTrace();
            }
            Log.i("onSuccess", data);
        }
        @Override
        public void onFailed(HttpException error, String msg) {
            try {
               User _User = mUtils.findById(User.class,SPUtils.getLoginId(PersonalInfo.this));
                setData(_User);
            } catch (DbException e) {
                e.printStackTrace();
            }

        }
    }
}
