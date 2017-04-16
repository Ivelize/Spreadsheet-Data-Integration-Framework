/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ErrorMeasuresTypes;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;

/**
 * @author chedeler
 * @authos klitos
 * 
 * This class seems responsible for producing the final matches. 
 * //TODO: Need to have here a method that will produce the final alignment in an RDF file like
 * the one suggested by OAIE and save them in a file. This is actually an RDF model therefore I
 * will need to create a new database for it to hold the new SDBStore that will holds the model.
 * then use the write out method to output the model in RDF/XML
 * 
 */
@Scope("prototype")
@Service
public class MatchingProducerServiceImpl implements MatchingProducerService {

	private static Logger logger = Logger.getLogger(MatchingProducerServiceImpl.class);

	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	

	/***
	 * This method is responsible for creating a list of Matching objects for specific cells.
	 * 
	 * @return List<Matching> 
	 */
	public List<Matching> produceMatchesForSpecificCells(final List<CanonicalModelConstruct> sourceConstructs, final List<CanonicalModelConstruct> targetConstructs,
														 final float[][] simMatrix, final Map<ControlParameterType, ControlParameter> controlParameters,
														 Set<SemanticMatrixCellIndex> cellsSet, final MatcherService matcherService) {
		//Initialise the array to hold the Matching objects	
		List<Matching> matches 		= new ArrayList<Matching>();
        String matcherServiceName 	= null;
		String rankingType 			= null;
        
        if (matcherService == null) {
        	matcherServiceName = "COMA";
        } else {
        	matcherServiceName =  matcherService.getName();
        }
        
		//Control parameter for ranking
		if (controlParameters.containsKey(ControlParameterType.MATCH_RANKING)) {
			rankingType = controlParameters.get(ControlParameterType.MATCH_RANKING).getValue();
		}
		
		try {
			if ((sourceConstructs != null) && (targetConstructs != null)) {	
				//Loop through the IndexSet of saved indexes and create a Matching object representation
				for (SemanticMatrixCellIndex cell : cellsSet) {
					int i = cell.rowIndex;
					int j = cell.colIndex;
															
					if (simMatrix != null) {	
						/*Create OneToOneMatching, for each match*/
						OneToOneMatching matching = new OneToOneMatching(sourceConstructs.get(i), targetConstructs.get(j),
																		simMatrix[i][j], matcherServiceName);
					
						if (matching != null) {
							matches.add(matching);
						}//end if
					}//end if
					
				}//end for				
				
				//Decide how to rank the matches
				if (rankingType != null) {
					if (rankingType.equals(ControlParameterType.RANKING_ASCE.toString())) {
						Collections.sort(matches, new MatchingComparator());
					} else if (rankingType.equals(ControlParameterType.RANKING_DESC.toString())) {
						Collections.sort(matches, Collections.reverseOrder(new MatchingComparator()));
					}	
				}//end if
		
			}//end if
		} catch (Exception exe) {
			logger.error("ERROR MatchingProducerServiceImpl - " + exe.getMessage());
		}
		
		return matches;		
	}//end produceMatchesForSpecificCells()	
	
	/***
	 * This is the new method that produces Matching objects for each alignment, we assume that a final alignment is produced by following 
	 * the procedure by COMA++ which aggregates first the results of different matchers and then a selection strategy is applied
	 * over the similarity scores. The produceFinalMatches() method is just responsible for creating Matching objects for each match.
	 * 
	 * @return List<Matching> - a List of matching objects 
	 */
	public List<Matching> produceFinalMatches(final float[][] simMatrix, final List<CanonicalModelConstruct> sourceConstructs,
			final List<CanonicalModelConstruct> targetConstructs, final Map<ControlParameterType, ControlParameter> controlParameters,
			final MatcherService matcherService) {
					
	    List<Matching> matches = new ArrayList<Matching>();
		String rankingType = null;
        String matcherServiceName = null;
        
        if (matcherService == null) {
        	matcherServiceName = "COMA";
        } else {
        	matcherServiceName =  matcherService.getName();
        }
		
		//Control parameter for ranking
		if (controlParameters.containsKey(ControlParameterType.MATCH_RANKING)) {
			rankingType = controlParameters.get(ControlParameterType.MATCH_RANKING).getValue();
		}
		
		/**
		 * All the matches are selected, even those with sim score 0, the purpose of this class is to create the Matching
		 * object after the process of aggregation and selection.
		 */
		for (int i = 0; i < simMatrix.length; i++) {
			for (int j = 0; j < simMatrix[i].length; j++) {
				/*Create OneToOneMatching, for each match*/
				OneToOneMatching matching = new OneToOneMatching(sourceConstructs.get(i), targetConstructs.get(j), simMatrix[i][j], matcherServiceName);
					
				if (matching != null) {
					matches.add(matching);
				}//end if
			}//end for
		}//end for
		
		//Decide how to rank the matches
		if (rankingType != null) {
			if (rankingType.equals(ControlParameterType.RANKING_ASCE.toString())) {
				Collections.sort(matches, new MatchingComparator());
			} else if (rankingType.equals(ControlParameterType.RANKING_DESC.toString())) {
				Collections.sort(matches, Collections.reverseOrder(new MatchingComparator()));
			}	
		}//end if

		return matches;
	}//end produceMatches()

