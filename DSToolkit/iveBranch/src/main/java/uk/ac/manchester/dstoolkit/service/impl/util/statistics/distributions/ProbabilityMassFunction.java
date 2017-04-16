package uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions;

import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ContingencyTable;

/***
 * This class builds a ProbabilityMassFunction (PMF) object for each semantic evidence. The counts that
 * construct the PMF are encapsulated by a Contingency Table.
 * 
 * @author klitos
 */
public class ProbabilityMassFunction extends AbstractProbabilityDistribution {

	//Hold a Boolean variable for this evidence
	BooleanVariables evidenceVariable;
	
	//Hold a reference to the ContingencyTable created for this semantic evidence
	ContingencyTable contingencyTable;	
		
	//Constructor
	public ProbabilityMassFunction(BooleanVariables evidenceVar, ContingencyTable cTable) {
		
		this.evidenceVariable = evidenceVar;
		this.contingencyTable = cTable;		
		
	}//end constructor

	public BooleanVariables getEvidenceVariable() {
		return evidenceVariable;
	}

	public void setEvidenceVariable(BooleanVariables evidenceVariable) {
		this.evidenceVariable = evidenceVariable;
	}

	public ContingencyTable getContingencyTable() {
		return contingencyTable;
	}

	public void setContingencyTable(ContingencyTable contingencyTable) {
		this.contingencyTable = contingencyTable;
	}
}//end class