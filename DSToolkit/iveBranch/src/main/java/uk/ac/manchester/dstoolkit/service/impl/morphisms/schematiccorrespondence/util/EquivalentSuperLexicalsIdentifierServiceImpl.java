/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.MostSimilarSuperLexicalPerSuperAbstract;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierService;

/**
 * @author chedeler
 *
 */
@Service(value = "equivalentSuperLexicalsIdentifierService")
public class EquivalentSuperLexicalsIdentifierServiceImpl implements EquivalentSuperLexicalsIdentifierService {

	private static Logger logger = Logger.getLogger(EquivalentSuperLexicalsIdentifierServiceImpl.class);
	private final Set<SuperLexical> processedSuperLexicals = new HashSet<SuperLexical>();
	private Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMapForSource;
	private Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMapForTarget;

	//it has to be the attribute A with the highest derivedMatch score to B within the entity of attribute A
	//ordered all derived matches ascending by score, this way I could be sure it's the highest within an entity
	//1. get SLs in other schema(s) with which the currentSL is matched
	//2. for each matched SL get the SLs that are matched with the SL to identify SLs in the same schema(s) as currentSL that are equivalent to it
	//   compare groups of potentially equivalent SLs to identify those most similar to the current SL according to the matching scores
	//3. got all groups of equivalent SL candidates for each SL matched to currentSL in equivalentSlCandidatesPerSaForEachMatchedSl
	//   calculate AttributeSim (Algorithm 8 in Chenjuan's thesis and identify group of most similar SLs that are equivalent to currentSL)
	//4. got group of SLs that's most similar to currentSL
	//   generate all combinations of SLs with currentSL, calculate agg(D) - line 18 algorithm 7 in Chenjuan's thesis
	//   and identify combination of SLs with max agg(D) ... those are identified as equivalent SLs
	//   create group and add all sls to processedSls
	//   check whether it's got all the equivalent sls with the same names, if not, add them (only for the most frequent name) - currently not done ... TODO

	private void init() {
		this.processedSuperLexicals.clear();
	}

	public Set<SuperLexical> getEquivalentSuperLexicalsForSuperLexical(SuperLexical superLexical, boolean isSource) {
		logger.debug("in getEquivalentSuperLexicalsForSuperLexical");
		logger.debug("superLexical: " + superLexical);
		logger.debug("isSource: " + isSource);
		logger.debug("this: " + this);
		logger.debug("equivalentSuperLexicalsSetsMapForSource: " + equivalentSuperLexicalsSetsMapForSource);
		logger.debug("equivalentSuperLexicalsSetsMapForTarget: " + equivalentSuperLexicalsSetsMapForTarget);
		if (isSource && equivalentSuperLexicalsSetsMapForSource.containsKey(superLexical))
			return equivalentSuperLexicalsSetsMapForSource.get(superLexical);
		if (!isSource && equivalentSuperLexicalsSetsMapForTarget.containsKey(superLexical))
			return equivalentSuperLexicalsSetsMapForTarget.get(superLexical);
		return new HashSet<SuperLexical>();
	}

	//TODO test fully
	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierService#identifyEquivalentSuperLexicals(uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorService, boolean)
	 */
	public Map<SuperLexical, Set<SuperLexical>> identifyEquivalentSuperLexicals(DerivedOneToOneMatchingsGeneratorService derivedMatchingsGenerator,
			double maxMatchingScore, boolean isSourceToTarget) {
		logger.debug("in identifyEquivalentSuperLexicals");
		logger.debug("this: " + this);
		logger.debug("derivedMatchingsGenerator: " + derivedMatchingsGenerator);
		logger.debug("maxMatchingScore: " + maxMatchingScore);
		logger.debug("isSourceToTarget: " + isSourceToTarget);
		init();
		Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMap = new HashMap<SuperLexical, Set<SuperLexical>>();
		List<DerivedOneToOneMatching> derivedOneToOneMatchingOrderedDescendingBySumOfMatchingScore = derivedMatchingsGenerator
				.getDerivedOneToOneMatchingOrderedDescendingBySumOfMatchingScores(derivedMatchingsGenerator.getDerivedOneToOneMatchings());
		logger.debug("derivedOneToOneMatchingOrderedDescendingBySumOfMatchingScore: " + derivedOneToOneMatchingOrderedDescendingBySumOfMatchingScore);
		for (DerivedOneToOneMatching currentOneToOneDerivedSuperLexicalMatching : derivedOneToOneMatchingOrderedDescendingBySumOfMatchingScore) {
			logger.debug("currentOneToOneDerivedSuperLexicalMatching: " + currentOneToOneDerivedSuperLexicalMatching);
			logger.debug("currentOneToOneDerivedSuperLexicalMatching.getConstruct1(): " + currentOneToOneDerivedSuperLexicalMatching.getConstruct1());
			logger.debug("currentOneToOneDerivedSuperLexicalMatching.getConstruct2(): " + currentOneToOneDerivedSuperLexicalMatching.getConstruct2());
			if (currentOneToOneDerivedSuperLexicalMatching.getConstruct1() instanceof SuperLexical
					&& currentOneToOneDerivedSuperLexicalMatching.getConstruct2() instanceof SuperLexical) {
				Set<SuperLexical> equivalentSuperLexicals = this
						.identifySuperLexicalsInSameSchemasAsAndEquivalentToSourceSuperLexicalInDerivedMatching(maxMatchingScore,
								derivedMatchingsGenerator, currentOneToOneDerivedSuperLexicalMatching, isSourceToTarget);
				logger.debug("equivalentSuperLexicals: " + equivalentSuperLexicals);
				if (equivalentSuperLexicals != null && equivalentSuperLexicals.size() > 1) {
					for (SuperLexical superLexical : equivalentSuperLexicals) {
						equivalentSuperLexicalsSetsMap.put(superLexical, equivalentSuperLexicals);
					}
				}
			}
		}
		logger.debug("equivalentSuperLexicalsSets.size(): " + equivalentSuperLexicalsSetsMap.size());
		if (isSourceToTarget)
			this.equivalentSuperLexicalsSetsMapForSource = equivalentSuperLexicalsSetsMap;
		else
			this.equivalentSuperLexicalsSetsMapForTarget = equivalentSuperLexicalsSetsMap;
		logger.debug("this: " + this);
		logger.debug("isSourceToTarget: " + isSourceToTarget);
		logger.debug("equivalentSuperLexicalsSetsMapForSource: " + equivalentSuperLexicalsSetsMapForSource);
		if (equivalentSuperLexicalsSetsMapForSource != null) {
			for (SuperLexical sourceSuperLexical : equivalentSuperLexicalsSetsMapForSource.keySet()) {
				logger.debug("sourceSuperLexical: " + sourceSuperLexical);
				for (SuperLexical equivalentSourceSuperLexical : equivalentSuperLexicalsSetsMapForSource.get(sourceSuperLexical))
					logger.debug("equivalentSourceSuperLexical: " + equivalentSourceSuperLexical);
			}
		}
		logger.debug("equivalentSuperLexicalsSetsMapForTarget: " + equivalentSuperLexicalsSetsMapForTarget);
		if (equivalentSuperLexicalsSetsMapForTarget != null) {
			for (SuperLexical targetSuperLexical : equivalentSuperLexicalsSetsMapForTarget.keySet()) {
				logger.debug("targetSuperLexical: " + targetSuperLexical);
				for (SuperLexical equivalentTargetSuperLexical : equivalentSuperLexicalsSetsMapForTarget.get(targetSuperLexical))
					logger.debug("equivalentTargetSuperLexical: " + equivalentTargetSuperLexical);
			}
		}
		return equivalentSuperLexicalsSetsMap;
	}

