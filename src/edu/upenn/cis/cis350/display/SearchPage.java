package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;


public class SearchPage extends QueryWrapper {
	private String search_term;

	// Timer for autocomplete
	Timer autocompleteTimer;

	String searchTerm;
	boolean selectedFromAutocomplete;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.search_page);

		search_term = "";

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.search_pcr);
		searchPCRView.setTypeface(timesNewRoman);
		TextView searchCommentView = (TextView) findViewById(R.id.search_comment);
		searchCommentView.setTypeface(timesNewRoman);

		// Handle user pushing enter after typing search term
		AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
		search.setAdapter(new ArrayAdapter<String>(SearchPage.this, 
				android.R.layout.simple_dropdown_item_1line, new String[0]));

		// Used for soft/virtual keyboards (they do not register with the onKeyListener)
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				selectedFromAutocomplete = false;
				// Cancel timer
				if (autocompleteTimer != null)
					autocompleteTimer.cancel();
				// Initialize new timer
				autocompleteTimer = new Timer();
				// Reschedule
				autocompleteTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						SearchPage.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								setAutocomplete();
							}

						});
						autocompleteTimer = null;
					}
				}, Constants.AUTOCOMPLETE_WAIT_TIME);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}

		});

		// Set the on-key listener to listen to textview input
		search.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If event is key-down event on "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					onEnterButtonClick(v);
					return true;
				}
				return false;
			}
		});
		
		Intent i = getIntent();
		String keyword = i.getStringExtra("keyword");
		if (keyword != null) {
			preProcessForNextPage(keyword, true);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
		search.setText("");

		// dismiss any remaining dialog that might be open
		removeDialog(RECENT_DIALOG);
		removeDialog(FAVORITES_DIALOG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_page_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_recent:
			showDialog(RECENT_DIALOG);
			return true;
		case R.id.menu_favorites:
			showDialog(FAVORITES_DIALOG);
			return true;
		case R.id.menu_settings:
			Intent i = new Intent(this, SettingsPage.class);
			// Start Settings Page activity
			startActivityForResult(i, Constants.NORMAL_OPEN_REQUEST);
			return true;
		case R.id.menu_quit:
			setResult(Constants.RESULT_QUIT);
			this.finish();
			return true;
		default: 
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.NORMAL_OPEN_REQUEST) {
			if (resultCode == RESULT_OK) {
				// Don't do anything
			}
			else if (resultCode == Constants.RESULT_QUIT) {
				setResult(Constants.RESULT_QUIT);
				this.finish();
			}
			else if (resultCode == Constants.AUTOCOMPLETE_RESET) {
				setResult(Constants.AUTOCOMPLETE_RESET);
				this.finish();
			}
		}
	}

	/**
	 * Helper function to find the appropriate keywords for autocomplete and fill in the
	 * autocomplete drop-down menu
	 */
	public void setAutocomplete() {
		AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
		String term = search.getText().toString();
		if (term.length() >= 2 && !search_term.equals(term)) {
			// Store last search_term
			search_term = term;

			// Check database for autocomplete key terms
			autoCompleteDB.open();
			String[] result = autoCompleteDB.checkAutocomplete(term);
			autoCompleteDB.close();

			Log.w("SearchPage", "Got results, setting autocomplete. Results: " + result);
			// Set autocomplete rows
			ArrayAdapter<String> auto_adapter = new ArrayAdapter<String>(SearchPage.this,
					R.layout.item_list, result);
			search.setAdapter(auto_adapter);
			search.showDropDown();

			// Set the on-click listener for when user clicks on an item
			// Only thing it does is set the flag for selectedFromAutocomplete to true
			search.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long rowId) {
					searchTerm = ((EditText)findViewById(R.id.search_term)).getText().toString();
					preProcessForNextPage(searchTerm, true);
				}
			});
		}
	}

	/**
	 * Button listener for submit button
	 * @param v
	 */
	public void onEnterButtonClick(View v) {
		searchTerm = ((EditText)findViewById(R.id.search_term)).getText().toString();
		preProcessForNextPage(searchTerm, false);
	}

	/**
	 * Button listener for clear button, erases all texts in AutocompleteTextView
	 * @param v
	 */
	public void onClearButtonClick(View v) {
		EditText search = (EditText)findViewById(R.id.search_term);
		search.setText("");
		selectedFromAutocomplete = false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case NO_MATCH_FOUND_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Search term had no results")
			.setCancelable(false)
			.setNegativeButton("Back", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			dialog = builder.create();
			return dialog;
		default:
			return super.onCreateDialog(id);
		}
	}
	
	class ServerQuery extends AsyncTask<KeywordMap, Integer, String> {

		private ProgressDialog dialog;
		int progress = 0;
		int total;
		
		Activity _activity;
		
		ServerQuery(Activity activity) {
			_activity = activity;
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
			
			SearchPage.this.runOnUiThread(new Runnable() {
				public void run() {
					dialog = new ProgressDialog(_activity);
					dialog.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_states));
					dialog.setCancelable(true);
					dialog.setCanceledOnTouchOutside(false);
					dialog.setIndeterminate(false);
					dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				}
			});

			// Run the parser
			runParser(input[0]);

			return "COMPLETE"; // CHANGE
		}

		protected void onPostExecute(String result) {
			dialog.dismiss();
			removeDialog(PROGRESS_BAR);
			proceed();	// TODO fix
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
					return;
				}

				courseSearchCache.open();
				courseSearchCache.addCourse(courses, 0);
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
					// TODO: make toast?
					return;
				}

				dialog.setProgress(progress);
				dialog.setMax(dept_courses.length());
				dialog.setMessage("Downloading information for " + input.getName());

				SearchPage.this.runOnUiThread(new Runnable() {
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
								}
								
								SearchPage.this.runOnUiThread(new Runnable() {
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
					executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
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

				ArrayList<Course> courses = parser.getReviewsForInstructor(input);

				// Add the resulting courses into cache
				if (courses == null) {
					Log.w("Parser", "getReviewsForInstructor returned null");
					return;
				}

				courseSearchCache.open();
				courseSearchCache.addCourse(courses, 1);
				courseSearchCache.close();
			}
			else {
				Log.w("ServerQuery", "Running ServerQuery with unknown type, keyword " + input.getAlias());
			}
		}
	}
}
