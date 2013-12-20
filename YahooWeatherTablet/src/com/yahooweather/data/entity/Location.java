package com.yahooweather.data.entity;

public class Location {
	private int mId;
	private String mWoeid;
	private String mName;

	public Location() {

	}

	public Location(int id, String woeid, String name) {
		super();
		mId = id;
		mWoeid = woeid;
		mName = name;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getWoeid() {
		return mWoeid;
	}

	public void setWoeid(String woeid) {
		mWoeid = woeid;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

}
