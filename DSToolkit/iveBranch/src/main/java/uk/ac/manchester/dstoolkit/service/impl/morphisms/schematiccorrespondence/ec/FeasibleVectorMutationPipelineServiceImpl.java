/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELREntityLevelRelationship;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.DecoderService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.FeasibleVectorMutationPipelineService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.FeasibilityCheckerService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.PairsOfEntitySetsGeneratorService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService;
import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import ec.vector.VectorDefaults;
import ec.vector.VectorSpecies;

/**
 * @author chedeler
 *
 */
@Scope("prototype")
@Service
//(value = "feasibleVectorMutationPipelineService")
@Configurable(autowire = Autowire.BY_NAME)
//, dependencyCheck = true)
public class FeasibleVectorMutationPipelineServiceImpl extends BreedingPipeline implements FeasibleVectorMutationPipelineService {

	private static Logger logger = Logger.getLogger(FeasibleVectorMutationPipelineServiceImpl.class);

	@Autowired
	@Qualifier("searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService")
	private SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService;

	@Autowired
	@Qualifier("pairsOfEntitySetsGeneratorService")
	private PairsOfEntitySetsGeneratorService pairsOfEntitySetsGeneratorService;

	@Autowired
	@Qualifier("feasibilityCheckerService")
	private FeasibilityCheckerService feasibilityChecker;

	@Autowired
	@Qualifier("decoderService")
	private DecoderService decoderService;

	//used only for default base
	public static final String P_DSTOOLKIT_INFERCORR_MUTATION = "dstoolkit-inferCorrespondence-mutation";
	public static final int NUM_SOURCES = 1;

