package com.dldzkj.app.renxing.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.dldzkj.app.renxing.R;

/**
 * 自定义popupWindow
 *
 * @author wwj
 */
public class AppPopWindow extends PopupWindow {
    private View conentView;
    private OnPopListenner lis;

    public void setLis(OnPopListenner lis) {
        this.lis = lis;
    }

    public interface OnPopListenner {
        void OnBtn1ClickLis(PopupWindow p, View v);

        void OnBtn2ClickLis(PopupWindow p, View v);

        void OnBtn3ClickLis(PopupWindow p, View v);
    }

    public AppPopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.app_popup_dialog, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
//         setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.popAnimation);
        LinearLayout btn1 = (LinearLayout) conentView
                .findViewById(R.id.pop_btn1);
        LinearLayout btn2 = (LinearLayout) conentView
                .findViewById(R.id.pop_btn2);
        LinearLayout btn3 = (LinearLayout) conentView
                .findViewById(R.id.pop_btn3);
        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setMyLayoutClick(LinearLayout layout, boolean isShow) {
        ImageView iv= (ImageView) layout.getChildAt(0);
        TextView tv= (TextView) layout.getChildAt(1);
        if(isShow){
            iv.setSelected(true);
        }else{

        }
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(Toolbar parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);

        } else {
            this.dismiss();
        }
    }
}