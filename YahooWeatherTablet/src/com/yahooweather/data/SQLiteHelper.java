package com.yahooweather.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "YW_Data";
	private static final int DATABASE_VERSION = 1;

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createLocation = "CREATE TABLE " + LocationDao.TABLE_NAME + "("
				+ LocationDao.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ LocationDao.WOEID + " TEXT," + LocationDao.NAME + " TEXT)";

		db.execSQL(createLocation);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + LocationDao.TABLE_NAME);

		// Create tables again
		onCreate(db);
	}
}
