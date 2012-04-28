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

/* Display all reviews for a specific course */
public class DisplayReviewsForCourse extends Display {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.course_reviews);

		// Set the default column fields
		defaultColId = Constants.instructorNameId;
		firstColId = Constants.courseQualityId;
		secondColId = Constants.instructorQualityId;
		thirdColId = Constants.difficultyId;

		defaultCol = (TextView) findViewById(R.id.default_tab);
		firstCol = (TextView) findViewById(R.id.first_tab);
		secondCol = (TextView) findViewById(R.id.second_tab);
		thirdCol = (TextView) findViewById(R.id.third_tab);
		
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

		// Check whether heart icon should be set or not
		recentSearches.open();
		if (recentSearches.ifExists(keyword, 1)) { // was favorited
			Log.v("Favorites", "Was favorited already");
			// If keyword already exists, set favorited icon
			favHeart = (ImageButton) findViewById(R.id.fav_heart);
			favHeart.setImageResource(R.drawable.favorites_selected_100);
		}
		else { // was not favorited
			Log.v("Favorites", "Was not favorited already");
			// Set unselected heart icon
			favHeart = (ImageButton) findViewById(R.id.fav_heart);
			favHeart.setImageResource(R.drawable.favorites_unselected_100);
		}
		recentSearches.close();

		// Set header font to Times New Roman
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
		defaultCol.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		sortingField = Sort.THIRD_ASC;

		printReviews(Type.COURSE);
		
		// Set the onclick callback for header
		TextView header = (TextView) findViewById(R.id.header);
		header.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Constants.RESULT_GO_TO_START);
				DisplayReviewsForCourse.this.finish();
			}
		});
	}

}