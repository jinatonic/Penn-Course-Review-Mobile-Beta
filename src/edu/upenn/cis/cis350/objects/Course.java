package edu.upenn.cis.cis350.objects;

public class Course {
	
	private String[] aliases;
	private String name;
	private String description;
	private String semester;
	private String id;
	private String comments;
	private Instructor instructor;
	private int num_reviewers;
	private int num_students;
	private String path;
	private Ratings ratings;
	private Section section;
	
	public Course(String[] aliases, String _name, String _description, String _semester, String _comments, String _id, Instructor _instructor, int _num_reviewers,
			int _num_students, String _path, Ratings _ratings, Section _section) {
		name = _name;
		description = _description;
		semester = _semester;
		id = _id;
		comments = _comments;
		instructor = _instructor;
		num_reviewers = _num_reviewers;
		num_students = _num_students;
		path = _path;
		ratings = _ratings;
		section = _section;
	}
	public String getName() { return name;}
	public String getDescription() {return description;}
	public String getSemester() {return semester;}
	public String getComments() { return comments; }
	public String getID() { return id; }
	public Instructor getInstructor() { return instructor; }
	public int getNumReviewers() { return num_reviewers; }
	public int getNumStudents() { return num_students; }
	public String getPath() { return path; }
	public Ratings getRatings() { return ratings; }
	public Section getSection() { return section; }
}