	//TODO test fully
	//TODO refactor
	protected Set<SuperLexical> identifySuperLexicalsInSameSchemasAsAndEquivalentToSourceSuperLexicalInDerivedMatching(double maxMatchingScore,
			DerivedOneToOneMatchingsGeneratorService derivedMatchingsGenerator, DerivedOneToOneMatching currentOneToOneDerivedMatching,
			boolean isSourceToTarget) {
		logger.debug("in identifySuperLexicalsInSameSchemasAsAndEquivalentToSourceSuperLexicalInDerivedMatching");
		logger.debug("this: " + this);
		logger.debug("maxMatchingScore: " + maxMatchingScore);
		logger.debug("derivedMatchingsGenerator: " + derivedMatchingsGenerator);
		logger.debug("currentOneToOneDerivedMatching: " + currentOneToOneDerivedMatching);
		logger.debug("isSourceToTarget: " + isSourceToTarget);
		SuperLexical superLexicalInCurrentDerivedMatching = null;
		SuperLexical matchedSuperLexicalInCurrentDerivedMatching = null;
		SuperAbstract superAbstractOfSuperLexicalInCurrentDerivedMatching = null;
		if (isSourceToTarget) {
			superLexicalInCurrentDerivedMatching = (SuperLexical) currentOneToOneDerivedMatching.getConstruct1();
			matchedSuperLexicalInCurrentDerivedMatching = (SuperLexical) currentOneToOneDerivedMatching.getConstruct2();
		} else {
			superLexicalInCurrentDerivedMatching = (SuperLexical) currentOneToOneDerivedMatching.getConstruct2();
			matchedSuperLexicalInCurrentDerivedMatching = (SuperLexical) currentOneToOneDerivedMatching.getConstruct1();
		}
		logger.debug("superLexicalInCurrentDerivedMatching: " + superLexicalInCurrentDerivedMatching);
		logger.debug("matchedSuperLexicalInCurrentDerivedMatching: " + matchedSuperLexicalInCurrentDerivedMatching);
		superAbstractOfSuperLexicalInCurrentDerivedMatching = superLexicalInCurrentDerivedMatching.getFirstAncestorSuperAbstract();
		logger.debug("superAbstractOfSuperLexicalInCurrentDerivedMatching: " + superAbstractOfSuperLexicalInCurrentDerivedMatching);
		if (!processedSuperLexicals.contains(superLexicalInCurrentDerivedMatching)) {
			logger.debug("superLexicalInCurrentDerivedMatching not yet processed");
			Set<SuperLexical> superLexicalsMatchedWithCurrentSuperLexical = derivedMatchingsGenerator
					.getSuperLexicalsMatchedWithSuperLexical(superLexicalInCurrentDerivedMatching);
			logger.debug("superLexicalsMatchedWithCurrentSuperLexical.size(): " + superLexicalsMatchedWithCurrentSuperLexical.size());
			Map<SuperLexical, Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>> equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical = new HashMap<SuperLexical, Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>>();
			for (SuperLexical matchedSuperLexical : superLexicalsMatchedWithCurrentSuperLexical) {
				logger.debug("matchedSuperLexical: " + matchedSuperLexical);
				Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> mostSimilarSuperLexicalPerSuperAbstract = this
						.forMatchedSuperLexicalIdentifySuperLexicalPerSuperAbstractWithLargestSumOfMatchingScores(derivedMatchingsGenerator,
								matchedSuperLexical);
				logger.debug("mostSimilarSuperLexicalPerSuperAbstract.size(): " + mostSimilarSuperLexicalPerSuperAbstract.size());
				if (mostSimilarSuperLexicalPerSuperAbstract.size() > 1
						&& mostSimilarSuperLexicalPerSuperAbstract.containsKey(superAbstractOfSuperLexicalInCurrentDerivedMatching)
						&& mostSimilarSuperLexicalPerSuperAbstract.get(superAbstractOfSuperLexicalInCurrentDerivedMatching)
								.getDerivedOneToOneMatching().equals(currentOneToOneDerivedMatching)) { //TODO the second and third check shouldn't be needed
					logger.debug("mostSimilarSuperLexicalPerSuperAbstract.size() > 1 and contains currentOneToOneDerivedMatching and corresponding superAbstract");
					equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical.put(matchedSuperLexical,
							mostSimilarSuperLexicalPerSuperAbstract);
				} else if (mostSimilarSuperLexicalPerSuperAbstract.size() == 1) {
					logger.debug("mostSimilarSuperLexicalPerSuperAbstract.size() == 1");
					if (!mostSimilarSuperLexicalPerSuperAbstract.keySet().iterator().next()
							.equals(superAbstractOfSuperLexicalInCurrentDerivedMatching)) {
						logger.debug("only one equivalent superLexical for matched SuperLexical and it's not in the same superAbstract as the current superLexical - TODO");
						logger.debug("superLexicalInCurrentDerivedMatching: " + superLexicalInCurrentDerivedMatching);
						logger.debug("superAbstractOfSuperLexicalInCurrentDerivedMatching: " + superAbstractOfSuperLexicalInCurrentDerivedMatching);
						logger.debug("mostSimilarSuperLexicalPerSuperAbstract.keySet().iterator().next(): "
								+ mostSimilarSuperLexicalPerSuperAbstract.keySet().iterator().next());
					} else {
						logger.debug("only one equivalent superLexical for matched SuperLexical and it's in the same superAbstract as the current superLexical - do nothing");
						logger.debug("superLexicalInCurrentDerivedMatching: " + superLexicalInCurrentDerivedMatching);
						logger.debug("superAbstractOfSuperLexicalInCurrentDerivedMatching: " + superAbstractOfSuperLexicalInCurrentDerivedMatching);
						logger.debug("mostSimilarSuperLexicalPerSuperAbstract.keySet().iterator().next(): "
								+ mostSimilarSuperLexicalPerSuperAbstract.keySet().iterator().next());
					}
				} else if (mostSimilarSuperLexicalPerSuperAbstract.size() > 1) {
					if (!mostSimilarSuperLexicalPerSuperAbstract.containsKey(superAbstractOfSuperLexicalInCurrentDerivedMatching))
						logger.error("mostSimilarSuperLexicalPerSuperAbstract doesn't contain superAbstractOfSuperLexicalInCurrentDerivedMatching - TODO");
					else {
						if (matchedSuperLexical.equals(matchedSuperLexicalInCurrentDerivedMatching)
								&& !mostSimilarSuperLexicalPerSuperAbstract.get(superAbstractOfSuperLexicalInCurrentDerivedMatching)
										.getDerivedOneToOneMatching().equals(currentOneToOneDerivedMatching)) {
							logger.debug("derived matching with maxSumOfMatchingScores between superLexicalInCurrentDerivedMatching and matchedSuperLexicalInCurrentDerivedMatching isn't currentOneToOneDerivedMatching");
							logger.debug("should be because it has a lower score");
							logger.debug("compare scores of the two derivedMatchings and replace with currentOneToOneDerivedMatching as it's the one with the highest score");
							logger.debug("mostSimilarSuperLexicalPerSuperAbstract.get(superAbstractOfSuperLexicalInCurrentDerivedMatching).getDerivedOneToOneMatching().getSumOfMatchingScores(): "
									+ mostSimilarSuperLexicalPerSuperAbstract.get(superAbstractOfSuperLexicalInCurrentDerivedMatching)
											.getDerivedOneToOneMatching().getSumOfMatchingScores());
							logger.debug("currentOneToOneDerivedMatching.getSumOfMatchingScores(): "
									+ currentOneToOneDerivedMatching.getSumOfMatchingScores());
							if (mostSimilarSuperLexicalPerSuperAbstract.get(superAbstractOfSuperLexicalInCurrentDerivedMatching)
									.getDerivedOneToOneMatching().getSumOfMatchingScores() > currentOneToOneDerivedMatching.getSumOfMatchingScores()) {
								logger.debug("currentOneToOneDerivedMatching has lower score - do nothing");
							} else {
								logger.debug("currentOneToOneDerivedMatching doesn't have lower score - TODO");
							}
						}

					}
				}
			}

			if (equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical.size() > 0) {
				Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> groupOfMostSimilarSuperLexicalsPerSuperAbstract = this
						.identifyGroupOfMostSimilarSuperLexicalPerSuperAbstract(maxMatchingScore, superLexicalInCurrentDerivedMatching,
								equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical);
				logger.debug("groupOfMostSimilarSuperLexicalsPerSuperAbstract: " + groupOfMostSimilarSuperLexicalsPerSuperAbstract);
				Set<SuperLexical> equivalentSuperLexicals = this.generateSetOfEquivalentSuperLexicals(superLexicalInCurrentDerivedMatching,
						groupOfMostSimilarSuperLexicalsPerSuperAbstract);
				logger.debug("equivalentSuperLexicals: " + equivalentSuperLexicals);
				processedSuperLexicals.addAll(equivalentSuperLexicals);
				return equivalentSuperLexicals;
			}
		}
		logger.debug("superLexicalInCurrentDerivedMatching already processed, return null");
		return null;
	}

