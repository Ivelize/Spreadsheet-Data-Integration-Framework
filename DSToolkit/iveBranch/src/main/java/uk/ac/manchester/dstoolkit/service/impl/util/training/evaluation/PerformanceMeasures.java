package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

public abstract class PerformanceMeasures {
	
	static Logger logger = Logger.getLogger(PerformanceMeasures.class);
	
	protected PerformanceErrorTypes mType = null;
	
	protected Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
	
	public abstract float calc(float[][] predictedMatrix,  float[][] observedMatrix);
	public abstract float calc(float[][] predictedMatrix, float[][] observedMatrix, Set<SemanticMatrixCellIndex> indexesSet);

	public PerformanceErrorTypes getmType() {
		return mType;
	}	
	
	public Map<ControlParameterType, ControlParameter> getControlParameters() {
		//unmodifiable because the order of entries in a Set is changing
		return Collections.unmodifiableMap(controlParameters);
	}
	
	public void removeControlParameter(ControlParameter controlParameter) {
		this.controlParameters.remove(controlParameter.getName());
	}
	
	public void addControlParameter(ControlParameter controlParameter) {
		this.controlParameters.put(controlParameter.getName(), controlParameter);
	}
}//end Class
