package edu.upenn.cis.cis350.test;

import java.util.ArrayList;

import org.json.JSONObject;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.backend.SearchCache;
import edu.upenn.cis.cis350.objects.Course;

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
	 * Test retrieveJSONObject with some known given paths and some invalid ones
	 */
	public void test_retrieveJSONObject() {
		String url = p.baseURL + "/depts/CIS" + p.token;
		JSONObject result = p.retrieveJSONObject(url);
		assertNotNull(result);
		
		url = p.baseURL + "/coursehistories/2468" + p.token;
		result = p.retrieveJSONObject(url);
		assertNotNull(result);
		
		url = "haha";
		result = p.retrieveJSONObject(url);
		assertNull(result);
	}
	
	/**
	 * 
	 */
	public void test_getReviewsForCourse() {
		ArrayList<Course> rs = p.getReviewsForCourse("cis121");
	}
}
