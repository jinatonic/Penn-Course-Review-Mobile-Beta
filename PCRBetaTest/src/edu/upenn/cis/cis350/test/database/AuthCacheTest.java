package edu.upenn.cis.cis350.test.database;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.database.AuthCache;

public class AuthCacheTest extends AndroidTestCase {

	AuthCache cache;
	
	@Override
	public void setUp() {
		cache = new AuthCache(this.getContext());
		cache.open();
		cache.resetTables();
	}
	
	@Override
	public void tearDown() {
		cache.close();
	}
	
	public void test_initialization() {
		assertEquals(null, cache.checkKey());
	}
	
	public void test_insertingAndCheck() {
		cache.insertKey("TESTING");
		
		assertEquals("TESTING", cache.checkKey());
	}
	
}
