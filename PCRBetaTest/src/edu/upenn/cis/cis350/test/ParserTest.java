package edu.upenn.cis.cis350.test;

import org.json.JSONObject;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.Parser;

public class ParserTest extends AndroidTestCase {

	Parser p;
	
	@Override
	public void setUp() {
		p = new Parser();
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
	
}