	//TODO test fully
	//TODO probably needs refactoring
	protected Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> forMatchedSuperLexicalIdentifySuperLexicalPerSuperAbstractWithLargestSumOfMatchingScores(
			DerivedOneToOneMatchingsGeneratorService derivedMatchingsGenerator, SuperLexical matchedSuperLexical) {
		logger.debug("in forMatchedSuperLexicalIdentifySuperLexicalPerSuperAbstractWithLargestSumOfMatchingScores");
		logger.debug("this: " + this);
		logger.debug("derivedMatchingsGenerator: " + derivedMatchingsGenerator);
		logger.debug("matchedSuperLexical: " + matchedSuperLexical);
		Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical = new HashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>();
		Map<SuperLexical, DerivedOneToOneMatching> matchedSuperLexicalsWithDerivedMatching = derivedMatchingsGenerator
				.getAllSuperLexicalsMatchedWithSuperLexicalWithDerivedMatching(matchedSuperLexical);
		logger.debug("matchedSuperLexicalsWithDerivedMatching.size(): " + matchedSuperLexicalsWithDerivedMatching.size());
		for (SuperLexical matchedSL : matchedSuperLexicalsWithDerivedMatching.keySet())
			logger.debug("matchedSL: " + matchedSL);
		Map<SuperAbstract, Map<SuperLexical, DerivedOneToOneMatching>> equivalentSuperLexicalCandidatesGroupedBySuperAbstract = derivedMatchingsGenerator
				.getMatchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings(matchedSuperLexicalsWithDerivedMatching);
		logger.debug("equivalentSuperLexicalCandidatesGroupedBySuperAbstract: " + equivalentSuperLexicalCandidatesGroupedBySuperAbstract);
		for (SuperAbstract currentSuperAbstract : equivalentSuperLexicalCandidatesGroupedBySuperAbstract.keySet()) {
			logger.debug("currentSuperAbstract: " + currentSuperAbstract);
			MostSimilarSuperLexicalPerSuperAbstract mostSimilarSuperLexicalPerSuperAbstract = derivedMatchingsGenerator
					.getMostSimilarSuperLexicalWithLargestSumOfMatchingScoresWithinGroupOfMatches(equivalentSuperLexicalCandidatesGroupedBySuperAbstract
							.get(currentSuperAbstract));
			logger.debug("mostSimilarSuperLexicalPerSuperAbstract.getSuperLexical(): " + mostSimilarSuperLexicalPerSuperAbstract.getSuperLexical());
			superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical.put(currentSuperAbstract, mostSimilarSuperLexicalPerSuperAbstract);
		}
		return superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical;
	}

	/*
	protected LinkedHashMap<SuperLexical, Set<SuperLexical>> identifyEquivalentSuperLexicalGroups(
			LinkedHashMap<SuperLexical, LinkedHashMap<SuperLexical, DerivedOneToOneMatching>> superLexicalsInSchemas1SuperLexicalsInSchemas2DerivedMatchingMap,
			LinkedHashMap<SuperLexical, LinkedHashMap<SuperLexical, DerivedOneToOneMatching>> superLexivalsInSchemas2SuperLexicalsInSchemas1DerivedMatchingMap,
			List<DerivedOneToOneMatching> listOfDerivedSuperLexicalMatchingsOrderedDescendingBySumOfMatchScore, boolean isSourceToTarget) {

		LinkedHashMap<SuperLexical, Set<SuperLexical>> superLexicalEquivalentSuperLexicalsMap = new LinkedHashMap<SuperLexical, Set<SuperLexical>>();
		LinkedHashSet<SuperLexical> processedSuperLexicals = new LinkedHashSet<SuperLexical>();
		SuperLexical superLexicalInCurrentDerivedMatching = null;
		SuperLexical matchedSuperLexicalInCurrentDerivedMatching = null;
		for (DerivedOneToOneMatching currentOneToOneDerivedSuperLexicalMatching : listOfDerivedSuperLexicalMatchingsOrderedDescendingBySumOfMatchScore) {
			if (!(currentOneToOneDerivedSuperLexicalMatching.getConstruct1() instanceof SuperLexical)
					&& !(currentOneToOneDerivedSuperLexicalMatching.getConstruct2() instanceof SuperLexical)) {
				logger.error("derivedMatching in orderedListOfDerivedSlMatchings not between two SLs - TODO sort this ... shouldn't get here");
			} else {
				if (isSourceToTarget) {
					superLexicalInCurrentDerivedMatching = (SuperLexical) currentOneToOneDerivedSuperLexicalMatching.getConstruct1();
					matchedSuperLexicalInCurrentDerivedMatching = (SuperLexical) currentOneToOneDerivedSuperLexicalMatching.getConstruct2();
				} else {
					superLexicalInCurrentDerivedMatching = (SuperLexical) currentOneToOneDerivedSuperLexicalMatching.getConstruct2();
					matchedSuperLexicalInCurrentDerivedMatching = (SuperLexical) currentOneToOneDerivedSuperLexicalMatching.getConstruct1();
				}
				if (processedSuperLexicals.contains(superLexicalInCurrentDerivedMatching)) {
					logger.debug("currentSl has already been processed");
				} else {
					LinkedHashMap<SuperLexical, DerivedOneToOneMatching> allSuperLexicalsMatchedWithCurrentSuperLexical = superLexicalsInSchemas1SuperLexicalsInSchemas2DerivedMatchingMap
							.get(superLexicalInCurrentDerivedMatching);
					LinkedHashMap<SuperLexical, LinkedHashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>> equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical = this
							.generateEquivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical(superLexicalInCurrentDerivedMatching,
									matchedSuperLexicalInCurrentDerivedMatching, currentOneToOneDerivedSuperLexicalMatching,
									allSuperLexicalsMatchedWithCurrentSuperLexical, superLexivalsInSchemas2SuperLexicalsInSchemas1DerivedMatchingMap,
									processedSuperLexicals);
					LinkedHashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> currentMostSimilarSLPerSuperAbstract = this
							.identifyMostSimilarSuperLexicalPerSuperAbstract(superLexicalInCurrentDerivedMatching,
									equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical);
					superLexicalEquivalentSuperLexicalsMap = this.fillEquivalentSlsGroupMapWithEquivalentSlsForCurrentSl(
							superLexicalInCurrentDerivedMatching, currentMostSimilarSLPerSuperAbstract, processedSuperLexicals,
							superLexicalEquivalentSuperLexicalsMap);
				}
			}
		}
		return superLexicalEquivalentSuperLexicalsMap;
	}
	*/

