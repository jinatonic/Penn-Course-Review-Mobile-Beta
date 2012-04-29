package edu.upenn.cis.cis350.database;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.Ratings;

/**
 * Helper class to access Android SQLite database and store the recently searched data for fast access
 * Old data is deleted on application start (e.g. a week old)
 * @author Jinyan
 *
 */

public class DepartmentSearchCache extends DatabaseHelperClass {

	/* TAG for logging purposes */
	private static final String TAG = "DepartmentSearchCache";

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
		mDb.execSQL("DROP TABLE IF EXISTS " + DEPARTMENT_TABLE);
		mDb.execSQL(DEPARTMENT_TABLE_CREATE);
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
			values.put("ratings_amountLearned", (r.getAmountLearned().equals(Constants.NA)) ? null : r.getAmountLearned());
			values.put("ratings_commAbility", (r.getCommAbility().equals(Constants.NA)) ? null : r.getCommAbility());
			values.put("ratings_courseQuality", (r.getCourseQuality().equals(Constants.NA)) ? null : r.getCourseQuality());
			values.put("ratings_difficulty", (r.getDifficulty().equals(Constants.NA)) ? null : r.getDifficulty());
			values.put("ratings_instructorAccess", (r.getInstructorAccess().equals(Constants.NA)) ? null : r.getInstructorAccess());
			values.put("ratings_instructorQuality", (r.getInstructorQuality().equals(Constants.NA)) ? null : r.getInstructorQuality());
			values.put("ratings_readingsValue", (r.getReadingsValue().equals(Constants.NA)) ? null : r.getReadingsValue());
			values.put("ratings_recommendMajor", (r.getRecommendMajor().equals(Constants.NA)) ? null : r.getRecommendMajor());
			values.put("ratings_recommendNonMajor", (r.getRecommendNonMajor().equals(Constants.NA)) ? null : r.getRecommendNonMajor());
			values.put("ratings_stimulateInterest", (r.getStimulateInterest().equals(Constants.NA)) ? null : r.getStimulateInterest());
			values.put("ratings_workRequired", (r.getWorkRequired().equals(Constants.NA)) ? null : r.getWorkRequired());

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
		keyword = keyword.toLowerCase().replace("'", "''");
		String query = "SELECT * FROM " + DEPARTMENT_TABLE + " WHERE LOWER(dept_id)='" + keyword + "'";
		Cursor c = mDb.rawQuery(query, null);

		return c.getCount() != 0;
	}

	/**
	 * Returns the department object associated with the keyword
	 */
	public Department getDepartment(String keyword) {
		Log.w(TAG, "Searching database for department " + keyword);

		keyword = keyword.toLowerCase().replace("'", "''");

		// First try to match based on course alias
		String query = "SELECT * FROM " + DEPARTMENT_TABLE + " WHERE LOWER(dept_id)='" + keyword + "'";
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();

		// If cached data found, recreate object and return it
		if (c.getCount() > 0) {
			Log.w(TAG, "getDepartment: department found, number of courses in department is " + c.getCount());
			String dept_name = c.getString(1);
			String dept_id = c.getString(2);
			String dept_path = c.getString(3);
			ArrayList<CourseAverage> courseAverages = new ArrayList<CourseAverage>();

			Double amountLearned;
			Double commAbility;
			Double courseQuality;
			Double difficulty;
			Double instructorAccess;
			Double instructorQuality;
			Double readingsValue;
			Double recommendMajor;
			Double recommendNonMajor;
			Double stimulateInterest;
			Double workRequired;

			do {
				String course_name = c.getString(4);
				String course_id = c.getString(5);
				String course_path = c.getString(6);

				amountLearned = (c.isNull(7)) ? null : c.getDouble(7);
				commAbility = (c.isNull(8)) ? null : c.getDouble(8);
				courseQuality = (c.isNull(9)) ? null : c.getDouble(9);
				difficulty = (c.isNull(10)) ? null : c.getDouble(10);
				instructorAccess = (c.isNull(11)) ? null : c.getDouble(11);
				instructorQuality = (c.isNull(12)) ? null : c.getDouble(12);
				readingsValue = (c.isNull(13)) ? null : c.getDouble(13);
				recommendMajor = (c.isNull(14)) ? null : c.getDouble(14);
				recommendNonMajor = (c.isNull(15)) ? null : c.getDouble(15);
				stimulateInterest = (c.isNull(16)) ? null : c.getDouble(16);
				workRequired = (c.isNull(17)) ? null : c.getDouble(17);

				Ratings tRate = new Ratings(
						amountLearned,
						commAbility,
						courseQuality,
						difficulty,
						instructorAccess,
						instructorQuality,
						readingsValue,
						recommendMajor,
						recommendNonMajor,
						stimulateInterest,
						workRequired
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
		Cursor c = mDb.rawQuery("SELECT count(*) AS num FROM " + DEPARTMENT_TABLE, null);
		c.moveToFirst();
		int num = c.getInt(c.getColumnIndex("num"));

		return num * Constants.DEPARTMENT_CACHE_ROW_SIZE / 1000;
	}
}
