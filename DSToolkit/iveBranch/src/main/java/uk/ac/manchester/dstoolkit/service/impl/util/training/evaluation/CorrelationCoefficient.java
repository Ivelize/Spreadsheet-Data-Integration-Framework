package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

import java.util.Set;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

public class CorrelationCoefficient extends PerformanceMeasures {

	public CorrelationCoefficient() {		
		this.mType = PerformanceErrorTypes.CORRELATION_COEFFICIENT;	
	}
	
	@Override
	public float calc(float[][] predictedMatrix,  float[][] observedMatrix) {
	
		float correlationCoef = 0.0F;
		
		//Loop the arrays
		int rowsNo 	  = predictedMatrix.length;
		int columnsNo = predictedMatrix[0].length;	
		

		float numberN  = (rowsNo * columnsNo) - 1; //denominator
						
		//Mean of the predicted and actual matrix  
		float sumA = 0.0F;
		float sumP = 0.0F;		
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				sumA = sumA + observedMatrix[i][j];
				sumP = sumP + predictedMatrix[i][j];				
			}
		}
		float meanA = sumA / (numberN + 1);
		float meanP = sumP / (numberN + 1);
		
		//------------------------------------------
		
		//Calculate s_P and s_A
		float sumPA = 0.0F;
		sumA = 0.0F;
		sumP = 0.0F;
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				
				float p  =  predictedMatrix[i][j];
				float a  =  observedMatrix[i][j];
				
				sumPA = sumPA + (float) ( (p - meanP) * (a - meanA) );
								
				sumA = sumA + (float) Math.pow(( a - meanA), 2);
				sumP = sumP + (float) Math.pow(( p - meanP), 2);			
			}//end for
		}//end for

		
		float numerator = (sumPA / numberN);
		float denominator = (float) Math.sqrt( (sumP / numberN) * (sumA / numberN) );
					
		
		correlationCoef = numerator / denominator;
		
			
		return correlationCoef;		
	}//end calc()

	
	/**
	 * This method is used for the calculation of the error using just the cells that had both syntactic
	 * and semantic evidences applied on them
	 */
	@Override
	public float calc(float[][] predictedMatrix, float[][] observedMatrix, Set<SemanticMatrixCellIndex> indexesSet) {

		float correlationCoef = 0.0F;
		
		float numberN  = indexesSet.size() - 1; //denominator
		
		//Mean of the predicted and actual matrix  
		float sumA = 0.0F;
		float sumP = 0.0F;	
		
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;
			
			sumA = sumA + observedMatrix[rowIndex][colIndex];
			sumP = sumP + predictedMatrix[rowIndex][colIndex];			
		}//end for
		
		float meanA = sumA / (numberN + 1);
		float meanP = sumP / (numberN + 1);
		
		//------------------------------------------
		
		//Calculate s_P and s_A
		float sumPA = 0.0F;
		sumA = 0.0F;
		sumP = 0.0F;
		
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;		

			float p  =  predictedMatrix[rowIndex][colIndex];
			float a  =  observedMatrix[rowIndex][colIndex];
			
			sumPA = sumPA + (float) ( (p - meanP) * (a - meanA) );
			
			sumA = sumA + (float) Math.pow(( a - meanA), 2);
			sumP = sumP + (float) Math.pow(( p - meanP), 2);			
		}//end for
		
		float numerator = (sumPA / numberN);
		float denominator = (float) Math.sqrt( (sumP / numberN) * (sumA / numberN) );				
		
		correlationCoef = numerator / denominator;
		
		return correlationCoef;
	}//end calc()	
}//end class