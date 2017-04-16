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
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;

@Scope("prototype")
@Service
public class BottomUpPropagationMatcherServiceImpl extends PropagationMatcherServiceImpl {

	static Logger logger = Logger.getLogger(BottomUpPropagationMatcherServiceImpl.class);

	@Autowired
	private MatchingProducerService matchingProducerService;

	private List<CanonicalModelConstruct> constructs1;
	private List<CanonicalModelConstruct> constructs2;

	//TODO refactor

	public BottomUpPropagationMatcherServiceImpl() {
		logger.debug("in BottomUpPropagationMatcherServiceImpl");
		this.setName("bottomUpPropagationMatcher");
		this.setMatcherType(MatcherType.BOTTOM_UP);
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
		logger.error("not implemented ... returning matching with 0 as similarity score");
		// TODO Auto-generated method stub
		return 0.0f;
	}

	@Override
	public List<Matching> match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		float[][] simMatrix = this.match(constructs1, constructs2);
		return matchingProducerService.produceMatches(simMatrix, constructs1, constructs2, controlParameters, this);
	}

	@Override
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in match");
		logger.debug("constructs1: " + constructs1);
		logger.debug("constructs2: " + constructs2);

		//TODO this is same code as in TopDownPropagationMatcherService - sort this
		this.constructs1 = constructs1;
		this.constructs2 = constructs2;

		float[][][] simCube = super.runChildMatchers(constructs1, constructs2);

		List<CanonicalModelConstruct> leaves1 = getLeaves(constructs1);
		List<CanonicalModelConstruct> leaves2 = getLeaves(constructs2);

		for (int i = 0; i < simCube.length; i++) {
			simCube[i] = computeRecusiveBottomUp(leaves1, leaves2, simCube[i]);
		}

		float[][] simMatrix = super.aggregate(simCube);
		simMatrix = super.select(simMatrix);

		return simMatrix;
	}

	private List<CanonicalModelConstruct> getLeaves(List<CanonicalModelConstruct> inputConstructs) {
		logger.debug("in getLeaves");
		ArrayList<CanonicalModelConstruct> output = new ArrayList<CanonicalModelConstruct>();

		for (CanonicalModelConstruct inputConstruct : inputConstructs) {
			if (inputConstruct.getTypeOfConstruct().equals(ConstructType.SUPER_LEXICAL)) {
				output.add(inputConstruct);
			} else if (inputConstruct.getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT)) {
				SuperAbstract saInputConstruct = (SuperAbstract) inputConstruct;
				if (((saInputConstruct.getSuperLexicals() == null) || (saInputConstruct.getSuperLexicals().size() == 0))
						&& ((saInputConstruct.getChildSuperAbstracts() == null) || (saInputConstruct.getChildSuperAbstracts().size() == 0))) {
					output.add(inputConstruct);
				}
			}
		}

		return output;
	}

	private float[][] computeRecusiveBottomUp(List<CanonicalModelConstruct> children1, List<CanonicalModelConstruct> children2, float[][] simMatrix) {
		logger.debug("in computeRecusiveBottomUp");
		for (CanonicalModelConstruct childConstruct1 : children1) {
			for (CanonicalModelConstruct childConstruct2 : children2) {
				simMatrix = computeParentsSimilarity(childConstruct1, childConstruct2, simMatrix);
			}
		}

		List<CanonicalModelConstruct> parents1 = getParents(children1);
		List<CanonicalModelConstruct> parents2 = getParents(children2);

		if ((parents1.size() == 0) || (parents2.size() == 0)) {
			return simMatrix;
		}

		simMatrix = computeRecusiveBottomUp(parents1, parents2, simMatrix);

		return simMatrix;
	}

	private float[][] computeParentsSimilarity(CanonicalModelConstruct childConstruct1, CanonicalModelConstruct childConstruct2, float[][] simMatrix) {
		logger.debug("in computeParentsSimilarity");
		//TOOD add more logoutputs
		CanonicalModelConstruct parent1 = getParent(childConstruct1);
		CanonicalModelConstruct parent2 = getParent(childConstruct2);

		if ((parent1 == null) || (parent2 == null)) {
			return simMatrix;
		}

		List<CanonicalModelConstruct> childrenOfParent1 = getChildren(parent1);
		List<CanonicalModelConstruct> childrenOfParent2 = getChildren(parent2);

		float simSum = 0.0F;
		for (CanonicalModelConstruct child1 : childrenOfParent1) {
			int index1 = constructs1.indexOf(child1);
			float maxSim = 0.0F;
			for (CanonicalModelConstruct child2 : childrenOfParent2) {
				int index2 = constructs2.indexOf(child2);
				if (simMatrix[index1][index2] > maxSim) {
					maxSim = simMatrix[index1][index2];
				}
			}
			simSum += maxSim;
		}

		for (CanonicalModelConstruct child2 : childrenOfParent2) {
			float maxSim = 0.0F;
			int index2 = constructs2.indexOf(child2);
			for (CanonicalModelConstruct child1 : childrenOfParent1) {
				int index1 = constructs1.indexOf(child1);
				if (simMatrix[index1][index2] > maxSim) {
					maxSim = simMatrix[index1][index2];
				}
			}
			simSum += maxSim;
		}

		float sim = simSum / (childrenOfParent1.size() + childrenOfParent2.size());

		int parent1Index = constructs1.indexOf(parent1);
		int parent2Index = constructs2.indexOf(parent2);

		if (sim > simMatrix[parent1Index][parent2Index]) {
			simMatrix[parent1Index][parent2Index] = sim;

		}

		return simMatrix;
	}

	private List<CanonicalModelConstruct> getParents(List<CanonicalModelConstruct> inputConstructs) {
		logger.debug("in getParents");
		ArrayList<CanonicalModelConstruct> output = new ArrayList<CanonicalModelConstruct>();

		for (CanonicalModelConstruct inputConstruct : inputConstructs) {
			CanonicalModelConstruct parent = getParent(inputConstruct);
			if ((parent != null) && (!output.contains(parent)))
				output.add(parent);
		}

		return output;
	}

	private CanonicalModelConstruct getParent(CanonicalModelConstruct inputConstruct) {
		logger.debug("in getParent");
		logger.debug("inputConstruct: " + inputConstruct);
		CanonicalModelConstruct output = null;

		CanonicalModelConstruct item = inputConstruct;

		if (item.getTypeOfConstruct().equals(ConstructType.SUPER_LEXICAL)) {
			output = ((SuperLexical) item).getParentSuperAbstract();
		} else if (item.getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT)) {
			if (((SuperAbstract) item).getParentSuperAbstract() != null) {
				output = ((SuperAbstract) item).getParentSuperAbstract();
			}
		}

		return output;
	}

}
