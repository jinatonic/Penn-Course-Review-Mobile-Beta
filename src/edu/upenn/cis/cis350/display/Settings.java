package edu.upenn.cis.cis350.display;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Settings extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.settings_layout);
	}

}
