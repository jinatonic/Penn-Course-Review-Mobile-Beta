package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
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

	/* GUYS, DAFUQ IS THIS?????
	public void showDetails() {
		int x = 0;
		x++;
	}
	*/

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
			TextView instructor = createRow(165, Gravity.LEFT, curCourse.getInstructor().getName(), 1);
			tr.addView(instructor);

			/* Create a TextView for Course Quality to be the row-content. */
			TextView courseQuality = createRow(89, Gravity.CENTER_HORIZONTAL, ((Double)curCourse.getRatings().getCourseQuality()).toString(), 2);
			tr.addView(courseQuality);

			/* Create a TextView for Instructor Quality to be the row-content. */
			TextView instructorQuality = createRow(89, Gravity.CENTER_HORIZONTAL, ((Double)curCourse.getRatings().getInstructorQuality()).toString(), 3);
			tr.addView(instructorQuality);

			/* Create a TextView for Difficulty to be the row-content. */
			TextView difficulty = createRow(89, Gravity.CENTER_HORIZONTAL, ((Double)curCourse.getRatings().getDifficulty()).toString(), 4);
			tr.addView(difficulty);

			/* Add row to TableLayout. */
			tl.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}
		tl.invalidate();
		
		tl.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View view) {
				android.app.Dialog dialog = new android.app.Dialog(DisplayReviewsForCourse.this);
				dialog.setContentView(R.layout.main_dialog);
			
				dialog.setCancelable(true);

				dialog.show();
				return true;
			}
		});
		
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

	TextView createRow(int width, int gravity, String text, int colNum) {
		TextView row = new TextView(this);
		row.setHeight(35);
		row.setWidth(width);
		row.setTextSize((float)9.5);
		row.setTextColor(getResources().getColor(R.color.text_gray));
		row.setGravity(gravity);
		row.setText(text);
		row.setBackgroundResource(R.layout.cell_gridline);
		row.setClickable(true);
		LayoutParams diffParams = new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		diffParams.column = colNum;
		row.setLayoutParams(diffParams);
		return row;
	}
}