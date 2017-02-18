package com.dldzkj.app.renxing.activity.blue;

import java.util.HashMap;


import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dldzkj.app.renxing.MyApplication;
import com.dldzkj.app.renxing.PromptManager;
import com.dldzkj.app.renxing.R;

public class MyFramlayout extends RelativeLayout {
	@SuppressWarnings("unused")
	private Context context;
	private MyApplication mApp;
	private int zeroY;
	@SuppressWarnings("unused")
	private int height;
	private int temp;
	private SoundPool mSound;
	private HashMap<Integer, Integer> soundMap;
	private boolean isTouch = false;
	private ImageView ivRipple;
	private Animation anim;
	private AnimationListener animListener;
	public MyFramlayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void init(Context context, MyApplication mApp) {
		this.mApp = mApp;
		zeroY = (int) getX();
		height = getHeight() / 9;
		this.context = context;
		mSound = new SoundPool(2, AudioManager.STREAM_RING, 5);
		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(1, mSound.load(context, R.raw.drop, 5));
		soundMap.put(2, mSound.load(context, R.raw.water, 5));
		animListener = new AnimListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		temp = getHeight() / 9;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mApp.BlueServiceIsConnnected()) {
			PromptManager.showToast(mApp, "玩具未连接");
			return false;
		}
		pointY = (event.getY() - zeroY) / temp;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isDown=true;
			mSound.play(soundMap.get(1), 5, 5, 0, 0, 1);
			handler.sendEmptyMessage(0);
			
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = (int) event.getX();
			msg.arg2 = (int) event.getY();
			handler.sendMessage(msg);
			break;
		case MotionEvent.ACTION_MOVE:
			mSound.play(soundMap.get(2), 5, 5, 0, 0, 1);
			float TouchX = event.getX();
			float TouchY = event.getY();
			Message m = new Message();
			m.what = 1;
			m.arg1 = (int) TouchX;
			m.arg2 = (int) TouchY;
			handler.sendMessage(m);

			break;
		case MotionEvent.ACTION_UP:
			isDown=false;
			break;
		default:
			break;
		}
		return true;
	}
	private boolean isDown=false;
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(isDown){
					mApp.sendBlueOrder(240 + (int) pointY);
					sendEmptyMessageDelayed(0, 50);
				}
				break;
			case 1:
				removeAllViews();
				ivRipple = new ImageView(context);
				ivRipple.setLayoutParams(new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				ivRipple.setImageResource(R.drawable.ripplering4);
				LayoutParams params = new LayoutParams(
						50, 50);
				params.leftMargin = msg.arg1;
				params.topMargin = msg.arg2;
				addView(ivRipple, params);
				anim = AnimationUtils.loadAnimation(
						context.getApplicationContext(), R.anim.ripplescale);
				 anim.setAnimationListener(animListener);
				ivRipple.setAnimation(anim);
				break;
			default:
				break;
			}
			
		};
	};
	private float pointY;

}
class AnimListener implements AnimationListener {

	private RelativeLayout relativeLayout;

	public AnimListener(RelativeLayout rl) {
		relativeLayout = rl;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		try {
			relativeLayout.removeAllViews();
		} catch (Exception e) {
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

}