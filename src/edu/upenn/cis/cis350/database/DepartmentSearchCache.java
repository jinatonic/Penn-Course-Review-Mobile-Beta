package edu.upenn.cis.cis350.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.Ratings;

/**
 * Helper class to access Android SQLite database and store the recently searched data for fast access
 * Old data is deleted on application start (e.g. a week old)
 * @author Jinyan
 *
 */

public class DepartmentSearchCache {

	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/* Database and table names */
	private static final String DATABASE_NAME = "ResultsCache";
	private static final String DEPARTMENT_TABLE = "DepartmentResults";
	private static final int DATABASE_VERSION = 2;

	/* Query strings */
	private static final String DEPARTMENT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + DEPARTMENT_TABLE + " (" +
			"dept_name char(50) NOT NULL," +
			"dept_id char(20) NOT NULL," +
			"dept_path char(50)," +
			"course_name char(50) NOT NULL," +
			"course_id char(20) NOT NULL," +
			"course_path char(50) NOT NULL," +
			"ratings_amountLearned float," +
			"ratings_commAbility float," +
			"ratings_courseQuality float," +
			"ratings_difficulty float," +
			"ratings_instructorAccess float," +
			"ratings_instructorQuality float," +
			"ratings_readingsValue float," +
			"ratings_recommendMajor float," +
			"ratings_recommendNonMajor float," +
			"ratings_stimulateInterest float," +
			"ratings_workRequired float," +
			"date int NOT NULL)";	// Date is stored as day of year for convenience/computation sake

