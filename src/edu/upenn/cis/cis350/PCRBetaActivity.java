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
		setContentView(R.layout.search_page);
	}
}