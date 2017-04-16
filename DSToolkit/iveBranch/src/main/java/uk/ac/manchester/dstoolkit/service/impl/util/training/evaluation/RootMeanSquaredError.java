package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

import java.util.Set;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

public class RootMeanSquaredError extends PerformanceMeasures {

	public RootMeanSquaredError() {
		this.mType = PerformanceErrorTypes.ROOT_MEAN_SQUARED_ERROR;
	}

	@Override
	public float calc(float[][] predictedMatrix,  float[][] observedMatrix) {
	
		float rootMeanSquareError = 0.0F;
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
		rootMeanSquareError = (float) Math.sqrt(meanSquareError);		
		
		return rootMeanSquareError;		
	}//end calc()	

	
	/**
	 * This method is used for the calculation of the error using just the cells that had both syntactic
	 * and semantic evidences applied on them
	 */	
	@Override
	public float calc(float[][] predictedMatrix, float[][] observedMatrix, Set<SemanticMatrixCellIndex> indexesSet) {
		float rootMeanSquareError = 0.0F;
		float meanSquareError = 0.0F;
		float numerator = 0.0F;
		
		logger.debug("indexesSet: " + indexesSet);
		
		/* Denominator in this case is the number of cells that have both syntactic and semantic evidence,
		   i.e., the size of the indexesSet */	
		int numberN = indexesSet.size();
		
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;		

			float p  =  predictedMatrix[rowIndex][colIndex];
			float a  =  observedMatrix[rowIndex][colIndex];
			
			logger.debug("predicted: " + p +" , actual: " + a);
			
			numerator = numerator + (float) Math.pow((p - a),2);
		}//end for
		
		logger.debug("numberN: " + numberN);
		
		meanSquareError = numerator / numberN;	
		rootMeanSquareError = (float) Math.sqrt(meanSquareError);
		
		logger.debug("rootMeanSquareError: " + rootMeanSquareError);
		
		return rootMeanSquareError;
	}//end calc()	
}//end class