package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.backend.SearchCache;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.Ratings;

/* Display all reviews for a specific dept */
public class DisplayReviewsForDept extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dept_reviews);

		// Get course reviews for the search term
		Intent i = getIntent();
		String searchTerm = i.getStringExtra(getResources().getString(R.string.SEARCH_TERM));

		// Initialize cache so parser can use it
		SearchCache cache = new SearchCache(this.getApplicationContext());
		cache.open();
		Parser p = new Parser(cache);
		Department dept = p.getReviewsForDept(searchTerm);
		ArrayList<CourseAverage> courseAvgs = dept.getCourseAverages();
		// Always close DB after using it!
		cache.close();

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.header);
		searchPCRView.setTypeface(timesNewRoman);
		
		// Top half of page under PCR header
		TextView number = (TextView)findViewById(R.id.dept_number);
		if (dept == null || (courseAvgs.size() == 0) || (courseAvgs == null)) {
			number.setText("No reviews found for this department.");
			return;
		}

		// Set the text below the PCR header - course ID (alias), course name, course description
		number.setText(dept.getId());
		number.setTypeface(timesNewRoman);
		TextView name = (TextView) findViewById(R.id.dept_name);
		name.setText(dept.getName());
		name.setTypeface(timesNewRoman);

		// Iterate through avg course reviews for dept and fill table cells
		Iterator<CourseAverage> iter = courseAvgs.iterator();
		TableLayout tl = (TableLayout)findViewById(R.id.reviews);
		while(iter.hasNext()) {
			CourseAverage curCourseAvg = iter.next();
			Ratings curRatings = curCourseAvg.getRatings();

			/* Create a new row to be added. */
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			/* Create a TextView for Course ID to be the row-content. */
			TextView courseId = new TextView(this);
			courseId.setHeight(35);
			courseId.setTextSize((float)9.5);
			courseId.setTextColor(getResources().getColor(R.color.text_gray));
			courseId.setText(curCourseAvg.getId());
			LayoutParams insParams = new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			insParams.column = 1;
			courseId.setLayoutParams(insParams);
			/* Add TextView to row. */
			tr.addView(courseId);

			/* Create a TextView for Course Quality to be the row-content. */
			TextView courseQuality = new TextView(this);
			courseQuality.setHeight(35);
			courseQuality.setTextSize((float)9.5);
			courseQuality.setTextColor(getResources().getColor(R.color.text_gray));
			courseQuality.setGravity(Gravity.CENTER_HORIZONTAL);
			courseQuality.setText(((Double)curRatings.getCourseQuality()).toString());
			LayoutParams courseParams = new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			courseParams.column = 2;
			courseQuality.setLayoutParams(courseParams);
			/* Add TextView to row. */
			tr.addView(courseQuality);

			/* Create a TextView for Instructor Quality to be the row-content. */
			TextView instructorQuality = new TextView(this);
			instructorQuality.setHeight(35);
			instructorQuality.setTextSize((float)9.5);
			instructorQuality.setTextColor(getResources().getColor(R.color.text_gray));
			instructorQuality.setGravity(Gravity.CENTER_HORIZONTAL);
			instructorQuality.setText(((Double)curRatings.getInstructorQuality()).toString());
			LayoutParams insQualParams = new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			insQualParams.column = 3;
			instructorQuality.setLayoutParams(insQualParams);
			/* Add TextView to row. */
			tr.addView(instructorQuality);

			/* Create a TextView for Difficulty to be the row-content. */
			TextView difficulty = new TextView(this);
			difficulty.setHeight(35);
			difficulty.setTextSize((float)9.5);
			difficulty.setTextColor(getResources().getColor(R.color.text_gray));
			difficulty.setGravity(Gravity.CENTER_HORIZONTAL);
			difficulty.setText(((Double)curRatings.getDifficulty()).toString());
			LayoutParams diffParams = new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			diffParams.column = 4;
			difficulty.setLayoutParams(diffParams);
			/* Add TextView to row. */
			tr.addView(difficulty);

			/* Add row to TableLayout. */
			tl.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}
	}
}