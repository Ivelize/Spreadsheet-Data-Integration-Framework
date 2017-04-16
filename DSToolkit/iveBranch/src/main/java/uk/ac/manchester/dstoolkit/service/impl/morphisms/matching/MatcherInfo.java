package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;

/**
 * This class is responsible for holding information about a matcher. In particular it holds:
 *   [1] The name of the matcher - this is the obj.getClass().getName().
 *   [2] The index number of this matcher provided by the list of matchers.
 *   [3] The [][] two-dimensional sim matrix produced by the match() of this Matcher. 
 * 
 * @author Klitos Christodoulou
 *
 */
public class MatcherInfo {

	private MatcherService matcherService = null;
	private String 		matcherName = null;
	private int 		indexNum;
	private float[][]	simMatrix = null;	
	
	public MatcherInfo(MatcherService matcher) {
		this.matcherService = matcher;
	}//end constructor	
	
	public MatcherInfo(MatcherService matcher, int indexNum) {
		this.matcherService = matcher;
		this.indexNum = indexNum;
	}//end constructor	
	
	public MatcherInfo(String matcherName, int indexNum) {
		this.matcherName = matcherName;
		this.indexNum = indexNum;
	}//end constructor	
	
	public MatcherInfo(String matcherName, int indexNum, float[][] simMatrix) {
		this.matcherName = matcherName;
		this.indexNum = indexNum;
		this.simMatrix = simMatrix;		
	}//end constructor	
	

	/*---- Setter Methods ----*/
	public void setMatcherName(String name) {
		this.matcherName = name;
	}
	
	public void setIndexNum(int index) {
		this.indexNum = index;
	}
	
	public void addSimMatrix(float[][] matrix) {
		this.simMatrix = matrix;
	}	

	public void setMatcherService(MatcherService matcherService) {
		this.matcherService = matcherService;
	}
	

	/*---- Getter Methods ----*/
	public String getMatcherName() {
		if (matcherService != null) { 
			return matcherService.getName();
		}		
		return this.matcherName;
	}
	
	public MatcherType getMatcherType() {
		if (matcherService != null) { 
			return matcherService.getMatcherType();
		}		
		return null;
	}	
	
	public int setIndexNum() {
		return this.indexNum;
	}
		
	public float[][] getSimMatrix() {
		return this.simMatrix;
	}	
	
	public MatcherService getMatcherService() {
		return matcherService;
	}
		
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("MatcherName: ").append(this.matcherName).append("\n");
		stringBuilder.append("IndexNum: ").append(this.indexNum).append("\n");
		stringBuilder.append("Similarity Matrix: ").append("\n");
		
		for (int row = 0; row < simMatrix.length; row++) {
			for (int col = 0; col < simMatrix[row].length; col++) {
				stringBuilder.append(" " + simMatrix[row][col]);
	        }//end for
			stringBuilder.append("\n");
	    }//end for		
		return stringBuilder.toString();
	}//end toString()
}//end class
