package uk.ac.manchester.dstoolkit.service.util.training;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;

public interface GenerateExpectationMatrix {
	
	public SemanticMatrix generateExpectationMatrix(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs);
	
	public SemanticMatrix generateExpectationMatrixFromModel(List<CanonicalModelConstruct> sourceConstructs,
															 List<CanonicalModelConstruct> targetConstructs,
															 String alignModelURI);
		
}