	/*
	protected LinkedHashMap<SuperLexical, LinkedHashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>> generateEquivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical(
			SuperLexical currentSuperLexical, SuperLexical currentMatchedSuperLexical,
			DerivedOneToOneMatching currentOneToOneSuperLexicalDerivedMatching,
			LinkedHashMap<SuperLexical, DerivedOneToOneMatching> matchedSuperLexicals,
			LinkedHashMap<SuperLexical, LinkedHashMap<SuperLexical, DerivedOneToOneMatching>> matchedSuperLexicalsDerivedMatchingMap,
			LinkedHashSet<SuperLexical> processedSuperLexicals) {
		LinkedHashMap<SuperLexical, LinkedHashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>> equivalentSlCandidatesPerSaForEachMatchedSl = new LinkedHashMap<SuperLexical, LinkedHashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>>();

		boolean foundCurrentOneToOneSlMatching = false;

		for (SuperLexical matchedSL : matchedSuperLexicals.keySet()) {
			logger.debug("matchedSL: " + matchedSL);
			logger.debug("matchedSL.getSchema(): " + matchedSL.getSchema());
			DerivedOneToOneMatching derivedMatchingToCurrentSlToAdd = null;
			LinkedHashMap<SuperLexical, DerivedOneToOneMatching> equivalentSlCandidates = matchedSuperLexicalsDerivedMatchingMap.get(matchedSL);
			logger.debug("equivalentSlCandidates: " + equivalentSlCandidates);
			logger.debug("equivalentSlCandidates.size(): " + equivalentSlCandidates.size());
			if (equivalentSlCandidates.containsKey(currentSuperLexical)) {
				logger.debug("found currentSL in equivalentSlCandidates");
				if (equivalentSlCandidates.size() > 1) {
					logger.debug("not just currentSl in equivalentSls ... check the others");
					//identify the most similar sl for each sa based on score of derivedMatching
					LinkedHashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> currentMostSimilarSLPerSuperAbstract = new LinkedHashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>();
					for (SuperLexical equivalentSlCandidate : equivalentSlCandidates.keySet()) {
						logger.debug("equivalentSlCandidate: " + equivalentSlCandidate);
						logger.debug("currentSl: " + currentSuperLexical);
						if (!processedSuperLexicals.contains(equivalentSlCandidate)) {
							logger.debug("processedSls doesn't contain equivalentSlCandidate ... carry on");
							if (equivalentSlCandidate.equals(currentSuperLexical)) {
								logger.debug("equivalentSlCandidate equals currentSl");
								logger.debug("ignore for now ... only process and add when others have been identified");
								logger.debug("equivalentSlCandidates.get(currentSl): " + equivalentSlCandidates.get(currentSuperLexical));
								if (matchedSL.equals(currentMatchedSuperLexical)) {
									logger.debug("matchedSl equals currentMatchedSl");
									logger.debug("check whether its the currentOneToOneSlMatching");
									logger.debug("currentOneToOneSlMatching: " + currentOneToOneSuperLexicalDerivedMatching);
									if (equivalentSlCandidates.get(currentSuperLexical).equals(currentOneToOneSuperLexicalDerivedMatching)) {
										logger.debug("it's the same derivedMatch");
										logger.debug("set derivedMatchingToCurrentSlToAdd to currentOneToOneSlMatching");
										if (derivedMatchingToCurrentSlToAdd == null)
											derivedMatchingToCurrentSlToAdd = currentOneToOneSuperLexicalDerivedMatching;
										else {
											logger.debug("derivedMatchingToCurrentSlToAdd not null: " + derivedMatchingToCurrentSlToAdd);
										}
										foundCurrentOneToOneSlMatching = true;
									} else {
										logger.debug("it's a different derivedMatch");
										logger.debug("currentOneToOneSlMatching.getSumOfMatchingScores(): "
												+ currentOneToOneSuperLexicalDerivedMatching.getSumOfMatchingScores());
										logger.debug("sumOfMatchingScores of derivedMatching for equivalentSlCandidate: "
												+ equivalentSlCandidates.get(equivalentSlCandidate).getSumOfMatchingScores());
										if (currentOneToOneSuperLexicalDerivedMatching.getSumOfMatchingScores() < equivalentSlCandidates.get(
												equivalentSlCandidate).getSumOfMatchingScores()) {
											logger.debug("currentMatching not the one with the higher score ... proceed with next matchedSl");
											break;
										} else {
											logger.debug("currentMatching is the one with the higher score ... carry on");
											logger.debug("set derivedMatchingToCurrentSlToAdd to currentOneToOneSlMatching");
											if (derivedMatchingToCurrentSlToAdd == null)
												derivedMatchingToCurrentSlToAdd = currentOneToOneSuperLexicalDerivedMatching;
											else {
												logger.debug("derivedMatchingToCurrentSlToAdd not null: " + derivedMatchingToCurrentSlToAdd);
											}
										}
									}
								} else {
									logger.debug("matchedSl doesn't equal currentMatchedSl");
									logger.debug("set derivedMatchingToCurrentSlToAdd to equivalentSlCandidates.get(currentSl)");
									if (derivedMatchingToCurrentSlToAdd == null)
										derivedMatchingToCurrentSlToAdd = equivalentSlCandidates.get(currentSuperLexical);
									else {
										logger.debug("derivedMatchingToCurrentSlToAdd not null: " + derivedMatchingToCurrentSlToAdd);
									}
								}
							} else {
								logger.debug("equivalentSlCandidate doesn't equal currentSl");
								SuperAbstract saOfEquivalentSlCandidate = equivalentSlCandidate.getParentSuperAbstract();
								logger.debug("saOfEquivalentSlCandidate: " + saOfEquivalentSlCandidate);
								if (!saOfEquivalentSlCandidate.equals(currentSuperLexical.getParentSuperAbstract())) {
									logger.debug("saOfEquivalentSlCandidate different from sa of currentSl");
									if (currentMostSimilarSLPerSuperAbstract.containsKey(saOfEquivalentSlCandidate)) {
										logger.debug("currentMostSimilarSLPerSuperAbstract contains SlCandidate for saOfEquivalentSlCandidate, compare scores");
										MostSimilarSuperLexicalPerSuperAbstract mostSimilarSlPerSa = currentMostSimilarSLPerSuperAbstract
												.get(saOfEquivalentSlCandidate);
										logger.debug("mostSimilarSlPerSa: " + mostSimilarSlPerSa);
										DerivedOneToOneMatching matchingOfMostSimilarSlPerSa = mostSimilarSlPerSa.getDerivedMatching();
										logger.debug("matchingOfMostSimilarSlPerSa: " + matchingOfMostSimilarSlPerSa);
										logger.debug("matchingOfMostSimilarSlPerSa.getSumOfMatchingScores(): "
												+ matchingOfMostSimilarSlPerSa.getSumOfMatchingScores());
										logger.debug("sumOfMatchingScores of derivedMatching for equivalentSlCandidate: "
												+ equivalentSlCandidates.get(equivalentSlCandidate).getSumOfMatchingScores());
										logger.debug("currentSl.getName(): " + currentSuperLexical.getName());
										logger.debug("equivalentSlCandidate.getName(): " + equivalentSlCandidate.getName());
										if (currentSuperLexical.getName().equalsIgnoreCase(equivalentSlCandidate.getName())) {
											logger.debug("currentSl and equivalentSlCandidate have same name - ignoring case, add equivalentSlCandidate even if other in same sa has higher similarity score - unless the one with the higher score has same name too");
											if (equivalentSlCandidates.get(equivalentSlCandidate).getSumOfMatchingScores() > matchingOfMostSimilarSlPerSa
													.getSumOfMatchingScores()) {
												logger.debug("sumOfMatchingScores of derivedMatching for equivalentSlCandidates > sumOfMatchingScores of current most similar Sl per Sa, replace");
												mostSimilarSlPerSa = new MostSimilarSuperLexicalPerSuperAbstract(saOfEquivalentSlCandidate,
														equivalentSlCandidate, equivalentSlCandidates.get(equivalentSlCandidate));
												currentMostSimilarSLPerSuperAbstract.put(saOfEquivalentSlCandidate, mostSimilarSlPerSa);
											} else {
												logger.debug("sumOfMatchingScores of derivedMatching for equivalentSlCandidates <= sumOfMatchingScores of current most similar Sl per Sa, check whether names are the same");
												logger.debug("currentSl.getName(): " + currentSuperLexical.getName());
												logger.debug("mostSimilarSlPerSa.getSl().getName(): " + mostSimilarSlPerSa.getSl().getName());
												if (!(mostSimilarSlPerSa.getSl().getName().equalsIgnoreCase(currentSuperLexical.getName()))) {
													logger.debug("current mostSimilarSlPerSa doesn't have same name as currentSl - replace with equivalentSlCandidate which has the same name");
													logger.debug("equivalentSlCandidates.get(equivalentSlCandidate).getSumOfMatchingScores(): "
															+ equivalentSlCandidates.get(equivalentSlCandidate).getSumOfMatchingScores());
													logger.debug("matchingOfMostSimilarSlPerSa.getSumOfMatchingScores(): "
															+ matchingOfMostSimilarSlPerSa.getSumOfMatchingScores());
													mostSimilarSlPerSa = new MostSimilarSuperLexicalPerSuperAbstract(saOfEquivalentSlCandidate,
															equivalentSlCandidate, equivalentSlCandidates.get(equivalentSlCandidate));
													currentMostSimilarSLPerSuperAbstract.put(saOfEquivalentSlCandidate, mostSimilarSlPerSa);
												} else {
													logger.debug("current mostSimilarSlPerSa has same name as currentSl - do nothing");
												}
											}
										} else {
											logger.debug("currentSl and equivalentSlCandidate have different names - compare sumOfMatchingScores");
											if (equivalentSlCandidates.get(equivalentSlCandidate).getSumOfMatchingScores() > matchingOfMostSimilarSlPerSa
													.getSumOfMatchingScores()) {
												logger.debug("sumOfMatchingScores of derivedMatching for equivalentSlCandidates > sumOfMatchingScores of current most similar Sl per Sa, replace");
												mostSimilarSlPerSa = new MostSimilarSuperLexicalPerSuperAbstract(saOfEquivalentSlCandidate,
														equivalentSlCandidate, equivalentSlCandidates.get(equivalentSlCandidate));
												currentMostSimilarSLPerSuperAbstract.put(saOfEquivalentSlCandidate, mostSimilarSlPerSa);
											} else {
												logger.debug("sumOfMatchingScores of derivedMatching for equivalentSlCandidates <= sumOfMatchingScores of current most similar Sl per Sa, do nothing");
											}
										}
									} else {
										logger.debug("currentMostSimilarSLPerSuperAbstract doesn't contain SlCandidate for saOfEquivalentSlCandidate, add current one");
										MostSimilarSuperLexicalPerSuperAbstract mostSimilarSlPerSa = new MostSimilarSuperLexicalPerSuperAbstract(
												saOfEquivalentSlCandidate, equivalentSlCandidate, equivalentSlCandidates.get(equivalentSlCandidate));
										currentMostSimilarSLPerSuperAbstract.put(saOfEquivalentSlCandidate, mostSimilarSlPerSa);
									}
								} else {
									logger.debug("saOfEquivalentSlCandidate same as sa of currentSl - compare matching scores");
									logger.debug("currentOneToOneSlMatching.getSumOfMatchingScores(): "
											+ currentOneToOneSuperLexicalDerivedMatching.getSumOfMatchingScores());
									logger.debug("sumOfMatchingScores of derivedMatching for equivalentSlCandidate: "
											+ equivalentSlCandidates.get(equivalentSlCandidate).getSumOfMatchingScores());
									if (currentOneToOneSuperLexicalDerivedMatching.getSumOfMatchingScores() < equivalentSlCandidates.get(
											equivalentSlCandidate).getSumOfMatchingScores()) {
										logger.debug("currentMatching not the one with the higher score ... proceed with next matchedSl");
										break;
									} else {
										logger.debug("currentMatching is the one with the higher score ... carry on");
									}
								}
							}
						} else {
							logger.debug("processedSls contains equivalentSlCandidate");
						}
					}
					logger.debug("currentMostSimilarSLPerSuperAbstract: " + currentMostSimilarSLPerSuperAbstract);
					logger.debug("currentMostSimilarSLPerSuperAbstract.size(): " + currentMostSimilarSLPerSuperAbstract.size());
					logger.debug("derivedMatchingToCurrentSlToAdd: " + derivedMatchingToCurrentSlToAdd);
					if (derivedMatchingToCurrentSlToAdd != null) {
						logger.debug("derivedMatchingToCurrentSlToAdd.getConstruct1(): " + derivedMatchingToCurrentSlToAdd.getConstruct1());
						logger.debug("derivedMatchingToCurrentSlToAdd.getConstruct2(): " + derivedMatchingToCurrentSlToAdd.getConstruct2());
					}

					if (currentMostSimilarSLPerSuperAbstract.size() > 0) {
						logger.debug("found a mostSimilarSl, assume it's not the current one ... add current one");
						logger.debug("check whether currentMostSimilarSLPerSuperAbstract contains currentSl.getParentSuperAbstract");
						if (currentMostSimilarSLPerSuperAbstract.containsKey(currentSuperLexical.getParentSuperAbstract())) {
							logger.debug("currentMostSimilarSLPerSuperAbstract contains SlCandidate for currentSl.getParentSuperAbstract(), check whether it's the same derivedMatching as derivedMatchingToCurrentSlToAdd");
							if (currentMostSimilarSLPerSuperAbstract.get(currentSuperLexical.getParentSuperAbstract()).getDerivedMatching()
									.equals(derivedMatchingToCurrentSlToAdd)) {
								logger.debug("it contains the derivedMatchingToCurrentSlToAdd - do nothing");
							} else {
								logger.debug("it contains a different matching ... replace with current one ... shouldn't happen though");
								MostSimilarSuperLexicalPerSuperAbstract mostSimilarSlPerSa = new MostSimilarSuperLexicalPerSuperAbstract(
										currentSuperLexical.getParentSuperAbstract(), currentSuperLexical, currentOneToOneSuperLexicalDerivedMatching);
								currentMostSimilarSLPerSuperAbstract.put(currentSuperLexical.getParentSuperAbstract(), mostSimilarSlPerSa);
							}
						} else {
							logger.debug("currentMostSimilarSLPerSuperAbstract doesn't contain SlCandidate for saOfEquivalentSlCandidate, add it");
							MostSimilarSuperLexicalPerSuperAbstract mostSimilarSlPerSa = new MostSimilarSuperLexicalPerSuperAbstract(
									currentSuperLexical.getParentSuperAbstract(), currentSuperLexical, derivedMatchingToCurrentSlToAdd);
							currentMostSimilarSLPerSuperAbstract.put(currentSuperLexical.getParentSuperAbstract(), mostSimilarSlPerSa);
						}

					} else {
						logger.debug("didn't find mostSimilarSls");
						if (foundCurrentOneToOneSlMatching) {
							logger.debug("but came across current matching, ignore all other matchesSLs for now and proceed with next matching in ordered matchingList");
							break;
						} else {
							logger.debug("didn't come across current matching, carry on with next matchedSL");
						}
					}

					logger.debug("currentMostSimilarSLPerSuperAbstract: " + currentMostSimilarSLPerSuperAbstract);
					logger.debug("currentMostSimilarSLPerSuperAbstract.size(): " + currentMostSimilarSLPerSuperAbstract.size());
					if (currentMostSimilarSLPerSuperAbstract.size() > 0) {
						logger.debug("found equivalent Sls for matchedSL, add to equivalentSlCandidatesPerSaForEachMatchedSl");
						equivalentSlCandidatesPerSaForEachMatchedSl.put(matchedSL, currentMostSimilarSLPerSuperAbstract);
						logger.debug("equivalentSlCandidatesPerSaForEachMatchedSl.size(): " + equivalentSlCandidatesPerSaForEachMatchedSl.size());
					}
				} else {
					logger.debug("only currentSl is in equivalentSls ... ignore for now, process next in ordered list of derived matchings");
					break;
				}

				logger.debug("equivalentSlCandidatesPerSaForEachMatchedSl.size(): " + equivalentSlCandidatesPerSaForEachMatchedSl.size());

			} else {
				logger.error("not found currentSL in equivalentSlCandidates ... something's wrong");
				logger.debug("equivalentSlCandidates: " + equivalentSlCandidates);
				break;
			}
		}

		return equivalentSlCandidatesPerSaForEachMatchedSl;
	}
	*/

