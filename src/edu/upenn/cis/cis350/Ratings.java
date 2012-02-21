package edu.upenn.cis.cis350;

public class Ratings {
	private double amountLearned;
	private double commAbility;
	private double courseQuality;
	private double difficulty;
	private double instructorAccess;
	private double instructorQuality;
	private double readingsValue;
	private double recommendMajor;
	private double recommendNonMajor;
	private double stimulateInterest;
	private double workRequired;
	
	public Ratings(Double _amountLearned, Double _commAbility, Double _courseQuality, Double _difficulty, Double _instructorAccess,
			Double _instructorQuality, Double _readingsValue, Double _recommendMajor, Double _recommendNonMajor, Double _stimulateInterest,
			Double _workRequired) {
		
		amountLearned = _amountLearned.doubleValue();
		commAbility = _commAbility.doubleValue();
		courseQuality = _courseQuality.doubleValue();
		difficulty = _difficulty.doubleValue();
		instructorAccess = _instructorAccess.doubleValue();
		instructorQuality = _instructorQuality.doubleValue();
		readingsValue = _readingsValue.doubleValue();
		recommendMajor = _recommendMajor.doubleValue();
		recommendNonMajor = _recommendNonMajor.doubleValue();
		stimulateInterest = _stimulateInterest.doubleValue();
		workRequired = _workRequired.doubleValue();
	}

	public double getAmountLearned() { return amountLearned; }
	public double getCommAbility() { return commAbility; }
	public double getCourseQuality() { return courseQuality; }
	public double getDifficulty() { return difficulty; }
	public double getInstructorAccess() { return instructorAccess; }
	public double getInstructorQuality() { return instructorQuality; }
	public double getReadingsValue() { return readingsValue; }
	public double getRecommendMajor() { return recommendMajor; }
	public double getRecommendNonMajor() { return recommendNonMajor; }
	public double getStimulateInterest() { return stimulateInterest; }
	public double getWorkRequired() { return workRequired; }
	
	public String toString(){
		return "Amount Learned: " + amountLearned +
				"\nCommunication Ability: " + commAbility +
				"\nCourse Quality: " + courseQuality +
				"\nDifficulty: " + difficulty +
				"\nInstructor Access: " + instructorAccess +
				"\nInstructor Quality: " + instructorQuality +
				"\nReadings Value: " + readingsValue +
				"\nRecommend to a Major: " + recommendMajor +
				"\nRecommend to a nonmajor: " + recommendNonMajor +
				"\nStimulate Interest: " + stimulateInterest +
				"\nWork Required: " + workRequired;
	}
}