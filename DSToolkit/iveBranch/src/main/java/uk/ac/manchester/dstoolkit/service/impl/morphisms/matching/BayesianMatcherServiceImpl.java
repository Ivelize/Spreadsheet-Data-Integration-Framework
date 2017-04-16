package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.BayesEntry;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ErrorMeasuresTypes;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.BayesianMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;


/***
 * This matcher service is responsible for:
 *  - creating the final matchings derived as degrees of belief on the equivalence/non-equivalence of constructs
 *  - 
 *  
 * @author klitos
 */

@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class BayesianMatcherServiceImpl extends MatcherServiceImpl implements BayesianMatcherService {

	private static Logger logger = Logger.getLogger(BayesianMatcherServiceImpl.class);

	//Instansiate matching producer service	
	@Autowired
	private MatchingProducerService matchingProducerService;

	//2D matrix that holds degress of belief, this can be converted to a float [][]
	private SemanticMatrix semMatrix = null; 
	
	public BayesianMatcherServiceImpl() {
		logger.debug("in BayesianMatcherServiceImpl");
		this.setName("BAYESIAN_APPROACH");
		this.setMatcherType(MatcherType.BAYESIAN_APPROACH);
	}

	public BayesianMatcherServiceImpl(SemanticMatrix semMatrix) {
		logger.debug("in BayesianMatcherServiceImpl");
		this.setName("BAYESIAN_APPROACH");
		this.setMatcherType(MatcherType.BAYESIAN_APPROACH);
		this.semMatrix = semMatrix;
	}
	
	/**
	 * This method is responsible for creating the matches and making them persistent. It makes a call to the matchingProducerService.
	 * @return List<Matching>
	 */
	public List<Matching> produceAndSaveMatches(Schema sourceSchema, Schema targetSchema) {
							
		List<CanonicalModelConstruct> constructs1 = this.getConstructs(sourceSchema.getCanonicalModelConstructs());
		List<CanonicalModelConstruct> constructs2 = this.getConstructs(targetSchema.getCanonicalModelConstructs());
		
		List<Matching> matchings = null;
		
		try {
			if ((constructs1 != null) && (constructs2 != null)) { 
			
				float[][] simMatrix = this.semMatrix.getSemMatrixAsArray();
			
				//Aggregation strategy does not apply for this matcher service
				if (this.getControlParameters().containsKey(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE)) {
					throw new Exception("Bayesian framework - aggregation is not allowed");
				}
			
				//Apply selection strategy if exists
				if (this.getControlParameters().containsKey(ControlParameterType.MATCH_SELECT_SELECTION_TYPE)) {
					simMatrix = this.select(simMatrix);			
				}
					
				matchings = matchingProducerService.produceFinalMatches(simMatrix, constructs1, constructs2, this.getControlParameters(), this);
				/*Use the constructed list of matchings and call saveMatchers() to persist the Matchings*/
				this.saveMatches(matchings);
			}//end if
		} catch (Exception exe) {
			logger.error("ERROR BayesianMatcherServiceImpl - " + exe.getMessage());
		}
			
		return matchings;
	}//end produceAndSaveMatches()
	
	/**
	 * This method is responsible for creating the matches without saving them. It makes a call to the matchingProducerService.
	 * @return List<Matching>
	 */
	public List<Matching> produceMatches(Schema sourceSchema, Schema targetSchema) {
							
		List<CanonicalModelConstruct> constructs1 = this.getConstructs(sourceSchema.getCanonicalModelConstructs());
		List<CanonicalModelConstruct> constructs2 = this.getConstructs(targetSchema.getCanonicalModelConstructs());
		
		List<Matching> matches = null;
		
		try {
			if ((constructs1 != null) && (constructs2 != null)) { 
			
				float[][] simMatrix = this.semMatrix.getSemMatrixAsArray();
			
				//Aggregation strategy does not apply for this matcher service
				if (this.getControlParameters().containsKey(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE)) {
					throw new Exception("Bayesian framework - aggregation is not allowed");
				}
			
				//Apply selection strategy if exists
				if (this.getControlParameters().containsKey(ControlParameterType.MATCH_SELECT_SELECTION_TYPE)) {
					simMatrix = this.select(simMatrix);			
				}
					
				matches = matchingProducerService.produceFinalMatches(simMatrix, constructs1, constructs2, this.getControlParameters(), this);

			}//end if
		} catch (Exception exe) {
			logger.error("ERROR BayesianMatcherServiceImpl - " + exe.getMessage());
		}
			
		return matches;
	}//end produceMatches()
	
	
	/***
	 * This method is responsible for creating the Matches objects along with their calculated error
	 * @return List<Matching>
	 */
	public List<Matching> produceAndSaveMatchesWithError(Schema sourceSchema, Schema targetSchema, float[][] errMatrix) {
		
		List<CanonicalModelConstruct> constructs1 = this.getConstructs(sourceSchema.getCanonicalModelConstructs());
		List<CanonicalModelConstruct> constructs2 = this.getConstructs(targetSchema.getCanonicalModelConstructs());
		
		List<Matching> matches = null;
		
		try {
			if ((constructs1 != null) && (constructs2 != null)) { 
			
				float[][] simMatrix = this.semMatrix.getSemMatrixAsArray();
			
				//Aggregation strategy does not apply for this matcher service
				if (this.getControlParameters().containsKey(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE)) {
					throw new Exception("Bayesian framework - aggregation is not allowed");
				}
			
				//Apply selection strategy if exists
				if (this.getControlParameters().containsKey(ControlParameterType.MATCH_SELECT_SELECTION_TYPE)) {
					simMatrix = this.select(simMatrix);			
				}
					
				matches = matchingProducerService.produceFinalMatchesWithError(simMatrix, errMatrix, constructs1, constructs2,
																							this.getControlParameters(), this);

			}//end if
		} catch (Exception exe) {
			logger.error("ERROR BayesianMatcherServiceImpl - " + exe.getMessage());
		}		
		
		return matches;	
	}//end produceAndSaveMatchesWithError()
		
	/***
	 * This method will produce Matching objects for selected cells in the matrix. A set of indexes is given as
	 * an argument to the method. This method also stores the individual error in a Match object.
	 * 
	 * @param Set<SemanticMatrixCellIndex> cellsSet
	 */
	public List<Matching> produceAndSaveMatchesForSpecificCellsWithError(Schema sourceSchema, Schema targetSchema,
																		float[][] errMatrix, Set<SemanticMatrixCellIndex> cellsSet) {
		
		List<CanonicalModelConstruct> constructs1 = this.getConstructs(sourceSchema.getCanonicalModelConstructs());
		List<CanonicalModelConstruct> constructs2 = this.getConstructs(targetSchema.getCanonicalModelConstructs());
		
		//Initialise the array that holds the Matching objects
		List<Matching> matches = new ArrayList<Matching>();
				
		try {
			ErrorMeasuresTypes errorType = null;
			
			if (this.getControlParameters().containsKey(ErrorMeasuresTypes.SQUARED_ERROR)) {
				errorType = this.getControlParameters().get(ErrorMeasuresTypes.SQUARED_ERROR).getErrorMeasureType();
			}		

			if (this.getControlParameters().containsKey(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
				errorType = this.getControlParameters().get(ErrorMeasuresTypes.ABSOLUTE_ERROR).getErrorMeasureType();
			}			
			
			if ((constructs1 != null) && (constructs2 != null)) {	
				//Loop through the IndexSet of saved indexes and create a Matching object representation
				for (SemanticMatrixCellIndex cell : cellsSet) {
					int i = cell.rowIndex;
					int j = cell.colIndex;
					
					//Get the entry that corresponds to the [i,j]
					BayesEntry semanticEntry = (BayesEntry) this.semMatrix.getCellSemanticEntry(i, j);
					
					if (semanticEntry != null) {	
						/*Create OneToOneMatching, for each match*/
						OneToOneMatching matching = new OneToOneMatching(constructs1.get(i), constructs2.get(j),
																semanticEntry.getLastPosterior(), this.getName());
											
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
					}//end if				
				}//end for		
			}//end if
		} catch (Exception exe) {
			logger.error("ERROR BayesianMatcherServiceImpl - " + exe.getMessage());
			exe.printStackTrace();
		}
		
		return matches;
	}//end produceAndSaveMatchesForSpecificCellsWithError()	
	
	/***
	 * This method will produce Matching objects for selected cells in the matrix. A set of indexes is given as
	 * an argument to the method.
	 * 
	 * @param Set<SemanticMatrixCellIndex> cellsSet
	 */
	public List<Matching> produceMatchesForSpecificCells(Schema sourceSchema, Schema targetSchema, Set<SemanticMatrixCellIndex> cellsSet) {
		
		List<CanonicalModelConstruct> constructs1 = this.getConstructs(sourceSchema.getCanonicalModelConstructs());
		List<CanonicalModelConstruct> constructs2 = this.getConstructs(targetSchema.getCanonicalModelConstructs());
		
		//Initialise the array that holds the Matching objects
		List<Matching> matches = new ArrayList<Matching>();
		
		try {
			if ((constructs1 != null) && (constructs2 != null)) {		
				//Loop through the IndexSet of saved indexes and create a Matching object representation
				for (SemanticMatrixCellIndex cell : cellsSet) {
					int i = cell.rowIndex;
					int j = cell.colIndex;
					
					//Get the entry that corresponds to the [i,j]
					BayesEntry semanticEntry = (BayesEntry) this.semMatrix.getCellSemanticEntry(i, j);
															
					if (semanticEntry != null) {	
						/*Create OneToOneMatching, for each match*/
						OneToOneMatching matching = new OneToOneMatching(constructs1.get(i), constructs2.get(j),
																semanticEntry.getLastPosterior(), this.getName());
					
						if (matching != null) {
							matches.add(matching);
						}//end if
					}//end if
					
				}//end for				
		
			}//end if
		} catch (Exception exe) {
			logger.error("ERROR BayesianMatcherServiceImpl - " + exe.getMessage());
			exe.printStackTrace();
		}
		
		return matches;		
	}//end produceMatches()
	
	/**
	 * @return SemanticMatrix - a matrix of type SemMatrix with degrees of belief
	 */
	public SemanticMatrix getSemMatrixWithDegreesOfBelief() {
		return this.semMatrix;
	}
	
	/**
	 * @return float[][] - a matrix of type [][] with degrees of belief
	 */
	public float[][] getMatrixWithDegreesOfBelief() {
		return this.semMatrix.getSemMatrixAsArray();
	}

	public void setSemMatrixWithDegreesOfBelief(SemanticMatrix m) {
		this.semMatrix = m;
	}
	
	private ArrayList<CanonicalModelConstruct> getConstructs(Set<CanonicalModelConstruct> inputConstructs) {
		ArrayList<CanonicalModelConstruct> outputConstructs = new ArrayList<CanonicalModelConstruct>();
		for (CanonicalModelConstruct inputConstruct : inputConstructs) {
			if (!inputConstruct.getTypeOfConstruct().equals(ConstructType.SUPER_RELATIONSHIP)) {
				outputConstructs.add(inputConstruct);
			}
		}
		return outputConstructs;
	}
}//end class