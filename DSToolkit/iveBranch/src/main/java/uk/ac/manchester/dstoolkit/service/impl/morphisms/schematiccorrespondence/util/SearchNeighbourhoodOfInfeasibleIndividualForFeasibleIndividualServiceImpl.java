/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.FeasibilityCheckerService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.PairsOfEntitySetsGeneratorService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService;
import ec.EvolutionState;

/**
 * @author chedeler
 *
 */
@Service(value = "searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService")
public class SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl implements
		SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService {

	@Autowired
	@Qualifier("pairsOfEntitySetsGeneratorService")
	private PairsOfEntitySetsGeneratorService pairsOfEntitySetsGeneratorService;

	@Autowired
	@Qualifier("feasibilityCheckerService")
	private FeasibilityCheckerService feasibilityChecker;

	private static Logger logger = Logger.getLogger(SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl.class);

	public ELRGenotypeBitVectorIndividual searchForFeasibleIndividual(ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual,
			ELRChromosome elrChromosome, EvolutionState state, int thread) {
		logger.debug("in searchForFeasibleIndividual");
		logger.debug("this: " + this);
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		if (elrGenotypeBitVectorIndividual != null)
			logger.debug("individual: " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
		logger.debug("elrChromosome: " + elrChromosome);
		logger.debug("state: " + state);
		logger.debug("thread: " + thread);
		logger.debug("feasibilityChecker: " + feasibilityChecker);
		logger.debug("pairsOfEntitySetsGeneratorService: " + pairsOfEntitySetsGeneratorService);

		if (this.isFeasibleIndividual(elrGenotypeBitVectorIndividual, elrChromosome)) {
			logger.debug("elrGenotypeBitVectorIndividual provided as input is feasible - return it");
			return elrGenotypeBitVectorIndividual;
		}
		logger.debug("elrGenotypeBitVectorIndividual provided as input is not feasible - search neighbourhood for feasible elrGenotypeBitVectorIndividual");
		return this.findFeasibleIndividualByRandomlyChangingBitsBetweenTrueAndFalseAndViceVersa(elrGenotypeBitVectorIndividual, elrChromosome, state,
				thread);
	}

	protected ELRGenotypeBitVectorIndividual findFeasibleIndividualByRandomlyChangingBitsBetweenTrueAndFalseAndViceVersa(
			ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual, ELRChromosome elrChromosome, EvolutionState state, int thread) {
		//this doesn't check that the individual provided as input is feasible, as it's already done in public searchForFeasibleIndividual
		logger.debug("in findFeasibleIndividual");
		logger.debug("this: " + this);
		logger.debug("feasibilityChecker: " + feasibilityChecker);
		logger.debug("pairsOfEntitySetsGeneratorService: " + pairsOfEntitySetsGeneratorService);
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		if (elrGenotypeBitVectorIndividual != null)
			logger.debug("individual: " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
		logger.debug("elrChromosome: " + elrChromosome);
		logger.debug("state: " + state);
		logger.debug("thread: " + thread);

		boolean isFeasible = false;

		while (!isFeasible) {

			int numberOfSelected = this.countNumberOfSelectedBits(elrGenotypeBitVectorIndividual);
			logger.debug("numberOfSelected: " + numberOfSelected);
			logger.debug("state: " + state);
			logger.debug("state.random: " + state.random);
			logger.debug("state.random[thread]: " + state.random[thread]);
			int randomIndex = state.random[thread].nextInt(elrGenotypeBitVectorIndividual.genomeLength());
			logger.debug("randomIndex: " + randomIndex);

			Boolean selected = elrGenotypeBitVectorIndividual.genome[randomIndex];
			logger.debug("selected: " + selected);

			//randomly change true to false and false to true (depending on how many are selected) to avoid nothing being selected, i.e., all bits of individual = false

			boolean changedBit = false;
			if (numberOfSelected > 1 && selected) {
				logger.debug("found more than 1 selected and is selected, decide randomly whether to change it to false");
				if (state.random[thread].nextBoolean()) {
					logger.debug("state.random[thread].nextBoolean() is true - change true to false");
					changedBit = true;
					elrGenotypeBitVectorIndividual.genome[randomIndex] = false;
					logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
					numberOfSelected--;
					logger.debug("numberOfSelected: " + numberOfSelected);
				} else
					logger.debug("state.random[thread].nextBoolean() is false - do nothing");
			} else if (numberOfSelected < 2 && !selected) { //TODO might have to change this to less than 1
				logger.debug("only have less than 2 selected and is not selected, decide randomly whether to change it to true");
				if (state.random[thread].nextBoolean()) {
					logger.debug("state.random[thread].nextBoolean() is true - change false to true");
					changedBit = true;
					elrGenotypeBitVectorIndividual.genome[randomIndex] = true;
					logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
					numberOfSelected++;
					logger.debug("numberOfSelected: " + numberOfSelected);
				} else
					logger.debug("state.random[thread].nextBoolean() is false - do nothing");
			}

			if (numberOfSelected > 0 && changedBit) {
				logger.debug("individual contains selected bit(s) and changedBit");
				//TODO I'm not checking for duplicates at the moment ... might have to add that
				if (this.isFeasibleIndividual(elrGenotypeBitVectorIndividual, elrChromosome)) {
					logger.debug("found feasibleIndividual - set isFeasible = true and return individual");
					isFeasible = true;
				}
			} else
				logger.debug("didn't find selected bit or not changed bit");
		}
		logger.debug("found feasible individual - return it");
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		if (elrGenotypeBitVectorIndividual != null)
			logger.debug("individual: " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
		return elrGenotypeBitVectorIndividual;
	}

	//TODO this isn't the best place for it ... as I could do with something like this in the feasibleInitializer ....
	protected boolean isFeasibleIndividual(ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual, ELRChromosome elrChromosome) {
		logger.debug("in isFeasibleIndividual");
		logger.debug("this: " + this);
		logger.debug("feasibilityChecker: " + feasibilityChecker);
		logger.debug("pairsOfEntitySetsGeneratorService: " + pairsOfEntitySetsGeneratorService);
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		if (elrGenotypeBitVectorIndividual != null)
			logger.debug("individual: " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
		logger.debug("elrChromosome: " + elrChromosome);
		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorService
				.generatePairsOfSourceAndTargetEntitySetsForELRGenotypeBitVectorIndividual(elrGenotypeBitVectorIndividual,
						elrChromosome.getChromosomeOfPairsOfSuperAbstracts());
		logger.debug("pairsOfEntitySets: " + pairsOfEntitySets);
		boolean isFeasible = feasibilityChecker.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets,
				elrChromosome.getMatchedSuperAbstractsInSourceSchemas(), elrChromosome.getMatchedSuperAbstractsInTargetSchemas(),
				elrChromosome.getMatchedSourceSuperAbstractTargetSuperAbstractsMap());
		logger.debug("isFeasible: " + isFeasible);
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		if (elrGenotypeBitVectorIndividual != null)
			logger.debug("individual: " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
		return isFeasible;
	}

	protected int countNumberOfSelectedBits(ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual) {
		logger.debug("in countNumberOfSelectedBits");
		logger.debug("this: " + this);
		logger.debug("feasibilityChecker: " + feasibilityChecker);
		logger.debug("pairsOfEntitySetsGeneratorService: " + pairsOfEntitySetsGeneratorService);
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		if (elrGenotypeBitVectorIndividual != null)
			logger.debug("individual: " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
		int numberOfSelected = 0;
		for (Boolean selected : elrGenotypeBitVectorIndividual.genome) {
			if (selected)
				numberOfSelected++;
		}
		logger.debug("numberOfSelected: " + numberOfSelected);
		return numberOfSelected;
	}

	//TODO not ideal ... these are public for testing purposes ...

	/**
	 * @param pairsOfEntitySetsGeneratorService the pairsOfEntitySetsGeneratorService to set
	 */
	public void setPairsOfEntitySetsGeneratorService(PairsOfEntitySetsGeneratorService pairsOfEntitySetsGeneratorService) {
		this.pairsOfEntitySetsGeneratorService = pairsOfEntitySetsGeneratorService;
	}

	/**
	 * @param feasibilityChecker the feasibilityChecker to set
	 */
	public void setFeasibilityChecker(FeasibilityCheckerService feasibilityChecker) {
		this.feasibilityChecker = feasibilityChecker;
	}
}
