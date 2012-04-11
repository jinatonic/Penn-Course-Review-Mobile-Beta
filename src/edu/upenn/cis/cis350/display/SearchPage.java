package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.backend.Normalizer;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.database.CourseSearchCache;
import edu.upenn.cis.cis350.database.DepartmentSearchCache;
import edu.upenn.cis.cis350.database.RecentSearches;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;


public class SearchPage extends Activity {
	private AutoCompleteDB autocomplete;
	private String search_term;
	
	// Timer for autocomplete
	Timer autocompleteTimer;
	
	Context context;
	String searchTerm;
	boolean selectedFromAutocomplete;
	
	private static final int NO_MATCH_FOUND_DIALOG = 1;
	private static final int RECENT_DIALOG = 2;
	private static final int PROGRESS_BAR = 3;
	
	AsyncTask<KeywordMap, Integer, String> currentTask;
	
	// KeywordMap object that we are currently searching for
	KeywordMap keywordmap;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		autocomplete = new AutoCompleteDB(this.getApplicationContext());
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		search_term = "";
		context = this.getApplicationContext();
		
		setContentView(R.layout.search_page);

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.search_pcr);
		searchPCRView.setTypeface(timesNewRoman);
		TextView searchCommentView = (TextView) findViewById(R.id.search_comment);
		searchCommentView.setTypeface(timesNewRoman);

		// Handle user pushing enter after typing search term
		AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
		search.setAdapter(new ArrayAdapter<String>(SearchPage.this, android.R.layout.simple_dropdown_item_1line, new String[0]));
		
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
	}
	
	@Override
	public void onResume() {
		super.onResume();
		AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
		search.setText("");
		
		// dismiss any remaining dialog that might be open
		removeDialog(RECENT_DIALOG);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	// Cancel current task and progress bar if they are active
			if (currentTask != null) {
				Log.w("SearchPage", "Back button is pressed, trying cancel current task");
				currentTask.cancel(true);
				removeDialog(PROGRESS_BAR);
				
				AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
				search.setText("");
			}
			else
				this.finish();
	    }
	    return super.onKeyDown(keyCode, event);
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
		case R.id.menu_quit:
			this.finish();
			return true;
		default: 
			return super.onOptionsItemSelected(item);
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
			autocomplete.open();
			String[] result = autocomplete.checkAutocomplete(term);
			autocomplete.close();
			
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
	 * Helper function to process going to next page and getting the correct info
	 */
	private void preProcessForNextPage(String searchTerm, boolean fromAuto) {
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
		
		AutoCompleteDB auto = new AutoCompleteDB(context);
		auto.open();
		
		// Call the function with appropriate Type field
		keywordmap = auto.getInfoForParser(searchTerm,  type);

		auto.close();
		
		// Display error dialog if the resulting keywordmap is null 
		if (keywordmap == null) {
			// TODO: display dialog
			Log.w("SearchPage", "enter pressed, no data found");
			
			showDialog(NO_MATCH_FOUND_DIALOG);
			
			return;
		}
		
		showDialog(PROGRESS_BAR);

		// Add the keywordmap into RecentSearches
		RecentSearches rs = new RecentSearches(context);
		rs.open();
		rs.addKeyword(keywordmap);
		rs.close();
	
		// Run async thread to get the correct information for the keywordmap
		SearchPage.this.runOnUiThread(new Runnable() {
			public void run() {
				currentTask = new ServerQuery().execute(keywordmap);
			}
		});
		
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
		case RECENT_DIALOG: 
			// Get the data from RecentSearches
			RecentSearches rs = new RecentSearches(context);
			rs.open();
			final String[] result = rs.getKeywords();
			rs.close();
			
			AlertDialog.Builder bDialog = new AlertDialog.Builder(this);
			ListView recentList = new ListView(this);
			
			ArrayAdapter<String> auto_adapter = new ArrayAdapter<String>(SearchPage.this,
	                R.layout.item_list, result);
			
			recentList.setAdapter(auto_adapter);
			bDialog.setView(recentList);
			bDialog.setInverseBackgroundForced(true);
			
			recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					Log.w("SearchPage", "Selected " + result[pos] + " from recentlist");
					preProcessForNextPage(result[pos], true);
				}
				
			});
			
			dialog = bDialog.create();
			return dialog;
		case PROGRESS_BAR:
			String message = "Retrieving reviews...";
			if (keywordmap.getType() == Type.DEPARTMENT)
				message = message + "\nNote: Departments may take longer to retrieve";
			dialog = ProgressDialog.show(SearchPage.this, "", message, true);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			return dialog;
		default:
			return null;
		}
	}

	/**
	 * Helper function to check if the given searchTerm exists in the database
	 * @return
	 */
	public boolean checkCache(String keyword, Type type) {
		switch (type) {
		case COURSE:
			CourseSearchCache course_cache = new CourseSearchCache(this.getApplicationContext());
			course_cache.open();
			if (course_cache.ifExistsInDB(keyword, 0)) {
				course_cache.close();
				return true;
			}
			else course_cache.close();
			return false;
		case DEPARTMENT:
			DepartmentSearchCache dept_cache = new DepartmentSearchCache(this.getApplicationContext());
			dept_cache.open();
			if (dept_cache.ifExistsInDB(keyword)) {
				dept_cache.close();
				return true;
			}
			else dept_cache.close();
			return false;
		case INSTRUCTOR:
			CourseSearchCache ins_cache = new CourseSearchCache(this.getApplicationContext());
			ins_cache.open();
			if (ins_cache.ifExistsInDB(keyword, 1)) {
				ins_cache.close();
				return true;
			}
			else ins_cache.close();
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
	public void proceed() {
		if (keywordmap == null) {
			Log.w("SearchPage", "ERROR: in proceed, keywordmap is null");
			return;
		}
		
		Type type = keywordmap.getType();
		if (type == Type.COURSE) {
			Intent i = new Intent(this, DisplayReviewsForCourse.class);
			i.putExtra(getResources().getString(R.string.SEARCH_TERM), keywordmap.getAlias());

			startActivity(i);
		}
		else if (type == Type.DEPARTMENT) {
			Intent i = new Intent(this, DisplayReviewsForDept.class);
			i.putExtra(getResources().getString(R.string.SEARCH_TERM), keywordmap.getAlias());

			startActivity(i);
		}
		else if (type == Type.INSTRUCTOR) {
			Intent i = new Intent(this, DisplayReviewsForInstructor.class);
			i.putExtra(getResources().getString(R.string.SEARCH_TERM), keywordmap.getName());
			
			startActivity(i);
		}
	}

	class ServerQuery extends AsyncTask<KeywordMap, Integer, String> {

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
			removeDialog(PROGRESS_BAR);
			proceed();	// TODO fix
		}

		public void runParser(KeywordMap input) {
			Log.w("Parser", "Running parser with " + input.getAlias());

			Parser parser = new Parser();

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
	
				CourseSearchCache cache = new CourseSearchCache(context);
				cache.open();
				cache.addCourse(courses, 0);
				cache.close();
			}
			else if (input.getType() == Type.DEPARTMENT) {
				// Check department cache first
				if (checkCache(input.getAlias(), Type.DEPARTMENT)) {
					Log.w("runParser", "Department " + input.getAlias() + " is found in DepartmentSearchCache");
					return;
				}

				Department department = parser.getReviewsForDept(input);
				
				if (department == null) {
					Log.w("Parser", "getReviewsForDept returned null");
					return;
				}
				
				DepartmentSearchCache cache = new DepartmentSearchCache(context);
				cache.open();
				cache.addDepartment(department);
				cache.close();
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
	
				CourseSearchCache cache = new CourseSearchCache(context);
				cache.open();
				cache.addCourse(courses, 1);
				cache.close();
			}
			else {
				Log.w("ServerQuery", "Running ServerQuery with unknown type, keyword " + input.getAlias());
			}
		}
	}
}
