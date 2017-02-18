package com.dldzkj.app.renxing.customview;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bbs.BBSDetailsActivity;
import com.dldzkj.app.renxing.bean.BBSModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/6.
 */
public class PostStore extends LinearLayout {

    TextView title, fromText, userText, timeText, commentCount;

    ListView postList;
    private CheckBox mCheck;
    private Context mContext;
    private List<BBSModel> deleteList;
    private View mView;
    private IsSelect isSelect;

    public PostStore(Context context) {
        this(context, null);
    }

    public PostStore(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public PostStore(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }
    public void setSelect(IsSelect select){
        isSelect = select;
    }
    public interface IsSelect{
        public void isSelect(Boolean bool);
    };

    public void init() {
         mView = LayoutInflater.from(mContext).inflate(R.layout.post_store_item, null);
        title = (TextView) mView.findViewById(R.id.postTitle);
        //     userText = (TextView)v.findViewById(R.id.userText);
        //     timeText = (TextView)v.findViewById(R.id.sendTime);
        fromText = (TextView) mView.findViewById(R.id.userText);
        commentCount = (TextView) mView.findViewById(R.id.commentCount);
        mCheck = (CheckBox)mView.findViewById(R.id.checkBox);
        //    postList = (ListView)v.findViewById(R.id.postList);
        addView(mView);
    }

    public void setData(final BBSModel model, final boolean isDelete,ArrayList<BBSModel> list) {
        if(isDelete){
            mCheck.setVisibility(VISIBLE);
        }else{
            mCheck.setVisibility(GONE);
        }
        deleteList = list;
        mCheck.setChecked(false);
        if(mCheck.getVisibility()==VISIBLE&&deleteList.size()>0){
            if(deleteList.contains(model)){
                mCheck.setChecked(true);
            }
        }
      /*  int i = mList.get(1).getBoardID();
        if(i==1){

        }*/
        //       MyAdapter _Adapter = new MyAdapter();
//        postList.setAdapter(_Adapter);
//        setHeight(postList);
        fromText.setText(model.getBoardName());
        title.setText(model.getTitle());
        //    userText.setText(model.getNicName());
        //    timeText.setText(model.getCreateTime());
        commentCount.setText(model.getReplyCount() + "");
        mView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDelete){
                    selected(model);
                    if(deleteList.size()>0){
                        isSelect.isSelect(true);
                    }else{
                        isSelect.isSelect(false);
                    }
                    return;
                }
                Intent _Intent = new Intent();
                _Intent.setClass(mContext, BBSDetailsActivity.class);
                _Intent.putExtra("pid", Integer.valueOf(model.getUFID()));
                mContext.startActivity(_Intent);
            }
        });
    }
    public void selected(BBSModel model) {
        if (!deleteList.contains(model)) {
            deleteList.add(model);
            mCheck.setChecked(true);
        } else {
            deleteList.remove(model);
            mCheck.setChecked(false);
        }

    }

   /* public void setHeight(ListView listView) {
        ListAdapter _ListAdapter = listView.getAdapter();
        int Height = 0;
        if (_ListAdapter == null) {
            return;
        }
        for (int i = 0; i < mList.size(); i++) {
            View _Item = (View) _ListAdapter.getView(i, null, listView);
            _Item.measure(0, 0);
            Height += _Item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams _Param = listView.getLayoutParams();
        _Param.height = Height + (listView.getDividerHeight() * (mList.size() - 1));
        listView.setLayoutParams(_Param);
    }*/

   /* private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            BBSModel _Model = mList.get(position);
            View v = LayoutInflater.from(mContext).inflate(R.layout.post_store_item, null);
        *//*    TextView title = (TextView) v.findViewById(R.id.postTitle);
            TextView userText = (TextView) v.findViewById(R.id.userText);
            TextView timeText = (TextView) v.findViewById(R.id.sendTime);
            TextView commentCount = (TextView) v.findViewById(R.id.commentCount);
            title.setText(_Model.getTitle());
            userText.setText(_Model.getUserName());
            timeText.setText(_Model.getAddTime());
            commentCount.setText(_Model.getReplyCount() + "");
            v.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return true;
                }
            });*//*
            return v;
        }*/

}
