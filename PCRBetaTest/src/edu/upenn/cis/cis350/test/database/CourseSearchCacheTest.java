package edu.upenn.cis.cis350.test.database;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.database.CourseSearchCache;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.Instructor;
import edu.upenn.cis.cis350.objects.Ratings;
import edu.upenn.cis.cis350.objects.Section;


public class CourseSearchCacheTest extends AndroidTestCase {

	Course testCourse;
	CourseSearchCache cache;
	
	@Override
	public void setUp() {
		cache = new CourseSearchCache(getContext());
		cache.open();
		cache.resetTables();
		
		Section testSection = new Section("CIS-121-001", "12345", "section_path", "Data Structure and Algo", "001");
		Ratings testRatings = new Ratings(4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0);
		Instructor testInstructor = new Instructor("123", "Kostas", "insn_path");
		testCourse = new Course("CIS-121", "Data structure and algo", "you learn stuff", "SPRING 2012", "no comments", "12", testInstructor, 50, 100, "course_path", testRatings, testSection);
	}
	
	@Override
	public void tearDown() {
		cache.close();
	}
	
	/**
	 * Test initializing the tables and such
	 */
	public void test_initialization() {
		ArrayList<Course> rs = cache.getCourse("test", 0);
		assertEquals(0, rs.size());
	}
	
	/**
	 * Test inserting an element into the table
	 */
	public void test_insertingCourse() {
		ArrayList<Course> t = new ArrayList<Course>();
		t.add(testCourse);
		
		cache.addCourse(t, 0);
		
		assertEquals(0, cache.getSize());
	}
	
	/**
	 * Test inserting invalid elements into the table
	 */
	public void test_insertingCourseWithInvalidEntries() {
		assertEquals(0, cache.getSize());
		
		Section testSection = new Section("CIS-121-001", "12345", "section_path", "Data Structure and Algo", "001");
		Ratings testRatings = new Ratings(4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0);
		Instructor testInstructor = new Instructor("123", "Kostas", "insn_path");
		Course testCourse = new Course(null, null, null, null, null, null, testInstructor, 50, 100, "course_path", testRatings, testSection);
		
		ArrayList<Course> t = new ArrayList<Course>();
		t.add(testCourse);
		
		cache.addCourse(t, 0);		// should fail and not insert anything
		
		assertEquals(0, cache.getSize());
	}
	
	/**
	 * Test getting information from database based on course alias (should still have the CIS-121 entry from previous tests)
	 */
	public void test_getCourseWithCourseAlias() {
		ArrayList<Course> t = new ArrayList<Course>();
		t.add(testCourse);
		
		cache.addCourse(t, 0);
		
		ArrayList<Course> testCourses = cache.getCourse("CIS-121", 0);
		
		assertEquals(1, testCourses.size());
		assertEquals("12", testCourses.get(0).getID());
		assertEquals("123", testCourses.get(0).getInstructor().getID());
		assertEquals("12345", testCourses.get(0).getSection().getID());
		assertEquals("4.0", testCourses.get(0).getRatings().getAmountLearned());
	}
	
	/**
	 * Test getting information from database based on professor's name (should still have the CIS-121 entry from previous tests)
	 */
	public void test_getCourseWithProfName() {
		ArrayList<Course> t = new ArrayList<Course>();
		t.add(testCourse);
		
		cache.addCourse(t, 1);
		
		ArrayList<Course> testCourses = cache.getCourse("Kostas", 1);
		
		assertEquals(1, testCourses.size());
		assertEquals("12", testCourses.get(0).getID());
		assertEquals("123", testCourses.get(0).getInstructor().getID());
		assertEquals("12345", testCourses.get(0).getSection().getID());
		assertEquals("4.0", testCourses.get(0).getRatings().getAmountLearned());
	}
	
	/**
	 * Test getting information from database with invalid input
	 */
	public void test_getCourseInvalidEntry() {
		ArrayList<Course> testCourses = cache.getCourse("HAHAHA", 0);
		
		assertEquals(0, testCourses.size());
	}
	
	/**
	 * Test resetting the table
	 */
	public void test_resetTable() {
		ArrayList<Course> t = new ArrayList<Course>();
		t.add(testCourse);
		
		cache.addCourse(t, 0);
		
		cache.resetTables();
		cache.close();
		cache.open();
		
		ArrayList<Course> testCourses = cache.getCourse("CIS-121", 0);
		assertEquals(0, testCourses.size());
	}
}
