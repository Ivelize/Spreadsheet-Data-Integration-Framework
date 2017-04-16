/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.InferCorrespondenceService;

/**
 * @author ive
 *
 */
public class _02ReverseHorizontalPartitioningScenarioTest extends AbstractIntegrationTest {

	private static Logger logger = Logger.getLogger(_02ReverseHorizontalPartitioningScenarioTest.class);

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("inferCorrespondenceService")
	private InferCorrespondenceService inferCorrespondenceService;

	private Schema spreadsheetGlobalSchema;
	private Schema spreadsheetSourceSchema;

	private final List<MatcherService> nameMatcherList = new ArrayList<MatcherService>();
	private final List<MatcherService> nameAndInstanceMatcherList = new ArrayList<MatcherService>();

	private Map<ControlParameterType, ControlParameter> controlParameters;

	/**
	 */
	@Override
	@Before
	public void setUp() {
		super.setUp();
		logger.debug("after setup()");
		// Set the threshold for all matchers
		controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		ControlParameter thresholdForAllMatchers = new ControlParameter(ControlParameterType.MATCH_SCORE_THRESHOLD, "0.7");
		controlParameters.put(ControlParameterType.MATCH_SCORE_THRESHOLD, thresholdForAllMatchers);

		spreadsheetGlobalSchema = schemaRepository.getSchemaByName("gs");
		spreadsheetSourceSchema = schemaRepository.getSchemaByName("s1");

		// My test for NGram
		int lengthOfNGram = 2;
		MatcherService NGramMatcher = new NGramMatcherServiceImpl(lengthOfNGram);

		// add the SELECTION TYPE here is Threshold
		ControlParameter thresholdSelectionForNGramMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, SelectionType.THRESHOLD.toString());

		// For the threshold set threshold to a value here 0.7
		ControlParameter thresholdForNGramMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.3");

		// Add them on the NGram
		NGramMatcher.addControlParameter(thresholdSelectionForNGramMatcher);
		NGramMatcher.addControlParameter(thresholdForNGramMatcher);

		// add it to the list of matchers to be executed now
		nameMatcherList.add(NGramMatcher);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.meta.SchemaServiceImpl#match(uk.ac.manchester.dstoolkit.domain.models.meta.Schema, uk.ac.manchester.dstoolkit.domain.models.meta.Schema, java.util.List)}.
	 *//*
	@Test
	public void testMatch() {

		List<Matching> matchings = schemaService.match(spreadsheetSourceSchema, spreadsheetGlobalSchema, nameMatcherList, controlParameters);

		assertEquals(57, matchings.size());

		//TODO this still needs to be tested properly
	}*/

	@Test
	public void testInferCorrespondence() throws ExecutionException {
		
		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------- MATCHING -------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		//TODO change this so that it doesn't need all the other stuff beforehand ... this is an integration test, not a unit test
		List<Matching> matchings = schemaService.match(spreadsheetSourceSchema, spreadsheetGlobalSchema, nameMatcherList, controlParameters);
		
		Set<Schema> sourceSchemas = new HashSet<Schema>();
		sourceSchemas.add(spreadsheetSourceSchema);
		Set<Schema> targetSchemas = new HashSet<Schema>();
		targetSchemas.add(spreadsheetGlobalSchema);
		int index = 0;
		for (Matching matching : matchings) {
			System.out.println("");
			System.out.println("matching.getMatcherName(): " + matching.getMatcherName());
			System.out.println("matching_id: " + matching.getId() + ", score: " + matching.getScore());
			if (matching instanceof OneToOneMatching) {
				OneToOneMatching oneToOneMatching = (OneToOneMatching) matching;
				CanonicalModelConstruct construct1 = oneToOneMatching.getConstruct1();
				CanonicalModelConstruct construct2 = oneToOneMatching.getConstruct2();
				if (construct1 instanceof SuperLexical) {
					SuperLexical sl1 = (SuperLexical) construct1;
					System.out.println("construct1 = SuperLexical: " + sl1.getName() + " SuperAbtract: " + sl1.getParentSuperAbstract().getName());
				} else if (construct1 instanceof SuperAbstract) {
					SuperAbstract sa1 = (SuperAbstract) construct1;
					System.out.println("construct1 = SuperAbtract: " + sa1.getName());
				}
				if (construct2 instanceof SuperLexical) {
					SuperLexical sl2 = (SuperLexical) construct2;
					System.out.println("construct2 = SuperLexical: " + sl2.getName() + " SuperAbtract: "
							+ sl2.getParentSuperAbstract().getName());
				} else if (construct2 instanceof SuperAbstract) {
					SuperAbstract sa2 = (SuperAbstract) construct2;
					System.out.println("construct1 = SuperAbtract: " + sa2.getName());
				}
			}
			index++;
		}
		System.out.println("");
		Set<SchematicCorrespondence> schematicCorrespondencesReturn = schemaService.inferCorrespondences(sourceSchemas, targetSchemas, matchings, new HashMap<ControlParameterType, ControlParameter>());
		System.out.println("");
		Set<Mapping> mapping = schemaService.viewGen(schematicCorrespondencesReturn);
		System.out.println("");		
		
		/*for (SchematicCorrespondence schematicCorrespondence : schematicCorrespondencesReturn) {
			Schema sm = schemaService.merge(schematicCorrespondence);
						
			Set<SchematicCorrespondence> schematicCorrespondencesMerge1 = schemaService.getMergedSchemaCorrespondence(spreadsheetSourceSchema, sm, nameMatcherList, controlParameters);
			Set<SchematicCorrespondence> schematicCorrespondencesMerge2 = schemaService.getMergedSchemaCorrespondence(spreadsheetGlobalSchema, sm, nameMatcherList, controlParameters);
			
			Set<Mapping> mapping1 = schemaService.viewGen(spreadsheetSourceSchema, sm, schematicCorrespondencesMerge1);
			Set<Mapping> mapping2 = schemaService.viewGen(spreadsheetGlobalSchema, sm, schematicCorrespondencesMerge2);
			
			System.out.println("Mapping_Query_1: " + mapping1);
			System.out.println("Mapping_Query_2: " + mapping2);
			
			for (SchematicCorrespondence scm : schematicCorrespondencesMerge2) {
				logger.info("INVERT schematicCorrespondencesMerge2: " + schemaService.invert(scm));
				Set<Mapping> mapping3 = schemaService.viewGen(sm, spreadsheetGlobalSchema, schematicCorrespondencesMerge2);
				System.out.println("Mapping_Query_3: " + mapping3);
				break;
			}
			
		}*/
		
		
		
	
	}

	
}
