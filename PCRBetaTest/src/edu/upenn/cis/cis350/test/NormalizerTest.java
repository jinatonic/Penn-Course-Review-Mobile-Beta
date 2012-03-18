package edu.upenn.cis.cis350.test;

import edu.upenn.cis.cis350.backend.Normalizer;
import android.test.AndroidTestCase;

public class NormalizerTest extends AndroidTestCase {
	
	public void test_normalize() {
		String output = Normalizer.normalize("cis121");
		assertEquals("cis-121", output);
		
		output = Normalizer.normalize("CD Murphy");
		assertEquals("cd murphy", output);
		
		output = Normalizer.normalize("CIS");
		assertEquals("cis", output);
		
		output = Normalizer.normalize("CIS-121");
		assertEquals("cis-121", output);
		
		// These should fail
		output = Normalizer.normalize("CIS-121cxzg");
		assertEquals(null, output);
		
		output = Normalizer.normalize("CS-32as");
		assertEquals(null, output);
	}
}
