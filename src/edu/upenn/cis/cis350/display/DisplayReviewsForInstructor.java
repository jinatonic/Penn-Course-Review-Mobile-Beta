package edu.upenn.cis.cis350.display;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.database.CourseSearchCache;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class DisplayReviewsForInstructor extends Display {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.instructor_reviews);

		// Set the default fields for columns
		defaultColId = Constants.courseId;
		firstColId = Constants.semesterId;
		secondColId = Constants.instructorQualityId;
		thirdColId = Constants.difficultyId;
		
		defaultCol = (TextView) findViewById(R.id.default_tab);
		firstCol = (TextView) findViewById(R.id.first_tab);
		secondCol = (TextView) findViewById(R.id.second_tab);
		thirdCol = (TextView) findViewById(R.id.third_tab);
		
		// Get course reviews for the search term
		Intent i = getIntent();
		String type = i.getStringExtra(getResources().getString(R.string.SEARCH_TYPE));
		String name = i.getStringExtra(getResources().getString(R.string.SEARCH_NAME));

		keyword = type + name;
		Log.w("DisplayReviewsForInstructor", "Displaying information for " + keyword);

		// Search database first
		CourseSearchCache cache = new CourseSearchCache(this.getApplicationContext());
		cache.open();
		courseReviews = cache.getCourse(name, 1);
		cache.close();

		// Check whether heart icon should be set or not
		recentSearches.open();
		if (recentSearches.ifExists(keyword, 1)) { // was favorited
			// If keyword already exists, set favorited icon
			favHeart = (ImageButton) findViewById(R.id.fav_heart);
			favHeart.setImageResource(R.drawable.favorites_selected_100);
		}
		else { // was not favorited
			// Set unselected heart icon
			favHeart = (ImageButton) findViewById(R.id.fav_heart);
			favHeart.setImageResource(R.drawable.favorites_unselected_100);
		}
		recentSearches.close();

		// Set header font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.header);
		searchPCRView.setTypeface(timesNewRoman);

		// Top half of page under PCR header - check if instructor was found
		TextView instructor = (TextView)findViewById(R.id.instructor_name);
		instructor.setTypeface(timesNewRoman);
		if (courseReviews == null || courseReviews.size() == 0) {
			instructor.setText("No reviews found for this instructor.");
			return;
		}

		String instructorName = courseReviews.get(0).getInstructor().getName();
		// Set the text below the PCR header - instructor name
		instructor.setText(instructorName);
		
		if (instructorName.length() > 21) {
			instructor.setWidth(400);
			instructor.setLines(2);
		}

		// Set difficulty to be thing its sorted by first
		thirdCol.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		sortingField = Sort.THIRD_ASC;

		printReviews(Type.INSTRUCTOR);
		
		// Set the onclick callback for header
		TextView header = (TextView) findViewById(R.id.header);
		header.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Constants.RESULT_GO_TO_START);
				DisplayReviewsForInstructor.this.finish();
			}
		});
	}
}
