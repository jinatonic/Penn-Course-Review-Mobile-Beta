package edu.upenn.cis.cis350.display;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.objects.Course;

public class DisplayReviewsForCourse extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		number.setText(courseReviews.get(0).getAlias());
		TextView name = (TextView) findViewById(R.id.course_name);
		name.setText(courseReviews.get(0).getName());
		TextView description = (TextView)findViewById(R.id.course_description);
		description.setText(courseReviews.get(0).getDescription());
		
		//Bottom half of page, reviews for course
		// Fill table cells with each Course's fields from courseReviews

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.header);
		searchPCRView.setTypeface(timesNewRoman);
	}
}