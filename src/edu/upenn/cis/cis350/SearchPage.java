package edu.upenn.cis.cis350;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SearchPage extends Activity {
	public static final int ACTIVITY_DisplayReviewsForCourse = 1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_page);
	}
	
	public void onEnterButtonClick(View v) {
		// Create an Intent using the current Activity and the Class to be created
		Intent i = new Intent(this, DisplayReviewsForCourse.class);
		// Add the search term as an extra to this Intent
		i.putExtra(getResources().getString(R.string.SEARCH_TERM), ((EditText)findViewById(R.id.search_term)).getText().toString());
		// Pass the Intent to the Activity, using the specified request code defined in SearchPage
		startActivityForResult(i, SearchPage.ACTIVITY_DisplayReviewsForCourse);
	}
	
	public void onClearButtonClick(View v) {
		EditText title = (EditText)findViewById(R.id.search_term);
		title.setText("");
	}

}
