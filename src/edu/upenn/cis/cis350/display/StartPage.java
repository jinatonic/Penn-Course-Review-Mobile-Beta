package edu.upenn.cis.cis350.display;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.AutoComplete;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.objects.KeywordMap;

public class StartPage extends Activity {
	private AutoCompleteDB autocomplete;
	private Button btnStartProgress;
	private ProgressDialog progressBar;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.start_page);

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView startPCRView = (TextView) findViewById(R.id.start_pcr);
		startPCRView.setTypeface(timesNewRoman);
		TextView startCommentView = (TextView) findViewById(R.id.start_comment);
		startCommentView.setTypeface(timesNewRoman);

		// Set icon of search button
		Button searchButton = (Button) findViewById(R.id.search_button);
		searchButton.setBackgroundResource(R.drawable.search_icon);
		// Set icon of favorites button
		Button favoritesButton = (Button) findViewById(R.id.favorites_button);
		favoritesButton.setBackgroundResource(R.drawable.favorites_icon);
		// Set icon of history button
		Button historyButton = (Button) findViewById(R.id.history_button);
		historyButton.setBackgroundResource(R.drawable.history_icon);

		setProgressBarIndeterminateVisibility(true);
		addListenerOnButton();
	}

	public void addListenerOnButton() {
		btnStartProgress = (Button) findViewById(R.id.search_button);
		btnStartProgress.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// prepare for a progress bar dialog
						progressBar = new ProgressDialog(v.getContext());
						progressBar.setCancelable(true);
						progressBar.setIndeterminate(true);
						progressBar.setMessage("Autocomplete Downloading ...Please Wait 5 minutes...");
						//progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressBar.show();
						//reset progress bar status

						StartPage.this.runOnUiThread(new Runnable() {
							public void run() {
								downloadAutoComplete();
							}
						});
					}
				});
	}

	public void goToSearchPage(){
		progressBar.dismiss();
		Intent i = new Intent(this, SearchPage.class);

		// Pass the Intent to the proper Activity (check for course search vs. dept search)
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w("StartPage", "Returning to startpage, quitting");
	}

	// file download simulator... a really simple
	public void downloadAutoComplete() {

		autocomplete = new AutoCompleteDB(this.getApplicationContext());
		autocomplete.open();
		//autocomplete.resetTables();		// COMMENT THIS OUT IF U DONT WANT TO LOAD AUTOCOMPLETE EVERY TIME
		if (autocomplete.updatesNeeded()) {
			new AutocompleteQuery().execute("lala");
		} 
		else {
			autocomplete.close();
			goToSearchPage();
		}
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
			goToSearchPage();
		}
	}
}
