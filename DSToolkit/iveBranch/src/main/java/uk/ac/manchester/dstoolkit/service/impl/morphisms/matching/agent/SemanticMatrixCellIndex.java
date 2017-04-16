package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

/***
 * This class is responsible to hold the indexes of cells in a Semantic Matrix
 * for Bayes that has both syntactic and semantic evidences applied to it. This info
 * is used later on to measure the error between only the cells that have semantic 
 * evidence applied on top of the syntactic evidences.
 * 
 * @author klitos
 */
public class SemanticMatrixCellIndex {
	public int rowIndex = -1;
	public int colIndex = -1;
	
	//Constructor
	public SemanticMatrixCellIndex(int rowIndex, int colIndex) {
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colIndex;
		result = prime * result + rowIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SemanticMatrixCellIndex other = (SemanticMatrixCellIndex) obj;
		if (colIndex != other.colIndex)
			return false;
		if (rowIndex != other.rowIndex)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "[" + rowIndex + "," + colIndex + "]";
	}
}//end class