	public FeasibleVectorMutationPipelineServiceImpl() {
		logger.debug("in FeasibleVectorMutationPipelineServiceImpl");
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.FeasibleVectorMutationPipelineService#defaultBase()
	 */
	//we have to specify a default base even though it's never used
	public Parameter defaultBase() {
		logger.debug("in defaultBase");
		logger.debug("P_DSTOOLKIT_INFERCORR_MUTATION: " + P_DSTOOLKIT_INFERCORR_MUTATION);
		return VectorDefaults.base().push(P_DSTOOLKIT_INFERCORR_MUTATION);
	}

	/* (non-Javadoc)
	 * @see ec.BreedingPipeline#numSources()
	 */
	@Override
	//Return 1 -- we use only one source
	public int numSources() {
		logger.debug("in numSources");
		logger.debug("NUM_SOURCES: " + NUM_SOURCES);
		return NUM_SOURCES;
	}

	//no need to test as it's just logging information and not doing anything
	//TODO code duplicated in FeasibleInitializerServiceImpl, here, FeasibleVectorCrossoverPipelineServiceImpl
	protected void logGenotypeAndSpeciesOfIndividual(Individual individual) {
		if (individual != null) {
			logger.debug("individual: " + individual.genotypeToStringForHumans());
			VectorSpecies species = (VectorSpecies) individual.species;
			logger.debug("species: " + species);
		}
	}

	//no need to test as it's just logging information and not doing anything
	//TODO code duplicated in FeasibleInitializerServiceImpl, here, FeasibleVectorCrossoverPipelineServiceImpl
	protected void logGenotypeAndSpeciesOfAllIndividuals(Individual[] individuals) {
		for (int i = 0; i < individuals.length; i++) {
			logger.debug("i: " + i);
			logger.debug("individuals[i]: " + individuals[i]);
			if (individuals[i] != null)
				this.logGenotypeAndSpeciesOfIndividual(individuals[i]);
		}
	}

	//no need to test as it's just logging information and not doing anything
	//TODO code duplicated in FeasibleInitializerServiceImpl, here, FeasibleVectorCrossoverPipelineServiceImpl
	protected void logDetailsOfPopulation(Individual[] individuals) {
		int noOfIndividual = 0;
		for (Individual individual : individuals) {
			logger.debug("noOfIndividual: " + noOfIndividual);
			logger.debug("individual: " + individual);
			if (individual != null) {
				logger.debug("individual.genotypeToStringForHumans(): " + individual.genotypeToStringForHumans());
				logger.debug("individial.birthday: " + individual.birthday);
				logger.debug("individual.count: " + Individual.count);
				logger.debug("individual.fitness.fitness(): " + individual.fitness.fitness());
				logger.debug("individual.evaluated: " + individual.evaluated);

				ELRGenotypeBitVectorIndividual elrGenotypeIndividual = (ELRGenotypeBitVectorIndividual) individual;
				ELRPhenotype phenotype = elrGenotypeIndividual.getElrPhenotype();
				logger.debug("phenotype: " + phenotype);
				ELRChromosome chromosome = phenotype.getChromosome();
				logger.debug("chromosome: " + chromosome);

				logger.debug("phenotype.getUnassociatedSourceEntities().size(): " + phenotype.getMatchedButUnassociatedSourceEntities().size());

				for (SuperAbstract matchedButUnassociatedSourceEntity : phenotype.getMatchedButUnassociatedSourceEntities())
					logger.debug("matchedButUnassociatedSourceEntity: " + matchedButUnassociatedSourceEntity);

				logger.debug("phenotype.getMatchedButUnassociatedTargetEntities().size(): "
						+ phenotype.getMatchedButUnassociatedTargetEntities().size());

				for (SuperAbstract matchedButUnassociatedTargetEntity : phenotype.getMatchedButUnassociatedTargetEntities())
					logger.debug("matchedButUnassociatedTargetEntity: " + matchedButUnassociatedTargetEntity);

				logger.debug("phenotype.getElrs().size(): " + phenotype.getElrs().size());
				for (ELREntityLevelRelationship elr : phenotype.getElrs()) {
					logger.debug("elr: " + elr);
					logger.debug("elr.getPhenotype(): " + elr.getPhenotype());
					logger.debug("elr.getNumberOfSourceEntities(): " + elr.getNumberOfSourceEntities());
					for (SuperAbstract sa1 : elr.getSourceEntitySet())
						logger.debug("sa1: " + sa1);
					logger.debug("elr.getNumberOfTargetEntities(): " + elr.getNumberOfTargetEntities());
					for (SuperAbstract sa2 : elr.getTargetEntitySet())
						logger.debug("sa2: " + sa2);

					logger.debug("elr.getSourceVectors(): " + elr.getSourceVectors());
					logger.debug("elr.getSourceVectors().size(): " + elr.getSourceVectors().size());

					for (VectorSpaceVector sourceVector : elr.getSourceVectors()) {
						logger.debug("sourceVector: " + sourceVector);
						logger.debug("sourceVector.getChromosome(): " + sourceVector.getChromosome());
						logger.debug("sourceVector.getConstructsWeightsMap(): " + sourceVector.getConstructsWeightsMap());
						logger.debug("sourceVector.getConstructsWeightsMap().size(): " + sourceVector.getConstructsWeightsMap().size());
						for (CanonicalModelConstruct[] constructsArray : sourceVector.getConstructsWeightsMap().keySet()) {
							logger.debug("constructsArray: " + constructsArray);
							logger.debug("weight: " + sourceVector.getConstructsWeightsMap().get(constructsArray));
							for (CanonicalModelConstruct construct : constructsArray)
								logger.debug("construct: " + construct);
						}
					}

					logger.debug("elr.getTargetVectors(): " + elr.getTargetVectors());
					logger.debug("elr.getTargetVectors().size(): " + elr.getTargetVectors().size());

					for (VectorSpaceVector targetVector : elr.getTargetVectors()) {
						logger.debug("targetVector: " + targetVector);
						logger.debug("targetVector.getChromosome(): " + targetVector.getChromosome());
						logger.debug("targetVector.getConstructsWeightsMap(): " + targetVector.getConstructsWeightsMap());
						logger.debug("targetVector.getConstructsWeightsMap().size(): " + targetVector.getConstructsWeightsMap().size());
						for (CanonicalModelConstruct[] constructsArray : targetVector.getConstructsWeightsMap().keySet()) {
							logger.debug("constructsArray: " + constructsArray);
							logger.debug("weight: " + targetVector.getConstructsWeightsMap().get(constructsArray));
							for (CanonicalModelConstruct construct : constructsArray)
								logger.debug("construct: " + construct);
						}
					}

				}
			}
			noOfIndividual++;
		}
		logger.debug("leaving logDetailsOfInitialPopulation");
	}

	/* (non-Javadoc)
	 * @see ec.BreedingSource#produce(int, int, int, int, ec.Individual[], ec.EvolutionState, int)
	 */
	//We're supposed to create at most _max_ and at least _min_ individuals, drawn from our source and mutated, and stick them into slots
	//in inds[] starting with the slot inds[start]. We do this by telling our source to stick those individuals into inds[] and then mutating
	//them right there. produce(...) returns the number of individuals actually put into inds[]
	@Override
	public int produce(final int min, final int max, final int start, final int subpopulation, final Individual[] individuals,
			final EvolutionState state, final int thread) {
		logger.debug("in produce");
		logger.debug("min: " + min);
		logger.debug("max: " + max);
		logger.debug("start: " + start);
		logger.debug("subpopulation: " + subpopulation);
		logger.debug("individuals: " + individuals);
		logger.debug("state: " + state);
		logger.debug("thread: " + thread);
		logger.debug("searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService: "
				+ searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService);
		logger.debug("pairsOfEntitySetsGeneratorService: " + pairsOfEntitySetsGeneratorService);
		logger.debug("feasibilityChecker: " + feasibilityChecker);
		logger.debug("decoderService: " + decoderService);

		this.logDetailsOfPopulation(individuals);

		//grab individuals from the source and stick them right into inds
		//we'll modify them from there
		logger.debug("before calling produce on sources[0]");
		int n = sources[0].produce(min, max, start, subpopulation, individuals, state, thread);
		logger.debug("after calling produce on sources[0], n: " + n);

		//should we bother to mutate the individuals at all

		logger.debug("likelihood: " + likelihood);

		//TODO Chenjuan decides randomly whether to apply mutation to both two children just produced by crossover, only one of them, 
		//TODO to none of them or to the parents of the two children
		//TODO the approach used here applies mutation randomly to any of the individuals in the generation, this could be the parents or the children
		//TODO or any other individual

		/*
		boolean mutate = state.random[thread].nextBoolean(likelihood);
		logger.debug("mutate: " + mutate);
		if (!mutate) {
			logger.debug("likelihood: " + likelihood);
			logger.debug("false - don't mutate");
			//DON'T produce children from source -- we already did
			return reproduce(n, start, subpopulation, inds, state, thread, false);
		} else {
			logger.debug("likelihood: " + likelihood);
			logger.debug("true - mutate");
		}
		*/
		//clone the individuals if necessary -- if our source is a BreedingPipeline they've already been cloned,
		//but if the sources is a SelectionMethod, the individuals are actual individuals from the previous population
		if (!(sources[0] instanceof BreedingPipeline)) {
			logger.debug("sources[0] isn't instance of BreedingPipeline - clone inds");
			for (int q = start; q < n + start; q++) {
				logger.debug("q");
				individuals[q] = (Individual) (individuals[q].clone());
				logger.debug("individuals[q]: " + individuals[q]);
				if (individuals[q] != null)
					this.logGenotypeAndSpeciesOfIndividual(individuals[q]);
			}
		} else {
			logger.debug("sources[0] is instance of BreedingPipeline ... just grab them ...");
			for (int q = start; q < n + start; q++) {
				logger.debug("q");
				individuals[q] = (individuals[q]);
				logger.debug("individuals[q]: " + individuals[q]);
				if (individuals[q] != null)
					this.logGenotypeAndSpeciesOfIndividual(individuals[q]);
			}
		}
		//Check to make sure that the individuals are BitVectorIndividuals and grab their species. For efficiency's sake, we assume taht all the
		//individuals in inds[] are the same type of individual and that they all share the same common species -- this is a safe assumption because
		//they're all breeding from the same subpopulation.
		if (!(individuals[start] instanceof ELRGenotypeBitVectorIndividual)) {
			logger.error("individuals[start] isn't a BitVectorIndividual - should abort - not doing that at the moment though");//TODO check out abort
			logger.error("subpopulation: " + subpopulation);
			if (individuals[start] != null)
				this.logGenotypeAndSpeciesOfIndividual(individuals[start]);
			//state.output.fatal("FeasibleVectorMutationPipeline didn't get a ELRGenotypeBitVectorIndividual. The offending individual is in subpopulation " + subpopulation + " and it's: " + inds[start]);
		}

		VectorSpecies species = (VectorSpecies) individuals[start].species;
		logger.debug("species: " + species);
		//mutate them
		for (int q = start; q < n + start; q++) {
			logger.debug("q: " + q);
			ELRGenotypeBitVectorIndividual i = (ELRGenotypeBitVectorIndividual) individuals[q];
			logger.debug("i: " + i.genotypeToStringForHumans());
			for (int x = 0; x < i.genome.length; x++) {
				logger.debug("x: " + x);
				logger.debug("mutationProbability: " + species.mutationProbability);
				boolean mutateBit = state.random[thread].nextBoolean(species.mutationProbability);
				logger.debug("mutateBit: " + mutateBit);
				if (mutateBit) {
					logger.debug("mutationProbability");
					logger.debug("mutate - true");
					logger.debug("i.genome[x] before mutating: " + i.genome[x]);
					if (i.genome[x])
						i.genome[x] = false;
					else
						i.genome[x] = true;
					logger.debug("i.genome[x] after mutating: " + i.genome[x]);
					this.logGenotypeAndSpeciesOfIndividual(i);
				}
				//it's a "new" individual, so it's no longer been evaluated
			}
			logger.debug("i: " + i.genotypeToStringForHumans());
			ELRPhenotype phenotype = this.checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt(i, state, thread);
			logger.debug("phenotype: " + phenotype);
			if (phenotype != null)
				i.setElrPhenotype(phenotype);
			i.evaluated = false;
		}

		logger.debug("got mutated population");

		this.logDetailsOfPopulation(individuals);
		logger.debug("n: " + n);
		logger.debug("leaving produce");
		return n;
	}

	//TODO check for code-duplication here, FeasibleInitializerServiceImpl, FeasibleVectorCrossoverPipelineServiceImpl and in searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService.searchForFeasibleIndividual
	protected ELRPhenotype checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt(
			ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual, EvolutionState state, int thread) {
		logger.debug("in checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt");
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		logger.debug("state: " + state);
		logger.debug("thread: " + thread);
		ELRChromosome elrChromosome = decoderService.getChromosome();
		logger.debug("elrChromosome: " + elrChromosome);
		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorService
				.generatePairsOfSourceAndTargetEntitySetsForELRGenotypeBitVectorIndividual(elrGenotypeBitVectorIndividual,
						elrChromosome.getChromosomeOfPairsOfSuperAbstracts());
		logger.debug("pairsOfEntitySets: " + pairsOfEntitySets);
		boolean isFeasible = feasibilityChecker.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets,
				elrChromosome.getMatchedSuperAbstractsInSourceSchemas(), elrChromosome.getMatchedSuperAbstractsInTargetSchemas(),
				elrChromosome.getMatchedSourceSuperAbstractTargetSuperAbstractsMap());
		logger.debug("isFeasible: " + isFeasible);
		if (!isFeasible) {
			logger.debug("elrGenotypeIndividual is not feasible - search neighbourhood for feasible individual");
			elrGenotypeBitVectorIndividual = searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService.searchForFeasibleIndividual(
					elrGenotypeBitVectorIndividual, elrChromosome, state, thread);
			logger.debug("feasible elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		} else
			logger.debug("elrGenotypeIndividual is feasible - keep it");
		logger.debug("should have feasible elrGenotypeIndividual here ... decode, i.e., generate Phenotype");
		ELRPhenotype elrPhenotype = decoderService.decodeAndGenerateVectorSpaceVectorsAndCalculateWeights(pairsOfEntitySets, elrChromosome);
		logger.debug("elrPhenotype: " + elrPhenotype);
		return elrPhenotype;
	}

}
