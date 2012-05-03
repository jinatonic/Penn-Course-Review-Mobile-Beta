package edu.upenn.cis.cis350.backend;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.util.Log;
import edu.upenn.isc.fastPdfServiceClient.api.FpsPennGroupsHasMember;

/**
 * Helper class to process PennKey authentication
 * @author Charles Kong
 */

public class PennAuthenticate extends AsyncTask<Void, Void, Boolean>{
	
	private String serialNumber;
	private final String path = "http://www.penncoursereview.com/androidapp/auth?serial=";
	
	public PennAuthenticate(String _serialNumber){
		serialNumber = _serialNumber;
	}
	
	
	protected Boolean doInBackground(Void... params) {
		URL url;
		try {
			url = new URL(path + serialNumber);
		
		Log.w("Retrieving pennkey", "url=" + url);
		URLConnection connection = url.openConnection();
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		Log.v("pennKey is", builder.toString());
		String pennKey = builder.toString();
		reader.close();
		boolean hasMember = new FpsPennGroupsHasMember().assignGroupName("penn:isc:ait:apps:pennCourseReview:groups:pennCourseReviewStudents").assignSubjectSourceId("pennperson").assignSubjectIdentifier(pennKey).executeReturnBoolean();
		return hasMember;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
