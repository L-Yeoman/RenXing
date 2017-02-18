package com.dldzkj.app.renxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.customview.AppDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lyBin on 2015/7/3.
 */
public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.cpl_editText)
    EditText cplEditText;
    @InjectView(R.id.cpl_cancel)
    TextView cplCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_phone_layout);
        ButterKnife.inject(this);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.in_save:
                String s = cplEditText.getText().toString();
                if (s.equals("")) {
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
         _Dialog.setTitle(getResources().getString(R.string.info_inputPhoneNumb_warning));
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_nick, menu);
        return true;
    }

    public void init() {
        setTitle(getResources().getString(R.string.info_chengePhone_title));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        cplCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cpl_cancel:
                cplEditText.setText("");
                break;
        }
    }
}
