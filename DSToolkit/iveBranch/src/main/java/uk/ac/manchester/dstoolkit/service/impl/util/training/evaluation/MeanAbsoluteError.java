package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

import java.util.Set;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

public class MeanAbsoluteError extends PerformanceMeasures {

	public MeanAbsoluteError() {
		this.mType = PerformanceErrorTypes.MEAN_ABS_ERROR;
	}

	@Override
	public float calc(float[][] predictedMatrix,  float[][] observedMatrix) {
	
		float meanAbsError = 0.0F;
		
		//Loop the arrays
		int rowsNo 	  = predictedMatrix.length;
		int columnsNo = predictedMatrix[0].length;	
		
		float numerator = 0.0F;
		int numberN   = rowsNo * columnsNo; //denominator
			
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				
				float p  =  predictedMatrix[i][j];
				float a  =  observedMatrix[i][j];
								
				numerator = numerator + (float) Math.abs((p - a));
				
			}//end for
		}//end for
		
		meanAbsError = numerator / (float) numberN;	
			
		return meanAbsError;		
	}//end calc()

	/**
	 * This method is used for the calculation of the error using just the cells that had both syntactic
	 * and semantic evidences applied on them
	 */
	@Override
	public float calc(float[][] predictedMatrix, float[][] observedMatrix, Set<SemanticMatrixCellIndex> indexesSet) {
		float meanAbsError = 0.0F;
		float numerator  = 0.0F;
		
		/* Denominator in this case is the number of cells that have both syntactic and semantic evidence,
		   i.e., the size of the indexesSet */	
		int numberN = indexesSet.size();
		
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;	
		
			float p  =  predictedMatrix[rowIndex][colIndex];
			float a  =  observedMatrix[rowIndex][colIndex];
							
			numerator = numerator + (float) Math.abs((p - a));		
		}//end for		
		
		meanAbsError = numerator / (float) numberN;	
		
		return meanAbsError;
	}//end calc()
}//end class