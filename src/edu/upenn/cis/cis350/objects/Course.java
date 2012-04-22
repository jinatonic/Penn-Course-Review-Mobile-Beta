package edu.upenn.cis.cis350.objects;

public class Course {
	
	private String alias;
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
	
	public Course(String _alias, String _name, String _description, String _semester, String _comments, String _id, Instructor _instructor, int _num_reviewers,
			int _num_students, String _path, Ratings _ratings, Section _section) {
		
		this.alias = _alias;
		this.name = _name;
		this.description = _description;
		this.semester = _semester;
		this.id = _id;
		this.comments = _comments;
		this.instructor = _instructor;
		this.num_reviewers = _num_reviewers;
		this.num_students = _num_students;
		this.path = _path;
		this.ratings = _ratings;
		this.section = _section;
	}

	public String getAlias() { return alias; }
	public String getName() { return name; }
	public String getDescription() {return description; }
	public String getSemester() {return semester; }
	public String getComments() { return comments; }
	public String getID() { return id; }
	public Instructor getInstructor() { return instructor; }
	public int getNumReviewers() { return num_reviewers; }
	public int getNumStudents() { return num_students; }
	public String getPath() { return path; }
	public Ratings getRatings() { return ratings; }
	public Section getSection() { return section; }
	public String getFullSemester(){
		if (semester == null) return null;
		char s = semester.charAt(semester.length()-1);
		if(s == 'A' || s == 'a')
			return "Spring "+semester.substring(0,semester.length()-1);
		else if(s == 'B' || s == 'b')
			return "Summer " + semester.substring(0,semester.length()-1);
		else if(s == 'C' || s == 'c')
			return "Fall " + semester.substring(0,semester.length()-1);
		else return semester;
	}
}
