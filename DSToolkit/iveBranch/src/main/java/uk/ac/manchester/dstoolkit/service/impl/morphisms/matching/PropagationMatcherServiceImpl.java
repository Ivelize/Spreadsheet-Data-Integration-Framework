package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.PropagationMatcherService;

public abstract class PropagationMatcherServiceImpl extends ConstructBasedMatcherServiceImpl implements PropagationMatcherService {

	private static Logger logger = Logger.getLogger(PropagationMatcherServiceImpl.class);

	@Override
	public abstract Matching match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2,
			Map<ControlParameterType, ControlParameter> controlParameters);

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.PropagationMatcherService#match(java.util.List, java.util.List)
	 */
	@Override
	public abstract List<Matching> match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2,
			Map<ControlParameterType, ControlParameter> controlParameters);

	@Override
	protected abstract float match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2);

	@Override
	public abstract float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2);

	protected List<CanonicalModelConstruct> getChildren(CanonicalModelConstruct parent) {
		logger.debug("in getChildren");
		logger.debug("parent: " + parent);
		List<CanonicalModelConstruct> output = new ArrayList<CanonicalModelConstruct>();

		if ((parent).getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT)) {
			SuperAbstract saParent = (SuperAbstract) parent;
			if (saParent.getChildSuperAbstracts() != null)
				output.addAll(saParent.getChildSuperAbstracts());
			if (saParent.getSuperLexicals() != null)
				output.addAll(saParent.getSuperLexicals());
		}
		return output;
	}
}
