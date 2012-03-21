package edu.upenn.cis.cis350.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONRequest {

	public static JSONObject retrieveJSONObject(String path){
		int count = 0;
		
		while(count < Constants.MAX_ATTEMPTS_FOR_CONNECTION) {
			
			try{
				URL url = new URL(path);
				Log.w("Parser: retrieveJSONObject", "url=" + url);
				URLConnection connection = url.openConnection();
				String line;
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				Log.v("Length", builder.toString());
				reader.close();
				return new JSONObject(builder.toString());
			}
			catch(IOException e) {
				Log.w("Parser: retrieveJSONObject", "IOException: Bad Url");
				e.printStackTrace();
				return null;
			} catch (JSONException e) {
				Log.w("Parser: retrieveJSONObject", "JSONException: mis-formatted JSON");
				count++;
				Log.w("JSONRequest", "reconnecting: attempt #" + count);
				continue;
			}
			
		}
		return null;
	}
}
