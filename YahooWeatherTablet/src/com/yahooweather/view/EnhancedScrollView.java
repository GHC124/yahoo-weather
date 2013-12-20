package com.yahooweather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class EnhancedScrollView extends ScrollView {
	private float mDistanceX;
	private float mDistanceY;
	private float mLastX;
	private float mLastY;

	@SuppressWarnings("unused")
	public EnhancedScrollView(Context context) {
		super(context);
	}

	@SuppressWarnings("unused")
	public EnhancedScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressWarnings("unused")
	public EnhancedScrollView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDistanceX = mDistanceY = 0f;
			mLastX = ev.getX();
			mLastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			mDistanceX += Math.abs(curX - mLastX);
			mDistanceY += Math.abs(curY - mLastY);
			mLastX = curX;
			mLastY = curY;
			if (mDistanceX > mDistanceY)
				return false;
		}

		return super.onInterceptTouchEvent(ev);
	}

}
