package edu.upenn.cis.cis350.display;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.AutoComplete;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.database.SearchCache;
import edu.upenn.cis.cis350.objects.KeywordMap;


public class SearchPage extends Activity {

	private AutoCompleteDB autocomplete;
	private SearchPage searchPage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		databaseMaintainance();
		searchPage = this;

		setContentView(R.layout.search_page);

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.search_pcr);
		searchPCRView.setTypeface(timesNewRoman);
		TextView searchCommentView = (TextView) findViewById(R.id.search_comment);
		searchCommentView.setTypeface(timesNewRoman);

		// Handle user pushing enter after typing search term
		AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
		search.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If event is key-down event on "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					onEnterButtonClick(v);
					return true;
				}
				else if (event.getAction() == KeyEvent.ACTION_UP) {
					AutoCompleteTextView search = (AutoCompleteTextView)findViewById(R.id.search_term);
					String term = search.getText().toString();
					if (term.length() >= 2) {
						// Check database for autocomplete key terms
						autocomplete.open();
						String[] result = autocomplete.checkAutocomplete(term);
						autocomplete.close();
						
						// Set autocomplete rows
						ArrayAdapter<String> auto_adapter = new ArrayAdapter<String>(searchPage,
				                android.R.layout.simple_dropdown_item_1line, result);
						search.setAdapter(auto_adapter);
						
						return true;
					}
				}
				return false;
			}
		});
	}

	public void databaseMaintainance() {
		// Perform clear on database
		SearchCache cache = new SearchCache(this.getApplicationContext());
		cache.open();
		cache.clearOldEntries();
		cache.close();

		autocomplete = new AutoCompleteDB(this.getApplicationContext());
		autocomplete.open();
		autocomplete.resetTables();		// COMMENT THIS OUT IF U DONT WANT TO LOAD AUTOCOMPLETE EVERY TIME
		autocomplete.close();
		autocomplete.open();
		if (autocomplete.updatesNeeded()) {
			new AutocompleteQuery().execute("lala");
		} 
		autocomplete.close();
	}

	public void onEnterButtonClick(View v) {
		// Check whether the search term entered was a dept or a course (ends in a number)
		// TODO does not yet account for instructor, will update after auto-complete implemented
		String searchTerm = ((EditText)findViewById(R.id.search_term)).getText().toString();
		// Create an Intent using the current Activity and the Class to be created
		Intent i = new Intent(this, LoadingPage.class);
		// Add the search term as an extra to this Intent
		i.putExtra(getResources().getString(R.string.SEARCH_TERM), searchTerm);
		// Pass the Intent to the proper Activity (check for course search vs. dept search)
		startActivityForResult(i, Constants.ACTIVITY_LOADING_PAGE);
	}

	// Clear search term on clear button click
	public void onClearButtonClick(View v) {
		EditText search = (EditText)findViewById(R.id.search_term);
		search.setText("");
	}

	class AutocompleteQuery extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... input) {
			if (input == null || input.length != 1) {
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
}
