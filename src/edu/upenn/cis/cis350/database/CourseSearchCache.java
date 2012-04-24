package edu.upenn.cis.cis350.database;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import edu.upenn.cis.cis350.backend.Constants;
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

public class CourseSearchCache extends DatabaseHelperClass {
	
	/* TAG for logging purposes */
	private static final String TAG = "CourseSearchCache";
	
	
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
		Log.w(TAG, "Closing CourseSearchCache");
		mDbHelper.close();
	}
	
	/** 
	 * Delete all entries from all tables in database (for testing purposes)
	 */
	public void resetTables() {
		Log.w(TAG, "Resetting database tables");
		mDb.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
		mDb.execSQL(COURSE_TABLE_CREATE);
	}
	
	/**
	 * Takes a given course and store the information in the database (if not exists)
	 * @param course given course to be added to the database
	 * @param type - 0 for course 1 for instructor
	 */
	public void addCourse(ArrayList<Course> courses, int type) {
		if (type == 0) 
			Log.w(TAG, "Starting to add list of courses, size " + courses.size());
		else
			Log.w(TAG, "Starting to add list of courses for instructor, size " + courses.size());
		
		for (Course course : courses) {
			Log.w(TAG, "adding course " + course.getAlias() + " " + course.getName() + " to database");

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
		keyword = keyword.toLowerCase().replace("'", "''");
		String query = null;
		if (type == 0) {
			query = "SELECT * FROM " + COURSE_TABLE + " WHERE LOWER(course_alias)='" + keyword + "' AND type=" + type;
		}
		else {
			query = "SELECT * FROM " + COURSE_TABLE + " WHERE LOWER(instructor_name)='" + keyword + "' AND type=" + type;
		}
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

		keyword = keyword.toLowerCase().replace("'", "''");
		ArrayList<Course> rs = new ArrayList<Course>();
		
		// First try to match based on course alias
		String query = null;
		if (type == 0)
			query = "SELECT * FROM " + COURSE_TABLE + " WHERE LOWER(course_alias)='" + keyword + "' AND type=" + 0;
		else
			query = "SELECT * FROM " + COURSE_TABLE + " WHERE LOWER(instructor_name)='" + keyword + "' AND type=" + 1;
		
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		
		// If cached data found, recreate object and return it
		if (c.getCount() > 0) {
			Log.w(TAG, "getCourse: courses found, number is " + c.getCount());
			do {
				Section tSection = new Section(	
												c.getString(25),
												c.getString(24),
												c.getString(26),
												c.getString(27),
												c.getString(28)
											  );
				Ratings tRate = new Ratings(
												c.getDouble(13),
												c.getDouble(14),
												c.getDouble(15),
												c.getDouble(16),
												c.getDouble(17),
												c.getDouble(18),
												c.getDouble(19),
												c.getDouble(20),
												c.getDouble(21),
												c.getDouble(22),
												c.getDouble(23)
										   );
				Instructor tIns = new Instructor(
												c.getString(7),
												c.getString(8),
												c.getString(9)
												);
				Course tCourse = new Course(
												c.getString(2),
												c.getString(1),
												c.getString(3),
												c.getString(4),
												c.getString(6),
												c.getString(5),
												tIns,
												c.getInt(10),
												c.getInt(11),
												c.getString(12),
												tRate,
												tSection
										   );
				rs.add(tCourse);
			} while (c.moveToNext());
		}
		else {
			Log.w(TAG, "No results found!");
		}
		
		return rs;
	}
	
	/**
	 * Get the size of the cache (number of entries)
	 * @return
	 */
	public int getSize() {
		Cursor c = mDb.rawQuery("SELECT count(*) AS num FROM " + COURSE_TABLE, null);
		c.moveToFirst();
		int num = c.getInt(c.getColumnIndex("num"));
		
		return num * Constants.SEARCH_CACHE_ROW_SIZE / 1000;
	}
}
