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
	
	// For easier access
	public static final int amountLearnedId = 0;
	public static final int commAbilityId = 1;
	public static final int courseQualityId = 2;
	public static final int difficultyId = 3;
	public static final int instructorAccessId = 4;
	public static final int instructorQualityId = 5;
	public static final int readingsValueId = 6;
	public static final int recommendMajorId = 7;
	public static final int recommendNonMajorId = 8;
	public static final int stimulateInterestId = 9;
	public static final int workRequiredId = 10;
	public static final int semesterId = 11;
	public static final int instructorNameId = 12;
	public static final int courseId = 13;
	
	// All can access these fields
	public static final String amountLearned = "Amount Learned";
	public static final String commAbility = "Comm Ability";
	public static final String courseQuality = "Course Quality";
	public static final String difficulty = "Difficulty";
	public static final String instructorAccess = "Instructor Access";
	public static final String instructorQuality = "Instructor Quality";
	public static final String readingsValue = "Readings Value";
	public static final String recommendMajor = "Rec Major";
	public static final String recommendNonMajor = "Rec Non-major";
	public static final String stimulateInterest = "Stimulate Interest";
	public static final String workRequired = "Work Required";
	
	public static final String NA = "N/A";
	
	// COURSE only fields
	public static final String semester = "Semester";

	public static final String[] fillString = {amountLearned, commAbility, courseQuality, difficulty, instructorAccess, instructorQuality,
		readingsValue, recommendMajor, recommendNonMajor, stimulateInterest, workRequired, semester
	};
	
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
