/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * @author chedeler
 *
 */
public interface FeasibleVectorCrossoverPipelineService {

	/* (non-Javadoc)
	 * @see ec.Prototype#defaultBase()
	 */
	//we have to specify a default base even though it's never used
	public abstract Parameter defaultBase();

	/* (non-Javadoc)
	 * @see ec.BreedingPipeline#numSources()
	 */

	//Return 2 -- we use only one source
	public abstract int numSources();

	/* (non-Javadoc)
	 * @see ec.BreedingSource#produce(int, int, int, int, ec.Individual[], ec.EvolutionState, int)
	 */
	public abstract int produce(int min, int max, int start, int subpopulation, Individual[] inds, EvolutionState state, int thread);

	public Individual[] getParents();

	public void setParents(Individual[] parents);
}