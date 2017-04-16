package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatchingComparator;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.BayesEntry;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;

/**
 * Represent a two dimensional array with arrayLists
 * 
 * @author klitos
 */
public class SemanticMatrix {

	/*Type of this semanticMatrix*/
	private SemanticMatrixType type;
	
	/*Hold rows, columns*/
	private int rows;
	private int columns;
	
	/*Index number of this semanticMatrix*/
	private int index;
	
	/*Represent the rows of the array*/
	private ArrayList<ArrayList<SemanticMatrixEntry>> rowsArray = null; 
	
	/* Store the index of this cell, to be used later to measure the error for the
	 * cells that have both syntactic and semantic evidences applied to them */
	Set<SemanticMatrixCellIndex> indexesSet = null;
	
	/**
	 * Constructor1: Create a new Semantic Matrix 
	 * @param rows - number of rows
	 * @param columns - number of columns
	 */
	public SemanticMatrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		rowsArray = new ArrayList<ArrayList<SemanticMatrixEntry>>();
		ArrayList<SemanticMatrixEntry> columnArray;
		for (int i=0; i<rows; i++) {
			columnArray = new ArrayList<SemanticMatrixEntry>();
			rowsArray.add(columnArray);
		}
	}//end constructor
	
	/**
	 * Constructor2: Create a new Semantic Matrix
	 * @param rows - number of rows
	 * @param columns - number of columns
	 * @param index - index number of this matrix to keep track of order
	 */
	public SemanticMatrix(int rows, int columns, int index) {
		this.rows = rows;
		this.columns = columns;
		this.index = index;
		rowsArray = new ArrayList<ArrayList<SemanticMatrixEntry>>();
		ArrayList<SemanticMatrixEntry> columnArray;
		for (int i=0; i<rows; i++) {
			columnArray = new ArrayList<SemanticMatrixEntry>();
			rowsArray.add(columnArray);
		}
	}//end constructor
	
	public ArrayList<SemanticMatrixEntry> getRow(int i) {
		return rowsArray.get(i);
	}
	
	public SemanticMatrixType getType() {
		return type;
	}

	public void setType(SemanticMatrixType type) {
		this.type = type;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public int getNoRows() {
		return this.rows;
	}
	
	public int getNoColumns() {
		return this.columns;
	}
		
	public SemanticMatrixEntry getCellSemanticEntry(int row, int column) {
		if (!(row < this.rows) || !(column < this.columns)) {
			return null;
		} 
		
		/*Get the ArrayList that holds the columns of this row*/
		ArrayList<SemanticMatrixEntry> columns = rowsArray.get(row);
		SemanticMatrixEntry entry = columns.get(column);
				
		return entry;
	}//end getCellSemanticEntry()
	
    /**
     * This method will transform an Expectation/Bayes matrix as a float[][] expMatrix.
     * To be used when calculating the root-mean-square-error. 
     */
    public float[][] getSemMatrixAsArray() {	
 	
    	float[][] expMatrix = null;
    	
    	if (type.equals(SemanticMatrixType.EXPECTATION_MATRIX)) {    	
    		expMatrix = new float[this.rows][this.columns];
    	
    		for (int i=0; i< rowsArray.size(); i++) {
    			ArrayList<SemanticMatrixEntry> columns = rowsArray.get(i);
    			for (int j=0; j< columns.size(); j++) {
    				//Get the cells from each matrix
    				ExpectationMatrixEntry expMatrixCell = (ExpectationMatrixEntry) columns.get(j);		
				
    				if (expMatrixCell == null) {
    					expMatrix[i][j] = 0.0F;	
    				} else {				
    					expMatrix[i][j] = expMatrixCell.getCellScore();
    				}
    			}//end for
    		}//end for    		
    	} else if (type.equals(SemanticMatrixType.BAYES)) {    		
    		expMatrix = new float[this.rows][this.columns];
    		
    		for (int i=0; i< rowsArray.size(); i++) {
    			ArrayList<SemanticMatrixEntry> columns = rowsArray.get(i);
    			for (int j=0; j< columns.size(); j++) {
    				//Get the cells from each matrix
    				BayesEntry bayesMatrixCell = (BayesEntry) columns.get(j);		
				
    				if (bayesMatrixCell == null) {
    					expMatrix[i][j] = 0.0F;	
    				} else {				
    					expMatrix[i][j] = (float) bayesMatrixCell.getLastPosterior();
    				}
    			}//end for
    		}//end for    		
    	}//end else 
    	
    	return expMatrix;
    }//end getSemMatrixAsArray() 	
       
    public void attachIndexesSet(Set<SemanticMatrixCellIndex> iSet) {
    	this.indexesSet = iSet;
    }//end attachIndexesSet()
    
    public Set<SemanticMatrixCellIndex> getIndexesSet() {    	
		//unmodifiable means that order is note changed but I cannot add new elements or modify the Set
    	if (this.indexesSet != null) { 
    		return Collections.unmodifiableSet(this.indexesSet);
    	} else {
    		return null;
    	}
    }//end getIndexesSet()    
    
    /***
     * This method is responsible for OneToOneMatching objects for the cells that have been 
     * identified to have assimilated both Syntactic and Semantic evidence. This method 
     * consults the Set<SemanticMatrixCellIndex> indexesSet which holds a list of cells 
     * that satisfy this requirement.
     * 
     * @return - List<Matching> is a SORTED list of Matching objects. Ready to be used for the 
     * calculation of Precision/Recall without performing sorting first.
     */
    public List<Matching> produceMatches(final List<CanonicalModelConstruct> sourceConstructs, 
    									 final List<CanonicalModelConstruct> targetConstructs,
    									 final Map<ControlParameterType, ControlParameter> controlParameters,
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
        
		//Loop through the IndexSet of saved indexes and create a Matching object representation
		for (SemanticMatrixCellIndex cell : indexesSet) {
			int i = cell.rowIndex;
			int j = cell.colIndex;
			
			SemanticMatrixEntry semanticEntry = this.getCellSemanticEntry(i, j);
			
			if (semanticEntry != null) {				
				float cellValue = 0.0F;				
		    	if (type.equals(SemanticMatrixType.EXPECTATION_MATRIX)) { 
		    		cellValue = ((ExpectationMatrixEntry) semanticEntry).getCellScore();
		    	} else if (type.equals(SemanticMatrixType.BAYES)) { 
		    		cellValue = (float) ((BayesEntry) semanticEntry).getLastPosterior();
		    	}//end if 
		    	
				/*Create OneToOneMatching, for each match*/
				OneToOneMatching matching = new OneToOneMatching(sourceConstructs.get(i), targetConstructs.get(j), cellValue, matcherServiceName);
				
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
        
		return matches;
    }//end produceMatches()   
    
    public boolean setContainsIndex(int i, int j) {
    	return this.indexesSet.contains(new SemanticMatrixCellIndex(i,j));  	
    }//end setContainsIndex()    

    public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0; i< rowsArray.size(); i++) {
			ArrayList<SemanticMatrixEntry> columns = rowsArray.get(i);			
			for (int j=0; j< columns.size(); j++) {
				SemanticMatrixEntry cell = columns.get(j);
				if (cell != null) {	
					if (type.equals(SemanticMatrixType.BAYES)) {
						stringBuilder.append( ((BayesEntry) columns.get(j)).getLastPosterior() + " ");
					} else {
						stringBuilder.append(columns.get(j).getCellValue() + " ");
					}
				} else {
					stringBuilder.append(cell + " ");
				}			
            }//end for
			stringBuilder.append("\n");
        }//end for
		return stringBuilder.toString();		
	}//end toString();

}//end
