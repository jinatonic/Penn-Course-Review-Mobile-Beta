package edu.upenn.cis.cis350.test.database;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.database.RecentSearches;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class RecentSearchesTest extends AndroidTestCase {

	RecentSearches cache;
	KeywordMap k1, k2, k3, k4;

	@Override
	public void setUp() {
		cache = new RecentSearches(this.getContext());

		// create dummy keywordmaps to test autocomplete
		k1 = new KeywordMap("k1_path", "k1_name", "course_id_1", Type.COURSE);
		k2 = new KeywordMap("k2_path", "k2_name", "course_id_2", Type.DEPARTMENT);
		k3 = new KeywordMap("k3_path", "k3_name", "course_id_3", Type.INSTRUCTOR);
		k4 = new KeywordMap("k4_path", "k4_name", "course_id_4", Type.UNKNOWN);

		cache.open();
		cache.resetTables(0);
		cache.resetTables(1);
	}

	@Override
	public void tearDown() {
		cache.close();
	}

	public void test_initialization() {
		assertEquals(0, cache.getSizeDebug(0));
		assertEquals(0, cache.getSizeDebug(1));
	}

	public void test_insertRecent() {
		cache.addKeyword(k1, 0);
		cache.addKeyword(k2, 0);
		cache.addKeyword(k3, 0);
		cache.addKeyword(k4, 0);
		
		assertEquals(4, cache.getSizeDebug(0));
	}
	
	public void test_insertFav() {
		cache.addKeyword(k1, 1);
		cache.addKeyword(k2, 1);
		cache.addKeyword(k3, 1);
		cache.addKeyword(k4, 1);
		
		assertEquals(4, cache.getSizeDebug(1));
	}
	
	public void test_getPKRecent() {
		cache.addKeyword(k1, 0);
		cache.addKeyword(k2, 0);
		
		assertEquals(3, cache.getNextPK(0));
	}
	
	public void test_getPKFav() {
		cache.addKeyword(k1, 1);
		cache.addKeyword(k2, 0);
		
		assertEquals(2, cache.getNextPK(0));
	}
	
	public void test_addKeywordRecent() {
		cache.addKeyword(k1, 0);
		cache.addKeyword("CIS121", 0);
		String[] rs = cache.getKeywords(0);
		
		assertEquals(2, cache.getSizeDebug(0));
		assertEquals(Constants.COURSE_TAG + k1.getAlias() + " - " + k1.getName(), rs[1]);
		assertEquals("CIS121", rs[0]);
	}
	
	public void test_addKeywordFav() {
		cache.addKeyword(k1, 1);
		cache.addKeyword("CIS121", 1);
		cache.addKeyword(k2, 0);
		String[] rs = cache.getKeywords(1);
		
		assertEquals(2, cache.getSizeDebug(1));
		assertEquals(Constants.COURSE_TAG + k1.getAlias() + " - " + k1.getName(), rs[1]);
		assertEquals("CIS121", rs[0]);
	}
	
	public void test_removeRecent() {
		cache.addKeyword(k1, 0);
		cache.removeKeyword(Constants.COURSE_TAG + k1.getAlias() + " - " + k1.getName(), 0);
		
		assertEquals(0, cache.getSizeDebug(0));
	}
	
	public void test_removeFav() {
		cache.addKeyword(k1, 1);
		cache.removeKeyword(Constants.COURSE_TAG + k1.getAlias() + " - " + k1.getName(), 1);
		
		assertEquals(0, cache.getSizeDebug(1));
	}
	
	public void test_existsRecent() {
		cache.addKeyword(k1, 0);
		assertTrue(cache.ifExists(Constants.COURSE_TAG + k1.getAlias() + " - " + k1.getName(), 0));
		assertFalse(cache.ifExists("fail", 0));
	}
	
	public void test_existsFav() {
		cache.addKeyword(k1, 1);
		assertTrue(cache.ifExists(Constants.COURSE_TAG + k1.getAlias() + " - " + k1.getName(), 1));
		assertFalse(cache.ifExists("fail", 1));
	}
}
