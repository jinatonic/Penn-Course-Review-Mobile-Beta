package edu.upenn.cis.cis350.database;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

/**
 * Helper class to access Android SQLite database and store the data for auto text completion
 * The table is populated asynchronously upon application start if it doesn't exist already
 * @author Jinyan Cao
 */

public class AutoCompleteDB extends DatabaseHelperClass {
	

	/* TAG for logging purposes */
	private static final String TAG = "AutoComplete";
	
	
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
		if (keywords == null || keywords.size() == 0)
			return;
		
		mDb.beginTransaction();
		
		final int year = Calendar.getInstance().get(Calendar.YEAR);
		final int type = (keywords.get(0).getType() == Type.COURSE) ? Constants.COURSE_ID : 
			(keywords.get(0).getType() == Type.INSTRUCTOR) ? Constants.INSTRUCTOR_ID : Constants.DEPARTMENT_ID;

		if (keywords.get(0).getAlias() == null)
			Log.w(TAG, "adding " + keywords.size() + " instructors to database");
		else
			Log.w(TAG, "adding " + keywords.size() + " courses to database");
		
		String sql = "Insert into " + AUTOCOMPLETE_TABLE + " (path, name, course_id, " +
				"course_id_norm, type, year) values(?,?,?,?,?,?)";
        SQLiteStatement insert = mDb.compileStatement(sql);
        
        for (KeywordMap keyword : keywords) {
			// First we add to the course table 
			String course_id = keyword.getAlias();
			String course_id_norm = "";
			if (course_id != null)
				course_id_norm = course_id.replace("-", "").toLowerCase();
			
			insert.bindString(1, keyword.getPath());
			insert.bindString(2, keyword.getName());
			insert.bindString(3, (course_id == null) ? "" : course_id);
			insert.bindString(4, (course_id_norm == null) ? "" : course_id_norm);
			insert.bindDouble(5, type);
			insert.bindDouble(6, year);
			
			insert.execute();
		}
		
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
		
