package edu.upenn.cis.cis350.test;

import java.util.ArrayList;

import android.content.Context;
import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.SearchCache;
import edu.upenn.cis.cis350.objects.Course;


public class SearchCacheTest extends AndroidTestCase {

	Context c;
	
	@Override
	public void setUp() {
		c = getContext();
	}
	
	/**
	 * Test initializing the tables and such
	 */
	public void testInitialization() {
		SearchCache cache = new SearchCache(c);
		cache.open();
		cache.resetTables();
		
		ArrayList<Course> rs = cache.getCourse("test");
		assertEquals(0, rs.size());
		
		cache.close();
	}
	
	/**
	 * Test inserting an element into the table
	 */
	public void testInsertingCourse() {
		
	}
	
}
