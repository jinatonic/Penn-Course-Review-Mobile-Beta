package edu.upenn.cis.cis350.test;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.database.SearchCache;

public class ParserTest extends AndroidTestCase {

	SearchCache cache;
	
	@Override
	public void setUp() {
		cache = new SearchCache(this.getContext());
		cache.open();
		cache.resetTables();
	}
	
	@Override
	public void tearDown() {
		cache.close();
	}
	
	/**
	 * 
	 */
	public void test_getReviewsForCourse() {
		//ArrayList<Course> rs = p.getReviewsForCourse("cis121");
	}
}
