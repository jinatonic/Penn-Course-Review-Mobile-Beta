package edu.upenn.cis.cis350.display;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.database.DepartmentSearchCache;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

/* Display all reviews for a specific dept */
public class DisplayReviewsForDept extends Display {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dept_reviews);

		// Get course reviews for the search term
		Intent i = getIntent();
		String type = i.getStringExtra(getResources().getString(R.string.SEARCH_TYPE));
		String alias = i.getStringExtra(getResources().getString(R.string.SEARCH_ALIAS));
		String name = i.getStringExtra(getResources().getString(R.string.SEARCH_NAME));

		keyword = type + alias + " - " + name;

		Log.w("DisplayReviewsForDepartment", "Displaying information for " + keyword);

		// Initialize cache so parser can use it
		DepartmentSearchCache cache = new DepartmentSearchCache(this.getApplicationContext());
		cache.open();
		Department dept = cache.getDepartment(alias); 
		// Always close DB after using it!
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

		// Check if the department was found
		TextView number = (TextView)findViewById(R.id.dept_number);
		number.setTypeface(timesNewRoman);
		if (dept == null) {
			number.setText("No reviews found for this department.");
			return;
		}

		courseAvgs = dept.getCourseAverages(); // filling in courseAvgs for use by Display

		// Top half of page under PCR header - check if dept found
		if ((courseAvgs.size() == 0) || (courseAvgs == null)) {
			number.setText("No reviews found for this department.");
			return;
		}

		// Set the text below the PCR header - course ID (alias), course name, course description
		number.setText(dept.getId());
		TextView name_view = (TextView) findViewById(R.id.dept_name);
		name_view.setText(dept.getName());
		name_view.setTypeface(timesNewRoman);

		// Set difficulty to be thing its sorted by first
		TextView defaultTab = (TextView) findViewById(R.id.dept_third_tab);
		defaultTab.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		sortingField = Sort.DIFFICULTY_ASC;

		printReviews(Type.DEPARTMENT);
		
		// Set the onclick callback for header
		TextView header = (TextView) findViewById(R.id.header);
		header.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Constants.RESULT_GO_TO_START);
				DisplayReviewsForDept.this.finish();
			}
		});
	}

}