package com.dldzkj.app.renxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.bean.ConstantValue;
import com.dldzkj.app.renxing.customview.AppDialog;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Lybin on 2015/7/2.
 */
public class NickActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.editText)
    EditText editText;
    @InjectView(R.id.nl_cancel)
    TextView nlCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nick_layout);
        ButterKnife.inject(this);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.in_save:
                String s = editText.getText().toString();
                if(s.equals("")){
                    showDialog();
                    break;
                }
                setResult(0,new Intent(s));
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public void showDialog(){
        AppDialog _Dialog = new AppDialog(this,R.style.dialog,AppDialog.TIP_TYPE);
        _Dialog.show();
        _Dialog.setTitle(getResources().getString(R.string.info_input_nick));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_nick, menu);
        return true;
    }

    public void init() {
        setTitle(getResources().getString(R.string.info_nick));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        nlCancel.setOnClickListener(this);
        editText.addTextChangedListener(new EditChange(editText));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nl_cancel:
                editText.setText("");
        }
    }
    public static String stringFilter(CharSequence str)throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String   regEx  =  "[^a-zA-Z0-9x_x\u4E00-\u9FA5]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        return   m.replaceAll("").trim();
    }

    class EditChange implements TextWatcher{

        private EditText mEdit;
        private final int MaxLength=10;//中文长度
        private final int ABCLength=20;//字符长度
        private int currentLength;
        private int cou = 0;
        private int selection = 0;

        public EditChange(EditText editText){
            mEdit=editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
           String ss= mEdit.getText().toString();
           String _S= stringFilter(ss);
            if (ss.matches("[\\u4e00-\\u9fa5]+")){
                currentLength=MaxLength ;
            }else{
                currentLength=ABCLength ;
            }
            //��ֹ��ѭ��
            if(!_S.equals(ss)){
                mEdit.setText(_S);
                mEdit.setSelection(_S.length());
            }
            cou=mEdit.length();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(cou>currentLength){
                selection = mEdit.getSelectionEnd();
                s.delete(currentLength,selection);
            }
        }
    }
}
