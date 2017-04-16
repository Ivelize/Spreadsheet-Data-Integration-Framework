package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

import java.util.Set;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

/***
 * Mean-squared Error 
 * 
 *  Updated version to calculate only the difference between the cells that have both syn and sem 
 *  evidences
 * 
 * @author klitos
 */
public class MeanSquaredError extends PerformanceMeasures {
	
	public MeanSquaredError() {		
		this.mType = PerformanceErrorTypes.MEAN_SQUARED_ERROR;		
	}	
	
	@Override
	public float calc(float[][] predictedMatrix,  float[][] observedMatrix) {
		float meanSquareError = 0.0F;
		
		//Loop the arrays
		int rowsNo 	  = predictedMatrix.length;
		int columnsNo = predictedMatrix[0].length;	
		
		float numerator = 0.0F;
		int numberN   = rowsNo * columnsNo; //denominator
		
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				
				float p  =  predictedMatrix[i][j];
				float a  =  observedMatrix[i][j];
					
				numerator = numerator + (float) Math.pow((p - a),2);
				
			}//end for
		}//end for
	
		meanSquareError = numerator / (float) numberN;	
				
		return meanSquareError;		
	}//end calc()
	
	/**
	 * This method is used for the calculation of the error using just the cells that had both syntactic
	 * and semantic evidences applied on them
	 */	
	@Override
	public float calc(float[][] predictedMatrix,  float[][] observedMatrix, Set<SemanticMatrixCellIndex> indexesSet) {

		float meanSquareError = 0.0F;
		float numerator	= 0.0F;
		
		/* Denominator in this case is the number of cells that have both syntactic and semantic evidence,
		   i.e., the size of the indexesSet */
		int numberN = indexesSet.size();

		/* Not necessary to loop the entire matrix because the set of indexes to be considered for
		 the calculation is provided */		
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;		
			
			float p  =  predictedMatrix[rowIndex][colIndex];
			float a  =  observedMatrix[rowIndex][colIndex];
						
			numerator = numerator + (float) Math.pow((p - a), 2);		
		}//end for
		
		meanSquareError = numerator / (float) numberN;	
			
		return meanSquareError;		
	}//end calc()	
}//end class