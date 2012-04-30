package edu.upenn.cis.cis350.display;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.database.CourseSearchCache;
import edu.upenn.cis.cis350.database.DepartmentSearchCache;
import edu.upenn.cis.cis350.database.RecentSearches;

/**
 * Settings page to clear local data
 * @author Jinyan Cao
 */

public class SettingsPage extends Activity {

	// Database pointers
	private CourseSearchCache courseSearchCache;
	private DepartmentSearchCache departmentSearchCache;
	private AutoCompleteDB autoCompleteDB;
	private RecentSearches recentSearches;
	
	private boolean autoCleared;
	
	private Toast toast;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.settings_page);

		// Instantiate the database items
		courseSearchCache = new CourseSearchCache(this.getApplicationContext());
		departmentSearchCache = new DepartmentSearchCache(this.getApplicationContext());
		autoCompleteDB = new AutoCompleteDB(this.getApplicationContext());
		recentSearches = new RecentSearches(this.getApplicationContext());
		
		showSearchCacheSize();
		showHistorySize();
		showFavoriteSize();
		showAutocompleteSize();
		
		autoCleared = false;
	}
	
	/**
	 * Used to capture the back key (to set the result code for re-downloading autocomplete)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        if (autoCleared) {
	        	Log.w("SettingsPage", "Cleared autocomplete, going back to startpage");
	        	setResult(Constants.RESULT_AUTOCOMPLETE_RESETTED);
	        }
	        else {
	        	Log.w("SettingsPage", "Going back to searchPage");
	        	setResult(RESULT_OK);
	        }
	        this.finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Helper method to refresh the size of search cache
	 */
	public void showSearchCacheSize() {
		// Set data store sizes
		TextView cacheSize = (TextView)findViewById(R.id.cache_size);
		
		courseSearchCache.open();
		long size = courseSearchCache.getSize();
		courseSearchCache.close();
		departmentSearchCache.open();
		size += departmentSearchCache.getSize();
		departmentSearchCache.close();
		
		cacheSize.setText(size + "KB"); // TODO
	}
	
	/**
	 * Helper method to refresh the size of history table
	 */
	public void showHistorySize() {
		TextView historySize = (TextView)findViewById(R.id.history_size);
		recentSearches.open();
		double size = recentSearches.getSize(0);
		recentSearches.close();
		historySize.setText(size + "KB"); // TODO
	}
	
	/**
	 * Helper method to refresh the size of favorites table
	 */
	public void showFavoriteSize() {
		TextView favSize = (TextView)findViewById(R.id.fav_size);
		recentSearches.open();
		double size = recentSearches.getSize(0);
		recentSearches.close();
		favSize.setText(size + "KB"); // TODO
	}
	
	/**
	 * Helper method to refresh the size of autocomplete table
	 */
	public void showAutocompleteSize() {
		TextView autocompleteSize = (TextView)findViewById(R.id.autocomplete_size);
		autoCompleteDB.open();
		long size = autoCompleteDB.getSize();
		autoCompleteDB.close();
		autocompleteSize.setText(size + "KB"); // TODO
	}

	/**
	 * Button listener for clearing the search cache
	 * @param v
	 */
	public void onClearCacheButtonClick(View v) {
		courseSearchCache.open();
		courseSearchCache.resetTables();
		courseSearchCache.close();
		
		departmentSearchCache.open();
		departmentSearchCache.resetTables();
		departmentSearchCache.close();
		
		showSearchCacheSize();
		
		Context context = getApplicationContext();
		CharSequence text = "Search cache cleared";

		int duration = Toast.LENGTH_LONG;

		if (toast != null)
			toast.cancel();
		toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * Button listener for clearing the history table
	 * @param v
	 */
	public void onClearHistoryButtonClick(View v) {
		recentSearches.open();
		recentSearches.resetTables(0);
		recentSearches.close();
		
		showHistorySize();
		
		Context context = getApplicationContext();
		CharSequence text = "History cleared";

		int duration = Toast.LENGTH_LONG;

		if (toast != null)
			toast.cancel();
		toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * Button listener for clearing the favorites table
	 * @param v
	 */
	public void onClearFavoritesButtonClick(View v) {
		recentSearches.open();
		recentSearches.resetTables(1);
		recentSearches.close();
		
		showFavoriteSize();
		
		Context context = getApplicationContext();
		CharSequence text = "Favorites cleared";

		int duration = Toast.LENGTH_LONG;

		if (toast != null)
			toast.cancel();
		toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	/**
	 * Button listener for clearing the autocomplete table
	 * @param v
	 */
	public void onClearAutocompleteButtonClick(View v) {
		autoCompleteDB.open();
		autoCompleteDB.resetTables();
		autoCompleteDB.close();
		
		autoCleared = true;
		
		showAutocompleteSize();
		
		Context context = getApplicationContext();
		CharSequence text = "Autocomplete data cleared";

		int duration = Toast.LENGTH_LONG;

		if (toast != null)
			toast.cancel();
		toast = Toast.makeText(context, text, duration);
		toast.show();
	}


}
