/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util;

import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;

/**
 * @author chedeler
 *
 */
public interface FeasibilityCheckerService {

	public abstract boolean allowsGenerationOfFeasiblePhenotype(Set<PairOfEntitySets> pairsOfEntitySets,
			Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas, Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas,
			Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap);

}