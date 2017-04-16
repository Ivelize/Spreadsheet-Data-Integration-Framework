/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.meta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import scala.xml.Null;
import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.matching.MatchingRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.schematiccorrespondence.SchematicCorrespondenceRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.InferCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.SchematicCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.CellPOJO;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.Constant;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.SpreadsheetService;

/**
 * @author ive
 *
 */
public class SchemaServiceImplIntegrationTest extends AbstractIntegrationTest {

	private static Logger logger = Logger.getLogger(SchemaServiceImplIntegrationTest.class);

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("inferCorrespondenceService")
	private InferCorrespondenceService inferCorrespondenceService;
	
	@Autowired
	@Qualifier("schematicCorrespondenceService")
	private SchematicCorrespondenceService schematicCorrespondenceService;
	
	@Autowired
	@Qualifier("schematicCorrespondenceRepository")
	private SchematicCorrespondenceRepository schematicCorrespondenceRepository;

	private Schema spreadsheetSourceSchema1;
	private Schema spreadsheetSourceSchema2;

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
		
		
	}
	
		
	@Test
	public void testReverseVP() throws ExecutionException {

		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------- MATCHING -------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		
		File spreadsheetSourceSpreadsheets[];
		File spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_SOURCE);
		spreadsheetSourceSpreadsheets = spreadsheetsDirectory.listFiles();
		
		File spreadsheetTargetSpreadsheets[];
		spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_TARGET);
		spreadsheetTargetSpreadsheets = spreadsheetsDirectory.listFiles();	
		

		for (int i = 0; i < spreadsheetSourceSpreadsheets.length; i++) {
			
			for (int k = 0; k < spreadsheetTargetSpreadsheets.length; k++) {
			
				if(!spreadsheetSourceSpreadsheets[i].getName().endsWith("csv") && !spreadsheetSourceSpreadsheets[i].getName().endsWith("xls"))
					i = i + 1;
				
				if(!spreadsheetTargetSpreadsheets[k].getName().endsWith("csv") && !spreadsheetTargetSpreadsheets[k].getName().endsWith("xls"))
					k = k + 1;
					
				String[] sourceName = spreadsheetSourceSpreadsheets[i].getName().split("[.]");			
				String[] targetName = spreadsheetTargetSpreadsheets[k].getName().split("[.]");	
				
				if(!(sourceName[0].equals(targetName[0]))){
			
					spreadsheetSourceSchema1 = schemaRepository.getSchemaByName("dryad_source_"+sourceName[0].replaceAll("[^a-zA-Z0-9]", ""));
					spreadsheetSourceSchema2 = schemaRepository.getSchemaByName("schema_target_"+targetName[0].replaceAll("[^a-zA-Z0-9]", ""));
					
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
					
					
					//TODO change this so that it doesn't need all the other stuff beforehand ... this is an integration test, not a unit test
					/*List<Matching> matching1 = schemaService.match(spreadsheetSourceSchema1, spreadsheetSourceSchema2, nameMatcherList, controlParameters);
		
					if(matching1 != null && !matching1.isEmpty()){
						List<Matching> lstMatchings = new ArrayList<Matching>();
						lstMatchings.addAll(matching1);
						
						Set<Schema> sourceSchemas = new HashSet<Schema>();
						sourceSchemas.add(spreadsheetSourceSchema1);
						
						Set<Schema> targetSchemas = new HashSet<Schema>();
						targetSchemas.add(spreadsheetSourceSchema2);
												*/
						
						System.out.println("");
						logger.debug("---------------------------------------------------------------");
						logger.debug("-                                                             -");
						logger.debug("------------------SCHEMATIC CORRESPONDENCE---------------------");
						logger.debug("-                                                             -");
						logger.debug("---------------------------------------------------------------");
					
					
						//schemaService.inferCorrespondences(sourceSchemas, targetSchemas, matching1, new HashMap<ControlParameterType, ControlParameter>());
					//}// end if match is not null
				
					
					
					System.out.println("FIM");
				}
			}//end for target
		}//end for source
		
		
		List<SchematicCorrespondence> lstSC = schematicCorrespondenceRepository.findAll();
		try {
			schemaService.createAttributesCorrelation(lstSC);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*try{
			System.out.println("iniciando");
			
			List<SchematicCorrespondence> lstSC =  schematicCorrespondenceRepository.findAll();
					//schematicCorrespondenceService.findAllSchematicCorrespondences();
			System.out.println("lstSC"+ lstSC.size());
		
			schemaService.generateResultsToGraph(lstSC);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/
		
	}

	
}
