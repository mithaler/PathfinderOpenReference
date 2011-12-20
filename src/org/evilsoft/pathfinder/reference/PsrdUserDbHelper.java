package org.evilsoft.pathfinder.reference;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PsrdUserDbHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "psrd_user_db";
	private static final int DATABASE_VERSION = 1;

	PsrdUserDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createCollectionTable());
		db.execSQL(createCollectionEntryTable());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	private String createCollectionTable() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE collections(");
		sb.append(" collection_id INTEGER PRIMARY KEY,");
		sb.append(" name TEXT");
		sb.append(")");
		return sb.toString();
	}

	private String createCollectionEntryTable() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE collection_entries(");
		sb.append(" collection_entry_id INTEGER PRIMARY KEY,");
		sb.append(" collection_id INTEGER,");
		sb.append(" section_id INTEGER,");
		sb.append(" name TEXT,");
		sb.append(" path TEXT");
		sb.append(")");
		return sb.toString();
	}
}
