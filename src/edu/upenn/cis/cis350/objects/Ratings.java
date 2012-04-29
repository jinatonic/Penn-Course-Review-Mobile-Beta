package edu.upenn.cis.cis350.objects;

import edu.upenn.cis.cis350.backend.Constants;

public class Ratings {
	private String amountLearned;
	private String commAbility;
	private String courseQuality;
	private String difficulty;
	private String instructorAccess;
	private String instructorQuality;
	private String readingsValue;
	private String recommendMajor;
	private String recommendNonMajor;
	private String stimulateInterest;
	private String workRequired;

	//default value for null ratings is 0
	public Ratings(Double _amountLearned, Double _commAbility, Double _courseQuality, Double _difficulty, Double _instructorAccess,
			Double _instructorQuality, Double _readingsValue, Double _recommendMajor, Double _recommendNonMajor, Double _stimulateInterest,
			Double _workRequired) {
		this.amountLearned = (_amountLearned!=null) ? _amountLearned.toString() : Constants.NA;
		this.commAbility = (_commAbility!=null) ? _commAbility.toString() : Constants.NA;
		this.courseQuality = (_courseQuality!=null) ? _courseQuality.toString() : Constants.NA;
		this.difficulty = (_difficulty!=null) ? _difficulty.toString() : Constants.NA;
		this.instructorAccess = (_instructorAccess!=null) ? _instructorAccess.toString() : Constants.NA;
		this.instructorQuality = (_instructorQuality!=null) ? _instructorQuality.toString() : Constants.NA;
		this.readingsValue = (_readingsValue!=null) ? _readingsValue.toString() : Constants.NA;
		this.recommendMajor = (_recommendMajor!=null) ? _recommendMajor.toString() : Constants.NA;
		this.recommendNonMajor = (_recommendNonMajor!=null) ? _recommendNonMajor.toString() : Constants.NA;
		this.stimulateInterest = (_stimulateInterest!=null) ? _stimulateInterest.toString() : Constants.NA;
		this.workRequired = (_workRequired!=null) ? _workRequired.toString() : Constants.NA;
	}
	//returns rating given by r, -1 on error
	public String getRating(int r){
		if(r == Constants.amountLearnedId) return amountLearned;
		if(r == Constants.commAbilityId) return commAbility;
		if(r == Constants.courseQualityId) return courseQuality;
		if(r == Constants.difficultyId) return difficulty;
		if(r == Constants.instructorAccessId) return instructorAccess;
		if(r == Constants.instructorQualityId) return instructorQuality;
		if(r == Constants.readingsValueId) return readingsValue;
		if(r == Constants.recommendMajorId) return recommendMajor;
		if(r == Constants.recommendNonMajorId) return recommendNonMajor;
		if(r == Constants.stimulateInterestId) return stimulateInterest;
		if(r == Constants.workRequiredId) return workRequired;
		
		return "err";
	}
	
	public String getAmountLearned() { return amountLearned; }
	public String getCommAbility() { return commAbility; }
	public String getCourseQuality() { return courseQuality; }
	public String getDifficulty() { return difficulty; }
	public String getInstructorAccess() { return instructorAccess; }
	public String getInstructorQuality() { return instructorQuality; }
	public String getReadingsValue() { return readingsValue; }
	public String getRecommendMajor() { return recommendMajor; }
	public String getRecommendNonMajor() { return recommendNonMajor; }
	public String getStimulateInterest() { return stimulateInterest; }
	public String getWorkRequired() { return workRequired; }
	
	public String toString() {
		return "Amount Learned: " + amountLearned +
				"\nCommunication Ability: " + commAbility + 
				"\nCourse Quality: "+ courseQuality +
				"\nDifficulty: " + difficulty + 
				"\nInstructor Access: " + instructorAccess + 
				"\nInstructor Quality: " + instructorQuality +
				"\nReadings Value: " + readingsValue + 
				"\nRecommend to a Major: " + recommendMajor +
				"\nRecommend to a nonmajor: " + recommendNonMajor+
				"\nStimulate Interest: " + stimulateInterest + 
				"\nWork Required: " + workRequired;
	}
}