package edu.upenn.cis.cis350.display;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Constants;

/**
 * Main search page
 * 
 * @author Connie Ho, Jinyan Cao, Cynthia Mai
 */

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
		case R.id.search_menu_recent:
			showDialog(RECENT_DIALOG);
			return true;
		case R.id.search_menu_favorites:
			showDialog(FAVORITES_DIALOG);
			return true;
		case R.id.search_menu_settings:
			Intent i = new Intent(this, SettingsPage.class);
			// Start Settings Page activity
			startActivityForResult(i, Constants.NORMAL_OPEN_REQUEST);
			return true;
		case R.id.search_menu_quit:
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
			else if (resultCode == Constants.RESULT_GO_TO_SEARCH) {
				// Don't do anything
			}
			else if (resultCode == Constants.RESULT_GO_TO_START) {
				setResult(Constants.RESULT_GO_TO_START);
				this.finish();
			}
			else if (resultCode == Constants.RESULT_AUTOCOMPLETE_RESETTED) {
				setResult(Constants.RESULT_AUTOCOMPLETE_RESETTED);
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
			final String[] result = autoCompleteDB.checkAutocomplete(term);
			autoCompleteDB.close();

			Log.w("SearchPage", "Got results, setting autocomplete. Results: " + result);
			// Set autocomplete rows
			search.setAdapter(new ArrayAdapter<String>(SearchPage.this, R.layout.item_list, result) {
				
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					String word = result[position];
					if (convertView == null) {
						convertView = new TextView(SearchPage.this);
						((TextView)convertView).setTextColor(Color.BLACK);
						((TextView)convertView).setTypeface(calibri);
						((TextView)convertView).setTextSize(14);
						convertView.setPadding(7, 8, 3, 8);
					}

					if (word.substring(0, 4).equals(Constants.COURSE_TAG)) {
						convertView.setBackgroundResource(R.drawable.course_bg);
					}
					else if (word.substring(0, 4).equals(Constants.INSTRUCTOR_TAG)) {
						convertView.setBackgroundResource(R.drawable.instructor_bg);
					}
					else {
						convertView.setBackgroundResource(R.drawable.dept_bg);
					}
						
					((TextView)convertView).setText(word);
					
					return convertView;
				}

			});
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
}
