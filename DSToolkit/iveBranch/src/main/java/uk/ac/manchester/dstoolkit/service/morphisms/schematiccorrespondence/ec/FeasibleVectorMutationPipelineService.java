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
public interface FeasibleVectorMutationPipelineService {

	/* (non-Javadoc)
	 * @see ec.Prototype#defaultBase()
	 */
	//we have to specify a default base even though it's never used
	public abstract Parameter defaultBase();

	/* (non-Javadoc)
	 * @see ec.BreedingPipeline#numSources()
	 */

	//Return 1 -- we use only one source
	public abstract int numSources();

	/* (non-Javadoc)
	 * @see ec.BreedingSource#produce(int, int, int, int, ec.Individual[], ec.EvolutionState, int)
	 */
	//We're supposed to create at most _max_ and at least _min_ individuals, drawn from our source and mutated, and stick them into slots
	//in inds[] starting with the slot inds[start]. We do this by telling our source to stick those individuals into inds[] and then mutating
	//them right there. produce(...) returns the number of individuals actually put into inds[]
	public abstract int produce(final int min, final int max, final int start, final int subpopulation, final Individual[] inds,
			final EvolutionState state, final int thread);

}