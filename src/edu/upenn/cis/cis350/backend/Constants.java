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
	
	// Authentication key is cached for 30 days (change to what the OSA people want)
	public static final int MAX_DAY_FOR_AUTHENTICATION = 30;
	
	// database sizes
	public static final int AUTOCOMPLETE_ROW_SIZE = 100;
	public static final int SEARCH_CACHE_ROW_SIZE = 500;
	public static final int DEPARTMENT_CACHE_ROW_SIZE = 300;
	public static final int FAVORITE_ROW_SIZE = 100;
}
