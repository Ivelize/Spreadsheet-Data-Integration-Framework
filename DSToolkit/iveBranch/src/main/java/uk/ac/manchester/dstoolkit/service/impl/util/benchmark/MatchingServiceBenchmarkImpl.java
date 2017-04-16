package uk.ac.manchester.dstoolkit.service.impl.util.benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.training.TrainingServiceUtilImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.training.VOCAB;
import uk.ac.manchester.dstoolkit.service.util.benchmark.MatchingServiceBenchmark;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.PrefixMapping;

/********************************************************************************
 * Class responsible for calculating the performance of the matching techniques:
 * 	(1) The COMA++ framework and its aggregation methods
 *  (2) The Bayesian inference framework 
 * 
 * 
 * @author klitos
 */
@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class MatchingServiceBenchmarkImpl extends TrainingServiceUtilImpl implements MatchingServiceBenchmark  {

	private static Logger logger = Logger.getLogger(MatchingServiceBenchmarkImpl.class);
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	private TDBStoreServiceImpl tdbStore = null;
	private PrefixMapping pmap = null;
	
	//Hold the location of the alignment files
	private Map<String, String> alignMap;
	
	private BenchmarkType bType = null;
	
	//Hold the URIs of the graphs in TDB
	public static String alignGraph;
	
	//For the syntactic evidence only either using COMA or Bayes
	public static String tpGraphSynURI;
	public static String fpGraphSynURI;
	
	//For assimilation of syn & sem evidence using Bayes
	public static String tpGraphSynSemURI;
	public static String fpGraphSynSemURI;

	/** Constructor - provide all the arguments **/
	public MatchingServiceBenchmarkImpl(BenchmarkType bType, TDBStoreServiceImpl tdb, String align_graph,
										String tp_g_syn_uri, String fp_g_syn_uri,
										String tp_g_syn_sem_uri, String fp_g_syn_sem_uri) {
		this.bType = bType;
		this.tdbStore = tdb;		
		this.alignGraph = align_graph;
		
		//only for syntactic evidence, either aggregate using COMA or Bayes
		this.tpGraphSynURI = tp_g_syn_uri;
		this.fpGraphSynURI = fp_g_syn_uri;	
		
		//separate graphs for the assimilation of syntactic and semantic evidence using Bayes
		this.tpGraphSynSemURI = tp_g_syn_sem_uri;
		this.fpGraphSynSemURI = fp_g_syn_sem_uri;
			
		/*Prepare a prefix map to be used to return Qnames of the properties*/
		if (pmap == null) {
			pmap = this.createPrefixMap();
		}
	}//end constructor
	
	/** Constructor - read the arguments as above from a filePath specified **/
	public MatchingServiceBenchmarkImpl(BenchmarkType bType, TDBStoreServiceImpl tdb, String filePath) {
		this.bType = bType;
		this.tdbStore = tdb;		

		this.alignMap = this.loadConfigAlignments(filePath);	
			
		/*Prepare a prefix map to be used to return Qnames of the properties*/
		if (pmap == null) {
			pmap = this.createPrefixMap();
		}
	}//end constructor
	
	/***
	 * Reset all the graphs associated with the benchmark including the one that holds the Alignment
	 */
	public void resetAll() {
		tdbStore.removeNamedModelTrans(alignGraph);
		
		//This is for the Syntactic Case: COMA / Bayesian assimilation of syntactic evidence only
		if (bType.equals(BenchmarkType.COMA_APPROACH) || bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY) ) {
			tdbStore.removeNamedModelTrans(tpGraphSynURI);
			tdbStore.removeNamedModelTrans(fpGraphSynURI);	
		} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
			tdbStore.removeNamedModelTrans(tpGraphSynSemURI);
			tdbStore.removeNamedModelTrans(fpGraphSynSemURI);	
		}
	}//end resetAll()
	
	/***
	 * Reset the classification of matches to TP/FP. Different Named Graphs are used according to the 
	 * type of benchmark.
	 */
	public void resetClassifications() {
		//This is for the Syntactic Case: COMA / Bayesian assimilation of syntactic evidence only
		if (bType.equals(BenchmarkType.COMA_APPROACH) || bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY) ) {
			tdbStore.removeNamedModelTrans(tpGraphSynURI);
			tdbStore.removeNamedModelTrans(fpGraphSynURI);	
		} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
			tdbStore.removeNamedModelTrans(tpGraphSynSemURI);
			tdbStore.removeNamedModelTrans(fpGraphSynSemURI);	
		}
	}//end resetClassifications()
	
	/***
	 * Add the map that holds the locations of the alignment files, from the benchmark
	 * @param alignMap
	 */
	public void addAlignments(Map<String, String> alignMap) {
		this.alignMap = alignMap;
	}
	
	/***
	 * Experiment1: Match using the syntactic matchers and aggregate them with COMA++. This method is 
	 * responsible for calculating the performance of the approach in terms of Precision and Recall
	 * using some Ground Truth. This method calculates top k %.
	 * 
	 * Note: I have noticed that the ranking of matches might be biased to the order this problem has been
	 * 		 sorted by adding DESC order to the SPARQL query. In cases of a tie score the TP are ranked first
	 * 
	 * @param matches
	 * @param percentage - specify % of the sample to calculate TP/FP/FN each time
	 */
	public void runTopKPercMatchesAggregationExperiment(List<Matching> matches, int percentage) {
		
		/*Only one alignment file is used, however the setup allows more than one*/
		String alignmentFile = alignMap.get("alignFile");
		logger.debug("alignmentFile: " + alignmentFile);
		
		//Load the alignment in TDB store, if not exists
		tdbStore.loadDataToModelFromRDFDump(alignGraph, alignmentFile);
		
		//Use method to classify the matches to TP and FP and make them persistent
		this.classifyMatches(matches);

		//Call method to calculate the Quality Measures
		this.calcMatchQualityMeasuresForTopKperc(percentage, matches);
				
		//Force the garbage collector
		//System.gc();		
	}//end runTopKPercMatchesAggregationExperiment()

	/***
	 * Experiment1: Match using the syntactic matchers and aggregate them with COMA++. This method is 
	 * responsible for calculating the performance of the approach in terms of Precision and Recall
	 * using some Ground Truth. This method calculates top k instead of the top k %.
	 * 
	 * @param matches
	 * @param interval - this is the interval of the x-axis
	 */
	public void runTopKMatchesAggregationExperiment(List<Matching> matches, int interval, Set<BooleanVariables> evidenceToAssimilateList) {
		logger.debug("in runTopKMatchesAggregationExperiment()");		
		/*Only one alignment file is used, however the setup allows more than one*/
		String alignmentFile = alignMap.get("alignFile");
		logger.debug("alignmentFile: " + alignmentFile);
		
		//Load the alignment in TDB store, if not exists
		tdbStore.loadDataToModelFromRDFDump(alignGraph, alignmentFile);
		
		//Use method to classify the matches to their actual classes 'T'rue or 'F'alse and make them persistent.
		this.classifyMatches(matches);
		
		//Then call method to calculate the Quality Measures for top k.
		this.calcMatchQualityMeasuresForTopK(interval, matches, evidenceToAssimilateList);
				
	}//end runTopKMatchesAggregationExperiment()
	
	/***
	 * Plot2: In this experiment we are looking to plot difference (d) 
	 * 
	 *   (1) classify the matches according to their presence in GT
	 *   (2) add the positions for each match in the triple store
	 */
	public void runDifferenceDExperiment(List<Matching> matches) {
		logger.debug("in runDifferenceDExperiment()");
		/*Only one alignment file is used, however the setup allows more than one*/
		String alignmentFile = alignMap.get("alignFile");
		logger.debug("alignmentFile: " + alignmentFile);
		
		//Load the alignment in TDB store, if not exists
		tdbStore.loadDataToModelFromRDFDump(alignGraph, alignmentFile);
		
		//Use method to classify the matches to their actual classes 'T'rue or 'F'alse and make them persistent.
		this.classifyMatches(matches);
		
		//After the matchers have been classified, run this method to find their position
		this.findPositionOfPersistentMatches();		
	}//end runDifferenceDExperiment()
	
	/***
	 * This method is responsible for creating the data file that is needed to plot the difference D
	 * for each combination of semantic evidence.
	 * 
	 * @param matches
	 * @return fileName - the file name of the file created
	 */
	public String generateDataForDiffDExperiment(List<Matching> matches, Set<BooleanVariables> evidenceToAssimilateList) {
		
		//Open up a file
		File loc = null;		
		BufferedWriter out = null;
		DecimalFormat df = null;
		
		String fileName = "";
		
		try {
			//Create file and write header
			df = new DecimalFormat("#.###");
			
			//Create the diff_d.csv file
			loc = this.createFile(graphvizDotGeneratorService.returnLocation(), "diffd");
			out = new BufferedWriter(new FileWriter(loc, true));
			
			if (out != null) {
				//save the file name and return it
				fileName = loc.getName();
			}
			
			//Output the list of Semantic Evidence available
			if (evidenceToAssimilateList != null) {
				String evidencesString = evidenceToAssimilateList.toString().replace(",", ".");
				out.append("\nEvidences: ").append(evidencesString).append("\n").append("\n");
			}
			
			//Print the header
			//diff_position : is the difference in previous position and new position.
			//diff_dob : is the difference in the previous degree of belief and new degree of belief.
			out.append("No, ").append("syn_position, syn_score, sem_position, sem_score, diff_pos, diff_dob, class_label").append("\n");
					
			//Open a new READ transaction		
			Dataset dataset = tdbStore.getDataset();
			dataset.begin(ReadWrite.READ);		
			try {	
				//Loop through the matches and find their positions in the Named Graphs of just syntactic and then in
				//the named graphs of both Syntactic and Semantic Evidence
				for (Matching matching : matches) {
					if (matching instanceof OneToOneMatching) {
						OneToOneMatching oneToOne = (OneToOneMatching) matching;
				
						//Get the Constructs from the Matching object			
						CanonicalModelConstruct construct1 = oneToOne.getConstruct1();
						CanonicalModelConstruct construct2 = oneToOne.getConstruct2();
					
						if ((construct1 != null) && (construct2 != null)) {
							//Prepare entity1 URI string to search the named Graphs
							String entity1 = null;				
							if (construct1 instanceof SuperAbstract) {
								entity1 = "http://" + construct1.getSchema().getDataSource().getName() + "#" + construct1.getName();
							} else if (construct1 instanceof SuperLexical) {
								//find the parent of this super abstract
								SuperAbstract parentSuperAbstract = ((SuperLexical) construct1).getFirstAncestorSuperAbstract();
								entity1 = "http://" + construct1.getSchema().getDataSource().getName() + "#" + parentSuperAbstract.getName() + "." + construct1.getName();					
							}//end if
						
							//Prepare entity2 string to search for in the alignment, in the form Parent.property
							String entity2 = null;				
							if (construct2 instanceof SuperAbstract) {
								entity2 = "http://" + construct2.getSchema().getDataSource().getName() + "#" + construct2.getName();
							} else if (construct2 instanceof SuperLexical) {
								//find the parent of this super abstract
								SuperAbstract parentSuperAbstract = ((SuperLexical) construct2).getFirstAncestorSuperAbstract();
								entity2 = "http://" + construct2.getSchema().getDataSource().getName() + "#" + parentSuperAbstract.getName() + "." + construct2.getName();
							}//end if
						
							logger.debug("entity1: " + entity1);
							logger.debug("entity2: " + entity2);
						
							//Ask query over the Named Graphs that have assimilate just syntactic evidence
							Query query = this.findPosition(entity1, entity2, BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY);
							QueryExecution qexec = QueryExecutionFactory.create(query, dataset);		
							ResultSet rs = qexec.execSelect();
						
							int synPosition = -1;
							double synScore = -0.0;
							String synLabel = "N/A";
							
							try {
								for ( ; rs.hasNext() ; ) {
									QuerySolution soln = rs.nextSolution() ;
									synPosition = soln.getLiteral("position").getInt();
									synScore = soln.getLiteral("score").getDouble();
									synLabel = soln.getLiteral("classLabel").getString();									
								}//end for
							} finally { qexec.close() ; }
						
							//Ask query over the Named Graphs that have assimilate both syntactic and semantic evidence
							int semPosition = -1;
							double semScore = -0.0;
							String semLabel = "N/A";
							
							query = this.findPosition(entity1, entity2, BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM);	
							qexec = QueryExecutionFactory.create(query, dataset);
							rs = qexec.execSelect();
							
							try {
								for ( ; rs.hasNext() ; ) {
									QuerySolution soln = rs.nextSolution() ;
									semPosition = soln.getLiteral("position").getInt();
									semScore = soln.getLiteral("score").getDouble();
									semLabel = soln.getLiteral("classLabel").getString();	
								}//end for
							} finally { qexec.close() ; }
						
							//Write into an actual file
							out.append(""+ matches.indexOf(matching)).append(", ");
							out.append(""+ synPosition).append(", ");
							out.append(df.format(synScore)).append(", ");
							
							//Label should be the same
							//out.append(synLabel).append(", ");
							
							out.append(""+ semPosition).append(", ");
							out.append(df.format(semScore)).append(", ");
							
							int diff_pos = semPosition - synPosition;
							out.append(""+ diff_pos).append(", ");
							double diff_dob = semScore - synScore;
							out.append(""+ diff_dob).append(", ");
							out.append(semLabel).append("\n");					    
						}//end if				
					}//end if				
				}//end for			
			} finally {
				dataset.end();
			}
			
			//At the end close the file
			if (out != null) {	out.close(); }
		} catch (IOException e) {
	       	logger.error("Error - I/O error while writing csv diff file.");
		}//end 
		
		return fileName;		
	}//end plotDiffereneceDExperiment()
	
	/***
	 * GNUPLOT: Output GNUplot code to generate the difference (d) plot of the previous and current position
	 * 
	 * @param fileName - the same fileName as the .csv file used to generate the data for this plot
	 * @param evidencesToAccumulate - get a list of the evidences accumulated
	 */
	public void plotDiffDexperiment(String fileName, Set<BooleanVariables> evidencesToAccumulate, String termType) {
		//Open up a file
		File loc = null;		
		BufferedWriter out = null;
		
		try {
			String filePath = graphvizDotGeneratorService.returnLocation();
			String newFileName = fileName.replace(".csv", ".plt");
			String noSuffixFileName = fileName.replace(".csv", "_pos");
			
			//Create the file name for the GNUplot code
			loc = new File(filePath + "/" + newFileName);
			out = new BufferedWriter(new FileWriter(loc, true));
						
			out.append("clear").append("\n");
			out.append("reset").append("\n");
			
			if (termType.equals("png")) {
				out.append("set term png enhanced size 800,600").append("\n");
				out.append("set output \"").append(noSuffixFileName).append(".png\"").append("\n");
			} else if (termType.equals("eps")) {
				out.append("set terminal postscript eps enhanced").append("\n");
				out.append("set output \"").append(noSuffixFileName).append(".eps\"").append("\n");
			}
			
			out.append("dx=5.").append("\n");
			out.append("n=4").append("\n");
			out.append("set key out vert right center box").append("\n");
			out.append("set style fill pattern 0 border").append("\n");
			out.append("set datafile separator \",\"").append("\n");
			
			//out.append("set multiplot").append("\n");
			
			out.append("set grid").append("\n");
			out.append("set xrange [0:1400]").append("\n");			
			out.append("set yrange [0:1400]").append("\n");

			out.append("set xtics 100").append("\n");			
			out.append("set ytics 100").append("\n");
			
			out.append("set title \"Difference in position for: ").append(evidencesToAccumulate.toString()).append("\"").append("\n");
			
			out.append("set xlabel \"purely syntactic position\"").append("\n");
			out.append("set ylabel \"semantically informed position\"").append("\n").append("\n");
						
			char backslash = '\\';
			
			out.append("plot \"").append(fileName).append("\" using 2:(stringcolumn(8) eq \"T\"? $4:1/0) title \" TP\" lc rgb \"blue\"," + backslash);
			out.append("\n");
			out.append("\t").append("\"\" using 2:(stringcolumn(8) eq \"F\"? $4:1/0) title \" FP\" lc rgb \"red\"").append(", x with lines lt 1 notitle").append("\n");
			
			//out.append("plot x with lines lt 1 notitle").append("\n");
			//out.append("unset multiplot").append("\n").append("\n");
			
			out.append("reset");
					
			//At the end close the file
			if (out != null) { out.close(); }
		} catch (IOException e) {
			logger.error("Error - I/O error while writing png diff Position file.");
		}//end catch 
	}//end plotDiffDexperiment()
	
	/***
	 * GNUPLOT: Output GNUplot code to generate the difference (d) plot of the previous and current Degree of Belief (DoB)
	 * 
	 * @param fileName - the same fileName as the .csv file used to generate the data for this plot
	 * @param evidencesToAccumulate - get a list of the evidences accumulated
	 */
	public void plotDiffDegreeOfBeliefexperiment(String fileName, Set<BooleanVariables> evidencesToAccumulate, String termType) {
		//Open up a file
		File loc = null;		
		BufferedWriter out = null;
		
		try {
			String filePath = graphvizDotGeneratorService.returnLocation();
			String newFileName = fileName.replace(".csv", "_dob.plt");
			String noSuffixFileName = fileName.replace(".csv", "_dob");
			
			//Create the file name for the GNUplot code
			loc = new File(filePath + "/" + newFileName);
			out = new BufferedWriter(new FileWriter(loc, true));
						
			out.append("clear").append("\n");
			out.append("reset").append("\n");
			
			if (termType.equals("png")) {
				out.append("set term png enhanced size 800,600").append("\n");
				out.append("set output \"").append(noSuffixFileName).append(".png\"").append("\n");
			} else if (termType.equals("eps")) {
				out.append("set terminal postscript eps enhanced").append("\n");
				out.append("set output \"").append(noSuffixFileName).append(".eps\"").append("\n");
			}
			
			out.append("dx=5.").append("\n");
			out.append("n=4").append("\n");
			out.append("set key out vert right center box").append("\n");
			out.append("set style fill pattern 0 border").append("\n");
			out.append("set datafile separator \",\"").append("\n");
			
			//out.append("set multiplot").append("\n");
			
			out.append("set grid").append("\n");
			out.append("set xrange [0:1]").append("\n");			
			out.append("set yrange [0:1]").append("\n");

			out.append("set xtics 0.1").append("\n");			
			out.append("set ytics 0.1").append("\n");
			
			out.append("set title \"Difference in DoB for: ").append(evidencesToAccumulate.toString()).append("\"").append("\n");
			
			out.append("set xlabel \"dob of syntactic\"").append("\n");
			out.append("set ylabel \"dob of the semantically informed\"").append("\n").append("\n");
						
			char backslash = '\\';
			
			out.append("plot \"").append(fileName).append("\" using 3:(stringcolumn(8) eq \"T\"? $5:1/0) title \" TP\" lc rgb \"blue\"," + backslash);
			out.append("\n");
			out.append("\t").append("\"\" using 3:(stringcolumn(8) eq \"F\"? $5:1/0) title \" FP\" lc rgb \"red\"").append(", x with lines lt 1 notitle").append("\n");
	
			out.append("reset");
					
			//At the end close the file
			if (out != null) { out.close(); }
		} catch (IOException e) {
			logger.error("Error - I/O error while writing png diff Position file.");
		}//end catch 
	}//end plotDiffDofDOBexperiment()	
	
	/***
	 * This method will do the following:
	 *   - issue a SPARQL query to get the BNode ids of all the anonymous resources that store each match result
	 *   - insert an RDF statement on that ID that is the position of the each result
	 */
	private void findPositionOfPersistentMatches() {
		logger.error("in findPositionOfPersistentMatches()");		
		//Open a new WRITE transaction		
		long count = 0;	
		double currentScore = -0.0;
    	double prevScore = -0.0;
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.WRITE);		
		try {			
			Model mergedModel  =  null;			
			if (bType.equals(BenchmarkType.COMA_APPROACH) || bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY) ) {
				mergedModel = dataset.getNamedModel(tpGraphSynURI).add(dataset.getNamedModel(fpGraphSynURI));
			} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
				mergedModel = dataset.getNamedModel(tpGraphSynSemURI).add(dataset.getNamedModel(fpGraphSynSemURI));
			}
			
			//Retrieve all the match instances from the named graphs
			Query query = this.getAllPersistentMatches();
			QueryExecution qexec = QueryExecutionFactory.create(query, mergedModel);		
			ResultSet rs = qexec.execSelect();
			
		    try {		    	
				for ( ; rs.hasNext() ; ) {
					QuerySolution soln = rs.nextSolution() ;
					Resource bNode = soln.getResource("s");
										
					RDFNode r1 = mergedModel.getRDFNode(bNode.asNode());
					
					//Increase the position index only if score is not the same as previous score
					currentScore = soln.getLiteral("score").getDouble();					
					if (currentScore != prevScore) {
						count = count + 1;
					}//end if
					prevScore = currentScore;
					
					r1.asResource().addLiteral(VOCAB.position, count);
				}//end for
			} finally { qexec.close() ; }		
			
			dataset.commit();		
		} finally {
			dataset.end();
		}	
	}//end findPositionOfPersistentMatchers()
	
			
	/***
	 * // TODO : I need to think whether I need to write some code for random sampling
	 * 
	 * The random selection process of the Matching instances is happening using a uniform random function,
	 * so all the instances have equal chances of selecting. At any time if an individual is selected twice
	 * it gets eliminated and a new individual is selected from the space (the set of all derived matches). 
	 * 
	 * @param matches - a list of matches, the list can be ranked or not, it does not make any
	 *                  differences to the random selection of matches.
	 * @param percentage
	 */
	public void runTopKPercmatchesAggregationExperimentRandomSampling(List<Matching> matches, int percentage) {
		
		/*Only one alignment file is used, however the setup allows more than one*/
		String alignmentFile = alignMap.get("alignFile");
		logger.debug("alignmentFile: " + alignmentFile);
		
		//Load the alignment in TDB store, if not exists
		tdbStore.loadDataToModelFromRDFDump(alignGraph, alignmentFile);
		
		//Use method to classify the matches to TP and FP and make them persistent
		this.classifyMatches(matches);
		
		//Call method to calculate the Quality Measures
		//this.calcMatchQualityMeasuresRandom(percentage, matches);
		
	}//end runTopKmatchesAggregationExperimentRandomSampling()
	
	/***
	 * Step1: This method is responsible to classify the matches according to their
	 * 	       actual class.
	 * 
	 *  T: means that match is 'T'rue, present in the GT (the set of real matches).
	 *  F: means that match is 'F'alse, not present in the GT (the set of real matches).
	 * 
	 * Different NamedGraphs are used for each case
	 * 
	 * train_classify_tp_syn = x-ns://benchmark.data/classify/syn/tp
	 * train_classify_fp_syn = x-ns://benchmark.data/classify/syn/fp
	 *	
	 * train_classify_tp_syn_sem = x-ns://benchmark.data/classify/syn_sem/tp
	 * train_classify_fp_syn_sem = x-ns://benchmark.data/classify/syn_sem/fp
	 * 
	 */
	public void classifyMatches(List<Matching> matches) {
		logger.debug("in classifyMatches()");
		//Open a new WRITE transaction		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.WRITE);		
		try {			
			Model alignGraphModel 	 =  tdbStore.getModel(alignGraph);	
			Model truePositiveModel  =  null;
			Model falsePositiveModel =  null;
			
			if (bType.equals(BenchmarkType.COMA_APPROACH) || bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY) ) {
				truePositiveModel  =  tdbStore.getModel(tpGraphSynURI);
				falsePositiveModel =  tdbStore.getModel(fpGraphSynURI);
			} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
				truePositiveModel  =  tdbStore.getModel(tpGraphSynSemURI);
				falsePositiveModel =  tdbStore.getModel(fpGraphSynSemURI);
			}
						
			//Loop through the matches to classify them to the TP set and FP set
			for (Matching matching : matches) {
				if (matching instanceof OneToOneMatching) {
					OneToOneMatching oneToOne = (OneToOneMatching) matching;
					
					//Get the Constructs from the Matching object			
					CanonicalModelConstruct construct1 = oneToOne.getConstruct1();
					CanonicalModelConstruct construct2 = oneToOne.getConstruct2();
					
					if ((construct1 != null) && (construct2 != null)) {
						//Prepare entity1 string to search for in the alignment, in the form Parent.property
						String entity1 = null;				
						if (construct1 instanceof SuperAbstract) {
							entity1 = "http://" + construct1.getSchema().getDataSource().getName() + "#" + construct1.getName();
						} else if (construct1 instanceof SuperLexical) {
							//find the parent of this super abstract
							SuperAbstract parentSuperAbstract = ((SuperLexical) construct1).getFirstAncestorSuperAbstract();
							entity1 = "http://" + construct1.getSchema().getDataSource().getName() + "#" + parentSuperAbstract.getName() + "." + construct1.getName();					
						}//end if
						
						//Prepare entity2 string to search for in the alignment, in the form Parent.property
						String entity2 = null;				
						if (construct2 instanceof SuperAbstract) {
							entity2 = "http://" + construct2.getSchema().getDataSource().getName() + "#" + construct2.getName();
						} else if (construct2 instanceof SuperLexical) {
							//find the parent of this super abstract
							SuperAbstract parentSuperAbstract = ((SuperLexical) construct2).getFirstAncestorSuperAbstract();
							entity2 = "http://" + construct2.getSchema().getDataSource().getName() + "#" + parentSuperAbstract.getName() + "." + construct2.getName();
						}//end if
						
						logger.debug("entity1: " + entity1);
						logger.debug("entity2: " + entity2);
						
						//Check if pair of construct exist in the alignment file	
						Query query = alignmentASKQuery(entity1, entity2);
												
						QueryExecution qexec = QueryExecutionFactory.create(query, alignGraphModel);		
						boolean alignExists = qexec.execAsk();
						
						try {
							if (alignExists) {
								//classify match as TP, create a bNode for each match and add it to the Graph
								this.createMatch(truePositiveModel, entity1,
																	entity2, "T", oneToOne.getScore());
							} else {
								//classify match as FP, create a bNode for each match and add it to the Graph
								this.createMatch(falsePositiveModel, entity1,
																	 entity2, "F", oneToOne.getScore());		
							}
						} finally { qexec.close() ; }
						
					}//end if				
				}//end if			
			}//end for			
			
			dataset.commit();
		} finally {
			dataset.end();
		}	
	}//end classifyMatches()
	
	/***
	 * This method is responsible for calculating the number of: TP, FP, FN 
	 * and Precision/Recall for each sample size (%). The final results are
	 * then stored into a csv file
	 * 
	 * //TODO: I have noticed that instances of matches that have the same 
	 * score their order is random. Does this causes a problem? Yes it does
	 * because the sample it seems biased because of this. So in the first
	 * 10% I can encounter more TP in one method and more FP in another case
	 * and that means that for the same 10% I can get different results at
	 * every run depending on the ranking order of the matches that have the
	 * same simScore. As a solution to this problem I have implement a method
	 * that does random sampling. The solution that I have now is to sort them
	 * with DESC to T 
	 * 
	 */
	public void calcMatchQualityMeasuresForTopKperc(int percentage, List<Matching> matches) {
		
		logger.info("percentage: " + percentage);
				
		File loc = null;		
		BufferedWriter out = null;
		DecimalFormat df = null;
				
		try {				
			//Format decimal
			df = new DecimalFormat("#.###");
			
			//Create the top_k_performance.csv file
			loc = this.createFile(graphvizDotGeneratorService.returnLocation(), "perc");
			out = new BufferedWriter(new FileWriter(loc, true));
			
			//Output the type of this benchmark
			if ((bType != null) && (out != null)) {
				out.append("\nBenchmark Type: ").append(bType.toString()).append("\n").append("\n");
			}//end if
			
			//Write the header of file
			if (bType.equals(BenchmarkType.COMA_APPROACH)) {
				out.append("sample size (%), ").append("#TP, #FP, #FN, #TN, Precision (Coma), Precision % (Coma), Recall (Coma), F-Measure (Coma), TP Rate, FP Rate").append("\n");
			} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY)) {
				out.append("sample size (%), ").append("#TP, #FP, #FN, #TN, Precision (Bayes_Syn), Precision % (Bayes_Syn), Recall (Bayes_Syn), F-Measure (Bayes_Syn), TP Rate, FP Rate").append("\n");			
			} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
				out.append("sample size (%), ").append("#TP, #FP, #FN, #TN, Precision (Bayes_Syn_Sem), Precision % (Bayes_Syn_Sem), Recall (Bayes_Syn_Sem), F-Measure (Bayes_Syn_Sem), TP Rate, FP Rate").append("\n");			
			}				
			
			//Find the number of real matches ( FN = real_matches - TP )
			//int totalNoOfRealMatches = this.executeSelectQuery(alignGraph, this.countAlignmentPairs());
			//logger.info("totalNoOfRealMatches: " + totalNoOfRealMatches);
									
			//Size of all matches derived 
			int matchesDerived = matches.size();
			logger.info("totalNoMatchesDerived: " + matchesDerived);
			
			//For each sample size (%) find the number of TP, FP, FN
			for(int i=0; i<=100; i = i + percentage) {
				out.append(""+ i).append(", ");
				
				double samplePerc = Math.floor( matchesDerived * (i / 100.0));
				
				//Call query to retrieve sample size of matches that are ranked
				Map<String, Integer> resultMap = this.getCounts((int) samplePerc);
				
				//add TP/ FP
				int tpCount = resultMap.get("TP");
				out.append(""+ tpCount).append(", ");
				int fpCount = resultMap.get("FP");
				out.append(""+ fpCount).append(", ");
				//add FN/ TN
				int fnCount = resultMap.get("FN");
				out.append(""+ fnCount).append(", ");
				int tnCount = resultMap.get("TN");
				out.append(""+ tnCount).append(", ");
				
				//Calculate Precision
				double precision = tpCount / (double) (tpCount + fpCount);
				out.append(df.format(precision)).append(", ");

				//Calculate Precision as Percentage
				double precisionPerc = precision * 100.0;
				out.append(df.format(precisionPerc)).append(", ");
				
				//Calculate Recall
				double recall =  tpCount / (double) (tpCount + fnCount);
				out.append(df.format(recall)).append(", ");	
				
				//F-measure: is the harmonic mean of precision and recall
				double f_measure = (2 * precision * recall) / (double) (precision + recall); 
				out.append(df.format(f_measure)).append(", ");	
				
				//TP Rate
				double tp_rate = 100 * ( tpCount / (double) (tpCount + fnCount) ); 
				out.append(df.format(tp_rate)).append(", ");
				
				//FP Rate
				double fp_rate = 100 * ( fpCount / (double) (fpCount + tnCount) ); 
				out.append(df.format(fp_rate)).append("\n");	
			}//end for		
			
			//close the csv file
			if (out != null) {	out.close(); }				
		} catch (IOException e) {
	       	logger.error("Error - I/O error while writing csv performance file.");
		}		
	}//end calcMatchQualityMeasuresForTopKperc()
	
	
	/***
	 * This method is responsible for calculating the number of: TP/FP - FN/TN 
	 * and the to calculate Precision/Recall for each k interval. The final results
	 * are then stored into a .csv file.
	 * 
	 *  TP: matches in the interval (this is the set of derived matches so far) that are present in the GT. 
	 *  FP: matches in the interval (this is the set of derived matches so far) that are not present in the GT.
	 * 
	 *  FN: matches outside the interval (not derived) that are present in the GT.
	 *  TN: matches outside the interval (not derived) that are not present in the GT.
	 * 
	 * 
	 * This method will calculate all the above only with SPARQL queries. At the beginning the method
	 * classifies each match to its actual class as T or F. True if it is present in the GT and False if
	 * it is not present in the GT. It then ranks the matches according to the score and class label T. Then
	 * it select a sample from the matches according to an interval (k). The matches selected in the interval
	 * represent the set of derived matches whereas the matches outside the set represent the set of matches 
	 * that have been missed by the algorithm. For each interval we then calculate the number of TP/FP from the
	 * set of derived matches and the number of FN/TN from the set of missed matches. 
	 * 
	 * @param interval - top k interval instead of top k %
	 * @param matches - a set of matches
	 */
	public void calcMatchQualityMeasuresForTopK(int interval, List<Matching> matches, Set<BooleanVariables> evidenceToAssimilateList) {
		logger.info("in calcMatchQualityMeasuresForTopK()");
		File loc = null;		
		BufferedWriter out = null;
		DecimalFormat df = null;
				
		try {				
			//Format decimal
			df = new DecimalFormat("#.###");	
			
			//Create the top_k_performance.csv file
			loc = this.createFile(graphvizDotGeneratorService.returnLocation(), "topk");
			out = new BufferedWriter(new FileWriter(loc, true));
		
			//Output the type of this benchmark
			if ((bType != null) && (out != null)) {
				out.append("\nBenchmark Type: ").append(bType.toString()).append("\n").append("\n");
			}//end if
			
			//Output the list of Semantic Evidence available
			if (evidenceToAssimilateList != null) {
				String evidencesString = evidenceToAssimilateList.toString().replace(",", ".");
				out.append("\nEvidences: ").append(evidencesString).append("\n").append("\n");
			}
			
			//Write the HEADER of file
			if (bType.equals(BenchmarkType.COMA_APPROACH)) {
				out.append("top k, ").append("#TP, #FP, #FN, #TN, Precision (Coma), Precision % (Coma), Recall (Coma), F-Measure (Coma), TP Rate, FP Rate").append("\n");
			} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY)) {
				out.append("top k, ").append("#TP, #FP, #FN, #TN, Precision (Bayes_Syn), Precision % (Bayes_Syn), Recall (Bayes_Syn), F-Measure (Bayes_Syn), TP Rate, FP Rate").append("\n");			
			} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
				out.append("top k, ").append("#TP, #FP, #FN, #TN, Precision (Bayes_Syn_Sem), Precision % (Bayes_Syn_Sem), Recall (Bayes_Syn_Sem), F-Measure (Bayes_Syn_Sem), TP Rate, FP Rate").append("\n");			
			}//end if
			
			//Get how many times to run the query to collect the data
			double result = Math.floor(matches.size() / (double) interval);
		
			int step = 0;
			for (double i = 0; i < result; i++) {
				step = step + interval;
				logger.debug("step: " + step);
				
				out.append(""+ step).append(", ");
			
				//Call query to get the counts
				Map<String, Integer> resultMap = this.getCounts(step);	
				
				//add TP/ FP
				int tpCount = resultMap.get("TP");
				out.append(""+ tpCount).append(", ");
				int fpCount = resultMap.get("FP");
				out.append(""+ fpCount).append(", ");
				//add FN/ TN
				int fnCount = resultMap.get("FN");
				out.append(""+ fnCount).append(", ");
				int tnCount = resultMap.get("TN");
				out.append(""+ tnCount).append(", ");
				
				//Calculate Precision
				double precision = tpCount / (double) (tpCount + fpCount);
				out.append(df.format(precision)).append(", ");
				
				//Calculate Precision as Percentage
				double precisionPerc = precision * 100.0;
				out.append(df.format(precisionPerc)).append(", ");
				
				//Calculate Recall
				double recall =  tpCount / (double) (tpCount + fnCount);
				out.append(df.format(recall)).append(", ");	
				
				//F-measure: is the harmonic mean of precision and recall
				double f_measure = (2 * precision * recall) / (double) (precision + recall); 
				out.append(df.format(f_measure)).append(", ");	
				
				//TP Rate
				double tp_rate = 100 * ( tpCount / (double) (tpCount + fnCount) ); 
				out.append(df.format(tp_rate)).append(", ");
				
				//FP Rate
				double fp_rate = 100 * ( fpCount / (double) (fpCount + tnCount) ); 
				out.append(df.format(fp_rate)).append("\n");				
			}//end for	
			
			//Needs to do it one time more time
			if (step < matches.size()) {
				
				step = matches.size();
				
				out.append(""+ step).append(", ");
				
				//Call query to get the counts
				Map<String, Integer> resultMap = this.getCounts(step);	
				
				//add TP/ FP
				int tpCount = resultMap.get("TP");
				out.append(""+ tpCount).append(", ");
				int fpCount = resultMap.get("FP");
				out.append(""+ fpCount).append(", ");
				//add FN/ TN
				int fnCount = resultMap.get("FN");
				out.append(""+ fnCount).append(", ");
				int tnCount = resultMap.get("TN");
				out.append(""+ tnCount).append(", ");
				
				//Calculate Precision
				double precision = tpCount / (double) (tpCount + fpCount);
				out.append(df.format(precision)).append(", ");
				
				//Calculate Recall
				double recall =  tpCount / (double) (tpCount + fnCount);
				out.append(df.format(recall)).append(", ");	
				
				//F-measure: is the harmonic mean of precision and recall
				double f_measure = (2 * precision * recall) / (double) (precision + recall); 
				out.append(df.format(f_measure)).append(", ");	
				
				//TP Rate
				double tp_rate = 100 * ( tpCount / (double) (tpCount + fnCount) ); 
				out.append(df.format(tp_rate)).append(", ");
				
				//FP Rate
				double fp_rate = 100 * ( fpCount / (double) (fpCount + tnCount) ); 
				out.append(df.format(fp_rate)).append("\n");				
			}//end if		
		
			//close the csv file
			if (out != null) { out.close(); }				
		} catch (IOException e) {
			logger.error("Error - I/O error while writing csv performance file.");
		}//end catch		
	}//end calcMatchQualityMeasuresForTopK()
	
	
	/***
	 * This method is being used by the calcMatchQualityMeasuresForTopK() to find the number of 
	 * TP/FP and FN/FP. 
	 * 
	 * From the set of derived matches, meaning the ones in the interval, if a match is has a T label
	 * , meaning is present in the GT is considered as TP, if it has a label F is consider as FP. 
	 * From the matches that are outside the interval, meaning are not derived, if a match has a label T
	 * then is in the GT and we consider it as FN otherwise is a TN.
	 * 
	 * @param limit
	 * @return
	 */
	public Map<String, Integer> getCounts(int limit) {
		
		//Map that will hold the results 
		Map<String, Integer> resultMap = null;		
		
		int tpCount = 0; int fpCount = 0; 
		int fnCount = 0; int tnCount = 0;
		
		//Open a new READ transaction		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.READ);		
		try {			
			//Create the hashMap that will hold the results
			resultMap = new HashMap<String, Integer>();
			
			/*We search for matches that are classified as T/F for the matches in the interval to find TP/FP
			  We search for matches that are classified as T/F from the matches that are not in the interval to find FN/TN*/
			Literal true_literal = ResourceFactory.createTypedLiteral(new String("T"));
			Literal false_literal = ResourceFactory.createTypedLiteral(new String("F"));	
			
			//Create and call a SPARQL query to get the sample
			Query query = this.getSample(limit);
			QueryExecution qexec = QueryExecutionFactory.create(query, dataset);		
			ResultSet rs = qexec.execSelect();

			//Ask SPARQL query to find TP/FP from the interval (the set of derived matches)
		    try {
				for ( ; rs.hasNext() ; ) {
					QuerySolution soln = rs.nextSolution() ;
					Literal class_label = soln.getLiteral("classLabel") ;
			
					if (class_label.equals(true_literal)) {
						tpCount = tpCount + 1;
					} else if (class_label.equals(false_literal)) {
						fpCount = fpCount + 1;
					}					
				}//end for
			} finally { qexec.close() ; }
		     
			//Ask SPARQL query to find FN/TN from matches outside the interval (the set of missed matches)
		    query = this.getMatchesOutsideInterval(limit);	
		    qexec = QueryExecutionFactory.create(query, dataset);
		    rs = qexec.execSelect();
		   
		    try {
			
				for ( ; rs.hasNext() ; ) {
					QuerySolution soln = rs.nextSolution() ;
					Literal class_label = soln.getLiteral("classLabel") ;
			
					if (class_label.equals(true_literal)) {
						fnCount = fnCount + 1;
					} else if (class_label.equals(false_literal)) {
						tnCount = tnCount + 1;
					}					
				}//end for		    	
		    } finally { qexec.close() ; }		
		
		} finally {
			dataset.end();
		}
	
		//Add the results to the hashMap
		resultMap.put("TP", tpCount);
		resultMap.put("FP", fpCount);		
	
		resultMap.put("FN", fnCount);
		resultMap.put("TN", tnCount);	
		
		return resultMap;		
	}//end getCounts()	
	
	
	/***
	 * Method that will return a new Resource to be added in the TP/FP model
	 *
	 * Store match as a BNode
	 */
	public Resource createMatch(Model model, String sourceName, String targetName, String cLabel, double score) {
		
		Resource bNode = model.createResource();
		bNode.addLiteral( VOCAB.sourceEntity, sourceName );
		bNode.addLiteral( VOCAB.targetEntity, targetName );
		bNode.addLiteral( VOCAB.score, score );
		bNode.addLiteral( VOCAB.classLabel, cLabel );
		return bNode;
	}//end createMatch()
	
	
	/**
	 * @return - Open a transaction over the dataset and execute a select query
	 * to get the number of counts
	 */
	public int executeSelectQuery(String modelURI, com.hp.hpl.jena.query.Query query) {
		//Open a new READ transaction

		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.READ);	
		try {		
			Model model = tdbStore.getModel(modelURI);	

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
	}//end getCount()	
	
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
	 * @return - result of a query that is used to count the number alignment pairs
	 */
	private com.hp.hpl.jena.query.Query countAlignmentPairs() {
		 String queryString =  getNSPrefixes() +	
	        		" SELECT (COUNT(*) as ?count)" +
	        		" WHERE { " +
	        		"   ?s align:entity1 ?e1 ; " + 
	        		"	   align:entity2 ?e2 . " +		
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end countTpFp()
	

	/***
	 * Query that retrieves the list of ranked matches into samples using the limit keyword
	 * 
	 * Note: I am ranking the on the score in descending order with the TP being ranked 
	 * 		 also first in case of a tie. Also, order by the class label ensures that TP
	 * 		 appear first in cases of a tie.
	 */
	private com.hp.hpl.jena.query.Query getSample(int limit) {
		String tpGraph = null;
		String fpGraph = null;
		if (bType.equals(BenchmarkType.COMA_APPROACH) || bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY) ) {
			tpGraph = this.tpGraphSynURI.trim();
			fpGraph = this.fpGraphSynURI.trim();
		} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
			tpGraph = this.tpGraphSynSemURI.trim();
			fpGraph = this.fpGraphSynSemURI.trim();
		}
		
		 String queryString =  getNSPrefixes() +	
	        		" SELECT ?sourceEntity ?targetEntity ?score ?classLabel " +
				    " FROM <" + tpGraph + "> " +
				    " FROM <" + fpGraph + "> " + 
	        		" WHERE { " +
	        		"   ?s j.0:sourceEntity ?sourceEntity ; " + 
	        		"      j.0:targetEntity ?targetEntity ; " +
	        		"      j.0:score ?score ; " +
	        		"	   j.0:classLabel ?classLabel ; " +
	        		" } " +
	        		" ORDER BY DESC(?score) DESC(?classLabel) " +
	        		" LIMIT " + limit;		
		
		return QueryFactory.create(queryString);		
	}//end countTpFp()
	
	/***
	 * SPARQL query to find the position of a match from the named Graphs
	 * @param limit
	 * @return
	 */
	private com.hp.hpl.jena.query.Query findPosition(String e1, String e2, BenchmarkType namedGraphs) {
		String tpGraph = null;
		String fpGraph = null;
		if (namedGraphs.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY)) {
			tpGraph = this.tpGraphSynURI.trim();
			fpGraph = this.fpGraphSynURI.trim();
		} else if (namedGraphs.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
			tpGraph = this.tpGraphSynSemURI.trim();
			fpGraph = this.fpGraphSynSemURI.trim();
		}
		
		String queryString =  getNSPrefixes() +	
	        		" SELECT ?score ?classLabel ?position " +
				    " FROM <" + tpGraph + "> " +
				    " FROM <" + fpGraph + "> " + 
	        		" WHERE { " +
	        		"   ?s j.0:sourceEntity \"" + e1 + "\" ^^xsd:string ; " + 
	        		"      j.0:targetEntity \"" + e2 + "\" ^^xsd:string ; " + 
	        		"      j.0:score ?score ; " +
	        		"	   j.0:classLabel ?classLabel ; " +	        		
	        		"	   j.0:position ?position ; " +
	        		" } ";
		
		logger.info("" + queryString);
		
		return QueryFactory.create(queryString);		
	}//end countTpFp()
	
	/***
	 * @return - SPARQL query that returns all the persistent matches. The results of this query come in an order
	 * that we use them to add a position of each BNode. The bNode unique id is extracted from the query and then 
	 * used to add the integer predicate position. This information is used for the creation the difference D plots.
	 */
	private com.hp.hpl.jena.query.Query getAllPersistentMatches() {
		 String queryString =  getNSPrefixes() +	
	        		" SELECT ?s ?sourceEntity ?targetEntity ?score ?classLabel " +
	        		" WHERE { " +
	        		"   ?s j.0:sourceEntity ?sourceEntity ; " + 
	        		"      j.0:targetEntity ?targetEntity ; " +
	        		"      j.0:score ?score ; " +
	        		"	   j.0:classLabel ?classLabel ; " +
	        		" } " +
	        		" ORDER BY DESC(?score) DESC(?classLabel) ";
		
		return QueryFactory.create(queryString);		
	}//end countTpFp()
	
	/***
	 * Query to retrieve the matches outside the interval using OFFSET
	 */
	private com.hp.hpl.jena.query.Query getMatchesOutsideInterval(int limit) {
		 
		String tpGraph = null;
		String fpGraph = null;
		if (bType.equals(BenchmarkType.COMA_APPROACH) || bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY) ) {
			tpGraph = this.tpGraphSynURI.trim();
			fpGraph = this.fpGraphSynURI.trim();
		} else if (bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
			tpGraph = this.tpGraphSynSemURI.trim();
			fpGraph = this.fpGraphSynSemURI.trim();
		}
				
		String queryString =  getNSPrefixes() +	
	        		" SELECT ?sourceEntity ?targetEntity ?score ?classLabel " +
				    " FROM <" + tpGraph + "> " +
				    " FROM <" + fpGraph + "> " + 
	        		" WHERE { " +
	        		"   ?s j.0:sourceEntity ?sourceEntity ; " + 
	        		"      j.0:targetEntity ?targetEntity ; " +
	        		"      j.0:score ?score ; " +
	        		"	   j.0:classLabel ?classLabel ; " +
	        		" } " +
	        		" ORDER BY DESC(?score) DESC(?classLabel) " +
	        		" OFFSET " + limit;		
		
		return QueryFactory.create(queryString);		
	}//end countTpFp()
	
	/**
	 * @param prefix - whether is top k OR top k % or difference d
	 * @return - a File to store the results the performance
	 */
	public File createFile(String filePath, String prefix) {
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss") ;
		
		if (prefix.equals("perc")) {
			return new File(filePath + "/top_k_perc_performance_" + dateFormat.format(date)+".csv") ;
		} else if (prefix.equals("diffd")) { 
			return new File(filePath + "/diff_d_" + dateFormat.format(date)+".csv") ;
	    } else {
			return new File(filePath + "/top_k_performance_" + dateFormat.format(date)+".csv") ;
		}
	}//createFile()
	
	/***
	 * Load alignments property file ./src/test/resources/training/benchmark/exp1.benchmark
	 * @param filePath
	 */
	protected Map<String, String> loadConfigAlignments(String filePath) {
		logger.debug("Loading alignments..." + filePath);
		Map<String, String> alignMap = null;		
		try {
			alignMap = new HashMap<String, String>();
			InputStream propertyStream = new FileInputStream(filePath);
			Properties alignProps = new java.util.Properties();
			alignProps.load(propertyStream);			
			alignGraph = alignProps.getProperty("ground_truth");

			//For only syntactic evidence either by aggregating with COMA or Bayesian use the following graphs
			tpGraphSynURI = alignProps.getProperty("train_classify_tp_syn");  
			fpGraphSynURI = alignProps.getProperty("train_classify_fp_syn");
			
			//For when syntactic & semantic evidence is assimilated using Bayes
			tpGraphSynSemURI = alignProps.getProperty("train_classify_tp_syn_sem");  
			fpGraphSynSemURI = alignProps.getProperty("train_classify_fp_syn_sem");
			
			//Load alignment file location
			for(String key : alignProps.stringPropertyNames()) {
				if (key.contains("alignFile")) {
				  String value = alignProps.getProperty(key);
				  alignMap.put(key, value);
				}//end if
			}//end for 			 
		 } catch (FileNotFoundException exc) {
		 	logger.error("train.properties file not found: " + exc);
		 } catch (IOException ioexc) {
		  	logger.error("train.properties file not found: ", ioexc);
		 }//end catch	
		
		return alignMap;
	}//end loadConfigAlignments()	
}//end class