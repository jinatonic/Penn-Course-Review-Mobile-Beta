package edu.upenn.cis.cis350.test;

import java.util.ArrayList;

import android.content.Context;
import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.SearchCache;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.Instructor;
import edu.upenn.cis.cis350.objects.Ratings;
import edu.upenn.cis.cis350.objects.Section;


public class SearchCacheTest extends AndroidTestCase {

	Context c;
	SearchCache cache;
	
	@Override
	public void setUp() {
		c = getContext();
		cache = new SearchCache(c);
		cache.open();
	}
	
	/**
	 * Test initializing the tables and such
	 */
	public void testInitialization() {
		cache.resetTables();
		
		ArrayList<Course> rs = cache.getCourse("test");
		assertEquals(0, rs.size());
		
		cache.close();
	}
	
	/**
	 * Test inserting an element into the table
	 */
	public void testInsertingCourse() {
		Section testSection = new Section("CIS-121-001", "12345", "section_path", "Data Structure and Algo", "001");
		Ratings testRatings = new Ratings(4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0);
		Instructor testInstructor = new Instructor("123", "Kostas", "insn_path");
		Course testCourse = new Course("CIS-121", "Data structure and algo", "you learn stuff", "SPRING 2012", "no comments", "12", testInstructor, 50, 100, "course_path", testRatings, testSection);
		
		cache.addCourse(testCourse);
		
		assertEquals(1, cache.getSize());
	}
	
	/**
	 * Test inserting invalid elements into the table
	 */
	public void testInsertingCourseWithInvalidEntries() {
		assertEquals(1, cache.getSize());	// should still have the last entry entered
		
		Section testSection = new Section("CIS-121-001", "12345", "section_path", "Data Structure and Algo", "001");
		Ratings testRatings = new Ratings(4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0);
		Instructor testInstructor = new Instructor("123", "Kostas", "insn_path");
		Course testCourse = new Course(null, null, null, null, null, null, testInstructor, 50, 100, "course_path", testRatings, testSection);
		
		cache.addCourse(testCourse);		// should fail and not insert anything
		
		assertEquals(1, cache.getSize());	// should still have the last entry entered
	}
	
	/**
	 * Test getting information from database based on course alias (should still have the CIS-121 entry from previous tests)
	 */
	public void testGetCourseWithCourseAlias() {
		ArrayList<Course> testCourses = cache.getCourse("CIS-121");
		
		assertEquals(1, testCourses.size());
		assertEquals("12", testCourses.get(0).getID());
		assertEquals("123", testCourses.get(0).getInstructor().getID());
		assertEquals("12345", testCourses.get(0).getSection().getID());
		assertEquals(4.0, testCourses.get(0).getRatings().getAmountLearned());
	}
	
	/**
	 * Test getting information from database based on professor's name (should still have the CIS-121 entry from previous tests)
	 */
	public void testGetCourseWithProfName() {
		ArrayList<Course> testCourses = cache.getCourse("Kostas");
		
		assertEquals(1, testCourses.size());
		assertEquals("12", testCourses.get(0).getID());
		assertEquals("123", testCourses.get(0).getInstructor().getID());
		assertEquals("12345", testCourses.get(0).getSection().getID());
		assertEquals(4.0, testCourses.get(0).getRatings().getAmountLearned());
	}
	
	/**
	 * Test getting information from database with invalid input
	 */
	public void testGetCourseInvalidEntry() {
		ArrayList<Course> testCourses = cache.getCourse("HAHAHA");
		
		assertEquals(0, testCourses.size());
	}
}
