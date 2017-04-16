package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

import java.util.Comparator;

/**
 * Custom comparator for SemanticMatrix class to be used for sorting their precedence.
 * 
 * @author klitos
 */
public class SemanticMatrixComparator implements Comparator<SemanticMatrix> {
	
	public int compare(SemanticMatrix sem1, SemanticMatrix sem2) {
		//Get the ordinals of their SemanticMatrixTypes
		int ordinal1 = sem1.getType().ordinal();
		int ordinal2 = sem2.getType().ordinal();
		
		if (ordinal1 > ordinal2) {
			return +1;
	    } else if (ordinal1 < ordinal2) {
	    	return -1;
	    } else {
            return 0;
        }		
	}//end compare
}//end Class