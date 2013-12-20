package com.yahooweather.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yahooweather.data.entity.Location;

public class LocationDao {
	public static final String TABLE_NAME = "Location";
	public static final String ID = "id";
	public static final String WOEID = "woeid";
	public static final String NAME = "name";

	private SQLiteHelper sqLiteHelper;

	public LocationDao(Context context) {
		sqLiteHelper = new SQLiteHelper(context);
	}

	public List<Location> getListLocation() {
		List<Location> listDto = new ArrayList<Location>();
		String sql = "Select * from " + TABLE_NAME;
		try {
			SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				do {
					Location location = new Location();
					location.setId(cursor.getInt(0));
					location.setWoeid(cursor.getString(1));
					location.setName(cursor.getString(2));
					// Adding obj to list
					listDto.add(location);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.getMessage();
		}
		return listDto;
	}

	public void insertLocation(Location location) {
		SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(WOEID, location.getWoeid());
		values.put(NAME, location.getName());
		// Check if row already existed in database
		int idExist = isLocationExists(db, location.getWoeid());
		if (idExist == 0) {
			int id = (int) db.insert(TABLE_NAME, null, values);
			location.setId(id);
		} else {
			location.setId(idExist);
			updateLocation(location);
		}
		db.close();
	}

	/**
	 * Updating a single row row will be identified by id
	 * */
	public int updateLocation(Location location) {
		SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(WOEID, location.getWoeid());
		values.put(NAME, location.getName());
		// updating row return
		int update = db.update(TABLE_NAME, values, ID + " = ?",
				new String[] { String.valueOf(location.getId()) });
		db.close();
		return update;

	}

	/**
	 * Reading a row is identified by row id
	 * */
	public Location getLocation(int id) {
		Location location = new Location();
		SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { ID, WOEID, NAME },
				ID + "=?", new String[] { String.valueOf(id) }, null, null,
				null, null);
		if (cursor != null && cursor.moveToFirst()) {
			location.setId(cursor.getInt(0));
			location.setWoeid(cursor.getString(2));
			location.setName(cursor.getString(1));
		}
		cursor.close();
		db.close();
		return location;
	}

	/**
	 * Deleting single row
	 * */
	public int deleteLocation(int id) {
		SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
		int rows = db.delete(TABLE_NAME, ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();

		return rows;
	}

	/**
	 * Checking whether a channel is already existed, check is done by matching
	 * read id
	 * */
	public int isLocationExists(SQLiteDatabase db, String woeid) {
		Cursor cursor = db.query(TABLE_NAME, new String[] { WOEID }, woeid
				+ "='" + woeid + "'", null, null, null, null, null);
		int id = 0;
		if (cursor != null && cursor.moveToFirst()) {
			id = cursor.getInt(0);
		}

		return id;
	}
}
