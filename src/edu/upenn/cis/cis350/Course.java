package edu.upenn.cis.cis350;

public class Course {
	private String id;
	private String comments;
	private Instructor instructor;
	private int num_reviewers;
	private int num_students;
	private String path;
	private Ratings ratings;
	private Section section;

	public Course(String _comments, String _id, Instructor _instructor, int _num_reviewers,
			int _num_students, String _path, Ratings _ratings, Section _section) {
		id = _id;
		comments = _comments;
		instructor = _instructor;
		num_reviewers = _num_reviewers;
		num_students = _num_students;
		path = _path;
		ratings = _ratings;
		section = _section;
	}

	public String getComments() { return comments; }
	public String getID() { return id; }
	public Instructor getInstructor() { return instructor; }
	public int getNumReviewers() { return num_reviewers; }
	public int getNumStudents() { return num_students; }
	public String getPath() { return path; }
	public Ratings getRatings() { return ratings; }
	public Section getSection() { return section; }

}
