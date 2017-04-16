/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.FeasibilityCheckerService;

/**
 * @author chedeler
 *
 */
@Service(value = "feasibilityCheckerService")
public class FeasibilityCheckerServiceImpl implements FeasibilityCheckerService {

	private static Logger logger = Logger.getLogger(FeasibilityCheckerServiceImpl.class);

	public boolean allowsGenerationOfFeasiblePhenotype(Set<PairOfEntitySets> pairsOfEntitySets,
			Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas, Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas,
			Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap) {
		logger.debug("in allowsGenerationOfFeasiblePhenotype");
		logger.debug("this: " + this);
		logger.debug("pairsOfEntitySets: " + pairsOfEntitySets);
		logger.debug("matchedSuperAbstractsInSourceSchemas: " + matchedSuperAbstractsInSourceSchemas);
		logger.debug("matchedSuperAbstractsInTargetSchemas: " + matchedSuperAbstractsInTargetSchemas);
		logger.debug("matchedSourceSuperAbstractTargetSuperAbstractsMap: " + matchedSourceSuperAbstractTargetSuperAbstractsMap);
		for (PairOfEntitySets pairOfEntitySets : pairsOfEntitySets) {
			logger.debug("pairOfEntitySets: " + pairOfEntitySets);
			for (SuperAbstract sourceSuperAbstract : pairOfEntitySets.getSourceEntitySet()) {
				logger.debug("sourceSuperAbstract: " + sourceSuperAbstract);
				if (!this.isMatched(sourceSuperAbstract, matchedSuperAbstractsInSourceSchemas, matchedSuperAbstractsInTargetSchemas)) {
					logger.debug("sourceSuperAbstract is not matched - infeasible phenotype, return false");
					return false;
				}
				for (SuperAbstract targetSuperAbstract : pairOfEntitySets.getTargetEntitySet()) {
					logger.debug("targetSuperAbstract: " + targetSuperAbstract);
					if (!this.isMatched(targetSuperAbstract, matchedSuperAbstractsInSourceSchemas, matchedSuperAbstractsInTargetSchemas)) {
						logger.debug("targetSuperAbstract is not matched - infeasible phenotype, return false");
						return false;
					}
					if (!this.areMatchedWithEachOther(sourceSuperAbstract, targetSuperAbstract, matchedSourceSuperAbstractTargetSuperAbstractsMap)) {
						logger.debug("sourceSuperAbstract and targetSuperAbstract aren't matched with each other - infeasible phenotype, return false");
						return false;
					}
				}
			}
		}
		logger.debug("checked all pairsOfEntitySets - still here, so should be feasible phenotype, return true");
		return true;
	}

	//TODO code duplicated from ELRChromosome
	protected boolean isMatched(SuperAbstract superAbstract, Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas,
			Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas) {
		logger.debug("in isMatched");
		logger.debug("this: " + this);
		logger.debug("superAbstract: " + superAbstract);
		logger.debug("matchedSuperAbstractsInSourceSchemas: " + matchedSuperAbstractsInSourceSchemas);
		logger.debug("matchedSuperAbstractsInTargetSchemas: " + matchedSuperAbstractsInTargetSchemas);
		if (matchedSuperAbstractsInSourceSchemas.contains(superAbstract) || matchedSuperAbstractsInTargetSchemas.contains(superAbstract)) {
			logger.debug("return true");
			return true;
		}
		logger.debug("return false");
		return false;
	}

	//TODO code duplicated from ELRChromosome
	protected boolean areMatchedWithEachOther(SuperAbstract superAbstract1, SuperAbstract superAbstract2,
			Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap) {
		logger.debug("in areMatchedWithEachOther");
		logger.debug("this: " + this);
		logger.debug("superAbstract1: " + superAbstract1);
		logger.debug("superAbstract2: " + superAbstract2);
		logger.debug("matchedSourceSuperAbstractTargetSuperAbstractsMap: " + matchedSourceSuperAbstractTargetSuperAbstractsMap);
		if (matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(superAbstract1)) {
			logger.debug("found superAbstract1 in matchedSourceSuperAbstractTargetSuperAbstractsMap");
			return matchedSourceSuperAbstractTargetSuperAbstractsMap.get(superAbstract1).contains(superAbstract2);
		} else if (matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(superAbstract2)) {
			logger.debug("found superAbstract2 in matchedSourceSuperAbstractTargetSuperAbstractsMap");
			return matchedSourceSuperAbstractTargetSuperAbstractsMap.get(superAbstract2).contains(superAbstract1);
		} else
			return false;
	}
}
