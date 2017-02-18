package com.dldzkj.app.renxing.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dldzkj.app.renxing.BaseActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.customview.AppDialog;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/6/30.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.layout_about)
    RelativeLayout layoutAbout;
    @InjectView(R.id.layout_clear)
    RelativeLayout layoutClear;
    ProgressDialog dialogPro;
    File imgLoaderCache, glideCache;
    @InjectView(R.id.et_send_content2)
    TextView etSendContent2;
    private long allSize = 0;
    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            etSendContent2.setText("是否清除缓存("+long2Str(allSize)+")");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        initToolBar();
        initEvents();
        imgLoaderCache = StorageUtils.getCacheDirectory(getBaseContext().getApplicationContext());
        glideCache = Glide.getPhotoCacheDir(getBaseContext().getApplicationContext());
        dialogPro = ProgressDialog.show(this, null, "请稍后...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                allSize = getDirectorySize(imgLoaderCache) + getDirectorySize(glideCache);
                h.sendEmptyMessage(0);
                dialogPro.dismiss();
            }
        }).start();
    }

    private void initEvents() {
        layoutAbout.setOnClickListener(this);
        layoutClear.setOnClickListener(this);
       /* switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });*/
    }

    private void initToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setTitle("设置");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_about:
                startActivity(new Intent(getApplicationContext(),Activity_About.class)
                        .putExtra("URL", "file:///android_asset/about_app.html"));
                break;
            case R.id.layout_clear:
                showDialog("确定清楚缓存图片(" + long2Str(allSize) + ")");
                break;
        }
    }

    public String long2Str(long size) {
        if (size / 1000 >= 0) {
            if (size / 1000000 > 0) {
                return (size / 10000000) + "M";
            } else {
                return (size / 1000) + "K";
            }
        } else {
            return size + "B";
        }
    }

    public void showDialog(String content) {
        final AppDialog dialog = new AppDialog(this, R.style.dialog, AppDialog.ASK_TYPE);
        dialog.show();
        dialog.setTitle(content);
        dialog.setSureText("确定");
        dialog.setSureClickListner(new AppDialog.dialogListenner() {
            @Override
            public void setOnSureLis(Dialog d, View v) {
                dialog.cancel();
                dialogPro.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        deleteFilesByDirectory(imgLoaderCache);
                        deleteFilesByDirectory(glideCache);
                        allSize = getDirectorySize(imgLoaderCache) + getDirectorySize(glideCache);
                        h.sendEmptyMessage(0);
                        dialogPro.dismiss();
                    }
                }).start();
            }
        });
    }

    private long getDirectorySize(File directory) {
        long size = 0;
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                size += getFileSize(item);
            }
        }
        return size;
    }

    private long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return size;
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */

    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }


}
