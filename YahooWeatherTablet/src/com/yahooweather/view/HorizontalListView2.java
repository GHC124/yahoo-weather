package com.yahooweather.view;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

/**
 * HorizontalListView implements and a Horizontal ListView. It gets Views from
 * an Adapter and recycles Views when they are no longer visible, thus
 * minimizing the number of required Views.
 * 
 * TODO: Add ability to handle multiple View types. Currently only handles a
 * single type of View. Does not call getViewType() or getViewTypeCount() on the
 * adapter.
 * 
 * TODO: Might need to consider additional parameters provided by View returned
 * from adapter. If so, update getParams(View child).
 * 
 */
public class HorizontalListView2 extends AdapterView<ListAdapter> {
	private static final String TAG = HorizontalListView2.class.getSimpleName();

	protected ListAdapter mAdapter;
	private GestureDetector mGesture;
	protected Scroller mScroller;
	private boolean mDataChanged = false;
	private int mCurrentX;

	// scrolling x.
	private int mNextX;

	// total width of views.
	private int mMaxX = 0;

	/*
	 * offsets for scrolling left and right.
	 */
	private int mDisplayLeftOffset = 0;
	private int mDisplayRightOffset = 0;

	/*
	 * state variables indicating whether offsets have been set or not.
	 */
	private boolean mDisplayLeftOffsetSet = false;
	private boolean mDisplayRightOffsetSet = false;

	/*
	 * first and last visible positions.
	 */
	private int mFirstVisiblePosition = 0;
	private int mLastVisiblePosition = 0;

	// using boundarys of 0..width of viewgroup view to determine visibility.
	private int mLeftVisibleBoundary;
	private int mRightVisibleBoundary;

	private int mAdapterChildCount;

	// holds recycled views, so they can be reused. currently, handles only
	// one type of View.
	//
	// TODO: Handle different View types defined by the Adapter via
	// getViewType() and getViewTypeCount(). Possibly make this a HashMap with
	// key being ViewType.
	private Queue<View> mRecycledViews = new LinkedList<View>();

	// mode/state of HorizontalListView.
	private enum TouchMode {
		TOUCH_MODE_REST, TOUCH_MODE_DOWN, TOUCH_MODE_FLING, TOUCH_MODE_SCROLL, TOUCH_MODE_SINGLE_TAP
	}

	private TouchMode mTouchMode = TouchMode.TOUCH_MODE_REST;
	private boolean mFillListLeft = true;

	// various listeners
	protected OnItemSelectedListener mOnItemSelectedListener;
	protected OnItemClickListener mOnItemClickedListener;
	protected HorizontalListViewScrollListener mHorizontalListViewScrollListener;

	// populated in onMeasure() and used for re-measuring maximum scrolling
	// amount (mMaxX) after dataset has changed.
	private int mWidthMeasureSpec;

	/**
	 * The last scroll state reported to clients through
	 * {@link OnScrollListener}.
	 */
	private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

