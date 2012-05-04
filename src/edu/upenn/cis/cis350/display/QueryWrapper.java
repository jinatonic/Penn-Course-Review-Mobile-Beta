package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.backend.Normalizer;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.database.CourseSearchCache;
import edu.upenn.cis.cis350.database.DepartmentSearchCache;
import edu.upenn.cis.cis350.database.RecentSearches;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

/**
 * Main super class for all of the activities. Contains most of the essential functionalities that all of the activities
 * require, such as dialogs, option menu, queryhelper, etc.
 * @author Jinyan Cao
 */

public class QueryWrapper extends Activity {

	// Database pointers
	protected CourseSearchCache courseSearchCache;
	protected DepartmentSearchCache departmentSearchCache;
	protected AutoCompleteDB autoCompleteDB;
	protected RecentSearches recentSearches;

	protected static final int NO_MATCH_FOUND_DIALOG = 1;
	protected static final int RECENT_DIALOG = 2;
	protected static final int FAVORITES_DIALOG = 3;
	protected static final int PROGRESS_BAR = 4;


	/* IMPORTANT STATE VARIABLES */
	protected KeywordMap keywordmap;
	protected AsyncTask<KeywordMap, Integer, String> currentTask;

	Typeface calibri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate the database items
		courseSearchCache = new CourseSearchCache(this.getApplicationContext());
		departmentSearchCache = new DepartmentSearchCache(this.getApplicationContext());
		autoCompleteDB = new AutoCompleteDB(this.getApplicationContext());
		recentSearches = new RecentSearches(this.getApplicationContext());

		// Set the calibri font for autocomplete
		calibri = Typeface.createFromAsset(this.getAssets(), "fonts/CALIBRI.TTF");
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case RECENT_DIALOG: 
		case FAVORITES_DIALOG:
			// Get the data from RecentSearches
			recentSearches.open();
			final String[] result;
			if (id == RECENT_DIALOG) 
				result = recentSearches.getKeywords(0);
			else
				result = recentSearches.getKeywords(1);

			recentSearches.close();

			if (result == null || result.length == 0) {
				Toast toast;
				if (id == RECENT_DIALOG) {
					toast = Toast.makeText(this.getApplicationContext(), "No recent searches found", Toast.LENGTH_SHORT);
				} else {
					toast = Toast.makeText(this.getApplicationContext(), "No favorites found", Toast.LENGTH_SHORT);
				}
				toast.show();
				return null;
			}

			AlertDialog.Builder bDialog = new AlertDialog.Builder(this);
			ListView recentList = new ListView(this);

