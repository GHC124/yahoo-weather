package com.yahooweather.util;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * Use to display an image on the right of the EditTextView and handle event on
 * that image.
 * 
 * @author ChungPV1
 * 
 */
public abstract class EditTextViewUtil implements OnTouchListener {

	private static final int FUZZ = 6;

	private final Drawable mDrawable;

	/**
	 * Constructor of EdiTextViewUtil. Receive edit text need to be inserted
	 * search clear image.
	 * 
	 * @param view
	 *            EditText view need to be inserted search clear image
	 */
	public EditTextViewUtil(TextView view) {
		super();
		final Drawable[] drawables = view.getCompoundDrawables();

		// If drawables is not Null and have an image.
		if (drawables != null && drawables.length == 4) {
			// Get image on the right
			mDrawable = drawables[2];

		} else {
			mDrawable = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Display image on the right when user typing and image is available
		if (event.getAction() == MotionEvent.ACTION_DOWN && mDrawable != null) {

			// Get x and y coordinate where user touch
			int x = (int) event.getX();
			int y = (int) event.getY();

			// Get the bounds rect of image
			Rect bounds = mDrawable.getBounds();

			int right = v.getWidth() - v.getPaddingRight();

			// We need to make sure that user touch on the image
			if (x >= (right - bounds.width() - FUZZ) && x <= (right + FUZZ)
					&& y >= (v.getPaddingTop() - FUZZ)
					&& y <= (v.getHeight() - v.getPaddingBottom()) + FUZZ) {
				return onDrawableTouch(event);
			}
		}
		return false;
	}

	public abstract boolean onDrawableTouch(final MotionEvent event);
}
