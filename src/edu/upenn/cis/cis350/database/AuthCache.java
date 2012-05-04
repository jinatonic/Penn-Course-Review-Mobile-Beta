package edu.upenn.cis.cis350.database;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import edu.upenn.cis.cis350.backend.Constants;


/**
 * Database to maintain PennKey authentication token
 * @author Jinyan Cao
 */

public class AuthCache extends DatabaseHelperClass {

	/* TAG for logging purposes */
	private static final String TAG = "Auth_cache";

	public AuthCache(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public AuthCache open() throws SQLException {
		Log.w(TAG, "Opening authentication table");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.execSQL(AUTHENTICATION_TABLE_CREATE);
		return this;
	}

	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing Authentication table");
		mDbHelper.close();
	}

	/** 
	 * Delete all entries from all tables in database (for testing purposes)
	 */
	public void resetTables() {
		Log.w(TAG, "Resetting database tables");
		mDb.execSQL("DROP TABLE IF EXISTS " + AUTHENTICATION_TABLE);
		mDb.execSQL(AUTHENTICATION_TABLE_CREATE);
	}

	/**
	 * Check if the current key (if there is one) is still valid
	 * @return
	 */
	public String checkKey() {
		Log.w(TAG, "Checking authentication key");
		Cursor c = mDb.rawQuery("SELECT * FROM " + AUTHENTICATION_TABLE, null);
		c.moveToFirst();

		if (c.getCount() == 0) {
			Log.w(TAG, "No key found");
			return null;
		}
		else if (c.getCount() == 1) {
			int year = c.getInt(c.getColumnIndex("year"));
			int day = c.getInt(c.getColumnIndex("day"));
			String key = c.getString(c.getColumnIndex("auth_key"));

			// Check if the date is valid 
			int curr_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
			int curr_year = Calendar.getInstance().get(Calendar.YEAR);

			if (Math.abs(curr_day - day) > Constants.MAX_DAY_FOR_AUTHENTICATION || Math.abs(curr_year - year) > 0) {
				Log.w(TAG, "Authentication key is out of date, resetting table");
				resetTables();
				return null;
			}

			return key;
		}
		else {
			Log.w(TAG, "There is more than one key in database");
			resetTables();
			return null;
		}
	}

	/** 
	 * Insert a given key into the database
	 */
	public void insertKey(String auth_key) {
		Log.w(TAG, "Inserting new key into the authcache");
		
		int curr_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		int curr_year = Calendar.getInstance().get(Calendar.YEAR);

		ContentValues values = new ContentValues();
		values.put("auth_key", auth_key);
		values.put("day", curr_day);
		values.put("year", curr_year);
		
		if (mDb.insert(AUTHENTICATION_TABLE, null, values) == -1)
			Log.w(TAG, "Failed to insert new key ito the table");
	}
}
