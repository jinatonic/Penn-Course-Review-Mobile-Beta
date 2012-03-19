package edu.upenn.cis.cis350.database;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

/**
 * Helper class to access Android SQLite database and store the data for auto text completion
 * The table is populated asynchronously upon application start if it doesn't exist already
 * @author Jinyan
 *
 */

public class AutoCompleteDB {
	
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	/* Database and table names */
	private static final String DATABASE_NAME = "AutoComplete";
	private static final String AUTOCOMPLETE_TABLE = "AutoCompleteEntries";
	private static final int DATABASE_VERSION = 2;
	
	/* Query strings */
	private static final String AUTOCOMPLETE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + AUTOCOMPLETE_TABLE + " (" +
			"path char(50)," +
			"name char(50)," +
			"course_id char(50)," +
			"type int NOT NULL," +		// 0 - course, 1 - instructor, 2 - department
			"year int NOT NULL)";	
	
	/* TAG for logging purposes */
	private static final String TAG = "AutoComplete";

	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(AUTOCOMPLETE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + AUTOCOMPLETE_TABLE);
            onCreate(db);
        }
    }
	
	public AutoCompleteDB(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public AutoCompleteDB open() throws SQLException {
		Log.w(TAG, "Opening AutoComplete");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.execSQL(AUTOCOMPLETE_TABLE_CREATE);
		return this;
	}
	
	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing AutoComplete");
		mDbHelper.close();
	}
	
	/**
	 * Adds the given list of entries into the database
	 * @param keywords
	 */
	public void addEntries(ArrayList<KeywordMap> keywords) {
		for (KeywordMap keyword : keywords) {
			Log.w(TAG, "adding course " + keyword.getAlias() + " to database");
			
			// First we add to the course table 
			ContentValues values = new ContentValues();
			
			values.put("path", keyword.getPath());
			values.put("name", keyword.getName());
			values.put("course_id", keyword.getAlias());
			values.put("type", (keyword.getType() == Type.COURSE) ? 0 : (keyword.getType() == Type.INSTRUCTOR) ? 1 : 2);
			values.put("year", Calendar.getInstance().get(Calendar.YEAR));
			
			if (mDb.insert(AUTOCOMPLETE_TABLE, null, values) == -1) 
				Log.w(TAG, "Failed to insert new course into table");
		}
	}
	
	/**
	 * Queries and tries to complete the given keyword based on current table
	 * @param keyword
	 */
	public void checkAutocomplete(String keyword) {
		String query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE name LIKE '%" +
						keyword + "%' OR course_id LIKE '%" + keyword + "%' LIMIT 10";
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		do {
			String name = c.getString(c.getColumnIndex("name"));
			String course_id = c.getString(c.getColumnIndex("course_id"));
			Log.w("AUTOCOMPLETE", "Found " + course_id + " - " + name);
		} while (c.moveToNext());
	}
	
	/**
	 * Query the database and returns if updates are needed
	 * @return true if autocomplete DB needs to be updated, false otherwise
	 */
	public boolean updatesNeeded() {
		Cursor c = mDb.rawQuery("SELECT * FROM " + AUTOCOMPLETE_TABLE + " LIMIT 1", null);
		c.moveToFirst();
		if (c.getCount() == 1) {
			int year = c.getInt(c.getColumnIndex("year"));
			return year != Calendar.getInstance().get(Calendar.YEAR);
		}
		else return true;
	}
	
	/** 
	 * Delete all entries from table
	 */
	public void resetTables() {
		Log.w(TAG, "Resetting the AutoComplete table");
		mDb.execSQL("DELETE FROM " + AUTOCOMPLETE_TABLE + " WHERE year > -1");
	}
}
