package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.AutoComplete;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.database.CourseSearchCache;
import edu.upenn.cis.cis350.database.DepartmentSearchCache;
import edu.upenn.cis.cis350.database.RecentSearches;
import edu.upenn.cis.cis350.objects.KeywordMap;

public class StartPage extends Activity {
	private Button searchButton, favoritesButton, historyButton;

	private boolean DLcomplete;
	private boolean DLstarted;

	// Database pointers
	CourseSearchCache courseSearchCache;
	DepartmentSearchCache departmentSearchCache;
	AutoCompleteDB autoCompleteDB;
	RecentSearches recentSearches;

	private static final int RECENT_DIALOG = 0;
	private static final int FAVORITES_DIALOG = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.start_page);

		DLcomplete = true;
		DLstarted = false;

		courseSearchCache = new CourseSearchCache(this.getApplicationContext());
		departmentSearchCache = new DepartmentSearchCache(this.getApplicationContext());
		autoCompleteDB = new AutoCompleteDB(this.getApplicationContext());
		recentSearches = new RecentSearches(this.getApplicationContext());

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView startPCRView = (TextView) findViewById(R.id.start_pcr);
		startPCRView.setTypeface(timesNewRoman);
		TextView startCommentView = (TextView) findViewById(R.id.start_comment);
		startCommentView.setTypeface(timesNewRoman);

		// Set icon of search button
		searchButton = (Button) findViewById(R.id.search_button);
		searchButton.setBackgroundResource(R.drawable.search_icon);
		// Set icon of favorites button
		favoritesButton = (Button) findViewById(R.id.favorites_button);
		favoritesButton.setBackgroundResource(R.drawable.favorites_icon);
		// Set icon of history button
		historyButton = (Button) findViewById(R.id.history_button);
		historyButton.setBackgroundResource(R.drawable.history_icon);

		setProgressBarIndeterminateVisibility(true);
		addListenerOnButton();

		// Run db maintenance in the background
		new DatabaseMaintenance().execute("");
	}

	@Override
	public void onResume() {
		super.onResume();
		// remove any remaining dialogs
		removeDialog(FAVORITES_DIALOG);
		removeDialog(RECENT_DIALOG);

		if (DLstarted && DLcomplete) {
			Log.w("StartPage", "Resuming startpage, download finished");
			DLstarted = false;

			goToSearchPage();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		final String[] result;
		AlertDialog.Builder bDialog;
		ArrayAdapter<String> auto_adapter;
		ListView recentList;
		switch (id) {
		case RECENT_DIALOG:
		case FAVORITES_DIALOG:
			// Get the data from RecentSearches
			recentSearches.open();
			result = recentSearches.getKeywords(id);	// 0 for recent, 1 for favorite, matches id
			recentSearches.close();

			bDialog = new AlertDialog.Builder(this);
			recentList = new ListView(this);

			auto_adapter = new ArrayAdapter<String>(StartPage.this,
					R.layout.item_list, result);

			recentList.setAdapter(auto_adapter);
			recentList.setCacheColorHint(Color.TRANSPARENT);	// Fix issue with list turning black on scrolling
			bDialog.setView(recentList);
			bDialog.setInverseBackgroundForced(true);

			recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					Log.w("StartPage", "Selected " + result[pos] + " from list");
					preProcessForNextPage(result[pos]);
				}

			});

			dialog = bDialog.create();
			return dialog;
		default:
			return null;
		}
	}

	/** 
	 * Sets the intent to go to SearchPage (so that it auto processes and queries for result)
	 * @param keyword
	 */
	public void preProcessForNextPage(String keyword) {
		Intent i = new Intent(this, SearchPage.class);
		i.putExtra("keyword", keyword);

		// Start activity with process request
		startActivityForResult(i, Constants.PROCESS_REQUEST);
	}

	public void addListenerOnButton() {
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				StartPage.this.runOnUiThread(new Runnable() {
					public void run() {
						downloadAutoComplete();
					}
				});
			}

		});

		favoritesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(FAVORITES_DIALOG);
			}

		});

		historyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(RECENT_DIALOG);
			}

		});
	}

	public void goToSearchPage() {
		Intent i = new Intent(this, SearchPage.class);

		// Pass the Intent to the proper Activity (check for course search vs. dept search)
		startActivityForResult(i, Constants.NORMAL_OPEN_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.NORMAL_OPEN_REQUEST || requestCode == Constants.PROCESS_REQUEST) {
			if (resultCode == RESULT_OK) {
				// Don't do anything
			}
			else if (resultCode == Constants.RESULT_QUIT)
				// Quit application if quit is issued
				this.finish();
		}
	}

	// file download simulator... a really simple
	public void downloadAutoComplete() {

		autoCompleteDB.open();
		//autoCompleteDB.resetTables();		// COMMENT THIS OUT IF U DONT WANT TO LOAD AUTOCOMPLETE EVERY TIME
		if (autoCompleteDB.updatesNeeded()) {
			// Autocomplete table is empty, need to populate it initially
			new AutocompleteQuery(this).execute("");
			autoCompleteDB.close();
		}
		else if (autoCompleteDB.getSize() < Constants.MAX_AUTOCOMPLETE_RESULT) {
			// Autocomplete table is corrupt or missing entries, redownload it
			autoCompleteDB.resetTables();
			new AutocompleteQuery(this).execute("");	// TODO add toast
			autoCompleteDB.close();
		}
		else {
			autoCompleteDB.close();
			goToSearchPage();
		}
	}

	class AutocompleteQuery extends AsyncTask<String, Integer, String> {

		private ProgressDialog dialog;
		int progress;
		int total;

		Activity _activity;

		AutocompleteQuery(Activity activity) {
			_activity = activity;
		}

		@Override
		protected void onPreExecute() {
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			
			dialog = new ProgressDialog(_activity);
			dialog.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_states));
			dialog.setCanceledOnTouchOutside(false);
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(200);	// change, 157
			
			progress = 0;

			dialog.setMessage("Initiating download for autocomplete...");

			dialog.show();
		}

		@Override
		protected String doInBackground(String... input) {
			if (input == null || input.length != 1) {
				Log.w("Parser", "given input is more than one string");
				return null;
			}

			DLcomplete = false;
			DLstarted = true;

			autoCompleteDB.open();
			final ExecutorService executor = Executors.newFixedThreadPool(8);

			publishMessage("Downloading instructor information...", progress);
			
			// Get instructors
			final ArrayList<KeywordMap> instructor_result = AutoComplete.getAutoCompleteInstructors();
			autoCompleteDB.addEntries(instructor_result);
			instructor_result.clear();
			
			progress += 33;
			
			publishMessage("Downloading department information...", progress);

			// Get individual departments
			ArrayList<KeywordMap> department_result = AutoComplete.getAutoCompleteDepartments();
			autoCompleteDB.addEntries(department_result);

			total = department_result.size();

			final ArrayList<KeywordMap> course_result = new ArrayList<KeywordMap>();

			for (final KeywordMap dept : department_result) {
				executor.execute(new Runnable() {
					public void run() {
						course_result.addAll(AutoComplete.getAutoCompleteCourses(dept));
						
						publishMessage("Downloading " + dept.getName(), ++progress);
					}
				});
			}

			try {
				// Make sure all of the queries complete executing before proceeding
				executor.shutdown();
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
				publishMessage("Saving data to database...", progress);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			autoCompleteDB.addEntries(course_result);
			
			publishMessage("Done", 200);

			DLcomplete = true;

			return "COMPLETE"; // CHANGE
		}

		protected void onPostExecute(String result) {
			if (dialog.isShowing())
				dialog.dismiss();

			goToSearchPage();
		}

		private void publishMessage(final String msg, final int progress) {
			StartPage.this.runOnUiThread(new Runnable() {
				public void run() {
					dialog.setProgress(progress);
					dialog.setMessage(msg);
				}
			});
		}
	}

	class DatabaseMaintenance extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			databaseMaintenance();

			return "DB maintainance complete";
		}


		/**
		 * Helper function to check for out-of-date entries in all of the databases and delete them if necessary
		 * Also fires the async query to get autocomplete data if the database doesn't exist 
		 */
		public void databaseMaintenance() {
			// Perform clear on database
			courseSearchCache.open();
			courseSearchCache.clearOldEntries();
			// cache.resetTables();
			courseSearchCache.close();

			departmentSearchCache.open();
			departmentSearchCache.clearOldEntries();
			// dept_cache.resetTables();
			departmentSearchCache.close();
		}
	}
}
