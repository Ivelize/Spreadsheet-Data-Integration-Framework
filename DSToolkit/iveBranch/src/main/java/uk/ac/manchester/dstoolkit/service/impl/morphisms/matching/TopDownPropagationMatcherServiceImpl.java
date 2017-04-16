package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;

@Scope("prototype")
@Service
public class TopDownPropagationMatcherServiceImpl extends PropagationMatcherServiceImpl {

	private static Logger logger = Logger.getLogger(TopDownPropagationMatcherServiceImpl.class);

	@Autowired
	private MatchingProducerService matchingProducerService;

	private List<CanonicalModelConstruct> constructs1;
	private List<CanonicalModelConstruct> constructs2;

	public TopDownPropagationMatcherServiceImpl() {
		logger.debug("in TopDownPropagationMatcherServiceImpl");
		this.setName("topDownPropagationMatcher");
		this.setMatcherType(MatcherType.TOP_DOWN);
	}

	@Override
	public Matching match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		float similarity = this.match(construct1, construct2);
		return matchingProducerService.produceSingleMatching(similarity, construct1, construct2, controlParameters, this);
	}

	@Override
	protected float match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2) {
		logger.debug("in protected match");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		logger.debug("construct1.getName: " + construct1.getName());
		logger.debug("construct2.getName: " + construct2.getName());
		logger.error("not implemented, returns 0 as similarity score");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Matching> match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		float[][] simMatrix = this.match(constructs1, constructs2);
		return matchingProducerService.produceMatches(simMatrix, constructs1, constructs2, controlParameters, this);
	}

	@Override
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in protected match");

		this.constructs1 = constructs1;
		this.constructs2 = constructs2;

		float[][][] simCube = super.runChildMatchers(constructs1, constructs2);

		List<CanonicalModelConstruct> parents1 = getTopParents(constructs1);
		List<CanonicalModelConstruct> parents2 = getTopParents(constructs2);

		for (int i = 0; i < simCube.length; i++) {
			simCube[i] = computeRecursiveTopDown(parents1, parents2, simCube[i]);
		}

		float[][] simMatrix = super.aggregate(simCube);
		simMatrix = super.select(simMatrix);

		return simMatrix;
	}

	private List<CanonicalModelConstruct> getTopParents(List<CanonicalModelConstruct> inputConstructs) {
		logger.debug("in getTopParents");
		ArrayList<CanonicalModelConstruct> output = new ArrayList<CanonicalModelConstruct>();

		for (CanonicalModelConstruct inputConstruct : inputConstructs) {
			logger.debug("inputConstruct: " + inputConstruct);
			if (inputConstruct.getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT)) {
				if (((SuperAbstract) inputConstruct).getParentSuperAbstract() == null) {
					output.add(inputConstruct);
				}
			}
		}
		return output;
	}

	private float[][] computeRecursiveTopDown(List<CanonicalModelConstruct> parents1, List<CanonicalModelConstruct> parents2, float[][] simMatrix) {
		logger.debug("in computeRecursiveTopDown");
		for (CanonicalModelConstruct parent1 : parents1) {
			for (CanonicalModelConstruct parent2 : parents2) {
				logger.debug("parent1: " + parent1);
				logger.debug("parent2: " + parent2);
				simMatrix = computeChildrenSimilarity(parent1, parent2, simMatrix);
			}
		}

		List<CanonicalModelConstruct> children1 = getChildren(parents1);
		List<CanonicalModelConstruct> children2 = getChildren(parents2);

		simMatrix = computeRecursiveTopDown(children1, children2, simMatrix);

		return simMatrix;
	}

	private float[][] computeChildrenSimilarity(CanonicalModelConstruct parent1, CanonicalModelConstruct parent2, float[][] simMatrix) {
		logger.debug("in computeChildrenSimilarity");
		List<CanonicalModelConstruct> childrenOfParent1 = getChildren(parent1);
		List<CanonicalModelConstruct> childrenOfParent2 = getChildren(parent2);

		int parent1Index = constructs1.indexOf(parent1);
		int parent2Index = constructs2.indexOf(parent2);

		for (CanonicalModelConstruct child1 : childrenOfParent1) {
			int child1Index = constructs1.indexOf(child1);
			for (CanonicalModelConstruct child2 : childrenOfParent2) {
				logger.debug("child1: " + child1);
				logger.debug("child2: " + child2);
				int child2Index = constructs2.indexOf(child2);
				simMatrix[child1Index][child2Index] = simMatrix[parent1Index][parent2Index];
			}
		}
		return simMatrix;
	}

	private List<CanonicalModelConstruct> getChildren(List<CanonicalModelConstruct> parents) {
		logger.debug("in getChildren");
		List<CanonicalModelConstruct> output = new ArrayList<CanonicalModelConstruct>();
		for (CanonicalModelConstruct parent : parents)
			output.addAll(getChildren(parent));
		return output;
	}

}
