package edu.upenn.cis.cis350.backend;

import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class Normalizer {

	/** 
	 * Given user input string, normalize to form that is query-able from database
	 * @return normalized string if works, null if input is invalid
	 */
	public static String normalize(String input, Type type) {
		switch (type) {
		case INSTRUCTOR:
			return input.toLowerCase();
		case COURSE:
		case DEPARTMENT: 
			int secondIndex = input.substring(input.indexOf('-')+1).indexOf('-');
			// If there are two '-' characters
			// remove everything after second '-'
			input = input.substring(0, secondIndex);
			input = input.trim();
			// remove first '-'
			input = input.replace("-", "");
			return input.toLowerCase(); 
		default:
			break;
		}
		
		return null;
	}
}
