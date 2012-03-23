package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Sorter;
import edu.upenn.cis.cis350.database.SearchCache;

import edu.upenn.cis.cis350.objects.Course;

/* Display all reviews for a specific course */
public class DisplayReviewsForCourse extends Activity {

	public enum Sort {INSTRUCTOR_ASC, INSTRUCTOR_DES, NAME_ASC, NAME_DES, CQ_ASC, 
		CQ_DES, IQ_ASC, IQ_DES, DIFFICULTY_ASC, DIFFICULTY_DES }
	Sort sortingField;
	ArrayList<Course> courseReviews;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.course_reviews);

		// Get course reviews for the search term
		Intent i = getIntent();
		String searchTerm = i.getStringExtra(getResources().getString(R.string.SEARCH_TERM));

		// Search database first
		SearchCache cache = new SearchCache(this.getApplicationContext());
		cache.open();
		courseReviews = cache.getCourse(searchTerm);
		cache.close();

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.header);
		searchPCRView.setTypeface(timesNewRoman);

		// Top half of page under PCR header
		TextView number = (TextView)findViewById(R.id.course_number);
		number.setTypeface(timesNewRoman);
		if (courseReviews == null || courseReviews.size() == 0) {
			number.setText("No reviews found for this course.");
			return;
		}

		// Set the text below the PCR header - course ID (alias), course name, course description
		number.setText(courseReviews.get(0).getAlias());
		TextView name = (TextView) findViewById(R.id.course_name);
		name.setText(courseReviews.get(0).getName());
		name.setTypeface(timesNewRoman);
		TextView description = (TextView)findViewById(R.id.course_description);
		description.setText(courseReviews.get(0).getDescription());

		// Set instructor name to be thing its sorted by first
		// TODO(cymai): see if you should change default
		TextView defaultTab = (TextView) findViewById(R.id.difficulty_tab);
		defaultTab.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		sortingField = Sort.DIFFICULTY_ASC;

		printReviews();

	}

	public void printReviews() {
		// Iterate through reviews for course and fill table cells
		Iterator<Course> iter = courseReviews.iterator();
		TableLayout tl = (TableLayout)findViewById(R.id.reviews);
		tl.removeAllViews();
		while(iter.hasNext()) {
			Course curCourse = iter.next();

			/* Create a new row to be added. */
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			/* Create a TextView for Instructor to be the row-content. */
			TextView instructor = new TextView(this);
			instructor.setHeight(35);
			instructor.setTextSize((float)9.5);
			instructor.setTextColor(getResources().getColor(R.color.text_gray));
			instructor.setText(curCourse.getInstructor().getName());
			instructor.setBackgroundResource(R.layout.cell_gridline);
			instructor.setClickable(true);
			LayoutParams insParams = new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			insParams.column = 1;
			instructor.setLayoutParams(insParams);
			/* Add TextView to row. */
			tr.addView(instructor);

			/* Create a TextView for Course Quality to be the row-content. */
			TextView courseQuality = new TextView(this);
			courseQuality.setHeight(35);
			courseQuality.setTextSize((float)9.5);
			courseQuality.setTextColor(getResources().getColor(R.color.text_gray));
			courseQuality.setGravity(Gravity.CENTER_HORIZONTAL);
			courseQuality.setText(((Double)curCourse.getRatings().getCourseQuality()).toString());
			courseQuality.setBackgroundResource(R.layout.cell_gridline);
			courseQuality.setClickable(true);
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
			instructorQuality.setText(((Double)curCourse.getRatings().getInstructorQuality()).toString());
			instructorQuality.setBackgroundResource(R.layout.cell_gridline);
			instructorQuality.setClickable(true);
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
			difficulty.setText(((Double)curCourse.getRatings().getDifficulty()).toString());
			difficulty.setBackgroundResource(R.layout.cell_gridline);
			difficulty.setClickable(true);
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
		tl.invalidate();
	}

	public void onClickSort(View v) {
		Sorter s = new Sorter();
		if(v.getId() == R.id.instructor_tab) {
			if (sortingField == Sort.INSTRUCTOR_ASC) {
				// TODO(cymai): change to instructor
				courseReviews = s.sortByRating(courseReviews, "difficulty", 1);
				sortingField = Sort.INSTRUCTOR_DES;
			} else {
				courseReviews = s.sortByRating(courseReviews, "difficulty", 0);
				sortingField = Sort.INSTRUCTOR_ASC;
			}
		} else if(v.getId() == R.id.course_quality_tab) {
			if (sortingField == Sort.CQ_ASC) {
				courseReviews = s.sortByRating(courseReviews, "courseQuality", 1);
				sortingField = Sort.CQ_DES;
			} else {
				courseReviews = s.sortByRating(courseReviews, "courseQuality", 0);
				sortingField = Sort.CQ_ASC;
			}
		} else if(v.getId() == R.id.instructor_quality_tab) {
			if (sortingField == Sort.IQ_ASC) {
				courseReviews = s.sortByRating(courseReviews, "instructorQuality", 1);
				sortingField = Sort.IQ_DES;
			} else {
				courseReviews = s.sortByRating(courseReviews, "instructorQuality", 0);
				sortingField = Sort.IQ_ASC;
			}
		} else if(v.getId() == R.id.difficulty_tab) {
			if (sortingField == Sort.DIFFICULTY_ASC) {
				courseReviews = s.sortByRating(courseReviews, "difficulty", 1);
				sortingField = Sort.DIFFICULTY_DES;
			} else {
				courseReviews = s.sortByRating(courseReviews, "difficulty", 0);
				sortingField = Sort.DIFFICULTY_ASC;
			}
		}
		findViewById(R.id.instructor_tab).setBackgroundColor(0);
		findViewById(R.id.course_quality_tab).setBackgroundColor(0);
		findViewById(R.id.instructor_quality_tab).setBackgroundColor(0);
		findViewById(R.id.difficulty_tab).setBackgroundColor(0);
		v.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		printReviews();
	}
}