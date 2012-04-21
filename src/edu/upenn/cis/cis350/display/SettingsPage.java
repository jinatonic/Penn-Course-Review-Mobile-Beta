package edu.upenn.cis.cis350.display;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsPage extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.settings_page);

		/*
		// Set title font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView dataTitle = (TextView) findViewById(R.id.data_title);
		dataTitle.setTypeface(timesNewRoman);
		*/

		// Set data store sizes
		TextView cacheSize = (TextView)findViewById(R.id.cache_size);
		cacheSize.setText(""); // TODO

		TextView historySize = (TextView)findViewById(R.id.history_size);
		historySize.setText(""); // TODO

		TextView favSize = (TextView)findViewById(R.id.fav_size);
		favSize.setText(""); // TODO

		TextView autocompleteSize = (TextView)findViewById(R.id.autocomplete_size);
		autocompleteSize.setText(""); // TODO
	}

	public void onClearCacheButtonClick(View v) {
		// TODO
	}

	public void onClearHistoryButtonClick(View v) {
		// TODO
	}

	public void onClearFavoritesButtonClick(View v) {
		// TODO
	}

	public void onClearAutocompleteButtonClick(View v) {
		// TODO
	}


}