			recentList.setAdapter(new ArrayAdapter<String>(QueryWrapper.this, R.layout.item_list, result) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					String word = result[position];
					if (convertView == null) {
						convertView = new TextView(QueryWrapper.this);
						((TextView)convertView).setTextColor(Color.BLACK);
						((TextView)convertView).setTypeface(calibri);
						((TextView)convertView).setTextSize(14);
						convertView.setPadding(7, 8, 3, 8);
					}
					
					if (word.substring(0, 4).equals(Constants.COURSE_TAG)) {
						convertView.setBackgroundResource(R.drawable.course_bg);
					}
					else if (word.substring(0, 4).equals(Constants.INSTRUCTOR_TAG)) {
						convertView.setBackgroundResource(R.drawable.instructor_bg);
					}
					else {
						convertView.setBackgroundResource(R.drawable.dept_bg);
					}
						
					((TextView)convertView).setText(word);
					
					return convertView;
				}
			});
			recentList.setCacheColorHint(Color.TRANSPARENT);	// Fix issue with list turning black on scrolling
			bDialog.setView(recentList);
			bDialog.setInverseBackgroundForced(true);

			recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					Log.w("SearchPage", "Selected " + result[pos] + " from recentlist");
					removeDialog(RECENT_DIALOG);
					removeDialog(FAVORITES_DIALOG);
					preProcessForNextPage(result[pos], true);
				}

			});

			dialog = bDialog.create();
			return dialog;
		case PROGRESS_BAR:
			String message = "Retrieving reviews...";
			if (keywordmap.getType() == Type.DEPARTMENT)
				message = message + "\nNote: Departments may take longer to retrieve";
			dialog = ProgressDialog.show(QueryWrapper.this, "", message, true);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);

			// Set key listener so when user presses back button we put current task to background and 
			// remove the progress bar (so user can search for other stuff)
			dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					// Only handle back button
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						Log.w("PROGRESS_BAR", "Back button is pressed, trying cancel current task");
						// Cancel current task and progress bar if they are active
						if (currentTask != null) {
							currentTask.cancel(true);
						}

						// Remove dialog and reset search field
						removeDialog(PROGRESS_BAR);

						AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
						if (search != null) {
							search.setText("");
						}

						return true;
					}
					return false;
				}

			});

			return dialog;
		default:
			return null;
		}
	}

	/**
	 * Helper function to process going to next page and getting the correct info
	 */
	protected void preProcessForNextPage(String searchTerm, boolean fromAuto) {
		if (searchTerm == null || searchTerm.trim().length() <= 1) {
			showDialog(NO_MATCH_FOUND_DIALOG);
			return;
		}

		Type type = Type.UNKNOWN;	// Default to UNKNOWN type

		if (fromAuto) {
			// Normalize the string and find the type
			String type_enum = searchTerm.substring(0, 4);
			// Re-set the type if we know it from autocomplete
			type = (type_enum.equals(Constants.INSTRUCTOR_TAG)) ? Type.INSTRUCTOR :
				(type_enum.equals(Constants.COURSE_TAG)) ? Type.COURSE : Type.DEPARTMENT;
			searchTerm = searchTerm.substring(4);
			searchTerm = Normalizer.normalize(searchTerm, type);
		}

		autoCompleteDB.open();

		// Call the function with appropriate Type field
		keywordmap = autoCompleteDB.getInfoForParser(searchTerm,  type);

		autoCompleteDB.close();

		// Display error dialog if the resulting keywordmap is null 
		if (keywordmap == null) {
			Log.w("SearchPage", "enter pressed, no data found");

			showDialog(NO_MATCH_FOUND_DIALOG);
			return;
		}

		showDialog(PROGRESS_BAR);

		// Add the keywordmap into RecentSearches
		recentSearches.open();
		recentSearches.addKeyword(keywordmap, 0);
		recentSearches.close();

		// Run async thread to get the correct information for the keywordmap
		currentTask = new ServerQuery(this).execute(keywordmap);
	}

	/**
	 * Helper function to check if the given searchTerm exists in the database
	 * @return
	 */
	protected boolean checkCache(String keyword, Type type) {
		switch (type) {
		case COURSE:
			courseSearchCache.open();
			if (courseSearchCache.ifExistsInDB(keyword, Constants.COURSE_ID)) {
				courseSearchCache.close();
				return true;
			}
			else courseSearchCache.close();
			return false;
		case DEPARTMENT:
			departmentSearchCache.open();
			if (departmentSearchCache.ifExistsInDB(keyword)) {
				departmentSearchCache.close();
				return true;
			}
			else departmentSearchCache.close();
			return false;
		case INSTRUCTOR:
			courseSearchCache.open();
			if (courseSearchCache.ifExistsInDB(keyword, Constants.INSTRUCTOR_ID)) {
				courseSearchCache.close();
				return true;
			}
			else courseSearchCache.close();
			return false;
		case UNKNOWN:
		default:
			return false;
		}
	}

	/** 
	 * Helper function to launch new result activity once all the data are done loading
	 * @param type
	 */
	protected void proceed() {
		if (keywordmap == null) {
			Log.w("SearchPage", "ERROR: in proceed, keywordmap is null");
			return;
		}

		Type type = keywordmap.getType();

		if (type == Type.COURSE) {
			Intent i = new Intent(this, DisplayReviewsForCourse.class);
			i.putExtra(getResources().getString(R.string.SEARCH_ALIAS), keywordmap.getAlias());
			i.putExtra(getResources().getString(R.string.SEARCH_NAME), keywordmap.getName());
			i.putExtra(getResources().getString(R.string.SEARCH_TYPE), Constants.COURSE_TAG);

			startActivityForResult(i, Constants.NORMAL_OPEN_REQUEST);
		}
		else if (type == Type.DEPARTMENT) {
			Intent i = new Intent(this, DisplayReviewsForDept.class);
			i.putExtra(getResources().getString(R.string.SEARCH_ALIAS), keywordmap.getAlias());
			i.putExtra(getResources().getString(R.string.SEARCH_NAME), keywordmap.getName());
			i.putExtra(getResources().getString(R.string.SEARCH_TYPE), Constants.DEPARTMENT_TAG);

			startActivityForResult(i, Constants.NORMAL_OPEN_REQUEST);
		}
		else if (type == Type.INSTRUCTOR) {
			Intent i = new Intent(this, DisplayReviewsForInstructor.class);
			i.putExtra(getResources().getString(R.string.SEARCH_ALIAS), keywordmap.getAlias());
			i.putExtra(getResources().getString(R.string.SEARCH_NAME), keywordmap.getName());
			i.putExtra(getResources().getString(R.string.SEARCH_TYPE), Constants.INSTRUCTOR_TAG);

			startActivityForResult(i, Constants.NORMAL_OPEN_REQUEST);
		}
	}


	protected class ServerQuery extends AsyncTask<KeywordMap, Integer, String> {

		private ProgressDialog dialog;
		int progress = 0;
		int total;

		Activity _activity;

		ServerQuery(Activity activity) {
			_activity = activity;
		}

		@Override 
		protected void onPreExecute() {
			dialog = new ProgressDialog(_activity);
			dialog.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_states));
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}

		/**
		 * Inputs are: path, name, course_id, type
		 * Recall: type: course-0, instructor-1, department-2, UNKNOWN-3
		 */
		@Override
		protected String doInBackground(KeywordMap... input) {
			if (input == null || input.length != 1) {
				Log.w("SearchPage: ServerQuery", "Too many arguments provided to AsyncTask");
				return null;
			}

			// Run the parser
			runParser(input[0]);

			return "COMPLETE"; // CHANGE
		}

		protected void onPostExecute(String result) {
			dialog.dismiss();
			removeDialog(PROGRESS_BAR);
			proceed();
		}

		public void runParser(KeywordMap input) {
			Log.w("Parser", "Running parser with " + input.getAlias());

			final Parser parser = new Parser();

			final ExecutorService executor = Executors.newFixedThreadPool(8);
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

			if (input.getType() == Type.COURSE) {
				// Check cache first, if exists, proceed
				if (checkCache(input.getAlias(), Type.COURSE)) {
					Log.w("runParser", "Course " + input.getAlias() + " is found in CourseSearchCache");
					return;
				}

				ArrayList<Course> courses = parser.getReviewsForCourse(input);

				// Add the resulting courses into cache
				if (courses == null) {
					Log.w("Parser", "getReviewsForCourse returned null");
					showErrorToast();
					return;
				}

				courseSearchCache.open();
				courseSearchCache.addCourse(courses, Constants.COURSE_ID);
				courseSearchCache.close();
			}
			else if (input.getType() == Type.DEPARTMENT) {
				// Check department cache first
				if (checkCache(input.getAlias(), Type.DEPARTMENT)) {
					Log.w("runParser", "Department " + input.getAlias() + " is found in DepartmentSearchCache");
					return;
				}

				JSONArray dept_courses = parser.getReviewsForDept(input);

				if (dept_courses == null) {
					Log.w("SearchPage Error", "NULL JSONArray returned by getReviewsForDept");
					return;
				}

				dialog.setProgress(progress);
				dialog.setMax(dept_courses.length());
				dialog.setMessage("Downloading information for " + input.getName());

				QueryWrapper.this.runOnUiThread(new Runnable() {
					public void run() {
						dialog.show();
					}
				});

				final CourseAverage[] avg = new CourseAverage[dept_courses.length()];
				for (int i = 0; i < dept_courses.length(); i++) {
					try {
						final JSONObject o = dept_courses.getJSONObject(i);
						final int j = i;
						executor.execute(new Runnable() {
							public void run() {
								CourseAverage t = parser.getCourseAvgForDept(o);
								if (t != null) {
									avg[j] = t;
								}
								else {
									Log.w("SearchPage Error", "NULL CourseAverage is returned by parser");
									showErrorToast();
								}

								QueryWrapper.this.runOnUiThread(new Runnable() {
									public void run() {
										dialog.setProgress(++progress);
									}
								});
							}
						});

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				try {
					// Make sure all of the queries complete executing before proceeding
					executor.shutdown();
					executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Department department = new Department(input.getName(), input.getAlias(), input.getPath(), new ArrayList<CourseAverage>(Arrays.asList(avg)));

				departmentSearchCache.open();
				departmentSearchCache.addDepartment(department);
				departmentSearchCache.close();
			}
			else if (input.getType() == Type.INSTRUCTOR) {
				// Check cache first, if exists, proceed
				if (checkCache(input.getName(), Type.INSTRUCTOR)) {
					Log.w("runParser", "Instructor " + input.getName() + " is found in CourseSearchCache");
					return;
				}

				JSONArray arr = parser.getReviewsForInstructor(input);
				if (arr == null) {
					Log.w("Parser", "getReviewsForInstructor returned null JSONArray");
					showErrorToast();
					return;
				}

				final ArrayList<Course> courses = new ArrayList<Course>();
				for (int i = 0; i < arr.length(); i++) {
					try {
						final JSONObject section = arr.getJSONObject(i);
						executor.execute(new Runnable() {
							public void run() {
								courses.addAll(parser.getCourseForInstructor(section));
							}
						});
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				try {
					// Make sure all of the queries complete executing before proceeding
					executor.shutdown();
					executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				courseSearchCache.open();
				courseSearchCache.addCourse(courses, Constants.INSTRUCTOR_ID);
				courseSearchCache.close();
			}
			else {
				Log.w("ServerQuery", "Running ServerQuery with unknown type, keyword " + input.getAlias());
			}
		}
		
		private void showErrorToast() {
			QueryWrapper.this.runOnUiThread(new Runnable() {
				public void run() {
					Toast toast = Toast.makeText(QueryWrapper.this, "Connection error. Please try again.", Toast.LENGTH_SHORT);
					toast.show();
				}
			});
		}
	}

}