	protected Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> identifyGroupOfMostSimilarSuperLexicalPerSuperAbstract(
			double maxMatchingScore,
			SuperLexical currentSuperLexical,
			Map<SuperLexical, Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>> equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical) {
		logger.debug("in identifyGroupOfMostSimilarSuperLexicalPerSuperAbstract");
		logger.debug("this: " + this);
		logger.debug("maxMatchingScore: " + maxMatchingScore);
		logger.debug("currentSuperLexical: " + currentSuperLexical);
		logger.debug("equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical.size(): "
				+ equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical.size());
		for (SuperLexical matchedSuperLexical : equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical.keySet()) {
			logger.debug("matchedSuperLexical: " + matchedSuperLexical);
			for (SuperAbstract superAbstract : equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical.get(matchedSuperLexical)
					.keySet()) {
				logger.debug("superAbstract: " + superAbstract);
				logger.debug("mostSimilarSuperLexicalPerSuperAbstract.getSuperLexical(): "
						+ equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical.get(matchedSuperLexical).get(superAbstract)
								.getSuperLexical());
			}
		}
		Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> currentMostSimilarSLPerSuperAbstract = null;
		double currentMaxAvgD = 0;

		for (SuperLexical matchedSuperLexical : equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical.keySet()) {
			logger.debug("matchedSuperLexical: " + matchedSuperLexical);
			Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> groupOfMostSimilarSuperLexicalPerSuperAbstractForMatchedSuperLexical = equivalentSuperLexicalCandidatesPerSuperAbstractForEachMatchedSuperLexical
					.get(matchedSuperLexical);
			logger.debug("groupOfMostSimilarSuperLexicalPerSuperAbstractForMatchedSuperLexical: "
					+ groupOfMostSimilarSuperLexicalPerSuperAbstractForMatchedSuperLexical);
			double scoreCurrentMatchedSuperLexical = groupOfMostSimilarSuperLexicalPerSuperAbstractForMatchedSuperLexical
					.get(currentSuperLexical.getFirstAncestorSuperAbstract()).getDerivedOneToOneMatching().getSumOfMatchingScores();
			logger.debug("scoreCurrentMatchedSuperLexical: " + scoreCurrentMatchedSuperLexical);
			groupOfMostSimilarSuperLexicalPerSuperAbstractForMatchedSuperLexical.get(currentSuperLexical.getFirstAncestorSuperAbstract()).setD(
					maxMatchingScore);
			int numberOfSas = groupOfMostSimilarSuperLexicalPerSuperAbstractForMatchedSuperLexical.keySet().size();
			logger.debug("numberOfSas: " + numberOfSas);
			double sumD = this.calculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstract(scoreCurrentMatchedSuperLexical,
					maxMatchingScore, groupOfMostSimilarSuperLexicalPerSuperAbstractForMatchedSuperLexical);
			logger.debug("sumD: " + sumD);
			double avgD = sumD / numberOfSas;
			logger.debug("avgD: " + avgD);
			logger.debug("currentMaxAvgD: " + currentMaxAvgD);
			if (currentMaxAvgD < avgD) {
				currentMostSimilarSLPerSuperAbstract = groupOfMostSimilarSuperLexicalPerSuperAbstractForMatchedSuperLexical;
				currentMaxAvgD = avgD;
			}
			logger.debug("currentMaxAvgD: " + currentMaxAvgD);
			logger.debug("currentMostSimilarSLPerSuperAbstract: " + currentMostSimilarSLPerSuperAbstract);
		}
		return currentMostSimilarSLPerSuperAbstract;
	}

