package com.dldzkj.app.renxing.customview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.NickActivity;

import java.util.Objects;

/**
 * Created by lyBin on 2015/7/1.
 */
public class PersanolInfoItem extends RelativeLayout {
    private int mPosition;
    private Context mContext;
    private TextView mName,mContent;
    private ImageView mExtend;

    public PersanolInfoItem(Context context,int position) {
        this(context,null,position);
    }

    public PersanolInfoItem(Context context, AttributeSet attrs,int position) {
        this(context, attrs, 0, position);
    }

    public PersanolInfoItem(Context context, AttributeSet attrs, int defStyleAttr,int position) {
        super(context, attrs, defStyleAttr);
        initView(context, position);
    }

    private void initView(Context context,int position){
        mContext=context;
        mPosition=position;
        if(mPosition==6){
           View  v =  LayoutInflater.from(mContext).inflate(R.layout.peision_info_item_two,null);
            mName= (TextView)v.findViewById(R.id.piit_name);
            mContent = (TextView)v.findViewById(R.id.piit_content);
            addView(v);
        }else{
            View  v = LayoutInflater.from(mContext).inflate(R.layout.personal_info_item,null);
            mName= (TextView)v.findViewById(R.id.pii_name);
            mContent = (TextView)v.findViewById(R.id.pii_content);
            mExtend = (ImageView)v.findViewById(R.id.pii_extend);
            addView(v);
        }
    }
    public void bindData(String s){
        switch(mPosition) {
            case 0:
                mName.setText(getResources().getString(R.string.info_nick));
                mContent.setText(s);
                break;
            case 1:
                mName.setText("手机号码");
                mContent.setText(s);
                mExtend.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mName.setText(getResources().getString(R.string.info_level));
                mContent.setText(s);
                mExtend.setVisibility(View.INVISIBLE);
                break;
            case 3:
                mName.setText(getResources().getString(R.string.info_sex));
                mContent.setText(s);
                mExtend.setVisibility(View.INVISIBLE);
                break;
            case 4:
                mName.setText(getResources().getString(R.string.info_birthday));
             //   SimpleDateFormat _Format=new SimpleDateFormat("yyyy-MM-dd");
             //   String ss=_Format.format(s);
                mContent.setText(s);
                break;
            case 5:
                mName.setText(getResources().getString(R.string.info_area));
                mContent.setText(s);
                break;
            case 6:
                mName.setText(getResources().getString(R.string.info_sign));
                mContent.setText(s);
                break;
            case 7:
                mName.setText(getResources().getString(R.string.info_change_password));
                break;
            default:
                break;
        }
    }

   public void setContent(String s){
       mContent.setText(s);
   }


}
