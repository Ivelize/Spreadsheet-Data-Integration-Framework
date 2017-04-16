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
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.FeasibleVectorCrossoverPipelineService;
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
//(value = "feasibleVectorCrossoverPipelineService")
@Configurable(autowire = Autowire.BY_NAME)
//, dependencyCheck = true)
public class FeasibleVectorCrossoverPipelineServiceImpl extends BreedingPipeline implements FeasibleVectorCrossoverPipelineService {

	private static Logger logger = Logger.getLogger(FeasibleVectorCrossoverPipelineServiceImpl.class);

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
	public static final String P_TOSS = "toss"; //throw away second child - should be false
	public static final String P_DSTOOLKIT_INFERCORR_CROSSOVER = "dstoolkit-inferCorrespondence-crossover";
	public static final int NUM_SOURCES = 2;

	//Should the pipeline discard the second parent after crossing over?
	public boolean tossSecondParent;

	// Temporary holding place for parents
	private Individual parents[];

	public FeasibleVectorCrossoverPipelineServiceImpl() {
		logger.debug("in FeasibleVectorCrossoverPipelineServiceImpl");
		logger.debug("searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService: "
				+ searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService);
		logger.debug("pairsOfEntitySetsGeneratorService: " + pairsOfEntitySetsGeneratorService);
		logger.debug("feasibilityChecker: " + feasibilityChecker);
		logger.debug("decoderService: " + decoderService);
		parents = new ELRGenotypeBitVectorIndividual[2];
		logger.debug("parents: " + parents);
		int i = 0;
		for (Individual indParent : parents) {
			logger.debug("i: " + i + " indParent: " + indParent);
			if (indParent != null)
				this.logGenotypeAndSpeciesOfIndividual(indParent);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.FeasibleVectorCrossoverPipelineService#defaultBase()
	 */
	//we have to specify a default base even though it's never used
	public Parameter defaultBase() {
		logger.debug("in defaultBase");
		logger.debug("P_DSTOOLKIT_INFERCORR_MUTATION: " + P_DSTOOLKIT_INFERCORR_CROSSOVER);
		return VectorDefaults.base().push(P_DSTOOLKIT_INFERCORR_CROSSOVER);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		logger.debug("in setup");
		Parameter def = defaultBase();
		logger.debug("def: " + def);
		tossSecondParent = state.parameters.getBoolean(base.push(P_TOSS), def.push(P_TOSS), false);
		logger.debug("tossSecondParent: " + tossSecondParent);
	}

	//Returns 2 * minimum number of typical individuals produced by any sources, else minimum number if tossSecondParent is true.
	@Override
	public int typicalIndsProduced() {
		logger.debug("in typicalIndsProduced");
		int numberOfIndsProduced = tossSecondParent ? minChildProduction() : minChildProduction() * 2;
		logger.debug("numberOfIndsProduced: " + numberOfIndsProduced);
		return numberOfIndsProduced;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.FeasibleVectorCrossoverPipelineService#getParents()
	 */
	public Individual[] getParents() {
		return parents;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.FeasibleVectorCrossoverPipelineService#setParents(ec.Individual[])
	 */
	public void setParents(Individual[] parents) {
		this.parents = parents;
	}

	/* (non-Javadoc)
	 * @see ec.BreedingPipeline#numSources()
	 */
	//Return 2 -- we use only one source
	@Override
	public int numSources() {
		logger.debug("in numSources");
		logger.debug("NUM_SOURCES: " + NUM_SOURCES);
		return NUM_SOURCES;
	}

	//no need to test as it's just logging information and not doing anything
	//TODO code duplicated in FeasibleInitializerServiceImpl, here, FeasibleVectorMutationPipelineServiceImpl
	protected void logGenotypeAndSpeciesOfIndividual(Individual individual) {
		if (individual != null) {
			logger.debug("individual: " + individual.genotypeToStringForHumans());
			VectorSpecies species = (VectorSpecies) individual.species;
			logger.debug("species: " + species);
		}
	}

	//no need to test as it's just logging information and not doing anything
	//TODO code duplicated in FeasibleInitializerServiceImpl, here, FeasibleVectorMutationPipelineServiceImpl
	protected void logGenotypeAndSpeciesOfAllIndividuals(Individual[] individuals) {
		for (int i = 0; i < individuals.length; i++) {
			logger.debug("i: " + i);
			logger.debug("individuals[i]: " + individuals[i]);
			if (individuals[i] != null)
				this.logGenotypeAndSpeciesOfIndividual(individuals[i]);
		}
	}

	//no need to test as it's just logging information and not doing anything
	//TODO code duplicated in FeasibleInitializerServiceImpl, here, FeasibleVectorMutationPipelineServiceImpl
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
	@Override
	public int produce(int min, int max, int start, int subpopulation, Individual[] individuals, EvolutionState state, int thread) {
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

		// how many individuals should we make?
		int n = typicalIndsProduced();
		logger.debug("n: " + n);
		if (n < min)
			n = min;
		if (n > max)
			n = max;
		logger.debug("n: " + n);

		// should we bother to crossover
		logger.debug("likelihood: " + likelihood);
		boolean crossover = state.random[thread].nextBoolean(likelihood);
		logger.debug("crossover: " + crossover);
		//boolean crossover = state.random[thread].nextBoolean(species.mutationProbability);
		//logger.debug("crossover: " + crossover);
		if (!crossover) {
			logger.debug("no crossover - return reproduce");
			return reproduce(n, start, subpopulation, individuals, state, thread, true); // DO produce children from source -- we've not done so already
		}

		logger.debug("crossover");
		//Check to make sure that the individuals are BitVectorIndividuals and grab their species. For efficiency's sake, we assume that all the
		//individuals in individuals[] are the same type of individual and that they all share the same common species -- this is a safe assumption because
		//they're all breeding from the same subpopulation.
		logger.debug("start: " + start);
		if (!(individuals[start] instanceof ELRGenotypeBitVectorIndividual)) {
			logger.error("inds[start] isn't a ELRGenotypeBitVectorIndividual - should abort - not doing that at the moment though"); //TODO check out abort
			logger.error("subpopulation: " + subpopulation);
			if (individuals[start] != null)
				this.logGenotypeAndSpeciesOfIndividual(individuals[start]);
			//state.output.fatal("FeasibleVectorCrossoverPipeline didn't get a ELRGenotypeBitVectorIndividual. The offending individual is in subpopulation " + subpopulation + " and it's: " + individuals[start]);
		}

		this.logGenotypeAndSpeciesOfAllIndividuals(individuals);

		for (int q = start; q < n + start; /* no increment */) { // keep on going until we're filled up
			logger.debug("q");
			logger.debug("inds[q]: " + individuals[q]);
			if (individuals[q] != null)
				this.logGenotypeAndSpeciesOfIndividual(individuals[q]);

			// grab two individuals from our sources
			if (sources[0] == sources[1]) // grab from the same source
			{
				logger.debug("sources[0] == sources[1] - grab two individuals from the same source");
				sources[0].produce(2, 2, 0, subpopulation, parents, state, thread);
				if (!(sources[0] instanceof BreedingPipeline)) // it's a selection method probably
				{
					logger.debug("sources not BreedingPipelines");
					parents[0] = (ELRGenotypeBitVectorIndividual) (parents[0].clone());
					parents[1] = (ELRGenotypeBitVectorIndividual) (parents[1].clone());
					logger.debug("parents[0]: " + parents[0]);
					if (parents[0] != null)
						this.logGenotypeAndSpeciesOfIndividual(parents[0]);
					logger.debug("parents[1]: " + parents[1]);
					if (parents[1] != null)
						this.logGenotypeAndSpeciesOfIndividual(parents[1]);
				} else {
					logger.debug("sources are BreedingPipelines");
				}
			} else // grab from different sources
			{
				logger.debug("sources[0] != sources[1] - grab two individuals from the same source");
				sources[0].produce(1, 1, 0, subpopulation, parents, state, thread);
				sources[1].produce(1, 1, 1, subpopulation, parents, state, thread);
				if (!(sources[0] instanceof BreedingPipeline)) // it's a selection method probably 
				{
					logger.debug("sources[0] not BreedingPipeline");
					parents[0] = (ELRGenotypeBitVectorIndividual) (parents[0].clone());
					logger.debug("parents[0]: " + parents[0]);
					if (parents[0] != null)
						this.logGenotypeAndSpeciesOfIndividual(parents[0]);
				} else
					logger.debug("sources[0] is BreedingPipeline");
				if (!(sources[1] instanceof BreedingPipeline)) // it's a selection method probably
				{
					logger.debug("sources[1] not BreedingPipeline");
					parents[1] = (ELRGenotypeBitVectorIndividual) (parents[1].clone());
					logger.debug("parents[1]: " + parents[1]);
					if (parents[1] != null)
						this.logGenotypeAndSpeciesOfIndividual(parents[1]);
				} else
					logger.debug("sources[1] is BreedingPipeline");
			}

			if (individuals[q] != null)
				this.logGenotypeAndSpeciesOfIndividual(individuals[q]);

			// at this point, parents[] contains our two selected individuals,
			// AND they're copied so we own them and can make whatever modifications
			// we like on them.

			// so we'll cross them over now.  Since this is the default pipeline,
			// we'll just do it by calling defaultCrossover on the first child

			if (parents[0] instanceof ELRGenotypeBitVectorIndividual && parents[1] instanceof ELRGenotypeBitVectorIndividual) {
				logger.debug("both parents are ELRGenotypeBitVectorIndividual");
				((ELRGenotypeBitVectorIndividual) parents[0]).defaultCrossover(state, thread, (ELRGenotypeBitVectorIndividual) parents[1]);
				parents[0].evaluated = false;
				parents[1].evaluated = false;
				logger.debug("parents[0]: " + parents[0]);
				if (parents[0] != null)
					this.logGenotypeAndSpeciesOfIndividual(parents[0]);
				logger.debug("parents[1]: " + parents[1]);
				if (parents[1] != null)
					this.logGenotypeAndSpeciesOfIndividual(parents[1]);

				ELRGenotypeBitVectorIndividual elrGenotypeParent0 = (ELRGenotypeBitVectorIndividual) parents[0];
				ELRPhenotype phenotype0 = this.checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt(elrGenotypeParent0, state, thread);
				logger.debug("phenotype0: " + phenotype0);
				if (phenotype0 != null)
					elrGenotypeParent0.setElrPhenotype(phenotype0);
				ELRGenotypeBitVectorIndividual elrGenotypeParent1 = (ELRGenotypeBitVectorIndividual) parents[1];
				ELRPhenotype phenotype1 = this.checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt(elrGenotypeParent1, state, thread);
				logger.debug("phenotype1: " + phenotype1);
				if (phenotype1 != null)
					elrGenotypeParent1.setElrPhenotype(phenotype1);

				if (parents[0] != null)
					logger.debug("parents[0].genotypeToStringForHumans(): " + parents[0].genotypeToStringForHumans());
				logger.debug("parents[1]: " + parents[1]);
				if (parents[1] != null)
					logger.debug("parents[1].genotypeToStringForHumans(): " + parents[1].genotypeToStringForHumans());

				// add 'em to the population
				individuals[q] = parents[0];
				q++;
				if (q < n + start && !tossSecondParent) {
					individuals[q] = parents[1];
					q++;
				}

				for (int i = 0; i < individuals.length; i++) {
					logger.debug("i: " + i);
					logger.debug("inds[i]: " + individuals[i]);
					if (individuals[i] != null)
						logger.debug("inds[i]" + individuals[i].genotypeToStringForHumans());
				}

			} else
				logger.debug("parents aren't ELRGenotypeBitVectorIndividual");
		}

		logger.debug("got crossed over population");

		this.logDetailsOfPopulation(individuals);

		logger.debug("n: " + n);
		logger.debug("leaving produce");
		return n;
	}

	//TODO check for code-duplication here, FeasibleInitializerServiceImpl, FeasibleVectorMutationPipelineServiceImpl and in searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService.searchForFeasibleIndividual
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
