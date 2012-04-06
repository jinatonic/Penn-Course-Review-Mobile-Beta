package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.AutoComplete;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.database.SearchCache;
import edu.upenn.cis.cis350.display.SearchPage.AutocompleteQuery;
import edu.upenn.cis.cis350.objects.KeywordMap;
public class StartPage extends Activity {
	private AutoCompleteDB autocomplete;
	Button btnStartProgress;
	ProgressDialog progressBar;
	private int isDone = 0;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_page);
		
		setProgressBarIndeterminateVisibility(true);
		addListenerOnButton();

	}

	public void addListenerOnButton() {
		btnStartProgress = (Button) findViewById(R.id.btnStartProgress);
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
		Intent i = new Intent(this, SearchPage.class);
		
		// Pass the Intent to the proper Activity (check for course search vs. dept search)
		startActivity(i);

	}
	
	// file download simulator... a really simple
	public int downloadAutoComplete() {

		autocomplete = new AutoCompleteDB(this.getApplicationContext());
		autocomplete.open();
		autocomplete.resetTables();		// COMMENT THIS OUT IF U DONT WANT TO LOAD AUTOCOMPLETE EVERY TIME
		autocomplete.close();
		autocomplete.open();
		if (autocomplete.updatesNeeded()) {
			new AutocompleteQuery().execute("lala");
		} 
		autocomplete.close();

		return 100;

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
