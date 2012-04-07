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
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.Instructor;
import edu.upenn.cis.cis350.objects.Ratings;
import edu.upenn.cis.cis350.objects.Section;

/**
 * Helper class to access Android SQLite database and store the recently searched data for fast access
 * Old data is deleted on application start (e.g. a week old)
 * @author Jinyan
 *
 */

public class CourseSearchCache {
	
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	/* Database and table names */
	private static final String DATABASE_NAME = "ResultsCache";
	private static final String COURSE_TABLE = "CourseResults";
	private static final int DATABASE_VERSION = 2;
	
	/* Query strings */
	private static final String COURSE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + COURSE_TABLE + " (" +
			"name char(50) NOT NULL," +
			"course_alias char(20) NOT NULL," +
			"description char(500)," +
			"semester char(50) NOT NULL," +
			"course_id char(50) NOT NULL," +
			"comments char(100)," +
			"instructor_id char(50) NOT NULL," +
			"instructor_name char(50) NOT NULL," +
			"instructor_path char(50) NOT NULL," +
			"num_reviewers integer NOT NULL DEFAULT 0," +
			"num_students integer NOT NULL DEFAULT 0," +
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
			"section_id char(50) NOT NULL," +
			"section_alias char(50) NOT NULL," +
			"section_path char(50) NOT NULL," +
			"section_name char(50) NOT NULL," +
			"section_number char(20) NOT NULL," +
			"type int NOT NULL," + // 0 for course, 1 for instructor
			"date int NOT NULL)";	// Date is stored as day of year for convenience/computation sake
	
