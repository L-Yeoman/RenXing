package com.dldzkj.app.renxing.mainfragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.activity.MyFavoritesActivity;
import com.dldzkj.app.renxing.activity.MyPhotoActivity;

import com.dldzkj.app.renxing.activity.PersonalInfo;
import com.dldzkj.app.renxing.activity.MyPostActivity;
import com.dldzkj.app.renxing.activity.PersonScoreActivity;
import com.dldzkj.app.renxing.activity.SettingActivity;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.bean.User;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.dldzkj.app.renxing.customview.AvaterDialogActivity;
import com.dldzkj.app.renxing.utils.AppDbUtils;
import com.dldzkj.app.renxing.utils.SPUtils;
import com.dldzkj.app.renxing.utils.WebUtils;
import com.lidroid.xutils.exception.HttpException;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MenuLeftFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.menu_setting)
    ImageView menuSetting;
    @InjectView(R.id.menu_avart)
    CircleImageView menuAvart;
    @InjectView(R.id.menu_photo)
    ImageView menuPhoto;
    @InjectView(R.id.menu_name)
    TextView menuName;
    @InjectView(R.id.menu_level)
    TextView menuLevel;
    @InjectView(R.id.menu_note)
    TextView menuNote;
    @InjectView(R.id.layout_one)
    RelativeLayout layoutOne;
    @InjectView(R.id.layout_two)
    RelativeLayout layoutTwo;
    @InjectView(R.id.layout_three)
    RelativeLayout layoutThree;
    @InjectView(R.id.layout_four)
    RelativeLayout layoutFour;
    @InjectView(R.id.layout_five)
    RelativeLayout layoutFive;
    @InjectView(R.id.layout_six)
    RelativeLayout layoutSix;

    @InjectView(R.id.right_icon1)
    ImageView Icon_SignIn;
    @InjectView(R.id.menu_score_value)
    TextView scoreValue;
    private User u;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_menu_left, container, false);
        ButterKnife.inject(this, view);
        menuAvart.setBorderColorResource(R.color.app_theme);
        menuAvart.setBorderWidth(8);
        initEvents();
        //获取是否签到
        WebRequstIsSignIn();
        WebRequstGetAllPoint();//获取积分总和
        return view;
    }

    private void getLoginUserData() {
        AppDbUtils util = new AppDbUtils();
        u = util.getLoginUser(SPUtils.getLoginId(getActivity()));
        if (u == null) return;
        menuName.setText(u.getNicName());
        if (u.getPortrait() != null && !u.getPortrait().isEmpty()) {
            Glide.with(this.getActivity())
                    .load(WebUtils.RENXING_WEB + u.getPortrait())
                    .into(menuAvart);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getLoginUserData();
        setNote();
    }

    private void initEvents() {
        layoutOne.setOnClickListener(this);
        layoutTwo.setOnClickListener(this);
        layoutThree.setOnClickListener(this);
        layoutFour.setOnClickListener(this);
        layoutFive.setOnClickListener(this);
        layoutSix.setOnClickListener(this);
        menuAvart.setOnClickListener(this);
        menuPhoto.setOnClickListener(this);
        menuSetting.setOnClickListener(this);
        menuNote.setOnClickListener(this);
    }

    private void setNote() {
        String noteStr = u == null ? "" : u.getSignature().toString();
        if (noteStr.isEmpty()) {
            noteStr = "这家伙很懒，什么也没有留下";
        }
        Drawable drawable = getResources().getDrawable(R.drawable.ic_home_side_quotes);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        SpannableString spannable = new SpannableString("[smile]  " + noteStr);
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(span, 0, "[smile]".length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        menuNote.setText(spannable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_avart:
                break;
            case R.id.menu_photo:
                showPopWindow();
                break;
            case R.id.layout_one:
                signIn();
                break;
            case R.id.layout_two:
                startActivity(new Intent(getActivity(), MyPhotoActivity.class));
                break;
            case R.id.layout_three:
                startActivity(new Intent(getActivity(), MyFavoritesActivity.class));
                break;
            case R.id.layout_four:
                startActivity(new Intent(getActivity(), MyPostActivity.class));
                break;
            case R.id.layout_five:
                startActivity(new Intent(getActivity(), PersonScoreActivity.class).putExtra("scoreValue",scoreValue.getText().toString()));
                break;
            case R.id.layout_six:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.menu_setting:
                startActivity(new Intent(getActivity(), PersonalInfo.class));
                break;
            case R.id.menu_note:
        }
    }

    private void showPopWindow() {
        startActivity(new Intent(getActivity(), AvaterDialogActivity.class));
        getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
    }

    public void signIn() {
        final Context _Context = MyApplication.getInstance().getApplicationContext();
        HashMap<String, Object> _Map = new HashMap<String, Object>();
        _Map.put("UserID", SPUtils.getLoginId(getActivity()));
        WebUtils.sendPost(getActivity(), ConstantValue.SignIn, _Map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    Icon_SignIn.setImageResource(R.drawable.ic_home_side_icon1_pressed);
                    showDialog("签到成功");
                } else {
                    Toast.makeText(MenuLeftFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(HttpException error, String msg) {
//                showDialog(msg);
            }
        });
    }

    public void showDialog(String content) {
        final AppDialog dialog = new AppDialog(getActivity(), R.style.dialog, AppDialog.OVER_TYPE);
        dialog.show();
        dialog.setTitle(content);
        dialog.setSureText("确定");
        dialog.setSureClickListner(new AppDialog.dialogListenner() {
            @Override
            public void setOnSureLis(Dialog d, View v) {
                dialog.cancel();
            }
        });
    }


    private void WebRequstGetAllPoint() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserID", SPUtils.getLoginId(getActivity()));
        WebUtils.sendPostNoProgress(getActivity(), ConstantValue.GetPointCount, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    String value = (String) JSON.parseObject(JSON.parseArray(data).get(0).toString()).get("Point");
                    scoreValue.setText(value);
                } else {

                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }
    private void WebRequstIsSignIn() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("UserID", SPUtils.getLoginId(getActivity()));
        WebUtils.sendPostNoProgress(getActivity(), ConstantValue.SignIsOrNot, map, new WebUtils.OnWebListenr() {
            @Override
            public void onSuccess(boolean isSuccess, String msg, String data) {
                if (isSuccess) {
                    int value = (int) JSON.parseObject(JSON.parseArray(data).get(0).toString()).get("IsSign");
                    if (value == 1) {
                        Icon_SignIn.setImageResource(R.drawable.ic_home_side_icon1_pressed);
                    }
                }

            }

            @Override
            public void onFailed(HttpException error, String msg) {

            }
        });
    }
}
