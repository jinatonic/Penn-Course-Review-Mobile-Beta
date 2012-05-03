import java.util.ArrayList;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.Ratings;


public class CourseAverageTest extends AndroidTestCase {

	public void test_computeAverage1() {
		Ratings r1 = new Ratings(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		Ratings r2 = new Ratings(2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0);
		Ratings r3 = new Ratings(3.0, 6.0, 9.0, 12.0, 15.0, 18.0, 21.0, 24.0, 27.0, 30.0, 33.0);
		Ratings r4 = new Ratings(null, null, null, null, null, null, null, null, null, null, null);

		Course c1 = new Course(r1);
		Course c2 = new Course(r2);
		Course c3 = new Course(r3);
		Course c4 = new Course(r4);
		
		ArrayList<Course> arr = new ArrayList<Course>();
		arr.add(c1);
		
		CourseAverage c = new CourseAverage(null, null, null, arr);
		Ratings rs = c.getRatings();
		
		assertEquals("1.0", rs.getAmountLearned());
		assertEquals("1.0", rs.getCommAbility());
		assertEquals("1.0", rs.getCourseQuality());
		assertEquals("1.0", rs.getDifficulty());
		assertEquals("1.0", rs.getInstructorAccess());
		assertEquals("1.0", rs.getInstructorQuality());
		assertEquals("1.0", rs.getReadingsValue());
		assertEquals("1.0", rs.getRecommendMajor());
		assertEquals("1.0", rs.getRecommendNonMajor());
		assertEquals("1.0", rs.getStimulateInterest());
		assertEquals("1.0", rs.getWorkRequired());
		
		arr.add(c2);
		c = new CourseAverage(null, null, null, arr);
		rs = c.getRatings();
		
		assertEquals("1.5", rs.getAmountLearned());
		assertEquals("1.5", rs.getCommAbility());
		assertEquals("1.5", rs.getCourseQuality());
		assertEquals("1.5", rs.getDifficulty());
		assertEquals("1.5", rs.getInstructorAccess());
		assertEquals("1.5", rs.getInstructorQuality());
		assertEquals("1.5", rs.getReadingsValue());
		assertEquals("1.5", rs.getRecommendMajor());
		assertEquals("1.5", rs.getRecommendNonMajor());
		assertEquals("1.5", rs.getStimulateInterest());
		assertEquals("1.5", rs.getWorkRequired());
		
		arr.add(c3);
		c = new CourseAverage(null, null, null, arr);
		rs = c.getRatings();
		
		assertEquals("2.0", rs.getAmountLearned());			// 6
		assertEquals("3.0", rs.getCommAbility());			// 9
		assertEquals("4.0", rs.getCourseQuality());			// 12
		assertEquals("5.0", rs.getDifficulty());			// 15
		assertEquals("6.0", rs.getInstructorAccess());		// 18
		assertEquals("7.0", rs.getInstructorQuality());		// 21
		assertEquals("8.0", rs.getReadingsValue());			// 24
		assertEquals("9.0", rs.getRecommendMajor());		// 27
		assertEquals("10.0", rs.getRecommendNonMajor());	// 30
		assertEquals("11.0", rs.getStimulateInterest());	// 33
		assertEquals("12.0", rs.getWorkRequired());			// 36
		
		arr.add(c4);		// should be same
		c = new CourseAverage(null, null, null, arr);
		rs = c.getRatings();
		
		assertEquals("2.0", rs.getAmountLearned());			// 6
		assertEquals("3.0", rs.getCommAbility());			// 9
		assertEquals("4.0", rs.getCourseQuality());			// 12
		assertEquals("5.0", rs.getDifficulty());			// 15
		assertEquals("6.0", rs.getInstructorAccess());		// 18
		assertEquals("7.0", rs.getInstructorQuality());		// 21
		assertEquals("8.0", rs.getReadingsValue());			// 24
		assertEquals("9.0", rs.getRecommendMajor());		// 27
		assertEquals("10.0", rs.getRecommendNonMajor());	// 30
		assertEquals("11.0", rs.getStimulateInterest());	// 33
		assertEquals("12.0", rs.getWorkRequired());			// 36
	}
	
}
