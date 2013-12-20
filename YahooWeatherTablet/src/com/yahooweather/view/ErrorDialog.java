package com.yahooweather.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yahooweather.R;

/**
 * Show error in dialog
 * 
 * @author ChungPV1
 * 
 */
public class ErrorDialog extends Dialog {
	private static ErrorDialog sDialogInstance;
	private Button mBtnOk;
	private TextView mTvMessage;

	private ErrorDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.yw_dialog_error);
		mTvMessage = (TextView) findViewById(R.id.yw_tvDialogError_Message);
		mBtnOk = (Button) findViewById(R.id.yw_btDialogError_OK);
		mBtnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public static synchronized ErrorDialog getInstance(Context context) {
		if (sDialogInstance == null) {
			sDialogInstance = new ErrorDialog(context);
		}

		return sDialogInstance;
	}

	public void show(String message) {
		if (mTvMessage != null && message != null) {
			mTvMessage.setText(message);
		}
		show();
	}

	public void show(Exception ex) {
		if (mTvMessage != null && ex != null && ex.getMessage() != null) {
			mTvMessage.setText(ex.getMessage());
		}
		show();
	}

	public static void clearInstance() {
		sDialogInstance = null;
	}
}
