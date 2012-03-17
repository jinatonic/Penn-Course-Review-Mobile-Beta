package edu.upenn.cis.cis350.test;

import android.content.Context;
import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.SearchCache;


public class SearchCacheTest extends AndroidTestCase {

	public void testInitialization() {
		Context c = getContext();
		SearchCache cache = new SearchCache(c);
		cache.open();
	}
	
}
