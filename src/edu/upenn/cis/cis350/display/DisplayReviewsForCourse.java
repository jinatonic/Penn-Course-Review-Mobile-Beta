package edu.upenn.cis.cis350.display;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import edu.upenn.cis.cis350.database.CourseSearchCache;
import edu.upenn.cis.cis350.database.RecentSearches;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

/* Display all reviews for a specific course */
public class DisplayReviewsForCourse extends Display {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.course_reviews);

		searches_db = new RecentSearches(this.getApplicationContext());

		// Get course reviews for the search term
		Intent i = getIntent();
		String type = i.getStringExtra(getResources().getString(R.string.SEARCH_TYPE));
		String alias = i.getStringExtra(getResources().getString(R.string.SEARCH_ALIAS));
		String name = i.getStringExtra(getResources().getString(R.string.SEARCH_NAME));

		keyword = type + alias + " - " + name;

		Log.w("DisplayReviewsForCourse", "Displaying information for " + keyword);

		// Search database first
		CourseSearchCache cache = new CourseSearchCache(this.getApplicationContext());
		cache.open();
		courseReviews = cache.getCourse(alias, 0);
		cache.close();

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.header);
		searchPCRView.setTypeface(timesNewRoman);

		// Top half of page under PCR header - check if course found
		TextView number = (TextView)findViewById(R.id.course_number);
		number.setTypeface(timesNewRoman);
		if (courseReviews == null || courseReviews.size() == 0) {
			number.setText("No reviews found for this course.");
			return;
		}

		// Set the text below the PCR header - course ID (alias), course name, course description
		number.setText(courseReviews.get(0).getAlias());
		TextView name_view = (TextView) findViewById(R.id.course_name);
		name_view.setText(courseReviews.get(0).getName());
		name_view.setTypeface(timesNewRoman);
		TextView description = (TextView)findViewById(R.id.course_description);
		description.setText(courseReviews.get(0).getDescription());

		// Set difficulty to be thing its sorted by first
		// TODO(cymai): see if you should change default
		TextView defaultTab = (TextView) findViewById(R.id.difficulty_tab);
		defaultTab.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		sortingField = Sort.DIFFICULTY_ASC;

		printReviews(Type.COURSE);
	}

}