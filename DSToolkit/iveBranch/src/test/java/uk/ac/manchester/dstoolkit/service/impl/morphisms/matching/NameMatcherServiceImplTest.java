package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.ConstructBasedMatcherService;

/** 
 * Test class to test the functionality of the new matcher that calculates the syntactic
 * similarity of namespace URIs that are strings. This matcher can be considered as a 
 * combined matcher which uses existing primitive matchers like nGram or EditDistance
 * for calculating the syntactic similarity of different parts of a URI string.
 * 
 *  http://example.com/over/there/onto.rfg#text
 *  \__/  \__________/\__________________/ \__/
 *  scheme authority         path		   fragment
 * 
 * @author klitos
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class NameMatcherServiceImplTest {	
	static Logger logger = Logger.getLogger(NameMatcherServiceImplTest.class);

	@Before
	public void setUp() {	
	}//end setUp()
	
	@Test
	public void testNameMatcher() {
	
		
		//TODO: Replace the following with configuration for the NameMatcherService. I need to test this if it works
		
		
		/*Create a new namespace matcher by attaching the method or methods used to find the syntactic similarity of strings*/
		NameMatcherServiceImpl namespaceMatcher = new NameMatcherServiceImpl();
		namespaceMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl()); //This is the string based matcher strategy
		namespaceMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());
		
		
		

		/*Then attach this matcher as a child matcher for a Construct based matcher*/
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(namespaceMatcher);		
		
		
		
		
	}
	
}//end class
