package com.dldzkj.app.renxing.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;


public class AppSelectPicsDialog extends Dialog {

    private TextView tv_title, btn_sure, btn_cancel;
    private dialogListenner listner;
    private Activity cc;

    public interface dialogListenner {
        void setOnCameraLis(Dialog d, View v);

        void setOnGalleryLis(Dialog d, View v);

        void setOnCancelLis(Dialog d, View v);
    }

    public AppSelectPicsDialog(Context context, int theme) {
        super(context, theme);
        this.cc = (Activity) context;

    }

    public void setDialogListner(dialogListenner listner1) {
        this.listner = listner1;
        if (listner != null) {
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.setOnCameraLis(AppSelectPicsDialog.this, v);
                }
            });
            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.setOnGalleryLis(AppSelectPicsDialog.this, v);
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.setOnCancelLis(AppSelectPicsDialog.this, v);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_dialog_selectpics_style);
       //******设置dialog宽度全屏*****//*
        WindowManager windowManager = cc.getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度this
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);

        setCanceledOnTouchOutside(true);
        tv_title = (TextView) findViewById(R.id.dialog_title);
        btn_sure = (TextView) findViewById(R.id.dialog_sure);
        btn_cancel = (TextView) findViewById(R.id.dialog_cancel);
    }

}