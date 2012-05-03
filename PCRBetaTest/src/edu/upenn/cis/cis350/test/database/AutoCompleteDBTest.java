package edu.upenn.cis.cis350.test.database;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.database.AutoCompleteDB;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class AutoCompleteDBTest extends AndroidTestCase {

	ArrayList<KeywordMap> km;
	KeywordMap k1, k2, k3, k4;
	AutoCompleteDB cache;
	
	@Override
	public void setUp() {
		// create dummy keywordmaps to test autocomplete
		k1 = new KeywordMap("k1_path", "k1_name", "course_id_1", Type.COURSE);
		k2 = new KeywordMap("k2_path", "k2_name", "course_id_2", Type.DEPARTMENT);
		k3 = new KeywordMap("k3_path", "k3_name", "course_id_3", Type.INSTRUCTOR);
		k4 = new KeywordMap("k4_path", "k4_name", "course_id_4", Type.UNKNOWN);
		
		km = new ArrayList<KeywordMap>();
		km.add(k1);
		km.add(k2);
		km.add(k3);
		km.add(k4);
		
		cache = new AutoCompleteDB(getContext());
		cache.open();
		cache.resetTables();
	}
	
	@Override
	public void tearDown() {
		cache.close();
	}
	
	public void test_initialization() {
		assertEquals(0, cache.getSize());
	}
	
	public void test_insertingIntoDB() {
		for (int i=0; i < 100; i++) 
			cache.addEntries(km);
		
		assertEquals(40, cache.getSize());
	}
	
	public void test_gettingRecommendation() {
		cache.addEntries(km);
		
		String[] result = cache.checkAutocomplete("k1_name");
		assertEquals(1, result.length);
		assertEquals(Constants.COURSE_TAG + k1.getAlias() + " - " + k1.getName(), result[0]);
		
		result = cache.checkAutocomplete("k3_name");
		assertEquals(1, result.length);
		assertEquals(Constants.COURSE_TAG + k3.getAlias() + " - " + k3.getName(), result[0]);
		
		result = cache.checkAutocomplete("course_id");
		assertEquals(4, result.length);
		assertEquals(Constants.COURSE_TAG + k1.getAlias() + " - " + k1.getName(), result[0]);
		assertEquals(Constants.COURSE_TAG + k2.getAlias() + " - " + k2.getName(), result[1]);
		assertEquals(Constants.COURSE_TAG + k3.getAlias() + " - " + k3.getName(), result[2]);
		assertEquals(Constants.COURSE_TAG + k4.getAlias() + " - " + k4.getName(), result[3]);
	}
	
	public void test_getInfoForParser() {
		cache.addEntries(km);
		
		KeywordMap rs = cache.getInfoForParser("course_id_1", Type.COURSE);
		assertNotNull(rs);
		assertEquals(k1.getAlias(), rs.getAlias());
		assertEquals(k1.getName(), rs.getName());
		assertEquals(k1.getPath(), rs.getPath());
		
		rs = cache.getInfoForParser("course_id_2", Type.DEPARTMENT);
		assertNotNull(rs);
		assertEquals(k2.getAlias(), rs.getAlias());
		assertEquals(k2.getName(), rs.getName());
		assertEquals(k2.getPath(), rs.getPath());
		
		rs = cache.getInfoForParser("k3_name", Type.INSTRUCTOR);
		assertNotNull(rs);
		assertEquals(k3.getAlias(), rs.getAlias());
		assertEquals(k3.getName(), rs.getName());
		assertEquals(k3.getPath(), rs.getPath());
	}
	
	
}