	protected double calculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstract(double scoreCurrentMatchedSuperLexical,
			double maxMatchingScore, Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> mostSimilarSuperLexicalPerSuperAbstract) {
		logger.debug("in calculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstract");
		logger.debug("this: " + this);
		logger.debug("scoreCurrentMatchedSuperLexical: " + scoreCurrentMatchedSuperLexical);
		logger.debug("maxMatchingScore: " + maxMatchingScore);
		logger.debug("mostSimilarSuperLexicalPerSuperAbstract: " + mostSimilarSuperLexicalPerSuperAbstract);
		double sumD = 0;
		for (SuperAbstract superAbstract : mostSimilarSuperLexicalPerSuperAbstract.keySet()) {
			logger.debug("superAbstract: " + superAbstract);
			double d = mostSimilarSuperLexicalPerSuperAbstract.get(superAbstract).getD();
			logger.debug("d: " + d);
			if (d < 0) { //this is a hack ... d for the currentSuperLexical is set to the maxMatchingScore and should not be recalculated, this hack ensures this
				d = this.calculateD(scoreCurrentMatchedSuperLexical, maxMatchingScore, mostSimilarSuperLexicalPerSuperAbstract.get(superAbstract)
						.getDerivedOneToOneMatching().getSumOfMatchingScores());
				logger.debug("d: " + superAbstract);
				mostSimilarSuperLexicalPerSuperAbstract.get(superAbstract).setD(d);
			}
			sumD += d;
			logger.debug("sumD: " + sumD);
		}
		return sumD;
	}

	protected double calculateD(double scoreCurrentMatchedSuperLexical, double maxMatchingScore, double sumOfMatchingScores) {
		logger.debug("in calculateD");
		logger.debug("this: " + this);
		logger.debug("scoreCurrentMatchedSuperLexical: " + scoreCurrentMatchedSuperLexical);
		logger.debug("maxMatchingScore: " + maxMatchingScore);
		logger.debug("sumOfMatchingScores: " + sumOfMatchingScores);
		double d = scoreCurrentMatchedSuperLexical + sumOfMatchingScores - maxMatchingScore;
		if (d < 0)
			d = 0;
		logger.debug("d: " + d);
		return d;
	}

	protected Set<SuperLexical> generateSetOfEquivalentSuperLexicals(SuperLexical currentSuperLexical,
			Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical) {
		logger.debug("in generateSetOfEquivalentSuperLexicals");
		logger.debug("this: " + this);
		logger.debug("currentSuperLexical: " + currentSuperLexical);
		logger.debug("superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical: " + superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical);
		Set<SuperLexical> equivalentSuperLexicals = new HashSet<SuperLexical>();

		// got group of SLs that's most similar to currentSL; 
		// generate all combinations of SLs with currentSL, calculate agg(D) - line 18 algorithm 7 in Chenjuan's thesis
		// and identify combination of SLs with max agg(D) ... those are identified as equivalent SLs

		logger.debug("superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical: " + superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical);
		Set<SuperAbstract> superAbstractsOfEquivalentSuperLexicals = superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical.keySet();
		logger.debug("superAbstractsOfEquivalentSuperLexicals: " + superAbstractsOfEquivalentSuperLexicals);
		List<List<SuperAbstract>> combinatorialCombinationsOfSuperAbstracts = this
				.generateCombinatorialCombinationsOfSuperAbstractsWithEachContainingSuperAbstractOfCurrentSuperLexical(
						superAbstractsOfEquivalentSuperLexicals, currentSuperLexical);
		logger.debug("combinatorialCombinationsOfSuperAbstracts: " + combinatorialCombinationsOfSuperAbstracts);
		List<SuperAbstract> bestCombination = this.identifyCombinationWithMaxAggregateD(combinatorialCombinationsOfSuperAbstracts,
				superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical);
		logger.debug("bestCombination: " + bestCombination);
		if (bestCombination != null) {
			for (SuperAbstract superAbstractOfEquivalentSuperLexical : bestCombination) {
				logger.debug("superAbstractOfEquivalentSuperLexical: " + superAbstractOfEquivalentSuperLexical);
				MostSimilarSuperLexicalPerSuperAbstract equivalentSuperLexicalDetails = superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical
						.get(superAbstractOfEquivalentSuperLexical);
				logger.debug("equivalentSuperLexicalDetails: " + equivalentSuperLexicalDetails);
				SuperLexical equivalentSuperLexical = equivalentSuperLexicalDetails.getSuperLexical();
				logger.debug("equivalentSuperLexical: " + equivalentSuperLexical);
				equivalentSuperLexicals.add(equivalentSuperLexical);
			}
		}

		return equivalentSuperLexicals;
	}

