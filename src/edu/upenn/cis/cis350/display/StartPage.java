package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.AutoComplete;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.objects.KeywordMap;

public class StartPage extends QueryWrapper {
	private Button searchButton, favoritesButton, historyButton;

	private boolean DLcomplete;
	private boolean DLstarted;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.start_page);

		DLcomplete = true;
		DLstarted = false;

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
			else if (resultCode == Constants.RESULT_QUIT) {
				setResult(Constants.RESULT_QUIT);
				// Quit application if quit is issued
				this.finish();
			}
		}
	}

	// file download simulator... a really simple
	public void downloadAutoComplete() {
		autoCompleteDB.open();
		//autoCompleteDB.resetTables();		// COMMENT THIS OUT IF U DONT WANT TO LOAD AUTOCOMPLETE EVERY TIME
		if (autoCompleteDB.updatesNeeded() || autoCompleteDB.getSize() < Constants.MAX_AUTOCOMPLETE_RESULT) {
			// Autocomplete table is empty, need to populate it initially
			// OR Autocomplete table is corrupt or missing entries, redownload it
			autoCompleteDB.resetTables();
			new AutocompleteQuery(this).execute("");
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

			publishMessage("Downloading \ninstructor information", progress);

			// Get instructors
			final ArrayList<KeywordMap> instructor_result = AutoComplete.getAutoCompleteInstructors();
			autoCompleteDB.addEntries(instructor_result);
			instructor_result.clear();

			progress += 33;

			publishMessage("Downloading \ndepartment information", progress);

			// Get individual departments
			ArrayList<KeywordMap> department_result = AutoComplete.getAutoCompleteDepartments();
			autoCompleteDB.addEntries(department_result);

			total = department_result.size();

			final ArrayList<KeywordMap> course_result = new ArrayList<KeywordMap>();

			for (final KeywordMap dept : department_result) {
				executor.execute(new Runnable() {
					public void run() {
						course_result.addAll(AutoComplete.getAutoCompleteCourses(dept));

						publishMessage("Downloading \n" + dept.getName(), ++progress);
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
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

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
}
