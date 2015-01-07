package com.beiluoshimen.securityguard.lock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * used with AppLockDB.
 * 
 * Note:
 * this class is no use anymore. Use AppLockDBProvider
 * we use AppLockDBProvider instead.
 * 2014/12/22 modified by Hsieh Yu-Hua
 * 
 * @deprecated
 * @author Hsieh Yu-Hua
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper{

	public AppLockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applock (_id integer primary key autoincrement,packname varchar(25))");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}



}
