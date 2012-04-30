package edu.upenn.cis.cis350.backend;

import android.util.Log;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;


/**
 * Helper class to perform String manipulation for normalization
 * @author Jinyan Cao
 */

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
	
	/**
	 * Given input string, change the string to capitalize first letter of each word
	 */
	public static String convertCase(String input) {
		input = input.toLowerCase();
		final StringBuilder result = new StringBuilder(input.length());
		String[] words = input.split("\\s");
		for (int i = 0; i < words.length; ++i) {
		  if(i > 0) { 
			  result.append(" "); 
		  }
		  if (words[i].length() <= 1) {
			  result.append(words[i]);
		  }
		  else {
			  result.append(Character.toUpperCase(words[i].charAt(0)))
			        .append(words[i].substring(1));
		  }
		}
		
		return result.toString();
	}
}
