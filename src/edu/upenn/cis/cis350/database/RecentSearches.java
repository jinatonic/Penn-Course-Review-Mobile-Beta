package edu.upenn.cis.cis350.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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

public class RecentSearches extends DatabaseHelperClass {
	
	/* TAG for logging purposes */
	private static final String TAG = "RecentSearches";
	
	public RecentSearches(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public RecentSearches open() throws SQLException {
		Log.w(TAG, "Opening RecentSearches");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.execSQL(SEARCHES_TABLE_CREATE);
		mDb.execSQL(FAVORITES_TABLE_CREATE);
		return this;
	}
	
	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing RecentSearches");
		mDbHelper.close();
	}
	
	/** 
	 * Delete all entries from all tables in database (for testing purposes)
	 * @param type - 0 for recent, 1 for favorites
	 */
	public void resetTables(int type) {
		Log.w(TAG, "Resetting database tables");
		if (type == 0) {
			mDb.execSQL("DROP TABLE IF EXISTS " + SEARCHES_TABLE);
			mDb.execSQL(SEARCHES_TABLE_CREATE);
		}
		else {
			mDb.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE);
			mDb.execSQL(FAVORITES_TABLE_CREATE);
		}
	}
	
	/**
	 * Get the next integer for primary key
	 * @param type - 0 for recent, 1 for favorites
	 */
	private int getNextPK(int type) {
		String table = (type == 0) ? SEARCHES_TABLE : FAVORITES_TABLE;
		String query = "SELECT s_id FROM " + table + " ORDER BY s_id DESC LIMIT 1";
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
	 * @param keywordmap
	 * @param type - 0 for recent, 1 for favorites
	 */
	public void addKeyword(KeywordMap keywordmap, int type) {
		Log.w(TAG, "Adding " + keywordmap.getAlias() + " - " + keywordmap.getName() + " to database " + type);
		
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
		
		int nextpk = getNextPK(type);
		
		ContentValues values = new ContentValues();
		values.put("s_id", nextpk);
		values.put("keyword", word);
		
		String table = (type == 0) ? SEARCHES_TABLE : FAVORITES_TABLE;
		
		if (mDb.insert(table, null, values) == -1)
			Log.w(TAG, "Failed to insert new keyword into table");
	}
	
	/**
	 * Overloaded for taking in a string argument
	 * Adds the given keyword to the database
	 * @param keyword
	 * @param type - 0 for recent, 1 for favorites
	 */
	public void addKeyword(String keyword, int type) {
		Log.w(TAG, "Adding " + keyword + " to database " + type);
		
		int nextpk = getNextPK(type);
		
		ContentValues values = new ContentValues();
		values.put("s_id", nextpk);
		values.put("keyword", keyword);
		
		String table = (type == 0) ? SEARCHES_TABLE : FAVORITES_TABLE;
		
		if (mDb.insert(table, null, values) == -1)
			Log.w(TAG, "Failed to insert new keyword into table");
	}
	
	/**
	 * Returns a list of keywords ordered by how recently it was accessed
	 * @param type - 0 for recent, 1 for favorites
	 */
	public String[] getKeywords(int type) {
		ArrayList<String> rs = new ArrayList<String>();
		String table = (type == 0) ? SEARCHES_TABLE : FAVORITES_TABLE;
		String query = "SELECT DISTINCT keyword FROM " + table + " ORDER BY s_id DESC LIMIT 50";
		
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		
		if (c.getCount() > 0) {
			do {
				String keyword = c.getString(0);
				
				rs.add(keyword);
			} while (c.moveToNext());
			
			String[] result = new String[rs.size()];
			Log.w(TAG, "RecentSearches returned " + rs.size() + " results, based on " + table);
			return rs.toArray(result);
		}
		else {
			Log.w(TAG, "RecentSearches returned no results");
			return new String[0];
		}
	}
	
	/**
	 * Remove the given keyword from the database
	 * @param keyword
	 * @param type - 0 for recent, 1 for favorites
	 */
	public void removeKeyword(String keyword, int type) {
		String table = (type == 0) ? SEARCHES_TABLE : FAVORITES_TABLE;
		mDb.execSQL("DELETE FROM " + table + " WHERE keyword='" + keyword + "'");
	}
	
	/**
	 * Checks if the given keyword exists in database
	 * @param keyword
	 * @param type - 0 for recent, 1 for favorites
	 * @return
	 */
	public boolean ifExists(String keyword, int type) {
		Log.w(TAG, "Checking if " + keyword + " exists in table");
		String table = (type == 0) ? SEARCHES_TABLE : FAVORITES_TABLE;
		String query = "SELECT * FROM " + table + " WHERE keyword='" + keyword.replace("'", "''") + "' LIMIT 1";
		
		Cursor c = mDb.rawQuery(query, null);
		
		return c.getCount() != 0;
	}
	
	/**
	 * Get the size of the recent/favorite table (in KBs)
	 * @param type - 0 for recent, 1 for favorites
	 * @return
	 */
	public double getSize(int type) {
		String table = (type == 0) ? SEARCHES_TABLE : FAVORITES_TABLE;
		Cursor c = mDb.rawQuery("SELECT COUNT(*) as num FROM " + table , null);
		c.moveToFirst();
		int num = c.getInt(c.getColumnIndex("num"));
		
		return ((double)num * Constants.FAVORITE_ROW_SIZE) / 1000;
	}
}
