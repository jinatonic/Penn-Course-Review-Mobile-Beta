package edu.upenn.cis.cis350.backend;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class Sorter {

	private class Pair implements Comparable<Pair> {
		double d;
		Course c;
		public Pair(double d, Course c){
			this.d = d;
			this.c = c;

		}
		public double getDouble() { return d;}
		public Course getCourse() {return c;}

		public int compareTo(Pair o1) {
			if(d == o1.getDouble())
				return 0;
			else if(d > o1.getDouble())
				return 1;
			else
				return -1;
		}
	}

	// Allows for sorting Courses based on Instructor field
	private class InstructorCourse implements Comparable<InstructorCourse> {
		Course c;
		public InstructorCourse(Course c) {
			this.c = c;
		}
		public Course getCourse() { return c; }
		public int compareTo(InstructorCourse other) {
			String insName = c.getInstructor().getName();
			String otherName = other.c.getInstructor().getName();
			if (insName.equals(otherName))
				return 0;
			else if (insName.compareTo(otherName) > 0)
				return 1;
			else
				return -1;
		}
	}

	// Allows for sorting Courses based on courseID (alias) field
	private class IdCourse implements Comparable<IdCourse> {
		Course c;
		public IdCourse(Course c) {
			this.c = c;
		}
		public Course getCourse() { return c; }
		public int compareTo(IdCourse other) {
			String id = c.getAlias();
			String otherId = other.c.getAlias();
			if (id.equals(otherId))
				return 0;
			else if (id.compareTo(otherId) > 0)
				return 1;
			else
				return -1;
		}
	}

	// sort list by instructor, order = 0 is alphabetical, 1 is reverse
	public ArrayList<Course> sortAlphabetically(ArrayList<Course> courses, Type sortType, int order) {
		ArrayList<Course> sortedCourses = new ArrayList<Course>();
		switch (sortType) {
		case INSTRUCTOR:
			ArrayList<InstructorCourse> unsortedInstructorCourses = new ArrayList<InstructorCourse>(); 
			for(Course c: courses) {
				unsortedInstructorCourses.add(new InstructorCourse(c));
			}
			// Sort alphabetically by instructor name
			Collections.sort(unsortedInstructorCourses);

			// Populate into ArrayList of courses in ascending order
			if (order == 0) {
				for (int i = 0; i < unsortedInstructorCourses.size(); i++){
					sortedCourses.add(unsortedInstructorCourses.get(i).getCourse());
				}
			}
			// Populate into ArrayList of courses in descending order
			else {
				for (int i = unsortedInstructorCourses.size()-1; i >= 0; i--){
					sortedCourses.add(unsortedInstructorCourses.get(i).getCourse());
				}
			}
			break;
		case COURSE:
			ArrayList<IdCourse> unsortedIdCourses = new ArrayList<IdCourse>(); 
			for(Course c: courses) {
				unsortedIdCourses.add(new IdCourse(c));
			}
			// Sort alphabetically by course Id
			Collections.sort(unsortedIdCourses);

			// Populate into ArrayList of courses in ascending order
			if (order == 0) {
				for (int i = 0; i < unsortedIdCourses.size(); i++){
					sortedCourses.add(unsortedIdCourses.get(i).getCourse());
				}
			}
			// Populate into ArrayList of courses in descending order
			else {
				for (int i = unsortedIdCourses.size()-1; i >= 0; i--){
					sortedCourses.add(unsortedIdCourses.get(i).getCourse());
				}
			}
			break;
		default:
			break;
		}
		return sortedCourses;
	}

	//sort list by semester, order = 0 is increasing, 1 is decreasing
	public ArrayList<Course> sortBySemester(ArrayList<Course> courses, int order){
		Pair [] pairs = new Pair[courses.size()];
		int count = 0;
		for(Course c: courses){
			String s = c.getSemester();
			double d = Double.parseDouble(s.substring(0,4));
			if(s.charAt(4) == 'B')
				d = d + .5;
			Pair p = new Pair(d,c);
			pairs[count] = p;
			count++;
		}

		Arrays.sort(pairs);
		ArrayList<Course> sortedCourses = new ArrayList<Course>();
		if(order == 0){
			for(int i = 0; i < pairs.length; i++){
				sortedCourses.add(pairs[i].getCourse());
			}
		}
		else {
			for(int i = pairs.length-1; i >= 0; i--){
				sortedCourses.add(pairs[i].getCourse());
			}
		}
		return sortedCourses;

	}

	// Sort array according to a specified rating, rRating, and order (0 = increasing, 1 = decreasing)
	public ArrayList<Course> sortByRating(ArrayList<Course> courses, String rRating, int order) {
		Pair [] pairs = new Pair[courses.size()];
		int count = 0;
		for (Course c: courses) {
			Double d = new Double(c.getRatings().getRating(rRating));
			Pair p = new Pair(d,c);
			pairs[count] = p;
			count++;
		}

		Arrays.sort(pairs);
		ArrayList<Course> sortedCourses = new ArrayList<Course>();
		if (order == 0) {
			for(int i = 0; i < pairs.length; i++){
				sortedCourses.add(pairs[i].getCourse());
			}
		}
		else {
			for (int i = pairs.length-1; i >= 0; i--) {
				sortedCourses.add(pairs[i].getCourse());
			}
		}
		return sortedCourses;		
	}
}