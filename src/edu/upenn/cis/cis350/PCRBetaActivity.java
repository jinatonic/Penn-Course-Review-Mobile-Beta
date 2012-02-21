package edu.upenn.cis.cis350;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.Iterator;

import org.json.*;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class PCRBetaActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Parser p = new Parser();
		String s ="Hi";
		try {
			s = p.getReviewsForCourse("CIS277");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			s = "What";
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			s = "Huh";
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			s = "Why";
			e.printStackTrace();
		}
		TextView title = (TextView)findViewById(R.id.test);
		title.setText(s);
	}
}