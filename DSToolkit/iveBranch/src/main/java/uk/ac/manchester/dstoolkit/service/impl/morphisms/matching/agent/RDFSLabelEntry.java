package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;

import com.hp.hpl.jena.rdf.model.Statement;

/***
 * This class is used by the RDFSLabelMatcherService
 * 
 * @author klitos
 *
 */
public class RDFSLabelEntry {

	static Logger logger = Logger.getLogger(RDFSLabelEntry.class);
	
	private CanonicalModelConstruct construct = null;
	private int indexOfConstruct;
	private Statement rdfsLabel = null;
	
	public RDFSLabelEntry(CanonicalModelConstruct c, int index, Statement label) {
		construct = c;
		indexOfConstruct = index;
		rdfsLabel = label;
	}//end constructor
	
	/**
	 * Return the CanonicalModelConstruct that has this rdfs:label
	 * @return CanonicalModelConstruct
	 */
	public CanonicalModelConstruct getConstruct() {
		return this.construct;
	}
	
	/**
	 * Get the index of the original ArrayList for this construct 
	 * @return - int the index of this construct in the original ArrayList
	 */
	public int getIndexOfConstruct() {
		return this.indexOfConstruct;
	}	
	
	/**
	 * Return rdfs:label as a statement
	 * @return - Statement
	 */
	public Statement getRDFSLabel() {
		return this.rdfsLabel;
	}	
	
	/**
	 * Return the language of this rdfs:type predicate
	 * @return String - the language of this rdfs:label
	 *  	   null - means that this construct does not have a language for rdfs:label predicate
	 */
	public String getLanguage() {
		if (rdfsLabel.getObject().isLiteral()) {
			return this.rdfsLabel.getLanguage(); 
		}
		return null;
	}//end getLanguage	
	
	/**
	 * Return the object value of this rdfs:label as a String, otherwise return null.
	 * @return String - the object value of this rdfs:label
	 *  	   null - means that this construct does not have an rdfs:label predicate
	 */
	public String getValueOfRDFSLabel() {
		if (rdfsLabel.getObject().isLiteral()) {
			return this.rdfsLabel.getLanguage(); 
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((construct == null) ? 0 : construct.hashCode());
		result = prime * result + indexOfConstruct;
		result = prime * result
				+ ((rdfsLabel == null) ? 0 : rdfsLabel.hashCode());
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
		RDFSLabelEntry other = (RDFSLabelEntry) obj;
		if (construct == null) {
			if (other.construct != null)
				return false;
		} else if (!construct.equals(other.construct))
			return false;
		if (indexOfConstruct != other.indexOfConstruct)
			return false;
		if (rdfsLabel == null) {
			if (other.rdfsLabel != null)
				return false;
		} else if (!rdfsLabel.equals(other.rdfsLabel))
			return false;
		return true;
	}	
}//end Class
