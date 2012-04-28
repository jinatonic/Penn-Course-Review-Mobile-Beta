package edu.upenn.cis.cis350.backend;

public class Constants {

	public static final int MAX_ATTEMPTS_FOR_CONNECTION = 3;
	
	public static final int ACTIVITY_LOADING_PAGE = 1;
	
	public static final int MAX_AUTOCOMPLETE_RESULT = 15;
	
	public static final int AUTOCOMPLETE_WAIT_TIME = 300;	// milliseconds
	
	public static final String COURSE_TAG = "[C] ";
	public static final String INSTRUCTOR_TAG = "[I] ";
	public static final String DEPARTMENT_TAG = "[D] ";
	
	public static final int MINIMUM_AUTOCOMPLETE_SIZE = 13000;
	
	// REQUEST CODES
	public static final int NORMAL_OPEN_REQUEST = 100;
	
	// RESULT CODES
	public static final int RESULT_QUIT = 200;
	public static final int RESULT_AUTOCOMPLETE_RESETTED = 201;
	public static final int RESULT_GO_TO_SEARCH = 202;
	public static final int RESULT_GO_TO_START = 203;
	
	// Authentication key is cached for 30 days (TODO: change to what the OSA people want)
	public static final int MAX_DAY_FOR_AUTHENTICATION = 30;
	
	
	/******************** USED FOR CALCULATING DATABASE SIZE ********************/
	// database sizes
	public static final int AUTOCOMPLETE_ROW_SIZE = 100;
	public static final int SEARCH_CACHE_ROW_SIZE = 500;
	public static final int DEPARTMENT_CACHE_ROW_SIZE = 300;
	public static final int FAVORITE_ROW_SIZE = 100;
	
	/******************** FOLLOWING CONSTANTS ARE FOR CHANGING FIELDS IN DISPLAY ********************/
	
	// All can access these fields
	public static final String amountLearned = "Amount Learned";
	public static final String commAbility = "Communication Ability";
	public static final String courseQuality = "Course Quality";
	public static final String difficulty = "Difficulty";
	public static final String instructorAccess = "Instructor Access";
	public static final String instructorQuality = "Instructor Quality";
	public static final String readingsValue = "Readings Value";
	public static final String recommendMajor = "Recommend Major";
	public static final String recommendNonMajor = "Recommend Non Major";
	public static final String stimulateInterest = "Stimulate Interest";
	public static final String workRequired = "Amount of Work Required";
	
	// COURSE only fields
	public static final String semester = "Semester";
	
	public static final String[] COURSE_SELECTION = {semester, amountLearned, commAbility, courseQuality, difficulty, instructorAccess, instructorQuality, readingsValue,
		recommendMajor, recommendNonMajor, stimulateInterest, workRequired
	};
	
	public static final String[] INSTRUCTOR_SELECTION = {semester, amountLearned, commAbility, courseQuality, difficulty, instructorAccess, instructorQuality, readingsValue,
		recommendMajor, recommendNonMajor, stimulateInterest, workRequired
	};
	
	public static final String[] DEPT_SELECTION = {amountLearned, commAbility, courseQuality, difficulty, instructorAccess, instructorQuality, readingsValue, recommendMajor, recommendNonMajor, 
		stimulateInterest, workRequired
	};
}
