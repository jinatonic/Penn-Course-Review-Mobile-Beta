package edu.upenn.cis.cis350.objects;

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

	//default value for null ratings is 0
	public Ratings(Double _amountLearned, Double _commAbility, Double _courseQuality, Double _difficulty, Double _instructorAccess,
			Double _instructorQuality, Double _readingsValue, Double _recommendMajor, Double _recommendNonMajor, Double _stimulateInterest,
			Double _workRequired) {

		amountLearned = (_amountLearned!=null) ? _amountLearned.doubleValue() : 0;
		commAbility = (_commAbility!=null) ? _commAbility.doubleValue() : 0;
		courseQuality = (_courseQuality!=null) ? _courseQuality.doubleValue() : 0;
		difficulty = (_difficulty!=null) ? _difficulty.doubleValue() : 0;
		instructorAccess = (_instructorAccess!=null) ? _instructorAccess.doubleValue() : 0;
		instructorQuality = (_instructorQuality!=null) ? _instructorQuality.doubleValue() : 0;
		readingsValue = (_readingsValue!=null) ? _readingsValue.doubleValue() : 0;
		recommendMajor = (_recommendMajor!=null) ? _recommendMajor.doubleValue() : 0;
		recommendNonMajor = (_recommendNonMajor!=null) ? _recommendNonMajor.doubleValue() : 0;
		stimulateInterest = (_stimulateInterest!=null) ? _stimulateInterest.doubleValue() : 0;
		workRequired = (_workRequired!=null) ? _workRequired.doubleValue() : 0;
	}
	//returns rating given by r, -1 on error
	public double getRating(String r){
		if(r.equalsIgnoreCase("amountLearned")) return amountLearned;
		if(r.equalsIgnoreCase("commAbility")) return commAbility;
		if(r.equalsIgnoreCase("courseQuality")) return courseQuality;
		if(r.equalsIgnoreCase("difficulty")) return difficulty;
		if(r.equalsIgnoreCase("instructorAccess")) return instructorAccess;
		if(r.equalsIgnoreCase("instructorQuality")) return instructorQuality;
		if(r.equalsIgnoreCase("readingsValue")) return readingsValue;
		if(r.equalsIgnoreCase("recommendMajor")) return recommendMajor;
		if(r.equalsIgnoreCase("recommendNonMajor")) return recommendNonMajor;
		if(r.equalsIgnoreCase("stimulateInterest")) return stimulateInterest;
		if(r.equalsIgnoreCase("workRequired")) return workRequired;
		
		return -1;
		
		
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