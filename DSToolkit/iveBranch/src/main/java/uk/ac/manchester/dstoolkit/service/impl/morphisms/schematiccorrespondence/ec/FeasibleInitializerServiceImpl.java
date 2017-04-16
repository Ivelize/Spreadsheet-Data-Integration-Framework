package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
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
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.FeasibleInitializerService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.FeasibilityCheckerService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.PairsOfEntitySetsGeneratorService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.simple.SimpleInitializer;

@Service(value = "feasibleInitializerService")
@Configurable(autowire = Autowire.BY_NAME)
//, dependencyCheck = true)
public class FeasibleInitializerServiceImpl extends SimpleInitializer implements FeasibleInitializerService {

	private static Logger logger = Logger.getLogger(FeasibleInitializerServiceImpl.class);

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

	@Override
	public Population initialPopulation(EvolutionState state, int thread) {
		logger.debug("in initialPopulation");
		logger.debug("this: " + this);
		logger.debug("searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService: "
				+ searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService);
		logger.debug("pairsOfEntitySetsGeneratorService: " + pairsOfEntitySetsGeneratorService);
		logger.debug("feasibilityChecker: " + feasibilityChecker);
		logger.debug("decoderService: " + decoderService);
		logger.debug("state: " + state);
		logger.debug("thread: " + thread);
		Population population = super.initialPopulation(state, thread);
		logger.debug("population: " + population);
		logger.debug("population.subpops[0].individuals: " + population.subpops[0].individuals);
		logger.debug("population.subpops[0].individuals.length: " + population.subpops[0].individuals.length);
		int noOfIndividual = 0;
		for (Individual individual : population.subpops[0].individuals) {
			logger.debug("noOfIndividual: " + noOfIndividual);
			logger.debug("individual: " + individual);
			if (individual != null)
				logger.debug("individual.genotypeToStringForHumans(): " + individual.genotypeToStringForHumans());
			if (!(individual instanceof ELRGenotypeBitVectorIndividual)) {
				logger.error("individual isn't instanceof ELRGenotypeBitVectorIndividual");
				state.output
						.fatal("FeasibleVectorMutationPipeline didn't get a ELRGenotypeBitVectorIndividual. The offending individual is in subpopulation "
								+ population.subpops[0] + " and it's: " + individual);
			}
			ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual = (ELRGenotypeBitVectorIndividual) individual;
			logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
			ELRPhenotype elrPhenotype = this.checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt(elrGenotypeBitVectorIndividual,
					state, thread);
			logger.debug("elrPhenotype: " + elrPhenotype);
			elrGenotypeBitVectorIndividual.setElrPhenotype(elrPhenotype);
		}
		logger.debug("got initial population");
		this.logDetailsOfInitialPopulation(population.subpops[0].individuals);
		return population;
	}

	//TODO check for code-duplication here, FeasibleVectorCrossoverPipelineServiceImpl, FeasibleVectorMutationPipelineServiceImpl and in searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService.searchForFeasibleIndividual
	protected ELRPhenotype checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt(
			ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual, EvolutionState state, int thread) {
		logger.debug("in checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt");
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		logger.debug("state: " + state);
		logger.debug("thread: " + thread);
		logger.debug("this: " + this);
		logger.debug("searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService: "
				+ searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService);
		logger.debug("pairsOfEntitySetsGeneratorService: " + pairsOfEntitySetsGeneratorService);
		logger.debug("feasibilityChecker: " + feasibilityChecker);
		logger.debug("decoderService: " + decoderService);
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

	//no need to test as it's just logging information and not doing anything
	//TODO code duplicated in FeasibleVectorMutationPipelineServiceImpl, here, FeasibleVectorCrossoverPipelineServiceImpl
	protected void logDetailsOfInitialPopulation(Individual[] individuals) {
		int noOfIndividual = 0;
		for (Individual individual : individuals) {
			logger.debug("noOfIndividual: " + noOfIndividual);
			logger.debug("individual: " + individual);
			if (individual != null) {
				logger.debug("individual.genotypeToStringForHumans(): " + individual.genotypeToStringForHumans());
				logger.debug("individial.birthday: " + individual.birthday);
				logger.debug("individual.count: " + Individual.count);
				logger.debug("individual.fitness.fitness(): " + individual.fitness.fitness());

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

	/**
	 * @param searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService the searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService to set
	 */
	protected void setSearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService(
			SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService) {
		this.searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService = searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService;
	}

	/**
	 * @param pairsOfEntitySetsGeneratorService the pairsOfEntitySetsGeneratorService to set
	 */
	protected void setPairsOfEntitySetsGeneratorService(PairsOfEntitySetsGeneratorService pairsOfEntitySetsGeneratorService) {
		this.pairsOfEntitySetsGeneratorService = pairsOfEntitySetsGeneratorService;
	}

	/**
	 * @param feasibilityChecker the feasibilityChecker to set
	 */
	protected void setFeasibilityChecker(FeasibilityCheckerService feasibilityChecker) {
		this.feasibilityChecker = feasibilityChecker;
	}

	/**
	 * @param decoderService the decoderService to set
	 */
	protected void setDecoderService(DecoderService decoderService) {
		this.decoderService = decoderService;
	}

}
