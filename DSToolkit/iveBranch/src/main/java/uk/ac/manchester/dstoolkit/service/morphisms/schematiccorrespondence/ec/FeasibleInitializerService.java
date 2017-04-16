/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec;

import ec.EvolutionState;
import ec.Population;

/**
 * @author chedeler
 *
 */
public interface FeasibleInitializerService {

	public abstract Population initialPopulation(EvolutionState state, int thread);

}