package edu.upenn.cis.cis350.backend;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
			"path char(50) NOT NULL," +
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
	
	
	
}
