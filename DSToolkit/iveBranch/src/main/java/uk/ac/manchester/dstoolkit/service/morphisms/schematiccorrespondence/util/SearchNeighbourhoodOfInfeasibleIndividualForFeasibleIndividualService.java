/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;
import ec.EvolutionState;

/**
 * @author chedeler
 *
 */
public interface SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService {

	public abstract ELRGenotypeBitVectorIndividual searchForFeasibleIndividual(ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual,
			ELRChromosome elrChromosome, EvolutionState state, int thread);

}