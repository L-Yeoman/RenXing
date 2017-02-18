package com.dldzkj.app.renxing.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.R;
import com.dldzkj.app.renxing.customview.ProgressWebView;

/**
 * @author Administrator
 * 
 */
public class  Activity_About extends BaseOtherActivity implements OnClickListener {

	private ProgressWebView web;
	private ImageView back;
	private RelativeLayout menu;
	private ImageView wb_back, wb_forward, wb_refresh;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		web = (ProgressWebView) findViewById(R.id.web);
		back = (ImageView) findViewById(R.id.back);
		menu = (RelativeLayout) findViewById(R.id.wb_menu);
		wb_back = (ImageView) findViewById(R.id.wb_goback);
		wb_forward = (ImageView) findViewById(R.id.wb_goforward);
		wb_refresh = (ImageView) findViewById(R.id.wb_refresh);
		// 鍚敤鏀寔javascript
		WebSettings settings = web.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				judgeMenuShow();
				super.onPageFinished(view, url);

			}
		});
		setListener();
		initData();
	}

	private void setListener() {
		back.setOnClickListener(this);
		if (menu.getVisibility() != View.VISIBLE) {
			return;
		}
	}

	private void judgeMenuShow() {
		wb_refresh.setOnClickListener(this);
		if (web.canGoBack()) {
			wb_back.setImageResource(R.drawable.lanzuo);
			wb_back.setClickable(true);
			wb_back.setOnClickListener(this);
		} else {
			wb_back.setImageResource(R.drawable.huizuo);
			wb_back.setClickable(false);
		}
		if (web.canGoForward()) {
			wb_forward.setImageResource(R.drawable.lanyou);
			wb_forward.setClickable(true);
			wb_forward.setOnClickListener(this);
		} else {
			wb_forward.setImageResource(R.drawable.huiyou);
			wb_forward.setClickable(false);
		}
	}

	private void initData() {
		// TODO Auto-generated method stub
		if (getIntent().getBooleanExtra("showMenu", false))
			menu.setVisibility(View.VISIBLE);
		if (getIntent().getStringExtra("URL") != null) {
			Log.d("zxl", "url:" + getIntent().getStringExtra("URL"));
			loadUrl(getIntent().getStringExtra("URL"));
		}
	}

	public void loadUrl(String url) {
		if (web != null) {
			web.loadUrl(url);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.wb_goback:
			web.goBack();
			break;
		case R.id.wb_goforward:
			web.goForward();
			break;
		case R.id.wb_refresh:
			web.reload();
			break;
		default:
			break;
		}
	}
}
