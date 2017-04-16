package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;

@Scope("prototype")
@Service
public class NamePathMatcherServiceImpl extends ConstructBasedMatcherServiceImpl {

	private static Logger logger = Logger.getLogger(NamePathMatcherServiceImpl.class);

	public NamePathMatcherServiceImpl() {
		logger.debug("in NamePathMatcherServiceImpl");
		this.setName("namePathMatcher");
		this.setMatcherType(MatcherType.NAMEPATH);
	}

	@Override
	public float match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2) {
		logger.debug("match");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		logger.debug("construct1.getName: " + construct1.getName());
		logger.debug("construct2.getName: " + construct2.getName());

		if (construct1.equals(construct2))
			return 1.0F;
		ArrayList<String> namePath1 = getPath(construct1);
		ArrayList<String> namePath2 = getPath(construct2);
		logger.debug("namePath1: " + namePath1);
		logger.debug("namePath2: " + namePath2);

		int iteration = Math.min(namePath1.size(), namePath2.size());
		float[] simArray = new float[iteration];

		for (int i = 0; i < iteration; i++) {
			float similarity = 0.0F;
			for (MatcherService childMatcher : this.getChildMatchers()) {
				logger.debug("childMatcher: " + childMatcher);
				if (childMatcher instanceof StringBasedMatcherServiceImpl) {
					logger.debug("childMatcher is StringBasedMatcher");
					StringBasedMatcherServiceImpl stringBasedMatcher = (StringBasedMatcherServiceImpl) childMatcher;
					similarity = stringBasedMatcher.match(namePath1.get(i), namePath2.get(i));
					logger.debug("similarity: " + similarity);
					//TODO sort out aggregates, selection etc. if multiple childMatchers
					simArray[i] = similarity;
				} else
					logger.error("childMatcher is not StringBasedMatcher, setting 0.0 as similarity score");
			}
		}

		float sum = 0.0F;
		for (int i = 0; i < iteration; i++) {
			sum += simArray[i];
		}
		logger.debug("sum: " + sum);
		return sum / Math.max(namePath1.size(), namePath2.size());
	}

	@Override
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in match");
		//TODO sort out aggregates, selection etc. if multiple childMatchers
		float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
		for (CanonicalModelConstruct construct1 : constructs1) {
			for (CanonicalModelConstruct construct2 : constructs2) {
				logger.debug("construct1: " + construct1);
				logger.debug("construct2: " + construct2);
				logger.debug("construct1.getName: " + construct1.getName());
				logger.debug("construct2.getName: " + construct2.getName());
				simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)] = this.match(construct1, construct2);
			}
		}
		return simMatrix;
	}

	private ArrayList<String> getPath(CanonicalModelConstruct construct) {
		logger.debug("in getPath");
		logger.debug("construct: " + construct);
		ArrayList<String> path = new ArrayList<String>();

		if (construct.getTypeOfConstruct().equals(ConstructType.SUPER_LEXICAL)) {
			path.add(((SuperLexical) construct).getName());
			SuperAbstract superAbstract = ((SuperLexical) construct).getParentSuperAbstract();
			path.add(superAbstract.getName());

			while (superAbstract.getParentSuperAbstract() != null) {
				superAbstract = superAbstract.getParentSuperAbstract();
				path.add(superAbstract.getName());
			}
		} else if (construct.getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT)) {
			SuperAbstract superAbstract = (SuperAbstract) construct;
			path.add(superAbstract.getName());

			while (superAbstract.getParentSuperAbstract() != null) {
				superAbstract = superAbstract.getParentSuperAbstract();
				path.add(superAbstract.getName());
			}
		}

		logger.debug("path: " + path);
		return path;
	}

}
