package edu.upenn.cis.cis350.display;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Parser;

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

		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView searchPCRView = (TextView) findViewById(R.id.header);
		searchPCRView.setTypeface(timesNewRoman);
	}
}