	/***
	 * 	
	 * @param simMatrix - a similarity matrix that holds the similarities or degrees of belief
	 * @param errMatrix - a matrix that holds the individual errors calculated given a numeric measure such as absError or squaredError
	 * @return
	 */
	public List<Matching> produceFinalMatchesWithError(final float[][] simMatrix, final float[][] errMatrix, final List<CanonicalModelConstruct> sourceConstructs,
			final List<CanonicalModelConstruct> targetConstructs, final Map<ControlParameterType, ControlParameter> controlParameters,
			final MatcherService matcherService) {
		
	    List<Matching> matches = new ArrayList<Matching>();
		String rankingType = null;
		ErrorMeasuresTypes errorType = null;
        String matcherServiceName = null;
        
        if (matcherService == null) {
        	matcherServiceName = "COMA";
        } else {
        	matcherServiceName =  matcherService.getName();
        }
		
		//Control parameter for ranking
		if (controlParameters.containsKey(ControlParameterType.MATCH_RANKING)) {
			rankingType = controlParameters.get(ControlParameterType.MATCH_RANKING).getValue();
		}
				
		if (controlParameters.containsKey(ControlParameterType.ERROR_MEASURE_TYPE)) {
			errorType = controlParameters.get(ControlParameterType.ERROR_MEASURE_TYPE).getErrorMeasureType();
		}		
	
		/**
		 * All the matches are selected, even those with sim score 0, the purpose of this class is to create the Matching
		 * object after the process of aggregation and selection.
		 */
		for (int i = 0; i < simMatrix.length; i++) {
			for (int j = 0; j < simMatrix[i].length; j++) {
				/*Create OneToOneMatching, for each match*/
				OneToOneMatching matching = new OneToOneMatching(sourceConstructs.get(i), targetConstructs.get(j), simMatrix[i][j], matcherServiceName);
					
				if (errorType != null) {
					if (errorType.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
						matching.setSquaredError(errMatrix[i][j]);
					} else if (errorType.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {						
						matching.setAbsError(errMatrix[i][j]);
					}
				}//end if				
				
				if (matching != null) {
					matches.add(matching);
				}//end if
			}//end for
		}//end for
		
		//Decide how to rank the matches
		if (rankingType != null) {
			if (rankingType.equals(ControlParameterType.RANKING_ASCE.toString())) {
				Collections.sort(matches, new MatchingComparator());
			} else if (rankingType.equals(ControlParameterType.RANKING_DESC.toString())) {
				Collections.sort(matches, Collections.reverseOrder(new MatchingComparator()));
			}	
		}//end if

		return matches;		
	}//end produceFinalMatchesWithError()
	
	
	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherProducerService#produceMatches(float[][], java.util.List, java.util.List, java.util.Map, uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService)
	 *
	 *
	 * This method will be called after aggregation, by taking as arguments the [][] two dimensional aggregated matrix, and the list of control
	 * parameters at this stage to select the final matches using the threshold type.  
	 *
	 */
	public List<Matching> produceMatches(final float[][] simMatrix, final List<CanonicalModelConstruct> constructs1,
			final List<CanonicalModelConstruct> constructs2, final Map<ControlParameterType, ControlParameter> controlParameters,
			final MatcherService matcherService) {
		logger.info("in produceMatches" + ", thread: " + Thread.currentThread());
		logger.info("matcherService.getName(): " + matcherService.getName() + ", thread: " + Thread.currentThread());
		logger.debug("controlParameters: " + controlParameters);
		logger.info("simMatrix: " + simMatrix);
		final List<Matching> matches = new ArrayList<Matching>();

		final double matchScoreThreshold = this.getMatchScoreThresholdFromControlParameters(controlParameters);
		logger.info("matchScoreThreshold: " + matchScoreThreshold + ", thread: " + Thread.currentThread());

		for (int i = 0; i < simMatrix.length; i++) {
			logger.debug("i: " + i);
			for (int j = 0; j < simMatrix[i].length; j++) {
				logger.debug("j: " + j);
				logger.debug("simMatrix[i][j]: " + simMatrix[i][j]);
				if (simMatrix[i][j] >= matchScoreThreshold) {
					logger.info("constructs1.get(i): " + constructs1.get(i) + ", thread: " + Thread.currentThread());
					logger.info("constructs2.get(j): " + constructs2.get(j) + ", thread: " + Thread.currentThread());
					logger.info("constructs1.get(i).getName(): " + constructs1.get(i).getName() + ", thread: " + Thread.currentThread());
					logger.info("constructs2.get(j).getName(): " + constructs2.get(j).getName() + ", thread: " + Thread.currentThread());
					logger.info("simMatrix[i][j]: " + simMatrix[i][j] + ", thread: " + Thread.currentThread());
					logger.debug("matcherService.getName(): " + matcherService.getName());
					final Matching matching = this.produceSingleMatching(simMatrix[i][j], constructs1.get(i), constructs2.get(j), controlParameters,
							matcherService);
					if (matching != null)
						matches.add(matching);
				} else if (simMatrix[i][j] > 0.0 && simMatrix[i][j] < matchScoreThreshold) {
					logger.debug("below threshold");
					logger.debug("constructs1.get(i).getName(): " + constructs1.get(i).getName());
					logger.debug("constructs2.get(j).getName(): " + constructs2.get(j).getName());
				}
			}
		}
		logger.info("matches.size(): " + matches.size() + ", thread: " + Thread.currentThread());
		return matches;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherProducerService#produceSingleMatching(float, uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct, java.util.Map, uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService)
	 */
	public Matching produceSingleMatching(final float simScore, final CanonicalModelConstruct construct1, final CanonicalModelConstruct construct2,
			final Map<ControlParameterType, ControlParameter> controlParameters, final MatcherService matcherService) {
		logger.debug("in produceSingleMatching");
		logger.debug("simScore: " + simScore);
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		final double matchScoreThreshold = this.getMatchScoreThresholdFromControlParameters(controlParameters);
		if (simScore >= matchScoreThreshold) {
			logger.debug("matcherService.getName(): " + matcherService.getName());
			final OneToOneMatching matching = new OneToOneMatching(construct1, construct2, simScore, matcherService.getName());
			return matching;
		}
		return null;
	}//end produceSingleMatching()
		

	/**
	 * When the selection method is THRESHOLD.
	 * //TODO: Implement other methods for other selection methods.
	 * 
	 * This method looks the list of parameters for the threshold value and returns it.
	 * @param controlParameters
	 * @return
	 */
	protected double getMatchScoreThresholdFromControlParameters(final Map<ControlParameterType, ControlParameter> controlParameters) {
		double matchScoreThreshold = 0.0;
		if (controlParameters.containsKey(ControlParameterType.MATCH_SCORE_THRESHOLD)) {
			logger.debug("found MATCH_SCORE_THRESHOLD in controlParameters");
			matchScoreThreshold = new Double(controlParameters.get(ControlParameterType.MATCH_SCORE_THRESHOLD).getValue()).doubleValue();
		}
		logger.debug("matchScoreThreshold: " + matchScoreThreshold);
		return matchScoreThreshold;
	}
	
	
	
	/***
	 * This method will save the Matchings in a file based on the OAEI format in an RDF file.
	 * 
	 * controlParameters are not used yet
	 */
	public void writeMatchingsToFile(final float[][] simMatrix, final List<CanonicalModelConstruct> sourceConstructs,
			final List<CanonicalModelConstruct> targetConstructs, final Map<ControlParameterType, ControlParameter> controlParameters) {
		
		File loc = null;		
		BufferedWriter out = null;
		DecimalFormat df = null;
		
		try {			
			//Create the file
			loc = new File(graphvizDotGeneratorService.returnLocation()+"/matchings.rdf");
			out = new BufferedWriter(new FileWriter(loc, true));
			
			df = new DecimalFormat("#.####");
			
			//Build the header file of the output file
			out.append("<?xml version='1.0' encoding='utf-8'?>").append("\n").append("\n");			
			out.append("<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment'").append("\n");
			out.append("\t\t").append("xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'").append("\n");
			out.append("\t\t").append("xmlns:xsd='http://www.w3.org/2001/XMLSchema#'").append("\n");			
			out.append("<Alignment>").append("\n");
			
			//Configuration - at the moment leave them with ??
			out.append("  <xml>").append("??").append("</xml>").append("\n");
			out.append("  <level>").append("??").append("</level>").append("\n");
			out.append("  <type>").append("??").append("</type>").append("\n");
			out.append("  <onto1>").append("??").append("</onto1>").append("\n");
			out.append("  <onto2>").append("??").append("</onto2>").append("\n");
			out.append("  <uri1>").append("??").append("</uri1>").append("\n");
			out.append("  <uri2>").append("??").append("</uri2>").append("\n");
			
			//Write the alignment of each matching
			for (CanonicalModelConstruct construct1 : sourceConstructs) {
				for (CanonicalModelConstruct construct2 : targetConstructs) {
					//find the pair position in the matrix
					int rowIndex = sourceConstructs.indexOf(construct1);
					int colIndex = targetConstructs.indexOf(construct2);
					float cellValue = simMatrix[rowIndex][colIndex];
					
					if (cellValue != 0.0F) {
						out.append("    ").append("<map>").append("\n");
						out.append("      ").append("<Cell>").append("\n");
						
						String dsName1 = "";
						if (construct1 instanceof SuperAbstract) {
							dsName1 = "http://" + construct1.getSchema().getDataSource().getName() + "#";
							out.append("\t\t").append("<entity1 rdf:resource='").append(dsName1);
							out.append(construct1.getName()).append("'/>").append("\n");
						} else if (construct1 instanceof SuperLexical) {
							//find the parent of this super abstract
							SuperAbstract parentSuperAbstract = ((SuperLexical) construct1).getFirstAncestorSuperAbstract();
							dsName1 = "http://" + parentSuperAbstract.getSchema().getDataSource().getName() + "#";
							out.append("\t\t").append("<entity1 rdf:resource='");
							out.append(dsName1).append(parentSuperAbstract.getName());
							out.append(".").append(construct1.getName()).append("'/>").append("\n");						
						}
						
						String dsName2 = "";
						if (construct2 instanceof SuperAbstract) {
							dsName2 = "http://" + construct2.getSchema().getDataSource().getName() + "#";
							out.append("\t\t").append("<entity2 rdf:resource='").append(dsName2);
							out.append(construct2.getName()).append("'/>").append("\n");
						} else if (construct2 instanceof SuperLexical) {
							//find the parent of this super abstract
							SuperAbstract parentSuperAbstract = ((SuperLexical) construct2).getFirstAncestorSuperAbstract();
							dsName2 = "http://" + parentSuperAbstract.getSchema().getDataSource().getName() + "#";
							out.append("\t\t").append("<entity2 rdf:resource='");
							out.append(dsName2).append(parentSuperAbstract.getName());
							out.append(".").append(construct2.getName()).append("'/>").append("\n");	
						}
												
						out.append("\t\t").append("<measure rdf:datatype='");
						out.append("rdf:datatype='http://www.w3.org/2001/XMLSchema#float'>");
						out.append(df.format(cellValue)).append("</measure>").append("\n");
						out.append("\t\t").append("<relation>").append("=").append("</relation>").append("\n");
						
						out.append("      ").append("</Cell>").append("\n");
						out.append("    ").append("</map>").append("\n");
						
					}//end if				
					
				}//end for
			}//end for
			
			out.append("</Alignment>").append("\n");
			out.append("</rdf:RDF>");
			
			if (out != null) {
				out.close();
			}			
		} catch (IOException e) {
	       	logger.error("Error - I/O error while writing Matchings to a file.");
		}				
	}//end
	
}//end Class