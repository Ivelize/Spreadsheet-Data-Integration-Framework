package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

import java.text.DecimalFormat;
import java.util.List;

import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.ClassesAlign;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.PredicatesAlign;

/***
 * This class holds data about each entry/cell in a SemanticMatrix. The class is a variation of the 
 * SemanticMatrixEntry, to be used for storing data regarding the creation of the Expectation Matrix. 
 *
 */
public class ExpectationMatrixEntry extends SemanticMatrixEntry {

	/*Hold a ref to a ClassAlign Object*/
	private ClassesAlign mapFoundClass = null;	
	
	/*Hold a ref to a PredicateAlign Object*/
	private PredicatesAlign mapFoundPred = null;
	
	/*Hold the value of that Cell*/
	private float scoreOfcell;
	
	/*Hold the evidence from Expectation Matrix*/
	private List<BooleanVariables> evidenceList = null;

	/*Constructor1: Generic*/
	public ExpectationMatrixEntry(float value, List<BooleanVariables> evidence) {
		this.setTypeOfEntry(SemanticMatrixType.EXPECTATION_MATRIX);
		this.scoreOfcell = (float) value;
       	evidenceList = evidence;
	}	

	/*Constructor2: for ClassAling*/
	public ExpectationMatrixEntry(ClassesAlign ca) {
		mapFoundClass = ca;
	}

	/*Constructor3: for PredicateAlign*/	
	public ExpectationMatrixEntry(PredicatesAlign pa) {
		mapFoundPred = pa;
	}	
	
	/*Return the score for this cell*/
	public float getCellScore() {		
		if (mapFoundClass != null) {
			return mapFoundClass.getScore();
		} else if (mapFoundPred != null) {
			return mapFoundPred.getScore();
		} 		
		return this.scoreOfcell;
	}//end getCellScore()
	
	/*Return the score of this cell as a percentage*/
	public String getCellScoreAsPerc() {
		double perc = this.getCellScore() * 100;
	    DecimalFormat df = new DecimalFormat("#.##");		
		return df.format(perc);	
	}

	/*Return Evidence List*/
    public List<BooleanVariables> getEvidence() {
		if (mapFoundClass != null) {
			return mapFoundClass.getEvidence();
		} else if (mapFoundPred != null) {
			return mapFoundPred.getEvidence();
		} 		
		return this.evidenceList;	
    }//getEvidence()	

	//Print the evidence of each cell
	public String toString() {
		return ">> This method is incomplete";		
	}//end toString()	
}//end class
