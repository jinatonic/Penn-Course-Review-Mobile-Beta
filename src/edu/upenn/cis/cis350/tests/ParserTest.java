package edu.upenn.cis.cis350.tests;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis.cis350.backend.Parser;

public class ParserTest extends TestCase {

	Parser parser;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		parser = new Parser();
	}
	
	@Test
	public void test_retrieveJSONObject() {
		// These should return valid JsonObjects
		JSONObject result = parser.retrieveJSONObject("cis121");
		assertNotNull(result);
		result = parser.retrieveJSONObject("cis553");
		assertNotNull(result);
		result = parser.retrieveJSONObject("stat430");
		assertNotNull(result);
		result = parser.retrieveJSONObject("enm321");
		assertNotNull(result);
		result = parser.retrieveJSONObject("econ001");
		assertNotNull(result);
		
		// These should return null
		result = parser.retrieveJSONObject("cis100");
		assertNull(result);
		result = parser.retrieveJSONObject("wtfisthis/cis121");
		assertNull(result);
		
		// Invalid inputs
		result = parser.retrieveJSONObject(null);
		assertNull(result);
		result = parser.retrieveJSONObject("");
		assertNull(result);
	}
	
	@Test
	public void test_getReviewsForCourse() {
		// TODO
	}
	
	@Test
	public void test_displayCourseReviews() {
		// TODO
	}
	
	@Test
	public void test_storeReviews() {
		// TODO
	}

}
