package edu.upenn.cis.cis350.test;

import java.util.ArrayList;

import edu.upenn.cis.cis350.backend.Parser;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.Department;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;
import android.test.AndroidTestCase;

public class ParserTest extends AndroidTestCase{
	
	
	public void test_retrieveDept(){
		KeywordMap k = new KeywordMap("/depts/ALAN","Asian Languages","ALAN",Type.DEPARTMENT);
		Parser p = new Parser();
		Department d = p.getReviewsForDept(k);
		assertEquals(d.getId(),"ALAN");
		assertEquals(d.getName(),"Asian Languages");
		assertEquals(d.getPath(), "/depts/ALAN");
		assertEquals(d.getCourseAverages().size(),7);
	}
	
	public void test_retrieveInstructor(){
		KeywordMap k = new KeywordMap("/instructors/683-G-RICHARD-SHELL","G RICHARD SHELL",null,Type.INSTRUCTOR);
		Parser p = new Parser();
		ArrayList<Course> reviews = p.getReviewsForInstructor(k);
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
