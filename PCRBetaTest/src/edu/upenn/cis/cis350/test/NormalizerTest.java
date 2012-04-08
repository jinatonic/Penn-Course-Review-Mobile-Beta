package edu.upenn.cis.cis350.test;

import android.test.AndroidTestCase;
import edu.upenn.cis.cis350.backend.Normalizer;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class NormalizerTest extends AndroidTestCase {
	
	public void test_normalize() {
		String output = Normalizer.normalize("cis121", Type.COURSE);
		assertEquals("cis-121", output);
		
		output = Normalizer.normalize("CD Murphy", Type.INSTRUCTOR);
		assertEquals("cd murphy", output);
		
		output = Normalizer.normalize("CIS", Type.DEPARTMENT);
		assertEquals("cis", output);
		
		output = Normalizer.normalize("CIS-121", Type.COURSE);
		assertEquals("cis-121", output);
		
		// These should fail
		output = Normalizer.normalize("CIS-121cxzg", Type.COURSE);
		assertEquals(null, output);
		
		output = Normalizer.normalize("CS-32as", Type.COURSE);
		assertEquals(null, output);
	}
}
