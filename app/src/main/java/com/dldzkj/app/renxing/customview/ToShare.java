package com.dldzkj.app.renxing.customview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dldzkj.app.renxing.BaseOtherActivity;
import com.dldzkj.app.renxing.R;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class ToShare extends Activity {
	private Context mContext;
	private View mView;
	private TextView mCancel;
	private RelativeLayout mChat, mChatCircle, mQq, mQqzone, mSina;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_layout);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);
		initView();

	}
	public void initView(){
		mCancel = (TextView) findViewById(R.id.sl_cancel);
		mChat =(RelativeLayout)findViewById(R.id.sl_weixin);
		mChatCircle =(RelativeLayout)findViewById(R.id.sl_weixin_circle);
		mQq =(RelativeLayout)findViewById(R.id.sl_qq);
		mSina =(RelativeLayout)findViewById(R.id.sl_sina);
		mChat.setOnClickListener(new ClickListener());
		mChatCircle.setOnClickListener(new ClickListener());
		mQq.setOnClickListener(new ClickListener());
		mSina.setOnClickListener(new ClickListener());
		mCancel.setOnClickListener(new ClickListener());
	}
	
	public class ClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.sl_qq:
			//	shareToQq();
				break;
			/*case R.id.to_qq_zone:
				shareToQqZone();
				break;*/
			case R.id.sl_sina:
			//	shareToSina();
				break;
			case R.id.sl_weixin:
			//	shareToChat();
				break;
			case R.id.sl_weixin_circle:
			//	shareToChatCircle();
				break;
			case R.id.sl_cancel:
				ToShare.this.finish();
				break;
			default:
				break;
			}
		}
	}
	
	public void shareToChat(){
		Wechat.ShareParams sp = new Wechat.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		/*sp.setTitle("测试分享的标题");
		sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
		sp.setText("测试分享的文本");
		sp.setImageUrl("http://www.someserver.com/测试图片网络地址.jpg");
		sp.setSite("发布分享的网站名称");
		sp.setSiteUrl("发布分享网站的地址");*/
	//	sp.setUrl("http://www.dldouqu.com/");
		Platform _Chat = ShareSDK.getPlatform(Wechat.NAME);

		_Chat.setPlatformActionListener(new shareBack());
		_Chat.share(sp);
	}
	public void shareToChatCircle(){
		WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setUrl("http://www.dldouqu.com/");
		Platform _ChatCircle = ShareSDK.getPlatform(WechatMoments.NAME);
		_ChatCircle.setPlatformActionListener(new shareBack());
		_ChatCircle.share(sp);
	}
	public void shareToQq(){
		QQ.ShareParams sp = new QQ.ShareParams();
		sp.setTitle("测试分享的标题");
		sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
		sp.setText("测试分享的文本");
		sp.setImageUrl("http://www.someserver.com/测试图片网络地址.jpg");
		Platform _QQ = ShareSDK.getPlatform(QQ.NAME);
		_QQ.setPlatformActionListener(new shareBack());
		_QQ.share(sp);
		
	}
	public void shareToQqZone(){
		QZone.ShareParams sp = new QZone.ShareParams();
		sp.setTitle("测试分享的标题");
		sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
		sp.setText("测试分享的文本");
		sp.setImageUrl("http://www.someserver.com/测试图片网络地址.jpg");
		sp.setSite("发布分享的网站名称");
		sp.setSiteUrl("发布分享网站的地址");

		Platform qzone = ShareSDK.getPlatform (QZone.NAME);
		qzone. setPlatformActionListener (new shareBack()); // 设置分享事件回调
		// 执行图文分享
		qzone.share(sp);
	}
	public void shareToSina(){
		SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
		sp.setText("测试分享的文本");
		sp.setImagePath("/mnt/sdcard/测试分享的图片.jpg");
		Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
		weibo.setPlatformActionListener(new shareBack());
		weibo.share(sp);
/*
		SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
		sp.setText("测试分享的文本");
		sp.setImageUrl("http://www.someserver.com/测试图片网络地址.jpg");

		Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
		weibo.setPlatformActionListener(new shareBack()); // 设置分享事件回调
		// 执行图文分享
		weibo.share(sp);*/
		
	}
	public class shareBack implements PlatformActionListener {
		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			mHandler.sendEmptyMessage(0);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			mHandler.sendEmptyMessage(1);
		}

		@Override
		public void onCancel(Platform arg0, int arg1) {
			
		}
		
	}
	
	Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){
				Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
			}
			if(msg.what==1){
				Toast.makeText(mContext, "分享失败",Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	
	
	
	
	
	
	
}
