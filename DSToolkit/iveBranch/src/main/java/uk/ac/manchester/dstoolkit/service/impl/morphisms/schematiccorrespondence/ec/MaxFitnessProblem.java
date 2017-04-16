/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELREntityLevelRelationship;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.DecoderService;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.BitVectorIndividual;

/**
 * @author chedeler
 *
 */
//TODO think about this ... service ... ? if yes, potentially rename accordingly
@Scope("prototype")
@Service
//(value = "maxFitnessProblem")
@Configurable(autowire = Autowire.BY_NAME)
//, dependencyCheck = true)
public class MaxFitnessProblem extends Problem implements SimpleProblemForm {

	private static Logger logger = Logger.getLogger(MaxFitnessProblem.class);

	@Autowired
	@Qualifier("decoderService")
	private DecoderService decoderService;

	public MaxFitnessProblem() {
		logger.debug("in MaxFitnessProblem");
		logger.debug("decoderService: " + decoderService);
	}

	/* (non-Javadoc)
	 * @see ec.simple.SimpleProblemForm#evaluate(ec.EvolutionState, ec.Individual, int, int)
	 */
	public void evaluate(EvolutionState state, Individual individual, int subpopulation, int threadnum) {
		logger.debug("in evaluate");
		logger.debug("decoderService: " + decoderService);
		if (decoderService != null)
			logger.debug("decoderService.getChromosome(): " + decoderService.getChromosome());
		logger.debug("state: " + state);
		logger.debug("state.generation: " + state.generation);
		logger.debug("individual: " + individual);
		if (individual != null)
			logger.debug("ind.genotypeToStringForHumans(): " + individual.genotypeToStringForHumans());
		logger.debug("subpopulation: " + subpopulation);
		logger.debug("threadnum: " + threadnum);
		if (individual != null) {
			logger.debug("ind.birthday: " + individual.birthday);
			logger.debug("ind.count: " + individual.count);
			if (individual.evaluated) {
				logger.debug("ind.evaluated == true: " + individual.evaluated);
				logger.debug("ind.fitness.fitness(): " + individual.fitness.fitness());
				logger.debug("removed return");
				//return;
			} else
				logger.debug("ind.evaluated == false: " + individual.evaluated);
			if (!(individual instanceof BitVectorIndividual)) {
				logger.error("ind isn't a BitVectorIndividual -- abort, individual: " + individual);
				state.output.fatal("Individual isn't a BitVectorIndividual", null);
			}
		}
		logger.debug("individual: " + individual);
		if (individual != null) {
			logger.debug("ind.genotypeToStringForHumans(): " + individual.genotypeToStringForHumans());
			ELRGenotypeBitVectorIndividual bvIndividual = (ELRGenotypeBitVectorIndividual) individual;
			logger.debug("bvIndividual.genotypeToStringForHumans(): " + bvIndividual.genotypeToStringForHumans());

			ELRChromosome chromosome = decoderService.getChromosome();
			logger.debug("chromosome: " + chromosome);

			logger.debug("bvInd.getElrPhenotype(): " + bvIndividual.getElrPhenotype());

			//float fitness = 0f;

			float _fitness = 0f;

			//logger.debug("before decoding genotype into phenotype");
			//ELRPhenotype phenotype = decoderService.decode(bvInd);
			ELRPhenotype phenotype = bvIndividual.getElrPhenotype();
			logger.debug("phenotype: " + phenotype);
			logger.debug("phenotype.getElrs().size(): " + phenotype.getElrs().size());
			if (phenotype != null) {
				for (ELREntityLevelRelationship elr : phenotype.getElrs()) {
					logger.debug("elr: " + elr);

					for (SuperAbstract sa1 : elr.getSourceEntitySet())
						logger.debug("sa1: " + sa1);
					for (SuperAbstract sa2 : elr.getTargetEntitySet())
						logger.debug("sa2: " + sa2);

					double similarityScore = elr
							.calculateCosineSimilarityOfSourceAndTargetVectorCombinationsAndIdentifyCombinationWithHighestSimilarity();
					logger.debug("similarityScore: " + similarityScore);
				}
				double fitnessDouble = phenotype.calculateFitnessOfPhenotype();
				logger.debug("fitnessDouble: " + fitnessDouble);

				if (!(bvIndividual.fitness instanceof SimpleFitness)) {
					logger.error("fitness of bvInd isn't SimpleFitness -- abort, bvIndividual.fitness: " + bvIndividual.fitness);
					state.output.fatal("Fitness of bvIndividual isn't SimpleFitness", null);
				}
				_fitness = (float) fitnessDouble;
			}

			logger.debug("bvInd.genotypeToStringForHumans(): " + bvIndividual.genotypeToStringForHumans());
			logger.debug("_fitness: " + _fitness);
			boolean _isIdeal = false;
			logger.debug("_isIdeal: " + _isIdeal);
			((SimpleFitness) bvIndividual.fitness).setFitness(state, _fitness, _isIdeal);
			bvIndividual.evaluated = true;
			logger.debug("bvIndividual.evaluated: " + bvIndividual.evaluated);
		}
		logger.debug("decoderService: " + decoderService);
	}

}
