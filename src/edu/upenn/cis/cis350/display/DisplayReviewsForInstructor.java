package edu.upenn.cis.cis350.display;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import edu.upenn.cis.cis350.database.CourseSearchCache;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class DisplayReviewsForInstructor extends Display {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.instructor_reviews);

		// Get course reviews for the search term
		Intent i = getIntent();
		String searchTerm = i.getStringExtra(getResources().getString(R.string.SEARCH_TERM));

		Log.w("DisplayReviewsForInstructor", "Displaying information for " + searchTerm);
		
		// Search database first
		CourseSearchCache cache = new CourseSearchCache(this.getApplicationContext());
		cache.open();
		courseReviews = cache.getCourse(searchTerm, 1);
		cache.close();

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.header);
		searchPCRView.setTypeface(timesNewRoman);

		// Top half of page under PCR header - check if instructor was found
		TextView number = (TextView)findViewById(R.id.instructor_name);
		number.setTypeface(timesNewRoman);
		if (courseReviews == null || courseReviews.size() == 0) {
			number.setText("No reviews found for this instructor.");
			return;
		}

		// Set the text below the PCR header - instructor name
		number.setText(courseReviews.get(0).getInstructor().getName());

		// Set difficulty to be thing its sorted by first
		TextView defaultTab = (TextView) findViewById(R.id.difficulty_tab);
		defaultTab.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		sortingField = Sort.DIFFICULTY_ASC;

		printReviews(Type.INSTRUCTOR);
	}


}
