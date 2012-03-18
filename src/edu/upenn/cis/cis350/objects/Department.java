package edu.upenn.cis.cis350.objects;

import java.util.ArrayList;

public class Department {
	private String name;
	private String id;
	private String path;
	private ArrayList<CourseAverage> courseRatings;
	
	public Department(String _name, String _id, String _path, ArrayList<CourseAverage> _courseRatings){
		name = _name;
		id = _id;
		path = _path;
		courseRatings = _courseRatings;
	}
	
	public String getName() { return name;}
	public String getId() { return id; }
	public String getPath() { return path; }
	public ArrayList<CourseAverage> getCourseAverages() { return courseRatings; }

}
