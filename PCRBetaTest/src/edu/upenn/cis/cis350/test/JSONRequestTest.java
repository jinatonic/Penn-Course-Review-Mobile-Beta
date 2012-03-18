package edu.upenn.cis.cis350.test;

import org.json.JSONObject;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.JSONRequest;
import edu.upenn.cis.cis350.backend.Parser;

public class JSONRequestTest extends AndroidTestCase {

	/**
	 * Test retrieveJSONObject with some known given paths and some invalid ones
	 */
	public void test_retrieveJSONObject() {
		String url = Parser.baseURL + "/depts/CIS" + Parser.token;
		JSONObject result = JSONRequest.retrieveJSONObject(url);
		assertNotNull(result);
		
		url = Parser.baseURL + "/coursehistories/2468" + Parser.token;
		result = JSONRequest.retrieveJSONObject(url);
		assertNotNull(result);
		
		url = "haha";
		result = JSONRequest.retrieveJSONObject(url);
		assertNull(result);
	}
	
}
