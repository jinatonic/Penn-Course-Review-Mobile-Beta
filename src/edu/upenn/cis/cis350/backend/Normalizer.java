package edu.upenn.cis.cis350.backend;

public class Normalizer {

	/** 
	 * Given user input string, normalize to form that is query-able from database
	 * @return normalized string if works, null if input is invalid
	 */
	public static String normalize(String input) {
		String output = input.trim().toLowerCase();
		output = output.replace("-", "");
		// If it's a course number, e.g. cis121
		if (output.matches("^.*?[0-9]+.*$")) {
			if (output.length() > 7) return null;
			String dept = "";
			String num = "";
			for (int i = 0; i < output.length(); i++) {
				if (output.charAt(i) >= 48 && output.charAt(i) <= 57) {
					dept = output.substring(0,i);
					num = output.substring(i);
					break;
				}
			}
			
			if (!num.matches("^[0-9]*$"))
				return null;
			
			return dept + "-" + num;
		}
		// If it doesn't contain any numbers, that means it's either professor name or department name
		else {
			return output;	// TODO fix
		}
	}
	
}
