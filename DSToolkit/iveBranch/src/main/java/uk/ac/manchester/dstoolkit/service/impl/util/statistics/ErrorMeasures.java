package uk.ac.manchester.dstoolkit.service.impl.util.statistics;

import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

public abstract class ErrorMeasures {

	static Logger logger = Logger.getLogger(ErrorMeasures.class);
	
	protected ErrorMeasuresTypes mType = null;	
	
	public ErrorMeasuresTypes getmType() {
		return mType;
	}	
	
	public abstract float calc(float p, float a);
	
	/***
	 * @param predictedMatrix - predicted values
	 * @param observedMatrix  - actual values
	 * @return float[][] - with all the pairwise error calculated
	 */
	public float[][] calc(float[][] predictedMatrix, float[][] observedMatrix) {
		int rowsNo 	  = predictedMatrix.length;
		int columnsNo = predictedMatrix[0].length;	
		
		//create resulted array
		float[][] array = new float[rowsNo][columnsNo];
		
		for (int i=0; i< rowsNo; i++) {
			for (int j=0; j< columnsNo; j++) {
				float p  =  predictedMatrix[i][j];
				float a  =  observedMatrix[i][j];				
				array[i][j] = this.calc(p, a);
			}
		}//end for
		
		return array;
	}//end calc
	
	public float[][] calc(float[][] predictedMatrix, float[][] observedMatrix, Set<SemanticMatrixCellIndex> indexesSet) {
		int rowsNo 	  = predictedMatrix.length;
		int columnsNo = predictedMatrix[0].length;	
		
		//create resulted array
		float[][] array = new float[rowsNo][columnsNo];
		
		for (SemanticMatrixCellIndex entry : indexesSet) {
			int rowIndex = entry.rowIndex;
			int colIndex = entry.colIndex;	
			
			float p  =  predictedMatrix[rowIndex][colIndex];
			float a  =  observedMatrix[rowIndex][colIndex];
			
			array[rowIndex][colIndex] = this.calc(p, a);
			
		}//end for		
		
		return array;
	}//end calc
}//end class