	private boolean mIdleNotificationSent = false;

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized (HorizontalListView2.this) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}

	};

	/**
	 * Used for updating layout in onLayout()/requestLayout() loop.
	 */
	private final Runnable mRequestLayoutRunnable = new Runnable() {
		@Override
		public void run() {
			requestLayout();
		}
	};

	/**
	 * Used during a fling for keeping row moving smoothly along with
	 * potententially other rows.
	 */
	private FillListDxRunnable mFillListDxRunnable = new FillListDxRunnable();

	private class FillListDxRunnable implements Runnable {
		public int dx;

		@Override
		public void run() {
			fillList(dx);
		}
	}

	/**
	 * Used during a fling for keeping row moving smoothly along with
	 * potententially other rows.
	 */
	private RequestLayoutDxRunnable mRequestLayoutDxRunnable = new RequestLayoutDxRunnable();

	private class RequestLayoutDxRunnable implements Runnable {
		public float distanceX;

		@Override
		public void run() {
			mNextX -= distanceX;
			requestLayout();
		}

	}

	/**
	 * Used for updating layout via fling.
	 * 
	 * TODO: If views don't line up from time to time, look further into
	 * postDelayed(). It seemed to be causing intermittent issues.
	 */
	private FlingRunnable mFlingRunnable;
	private final int FLING_RUNNABLE_DELAY = 200;

	private class FlingRunnable implements Runnable {

		public MotionEvent mE1;
		public MotionEvent mE2;
		public float mVelocityX;
		public float mVelocityY;

		public void setMotionEvent1(MotionEvent e1) {
			mE1 = e1;
		}

		public void setMotionEvent2(MotionEvent e2) {
			mE2 = e2;
		}

		public void setVelocityX(float velocityX) {
			mVelocityX = velocityX;
		}

		public void setVelocityY(float velocityY) {
			mVelocityY = velocityY;
		}

		@Override
		public void run() {

			// if at rest, we will begin fling cycle.
			// otherwise we will try again.
			if (mTouchMode == TouchMode.TOUCH_MODE_REST) {

				// DEV_DEBUG:
				// Log.d(TAG, "FlingRunnable (" + hashCode() +
				// ") - IMMEDIATE fling.");

				clearIdleNotification();
				mTouchMode = TouchMode.TOUCH_MODE_FLING;
				reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);

				// TODO: Using mNextX as the starting point seems reasonable;
				// although,
				// taking X value from event2 into consideration might be
				// worthwhile.

				// the X offset within the entire viewgroup == 0 ...
				// child(N).width().
				final int eventStartX = mNextX;

				// DEV_DEBUG:
				// Log.d(TAG, "FlingRunnable (" + hashCode() +
				// ") - eventStartX == " + eventStartX);

				// TODO: Believe this calculation should occur when the runnable
				// is executed, but it might need to occur at the time the
				// gesture
				// happened.

				mScroller.fling(
				/*
				 * startX, startY
				 */
				eventStartX, 0,
				/*
				 * velocityX, velocityY
				 */
				(int) mVelocityX, 0,
				/*
				 * minX, maxX
				 */
				mLeftVisibleBoundary, mMaxX,
				/*
				 * minY, maxY
				 */
				0, 0);

				requestLayout();

			} else {

				// DEV_DEBUG:
				// Log.d(TAG, "FlingRunnable (" + hashCode() +
				// ") - unfinished event; DELAYED fling.");

				// if other event still hasn't finished, we will try again.
				postDelayed(this, FLING_RUNNABLE_DELAY);
			}
		}

	}

	/**
	 * Can't distinguish between multiple immediate scrolls and when a scroll is
	 * complete. Consequently, this Runnable will be used for notifying state
	 * change.
	 */
	private final int SCROLL_STATE_IDLE_RUNNABLE_DELAY = 200;
	private Runnable mScrollStateIdleRunnable = new Runnable() {

		@Override
		public void run() {
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
		}
	};

	private void clearIdleNotification() {
		removeCallbacks(mScrollStateIdleRunnable);
		mIdleNotificationSent = false;
	}

	/**
	 * Initial state.
	 */
	private void init() {

		resetLeftOffset();
		resetRightOffset();

		mFillListLeft = true;

		mFirstVisiblePosition = 0;
		mLastVisiblePosition = 0;

		clearIdleNotification();

		mTouchMode = TouchMode.TOUCH_MODE_REST;

		mCurrentX = 0;
		mNextX = 0;
		mMaxX = 0;
	}

	public HorizontalListView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);

		init();
	}

	public HorizontalListView2(Context context, AttributeSet attrs) {

		// TODO: think about style. -1 reasonable value?
		this(context, attrs, -1);
	}

	public HorizontalListView2(Context context) {
		this(context, null);
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		mOnItemClickedListener = listener;
	}

	public void setOnScrollListener(HorizontalListViewScrollListener listener) {
		mHorizontalListViewScrollListener = listener;
	}

	/**
	 * callback interface used to notify listeners to scroll, fling, and stop.
	 */
	public interface HorizontalListViewScrollListener {

		/**
		 * Notified when view should scroll.
		 * 
		 * @param scrollingView
		 *            The scrolling View that caused this event.
		 * @param e1
		 * @param e2
		 * @param velocityX
		 * @param velocityY
		 */
		public void onScroll(HorizontalListView2 scrollingView, MotionEvent e1,
				MotionEvent e2, float velocityX, float velocityY);

		/**
		 * Notified when scrolling state changed.
		 * 
		 * @param view
		 *            The scrolling View that caused this event.
		 * @param scrollState
		 *            New state.
		 */
		public void onScrollStateChanged(HorizontalListView2 view,
				int scrollState);

		/**
		 * Notified when view should fling.
		 * 
		 * @param scrollingView
		 *            The scrolling View that caused this event.
		 * @param distanceX
		 *            Fling distance.
		 */
		public void onFling(HorizontalListView2 scrollingView, int distanceX);

		/**
		 * Notified when View should stop scrolling immediately.
		 * 
		 * @param scrollingView
		 *            The scrolling View that caused this event.
		 */
		public void onForceScrollFinish(HorizontalListView2 scrollingView);

		/**
		 * Notified when overscrolling to the left.
		 * 
		 * @param scrollingView
		 */
		public void onOverScrolledLeft(HorizontalListView2 scrollingView);

		/**
		 * Notified when overscrolling to the right.
		 * 
		 * @param scrollingView
		 */
		public void onOverScrolledRight(HorizontalListView2 scrollingView);
	}

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {

		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}

		mAdapter = adapter;
		mAdapterChildCount = mAdapter.getCount();
		mAdapter.registerDataSetObserver(mDataObserver);

		reset();
	}

	/**
	 * Measure the view and its content to determine the measured width and the
	 * measured height.
	 * 
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// DEV_DEBUG:
		// Log.d(TAG, "onMeasure, width == " + getWidth() +
		// " widthMeasureSpec == " + MeasureSpec.toString(widthMeasureSpec) +
		// " heightMeasureSpec == " + MeasureSpec.toString(heightMeasureSpec));

		// hold measure specs so mMaxX can be re-calculated on a dataset change.
		mWidthMeasureSpec = widthMeasureSpec;

		// TODO: don't believe setMeasuredDimension() needs to be called as
		// super.onMeasure() should handle viewgroup dimensions.
	}

	/**
	 * Calculates the maximum X that can be scrolled horizontally.
	 * 
	 * @param widthMeasureSpec
	 *            Measure spec provided in onMeasure().
	 */
	private View mMeasureChild = null;

	private void measureMaxX(int widthMeasureSpec) {

		// sum all children widths.
		for (int lv = 0; lv < mAdapterChildCount; lv++) {

			mMeasureChild = mAdapter.getView(lv, mMeasureChild, this);

			LayoutParams params = getLayoutParams(mMeasureChild);
			mMeasureChild.setLayoutParams(params);

			mMaxX += measureChild(mMeasureChild);
		}

		final int viewGroupWidth = MeasureSpec.getSize(widthMeasureSpec);

		// DEV_DEBUG:
		// Log.d(TAG, "measureMaxX - entire width of HorizontalListView == " +
		// mMaxX +
		// " viewgroup width == " + viewGroupWidth +
		// " reduced mMaxX == " + (mMaxX - viewGroupWidth) );

		// reduce mMaxX by viewgroup width as we can't scroll more than
		// this total distance.
		//
		// assuming width is specified via EXACTLY X pixels.
		mMaxX -= viewGroupWidth;
	}

	/**
	 * Puts class into initial state.
	 */
	private void reset() {
		mRecycledViews.clear();
		init();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {
		// TODO Auto-generated method stub
	}

	/**
	 * Measures child.
	 * 
	 * @param child
	 *            to measure.
	 * @return measured width.
	 * 
	 *         TODO: Are children getting measured too often?
	 */
	private int measureChild(View child) {

		final LayoutParams params = getLayoutParams(child);

		// DEV_DEBUG:
		// Log.d(TAG,"measureChild - child layoutParams width == " +
		// params.width);

		int measuredWidth;
		switch (params.width) {

		case ViewGroup.LayoutParams.FILL_PARENT:

			measuredWidth = MeasureSpec.makeMeasureSpec(getWidth(),
					MeasureSpec.EXACTLY);
			break;

		case ViewGroup.LayoutParams.WRAP_CONTENT:
			// TODO: may need to re-think this.
			measuredWidth = MeasureSpec.makeMeasureSpec(params.width,
					MeasureSpec.UNSPECIFIED);
			break;

		default:

			// user-specified exact size
			measuredWidth = MeasureSpec.makeMeasureSpec(params.width,
					MeasureSpec.EXACTLY);
			break;
		}

		// TODO: which makes sense. max of viewgroup or individual measurement?
		// child should be at most the height of the viewgroup.
		// int measuredHeight =
		// MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST);

		int measuredHeight;
		switch (params.height) {

		case ViewGroup.LayoutParams.FILL_PARENT:

			measuredHeight = MeasureSpec.makeMeasureSpec(getHeight(),
					MeasureSpec.EXACTLY);
			break;

		case ViewGroup.LayoutParams.WRAP_CONTENT:

			// TODO: may need to re-think this.
			measuredHeight = MeasureSpec.makeMeasureSpec(params.height,
					MeasureSpec.UNSPECIFIED);
			break;

		default:

			// user-specified exact size
			measuredHeight = MeasureSpec.makeMeasureSpec(params.height,
					MeasureSpec.EXACTLY);
			break;
		}

		child.measure(measuredWidth, measuredHeight);

		return child.getMeasuredWidth();
	}

	/**
	 * Returns layout parameters for the child.
	 * 
	 * @param child
	 *            Child whose parameters were requested.
	 * @return If supplied, parameters from child. Otherwise, FILL_PARENT width
	 *         and height.
	 */
	private LayoutParams getLayoutParams(View child) {
		LayoutParams params = child.getLayoutParams();
		return (params != null) ? params : new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	/**
	 * Removes all ViewGroup children and sends them to the recycler. It allows
	 * for optimizing getView(), which can hold on to views until the scroll
	 * direction changes. At that point, this is called and then all of the
	 * views are populated by the adapter.
	 */
	private void recycleViewGroupViews() {
		View child;

		while ((child = getChildAt(0)) != null) {
			removeViewInLayout(child);
			mRecycledViews.offer(child);
		}
	}

	/**
	 * Returns view from adapter. The view can reside in the viewGroup,
	 * recycler, or be created by the adapter.
	 * 
	 * @param viewGroupPosition
	 *            position where we are in the viewgroup.
	 * @param position
	 *            position in the adapter.
	 * @return View from adapter.
	 * 
	 *         TODO: Handle different View types defined by the Adapter via
	 *         getViewType() and getViewTypeCount(). Possibly use a HashMap with
	 *         key being ViewType.
	 * 
	 */
	private View getView(int viewGroupPosition, int position) {

		View child;

		// if the viewgroup does not have child,
		// get it from the adapter, which will attempt to use recycled views.
		// otherwise, return the viewgroup child.
		if ((child = getChildAt(viewGroupPosition)) == null) {
			child = mAdapter.getView(position, mRecycledViews.poll(), this);
		}

		return child;
	}

	/**
	 * Resets the right offset, and its state.
	 */
	private void resetRightOffset() {
		mDisplayRightOffsetSet = false;
		mDisplayRightOffset = 0;
	}

	/**
	 * Sets the right offset, and sets the state as "set".
	 * 
	 * @param newOffset
	 *            value of right offset.
	 */
	private void setRightOffset(final int newOffset) {
		mDisplayRightOffsetSet = true;
		mDisplayRightOffset = newOffset;
	}

	/**
	 * Determines whether or not the view can scroll left.
	 * 
	 * @return true if possible, false, if already scrolled as far left as
	 *         possible.
	 * 
	 *         TODO: Is this over-engineered? Would returning (mNextX > 0)
	 *         suffice?
	 */
	private boolean canScrollLeft() {
		// right offset has been set and offset is at right edge.
		// and last position is shown.
		//
		// or
		//
		// right offset has been set and right offset is left
		// of right edge; i.e., all views are on display.

		return !(mDisplayRightOffsetSet && ((mDisplayRightOffset == 0 && (mLastVisiblePosition == mAdapterChildCount - 1)) || (mDisplayRightOffset > 0 && (mDisplayRightOffset <= mRightVisibleBoundary))));
	}

	/**
	 * Determines whether or not the view can scroll right.
	 * 
	 * @return true if possible, false, if already scrolled as far right as
	 *         possible.
	 * 
	 *         TODO: Is this over-engineered? Would returning (mNextX < mMaxX)
	 *         suffice?
	 */
	private boolean canScrollRight() {

		// left offset has been set and offset is at left edge.
		// and first position is shown.

		return !(mDisplayLeftOffsetSet && (mDisplayLeftOffset == 0) && (mFirstVisiblePosition == 0));
	}

	/**
	 * Resets the left offset, and its state.
	 */
	private void resetLeftOffset() {
		mDisplayLeftOffsetSet = false;
		mDisplayLeftOffset = 0;
	}

	/**
	 * Sets the left offset, and sets the state as "set".
	 * 
	 * @param newOffset
	 *            value of left offset.
	 */
	private void setLeftOffset(final int newOffset) {
		mDisplayLeftOffsetSet = true;
		mDisplayLeftOffset = newOffset;
	}

	/**
	 * Fills the listview by scrolling views to the right by dx.
	 * 
	 * @param dx
	 *            how far to scroll right.
	 * @return true if it successfully scrolled, false if it would have
	 *         over-scrolled.
	 * 
	 *         TODO: Add a third state. Currently, the function returns true to
	 *         layout the list from the right. It returns false to layout the
	 *         list from the left. It might be worth having a third state which
	 *         says to do nothing when the user attempts to scroll right and
	 *         can't scroll any further.
	 * 
	 */
	private boolean fillListRight(int dx) {

		// if we switched direction, then recycle all views.
		// otherwise, views are shown in reverse order.
		if (mFillListLeft) {
			recycleViewGroupViews();
		}

		mFillListLeft = false;

		int offset = mDisplayRightOffset - dx;

		// index of where we are in the viewgroup.
		int viewGroupPosition = 0;

		while (true) {

			// position in adapter.
			int position = mLastVisiblePosition - viewGroupPosition;

			// DEV_DEBUG:
			// Log.d(TAG, "fillListRight (" + hashCode() + ") - position == " +
			// position);

			// get child & width.
			View child = getView(getChildCount() - 1 - viewGroupPosition,
					position);
			int childWidth = measureChild(child);

			//
			// handle view that will not be visible.
			//
			if (!willBeVisible(mLeftVisibleBoundary, mRightVisibleBoundary,
					mRightVisibleBoundary - childWidth, mRightVisibleBoundary,
					-offset)) {

				removeViewInLayout(child);
				mRecycledViews.offer(child);

				// case: view is beyond left edge.
				// result: we are done.
				//
				// we have iterated through the children. the viewgroup has at
				// least one child displayed. this child won't be displayed.
				// we are done.
				if (viewGroupPosition > 0) {
					// DEV_DEBUG:
					// Log.d(TAG, "fillListRight (" + hashCode() +
					// ") - break - view is beyond left edge.");

					break;
				}

				// case: view scrolled off to the right.
				// result: calculate right offset.
				//
				// we just hid the right-most view, we need to recalculate the
				// display offset.
				if (viewGroupPosition == 0) {
					setRightOffset(offset + childWidth);
				}

				// item scrolled off screen, decrement last visible position.
				--mLastVisiblePosition;

				// case: we have gone past first position.
				// result: fail.
				//
				// if no more views to display, we will return false, so that we
				// can populate list from left to right.
				if (mLastVisiblePosition < 0) {

					// DEV_DEBUG:
					// Log.d(TAG, "fillListRight (" + hashCode() +
					// ") - fail - gone past first position");

					onForceScrollFinish();
					mFirstVisiblePosition = 0;
					return false;
				}

			} else {

				//
				// visible view.
				//

				// update first visible position and offset through each
				// iteration.
				mFirstVisiblePosition = position;
				setLeftOffset(mRightVisibleBoundary - (childWidth + offset));

				// if the view hasn't already been added, add it at the HEAD of
				// the list.
				if (getChildAt(viewGroupPosition) == null) {
					addViewInLayout(child, 0, getLayoutParams(child), true);
				}

				// case: modifying the first visible item in the viewgroup.
				// result: save offset.
				if (viewGroupPosition == 0) {
					setRightOffset(offset);

				}

				// case: left edge of *displayed* view is invisible or
				// left edge of view is right at viewgroup edge.
				// result: we are done.
				// +--------------------
				// |
				// ... | view | view |view | view | ...
				// |
				// | or
				// |
				// | view | view |view | view | ...
				// |
				// +--------------------
				if (mDisplayLeftOffset <= mLeftVisibleBoundary) {

					// DEV_DEBUG:
					// Log.d(TAG, "fillListRight (" + hashCode() +
					// ") - break - left edge of " +
					// "*displayed* view is invisible or left " +
					// "edge of view is right at viewgroup edge.");

					break;
				}

				// case: left edge of first view is too far right.
				// result: fail.
				//
				// if the first view is showing and will be to the right of the
				// viewgroup's left edge, we are done. we will return false, so
				// that we can populate list from left to right.
				//
				//
				// issue, dx shift too far right:
				// +----------------------
				// |
				// | view | view |view | <-- need to shift to the left.
				// |
				// +----------------------
				//
				// resolution, populate list from left to right.
				// +----------------------
				// |
				// | view | view |view | -->
				// |
				// +----------------------
				if ((position == 0)
						&& (mDisplayLeftOffset > mLeftVisibleBoundary)) {

					// DEV_DEBUG:
					// Log.d(TAG, "fillListRight (" + hashCode() +
					// ") - fail - left edge of first view too far right");

					return false;
				}

				++viewGroupPosition;
			}

			offset += childWidth;

		}

		// scrolled right successfully.
		return true;
	}

	/**
	 * Lays out each child within the viewgroup, going from right to left.
	 */
	private void layoutRight() {
		// DEV_DEBUG:
		// Log.d(TAG, "layoutRight called.");

		int offset = mRightVisibleBoundary - mDisplayRightOffset;
		final int childCount = getChildCount();

		for (int lv = childCount - 1; lv >= 0; lv--) {
			View child = getChildAt(lv);

			int childWidth = child.getMeasuredWidth();
			child.layout(offset - childWidth, 0, offset,
					child.getMeasuredHeight());
			cleanupLayoutState(child);
			offset -= childWidth;

			// DEV_DEBUG:
			// HorizontalListViewDebug.logViewInfo(child, lv +
			// mFirstVisiblePosition);
		}

	}

	/**
	 * Lays out each child within the viewgroup, going from left to right.
	 */
	private void layoutLeft() {
		// DEV_DEBUG:
		// Log.d(TAG, "layoutLeft called.");

		int offset = mDisplayLeftOffset;
		final int childCount = getChildCount();

		for (int lv = 0; lv < childCount; lv++) {
			View child = getChildAt(lv);

			int childWidth = child.getMeasuredWidth();
			child.layout(offset, 0, offset + childWidth,
					child.getMeasuredHeight());
			cleanupLayoutState(child);
			offset += childWidth;

			// DEV_DEBUG:
			// HorizontalListViewDebug.logViewInfo(child, mFirstVisiblePosition
			// + lv);
		}

	}

	/**
	 * Fills the listview by scrolling views to the left by dx.
	 * 
	 * @param dx
	 *            how far to scroll left.
	 * @return true if it successfully scrolled, false if it would have
	 *         over-scrolled.
	 * 
	 *         TODO: Add a third state. Currently, the function returns true to
	 *         layout the list from the left. It returns false to layout the
	 *         list from the right. It might be worth having a third state which
	 *         says to do nothing when the user attempts to scroll left and
	 *         can't scroll any further.
	 */
	private boolean fillListLeft(int dx) {

		// if we switched direction, then recycle all views.
		// otherwise, views are shown in reverse order.
		if (!mFillListLeft) {
			recycleViewGroupViews();
		}

		mFillListLeft = true;

		int offset = mDisplayLeftOffset + dx;

		// index of where we are in the viewgroup.
		int viewGroupPosition = 0;

		while (true) {

			// position in adapter.
			int position = viewGroupPosition + mFirstVisiblePosition;

			// DEV_DEBUG:
			// Log.d(TAG, "fillListLeft (" + hashCode() + ") - position == " +
			// position);

			// get child & width.
			View child = getView(viewGroupPosition, position);
			int childWidth = measureChild(child);

			//
			// handle view that will not be visible.
			//
			if (!willBeVisible(mLeftVisibleBoundary, mRightVisibleBoundary, 0,
					childWidth, offset)) {

				removeViewInLayout(child);
				mRecycledViews.offer(child);

				// case: view is beyond right edge.
				// result: we are done.
				//
				// we have iterated through the children. the viewgroup has at
				// least one child displayed. this child won't be displayed.
				// we are done.
				if (viewGroupPosition > 0) {
					// DEV_DEBUG:
					// Log.d(TAG, "fillListLeft (" + hashCode() +
					// ") - break - view is beyond right edge.");

					break;
				}

				// case: view scrolled off to the left.
				// result: calculate left offset.
				//
				// if we just hid the left-most view, we need to recalculate
				// the display offset.
				if (viewGroupPosition == 0) {
					setLeftOffset(offset + childWidth);
				}

				// item scrolled off screen; therefore, increment first visible
				// position.
				++mFirstVisiblePosition;

				// case: we have gone past the last position.
				// result: fail.
				//
				// if no more views to display, we will return false, so that we
				// can populate list from right to left.
				if (mFirstVisiblePosition > mAdapterChildCount - 1) {

					// DEV_DEBUG:
					// Log.d(TAG, "fillListLeft (" + hashCode() +
					// ") - fail - gone past last position." +
					// " mFirstVisiblePosition == " + mFirstVisiblePosition +
					// " mAdapterChildCount - 1 == " + (mAdapterChildCount -
					// 1));

					onForceScrollFinish();
					mLastVisiblePosition = mAdapterChildCount - 1;
					return false;
				}

			} else {

				//
				// visible view.
				//

				// update last visible position and offset through each
				// iteration.
				mLastVisiblePosition = position;

				setRightOffset(mRightVisibleBoundary - (childWidth + offset));

				// if the view hasn't already been added, add it.
				if (getChildAt(viewGroupPosition) == null) {
					addViewInLayout(child, viewGroupPosition,
							getLayoutParams(child), true);
				}

				// case: modifying first visible item in the viewgroup.
				// result: save offset.
				if (viewGroupPosition == 0) {
					setLeftOffset(offset);
				}

				// case: all views are narrower than viewgroup.
				// result: we are done.
				//
				// -----------------------------+
				// |
				// |
				// | view |view | view |
				// |
				// |
				// -----------------------------+
				if (getFirstVisiblePosition() == 0
						&& (position == mAdapterChildCount - 1)
						&& (childWidth + offset) <= mRightVisibleBoundary) {

					// DEV_DEBUG:
					// Log.d(TAG, "fillListLeft (" + hashCode() +
					// ") - break - all views are narrow than ViewGroup");

					break;
				}

				// case: right edge of *displayed* view is invisible
				// or at at the edge.
				// result: we are done.
				//
				// -----------------------------+
				// |
				// ... | view | view |view | view | ...
				// |
				// ... | view |view | view |
				// |
				// |
				// -----------------------------+
				if ((childWidth + offset) >= mRightVisibleBoundary) {

					// DEV_DEBUG:
					// Log.d(TAG, "fillListLeft (" + hashCode() +
					// ") - break - right edge of " +
					// "*displayed* view is invisible or at " +
					// "the edge.");

					break;
				}

				// case: right edge of last view is too far left.
				// result: fail.
				//
				// if the last view is showing and will be to the left of the
				// viewgroup's right edge, we are done. we will return false, so
				// that we can populate list from right to left.
				//
				//
				// issue, dx shift too far left:
				// -----------------------------+
				// |
				// | view | view |view | <-- need to shift to the right.
				// |
				// -----------------------------+
				//
				// resolution, populate list from right to left:
				// -----------------------------+
				// |
				// <-- | view | view |view |
				// |
				// -----------------------------+
				if ((position == mAdapterChildCount - 1)
						&& (childWidth + offset < mRightVisibleBoundary)) {

					// DEV_DEBUG:
					// Log.d(TAG, "fillListLeft (" + hashCode() +
					// ") - fail - last view too far left.");

					return false;
				}

				++viewGroupPosition;
			}

			offset += childWidth;
		}

		// scrolled left successfully.
		return true;
	}

	/**
	 * Fills the list with views from the adapter. If we are standing still
	 * (dx=0) or scrolling left (dx<1), we fill the list from the left. If we
	 * are scrolling right (dx>0), we fill the list from the right.
	 * 
	 * @param dx
	 */
	private void fillList(final int dx) {

		// DEV_DEBUG:
		// Log.d(TAG, "fillList (" + hashCode() + ") - current values:\t" + dx +
		// "\t" + mCurrentX +
		// "\t" + mNextX +
		// "\t" + mDisplayLeftOffset +
		// "\t" + mDisplayRightOffset);

		if (dx <= 0) {

			//
			// scrolling left or standing still.
			//

			// if we succeeded, layout from the left.
			if (fillListLeft(dx)) {
				layoutLeft();
			} else {

				// if we failed, we have overscrolled.
				// consequently, fill list from the right with no offset.
				resetRightOffset();
				fillListRight(0);
				setRightOffset(0);
				layoutRight();
			}

		} else {

			//
			// scrolling right.
			//

			// if we succeeded, layout from the right.
			if (fillListRight(dx)) {
				layoutRight();
			} else {

				// if we failed, we have overscrolled.
				// consequently, fill list from the left with no offset.
				resetLeftOffset();
				fillListLeft(0);
				setLeftOffset(0);
				layoutLeft();
			}
		}

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		// if no adapter, nothing to do.
		if (mAdapter == null) {
			return;
		}

		// using boundarys of 0..width of viewgroup view to determine
		// visibility.
		mLeftVisibleBoundary = 0;
		mRightVisibleBoundary = getWidth();

		// data changed.
		if (mDataChanged) {

			final int oldNextX = mNextX;

			init();
			recycleViewGroupViews();
			mAdapterChildCount = mAdapter.getCount();

			// reset mMaxX, so re-calculating our total width can occur.
			mMaxX = 0;

			mNextX = oldNextX;
			mDataChanged = false;
		}

		// save nextX if scroller ran computation.
		if (mScroller.computeScrollOffset()) {
			mNextX = mScroller.getCurrX();

		}

		final int dx = mCurrentX - mNextX;

		// if flinging and there are listeners, pass along new distance and
		// add UI update into message queue. (assuming HorizontalListView is
		// scrolling with other views, and want all to move together smoothly.)
		//
		// otherwise, update UI immediately.
		//
		if ((mTouchMode == TouchMode.TOUCH_MODE_FLING)
				&& (mHorizontalListViewScrollListener != null)) {

			mHorizontalListViewScrollListener.onFling(this, dx);

			mFillListDxRunnable.dx = dx;
			post(mFillListDxRunnable);

		} else {
			fillList(dx);
		}

		mCurrentX = mNextX;

		// if scrolling is not finished, requestLayout().
		if (!mScroller.isFinished()) {
			post(mRequestLayoutRunnable);

		} else {

			mTouchMode = TouchMode.TOUCH_MODE_REST;

			// in order to have multiple immediate scrolls occur without
			// notifying
			// listener, delaying the notification.
			if (!mIdleNotificationSent) {
				postDelayed(mScrollStateIdleRunnable,
						SCROLL_STATE_IDLE_RUNNABLE_DELAY);
				mIdleNotificationSent = true;
			}
		}

	}

	/**
	 * Determines if a view shifted (left/right) via dx will be visible.
	 * 
	 * @param leftVisibleBoundary
	 *            left visible coordinate.
	 * @param rightVisibleBoundary
	 *            right visible coordinate.
	 * @param view
	 *            view to be used.
	 * @param dx
	 *            distance shifted.
	 * @return true if child will be visible, false otherwise.
	 * 
	 *         TODO: Possibly replace with Rect/contains() and use similarly to
	 *         onSingleTapUp().
	 */
	private boolean willBeVisible(final int leftVisibleBoundary,
			final int rightVisibleBoundary, final int childLeft,
			final int childRight, final int dx) {

		final int newLeft = childLeft + dx;
		final int newRight = childRight + dx;

		// true for three cases:
		//
		// 1) view's left edge is between the visible boundary:
		// +------------------------+
		// | |
		// | | view |
		// | |
		// +------------------------+
		//
		// 2) view's right edge is between the visible boundary:
		// +------------------------+
		// | |
		// | view |
		// | |
		// +------------------------+
		//
		// 3) view is larger than viewgroup:
		// +------------------------+
		// | |
		// | view |
		// | |
		// +------------------------+
		final boolean visible = (((newLeft >= leftVisibleBoundary) && (newLeft <= rightVisibleBoundary))
				|| ((newRight >= leftVisibleBoundary) && (newRight <= rightVisibleBoundary)) || ((newLeft < leftVisibleBoundary) && (newRight > rightVisibleBoundary)));

		// DEV_DEBUG:
		// Log.d(TAG, "willBeVisible == " + visible);

		return visible;
	}

	public synchronized void setNextX(int nextX) {
		mNextX = nextX;
	}

	public synchronized int getNextX() {
		return mNextX;
	}

	// TODO: what to do here? throw exception until coded?
	// public void scrollTo(int x) {
	// mScroller.startScroll(mNextX, 0, x - mNextX, 0);
	//
	// // TODO: handle scroll listener too?
	// // don't think I saw this ever called...
	// requestLayout();
	// }

	@Override
	public int getFirstVisiblePosition() {
		return mFirstVisiblePosition;
	}

	@Override
	public int getLastVisiblePosition() {
		return mLastVisiblePosition;
	}

	/**
	 * Fires an "on scroll state changed" event to the registered
	 * {@link android.widget.AbsListView.OnScrollListener}, if any. The state
	 * change is fired only if the specified state is different from the
	 * previously known state.
	 * 
	 * @param newState
	 *            The new scroll state.
	 */
	private void reportScrollStateChange(int newState) {
		if (newState != mLastScrollState) {
			if (mHorizontalListViewScrollListener != null) {

				mHorizontalListViewScrollListener.onScrollStateChanged(this,
						newState);
				mLastScrollState = newState;
			}
		}
	}

	/**
	 * Flings a view by distanceX. Uses the message queue to keep movement in
	 * sequence.
	 */
	public void onFling(final float distanceX) {

		// do NOT change state of mTouchMode here. it is handled in the
		// flingRunnable
		// started by the gesture. it is assumed that this method will be
		// invoked
		// by the something like a HorizontalScrollDispatcher to "fling"
		// multiple
		// HorizontalListViews together.

		// DEV_DEBUG:
		// Log.d(TAG, "(" + hashCode() + ") onFling(dx), distanceX == " + (int)
		// distanceX +
		// " mNextX == " + mNextX);

		mRequestLayoutDxRunnable.distanceX = distanceX;
		post(mRequestLayoutDxRunnable);
	}

	/**
	 * Override in a "monitor" HorizontalListView to know when the state
	 * changed.
	 * 
	 * @param view
	 * @param scrollState
	 */
	public void onScrollStateChanged(HorizontalListView2 view, int scrollState) {
		// do nothing.
	}

	/**
	 * Override in a "monitor" HorizontalListView to know when the overscroll
	 * occurred.
	 * 
	 * @param view
	 */
	public void onOverScrolledLeft(HorizontalListView2 view) {
		// do nothing.
	}

	/**
	 * Override in a "monitor" HorizontalListView to know when the overscroll
	 * occurred.
	 * 
	 * @param view
	 */
	public void onOverScrolledRight(HorizontalListView2 view) {
		// do nothing.
	}

	/**
	 * Scrolls the view by distanceX.
	 * 
	 * @param distanceX
	 * @return true that it handled the event.
	 */
	public boolean onScroll(float distanceX) {

		mTouchMode = TouchMode.TOUCH_MODE_SCROLL;

		// DEV_DEBUG:
		// Log.d(TAG, "(" + hashCode() + ") onScroll(dx), distanceX == " + (int)
		// distanceX +
		// " mNextX == " + mNextX);

		mNextX += (int) distanceX;

		// want immediate UI update as we are assuming user is interactively
		// scrolling.
		requestLayout();

		return true;
	}

	/**
	 * Stop scrolling immediately. Reset the state.
	 */
	public void onForceScrollFinish() {
		mScroller.forceFinished(true);

		mTouchMode = TouchMode.TOUCH_MODE_REST;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return mGesture.onTouchEvent(ev);
	}

	/**
	 * Receives various gesture events (scroll, fling, single tap).
	 */
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		/**
		 * Receives an onDown event. Treat it as a "stop" event, as in the view
		 * may have been scrolling, and we want to stop scrolling immediately.
		 */
		@Override
		public boolean onDown(MotionEvent e) {

			// DEV_DEBUG:
			// Log.d(TAG, "GESTURE - onDown");

			clearIdleNotification();
			mTouchMode = TouchMode.TOUCH_MODE_DOWN;

			HorizontalListView2.this.onForceScrollFinish();

			// if we have a listener, notify it.
			if (mHorizontalListViewScrollListener != null) {
				mHorizontalListViewScrollListener
						.onForceScrollFinish(HorizontalListView2.this);
			}

			return true;
		}

		/**
		 * Flings data horizontally. Changes state, mTouchMode =
		 * TouchMode.TOUCH_MODE_FLING.
		 * 
		 * @param e1
		 *            Motion event where the event began.
		 * @param e2
		 *            Motion event where the event ended.
		 * @param velocityX
		 *            Horizontal velocity.
		 * @param velocityY
		 *            Vertical velocity.
		 * 
		 *            TODO: There can be multiple scroll events prior to a fling
		 *            event: user drags his finger and eventually "flings."
		 *            Possibly modify this and onScroll() so that scroll events
		 *            finish prior to "fling." Or possibly postDelay() initial
		 *            fling event if scroll is unfinished.
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			// DEV_DEBUG:
			// Log.d(TAG, "GESTURE - onFling, velocityX == " + velocityX);
			// Log.d(TAG, "GESTURE - onFling, mTouchMode == " + mTouchMode);
			// Log.d(TAG, "GESTURE - onFling\tdx" + "\tmCurrentX" + "\tmNextX" +
			// "\tmDisplayLeftOffset" + "\tmDisplayRightOffset");

			// calculate entire width of views in adapter in order for
			// scroll/fling to
			// work correctly.
			if (mMaxX == 0) {
				measureMaxX(mWidthMeasureSpec);
			}

			// trying to keep velocity < 0 == scroll left.
			velocityX = -velocityX;

			// if scrolling left/standing still and already scrolled as far left
			// as
			// possible
			// or
			// if scrolling right and already scrolled as far right as possible
			// return.
			if (((velocityX >= 0) && (!canScrollLeft()))
					|| ((velocityX < 0) && (!canScrollRight()))) {

				// DEV_DEBUG:
				// Log.d(TAG,
				// "GESTURE - onFling - view scrolled as far left/right as possible. ignoring fling.");

				onForceScrollFinish();
				return true;
			}

			//
			// want all fling events to occur via the handler/thread message
			// queue.
			//

			// DEV_DEBUG:
			// Log.d(TAG, "GESTURE - onFling, posting fling runnable.");

			if (mFlingRunnable == null) {
				mFlingRunnable = new FlingRunnable();
			}

			mFlingRunnable.setMotionEvent1(e1);
			mFlingRunnable.setMotionEvent2(e2);
			mFlingRunnable.setVelocityX(velocityX);
			mFlingRunnable.setVelocityY(velocityY);

			removeCallbacks(mFlingRunnable);
			post(mFlingRunnable);

			return true;
		}

		/**
		 * Scrolls view horizontally. Changes state, mTouchMode =
		 * TOUCH_MODE_SCROLL. *
		 * 
		 * @param e1
		 *            Motion event where the event began.
		 * @param e2
		 *            Motion event where the event ended.
		 * @param distanceX
		 *            Horizontal distance.
		 * @param distanceY
		 *            Vertical distance.
		 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			// DEV_DEBUG:
			// Log.d(TAG, "GESTURE - onScroll, distanceX == " + (int) distanceX
			// +
			// " distanceY == " + (int) distanceY);

			// calculate entire width of views in adapter in order for
			// scroll/fling to
			// work correctly.
			if (mMaxX == 0) {
				measureMaxX(mWidthMeasureSpec);
			}

			// if user is scrolling more vertically than horizontally,
			// we will ignore/defer scroll to parent.
			//
			// if multiple HorizontalListView reside in a (vertical) ListView,
			// this allows the ListView to scroll vertically.
			if (Math.abs(distanceY) > Math.abs(distanceX)) {
				// DEV_DEBUG:
				// Log.d(TAG,
				// "GESTURE - onScroll, vertical scroll exceeds horizontal scroll. ignoring scroll.");

				return false;
			}

			mTouchMode = TouchMode.TOUCH_MODE_SCROLL;

			final boolean overScrollRight = (distanceX >= 0f)
					&& (!canScrollLeft());
			final boolean overScrollLeft = (distanceX < 0f)
					&& (!canScrollRight());

			// if scrolling left and already scrolled as far left as possible
			// or
			// if scrolling right and already scrolled as far right as possible
			// return.
			if (overScrollLeft || overScrollRight) {

				// DEV_DEBUG:
				// Log.d(TAG,
				// "GESTURE - onScroll, view scrolled as far left/right as possible. ignoring scroll");

				onForceScrollFinish();

				// send out applicabale overscroll notification if listener
				// registered.
				if (mHorizontalListViewScrollListener != null) {
					if (overScrollLeft) {
						mHorizontalListViewScrollListener
								.onOverScrolledLeft(HorizontalListView2.this);
					} else {
						mHorizontalListViewScrollListener
								.onOverScrolledRight(HorizontalListView2.this);
					}
				}

				return true;
			}

			// DEV_DEBUG:
			// Log.d(TAG, "GESTURE - onScroll, distanceX == " + (int) distanceX
			// +
			// " mNextX == " + mNextX +
			// " mMaxX == " + mMaxX +
			// " mNextX + distanceX == " + (int) (mNextX + distanceX));

			clearIdleNotification();

			//
			// need to handle overscrolling.
			//

			// if distanceX would cause us to overscroll left,
			// we align with left edge.
			if (mNextX + distanceX < 0) {

				distanceX = -mNextX;

			} else if (mNextX + distanceX > mMaxX) {

				// if distanceX would cause us to overscroll right,
				// we align with right edge.

				distanceX = mMaxX - mNextX;
			}

			reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);

			boolean handledEvent = HorizontalListView2.this.onScroll(distanceX);

			// if we have a listener, notify it.
			if (mHorizontalListViewScrollListener != null) {
				mHorizontalListViewScrollListener.onScroll(
						HorizontalListView2.this, e1, e2, distanceX, distanceY);
			}

			return handledEvent;
		}

		/**
		 * Handles a single tap, and calls registered listeners:
		 * OnItemClickListener and OnItemSelectedListener. Changes state,
		 * mTouchMode == TOUCH_MODE_SINGLE_TAP.
		 * 
		 * @param e1
		 *            Motion event where the event began.
		 * @param e2
		 *            Motion event where the event ended.
		 * @param distanceX
		 *            Horizontal distance.
		 * @param distanceY
		 *            Vertical distance.
		 * 
		 * 
		 */
		@Override
		public boolean onSingleTapUp(MotionEvent e) {

			clearIdleNotification();
			mTouchMode = TouchMode.TOUCH_MODE_SINGLE_TAP;

			Rect viewRect = new Rect();

			for (int lv = 0; lv < getChildCount(); lv++) {

				View child = getChildAt(lv);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);

				int position = mFirstVisiblePosition + lv;

				if (viewRect.contains((int) e.getX(), (int) e.getY())) {

					if (mOnItemClickedListener != null) {
						mOnItemClickedListener.onItemClick(
								HorizontalListView2.this, child, position,
								mAdapter.getItemId(position));
					}

					// TODO: test this further.
					if (mOnItemSelectedListener != null) {
						mOnItemSelectedListener.onItemSelected(
								HorizontalListView2.this, child, position,
								mAdapter.getItemId(position));
					}

					break;
				}

			}
			return true;
		}

	};

	/**
	 * Provides very crude debug information. Modify as desired.
	 * 
	 * @return Crude debug info.
	 */
	@Override
	public String toString() {

		return
		// "mAdapterChildCount\t" +
		// "mLeftVisibleBoundary\t" +
		// "mRightVisibleBoundary\t" +
		// "mMaxX\t" +
		// "mCurrentX\t" +
		// "mNextX\t" +
		// "mDisplayLeftOffset\t" +
		// "mDisplayRightOffset\t" +
		// "mDisplayLeftOffsetSet\t" +
		// "mDisplayRightOffsetSet\t" +
		// "mFirstVisiblePosition\t" +
		// "mLastVisiblePosition\t" +
		// "\n" +

		// mAdapterChildCount + "\t" +
		// mLeftVisibleBoundary + "\t" +
		// mRightVisibleBoundary +"\t" +
		mMaxX + "\t" + mCurrentX + "\t" + mNextX + "\t" + mDisplayLeftOffset
				+ "\t" + mDisplayRightOffset + "\t" + mDisplayLeftOffsetSet
				+ "\t" + mDisplayRightOffsetSet + "\t" + mFirstVisiblePosition
				+ "\t" + mLastVisiblePosition + "\t" + "";
	}

	/**
	 * Debug method to return mMaxX.
	 * 
	 * @return
	 */
	public int getMaxX() {

		// can also force calculation before returning:
		// mMaxX = 0;
		// measureMaxX(mWidthMeasureSpec);

		return mMaxX;
	}

}
