package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.AutoComplete;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.backend.Normalizer;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.database.SearchCache;
import edu.upenn.cis.cis350.objects.Course;
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
	
	// KeywordMap object that we are currently searching for
	KeywordMap keywordmap;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		databaseMaintainance();
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
				else if (event.getAction() == KeyEvent.ACTION_UP) {
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
							Looper.prepare();
							SearchPage.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									setAutocomplete();
								}
							});
							autocompleteTimer = null;
						}
					}, Constants.AUTOCOMPLETE_WAIT_TIME);
					return true;
				}
				return false;
			}
		});
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
	                android.R.layout.simple_dropdown_item_1line, result);
			search.setAdapter(auto_adapter);
			search.showDropDown();
			
			// Set the on-click listener for when user clicks on an item
			// Only thing it does is set the flag for selectedFromAutocomplete to true
			search.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long rowId) {
					String selectedItem = adapter.getItemAtPosition((int) rowId).toString();
					
					// Normalize the string and find the type
					String type = selectedItem.substring(0, 3);
					selectedItem = selectedItem.substring(3);
					selectedItem = Normalizer.normalize(selectedItem, 
							(type.equals("I: ")) ? Type.INSTRUCTOR : (type.equals("C: ")) ? Type.COURSE : Type.DEPARTMENT);
					
					AutoCompleteDB auto = new AutoCompleteDB(context);
					auto.open();
					
					if (type.equals("C: ")) {
						keywordmap = auto.getInfoForParser(selectedItem,  Type.COURSE);
					}
					else if (type.equals("I: ")) {
						keywordmap = auto.getInfoForParser(selectedItem,  Type.INSTRUCTOR);
					}
					else if (type.equals("D: ")) {
						keywordmap = auto.getInfoForParser(selectedItem,  Type.DEPARTMENT);
					}
					else {
						Log.w("SearchPage: setAutocomplete", "Autocomplete onitemclick type not recognized");
					}
					
					auto.close();
					
					// Backup check in case result wasn't generated correctly
					if (keywordmap == null)
						Log.w("SearchPage", "ERROR, onItemClick returned NULL KeywordMap");
					
					// Run async task in background and display some type of loading icon
					new ServerQuery().execute(keywordmap);
					
					// TODO: ADD LOADING ICON
				}
			});
		}
	}

	/**
	 * Helper function to check for out-of-date entries in all of the databases and delete them if necessary
	 * Also fires the async query to get autocomplete data if the database doesn't exist 
	 */
	public void databaseMaintainance() {
		// Perform clear on database
		SearchCache cache = new SearchCache(this.getApplicationContext());
		cache.open();
		cache.clearOldEntries();
		cache.resetTables();	// REMOVE THIS WHEN FINISH DEBUGGING
		cache.close();

		autocomplete = new AutoCompleteDB(this.getApplicationContext());
		autocomplete.open();
		//autocomplete.resetTables();		// COMMENT THIS OUT IF U DONT WANT TO LOAD AUTOCOMPLETE EVERY TIME
		//autocomplete.close();
		//autocomplete.open();
		if (autocomplete.updatesNeeded()) {
			autocomplete.close();
			new AutocompleteQuery().execute("lala");
			// TODO: add loading bar
		} 
		else 
			autocomplete.close();
	}

	/**
	 * Button listener for submit button
	 * @param v
	 */
	public void onEnterButtonClick(View v) {
		// Check whether the search term entered was a dept or a course (ends in a number)
		// TODO does not yet account for instructor, will update after auto-complete implemented
		searchTerm = ((EditText)findViewById(R.id.search_term)).getText().toString();
		
		AutoCompleteDB auto = new AutoCompleteDB(context);
		auto.open();
		KeywordMap result = auto.getInfoForParser(searchTerm, Type.UNKNOWN);
		auto.close();
		new ServerQuery().execute(result);
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

	/**
	 * Async task that queries the database for data for the text autocompletion
	 * @author Jinyan
	 *
	 */
	class AutocompleteQuery extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... input) {
			if (input == null || input.length != 5) {
				Log.w("Parser", "given input is more than one string");
				return null;
			}

			ArrayList<KeywordMap> result = AutoComplete.getAutoCompleteTerms();
			autocomplete.open();
			autocomplete.addEntries(result);
			autocomplete.close();

			return "COMPLETE"; // CHANGE
		}

		protected void onPostExecute(String result) {
		}
	}
	
	/**
	 * Helper function to check if the given searchTerm exists in the database
	 * @return
	 */
	public boolean checkCache(String keyword, Type type) {
		SearchCache cache = new SearchCache(this.getApplicationContext());
		cache.open();
		if (cache.ifExistsInDB(keyword)) {
			cache.close();
			return true;
		}
		else cache.close();
		return false;
	}

	/** 
	 * Helper function to launch new result activity once all the data are done loading
	 * @param type
	 */
	public void proceed() {
		if (keywordmap == null) return;
		
		Type type = keywordmap.getType();
		if (type == Type.COURSE) {
			Intent i = new Intent(this, DisplayReviewsForCourse.class);
			i.putExtra(getResources().getString(R.string.SEARCH_TERM), keywordmap.getAlias());

			startActivity(i);
		}
		else if (type == Type.DEPARTMENT) {
			Intent i = new Intent(this, DisplayReviewsForDept.class);
			i.putExtra(getResources().getString(R.string.SEARCH_TERM), searchTerm);

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
			proceed();	// TODO fix
		}

		public void runParser(KeywordMap input) {
			Log.w("Parser", "Running parser with " + input.getAlias());

			Parser parser = new Parser();

			if (input.getType() == Type.COURSE) {
				// Check cache first, if exists, proceed
				if (checkCache(input.getAlias(), Type.COURSE)) {
					Log.w("runParser", "Course " + input.getAlias() + " is found in SearchCache");
					return;
				}
				
				ArrayList<Course> courses = parser.getReviewsForCourse(input);
	
				// Add the resulting courses into cache
				if (courses == null) {
					Log.w("Parser", "getReviewsForCourse returned null");
					return;
				}
	
				SearchCache cache = new SearchCache(context);
				cache.open();
				cache.addCourse(courses);
				cache.close();
			}
			else if (input.getType() == Type.UNKNOWN) {
			}
			else {
				
			}
		}
	}
}