	protected List<SuperAbstract> identifyCombinationWithMaxAggregateD(List<List<SuperAbstract>> combinatorialCombinationsOfSuperAbstracts,
			Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical) {
		logger.debug("in identifyCombinationWithMaxAggregateD");
		logger.debug("this: " + this);
		List<SuperAbstract> currentBestCombination = null;
		double maxAggregateD = 0;
		for (List<SuperAbstract> currentCombination : combinatorialCombinationsOfSuperAbstracts) {
			double sumD = this.calculateSumOfDOfCombination(currentCombination, superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical);
			logger.debug("sumD: " + sumD);
			double square = sumD * sumD;
			logger.debug("square: " + square);
			double aggD = square / currentCombination.size();
			logger.debug("aggD: " + aggD);
			if (maxAggregateD < aggD) {
				maxAggregateD = aggD;
				currentBestCombination = currentCombination;
			}
			logger.debug("maxAggregateD: " + maxAggregateD);
			logger.debug("currentBestCombination: " + currentBestCombination);
		}
		return currentBestCombination;
	}

	protected double calculateSumOfDOfCombination(List<SuperAbstract> combinationOfSuperAbstracts,
			Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical) {
		logger.debug("in calculateSumOfDOfCombination");
		logger.debug("this: " + this);
		double sumD = 0d;
		for (SuperAbstract superAbstract : combinationOfSuperAbstracts) {
			logger.debug("superAbstract: " + superAbstract);
			MostSimilarSuperLexicalPerSuperAbstract mostSimilarSuperLexicalPerSuperAbstract = superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical
					.get(superAbstract);
			sumD += mostSimilarSuperLexicalPerSuperAbstract.getD();
		}
		logger.debug("sumD: " + sumD);
		return sumD;
	}

	/*
	protected LinkedHashMap<SuperLexical, Set<SuperLexical>> fillEquivalentSlsGroupMapWithEquivalentSlsForCurrentSl(SuperLexical currentSl,
			LinkedHashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> currentMostSimilarSLPerSuperAbstract,
			LinkedHashSet<SuperLexical> processedSls, LinkedHashMap<SuperLexical, Set<SuperLexical>> slEquivalentSlsGroupMap) {

		//TODO still not ideal, carrying slEquivalentSlsGroupMap and processedSls around and filling them as I go along ... more refactoring to do

		//got group of SLs that's most similar to currentSL
		//generate all combinations of SLs with currentSL, calculate agg(D) - line 18 algorithm 7 in Chenjuan's thesis
		//and identify combination of SLs with max agg(D) ... those are identified as equivalent SLs

		List<SuperAbstract> currentBestCombination = new ArrayList<SuperAbstract>();

		double maxAggD = 0;
		LinkedHashMap<String, LinkedHashSet<SuperLexical>> slNameSuperLexicals = new LinkedHashMap<String, LinkedHashSet<SuperLexical>>();

		if (currentMostSimilarSLPerSuperAbstract != null) {

			logger.debug("currentMostSimilarSLPerSuperAbstract: " + currentMostSimilarSLPerSuperAbstract);
			for (SuperAbstract sa : currentMostSimilarSLPerSuperAbstract.keySet()) {
				logger.debug("sa: " + sa);
				logger.debug("currentMostSimilarSLPerSuperAbstract.get(sa).sl: " + currentMostSimilarSLPerSuperAbstract.get(sa).getSl());
				logger.debug("currentMostSimilarSLPerSuperAbstract.get(sa).d: " + currentMostSimilarSLPerSuperAbstract.get(sa).getD());
				logger.debug("currentMostSimilarSLPerSuperAbstract.get(sa).derivedMatching: "
						+ currentMostSimilarSLPerSuperAbstract.get(sa).getDerivedMatching());
			}

			//TODO something wrong here ... :-(

			Set<SuperAbstract> sAsOfEquivalentSLs = currentMostSimilarSLPerSuperAbstract.keySet();
			List<SuperAbstract> sAsOfEquivalentSLsList = new ArrayList<SuperAbstract>();
			for (SuperAbstract sa : sAsOfEquivalentSLs)
				sAsOfEquivalentSLsList.add(sa);
			List<List<SuperAbstract>> combinatorialCombinationsOfSas = this.generateCombinatorialCombinationsOfSuperAbstracts(sAsOfEquivalentSLsList,
					currentSl);
			for (List<SuperAbstract> currentCombination : combinatorialCombinationsOfSas) {
				logger.debug("currentCombination: " + currentCombination);
				logger.debug("currentCombination.size(): " + currentCombination.size());
				double sumD = 0d;
				for (SuperAbstract sa : currentCombination) {
					logger.debug("sa: " + sa);
					MostSimilarSuperLexicalPerSuperAbstract mostSimilarSlPerSa = currentMostSimilarSLPerSuperAbstract.get(sa);
					logger.debug("mostSimilarSlPerSa.getSl(): " + mostSimilarSlPerSa.getSl());
					logger.debug("mostSimilarSlPerSa.getDerivedMatching(): " + mostSimilarSlPerSa.getDerivedMatching());
					logger.debug("mostSimilarSlPerSa.getD(): " + mostSimilarSlPerSa.getD());
					sumD += mostSimilarSlPerSa.getD();
					logger.debug("sumD: " + sumD);
				}
				double square = sumD * sumD;
				logger.debug("square: " + square);
				double aggD = square / currentCombination.size();
				logger.debug("aggD: " + aggD);
				logger.debug("maxAggD: " + maxAggD);
				if (maxAggD < aggD) {
					logger.debug("found new maxAggD - keep its combination in currentBestCombination");
					maxAggD = aggD;
					currentBestCombination = currentCombination;
				}
				logger.debug("currentBestCombination: " + currentBestCombination);
				if (currentBestCombination != null)
					logger.debug("currentBestCombination.size(): " + currentBestCombination.size());
			}

			//identified equivalent sls
			//gather all SuperLexicals with same name

			for (SuperAbstract superAbstract : currentMostSimilarSLPerSuperAbstract.keySet()) {
				MostSimilarSuperLexicalPerSuperAbstract equivalentSlDetails = currentMostSimilarSLPerSuperAbstract.get(superAbstract);
				logger.debug("equivalentSlDetails: " + equivalentSlDetails);
				SuperLexical equivalentSl = equivalentSlDetails.getSl();
				logger.debug("equivalentSl: " + equivalentSl);
				String slName = equivalentSl.getName();
				logger.debug("slName: " + slName);
				if (slNameSuperLexicals.containsKey(slName.toLowerCase())) {
					LinkedHashSet<SuperLexical> sls = slNameSuperLexicals.get(slName.toLowerCase());
					sls.add(equivalentSl);
					logger.debug("added equivalentSl to sls");
				} else {
					logger.debug("new name");
					LinkedHashSet<SuperLexical> sls = new LinkedHashSet<SuperLexical>();
					sls.add(equivalentSl);
					logger.debug("added equivalentSl to sls");
					slNameSuperLexicals.put(slName.toLowerCase(), sls);
					logger.debug("put equivalentSl in slNameSuperLexicals");
				}
			}
		}

		//create group and add all sls to processedSls
		//check whether it's got all the equivalent sls with the same names, if not, add them (only for the most frequent name)

		logger.debug("currentBestCombination: " + currentBestCombination);
		if (currentBestCombination != null)
			logger.debug("currentBestCombination.size(): " + currentBestCombination.size());

		//EquivalentSuperLexicalsGroup equivalentSuperLexicalsGroup = new EquivalentSuperLexicalsGroup();
		Set<SuperLexical> equivalentSuperLexicalsSet = new HashSet<SuperLexical>();

		if (currentBestCombination != null) {

			LinkedHashMap<String, LinkedHashSet<SuperLexical>> slNameSlsInBestCombination = new LinkedHashMap<String, LinkedHashSet<SuperLexical>>();

			for (SuperAbstract saOfEquivalentSl : currentBestCombination) {
				logger.debug("saOfEquivalentSl: " + saOfEquivalentSl);
				MostSimilarSuperLexicalPerSuperAbstract equivalentSlDetails = currentMostSimilarSLPerSuperAbstract.get(saOfEquivalentSl);
				logger.debug("equivalentSlDetails: " + equivalentSlDetails);
				SuperLexical equivalentSl = equivalentSlDetails.getSl();
				logger.debug("equivalentSl: " + equivalentSl);
				String equivalentSlName = equivalentSl.getName();
				logger.debug("equivalentSlName: " + equivalentSlName);
				if (slNameSlsInBestCombination.containsKey(equivalentSlName.toLowerCase())) {
					LinkedHashSet<SuperLexical> sls = slNameSlsInBestCombination.get(equivalentSlName.toLowerCase());
					sls.add(equivalentSl);
					logger.debug("added equivalentSl to sls");
				} else {
					logger.debug("new name");
					LinkedHashSet<SuperLexical> sls = new LinkedHashSet<SuperLexical>();
					sls.add(equivalentSl);
					logger.debug("added equivalentSl to sls");
					slNameSlsInBestCombination.put(equivalentSlName.toLowerCase(), sls);
					logger.debug("put equivalentSl in slNameSlsInBestCombination");
				}
				equivalentSuperLexicalsSet.add(equivalentSl);
				if (!slEquivalentSlsGroupMap.containsKey(equivalentSl)) {
					logger.debug("equivalentSlsGroupMap doesn't contain equivalentSl, add it");
					slEquivalentSlsGroupMap.put(equivalentSl, equivalentSuperLexicalsSet);
					logger.debug("add equivalentSl to processedSls");
					processedSls.add(equivalentSl);
				} else
					logger.debug("equivalentSlsGroupMap already contains equivalentSl ... something wrong here - sort this");
			}

			logger.debug("slNameSlsInBestCombination.size(): " + slNameSlsInBestCombination);
			if (slNameSlsInBestCombination.size() > 1) {
				logger.debug("sls with different names in bestCombination, check for most frequent one");
				int maxNoOfSlsWithSameName = 0;
				LinkedHashSet<SuperLexical> slsWithSameName = null;
				String mostFrequentSlName = null;
				for (String slName : slNameSlsInBestCombination.keySet()) {
					logger.debug("slName: " + slName);
					LinkedHashSet<SuperLexical> slsWithName = slNameSlsInBestCombination.get(slName);
					logger.debug("maxNoOfSlsWithSameName: " + maxNoOfSlsWithSameName);
					logger.debug("slsWithName.size(); " + slsWithName.size());
					if (slsWithName.size() > maxNoOfSlsWithSameName) {
						logger.debug("new most frequent name");
						slsWithSameName = slsWithName;
						mostFrequentSlName = slName;
						maxNoOfSlsWithSameName = slsWithName.size();
					} else if (slsWithName.size() == maxNoOfSlsWithSameName) {
						logger.debug("same number of occurrences in group for slName - check for overall number of occurrences and go with max");
						LinkedHashSet<SuperLexical> slsWithSlName = slNameSuperLexicals.get(slName.toLowerCase());
						logger.debug("slsWithSlName.size(): " + slsWithSlName.size());
						LinkedHashSet<SuperLexical> slsWithMostFrequentSlName = slNameSuperLexicals.get(mostFrequentSlName.toLowerCase());
						logger.debug("slsWithMostFrequentSlName.size(): " + slsWithMostFrequentSlName.size());
						if (slsWithSlName.size() > slsWithMostFrequentSlName.size()) {
							logger.debug("more sls with slName overall - keep");
							slsWithSameName = slsWithName;
							mostFrequentSlName = slName;
							maxNoOfSlsWithSameName = slsWithName.size();
						} else {
							logger.debug("no more sls with slName overall - do nothing");
						}
					} else {
						logger.debug("no new most frequent name - do nothing");
					}
				}
				logger.debug("maxNoOfSlsWithSameName: " + maxNoOfSlsWithSameName);
				logger.debug("slsWithSameName.size(): " + slsWithSameName.size());
				logger.debug("mostFrequentSlName: " + mostFrequentSlName);

				if (!slsWithSameName.containsAll(slNameSuperLexicals.get(mostFrequentSlName))) {
					logger.debug("don't have all sls with same most frequent name ... add those missing");
					for (SuperLexical sl : slNameSuperLexicals.get(mostFrequentSlName)) {
						logger.debug("sl: " + sl);
						if (!slsWithSameName.contains(sl)) {
							logger.debug("sl not in group of equivalent sls, add it");
							equivalentSuperLexicalsSet.add(sl);
							if (!slEquivalentSlsGroupMap.containsKey(sl)) {
								logger.debug("equivalentSlsGroupMap doesn't contain sl, add it");
								slEquivalentSlsGroupMap.put(sl, equivalentSuperLexicalsSet);
								logger.debug("add equivalentSl to processedSls");
								processedSls.add(sl);
							} else
								logger.debug("equivalentSlsGroupMap already contains equivalentSl ... something wrong here - sort this");
						}
					}
				} else {
					logger.debug("got all sls with most frequent name ... do nothing");
				}
			}
		}

		return slEquivalentSlsGroupMap;
	}
	*/

