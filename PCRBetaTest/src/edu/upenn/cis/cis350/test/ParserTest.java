package edu.upenn.cis.cis350.test;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class ParserTest extends AndroidTestCase{
	
	public void test_retrieveDept(){
		KeywordMap k = new KeywordMap("/depts/ALAN","Asian Languages","ALAN",Type.DEPARTMENT);
		Parser p = new Parser();
		
		JSONArray arr = p.getReviewsForDept(k);
		ArrayList<CourseAverage> avg = new ArrayList<CourseAverage>();
		for (int i=0; i<arr.length(); i++) {
			try {
				JSONObject o = arr.getJSONObject(i);
				avg.add(p.getCourseAvgForDept(o));
			} catch (JSONException e) {
				e.printStackTrace();
				fail("Exception should not be thrown");
			}
		}

		assertEquals(7, avg.size());
		assertEquals("ELEM VIETNAMESE I & II", avg.get(0).getName());
		assertEquals("FILIPINO LANG & CULTURE", avg.get(6).getName());
	}
	
	public void test_retrieveInstructor(){
		KeywordMap k = new KeywordMap("/instructors/683-G-RICHARD-SHELL","G RICHARD SHELL",null,Type.INSTRUCTOR);
		Parser p = new Parser();
		
		JSONArray arr = p.getReviewsForInstructor(k);
		ArrayList<Course> reviews = new ArrayList<Course>();
		for (int i=0; i<arr.length(); i++) {
			try {
				reviews.addAll(p.getCourseForInstructor(arr.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
				fail("Exception should not be thrown");
			}
		}
		
		for(int i = 0; i < reviews.size(); i++){
			Course c = reviews.get(i);
			assertEquals(c.getInstructor().getName(), "G RICHARD SHELL");
			
		}
	}
		
	public void test_retrieveCourse(){
		KeywordMap k = new KeywordMap("/coursehistories/2","INTRO GREEK ARCHAELOGY","AAMW-401",Type.COURSE);
		Parser p = new Parser();
		ArrayList<Course> reviews = p.getReviewsForCourse(k);
		for(int i = 0; i < reviews.size(); i++){
			Course c = reviews.get(i);
			assertEquals(c.getAlias(), "AAMW-401");
		}
	}

}
