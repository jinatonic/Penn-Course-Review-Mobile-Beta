package edu.upenn.cis.cis350.display;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import edu.upenn.cis.cis350.database.DepartmentSearchCache;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

/* Display all reviews for a specific dept */
public class DisplayReviewsForDept extends Display {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dept_reviews);

		// Get course reviews for the search term
		Intent i = getIntent();
		String searchTerm = i.getStringExtra(getResources().getString(R.string.SEARCH_TERM));

		// Initialize cache so parser can use it
		DepartmentSearchCache cache = new DepartmentSearchCache(this.getApplicationContext());
		cache.open();
		Department dept = cache.getDepartment(searchTerm); // TODO CHANGE
		// Always close DB after using it!
		cache.close();

		// Set font to Times New Roman
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
		TextView name = (TextView) findViewById(R.id.dept_name);
		name.setText(dept.getName());
		name.setTypeface(timesNewRoman);
		
		// Set difficulty to be thing its sorted by first
		TextView defaultTab = (TextView) findViewById(R.id.difficulty_tab);
		defaultTab.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		sortingField = Sort.DIFFICULTY_ASC;

		printReviews(Type.DEPARTMENT);
	}
	
}