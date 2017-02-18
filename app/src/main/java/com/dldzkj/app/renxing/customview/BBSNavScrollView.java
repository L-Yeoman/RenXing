package com.dldzkj.app.renxing.customview;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.dldzkj.app.renxing.R;

public class BBSNavScrollView extends LinearLayout
{

	private View mTop;
	private View mNav;
	private ViewPager mViewPager;
	
	private int mTopViewHeight;
	private ViewGroup mInnerScrollView;
	private boolean isTopHidden = false;

	private OverScroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private int mMaximumVelocity, mMinimumVelocity;
	
	private float mLastY;
	private boolean mDragging;

	
	
	

	public BBSNavScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);


		mScroller = new OverScroller(context);
		//速度跟踪
		mVelocityTracker = VelocityTracker.obtain();
		//最小移动距离
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mMaximumVelocity = ViewConfiguration.get(context)
				.getScaledMaximumFlingVelocity();
		mMinimumVelocity = ViewConfiguration.get(context)
				.getScaledMinimumFlingVelocity();

	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		mTop = findViewById(R.id.id_stickynavlayout_topview);
		mNav = findViewById(R.id.id_stickynavlayout_indicator);
		View view = findViewById(R.id.id_stickynavlayout_viewpager);
		if (!(view instanceof ViewPager))
		{
			throw new RuntimeException(
					"id_stickynavlayout_viewpager show used by ViewPager !");
		}
		mViewPager = (ViewPager) view;
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
		params.height = getMeasuredHeight() - mNav.getMeasuredHeight();
	}



	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		//标题图片的高
		mTopViewHeight = mTop.getMeasuredHeight();
	}


	@Override
	//向子控件传递
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
		float y = ev.getY();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;

			getCurrentScrollView();

			if (Math.abs(dy) > mTouchSlop)
			{
				mDragging = true;
				
				
				if(mInnerScrollView instanceof ScrollView)
				{
					if (!isTopHidden
							|| (mInnerScrollView.getScrollY() == 0 && isTopHidden && dy > 0))
					{
						return true;
					}
				}else if(mInnerScrollView instanceof ListView)
				{
					ListView lv = (ListView) mInnerScrollView;
					View c = lv.getChildAt(lv.getFirstVisiblePosition());
					if (!isTopHidden
							|| ( c!= null && c.getTop() == 0 && isTopHidden && dy > 0))
					{
						return true;
					}
				}
				
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private void getCurrentScrollView()
	{

		int currentItem = mViewPager.getCurrentItem();
		PagerAdapter a = mViewPager.getAdapter();
		if (a instanceof FragmentPagerAdapter)
		{
			FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
			Fragment item = fadapter.getItem(currentItem);
			mInnerScrollView = (ViewGroup) (item.getView()
					.findViewById(R.id.id_stickynavlayout_innerscrollview));
		} else if (a instanceof FragmentStatePagerAdapter)
		{
			FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
			Fragment item = fsAdapter.getItem(currentItem);
			mInnerScrollView = (ViewGroup) (item.getView()
					.findViewById(R.id.id_stickynavlayout_innerscrollview));
		}
	}

	@Override
	//回传父控件
	public boolean onTouchEvent(MotionEvent event)
	{
		mVelocityTracker.addMovement(event);
		int action = event.getAction();
		float y = event.getY();

		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished())
				mScroller.abortAnimation();
			mVelocityTracker.clear();
			mVelocityTracker.addMovement(event);
			mLastY = y;
			return true;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;

			if (!mDragging && Math.abs(dy) > mTouchSlop)
			{
				mDragging = true;
			}
			if (mDragging)
			{
				scrollBy(0, (int) -dy);
				mLastY = y;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			mDragging = false;
			if (!mScroller.isFinished())
			{
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_UP:
			mDragging = false;
			//初始化速率
			mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
			//速率
			int velocityY = (int) mVelocityTracker.getYVelocity();
			if (Math.abs(velocityY) > mMinimumVelocity)
			{
				fling(-velocityY);
			}
			mVelocityTracker.clear();
			break;
		}

		return super.onTouchEvent(event);
	}

	public void fling(int velocityY)
	{
		//快滑
		mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
		invalidate();
	}

	@Override
	public void scrollTo(int x, int y)
	{
		if (y < 0)
		{
			y = 0;
		}
		if (y > mTopViewHeight)
		{
			y = mTopViewHeight;
		}
		if (y != getScrollY())
		{
			super.scrollTo(x, y);
		}
		//是否隐藏标题图片
		isTopHidden = getScrollY() == mTopViewHeight;

	}
	/* Scroller.computeScrollOffset方法是来判断滚动过程是否完成的,
	如果没有完成，就需要不停的scrollTo下去，所以在最后需要加一个invalidate(),
	这样可以再次触发computScroll,直到滚动已经结束。*/
	@Override
	public void computeScroll()
	{
		if (mScroller.computeScrollOffset())
		{
			scrollTo(0, mScroller.getCurrY());
			invalidate();
		}
	}

}