		Log.w(TAG, "Autocomplete done with one insert");
	}
	
	/**
	 * Queries and tries to complete the given keyword based on current table
	 * @param keyword
	 */
	public String[] checkAutocomplete(String keyword) {
		keyword = keyword.trim();
		keyword = keyword.toLowerCase().replace("-", "");
		// Remove whitespace if we are looking for a course
		if (keyword.matches("^.*?[0-9]+.*$")) {
			keyword = keyword.replace(" ", "");
		}
		Log.w("AutocompleteDB", "Search DB for " + keyword);

		keyword = keyword.replace("'", "''");
		
		String query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE LOWER(name) LIKE '%" +
						keyword + "%' OR course_id_norm LIKE '" + keyword + "%'";
		
		ArrayList<String> result = new ArrayList<String>();
		
		// We query for department first, then courses, then instructors, assuming that instructors will be least common query
		query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE course_id_norm = '" + keyword + "' AND type=" + Constants.DEPARTMENT_ID;
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		if (c.getCount() == 1) {
			String course_id = c.getString(c.getColumnIndex("course_id"));
			String name = c.getString(c.getColumnIndex("name"));
			Log.w("AUTOCOMPLETE", "Found department " + course_id + " - " + name);
			result.add(Constants.DEPARTMENT_TAG + course_id + " - " + name);
		}
		
		// Then we query for courses
		query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE course_id_norm LIKE '" + 
					keyword + "%' AND type=" + Constants.COURSE_ID + " LIMIT " + Constants.MAX_AUTOCOMPLETE_RESULT;
		c = mDb.rawQuery(query, null);
		c.moveToFirst();
		if (c.getCount() > 0) {
			int course_id_index = c.getColumnIndex("course_id");
			int name_index = c.getColumnIndex("name");
			do {
				String course_id = c.getString(course_id_index);
				String name = c.getString(name_index);
				Log.w("AUTOCOMPLETE", "Found course " + course_id + " - " + name);
				result.add(Constants.COURSE_TAG + course_id + " - " + name);
			} while (c.moveToNext());
		}
		
		// Then we query for instructor (if and only if we didn't find enough course/departments
		if (result.size() < Constants.MAX_AUTOCOMPLETE_RESULT) {
			query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE LOWER(name) LIKE '%" + 
						keyword + "%' AND type!=" + Constants.DEPARTMENT_ID + " LIMIT " + Constants.MAX_AUTOCOMPLETE_RESULT;
			c = mDb.rawQuery(query, null);
			c.moveToFirst();
			if (c.getCount() > 0) {
				int course_id_index = c.getColumnIndex("course_id");
				int name_index = c.getColumnIndex("name");
				int type_index = c.getColumnIndex("type");
				
				do {
					int type = c.getInt(type_index);
					// If it's instructor
					if (type == 1) {
						String name = c.getString(name_index);
						Log.w("AUTOCOMPLETE", "Found instructor " + name);
						result.add(Constants.INSTRUCTOR_TAG + name);
					}
					// If it's course (matched based on course name)
					else {
						String name = c.getString(name_index);
						String course_id = c.getString(course_id_index);
						Log.w("AUTOCOMPLETE", "Found course " + course_id + " - " + name);
						result.add(Constants.COURSE_TAG + course_id + " - " + name);
					}
				} while (c.moveToNext());
			}
		}
		
		String[] result_array = new String[result.size()];
		return result.toArray(result_array);
	}
	
	public KeywordMap getInfoForParser(String keyword, Type type) {
		keyword = keyword.toLowerCase().replace("'", "''");
		String query = null;
		String backup_query = null;
		if (type == Type.COURSE || type == Type.DEPARTMENT) 
			query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE LOWER(course_id_norm) LIKE '%" + keyword + "%' LIMIT 1";
		else if (type == Type.INSTRUCTOR) 
			query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE LOWER(name)='" + keyword + "' AND type=" + Constants.INSTRUCTOR_ID + " LIMIT 1";
		else {
			// UNKNOWN type, find best match
			// normalize input first
			keyword = keyword.trim();
			keyword = keyword.toLowerCase().replace("-", "");
			// Remove whitespace if we are looking for a course
			if (keyword.matches("^.*?[0-9]+.*$")) {
				keyword = keyword.replace(" ", "");
			}
			query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE LOWER(course_id_norm)='" + keyword +"' LIMIT 1";
			backup_query = "SELECT * FROM " + AUTOCOMPLETE_TABLE + " WHERE LOWER(name) LIKE '%" + keyword + "%' LIMIT 1";
		}
		
		Log.w("AutocompleteDB", "Getting info for parser, keyword: " + keyword + " query: " + query);
		
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		
		if (c.getCount() == 0) {
			if (type == Type.UNKNOWN) {
				c = mDb.rawQuery(backup_query, null);
				c.moveToFirst();
				if (c.getCount() == 0)
					return null;
			}
			else 
				return null;
		}
		
		String path = c.getString(c.getColumnIndex("path"));
		String name = c.getString(c.getColumnIndex("name"));
		String course_id = c.getString(c.getColumnIndex("course_id"));
		int dbtype = c.getInt(c.getColumnIndex("type"));
		Type convertedType = (dbtype == Constants.COURSE_ID) ? Type.COURSE : (dbtype == Constants.INSTRUCTOR_ID) ? Type.INSTRUCTOR : 
								(dbtype == Constants.DEPARTMENT_ID) ? Type.DEPARTMENT : Type.UNKNOWN;
		
		Log.w("AutocompleteDB", "result, path: " + path + " name: " + name + " course_id: " + course_id);
		
		return new KeywordMap(path, name, course_id, convertedType);
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
		mDb.execSQL("DROP TABLE IF EXISTS " + AUTOCOMPLETE_TABLE);
		mDb.execSQL(AUTOCOMPLETE_TABLE_CREATE);
	}
	
	/**
	 * Get the size of the autocomplete table (in KBs)
	 * @return
	 */
	public long getSize() {
		Cursor c = mDb.rawQuery("SELECT COUNT(*) AS num FROM " + AUTOCOMPLETE_TABLE, null);
		c.moveToFirst();
		int num = c.getInt(c.getColumnIndex("num"));
		
		return num * Constants.AUTOCOMPLETE_ROW_SIZE / 1000;
	}
}
