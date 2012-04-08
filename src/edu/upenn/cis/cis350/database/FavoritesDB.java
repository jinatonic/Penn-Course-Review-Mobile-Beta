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

public class FavoritesDB {
	
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	/* Database and table names */
	private static final String DATABASE_NAME = "ResultsCache";
	private static final String FAVORITES_TABLE = "Favorites";
	private static final int DATABASE_VERSION = 2;
	
	/* Query strings */
	private static final String FAVORITES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + FAVORITES_TABLE + " (" +
			"s_id integer PRIMARY KEY," +
			"keyword char(50) NOT NULL)";
	
	/* TAG for logging purposes */
	private static final String TAG = "FavoritesDB";

	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FAVORITES_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE);
            onCreate(db);
        }
    }
	
	public FavoritesDB(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public FavoritesDB open() throws SQLException {
		Log.w(TAG, "Opening FavoritesTable");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.execSQL(FAVORITES_TABLE_CREATE);
		return this;
	}
	
	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing FavoritesTable");
		mDbHelper.close();
	}
	
	/** 
	 * Delete all entries from all tables in database (for testing purposes)
	 */
	public void resetTables() {
		Log.w(TAG, "Resetting database tables");
		mDb.execSQL("DROP TABLE IF EXISTS "+ FAVORITES_TABLE);
	}
	
	/**
	 * Get the next integer for primary key
	 */
	private int getNextPK() {
		String query = "SELECT s_id FROM " + FAVORITES_TABLE + " ORDER BY s_id DESC LIMIT 1";
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
		
		String word;
		if (keywordmap.getType() == Type.COURSE) {
			word = Constants.COURSE_TAG + keywordmap.getAlias() + " - " + keywordmap.getName();
		}
		else if (keywordmap.getType() == Type.DEPARTMENT) {
			word = Constants.DEPARTMENT_TAG + keywordmap.getAlias() + " - " + keywordmap.getName();
		}
		else {
			word = Constants.INSTRUCTOR_TAG + keywordmap.getName();
		}
		
		// First try to delete the keyword in DB if already exists in DB
		int nextpk = getNextPK();
		
		ContentValues values = new ContentValues();
		values.put("s_id", nextpk);
		values.put("keyword", word);
		
		if (mDb.insert(FAVORITES_TABLE, null, values) == -1)
			Log.w(TAG, "Failed to insert new keyword into table");
	}
	
	/**
	 * Returns a list of keywords ordered by how recently it was accessed
	 */
	public String[] getKeywords() {
		ArrayList<String> rs = new ArrayList<String>();
		String query = "SELECT DISTINCT keyword FROM " + FAVORITES_TABLE + " ORDER BY s_id DESC LIMIT 50";
		
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		
		if (c.getCount() > 0) {
			do {
				String keyword = c.getString(0);
				
				rs.add(keyword);
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
