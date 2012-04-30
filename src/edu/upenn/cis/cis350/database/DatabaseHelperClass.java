package edu.upenn.cis.cis350.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Super class that manages most of the local DBs (except for pennkey and autocomplete tables)
 * @author Jinyan Cao
 */

public class DatabaseHelperClass {

	protected Context mCtx;
	
	protected DatabaseHelper mDbHelper;
	protected SQLiteDatabase mDb;
	
	private final static String TAG = "DBHelperClass";
	
	protected static final String DATABASE_NAME = "ResultsCache";
	protected static final int DATABASE_VERSION = 5;
	
	protected static final String COURSE_TABLE = "CourseResults";
	protected static final String COURSE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + COURSE_TABLE + " (" +
			"p_id integer PRIMARY KEY AUTOINCREMENT," +		// 0
			"name char(50) NOT NULL," +						// 1
			"course_alias char(20) NOT NULL DEFAULT ''," +	// 2
			"description char(700)," +						// 3
			"semester char(50)," +							// 4
			"course_id char(50) NOT NULL," +				// 5
			"comments char(300)," +							// 6
			"instructor_id char(50) NOT NULL," +			// 7
			"instructor_name char(50) NOT NULL," +			// 8
			"instructor_path char(50) NOT NULL," +			// 9
			"num_reviewers integer NOT NULL DEFAULT 0," +	// 10
			"num_students integer NOT NULL DEFAULT 0," +	// 11
			"course_path char(50) NOT NULL," +				// 12
			"ratings_amountLearned float," +				// 13
			"ratings_commAbility float," +					// 14
			"ratings_courseQuality float," +				// 15
			"ratings_difficulty float," +					// 16
			"ratings_instructorAccess float," +				// 17
			"ratings_instructorQuality float," +			// 18
			"ratings_readingsValue float," +				// 19
			"ratings_recommendMajor float," +				// 20
			"ratings_recommendNonMajor float," +			// 21
			"ratings_stimulateInterest float," +			// 22
			"ratings_workRequired float," +					// 23
			"section_id char(50) NOT NULL," +				// 24
			"section_alias char(50) NOT NULL," +			// 25
			"section_path char(50) NOT NULL," +				// 26
			"section_name char(50) NOT NULL," +				// 27
			"section_number char(20) NOT NULL," +			// 28		NOTE: DO NOT TOUCHED NUMBERED COLUMNS
			"type int NOT NULL," + // 0 for course, 1 for instructor
			"date int NOT NULL)";	// Date is stored as day of year for convenience/computation sake
	
	protected static final String DEPARTMENT_TABLE = "DepartmentResults";
	protected static final String DEPARTMENT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + DEPARTMENT_TABLE + " (" +
			"d_id integer PRIMARY KEY AUTOINCREMENT," +		// 0
			"dept_name char(50) NOT NULL," +				// 1
			"dept_id char(20) NOT NULL," +					// 2
			"dept_path char(50)," +							// 3
			"course_name char(50) NOT NULL," +				// 4
			"course_id char(20) NOT NULL," +				// 5
			"course_path char(50) NOT NULL," +				// 6
			"ratings_amountLearned float," +				// 7
			"ratings_commAbility float," +					// 8
			"ratings_courseQuality float," +				// 9
			"ratings_difficulty float," +					// 10
			"ratings_instructorAccess float," +				// 11
			"ratings_instructorQuality float," +			// 12
			"ratings_readingsValue float," +				// 13
			"ratings_recommendMajor float," +				// 14
			"ratings_recommendNonMajor float," +			// 15
			"ratings_stimulateInterest float," +			// 16
			"ratings_workRequired float," +					// 17			// NOTE: DO NOT TOUCH NUMBERED COLUMNS
			"date int NOT NULL)";	// Date is stored as day of year for convenience/computation sake
	
	protected static final String SEARCHES_TABLE = "RecentSearches";
	protected static final String FAVORITES_TABLE = "Favorites";
	
	protected static final String SEARCHES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + SEARCHES_TABLE + " (" +
			"s_id integer PRIMARY KEY," +
			"keyword char(50) NOT NULL)";
	
	protected static final String FAVORITES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + FAVORITES_TABLE + " (" +
			"s_id integer PRIMARY KEY," +
			"keyword char(50) NOT NULL)";
	
	protected static final String AUTHENTICATION_TABLE = "AuthenticationTable";
	protected static final String AUTHENTICATION_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + AUTHENTICATION_TABLE + " (" +
			"auth_key char(10) NOT NULL," +
			"year integer NOT NULL," +
			"day integer NOT NULL)";
	
	
	protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(COURSE_TABLE_CREATE);
            db.execSQL(DEPARTMENT_TABLE_CREATE);
            db.execSQL(FAVORITES_TABLE_CREATE);
            db.execSQL(SEARCHES_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DEPARTMENT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SEARCHES_TABLE);
            onCreate(db);
        }
    }
	
}
