/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util;

import java.util.List;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;

/**
 * @author chedeler
 *
 */
public interface PairsOfEntitySetsGeneratorService {

	public abstract Set<PairOfEntitySets> generatePairsOfSourceAndTargetEntitySetsForELRGenotypeBitVectorIndividual(
			ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual, List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts);
}