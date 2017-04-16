package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

import java.util.Set;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

public class RelativeAbsoluteError extends PerformanceMeasures {

	public RelativeAbsoluteError() {	
		this.mType = PerformanceErrorTypes.RELATIVE_ABS_ERROR;	
	}
	
	@Override
	public float calc(float[][] predictedMatrix,  float[][] observedMatrix) {
	
		float relativeAbsError = 0.0F;
		
		//Loop the arrays
		int rowsNo 	  = predictedMatrix.length;
		int columnsNo = predictedMatrix[0].length;	
		
		int numberN = rowsNo * columnsNo; //denominator
						
		//Mean of the actual matrix (observedMatrix) 
		float meanA = 0.0F;
		float sum = 0.0F;
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				sum = sum + observedMatrix[i][j];
			}
		}//end for
		
		meanA = sum / numberN;
		
		//------------------------------------------
		
		float numerator   = 0.0F;
		float denominator = 0.0F;		
		
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				
				float p  =  predictedMatrix[i][j];
				float a  =  observedMatrix[i][j];
								
				numerator = numerator + (float) Math.abs((p - a));
	
				denominator = denominator + (float) Math.abs((a - meanA));				
			}//end for
		}//end for
				
		relativeAbsError = numerator / denominator;		
			
		return relativeAbsError;		
	}//end calc()

	@Override
	public float calc(float[][] predictedMatrix, float[][] observedMatrix, Set<SemanticMatrixCellIndex> indexesSet) {
		float relativeAbsError = 0.0F;
		
		/* Denominator in this case is the number of cells that have both syntactic and semantic evidence,
		   i.e., the size of the indexesSet */	
		int numberN = indexesSet.size();
		
		//Mean of the actual matrix (observedMatrix) 
		float meanA = 0.0F;
		float sum = 0.0F;
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;
			
			sum = sum + observedMatrix[rowIndex][colIndex];			
		}//end for
		
		meanA = sum / numberN;
		
		//------------------------------------------
		
		float numerator   = 0.0F;
		float denominator = 0.0F;
		
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;
			
			float p  =  predictedMatrix[rowIndex][colIndex];
			float a  =  observedMatrix[rowIndex][colIndex];
			
			numerator = numerator + Math.abs((p - a));
			
			denominator = denominator + Math.abs((a - meanA));			
		}//end for
		
		relativeAbsError = numerator / denominator;		
		
		return relativeAbsError;
	}//end calc()	
}//end class