	/*
	 * generate all bit patterns of length superAbstracts.size() - there are 2^n bit patterns
	 * if bit patterns are treated as binary numbers we only have to enumerate the binary numbers 0, 1, ... 2^n-1
	 * and generate the corresponding bit pattern for each bit pattern, create corresponding subset add those elements to list whose bit is 1
	 */
	protected List<List<SuperAbstract>> generateCombinatorialCombinationsOfSuperAbstractsWithEachContainingSuperAbstractOfCurrentSuperLexical(
			Set<SuperAbstract> superAbstracts, SuperLexical currentSuperLexical) {
		logger.debug("in generateCombinatorialCombinationsOfSuperAbstractsWithEachContainingSuperAbstractOfCurrentSuperLexical");
		logger.debug("this: " + this);
		logger.debug("superAbstracts.size(): " + superAbstracts.size());
		logger.debug("currentSuperLexical: " + currentSuperLexical);
		List<SuperAbstract> superAbstractsList = new ArrayList<SuperAbstract>();
		for (SuperAbstract sa : superAbstracts) {
			logger.debug("sa: " + sa);
			superAbstractsList.add(sa);
		}
		logger.debug("superAbstractsList.size(): " + superAbstractsList);
		List<List<SuperAbstract>> allCombinations = new ArrayList<List<SuperAbstract>>();
		SuperAbstract superAbstractOfCurrentSuperLexical = currentSuperLexical.getFirstAncestorSuperAbstract();
		superAbstractsList.remove(superAbstractOfCurrentSuperLexical);
		int numberOfSuperAbstracts = superAbstractsList.size();
		logger.debug("numberOfSuperAbstracts: " + numberOfSuperAbstracts);
		for (int i = 0; i < Math.pow(2, numberOfSuperAbstracts); i++) {
			logger.debug("i: " + i);
			String binaryString = Integer.toBinaryString(i);
			logger.debug("binaryString.length(): " + binaryString.length());
			while (binaryString.length() < numberOfSuperAbstracts)
				binaryString = "0" + binaryString;
			logger.debug("binaryString: " + binaryString);
			List<SuperAbstract> currentSubset = new ArrayList<SuperAbstract>();
			for (int j = 0; j < binaryString.length(); j++) {
				logger.debug("j: " + j);
				char bit = binaryString.charAt(j);
				logger.debug("bit: " + bit);
				if (bit == '1') {
					SuperAbstract selectedSuperAbstract = superAbstractsList.get(j);
					logger.debug("selectedSuperAbstract: " + selectedSuperAbstract);
					currentSubset.add(selectedSuperAbstract);
				}
			}
			currentSubset.add(superAbstractOfCurrentSuperLexical);
			logger.debug("currentSubset.size(): " + currentSubset.size());
			allCombinations.add(currentSubset);
			logger.debug("allCombinations.size(): " + allCombinations.size());
		}
		return allCombinations;
	}

}