	/* TAG for logging purposes */
	private static final String TAG = "DepartmentSearchCache";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DEPARTMENT_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DEPARTMENT_TABLE);
			onCreate(db);
		}
	}

	public DepartmentSearchCache(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public DepartmentSearchCache open() throws SQLException {
		Log.w(TAG, "Opening DepartmentSearchCache");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.execSQL(DEPARTMENT_TABLE_CREATE);
		return this;
	}

	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing DepartmentSearchCache");
		mDbHelper.close();
	}

	/** 
	 * Delete all entries from all tables in database (for testing purposes)
	 */
	public void resetTables() {
		Log.w(TAG, "Resetting database tables");
		mDb.execSQL("DELETE FROM " + DEPARTMENT_TABLE + " WHERE course_id > -1");
	}

	/** 
	 * Scan the table and remove any entries older than 30 days
	 */
	public void clearOldEntries() {
		Log.w(TAG, "Clearing database of old entries");
		int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		int end_day = (day < 30) ? day + 365 - 30 : day - 80;
		mDb.execSQL("DELETE FROM " + DEPARTMENT_TABLE + " WHERE date < " + end_day + " or (date > " + day + " and date > " + end_day + ")");
	}

	/** 
	 * Takes a given department and add it to the database
	 */
	public void addDepartment(Department department) {
		String dept_name = department.getName();
		String dept_id = department.getId();
		String dept_path = department.getPath();

		for (CourseAverage c : department.getCourseAverages()) {
			// First we add to the course table 
			ContentValues values = new ContentValues();
			values.put("dept_name", dept_name);
			values.put("dept_id", dept_id);
			values.put("dept_path", dept_path);
			values.put("course_name", c.getName());
			values.put("course_id", c.getId());
			values.put("course_path", c.getPath());
			
			Ratings r = c.getRatings();
			values.put("ratings_amountLearned", r.getAmountLearned());
			values.put("ratings_commAbility", r.getCommAbility());
			values.put("ratings_courseQuality", r.getCourseQuality());
			values.put("ratings_difficulty", r.getDifficulty());
			values.put("ratings_instructorAccess", r.getInstructorAccess());
			values.put("ratings_instructorQuality", r.getInstructorQuality());
			values.put("ratings_readingsValue", r.getReadingsValue());
			values.put("ratings_recommendMajor", r.getRecommendMajor());
			values.put("ratings_recommendNonMajor", r.getRecommendNonMajor());
			values.put("ratings_stimulateInterest", r.getStimulateInterest());
			values.put("ratings_workRequired", r.getWorkRequired());
			
			values.put("date", Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

			Log.w(TAG, "Inserting course " + c.getId() + " for department " + dept_name);
			
			// insert the given course into the database
			if (mDb.insert(DEPARTMENT_TABLE, null, values) == -1) 
				Log.w(TAG, "Failed to insert new course into table");
		}
	}

	/**
	 * Checks if a given keyword exists in the table
	 * @param keyword
	 * @return true if matched something in db, false otherwise
	 */
	public boolean ifExistsInDB(String keyword) {
		Log.w(TAG, "Trying to find " + keyword + " in DB");
		// First try to match based on course alias
		keyword = keyword.toLowerCase();
		String query = "SELECT * FROM " + DEPARTMENT_TABLE + " WHERE LOWER(dept_id)='" + keyword + "'";
		Cursor c = mDb.rawQuery(query, null);
		
		return c.getCount() != 0;
	}
	
	/**
	 * Returns the department object associated with the keyword
	 */
	public Department getDepartment(String keyword) {
		Log.w(TAG, "Searching database for department " + keyword);

		long size = new File(mDb.getPath()).length();
		Log.w("DepartmentSearchCache", "Size of db is " + size);
		
		keyword = keyword.toLowerCase();
		
		// First try to match based on course alias
		String query = "SELECT * FROM " + DEPARTMENT_TABLE + " WHERE LOWER(dept_id)='" + keyword + "'";
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		
		// If cached data found, recreate object and return it
		if (c.getCount() > 0) {
			Log.w(TAG, "getDepartment: department found, number of courses in department is " + c.getCount());
			String dept_name = c.getString(c.getColumnIndex("dept_name"));
			String dept_id = c.getString(c.getColumnIndex("dept_id"));
			String dept_path = c.getString(c.getColumnIndex("dept_path"));
			ArrayList<CourseAverage> courseAverages = new ArrayList<CourseAverage>();
			
			int AL = c.getColumnIndex("ratings_amountLearned");
			int CA = c.getColumnIndex("ratings_commAbility");
			int CQ = c.getColumnIndex("ratings_courseQuality");
			int D = c.getColumnIndex("ratings_difficulty");
			int IA = c.getColumnIndex("ratings_instructorAccess");
			int IQ = c.getColumnIndex("ratings_instructorQuality");
			int RV = c.getColumnIndex("ratings_readingsValue");
			int RM = c.getColumnIndex("ratings_recommendMajor");
			int RNM = c.getColumnIndex("ratings_recommendNonMajor");
			int SI = c.getColumnIndex("ratings_stimulateInterest");
			int WR = c.getColumnIndex("ratings_workRequired");
			
			int course_name_index = c.getColumnIndex("course_name");
			int course_id_index = c.getColumnIndex("course_id");
			int course_path_index = c.getColumnIndex("course_path");
			
			do {
				String course_name = c.getString(course_name_index);
				String course_id = c.getString(course_id_index);
				String course_path = c.getString(course_path_index);
				Ratings tRate = new Ratings(
						c.getDouble(AL),
						c.getDouble(CA),
						c.getDouble(CQ),
						c.getDouble(D),
						c.getDouble(IA),
						c.getDouble(IQ),
						c.getDouble(RV),
						c.getDouble(RM),
						c.getDouble(RNM),
						c.getDouble(SI),
						c.getDouble(WR)
				   );
				
				courseAverages.add(new CourseAverage(course_name, course_id, course_path, tRate));
			} while (c.moveToNext());
			
			return new Department(dept_name, dept_id, dept_path, courseAverages);
		}
		
		return null;
	}
	
	/**
	 * Get the size of the cache (number of entries)
	 * @return
	 */
	public int getSize() {
		Log.w(TAG, "Getting size of the table");
		Cursor c = mDb.rawQuery("SELECT count(*) AS count FROM " + DEPARTMENT_TABLE, null);
		c.moveToFirst();
		return c.getInt(c.getColumnIndex("count"));
	}
}
