package edu.upenn.cis.cis350.display;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.objects.Course;

public class DisplayReviewsForCourse extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.course_reviews);
		Intent i = getIntent();
		String searchTerm = i.getStringExtra(getResources().getString(R.string.SEARCH_TERM));
		Parser p = new Parser();
		ArrayList<Course> courseReviews = new ArrayList<Course>();
		try {
			courseReviews = p.getReviewsForCourse(searchTerm);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Top half of page, course name/description
		TextView number = (TextView)findViewById(R.id.course_number);
		if (courseReviews.size() == 0) {
			number.setText("No reviews found for this course.");
			return;
		}
		number.setText(courseReviews.get(0).getAlias());
		TextView name = (TextView) findViewById(R.id.course_name);
		name.setText(courseReviews.get(0).getName());
		TextView description = (TextView)findViewById(R.id.course_description);
		description.setText(courseReviews.get(0).getDescription());

		Iterator<Course> iter = courseReviews.iterator();

		// Bottom half of page, reviews for course
		// Fill table cells with each Course's fields from courseReviews
		TableLayout tl = (TableLayout)findViewById(R.id.reviews);
		while(iter.hasNext()) {
			Course curCourse = iter.next();
			/* Create a new row to be added. */
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			/* Create a TextView to be the row-content. */
			TextView instructor = new TextView(this);
			instructor.setText(curCourse.getInstructor().getName());
			instructor.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			/* Add TextView to row. */
			tr.addView(instructor);
			/* Add row to TableLayout. */
			tl.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			
			TextView courseQuality = new TextView(this);
			courseQuality.setText(((Double)curCourse.getRatings().getDifficulty()).toString());
			courseQuality.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			/* Add TextView to row. */
			tr.addView(courseQuality);
			/* Add row to TableLayout. */
			tl.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			
			TextView instructorQuality = new TextView(this);
			instructorQuality.setText(((Double)curCourse.getRatings().getInstructorQuality()).toString());
			instructorQuality.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			/* Add TextView to row. */
			tr.addView(instructorQuality);
			/* Add row to TableLayout. */
			tl.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			
			TextView difficulty = new TextView(this);
			difficulty.setText(((Double)curCourse.getRatings().getDifficulty()).toString());
			difficulty.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			/* Add TextView to row. */
			tr.addView(difficulty);
			/* Add row to TableLayout. */
			tl.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.header);
		searchPCRView.setTypeface(timesNewRoman);
	}
}