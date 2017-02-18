package com.dldzkj.app.renxing.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dldzkj.app.renxing.R;


public class AppDialog extends Dialog {

    private TextView tv_title, btn_sure;
    private ImageView icon;
    private int type;
    public static final int ASK_TYPE = 0;
    public static final int TIP_TYPE = 1;
    public static final int OVER_TYPE = 2;
    private dialogListenner listner;

    public interface dialogListenner {
        void setOnSureLis(Dialog d, View v);
    }
/******tip类型多为提示或警告，可以不用重写按钮点击事件
 *      ask类型多为选择询问，需要重写确定按钮事件
 *      OVEr类型多为操作完毕，进行下一步操作，需要重写点击事件*******/
    public AppDialog(Context context, int theme, int dialogType) {
        super(context, theme);
        this.type = dialogType;
    }
    public void setSureClickListner(final dialogListenner listner) {
        if (listner != null) {
            this.listner = listner;
            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.setOnSureLis(AppDialog.this, v);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_dialog_style);
        icon = (ImageView) findViewById(R.id.dialog_icon);
        tv_title = (TextView) findViewById(R.id.dialog_title);
        btn_sure = (TextView) findViewById(R.id.dialog_sure);

        if (type != ASK_TYPE) {
            switch (type){
                case TIP_TYPE:
                    icon.setImageResource(R.drawable.ic_sorry);
                    btn_sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                    break;
                case OVER_TYPE:
                    icon.setImageResource(R.drawable.ic_up_box_icon);
                    break;
            }
            return;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        tv_title.setText(title);
    }

    public void setSureText(CharSequence title) {
        btn_sure.setText(title);
    }

}