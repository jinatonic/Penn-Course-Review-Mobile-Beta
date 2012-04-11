package edu.upenn.cis.cis350.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;

import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.backend.Sorter;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class SorterTest extends AndroidTestCase {
	
	public void test_sortAlpha(){
		KeywordMap k = new KeywordMap("/instructors/683-G-RICHARD-SHELL","G RICHARD SHELL",null,Type.INSTRUCTOR);
		Parser p = new Parser();
		Sorter s = new Sorter();
		ArrayList<Course> reviews = p.getReviewsForInstructor(k);
		reviews = s.sortAlphabetically(reviews, Type.COURSE, 0);
		for(int i = 0; i < reviews.size()-1; i++){
			assertTrue(reviews.get(i).getAlias().compareToIgnoreCase(reviews.get(i+1).getAlias())<=0);
		}
	}
	
	public void test_sortSemester(){
		KeywordMap k = new KeywordMap("/coursehistories/2","INTRO GREEK ARCHAELOGY","AAMW-401",Type.COURSE);
		Parser p = new Parser();
		Sorter s = new Sorter();
		ArrayList<Course> reviews = p.getReviewsForCourse(k);
		reviews = s.sortBySemester(reviews, 0);
		for(int i = 0; i < reviews.size()-1; i++){
			assertTrue(reviews.get(i).getSemester().compareToIgnoreCase(reviews.get(i+1).getSemester())<=0);
		}
	}
	
	public void test_sortRating(){
		KeywordMap k = new KeywordMap("/coursehistories/2","INTRO GREEK ARCHAELOGY","AAMW-401",Type.COURSE);
		Parser p = new Parser();
		Sorter s = new Sorter();
		ArrayList<Course> reviews = p.getReviewsForCourse(k);
		reviews = s.sortByRating(reviews, "difficulty", 1);
		for(int i = 0; i < reviews.size()-1; i++){
			assertTrue(reviews.get(i).getRatings().getDifficulty() < reviews.get(i+1).getRatings().getDifficulty());
		}
	}

}