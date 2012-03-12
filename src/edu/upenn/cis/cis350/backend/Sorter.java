package edu.upenn.cis.cis350.backend;
import java.util.ArrayList;
import java.util.Arrays;

import edu.upenn.cis.cis350.objects.*;

public class Sorter {
	
	private class Pair implements Comparable{
		double d;
		Course c;
		public Pair(double d, Course c){
			this.d = d;
			this.c = c;
			
		}
		public double getDouble() { return d;}
		public Course getCourse() {return c;}
		
		public int compareTo(Object o1) {
			if(d == ((Pair)o1).getDouble())
				return 0;
			else if(d > ((Pair)o1).getDouble())
				return 1;
			else
				return -1;
		}
	}
	
	public ArrayList<Course> sortBy(ArrayList<Course> courses, String rRating){
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
		for(int i = 0; i < pairs.length; i++){
			sortedCourses.add(pairs[i].getCourse());
		}
		return sortedCourses;
		
	}

}
