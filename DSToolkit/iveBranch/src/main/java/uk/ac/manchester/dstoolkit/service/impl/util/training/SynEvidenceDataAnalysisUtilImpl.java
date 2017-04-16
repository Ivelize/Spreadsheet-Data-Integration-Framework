package uk.ac.manchester.dstoolkit.service.impl.util.training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.TempConstruct;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.MeasureType;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.ConstructBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.training.SynEvidenceDataAnalysisUtil;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;

/*********************************************************************************************************
 * 									TrainingServiceUtilImpl (Abstract)
 * 													|
 * 					________________________________|___________________________________
 * 					|								|									|
 * 					|								|									|
 * 		SynEvidenceTrainingUtilImpl		SemEvidenceDataAnalysisUtilImpl		SemEvidenceTrainingUtilImpl
 * 			  (this class)
 *
 * 
 * This Class is responsible for:
 *  - import alignment in an RDF named Graph
 *  - extracting the schema element names from the alignment
 *  - store the schema elements as two sets
 *  - run the matcher to calculate the pairwise similarities
 *  - store each pair with its similarity in a named Graph
 *  
 *  Then aim of this class is to study the behaviour of specific matchers when they are matching 
 *  pair of constructs that are known to be equivalent or non-equivalent. For example, edit-distance
 *  works at the element level so we are testing it on that. If another algorithm was working in another
 *  level we will test it directly on that level. For example an algorithm that finds the similarity
 *  between constructs be looking rdfs:label, or rdfs:comment bags of words, it can be tested only on that
 * 
 *  Note: I have noticed that the OAEI ontologies have also instance level data and that there are test where only the TBox is
 *  of the ontology is changed, therefore perhaps I can use an algorithm to import the ontology in DSToolkit, or use my schema
 *  extraction to get the schema  
 *  
 *  train_matcher_align = http://train.matcher/temp/align
 *  train_classify_tp = http://train.matcher/classify/tp
 *  train_classify_fp = http://train.matcher/classify/fp
 * 
 *  Files test_101 - contain the alignment 
 *        onto_101 - contain the ontology
 * 
 * @author klitos
 */
