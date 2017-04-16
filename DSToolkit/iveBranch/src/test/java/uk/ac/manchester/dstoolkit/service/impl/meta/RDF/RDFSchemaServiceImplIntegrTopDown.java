package uk.ac.manchester.dstoolkit.service.impl.meta.RDF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.InferCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;

/**
 * @author klitos
 *
 * This is not a proper test class, used as a client to run implemented functionality for top-down integration. 
 * The class extends the RDFAbstractIntegrationTest which is mainly responsible for loading the sources along with
 * their schema into DSToolkit.
 *   
 * Top Down Integration:
 *
 * - Load the Global Schema along with the Local Schemas.
 * - Match GS with all LS
 *   
 */

public class RDFSchemaServiceImplIntegrTopDown extends RDFAbstractIntegrationTest {
	
	private static Logger logger = Logger.getLogger(RDFSchemaServiceImplIntegrTopDown.class);
	
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
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	
	//Declare list of matchers schema and instance base matchers available
	private final List<MatcherService> nameMatcherList = new ArrayList<MatcherService>();
	private final List<MatcherService> nameAndInstanceMatcherList = new ArrayList<MatcherService>();
	
	private Map<ControlParameterType, ControlParameter> controlParameters;

	//DBtune RDF source schemas
	private Schema magnatuneRDFSchema;
	private Schema jamendoRDFSchema;	
	private Schema dbTuneIntegrSchema;
	
	
	@Override
	@Before
	public void setUp() {
		super.setUp();
		
		/*//Set the threshold for all matchers
		controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		ControlParameter thresholdForAllMatchers = new ControlParameter(ControlParameterType.MATCH_SCORE_THRESHOLD, "0.4");
		controlParameters.put(ControlParameterType.MATCH_SCORE_THRESHOLD, thresholdForAllMatchers);

		magnatuneRDFSchema = schemaRepository.getSchemaByName("magnatuneRDFSchema");
		jamendoRDFSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		
		*//******************//*
		//My test for edit-distance
		MatcherService levenshteinMatcher = new LevenshteinMatcherServiceImpl();
		  
		//add the SELECTION TYPE here is Threshold
		ControlParameter thresholdSelectionForLevenshteinMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());		
		
		//For the threshold set threshold to a value here 0.3
		ControlParameter thresholdForLevenshteinMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.4");
		
		//Add them on the levenshteinMatcher
		levenshteinMatcher.addControlParameter(thresholdSelectionForLevenshteinMatcher);
		levenshteinMatcher.addControlParameter(thresholdForLevenshteinMatcher);
		
		//add it to the list of matchers to be executed now
		nameMatcherList.add(levenshteinMatcher);
		 */
		
		
		
		/******************/
		
		//dbTuneIntegrSchema = schemaRepository.getSchemaByName("DBTuneIntegrRDF");
		
		//Use Schema based matcher - NGramMatcher
		/*MatcherService ngramMatcherForNameMatcher = new NGramMatcherServiceImpl(3);
		
		MatcherService nameMatcher = new NameMatcherServiceImpl();
		ControlParameter thresholdSelectionForNameMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		ControlParameter thresholdForNameMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.3");
		nameMatcher.addControlParameter(thresholdSelectionForNameMatcher);
		nameMatcher.addControlParameter(thresholdForNameMatcher);
		nameMatcher.addChildMatcher(ngramMatcherForNameMatcher);

	    //Can have several matchers, add NGram to the set of matchers
		nameMatcherList.add(nameMatcher);
		
		//Use Instance based matcher		
		/*MatcherService ngramMatcherForInstanceMatcher = new NGramMatcherServiceImpl(3);

		MatcherService instanceMatcher = new InstanceBasedMatcherServiceImpl();
		ControlParameter thresholdSelectionForInstanceMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		ControlParameter thresholdForInstanceMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.3");
		instanceMatcher.addControlParameter(thresholdSelectionForInstanceMatcher);
		instanceMatcher.addControlParameter(thresholdForInstanceMatcher);
		instanceMatcher.addChildMatcher(ngramMatcherForInstanceMatcher);

		nameAndInstanceMatcherList.add(nameMatcher);
		nameAndInstanceMatcherList.add(instanceMatcher);*/	
	
	}//end setUp()	
	
