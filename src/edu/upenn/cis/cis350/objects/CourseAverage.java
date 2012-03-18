package edu.upenn.cis.cis350.objects;

import java.util.ArrayList;

public class CourseAverage {
	private String name;
	private String id;
	private String path;
	private Ratings ratings;
	
	public CourseAverage(String _name, String _id, String _path, ArrayList<Course> courses) {

		path = _path;
		name = _name;
		id = _name;
		ratings = computeAverageRatings(courses);
	}
	
	private Ratings computeAverageRatings(ArrayList<Course> courses){
		
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
			if(r.getAmountLearned() > 0){
				tot_amountLearned += r.getAmountLearned();
				count_amountLearned += 1;
			}
			if(r.getCommAbility() > 0){
				tot_commAbility += r.getCommAbility();
				count_commAbility += 1;
			}
			if(r.getCourseQuality() > 0){
				tot_courseQuality += r.getCourseQuality();
				count_courseQuality += 1;
			}
			if(r.getDifficulty() > 0){
				tot_difficulty += r.getDifficulty();
				count_difficulty += 1;
			}
			if(r.getInstructorAccess() > 0){
				tot_instructorAccess += r.getInstructorAccess();
				count_instructorAccess += r.getInstructorAccess();
			}
			if(r.getInstructorQuality() > 0){
				tot_instructorQuality += r.getInstructorQuality();
				count_instructorQuality += 1;
			}
			if(r.getReadingsValue() > 0){
				tot_readingsValue += r.getReadingsValue();
				count_readingsValue += 1;
			}
			if(r.getRecommendMajor() > 0){
				tot_recommendMajor += r.getRecommendMajor();
				count_recommendMajor += 1;
			}
			if(r.getRecommendNonMajor() > 0){
				tot_recommendNonMajor += r.getRecommendNonMajor();
				count_recommendNonMajor += 1;
			}
			if(r.getStimulateInterest() > 0){
				tot_stimulateInterest += r.getStimulateInterest();
				count_stimulateInterest += 1;
			}
			if(r.getWorkRequired() > 0){
				tot_workRequired += r.getWorkRequired();
				count_workRequired += 1;
			}
			
		}
		double avg_amountLearned = tot_amountLearned/count_amountLearned;
	    double avg_commAbility = tot_commAbility/count_commAbility;
		double avg_courseQuality = tot_courseQuality/count_courseQuality;
		double avg_difficulty = tot_difficulty/count_difficulty;
		double avg_instructorAccess = tot_instructorAccess/count_instructorAccess;
		double avg_instructorQuality = tot_instructorQuality/count_instructorQuality;
		double avg_readingsValue = tot_readingsValue/count_readingsValue;
		double avg_recommendMajor = tot_recommendMajor/count_recommendMajor;
		double avg_recommendNonMajor = tot_recommendNonMajor/count_recommendNonMajor;
		double avg_stimulateInterest = tot_stimulateInterest/count_stimulateInterest;
		double avg_workRequired = tot_workRequired/count_workRequired;
		Ratings avgRatings = new Ratings(avg_amountLearned, avg_commAbility, avg_courseQuality,
				avg_difficulty, avg_instructorAccess, avg_instructorQuality, avg_readingsValue,
				avg_recommendMajor, avg_recommendNonMajor, avg_stimulateInterest, avg_workRequired);
		return avgRatings;
	}

}