@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class SynEvidenceDataAnalysisUtilImpl extends TrainingServiceUtilImpl implements SynEvidenceDataAnalysisUtil {
	
	private static Logger logger = Logger.getLogger(SynEvidenceDataAnalysisUtilImpl.class);
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	@Autowired
	private MatchingProducerService matchingProducerService;
	
	//Hold the list of matchers to run
	private List<MatcherService> matchersList = null;
	
	private TDBStoreServiceImpl tdbStore = null;
	private PrefixMapping pmap = null;
	
	//Hold the URIs of the graphs in TDB
	public static String alignGraph;
	public static String tpGraphURI;
	public static String fpGraphURI;
	
	//Hold the location of the alignment files
	private Map<String, String> alignMap;
	
	//Location to hold the output
	private static String location = "./src/test/resources/training/benchmark/";
	
	/*Constructor*/
	public SynEvidenceDataAnalysisUtilImpl(TDBStoreServiceImpl tdb, String align_graph, String tp_g_uri, String fp_g_uri) {
		this.tdbStore = tdb;		
		this.alignGraph = align_graph;
		this.tpGraphURI = tp_g_uri;
		this.fpGraphURI = fp_g_uri;		
		
		matchersList = new ArrayList<MatcherService>();
		
		/*Prepare a prefix map to be used to return Qnames of the properties*/
		if (pmap == null) {
			pmap = this.createPrefixMap();
		}
	}	
	
	/*Constructor 2*/
	public SynEvidenceDataAnalysisUtilImpl(TDBStoreServiceImpl tdb, String align_graph, String tp_g_uri, String fp_g_uri, Map<String, String> alignMap) {
		this.tdbStore = tdb;		
		this.alignGraph = align_graph;
		this.tpGraphURI = tp_g_uri;
		this.fpGraphURI = fp_g_uri;		
		this.alignMap 	= alignMap;	
		
		matchersList = new ArrayList<MatcherService>();
		
		/*Prepare a prefix map to be used to return Qnames of the properties*/
		if (pmap == null) {
			pmap = this.createPrefixMap();
		}
	}
	
	/***
	 * Reset all the graphs associated with the benchmark
	 */
	public void resetAll() {
		tdbStore.rmvNamedModel(alignGraph);
		tdbStore.rmvNamedModel(tpGraphURI);
		tdbStore.rmvNamedModel(fpGraphURI);	
	}//end resetAll()	
	
	/***
	 * Reset the classification of matches to TP/FP
	 */
	public void resetClassifications() {
		tdbStore.removeNamedModel(tpGraphURI);
		tdbStore.removeNamedModel(fpGraphURI);
	}//end resetClassifications()	
	
	/***
	 * Add the map that holds the locations of the alignment files, from the benchmark
	 * @param alignMap
	 */
	public void addAlignments(Map<String, String> alignMap) {
		this.alignMap = alignMap;
	}	
	
	/***
	 * Add matchers to the service to find their distributions
	 */
	public void addMatcher(MatcherService matcherService) {
		matchersList.add(matcherService);
	}//end addMatcher()	
	
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	public void runExperiment() {
		
		//Hold the source and target constructs to match their localnames
		List<CanonicalModelConstruct> sourceConstructs;
		List<CanonicalModelConstruct> targetConstructs;				

		//Iterate over the HashMap that holds the alignment files
		for (String key : alignMap.keySet()) {	
			
			String alignmentFile = alignMap.get(key);
			logger.debug("alignmentFile: " + alignmentFile);
		
			//Load the alignment in TDB store, if not exists
			tdbStore.loadDataToModelFromRDFDump(alignGraph, alignmentFile);
					
			//Construct the List for the source, target constructs
			sourceConstructs = constructCanonicalModelConstructList("align:entity1");		
			targetConstructs = constructCanonicalModelConstructList("align:entity2");			
			
			logger.info("sourceConstructs: " + sourceConstructs.size());
			logger.info("targetConstructs: " + targetConstructs.size());						
		
			//For every matcher registered to the benchmark service
			if (this.matchersList == null || this.matchersList.size() == 0) {
				logger.error("ERROR - Benchmark service requires list of matcher services.");
			} else {
			
				for (MatcherService matcherService : matchersList) {				
					//Edit-distance and ngram are Construct based matchers
					if (matcherService instanceof ConstructBasedMatcherService) {
					
						//Hold the matchings of each matcher in a similarity matrix 
						float[][] simMatrix = ((ConstructBasedMatcherService) matcherService).match(sourceConstructs, targetConstructs);
					
						//graphvizDotGeneratorService.generateDOT(sourceConstructs, targetConstructs, simMatrix, "training");
					
						//Classify matches to TP or FP
						classifyMatches(simMatrix, sourceConstructs, targetConstructs, matcherService, key);
					
					}//end if				
				}//end for			
			}//end else	
			
			//Reset the alignment graph
			tdbStore.removeNamedModelTrans(alignGraph);	
			
			//Force the garbage collector
			System.gc();
		}//end for				
		
		
		//write matches in a text file, one for TP and one for FP pairs
		//this will be needed when studying the behaviour of a matcher
		//store the results here: ./src/test/resources/training/benchmark/
		writeModelToCSVFile(tpGraphURI, MeasureType.TRUE_POSITIVE);
		writeModelToCSVFile(fpGraphURI, MeasureType.FALSE_POSITIVE);

					
		//Print statistics to a file
		//writeStatisticsFile(matcherService);

	}//end extractSchemaFromAlignment()		
	
	/***
	 * Print some statistics to a file, for this particular matcher
	 * 
	 * ./src/test/resources/training/benchmark/stats.txt
	 */
	public void writeStatisticsFile(MatcherService matcherService) {				
		int totalNoTP = this.getCount(tpGraphURI, matcherService);
		int totalNoFP = this.getCount(fpGraphURI, matcherService);
		int totalNoMatches = totalNoTP + totalNoFP;
					
		StringBuilder stats = new StringBuilder();
		
		stats.append("Statistics for matcher: " + matcherService.getName()).append("\n");
		stats.append(" - Total count of TP matches: ").append(totalNoTP).append("\n");
		stats.append(" - Total count of FP matches: ").append(totalNoFP).append("\n");
		stats.append(" - Total count of matches: ").append(totalNoMatches);	
		
		try {
			File file = new File("./src/test/resources/training/benchmark/stats_" + matcherService.getName() +".txt");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(stats.toString());
			output.close();	        
		} catch (Exception exe) {
			logger.debug("ERROR - while writing stats to file." + exe);	
		}
	}//end writeStatisticsFile()	
	
	/***
	 * Output TP/FP model as a csv text file
	 * @param model
	 * @param location
	 */
	public void writeModelToCSVFile(String modelURI, MeasureType mt) {
		String saveLoc = "";
				
		//Open a new READ transaction	
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.READ);	
		try {		
			Model model = tdbStore.getModel(modelURI);	
			
			if (this.matchersList == null || this.matchersList.size() == 0) {
				logger.error("ERROR - Benchmark service requires list of matcher services.");
			} else {
			
				for (MatcherService matcherService : matchersList) {
					
					if (mt.equals(MeasureType.TRUE_POSITIVE)) {
						saveLoc = location + "tp_" + matcherService.getName() + ".csv";
					} else if (mt.equals(MeasureType.FALSE_POSITIVE)) {
						saveLoc = location + "fp_" + matcherService.getName() + ".csv";
					}
					
					FileOutputStream out = new FileOutputStream(new File(saveLoc));
					
					Query query = csvQuery(matcherService.getName());
					QueryExecution qexec = QueryExecutionFactory.create(query, model);		
					ResultSet results = qexec.execSelect();			
					try {
						ResultSetFormatter.outputAsCSV(out, results);
					} finally { qexec.close(); }					
					
				}//end for
			}//end else		
		} catch (FileNotFoundException exe) {
			logger.debug("ERROR - while writing model to csv." + exe);			
		} finally {
			dataset.end();
		}
	}//end writeModelToCSVFile()	
	
	/***
	 * Get some stats
	 * @param model
	 * @param location
	 */
	public int getCount(String modelURI, MatcherService matcherService) {
		//Open a new READ transaction		
		String matcherName = matcherService.getName();
		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.READ);	
		try {		
			Model model = tdbStore.getModel(modelURI);	
			
			Query query = countTpFp(matcherName);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);		
			ResultSet results = qexec.execSelect();		
			try {
				if (results.hasNext()) {
					QuerySolution soln  = results.nextSolution() ;
					Literal count = soln.getLiteral("count") ;
					return count.getInt();
				}
			} finally {	qexec.close() ;	}
			
			return -1;
	
		} finally {
			dataset.end();
		}
	}//end getStats()	
	
	/***
	 * Classify matches to TP and FP and make them persistent to the triple store 
	 * 
	 * train_classify_tp = x-ns://train.matcher/classify/tp
	 * train_classify_fp = x-ns://train.matcher/classify/fp
	 * 
	 */
	public void classifyMatches(final float[][] simMatrix, final List<CanonicalModelConstruct> constructs1,
			final List<CanonicalModelConstruct> constructs2, final MatcherService matcherService, final String alignmentFile) {
		
		//Open a new WRITE transaction		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.WRITE);		
		try {			
			Model alignGraphModel 	 =  tdbStore.getModel(alignGraph);	
			Model truePositiveModel  =  tdbStore.getModel(tpGraphURI);
			Model falsePositiveModel =  tdbStore.getModel(fpGraphURI);
			
			//Loop through the simMatrix
			for (int i = 0; i < simMatrix.length; i++) {
				for (int j = 0; j < simMatrix[i].length; j++) {
			
					//TODO: I might need to change this to just > 0.0
					if (simMatrix[i][j] >= 0.0) {						
						CanonicalModelConstruct sourceConstruct = constructs1.get(i);
						CanonicalModelConstruct targetConstruct = constructs2.get(j);	
						
						//check if pair of construct URIs is in the alignment		
						Query query = alignmentASKQuery(sourceConstruct.getProperty("constructURI").getValue(),
														targetConstruct.getProperty("constructURI").getValue());
												
						QueryExecution qexec = QueryExecutionFactory.create(query, alignGraphModel);		
						boolean alignExists = qexec.execAsk();
						try {
							if (alignExists) {
								//classify match as TP, create a bNode for each match and add it to the Graph
								this.createMatch(truePositiveModel, sourceConstruct.getName(),
															targetConstruct.getName(), "TP", matcherService.getName(), simMatrix[i][j], alignmentFile);														
							} else {
								//classify match as FP, create a bNode for each match and add it to the Graph
								this.createMatch(falsePositiveModel, sourceConstruct.getName(),
															targetConstruct.getName(), "FP", matcherService.getName(), simMatrix[i][j], alignmentFile);							
							}
						} finally { qexec.close() ; }
					}//end if					
				}//end for
			}//end for			
			dataset.commit();
		} finally {
			dataset.end();
		}
	}//end classifyMatches()	
	
	/***
	 * Method that will return a new Resource to be added in the TP/FP model
	 *
	 * Store match as a BNode
	 */
	public Resource createMatch(Model model, String sourceName, String targetName, String label, String matcherName, float score, String alignmentFile) {
		
		Resource bNode = model.createResource();
		bNode.addLiteral( VOCAB.sourceEntity, sourceName );
		bNode.addLiteral( VOCAB.targetEntity, targetName );
		bNode.addLiteral( VOCAB.score, score );
		bNode.addLiteral( VOCAB.classLabel, label );
		bNode.addLiteral( VOCAB.matcherService, matcherName );
		bNode.addLiteral( VOCAB.fileName, alignmentFile );
		
		return bNode;
	}//end createMatch()	
	
	/***
	 * Add the elements to a List and return the list back,
	 * the lists will then be used to do the matching and see how the matchers perform
	 */
	public List<CanonicalModelConstruct> constructCanonicalModelConstructList(String qName) {
		List<CanonicalModelConstruct> constructs = new ArrayList<CanonicalModelConstruct>();
		
		//Open a new READ transaction		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.READ);		
		try {
			Model alignGraphModel = tdbStore.getModel(alignGraph);	
			
			Query query = getElementsQuery(qName);
			QueryExecution qexec = QueryExecutionFactory.create(query, alignGraphModel);		
			ResultSet results = qexec.execSelect();
			try {				
				for ( ; results.hasNext() ; ) {
					QuerySolution soln  = results.nextSolution() ;
					RDFNode element = soln.get("entity");	
					//Get the local name
					String localName = element.asResource().getLocalName();
					logger.debug("localName: " + localName);
					logger.debug("constructURI: " + element.asResource().getURI());
					//Create a temp construct to hold the element name
					TempConstruct tempConstruct = new TempConstruct();
					tempConstruct.setName(localName);					
					tempConstruct.addProperty(new CanonicalModelProperty("constructURI",element.asResource().getURI()));
					
					//Add it to the list of constructs
					constructs.add(tempConstruct);				
				}//end for		
			} finally {
				qexec.close();
			}			
		} finally {
			dataset.end();	
		}
		
		return constructs;
	}//end getCanonicalModelList()	
	
	/******************
	* SPARQL Queries *
	******************/
	
	/**
	 * entity 1 - is the source element (align:entity1)
	 * entity 2 - is the target element (align:entity2)
	 * 
	 * @return - get the elements of the source schema/ target schema
	 */
	private com.hp.hpl.jena.query.Query getElementsQuery(String qName) {
		 String queryString =  getNSPrefixes() +	
	        		" SELECT DISTINCT ?entity " +
	        		" WHERE { " +
	        		"   		?s " + qName + " ?entity . " +
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end getElementsQuery()	
		
	/**
	 * @return - SPARQL ASK query returns true if match exists in the alignment
	 */
	private com.hp.hpl.jena.query.Query alignmentASKQuery(String entity1URI, String entity2URI) {
		 String queryString =  getNSPrefixes() +	
	        		" ASK " +
	        		" WHERE { " +
	        		"   ?s align:entity1 <" + entity1URI + "> ." + 
	        		"   ?s align:entity2 <" + entity2URI + "> ." +
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end alignmentQuery()	
	
	/**
	 * @return - return matches for a specific matcher
	 */
	private com.hp.hpl.jena.query.Query csvQuery(String matcherName) {
		 String queryString =  getNSPrefixes() +	
	        		" SELECT ?sourceEntity ?targetEntity ?score ?classLabel ?matcherName ?fileName" +
	        		" WHERE { " +
	        		"   ?s j.0:sourceEntity ?sourceEntity ; " + 
	        		"      j.0:targetEntity ?targetEntity ; " +
	        		"      j.0:score ?score ; " +
	        		"	   j.0:classLabel ?classLabel ; " +
	        		"	   j.0:matcherService ?matcherName ; " +
	        		"	   j.0:fileName ?fileName . " +
	        		" FILTER ( ?matcherName = \""+ matcherName + "\" ) " +	
	        		" }";		
		
		return QueryFactory.create(queryString);		
	}//end csvQuery()
	
	/**
	 * @return - the matches for all the Matchers sorted by matcher name
	 */
	private com.hp.hpl.jena.query.Query allMatchersResultscsvQuery() {
		 String queryString =  getNSPrefixes() +	
	        		" SELECT ?sourceEntity ?targetEntity ?score ?classLabel ?matcherName" +
	        		" WHERE { " +
	        		"   ?s j.0:sourceEntity ?sourceEntity ; " + 
	        		"      j.0:targetEntity ?targetEntity ; " +
	        		"      j.0:score ?score ; " +
	        		"	   j.0:classLabel ?classLabel ; " +
	        		"	   j.0:matcherService ?matcherName . " +
	        		" } GROUP BY ?matcherName ";		
		
		return QueryFactory.create(queryString);		
	}//end csvQuery()	
	
	/**
	 * @return - result of a query that is used to count the number of TP/ FP
	 */
	private com.hp.hpl.jena.query.Query countTpFp(String matcherName) {
		 String queryString =  getNSPrefixes() +	
	        		" SELECT (COUNT(*) as ?count)" +
	        		" WHERE { " +
	        		"   ?s j.0:sourceEntity ?sourceEntity ; " + 
	        		"      j.0:targetEntity ?targetEntity ; " +
	        		"      j.0:score ?score ; " +
	        		"	   j.0:classLabel ?classLabel ; " +
	        		"	   j.0:matcherService ?matcherName ; " +
	        		"	   j.0:fileName ?fileName . " +
	        		" FILTER ( ?matcherName = \""+ matcherName + "\" ) " +	       		
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end countTpFp()
}//end Class