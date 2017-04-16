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
public class CopyOfSchemaServiceImplIntegrationTest extends AbstractIntegrationTest {

	private static Logger logger = Logger.getLogger(CopyOfSchemaServiceImplIntegrationTest.class);

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
			
				if (!spreadsheetSourceSpreadsheets[i].getName().endsWith("xls"))
					i = i + 1;
				
				if (!spreadsheetTargetSpreadsheets[k].getName().endsWith("xls"))
					k = k + 1;
					
				String[] sourceName = spreadsheetSourceSpreadsheets[i].getName().split("[.]");			
				String[] targetName = spreadsheetTargetSpreadsheets[k].getName().split("[.]");	
				
				if(!(sourceName[0].equals(targetName[0]))){
			
					spreadsheetSourceSchema1 = schemaRepository.getSchemaByName("schema_source_"+sourceName[0]);
					spreadsheetSourceSchema2 = schemaRepository.getSchemaByName("schema_target_"+targetName[0]);
					
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
					List<Matching> matching1 = schemaService.match(spreadsheetSourceSchema1, spreadsheetSourceSchema2, nameMatcherList, controlParameters);
		
					if(matching1 != null && !matching1.isEmpty()){
						List<Matching> lstMatchings = new ArrayList<Matching>();
						lstMatchings.addAll(matching1);
						//lstMatchings.addAll(matching2);
						//lstMatchings.addAll(matching3);
						
						Set<Schema> sourceSchemas = new HashSet<Schema>();
						sourceSchemas.add(spreadsheetSourceSchema1);
						//sourceSchemas.add(spreadsheetSourceSchema1);
						//sourceSchemas.add(spreadsheetSourceSchema2);
						Set<Schema> targetSchemas = new HashSet<Schema>();
						targetSchemas.add(spreadsheetSourceSchema2);
						//targetSchemas.add(spreadsheetSourceSchema2);
						//targetSchemas.add(spreadsheetSourceSchema3);
						
						/*int index = 0;
						for (Matching matching : lstMatchings) {
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
						}*/
						
						
						System.out.println("");
						logger.debug("---------------------------------------------------------------");
						logger.debug("-                                                             -");
						logger.debug("------------------SCHEMATIC CORRESPONDENCE---------------------");
						logger.debug("-                                                             -");
						logger.debug("---------------------------------------------------------------");
					
					
						Set<SchematicCorrespondence> schematicCorrespondencesReturn = schemaService.inferCorrespondences(sourceSchemas, targetSchemas, matching1, new HashMap<ControlParameterType, ControlParameter>());
					}// end if match is not null
					
					/*System.out.println("schematicCorrespondencesReturn.size(): " + schematicCorrespondencesReturn.size());
					for (SchematicCorrespondence schematicCorrespondence : schematicCorrespondencesReturn) {
						System.out.println(" ");
						System.out.println(" ");
						System.out.println("schematicCorrespondence.getCardinalityType(): " + schematicCorrespondence.getCardinalityType());
						System.out.println("schematicCorrespondence.getgetType(): " + schematicCorrespondence.getConstructRelatedSchematicCorrespondenceType());
						System.out.println("schematicCorrespondence.getDirection(): " + schematicCorrespondence.getDirection());
						System.out.println("schematicCorrespondence.getName(): " + schematicCorrespondence.getName());
						System.out.println("schematicCorrespondence.getSchematicCorrespondenceType(): " + schematicCorrespondence.getSchematicCorrespondenceType());
						System.out.println("schematicCorrespondence.getShortName(): " + schematicCorrespondence.getShortName());
						System.out.println("schematicCorrespondence.getConstructs1(): " + schematicCorrespondence.getConstructs1());
						for (CanonicalModelConstruct construct1 : schematicCorrespondence.getConstructs1()) {
							System.out.println("construct1.getName()" + construct1.getName());
							System.out.println("construct1.getSchema()" + construct1.getSchema().getName());
							System.out.println("construct1.getMorphisms()" + construct1.getMorphisms());
						}
						System.out.println("schematicCorrespondence.getConstructs2(): " + schematicCorrespondence.getConstructs2());
						for (CanonicalModelConstruct construct2 : schematicCorrespondence.getConstructs2()) {
							System.out.println("construct2.getName()" + construct2.getName());
							System.out.println("construct2.getSchema()" + construct2.getSchema().getName());
							System.out.println("construct2.getMorphisms()" + construct2.getMorphisms());
						}
						System.out.println(" ");
						System.out.println(" ");
						for (SchematicCorrespondence schematicCorrespondence2 : schematicCorrespondence.getChildSchematicCorrespondences()) {
							System.out.println(" ");
							System.out.println(" ");
							System.out.println("schematicCorrespondence2.getCardinalityType(): " + schematicCorrespondence2.getCardinalityType());
							System.out.println("schematicCorrespondence2.getgetType(): " + schematicCorrespondence2.getConstructRelatedSchematicCorrespondenceType());
							System.out.println("schematicCorrespondence2.getDirection(): " + schematicCorrespondence2.getDirection());
							System.out.println("schematicCorrespondence2.getName(): " + schematicCorrespondence2.getName());
							System.out.println("schematicCorrespondence2.getSchematicCorrespondenceType(): " + schematicCorrespondence2.getSchematicCorrespondenceType());
							System.out.println("schematicCorrespondence2.getShortName(): " + schematicCorrespondence2.getShortName());
							System.out.println("schematicCorrespondence2.getConstructs1(): " + schematicCorrespondence2.getConstructs1());
							for (CanonicalModelConstruct construct1 : schematicCorrespondence2.getConstructs1()) {
								System.out.println("schematicCorrespondence2.construct1.getName()" + construct1.getName());
								System.out.println("schematicCorrespondence2.construct1.getSchema()" + construct1.getSchema().getName());
								System.out.println("schematicCorrespondence2.construct1.getMorphisms()" + construct1.getMorphisms());
							}
							System.out.println("schematicCorrespondence2.getConstructs2(): " + schematicCorrespondence2.getConstructs2());
							for (CanonicalModelConstruct construct2 : schematicCorrespondence2.getConstructs2()) {
								System.out.println("schematicCorrespondence2.construct2.getName()" + construct2.getName());
								System.out.println("schematicCorrespondence2.construct2.getSchema()" + construct2.getSchema().getName());
								System.out.println("schematicCorrespondence2.construct2.getMorphisms()" + construct2.getMorphisms());
							}
						}
					}*/	
					
					System.out.println("FIM");
				}
			}//end for target
		}//end for source
		

		
	/*try {
		List<SchematicCorrespondence> lstSC = schematicCorrespondenceService.findAllSchematicCorrespondences();
        schemaService.generateResultsToGraph(lstSC);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	*/
		
		//Set<Mapping> mapping1 = schemaService.viewGen(schematicCorrespondencesReturn);
		//System.out.println("mapping: " + mapping1.iterator().next().getQuery1String());
		
		/*logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("-----------------------------EXTRACT---------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		List<SchematicCorrespondence> schematicCorrespondencesDB = schematicCorrespondenceService.findAllSchematicCorrespondences();
		
		Set<Schema> smExtracted = schemaService.extract(schematicCorrespondencesDB);
		Set<CanonicalModelConstruct> setCM = smExtracted.iterator().next().getCanonicalModelConstructs();	
		
		for (CanonicalModelConstruct canonicalModelConstruct : setCM) {
			System.out.println("CM Name: " + canonicalModelConstruct.getName());
		}
		
		Set<SchematicCorrespondence> extractedSC = smExtracted.iterator().next().getSchematicCorrespondenceMinimumModel();
		
		System.out.println("schematicCorrespondencesMergeCorrespondences.size(): " + extractedSC.size());
		for (SchematicCorrespondence schematicCorrespondence3 : extractedSC) {
			
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("schematicCorrespondence3.getCardinalityType(): " + schematicCorrespondence3.getCardinalityType());
			System.out.println("schematicCorrespondence3.getgetType(): " + schematicCorrespondence3.getConstructRelatedSchematicCorrespondenceType());
			System.out.println("schematicCorrespondence3.getDirection(): " + schematicCorrespondence3.getDirection());
			System.out.println("schematicCorrespondence3.getName(): " + schematicCorrespondence3.getName());
			System.out.println("schematicCorrespondence3.getSchematicCorrespondenceType(): " + schematicCorrespondence3.getSchematicCorrespondenceType());
			System.out.println("schematicCorrespondence3.getShortName(): " + schematicCorrespondence3.getShortName());
			System.out.println(" ");
			System.out.println(" ");
			for (SchematicCorrespondence schematicCorrespondence4 : schematicCorrespondence3.getChildSchematicCorrespondences()) {
				System.out.println(" ");
				System.out.println(" ");
				System.out.println("schematicCorrespondence4.getCardinalityType(): " + schematicCorrespondence4.getCardinalityType());
				System.out.println("schematicCorrespondence4.getgetType(): " + schematicCorrespondence4.getConstructRelatedSchematicCorrespondenceType());
				System.out.println("schematicCorrespondence4.getDirection(): " + schematicCorrespondence4.getDirection());
				System.out.println("schematicCorrespondence4.getName(): " + schematicCorrespondence4.getName());
				System.out.println("schematicCorrespondence4.getSchematicCorrespondenceType(): " + schematicCorrespondence4.getSchematicCorrespondenceType());
				System.out.println("schematicCorrespondence4.getShortName(): " + schematicCorrespondence4.getShortName());
			}
		}
		
		Schema minimumModel = schemaService.getMinimumModel();
		
		/*
		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("-----------------------------MERGE-----------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		

		Schema sm = schemaService.merge(schematicCorrespondencesReturn);
		Map<Integer, SchematicCorrespondence> mergeSC = sm.getSchematicCorrespondenceSchemaMerged();
		
		System.out.println("schematicCorrespondencesMergeCorrespondences.size(): " + mergeSC.size());
		for (int j = 1; j <= mergeSC.size(); j++) {
			SchematicCorrespondence schematicCorrespondence3 = mergeSC.get(j);
		
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("schematicCorrespondence3.getCardinalityType(): " + schematicCorrespondence3.getCardinalityType());
			System.out.println("schematicCorrespondence3.getgetType(): " + schematicCorrespondence3.getConstructRelatedSchematicCorrespondenceType());
			System.out.println("schematicCorrespondence3.getDirection(): " + schematicCorrespondence3.getDirection());
			System.out.println("schematicCorrespondence3.getName(): " + schematicCorrespondence3.getName());
			System.out.println("schematicCorrespondence3.getSchematicCorrespondenceType(): " + schematicCorrespondence3.getSchematicCorrespondenceType());
			System.out.println("schematicCorrespondence3.getShortName(): " + schematicCorrespondence3.getShortName());
			System.out.println(" ");
			System.out.println(" ");
			for (SchematicCorrespondence schematicCorrespondence4 : schematicCorrespondence3.getChildSchematicCorrespondences()) {
				System.out.println(" ");
				System.out.println(" ");
				System.out.println("schematicCorrespondence4.getCardinalityType(): " + schematicCorrespondence4.getCardinalityType());
				System.out.println("schematicCorrespondence4.getgetType(): " + schematicCorrespondence4.getConstructRelatedSchematicCorrespondenceType());
				System.out.println("schematicCorrespondence4.getDirection(): " + schematicCorrespondence4.getDirection());
				System.out.println("schematicCorrespondence4.getName(): " + schematicCorrespondence4.getName());
				System.out.println("schematicCorrespondence4.getSchematicCorrespondenceType(): " + schematicCorrespondence4.getSchematicCorrespondenceType());
				System.out.println("schematicCorrespondence4.getShortName(): " + schematicCorrespondence4.getShortName());
			}
		}
		
		
		System.out.println("");
		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("----------------------------MAPPING----------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		Mapping mapping2 = schemaService.viewGen(sm.getSchematicCorrespondenceSchemaMerged());
		System.out.println("mapping: " + mapping2.toString());

		
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" INVERT ");
		System.out.println(" ");
		System.out.println(" ");
		
		Map<Integer, SchematicCorrespondence> mergeSC1 = sm.getSchematicCorrespondenceSchemaMerged();
		System.out.println("schematicCorrespondencesMergeCorrespondences.size(): " + mergeSC1.size());
		Set<SchematicCorrespondence> setSC = new HashSet<SchematicCorrespondence>();
		for (int j = 1; j <= mergeSC1.size(); j++) {
			SchematicCorrespondence schematicCorrespondence3 = mergeSC1.get(j);
			setSC.add(schemaService.invert(schematicCorrespondence3));
		}
		

		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("----------------------------MAPPING----------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		Set<Mapping> mapping3 = schemaService.viewGen(setSC);
		for (Mapping mapping : mapping3) {
			System.out.println("mapping: " + mapping.toString());
		}*/
	
	}
	
	
	/*@Test
	public void testReverseHP() throws ExecutionException {
		
		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------- MATCHING -------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		//TODO change this so that it doesn't need all the other stuff beforehand ... this is an integration test, not a unit test
		List<Matching> matchings = schemaService.match(spreadsheetSourceSchema1, spreadsheetSourceSchema2, nameMatcherList, controlParameters);
		Set<Schema> sourceSchemas = new HashSet<Schema>();
		sourceSchemas.add(spreadsheetSourceSchema1);
		Set<Schema> targetSchemas = new HashSet<Schema>();
		targetSchemas.add(spreadsheetSourceSchema2);
		
		
		System.out.println("");
		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("------------------SCHEMATIC CORRESPONDENCE---------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		Set<SchematicCorrespondence> schematicCorrespondencesReturn = schemaService.inferCorrespondences(sourceSchemas, targetSchemas, matchings, new HashMap<ControlParameterType, ControlParameter>());
			
		System.out.println("");		
		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("-----------------------------MERGE-----------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		

		Schema sm = schemaService.merge(schematicCorrespondencesReturn);
		Map<Integer, SchematicCorrespondence> mergeSC = sm.getSchematicCorrespondenceSchemaMerged();
		
		System.out.println("");
		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("----------------------------MAPPING----------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		Mapping mapping1 = schemaService.viewGen(sm.getSchematicCorrespondenceSchemaMerged());
		System.out.println("mapping: " + mapping1.toString());
		
		
			}*/

	
}
