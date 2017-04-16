/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.merge;

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

import uk.ac.manchester.dstoolkit.MergeAbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;

/**
 * @author ive
 *
 */
public class SchemaServiceMergeTest extends MergeAbstractIntegrationTest {

	private static Logger logger = Logger.getLogger(SchemaServiceMergeTest.class);

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	private Schema sourceSchema;
	private Schema targetSchema;

	private final List<MatcherService> nameMatcherList = new ArrayList<MatcherService>();

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
		
		sourceSchema = schemaRepository.getSchemaByName("source");
		targetSchema = schemaRepository.getSchemaByName("target");
		
		// My test for NGram
		int lengthOfNGram = 3;
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
	
		
	@Test
	public void testMerge() throws ExecutionException {					
								
		/**
		 * @input sourceSchema, targetSchema, matchAlgorithm, matchParameters
		 * @output list of matchings
		 */
		List<Matching> matching1 = schemaService.match(sourceSchema, targetSchema, nameMatcherList, controlParameters);
		
		List<Matching> lstMatchings = new ArrayList<Matching>();
		lstMatchings.addAll(matching1);
						
		Set<Schema> sourceSchemas = new HashSet<Schema>();
		sourceSchemas.add(sourceSchema);
			
		Set<Schema> targetSchemas = new HashSet<Schema>();
		targetSchemas.add(targetSchema);
					
		/**
		 * @input sourceSchema, targetSchema, list of matchings
		 * @output set of schematic correspondences
		 */
		Set<SchematicCorrespondence> schematicCorrespondencesReturn = schemaService.inferCorrespondences(sourceSchemas, targetSchemas, matching1, new HashMap<ControlParameterType, ControlParameter>());
				
		/**
		 * @input set of schematic correspondences
		 * @output merged schema, schematic correspondence between the merged schema with the schemas which it was created.
		 */
		Schema sm = schemaService.merge(schematicCorrespondencesReturn);
		Map<Integer, SchematicCorrespondence> mergeSC = sm.getSchematicCorrespondenceSchemaMerged();
		
		Mapping mapping = schemaService.viewGen(mergeSC);
		System.out.println("Mapping:" + mapping.toString());
		
	}
	
}
