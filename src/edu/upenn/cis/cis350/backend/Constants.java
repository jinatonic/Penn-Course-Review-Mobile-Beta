package edu.upenn.cis.cis350.backend;

public class Constants {

	public static final int MAX_ATTEMPTS_FOR_CONNECTION = 3;
	
	public static final int ACTIVITY_LOADING_PAGE = 1;
	
	public static final int NORMAL_PAGE_LOAD = 5;

	public static final int MAX_AUTOCOMPLETE_RESULT = 15;
	
	public static final int AUTOCOMPLETE_WAIT_TIME = 300;	// milliseconds
	
	public static final String COURSE_TAG = "[C] ";
	public static final String INSTRUCTOR_TAG = "[I] ";
	public static final String DEPARTMENT_TAG = "[D] ";
	
	public static final int MINIMUM_AUTOCOMPLETE_SIZE = 13000;
	
	public static final int RESULT_QUIT = -100;
	public static final int NORMAL_OPEN_REQUEST = 100;
	public static final int PROCESS_REQUEST = 101;
	public static final int AUTOCOMPLETE_RESET = 102;
	
	// Authentication key is cached for 30 days (change to what the OSA people want)
	public static final int MAX_DAY_FOR_AUTHENTICATION = 30;
	
	// database sizes
	public static final int AUTOCOMPLETE_ROW_SIZE = 100;
	public static final int SEARCH_CACHE_ROW_SIZE = 1000;
	public static final int DEPARTMENT_CACHE_ROW_SIZE = 700;
	public static final int FAVORITE_ROW_SIZE = 30;
}
