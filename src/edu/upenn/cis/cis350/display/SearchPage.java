package edu.upenn.cis.cis350.display;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.SearchCache;

public class SearchPage extends Activity {
	
	public static final int ACTIVITY_LOADING_PAGE = 1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Perform clear on database
		SearchCache cache = new SearchCache(this.getApplicationContext());
		cache.open();
		cache.clearOldEntries();
		cache.close();

		setContentView(R.layout.search_page);

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.search_pcr);
		searchPCRView.setTypeface(timesNewRoman);
		TextView searchCommentView = (TextView) findViewById(R.id.search_comment);
		searchCommentView.setTypeface(timesNewRoman);

		// Handle user pushing enter after typing search term
		EditText search = (EditText)findViewById(R.id.search_term);
		search.setOnKeyListener(new OnKeyListener() {
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

	public void onEnterButtonClick(View v) {
		// Check whether the search term entered was a dept or a course (ends in a number)
		// TODO does not yet account for instructor, will update after auto-complete implemented
		String searchTerm = ((EditText)findViewById(R.id.search_term)).getText().toString();
		// Create an Intent using the current Activity and the Class to be created
		Intent i = new Intent(this, LoadingPage.class);
		// Add the search term as an extra to this Intent
		i.putExtra(getResources().getString(R.string.SEARCH_TERM), searchTerm);
		// Pass the Intent to the proper Activity (check for course search vs. dept search)
		startActivityForResult(i, SearchPage.ACTIVITY_LOADING_PAGE);
	}

	// Clear search term on clear button click
	public void onClearButtonClick(View v) {
		EditText search = (EditText)findViewById(R.id.search_term);
		search.setText("");
	}
}
