package edu.upenn.cis.cis350.backend;

import android.util.Log;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class Normalizer {

	/** 
	 * Given user input string, normalize to form that is query-able from database
	 * @return normalized string if works, null if input is invalid
	 */
	public static String normalize(String input, Type type) {
		String output = null;
		switch (type) {
		case INSTRUCTOR:
			output = input.toLowerCase();
			break;
		case COURSE:
		case DEPARTMENT: 
			String temp = input.substring(input.indexOf('-')+1);
			int secondIndex = temp.indexOf('-') + input.length() - temp.length();
			// If there are two '-' characters
			// remove everything after second '-'
			output = input.substring(0, secondIndex);
			output = output.trim();
			// remove first '-'
			output = output.replace("-", "");
			output = output.toLowerCase(); 
			break;
		default:
			break;
		}
		
		Log.w("Normalizer", "input is " + input + " output is " + output);
		return output;
	}
}
