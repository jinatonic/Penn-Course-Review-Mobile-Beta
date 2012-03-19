package edu.upenn.cis.cis350.display;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import edu.upenn.cis.cis350.backend.Normalizer;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.database.SearchCache;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class LoadingPage extends Activity {
	
    ProgressBar mProgress;
    int mProgressStatus = 0;
    
    Handler mHandler = new Handler();
    
	Context context;
	String searchTerm;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getApplicationContext();
		
		mProgress = (ProgressBar) findViewById(R.id.loading_page_progress);
		
		// Get course reviews for the search term
		Intent i = getIntent();
		searchTerm = i.getStringExtra(getResources().getString(R.string.SEARCH_TERM));
		
		searchTerm = Normalizer.normalize(searchTerm);

		if (!checkCache())
			new ServerQuery().execute(searchTerm);
	}	
	
	public boolean checkCache() {
		SearchCache cache = new SearchCache(this.getApplicationContext());
		cache.open();
		if (cache.ifExistsInDB(searchTerm)) {
			cache.close();
			proceed(Type.COURSE); // TODO: Change
			return true;
		}
		cache.close();
		return false;
	}
	
	public void proceed(Type type) {
		if (type == Type.COURSE) {
			Intent i = new Intent(this, DisplayReviewsForCourse.class);
			i.putExtra(getResources().getString(R.string.SEARCH_TERM), searchTerm);
			
			startActivity(i);
		}
	}
	
	public void setProgressBar() {
		mProgress.setProgress(mProgressStatus);
	}
	
	class ServerQuery extends AsyncTask<String, Integer, String> {
		
		@Override
		protected String doInBackground(String... input) {
			if (input == null || input.length != 1) {
				Log.w("Parser", "given input is more than one string");
				return null;
			}
			
			// Run the parser
			runParser(input[0]);
			
			return "COMPLETE"; // CHANGE
		}
		
		protected void onPostExecute(String result) {
			proceed(Type.COURSE);
		}
		
		public void runParser(String input) {
			Log.w("Parser", "Running parser with " + input);

			Parser parser = new Parser();
			
			// TODO FIX
			ArrayList<Course> courses = parser.getReviewsForCourse(input);
			
			// Add the resulting courses into cache
			if (courses == null) {
				Log.w("Parser", "getReviewsForCourse returned null");
				return;
			}
			
			SearchCache cache = new SearchCache(context);
			cache.open();
			cache.addCourse(courses);
			cache.close();
			
			publishProgress(90);
		}
	}
}
