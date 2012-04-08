package edu.upenn.cis.cis350.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

/**
 * Helper class to access Android SQLite database and store the recently searched data for fast access
 * Old data is deleted on application start (e.g. a week old)
 * @author Jinyan
 *
 */

public class RecentSearches {
	
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	/* Database and table names */
	private static final String DATABASE_NAME = "ResultsCache";
	private static final String SEARCHES_TABLE = "RecentSearches";
	private static final int DATABASE_VERSION = 2;
	
	/* Query strings */
	private static final String SEARCHES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + SEARCHES_TABLE + " (" +
			"s_id integer PRIMARY KEY," +
			"keyword char(50) NOT NULL," +
			"type int NOT NULL)";
	
	/* TAG for logging purposes */
	private static final String TAG = "RecentSearches";

	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SEARCHES_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SEARCHES_TABLE);
            onCreate(db);
        }
    }
	
	public RecentSearches(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public RecentSearches open() throws SQLException {
		Log.w(TAG, "Opening CourseSearchCache");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.execSQL(SEARCHES_TABLE_CREATE);
		return this;
	}
	
	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing CourseSearchCache");
		mDbHelper.close();
	}
	
	/** 
	 * Delete all entries from all tables in database (for testing purposes)
	 */
	public void resetTables() {
		Log.w(TAG, "Resetting database tables");
		mDb.execSQL("DELETE FROM " + SEARCHES_TABLE + " WHERE type > -1");
		mDb.execSQL("DROP TABLE IF EXISTS "+ SEARCHES_TABLE);
	}
	
	/**
	 * Checks if a given keyword exists in the table
	 * @param keyword
	 * @return true if matched something in db, false otherwise
	 */
	private void deleteIfExistsInDB(String keyword) {
		// First try to match based on course alias
		keyword = keyword.toLowerCase();
		String query = "DELETE FROM " + SEARCHES_TABLE + " WHERE LOWER(keyword)='" + keyword + "'";
		mDb.execSQL(query);
	}
	
	/**
	 * Get the next integer for primary key
	 */
	private int getNextPK() {
		String query = "SELECT s_id FROM " + SEARCHES_TABLE + " ORDER BY s_id DESC LIMIT 1";
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		
		if (c.getCount() > 0) {
			int currPK = c.getInt(c.getColumnIndex("s_id"));
			return currPK + 1;
		}
		else
			return 1;
	}
	
	/**
	 * Adds the given keyword to the database
	 * @param keyword
	 * @param type
	 */
	public void addKeyword(KeywordMap keywordmap) {
		Log.w(TAG, "Adding " + keywordmap.getAlias() + " - " + keywordmap.getName() + " to database");
		
		int type;
		String word;
		if (keywordmap.getType() == Type.COURSE) {
			type = 0;
			word = Constants.COURSE_TAG + keywordmap.getAlias() + " - " + keywordmap.getName();
		}
		else if (keywordmap.getType() == Type.DEPARTMENT) {
			type = 2;
			word = Constants.DEPARTMENT_TAG + keywordmap.getAlias() + " - " + keywordmap.getName();
		}
		else {
			type = 1;
			word = Constants.INSTRUCTOR_TAG + keywordmap.getName();
		}
		
		// First try to delete the keyword in DB if already exists in DB
		deleteIfExistsInDB(word);
		int nextpk = getNextPK();
		
		ContentValues values = new ContentValues();
		values.put("s_id", nextpk);
		values.put("keyword", word);
		values.put("type", type);
		
		if (mDb.insert(SEARCHES_TABLE, null, values) == -1)
			Log.w(TAG, "Failed to insert new keyword into table");
	}
	
	/**
	 * Returns a list of keywords ordered by how recently it was accessed
	 */
	public String[] getKeywords() {
		ArrayList<String> rs = new ArrayList<String>();
		String query = "SELECT * FROM " + SEARCHES_TABLE + " ORDER BY s_id DESC LIMIT 50";
		
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		
		if (c.getCount() > 0) {
			do {
				String keyword = c.getString(1);
				int type = c.getInt(2);
				String enum_type = (type == 0) ? Constants.COURSE_TAG : (type == 1) ? Constants.INSTRUCTOR_TAG : Constants.DEPARTMENT_TAG;
				
				rs.add(enum_type + keyword);
			} while (c.moveToNext());
			
			String[] result = new String[rs.size()];
			Log.w(TAG, "RecentSearches returned " + rs.size() + " results");
			return rs.toArray(result);
		}
		else {
			Log.w(TAG, "RecentSearches returned no results");
			return new String[0];
		}
	}
}
