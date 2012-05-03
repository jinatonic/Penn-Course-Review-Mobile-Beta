package edu.upenn.cis.cis350.test.backend;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.Normalizer;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class NormalizerTest extends AndroidTestCase {
	
	public void test_normalize() {
		String output = Normalizer.normalize("CIS 121 - Blah", Type.COURSE);
		assertEquals("cis 121", output);
		
		output = Normalizer.normalize("CD Murphy", Type.INSTRUCTOR);
		assertEquals("cd murphy", output);
		
		output = Normalizer.normalize("CIS - Computer Information Science", Type.DEPARTMENT);
		assertEquals("cis", output);
		
		output = Normalizer.normalize("CIS-121 - Haha", Type.COURSE);
		assertEquals("cis121", output);
	}
}
