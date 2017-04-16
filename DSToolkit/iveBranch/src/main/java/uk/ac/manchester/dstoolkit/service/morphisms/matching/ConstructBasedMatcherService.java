package uk.ac.manchester.dstoolkit.service.morphisms.matching;

import java.util.List;
import java.util.Map;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;

public interface ConstructBasedMatcherService extends MatcherService {

	//public float[] runChildMatchers(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2);

	//public float[][][] runChildMatchers(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2);

	public Matching match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2,
			Map<ControlParameterType, ControlParameter> controlParameters);
	
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2);

	public List<Matching> match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2,
			Map<ControlParameterType, ControlParameter> controlParameters);
	
}