	/* TAG for logging purposes */
	private static final String TAG = "CourseSearchCache";

	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(COURSE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
            onCreate(db);
        }
    }
	
	public CourseSearchCache(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public CourseSearchCache open() throws SQLException {
		Log.w(TAG, "Opening CourseSearchCache");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		mDb.execSQL(COURSE_TABLE_CREATE);
		return this;
	}
	
	/**
	 * Close all associated database tables
	 */
	public void close() {
		Log.w(TAG, "Closing SearchCache");
		mDbHelper.close();
	}
	
	/** 
	 * Delete all entries from all tables in database (for testing purposes)
	 */
	public void resetTables() {
		Log.w(TAG, "Resetting database tables");
		mDb.execSQL("DELETE FROM " + COURSE_TABLE + " WHERE course_id > -1");
	}
	
	/** 
	 * Scan the table and remove any entries older than 30 days
	 */
	public void clearOldEntries() {
		Log.w(TAG, "Clearing database of old entries");
		int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		int end_day = (day < 30) ? day + 365 - 30 : day - 80;
		mDb.execSQL("DELETE FROM " + COURSE_TABLE + " WHERE date < " + end_day + " or (date > " + day + " and date > " + end_day + ")");
	}
	
	/**
	 * Takes a given course and store the information in the database (if not exists)
	 * @param course given course to be added to the database
	 * @param type - 0 for course 1 for instructor
	 */
	public void addCourse(ArrayList<Course> courses, int type) {
		for (Course course : courses) {
			Log.w(TAG, "adding course " + course.getAlias() + " to database");
			// First we check that the course doesn't already exist in the database
			Cursor c = mDb.rawQuery("SELECT course_id FROM " + COURSE_TABLE + " WHERE course_id='" + course.getID() + "' and section_id='" + course.getSection().getID() + "'", null);
			c.moveToFirst();
			if (c.getCount() > 0)
				return;
			
			String id = course.getID();
			
			// First we add to the course table 
			ContentValues values = new ContentValues();
			values.put("name", course.getName());
			values.put("course_alias", course.getAlias());
			values.put("description", course.getDescription());
			values.put("semester", course.getSemester());
			values.put("course_id", id);
			values.put("comments", course.getComments());
			values.put("instructor_id", course.getInstructor().getID());
			values.put("instructor_name", course.getInstructor().getName());
			values.put("instructor_path", course.getInstructor().getPath());
			values.put("num_reviewers", course.getNumReviewers());
			values.put("num_students", course.getNumStudents());
			values.put("course_path", course.getPath());
			
			Ratings r = course.getRatings();
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
			
			Section section = course.getSection();
			values.put("section_id", section.getID());
			values.put("section_alias", section.getAlias());
			values.put("section_path", section.getPath());
			values.put("section_name", section.getName());
			values.put("section_number", section.getSectionNum());
			values.put("type", type);
			
			values.put("date", Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
			
			if (mDb.insert(COURSE_TABLE, null, values) == -1) 
				Log.w(TAG, "Failed to insert new course into table");
		}
	}
	
	/**
	 * Checks if a given keyword exists in the table
	 * @param keyword
	 * @param type - 0 to search course, 1 to search instructor
	 * @return true if matched something in db, false otherwise
	 */
	public boolean ifExistsInDB(String keyword, int type) {
		Log.w(TAG, "Trying to find " + keyword + " in DB");
		// First try to match based on course alias
		keyword = keyword.toLowerCase();
		String query = "SELECT * FROM " + COURSE_TABLE + " WHERE LOWER(course_alias)='" + keyword + "' AND type=" + type;
		Cursor c = mDb.rawQuery(query, null);
		
		return c.getCount() != 0;
	}
	
	/**
	 * Takes a course alias ('-' included in course, e.g. cis-121) and search cached database for data
	 * @param keyword can be either course-alias (normalized) or professor's name
	 * @param type - 0 to search course, 1 to search instructor
	 * @return ArrayList of courses, or empty ArrayList if no data is found
	 */
	public ArrayList<Course> getCourse(String keyword, int type) {
		Log.w(TAG, "Searching database for course " + keyword);
		keyword = keyword.toLowerCase();
		ArrayList<Course> rs = new ArrayList<Course>();
		
		// First try to match based on course alias
		String query = "SELECT * FROM " + COURSE_TABLE + " WHERE LOWER(course_alias)='" + keyword + "' AND type=" + type;
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		
		// If cached data found, recreate object and return it
		if (c.getCount() > 0) {
			Log.w(TAG, "getCourse: courses found, number is " + c.getCount());
			do {
				Section tSection = new Section(	
												c.getString(c.getColumnIndex("section_alias")),
												c.getString(c.getColumnIndex("section_id")),
												c.getString(c.getColumnIndex("section_path")),
												c.getString(c.getColumnIndex("section_name")),
												c.getString(c.getColumnIndex("section_number"))
											  );
				Ratings tRate = new Ratings(
												c.getDouble(c.getColumnIndex("ratings_amountLearned")),
												c.getDouble(c.getColumnIndex("ratings_commAbility")),
												c.getDouble(c.getColumnIndex("ratings_courseQuality")),
												c.getDouble(c.getColumnIndex("ratings_difficulty")),
												c.getDouble(c.getColumnIndex("ratings_instructorAccess")),
												c.getDouble(c.getColumnIndex("ratings_instructorQuality")),
												c.getDouble(c.getColumnIndex("ratings_readingsValue")),
												c.getDouble(c.getColumnIndex("ratings_recommendMajor")),
												c.getDouble(c.getColumnIndex("ratings_recommendNonMajor")),
												c.getDouble(c.getColumnIndex("ratings_stimulateInterest")),
												c.getDouble(c.getColumnIndex("ratings_workRequired"))
										   );
				Instructor tIns = new Instructor(
												c.getString(c.getColumnIndex("instructor_id")),
												c.getString(c.getColumnIndex("instructor_name")),
												c.getString(c.getColumnIndex("instructor_path"))
												);
				Course tCourse = new Course(
												c.getString(c.getColumnIndex("course_alias")),
												c.getString(c.getColumnIndex("name")),
												c.getString(c.getColumnIndex("description")),
												c.getString(c.getColumnIndex("semester")),
												c.getString(c.getColumnIndex("comments")),
												c.getString(c.getColumnIndex("course_id")),
												tIns,
												c.getInt(c.getColumnIndex("num_reviewers")),
												c.getInt(c.getColumnIndex("num_students")),
												c.getString(c.getColumnIndex("course_path")),
												tRate,
												tSection
										   );
				rs.add(tCourse);
			} while (c.moveToNext());
		}
		
		return rs;
	}
	
	/**
	 * Get the size of the cache (number of entries)
	 * @return
	 */
	public int getSize() {
		Log.w(TAG, "Getting size of the table");
		Cursor c = mDb.rawQuery("SELECT count(*) AS count FROM " + COURSE_TABLE, null);
		c.moveToFirst();
		return c.getInt(c.getColumnIndex("count"));
	}
}
