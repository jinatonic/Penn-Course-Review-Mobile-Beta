package edu.upenn.cis.cis350.objects;

import java.util.ArrayList;

import edu.upenn.cis.cis350.backend.Constants;

/**
 * Represents a Course object with all of its fields averaged across all sections
 * Used by Departments
 * @author Charles Kong
 */

public class CourseAverage {
	private String name;
	private String id;
	private String path;
	private Ratings ratings;

	public CourseAverage(String _name, String _id, String _path, ArrayList<Course> courses) {
		path = _path;
		name = _name;
		id = _id;
		ratings = computeAverageRatings(courses);
	}
	
	public CourseAverage(String _name, String _id, String _path, Ratings rating) {
		path = _path;
		name = _name;
		id = _id;
		ratings = rating;
	}

	public Ratings getRatings() {
		return ratings;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	//Computes the averages for each type of rating in the ArrayList. A Course object contains a Rating object, and 
	//each Rating object has a field for each type of Rating. 
	private Ratings computeAverageRatings(ArrayList<Course> courses) {

		double count_amountLearned = 0;
		double count_commAbility = 0;
		double count_courseQuality = 0;
		double count_difficulty = 0;
		double count_instructorAccess = 0;
		double count_instructorQuality = 0;
		double count_readingsValue = 0;
		double count_recommendMajor = 0;
		double count_recommendNonMajor = 0;
		double count_stimulateInterest = 0;
		double count_workRequired = 0;
		double tot_amountLearned = 0;
		double tot_commAbility = 0;
		double tot_courseQuality = 0;
		double tot_difficulty = 0;
		double tot_instructorAccess = 0;
		double tot_instructorQuality = 0;
		double tot_readingsValue = 0;
		double tot_recommendMajor = 0;
		double tot_recommendNonMajor = 0;
		double tot_stimulateInterest = 0;
		double tot_workRequired = 0;

		for(int i = 0; i < courses.size(); i++){
			Ratings r = courses.get(i).getRatings();
			if(!r.getAmountLearned().equals(Constants.NA)){
				tot_amountLearned += new Double(r.getAmountLearned());
				count_amountLearned += 1;
			}
			if(!r.getCommAbility().equals(Constants.NA)){
				tot_commAbility += new Double(r.getCommAbility());
				count_commAbility += 1;
			}
			if(!r.getCourseQuality().equals(Constants.NA)){
				tot_courseQuality += new Double(r.getCourseQuality());
				count_courseQuality += 1;
			}
			if(!r.getDifficulty().equals(Constants.NA)){
				tot_difficulty += new Double(r.getDifficulty());
				count_difficulty += 1;
			}
			if(!r.getInstructorAccess().equals(Constants.NA)){
				tot_instructorAccess += new Double(r.getInstructorAccess());
				count_instructorAccess += new Double(r.getInstructorAccess());
			}
			if(!r.getInstructorQuality().equals(Constants.NA)){
				tot_instructorQuality += new Double(r.getInstructorQuality());
				count_instructorQuality += 1;
			}
			if(!r.getReadingsValue().equals(Constants.NA)){
				tot_readingsValue += new Double(r.getReadingsValue());
				count_readingsValue += 1;
			}
			if(!r.getRecommendMajor().equals(Constants.NA)){
				tot_recommendMajor += new Double(r.getRecommendMajor());
				count_recommendMajor += 1;
			}
			if(!r.getRecommendNonMajor().equals(Constants.NA)){
				tot_recommendNonMajor += new Double(r.getRecommendNonMajor());
				count_recommendNonMajor += 1;
			}
			if(!r.getStimulateInterest().equals(Constants.NA)){
				tot_stimulateInterest += new Double(r.getStimulateInterest());
				count_stimulateInterest += 1;
			}
			if(!r.getWorkRequired().equals(Constants.NA)){
				tot_workRequired += new Double(r.getWorkRequired());
				count_workRequired += 1;
			}

		}

		int temp_amountLearned = (int)((tot_amountLearned/count_amountLearned) * 100);
		double avg_amountLearned = (double)temp_amountLearned/100.0;

		int temp_commAbility = (int)((tot_commAbility/count_commAbility) * 100);
		double avg_commAbility = (double)temp_commAbility/100.0;

		int temp_courseQuality = (int)((tot_courseQuality/count_courseQuality) * 100);
		double avg_courseQuality = (double)temp_courseQuality/100.0;

		int temp_difficulty = (int)((tot_difficulty/count_difficulty) * 100);
		double avg_difficulty = (double)temp_difficulty/100.0;

		int temp_instructorAccess = (int)((tot_instructorAccess/count_instructorAccess) * 100);
		double avg_instructorAccess = (double)temp_instructorAccess/100.0;

		int temp_instructorQuality = (int)((tot_instructorQuality/count_instructorQuality) * 100);
		double avg_instructorQuality = (double)temp_instructorQuality/100.0;

		int temp_readingsValue = (int)((tot_readingsValue/count_readingsValue) * 100);
		double avg_readingsValue = (double)temp_readingsValue/100.0;

		int temp_recommendMajor = (int)((tot_recommendMajor/count_recommendMajor) * 100);
		double avg_recommendMajor = (double)temp_recommendMajor/100.0;

		int temp_recommendNonMajor = (int)((tot_recommendNonMajor/count_recommendNonMajor) * 100);
		double avg_recommendNonMajor = (double)temp_recommendNonMajor/100.0;

		int temp_stimulateInterest = (int)((tot_stimulateInterest/count_stimulateInterest) * 100);
		double avg_stimulateInterest = (double)temp_stimulateInterest/100.0;

		int temp_workRequired = (int)((tot_workRequired/count_workRequired) * 100);
		double avg_workRequired = (double)temp_workRequired/100.0;

		Ratings avgRatings = new Ratings((count_amountLearned < 1) ? null : avg_amountLearned, (count_commAbility < 1) ? null : avg_commAbility,
				(count_courseQuality < 1) ? null : avg_courseQuality, (count_difficulty < 1) ? null : avg_difficulty,
				(count_instructorAccess < 1) ? null : avg_instructorAccess, (count_instructorQuality < 1) ? null : avg_instructorQuality,
				(count_readingsValue < 1) ? null : avg_readingsValue, (count_recommendMajor < 1) ? null : avg_recommendMajor,
				(count_recommendNonMajor < 1) ? null : avg_recommendNonMajor, (count_stimulateInterest < 1) ? null : avg_stimulateInterest,
				(count_workRequired < 1) ? null : avg_workRequired);
		
		return avgRatings;
	}

}
