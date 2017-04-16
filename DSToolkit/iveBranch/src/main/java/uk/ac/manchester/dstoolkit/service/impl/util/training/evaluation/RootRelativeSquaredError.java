package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

import java.util.Set;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

public class RootRelativeSquaredError extends PerformanceMeasures {

	public RootRelativeSquaredError() {	
		this.mType = PerformanceErrorTypes.ROOT_RELATIVE_SQUARED_ERROR;	
	}
	
	@Override
	public float calc(float[][] predictedMatrix,  float[][] observedMatrix) {
	
		float rootRelativeSquaredError = 0.0F;
		float relativeSquaredError = 0.0F;
		
		//Loop the arrays
		int rowsNo 	  = predictedMatrix.length;
		int columnsNo = predictedMatrix[0].length;	
		

		int numberN   = rowsNo * columnsNo; //denominator
						
		//Mean of the actual matrix (observedMatrix) 
		float meanA = 0.0F;
		float sum = 0.0F;
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				sum = sum + observedMatrix[i][j];
			}
		}
		
		meanA = sum / numberN;
		
		//------------------------------------------
		
		float numerator   = 0.0F;
		float denominator = 0.0F;		
		
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				
				float p  =  predictedMatrix[i][j];
				float a  =  observedMatrix[i][j];
								
				numerator = numerator + (float) Math.pow((p - a), 2);
	
				denominator = denominator + (float) Math.pow((a - meanA), 2);				
			}//end for
		}//end for
				
		relativeSquaredError = numerator / denominator;		
		
		rootRelativeSquaredError = (float) Math.sqrt(relativeSquaredError);
			
		return rootRelativeSquaredError;		
	}//end calc()

	
	/**
	 * This method is used for the calculation of the error using just the cells that had both syntactic
	 * and semantic evidences applied on them
	 */
	@Override
	public float calc(float[][] predictedMatrix, float[][] observedMatrix, Set<SemanticMatrixCellIndex> indexesSet) {

		float rootRelativeSquaredError = 0.0F;
		float relativeSquaredError = 0.0F;
		
		logger.debug("indexesSet: " + indexesSet);
		
		/* Denominator in this case is the number of cells that have both syntactic and semantic evidence,
		   i.e., the size of the indexesSet */	
		int numberN = indexesSet.size();
		
		logger.debug("numberN: " + numberN);
		
		//Mean of the actual matrix (observedMatrix) 
		float meanA = 0.0F;
		float sum = 0.0F;
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;	
			
			sum = sum + observedMatrix[rowIndex][colIndex];			
		}//end for
		
		meanA = sum / (float) numberN;
		
		//------------------------------------------
		
		float numerator   = 0.0F;
		float denominator = 0.0F;
		
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;		

			float p  =  predictedMatrix[rowIndex][colIndex];
			float a  =  observedMatrix[rowIndex][colIndex];
			
			logger.debug("predicted: " + p +" , actual: " + a);
			
			numerator = numerator + (float) Math.pow((p - a), 2);

			denominator = denominator + (float) Math.pow((a - meanA), 2);			
		}//end for
		
		logger.debug("numerator: " + numerator);
		logger.debug("denominator: " + denominator);
		
		relativeSquaredError = numerator / denominator;		
		
		rootRelativeSquaredError = (float) Math.sqrt(relativeSquaredError);
		
		logger.debug("rootRelativeSquaredError: " + rootRelativeSquaredError);
		
		return rootRelativeSquaredError;
	}//end calc()
}//end class
