package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.Comparator;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;

/**
 * Custom comparator for the Matching objects to be used for sorting their precedence.
 * 
 * @author klitos
 */
public class MatchingComparator implements Comparator<Matching> { 

	/***
	 * Note: That the ranking of matches by Jena is different for the ranking
	 * achieved using this comparator. For the reason that the ranking achieved
	 * by Jena takes into account both the ?score and ?label
	 */	
	public int compare(Matching o1, Matching o2) {
	
		if (o1.getScore() > o2.getScore()) {
			return +1;
		} else if (o1.getScore() < o2.getScore()) {
			return -1;
		} else {
			return 0;
		}		
	}//end compare()	
}//end Class