package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

/****
 * 
 * 
 * 
 * Matcher Type: Combined Matcher
 * Working on: Structure level 
 * 
 * @author chedeler
 * @author klitos
 *
 */

@Scope("prototype")
@Service
public class StatisticsMatcherServiceImpl extends ConstructBasedMatcherServiceImpl {

	private static Logger logger = Logger.getLogger(StatisticsMatcherServiceImpl.class);

	public StatisticsMatcherServiceImpl() {
		logger.debug("in StatisticsMatcherServiceImpl");
		this.setName("statisticsMatcher");
		this.setMatcherType(MatcherType.STATISTICS);
	}

	@Override
	public float match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2) {
		logger.debug("in match");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		logger.debug("construct1.getName: " + construct1.getName());
		logger.debug("construct2.getName: " + construct2.getName());
		float sim = 0.0F;

		if ((construct1.getTypeOfConstruct().equals(ConstructType.SUPER_LEXICAL))
				&& (construct2.getTypeOfConstruct().equals(ConstructType.SUPER_LEXICAL)) && (getLevel(construct1) == getLevel(construct2))) {
			sim = 1.0F;
		}

		if ((construct1.getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT))
				&& (construct2.getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT)) && (getLevel(construct1) == getLevel(construct2))) {
			sim = 1.0F;
		}

		logger.debug("sim: " + sim);
		return sim;
	}

	@Override
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in match");
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
		//TODO sort out selection and aggregation
		return simMatrix;
	}

	private int getLevel(CanonicalModelConstruct construct) {
		logger.debug("in getLevel");
		logger.debug("construct: " + construct);
		int level = 0;
		if (construct.getTypeOfConstruct().equals(ConstructType.SUPER_LEXICAL)) {
			level++;
			SuperAbstract superAbstract = ((SuperLexical) construct).getParentSuperAbstract();
			while (superAbstract.getParentSuperAbstract() != null) {
				level++;
				superAbstract = superAbstract.getParentSuperAbstract();
			}
		} else if (construct.getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT)) {
			SuperAbstract superAbstract = (SuperAbstract) construct;
			while (superAbstract.getParentSuperAbstract() != null) {
				level++;
				superAbstract = superAbstract.getParentSuperAbstract();
			}
		}
		logger.debug("level: " + level);
		return level;
	}

}
