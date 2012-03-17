package edu.upenn.cis.cis350.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.Ratings;
import edu.upenn.cis.cis350.objects.Section;

/**
 * Helper class to access Android SQLite database and store the recently searched data for fast access
 * @author Jinyan
 *
 */

public class SearchCache {
	
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	/* Database and table names */
	private static final String DATABASE_NAME = "ResultsCache";
	private static final String COURSE_TABLE = "CourseResults";
	private static final String SECTION_TABLE = "CourseSections";
	private static final int DATABASE_VERSION = 2;
	
	/* Query strings */
	private static final String COURSE_TABLE_CREATE = "CREATE table IF NOT EXISTS " + COURSE_TABLE + " (" +
			"id char(20) NOT NULL," +
			"comments char(500)," +
			"instructor_id char(20) NOT NULL," +
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
			"PRIMARY KEY (id))";
	
	private static final String SECTION_TABLE_CREATE = "CREATE table IF NOT EXISTS " + SECTION_TABLE + " (" +
			"course_id char(20) REFERENCES " + COURSE_TABLE + "," +
			"section_id char(20) NOT NULL," +
			"section_path char(50) NOT NULL," +
			"section_name char(50) NOT NULL," +
			"section_number char(20) NOT NULL," +
			"section_alias char(50) NOT NULL," +
			"PRIMARY KEY (course_id, section_id)";
	
	/* TAG for logging purposes */
	private static final String TAG = "SearchCache";

	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(COURSE_TABLE_CREATE);
            db.execSQL(SECTION_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SECTION_TABLE);
            onCreate(db);
        }
    }
	
	public SearchCache(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
	 * Open the SQLite database and get the associating tables (if they exist, else create them)
	 * @return SearchCache with the database opened
	 * @throws SQLException
	 */
	public SearchCache open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Close all associated database tables
	 */
	public void close() {
		mDbHelper.close();
	}
	
	/**
	 * Takes a given course and store the information in the database (if not exists)
	 * @param course
	 * @return
	 */
	public void addCourse(Course course) {
		// First we check that the course doesn't already exist in the database
		Cursor c = mDb.rawQuery("SELECT id FROM " + COURSE_TABLE + " WHERE id='" + course.getID() + "'", null);
		c.moveToFirst();
		if (c.getCount() > 0)
			return;
		
		String id = course.getID();
		
		// First we add to the course table 
		ContentValues values = new ContentValues();
		values.put("id", id);
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
		
		if (mDb.insert(COURSE_TABLE, null, values) == -1) 
			Log.w(TAG, "Failed to insert new course into table");
		
		// We first remove all occurrences with the course id
		mDb.execSQL("DELETE FROM " + SECTION_TABLE + " WHERE course_id='" + id + "'");
		
		// Then we loop through the sections and insert into the sections table
		// TODO: Finish after Charles changed
		Section sections = course.getSection();
		values = new ContentValues();
		values.put("course_id", id);
		values.put("section_id", sections.getID());
		values.put("section_path", sections.getPath());
		values.put("section_number", sections.getSectionNum());
		values.put("section_alias", sections.getAliases()[0]);
		
		if (mDb.insert(SECTION_TABLE, null, values) == -1)
			Log.w(TAG, "Failed to insert new section into table");
		
	}
}
