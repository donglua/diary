package com.hdlu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private SQLiteDatabase db;
	private String table = "DIARY";

	public DBHelper(Context context) {
		super(context, "diary.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "
				+ table
				+ " (_id INTEGER not null PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(50),CONTENT TEXT, DT TIMESTAMP default (datetime('now', 'localtime')));";
		db.execSQL(sql);
	}

	public SQLiteDatabase getDb() {
		if (null == db || !db.isOpen())
			db = this.getWritableDatabase();
		return db;
	}

	public void insert(ContentValues values) {
		getDb().insert(table, null, values);
	}

	public void update(ContentValues values, String id) {
		getDb().update(table, values, "_id=?", new String[] { id });
	}

	public void delete(String _id) {
		getDb().delete(table, "_id=?", new String[] { _id });
	}

	public Cursor queryAll() {
		Cursor cur = getDb().rawQuery("select * from " + table, null);
		return cur;
	}

	public Cursor query(String sql, String[] args) {
		Cursor cur = getDb().rawQuery(sql, args);
		return cur;
	}

	@Override
	public synchronized void close() {
		db.close();
		super.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
