package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.StringBasedMatcherService;

public abstract class StringBasedMatcherServiceImpl extends ConstructBasedMatcherServiceImpl implements StringBasedMatcherService {

	//public abstract Matching match(String string1, String string2, Map<ControlParameterType, ControlParameter> controlParameters);

	//public abstract List<Matching> match(List<String> strings1, List<String> strings2, Map<ControlParameterType, ControlParameter> controlParameters);

	
	protected abstract float match(String string1, String string2);

	@Override
	public abstract float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2);
}//end StringBasedMatcherServiceImpl
