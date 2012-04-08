package edu.upenn.cis.cis350.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.database.DepartmentSearchCache;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.Department;

public class DepartmentSearchCacheTest extends AndroidTestCase {

	Department d;
	CourseAverage c1, c2;
	DepartmentSearchCache cache;
	
	@Override
	public void setUp() {
		c1 = new CourseAverage("name1", "id1", "path1", new ArrayList<Course>());
		c2 = new CourseAverage("name2", "id2", "path2", new ArrayList<Course>());
		
		ArrayList<CourseAverage> c_avg = new ArrayList<CourseAverage>();
		c_avg.add(c1);
		c_avg.add(c2);
		
		d = new Department("d_name", "d_id", "d_path", c_avg);
		
		cache = new DepartmentSearchCache(getContext());
		cache.open();
		cache.resetTables();
		cache.close();
	}
	
	@Override
	public void tearDown() {
		cache.close();
	}
	
	public void test_initialization() {
		cache.open();
		
		assertEquals(0, cache.getSize());
	}
	
	public void test_insertIntoDB() {
		cache.open();
		
		cache.addDepartment(d);
		
		assertEquals(2, cache.getSize());
	}
	
	public void test_insertDupIntoDB() {
		cache.open();
		
		cache.addDepartment(d);
		cache.addDepartment(d);
		
		assertEquals(4, cache.getSize());	// we actually allow this
	}
	
	public void test_retrievingObj() {
		cache.open();
		
		cache.addDepartment(d);
		
		Department rs = cache.getDepartment("lala");
		assertNull(rs);
		
		rs = cache.getDepartment("d_id");
		assertEquals(d.getName(), rs.getName());
		assertEquals(d.getId(), rs.getId());
		assertEquals(d.getPath(), rs.getPath());
	}

	public void test_ifExists() {
		cache.open();
		
		cache.addDepartment(d);
		
		assertTrue(cache.ifExistsInDB("d_id"));
		assertFalse(cache.ifExistsInDB("d_name"));
	}
	
	public void test_clearDB() {
		cache.open();
		
		cache.addDepartment(d);
		
		assertEquals(2, cache.getSize());
		
		cache.resetTables();
		
		assertEquals(0, cache.getSize());
	}
	
}
