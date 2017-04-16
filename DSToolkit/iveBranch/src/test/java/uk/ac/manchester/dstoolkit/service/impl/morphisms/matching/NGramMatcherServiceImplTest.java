package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ruhaila
 *
 */

public class NGramMatcherServiceImplTest {

	private String str1, str2;

	private NGramMatcherServiceImpl ngrammatcher;

	static Logger logger = Logger.getLogger(NGramMatcherServiceImplTest.class);

	@Before
	public void setUp() {
		int lengthOfNGram = 3;
		ngrammatcher = new NGramMatcherServiceImpl(lengthOfNGram);
		str1 = "good morning";
		str2 = "morning good";
	}

	@Test
	public void testGenerateNGrams() {
		ArrayList<String> grams1 = ngrammatcher.generateNGrams(str1);

		for (String gram : grams1) {
			logger.debug("gram: " + gram);
		}

		assertEquals(10, grams1.size());
		assertEquals("goo", grams1.get(0));
		assertEquals("ood", grams1.get(1));
		assertEquals("od ", grams1.get(2));
		assertEquals("d m", grams1.get(3));
		assertEquals(" mo", grams1.get(4));
		assertEquals("mor", grams1.get(5));
		assertEquals("orn", grams1.get(6));
		assertEquals("rni", grams1.get(7));
		assertEquals("nin", grams1.get(8));
		assertEquals("ing", grams1.get(9));

	}

	@Test
	public void testMatch() {
		float sim = ngrammatcher.match(str1, str2);
		double epsilon = 0;

		logger.debug("sim: " + sim);

		assertEquals(0.7F, sim, epsilon);

	}
}