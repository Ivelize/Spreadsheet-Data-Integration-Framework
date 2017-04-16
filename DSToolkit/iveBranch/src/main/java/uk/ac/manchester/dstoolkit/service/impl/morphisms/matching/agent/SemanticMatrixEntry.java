package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

import java.util.HashSet;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;

/***
 * This class holds data about each entry/cell in a SemanticMatrix 
 *	
 * @author klitos
 */
public class SemanticMatrixEntry {

	//EQUIVALENCE, SUBSUMPTION, SHARE_SUPERCLASS, DOMAIN, RANGE, NAMESPACE
	private SemanticMatrixType typeOfEntry; //This holds the type of the matrix that this entry belongs to
	//Directionality
	private CanonicalModelConstruct FROM; 
	private CanonicalModelConstruct TO;
	//The value(s) of each cell are now represented as a Set that does not allow duplicates
	private Set<BooleanVariables> cellValue = null;
	//String to hold directionality from row > column
	private String dirSymbol="";
	
	/*Constructor0*/
	public SemanticMatrixEntry() {
		this.cellValue = new HashSet<BooleanVariables>();
		this.typeOfEntry = null;
		this.FROM = null;
		this.TO = null;
	}
	
	/*Constructor1: Hold From/To contructs*/
	public SemanticMatrixEntry(CanonicalModelConstruct from, CanonicalModelConstruct to) {
		this.cellValue = new HashSet<BooleanVariables>();
		this.typeOfEntry = null;
		this.FROM = from;
		this.TO = to;
	}

	public SemanticMatrixType getTypeOfEntry() {
		return typeOfEntry;
	}

	public void setTypeOfEntry(SemanticMatrixType t) {
		this.typeOfEntry = t;
	}	
	
	public String getDirSymbol() {
		return dirSymbol;
	}

	public void setDirSymbol(String dirSymbol) {
		this.dirSymbol = dirSymbol;
	}
	
	/**
	 * Method that adds a BooleanVariable when discovered to the collection of
	 * Boolean evidences
	 * @param e
	 */
	public void addCellValueToList(BooleanVariables e) {
		this.cellValue.add(e);
	}
	
	/***
	 * @return - a collection of BooleanVariables discovered for the pair
	 * identified by the cell. The collection does not allow duplicate evidence.
	 * Use this method to get the value(s) of the cell
	 */
	public Set<BooleanVariables> getCellValue() {
		return this.cellValue;
	}	
	/**
	 * Check whether the list that holds BooleanVariables is empty or not
	 * @return
	 */
	public boolean isCellValueListEmpty() {
			if (this.cellValue.isEmpty()) {				
				return true;
			} else {
				return false;
			}
	}	
	
	public CanonicalModelConstruct getFROM() {
		return this.FROM;
	}

	public void setFROM(CanonicalModelConstruct from) {
		this.FROM = from;
	}

	public CanonicalModelConstruct getTO() {
		return this.TO;
	}

	public void setTO(CanonicalModelConstruct to) {
		this.TO = to;
	}
	
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[ From: " + this.FROM + " | To: " + this.TO);
		stringBuilder.append("  | Cell value: " + this.cellValue + " | Type: " + this.typeOfEntry + " ]");		
		return stringBuilder.toString();		
	}
}//end SemanticMatrixEntry
