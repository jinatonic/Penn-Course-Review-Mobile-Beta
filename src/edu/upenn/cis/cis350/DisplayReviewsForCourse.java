package edu.upenn.cis.cis350;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayReviewsForCourse extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_reviews);
		Intent i = getIntent();
		String searchTerm = i.getStringExtra(getResources().getString(R.string.SEARCH_TERM));
		Parser p = new Parser();
		String courseReviews = "";
		try {
			courseReviews = p.getReviewsForCourse(searchTerm);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		TextView title = (TextView)findViewById(R.id.reviews);
		title.setText(courseReviews);
	}
}