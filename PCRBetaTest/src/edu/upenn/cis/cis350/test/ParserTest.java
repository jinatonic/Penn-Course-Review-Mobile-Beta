package edu.upenn.cis.cis350.test;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.backend.SearchCache;

public class ParserTest extends AndroidTestCase {

	SearchCache cache;
	Parser p;
	
	@Override
	public void setUp() {
		cache = new SearchCache(this.getContext());
		cache.open();
		cache.resetTables();
		p = new Parser(cache);
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
