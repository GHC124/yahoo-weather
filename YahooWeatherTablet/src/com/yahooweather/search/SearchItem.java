package com.yahooweather.search;

public class SearchItem {
	private String mText;
	private String mHtml;
	private String mWoeid;

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}
	
	public String getHtml() {
		return mHtml;
	}

	public void setHtml(String html) {
		mHtml = html;
	}

	public String getWoeid() {
		return mWoeid;
	}

	public void setWoeid(String woeid) {
		mWoeid = woeid;
	}

	@Override
	public String toString() {
		return mText;
	}

}