	/** 
	 * Schema matcher only 
	 * 
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.meta.SchemaServiceImpl#match(uk.ac.manchester.dstoolkit.domain.models.meta.Schema, uk.ac.manchester.dstoolkit.domain.models.meta.Schema, java.util.List)}.
	 * @throws ExecutionException 
	 */
	@Test
	public void testMatchSchemaSchemaListOfMatcherService() throws ExecutionException {
		logger.debug("in testMatchSchemaSchemaListOfMatcherService()");
		
		/*Step 1: Fetch metadata*/
		//schemaService.schemaEnrichment(magnatuneRDFSchema, jamendoRDFSchema);
		
		
		/***************************************************************************************************
		 * OUTPUT: Visualise the RDF schemas, output <constucts and URIs>
		 */
		/*
		 String dotStringSource = graphvizDotGeneratorService.generateDot(magnatuneRDFSchema, "vertical", true, true);
		 File source = graphvizDotGeneratorService.exportAsDOTFile(dotStringSource, "Schema", "source");	
		 graphvizDotGeneratorService.exportDOT2PNG(source, "png", "source", null);
		
		 String dotStringTarget = graphvizDotGeneratorService.generateDot(jamendoRDFSchema, "vertical", true, true);
		 File target = graphvizDotGeneratorService.exportAsDOTFile(dotStringTarget, "Schema", "target");	
		 graphvizDotGeneratorService.exportDOT2PNG(target, "png", "target", null);
		
		/****************************************************************************************************/
		
		//List<Matching> matchings = schemaService.match(magnatuneRDFSchema, jamendoRDFSchema, nameMatcherList, controlParameters);
		
		
		

		
		

		//List<Matching> matchings = schemaService.match(magnatuneRDFSchema, jamendoRDFSchema, nameMatcherList, controlParameters);
		
		//List<Matching> matchings = schemaService.match(telegraphisRDFSchema, mondialEuropeRDFSchema, nameAndInstanceMatcherList,
				//telegraphisRDFSchema.getDataSource(), mondialEuropeRDFSchema.getDataSource(), controlParameters);

		
		//assertEquals(57, matchings.size());
		//logger.debug("matchings :" + matchings);
		
	    //Use Graphiz to visualise Matchings
		//if (matchings != null) {
			/*Set<SuperAbstract> magnatuneSAset = magnatuneRDFSchema.getSuperAbstracts();
			List<SuperAbstract> magnatuneSAlist = new ArrayList<SuperAbstract>(magnatuneSAset);
			
			Set<SuperAbstract> jamendoSAset = jamendoRDFSchema.getSuperAbstracts();
			List<SuperAbstract> jamendoSAlist = new ArrayList<SuperAbstract>(jamendoSAset);	*/
			
			/*logger.debug("generate Graphiz dot for matchings :");			
			String grahizMatchings = graphvizDotGeneratorService.generateDot(magnatuneSAlist, jamendoSAlist, matchings, true, null);*/
			//logger.debug(grahizMatchings);
		//}	
		
	}//end testMatchSchemaSchemaListOfMatcherService()

	
	/**
	 * RDF schemas integration - Infer correspondences
	 * 
	 * This is not a Junit test but an Integration test
	 * 
	 * @throws ExecutionException
	 */
 /*   @Test
	public void testInferCorrespondence() throws ExecutionException {
    	
        *//***
         * STEP 1: Matching 
         *  - Global Schema  : dbTuneIntegrSchema (GS)
         *  - Local Schemata : magnatune (LS1), jamendo (LS2)
         *//*
    	
    	Match(dbTuneIntegrRDF, magnatune)
		List<Matching> matchings1 = schemaService.match(dbTuneIntegrSchema, magnatuneRDFSchema, nameMatcherList,
				dbTuneIntegrSchema.getDataSource(), magnatuneRDFSchema.getDataSource(), controlParameters);   	
    	
    	Graphiz (dbTuneIntegrSchema, magnatuneRDFSchema)
		if (matchings1 != null) {			
			Set<SuperAbstract> dbtuneSAset = dbTuneIntegrSchema.getSuperAbstracts();
			List<SuperAbstract> dbtuneSAlist = new ArrayList<SuperAbstract>(dbtuneSAset);
			
			Set<SuperAbstract> magnatuneSAset = magnatuneRDFSchema.getSuperAbstracts();
			List<SuperAbstract> magnatuneSAlist = new ArrayList<SuperAbstract>(magnatuneSAset);

			logger.debug("generate Graphiz dot for matchings :");			
			String grahizMatchings = graphvizDotGeneratorService.generateDot(dbtuneSAlist, magnatuneSAlist, matchings1, true, null);
			logger.debug(grahizMatchings);
		}
		
    	Match(dbTuneIntegrRDF, jamendo)
		List<Matching> matchings2 = schemaService.match(dbTuneIntegrSchema, jamendoRDFSchema, nameMatcherList,
				dbTuneIntegrSchema.getDataSource(), jamendoRDFSchema.getDataSource(), controlParameters); 
    	
    	Graphiz (dbTuneIntegrSchema, jamendoRDFSchema)
		if (matchings2 != null) {			
			Set<SuperAbstract> dbtuneSAset = dbTuneIntegrSchema.getSuperAbstracts();
			List<SuperAbstract> dbtuneSAlist = new ArrayList<SuperAbstract>(dbtuneSAset);
		
			Set<SuperAbstract> jamendoSAset = jamendoRDFSchema.getSuperAbstracts();
			List<SuperAbstract> jamendoSAlist = new ArrayList<SuperAbstract>(jamendoSAset);

			logger.debug("generate Graphiz dot for matchings :");			
			String grahizMatchings = graphvizDotGeneratorService.generateDot(dbtuneSAlist, jamendoSAlist, matchings2, true, null);
			logger.debug(grahizMatchings);
	    }//end if		
		
		Union the Matches from magnatune & jamendo
	    matchings1.addAll(matchings2);   	    
	    
	    *//***
	     * STEP 2: Infer-correspondences
	     *//*
		Set<Schema> sourceSchemas = new HashSet<Schema>();
		sourceSchemas.add(dbTuneIntegrSchema);
		
		Set<Schema> targetSchemas = new HashSet<Schema>();
		targetSchemas.add(magnatuneRDFSchema);
		targetSchemas.add(jamendoRDFSchema);		
		
		//Print results of the matchings
		int index = 0;
		for (Matching matching : matchings) {
			logger.debug("matching_" + index + ", id: " + matching.getId() + ", score: " + matching.getScore());
			logger.debug("matching.getMatcherName(): " + matching.getMatcherName());
			logger.debug("matching.getConstructs1().size(): " + matching.getConstructs1().size());
			logger.debug("matching.getConstructs2().size(): " + matching.getConstructs2().size());
			if (matching instanceof OneToOneMatching) {
				OneToOneMatching oneToOneMatching = (OneToOneMatching) matching;
				CanonicalModelConstruct construct1 = oneToOneMatching.getConstruct1();
				CanonicalModelConstruct construct2 = oneToOneMatching.getConstruct2();
				if (construct1 instanceof SuperLexical) {
					SuperLexical sl1 = (SuperLexical) construct1;
					logger.debug("construct1 = SL: " + sl1.getName() + ", id: " + sl1.getId() + " parentSA: "
							+ sl1.getParentSuperAbstract().getName());
				} else if (construct1 instanceof SuperAbstract) {
					SuperAbstract sa1 = (SuperAbstract) construct1;
					logger.debug("construct1 = SA: " + sa1.getName() + ", id: " + sa1.getId());
				}
				if (construct2 instanceof SuperLexical) {
					SuperLexical sl2 = (SuperLexical) construct2;
					logger.debug("construct2 = SL: " + sl2.getName() + ", id: " + sl2.getId() + " parentSA: "
							+ sl2.getParentSuperAbstract().getName());
				} else if (construct2 instanceof SuperAbstract) {
					SuperAbstract sa2 = (SuperAbstract) construct2;
					logger.debug("construct1 = SA: " + sa2.getName() + ", id: " + sa2.getId());
				}
			}
			index++;			
			
		}//end for
		
		
		//Run infer correspondences 
		logger.debug("RDF_run infer correspondences>>>");
		
		Set<SchematicCorrespondence> correspondencesToReturn = schemaService.inferCorrespondences(sourceSchemas, targetSchemas, matchings1 ,new HashMap<ControlParameterType, ControlParameter>());
		
	    for (SchematicCorrespondence correspondence : correspondencesToReturn) {
			logger.debug(">>>"+ correspondence.toString());
        }
	}//end testInferCorrespondence()
*/	
	
 
    
	
	
	/**
	 * Use this test case to visualise just the schemas.
	 */
	/*@Test
	public void testVisualiaseSchemasGraphiz() {
		//TODO: Do Graphiz to automatically generate a png image.
		//Use Graphiz to visualise RDF source schemas imported
		if (magnatuneRDFSchema != null) {
			logger.debug("magnatuneRDFSchema :" + magnatuneRDFSchema.getName());
			logger.debug("generate Graphiz dot for magnatuneRDFSchema :");
			String graphizMagnatune = graphvizDotGeneratorService.generateDot(magnatuneRDFSchema);
			logger.debug(graphizMagnatune);
		}
		
		if (jamendoRDFSchema != null) {
			logger.debug("jamendoRDFSchema :" + jamendoRDFSchema.getName());
			logger.debug("generate Graphiz dot for jamendoRDFSchema:");
			String graphizJamendo = graphvizDotGeneratorService.generateDot(jamendoRDFSchema);
			logger.debug(graphizJamendo);
		}		
	}//testVisualiaseSchemasGraphiz()
*/	
	
}//end class
