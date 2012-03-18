package edu.upenn.cis.cis350.backend;
import java.util.ArrayList;
import java.util.Arrays;

import edu.upenn.cis.cis350.objects.*;

public class Sorter {
	
	private class Pair implements Comparable<Pair>{
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
		else{
			for(int i = pairs.length-1; i >= 0; i--){
				sortedCourses.add(pairs[i].getCourse());
			}
		}
		return sortedCourses;
		
	}
	//sort array according to a specified rating, rRating, and order (0 = increasing, 1 = decreasing)
	public ArrayList<Course> sortByRating(ArrayList<Course> courses, String rRating, int order){
		Pair [] pairs = new Pair[courses.size()];
		int count = 0;
		for(Course c: courses){
			Double d = new Double(c.getRatings().getRating(rRating));
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
		else{
			for(int i = pairs.length-1; i >= 0; i--){
				sortedCourses.add(pairs[i].getCourse());
			}
		}
		return sortedCourses;
		
	}
	
	

}
