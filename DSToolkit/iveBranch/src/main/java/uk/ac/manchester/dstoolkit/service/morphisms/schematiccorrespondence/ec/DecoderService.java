/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec;

import java.util.Set;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;

/**
 * @author chedeler
 *
 */
public interface DecoderService {

	public abstract ELRPhenotype decodeAndGenerateVectorSpaceVectorsAndCalculateWeights(Set<PairOfEntitySets> pairsOfEntitySets, ELRChromosome chromosome);

	public abstract ELRChromosome getChromosome();

	public abstract void setChromosome(ELRChromosome chromosome);
}