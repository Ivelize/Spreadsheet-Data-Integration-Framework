/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.MostSimilarSuperLexicalPerSuperAbstract;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorService;

/**
 * @author chedeler
 *
 */
@Service(value = "derivedOneToOneMatchingsGeneratorService")
public class DerivedOneToOneMatchingsGeneratorServiceImpl implements DerivedOneToOneMatchingsGeneratorService {

	private static Logger logger = Logger.getLogger(DerivedOneToOneMatchingsGeneratorServiceImpl.class);

	private LinkedHashSet<Schema> sourceSchemas;
	private LinkedHashSet<Schema> targetSchemas;
	private List<Matching> matchings;

	private List<DerivedOneToOneMatching> derivedOneToOneMatchings;

	//TODO this needs to be changed as this restricts the type of relationships we can look for, e.g., relationships between superAbstracts and superLexicals will not be found ...
	//TODO leave it for now though as Chenjuan's inferCorrespondence doesn't look for those for now
	private Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap = null;
	private Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap = null;

	private Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap = null;
	private Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap = null;

	private double maxSumOfMatchingScore;

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#generateDerivedMatchings(java.util.Map)
	 */
	public List<DerivedOneToOneMatching> generateDerivedMatchings(LinkedHashSet<Schema> sourceSchemas, LinkedHashSet<Schema> targetSchemas,
			List<Matching> matchings) {
		logger.debug("in generateDerivedMatchings");
		logger.debug("this: " + this);
		logger.debug("sourceSchemas: " + sourceSchemas);
		logger.debug("targetSchemas: " + targetSchemas);
		logger.debug("matchings: " + matchings);

		init();

		if (sourceSchemas != null)
			logger.debug("sourceSchemas.size(): " + sourceSchemas.size());
		if (targetSchemas != null)
			logger.debug("targetSchemas.size(): " + targetSchemas.size());
		if (matchings != null)
			logger.debug("matching.size()s: " + matchings.size());

		this.sourceSchemas = sourceSchemas;
		this.targetSchemas = targetSchemas;
		this.matchings = matchings;

		Map<CanonicalModelConstruct, Map<CanonicalModelConstruct, Set<Matching>>> matchedSourceConstructsTargetConstructsMatchingsMap = this
				.generateMatchedSourceConstructsTargetConstructsMatchingsMap(sourceSchemas, targetSchemas, matchings);
		logger.debug("matchedSourceConstructsTargetConstructsMatchingsMap: " + matchedSourceConstructsTargetConstructsMatchingsMap);
		logger.debug("matchedSourceConstructsTargetConstructsMatchingsMap.size(): " + matchedSourceConstructsTargetConstructsMatchingsMap.size());

		List<DerivedOneToOneMatching> derivedOneToOneMatchings = new ArrayList<DerivedOneToOneMatching>();
		for (CanonicalModelConstruct sourceConstruct : matchedSourceConstructsTargetConstructsMatchingsMap.keySet()) {
			logger.debug("sourceConstruct: " + sourceConstruct);
			for (CanonicalModelConstruct targetConstruct : matchedSourceConstructsTargetConstructsMatchingsMap.get(sourceConstruct).keySet()) {
				logger.debug("targetConstruct: " + targetConstruct);
				Set<Matching> matchingsBetweenSourceAndTargetConstruct = matchedSourceConstructsTargetConstructsMatchingsMap.get(sourceConstruct)
						.get(targetConstruct);
				logger.debug("matchingsBetweenSourceAndTargetConstruct.size(): " + matchingsBetweenSourceAndTargetConstruct);
				logger.debug("matchingsBetweenSourceAndTargetConstruct: " + matchingsBetweenSourceAndTargetConstruct);
				DerivedOneToOneMatching derivedMatching = this.createDerivedMatching(sourceConstruct, targetConstruct,
						matchingsBetweenSourceAndTargetConstruct);
				logger.debug("derivedMatching: " + derivedMatching);
				logger.debug("derivedMatching.getConstruct1(): " + derivedMatching.getConstruct1());
				logger.debug("derivedMatching.getConstruct2(): " + derivedMatching.getConstruct2());
				logger.debug("derivedMatching.getNumberOfMatchings(): " + derivedMatching.getNumberOfMatchings());
				logger.debug("derivedMatching.getSumOfMatchingScores(): " + derivedMatching.getSumOfMatchingScores());
				derivedOneToOneMatchings.add(derivedMatching);
				//this.maxSumOfMatchingScore = this.determineCurrentMaxSumOfMatchingScore(derivedMatching);
				if (sourceConstruct instanceof SuperLexical && targetConstruct instanceof SuperLexical) {
					this.addToSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap(sourceConstruct, targetConstruct, derivedMatching);
					this.addToTargetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap(sourceConstruct, targetConstruct, derivedMatching);
				} else if (sourceConstruct instanceof SuperAbstract && targetConstruct instanceof SuperAbstract) {
					this.addToSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap(sourceConstruct, targetConstruct, derivedMatching);
					this.addToTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap(sourceConstruct, targetConstruct, derivedMatching);
				}
			}
		}
		this.derivedOneToOneMatchings = derivedOneToOneMatchings;
		return derivedOneToOneMatchings;
	}

	/*
	public Map<CanonicalModelConstruct[], Double> getConstructsArrayWithMaxAvgMatchingScores(CanonicalModelConstruct[] constructsArray,
			List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> constructMatchedWithConstructArraysAndDerivedMatchingsList) {
		logger.debug("in getConstructsArrayWithMaxAvgMatchingScores");
		logger.debug("constructsArray: " + constructsArray);
		for (CanonicalModelConstruct constructInArray : constructsArray)
			logger.debug("constructInArray: " + constructInArray);
		logger.debug("constructMatchedWithConstructArraysAndDerivedMatchingsList: " + constructMatchedWithConstructArraysAndDerivedMatchingsList);

		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> matchedConstructsWithCollatedDerivedOneToOneMatchings = this
				.groupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings(constructsArray,
						constructMatchedWithConstructArraysAndDerivedMatchingsList);
		Map<CanonicalModelConstruct[], Double> matchedConstructsWithAvgMatchingScore = this.calculateAvgOfMatchingScoresForMatchedConstructsArrays(
				constructsArray, matchedConstructsWithCollatedDerivedOneToOneMatchings);
		return this.identifyConstructsArrayWithMaxAvgMatchingScores(matchedConstructsWithAvgMatchingScore);
	}
	*/

	public Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> groupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings(
			List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> constructMatchedWithConstructArraysAndDerivedMatchingsList) {
		logger.debug("in groupMatchedConstructsAndCalculateSumOfMatchingScore");
		logger.debug("this: " + this);
		logger.debug("constructMatchedWithConstructArraysAndDerivedMatchingsList: " + constructMatchedWithConstructArraysAndDerivedMatchingsList);
		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> matchedConstructsWithCollatedDerivedMatchings = new HashMap<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>();
		for (Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> mapOfMatchedConstructsWithDerivedMatchings : constructMatchedWithConstructArraysAndDerivedMatchingsList) {
			logger.debug("mapOfMatchedConstructsWithDerivedMatchings: " + mapOfMatchedConstructsWithDerivedMatchings);
			for (CanonicalModelConstruct[] matchedConstructsArray : mapOfMatchedConstructsWithDerivedMatchings.keySet()) {
				logger.debug("matchedConstructsArray: " + matchedConstructsArray);
				for (CanonicalModelConstruct matchedConstruct : matchedConstructsArray)
					logger.debug("matchedConstruct: " + matchedConstruct);
				if (matchedConstructsWithCollatedDerivedMatchings.containsKey(matchedConstructsArray)) {
					logger.debug("matchedConstructsWithSumOfMatchingScore contains matchedConstructsArray");
					matchedConstructsWithCollatedDerivedMatchings.get(matchedConstructsArray).addAll(
							mapOfMatchedConstructsWithDerivedMatchings.get(matchedConstructsArray));
				} else {
					logger.debug("matchedConstructsWithSumOfMatchingScore doesn't contain matchedConstructsArray");
					matchedConstructsWithCollatedDerivedMatchings.put(matchedConstructsArray,
							mapOfMatchedConstructsWithDerivedMatchings.get(matchedConstructsArray));
				}
			}
		}
		return matchedConstructsWithCollatedDerivedMatchings;
	}

	public Map<CanonicalModelConstruct[], Double> calculateAvgOfMatchingScoresForMatchedConstructsArrays(CanonicalModelConstruct[] constructsArray,
			Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> mapOfMatchedConstructsWithDerivedMatchings) {
		logger.debug("in calculateAvgOfMatchingScoresForMatchedConstructsArrays");
		logger.debug("this: " + this);
		logger.debug("constructsArray: " + constructsArray);
		for (CanonicalModelConstruct constructInArray : constructsArray)
			logger.debug("constructInArray: " + constructInArray);
		logger.debug("mapOfMatchedConstructsWithDerivedMatchings: " + mapOfMatchedConstructsWithDerivedMatchings);
		Map<CanonicalModelConstruct[], Double> matchedConstructsWithAvgOfMatchingScore = new HashMap<CanonicalModelConstruct[], Double>();
		for (CanonicalModelConstruct[] matchedConstructsArray : mapOfMatchedConstructsWithDerivedMatchings.keySet()) {
			logger.debug("matchedConstructsArray: " + matchedConstructsArray);
			for (CanonicalModelConstruct matchedConstruct : matchedConstructsArray)
				logger.debug("matchedConstruct: " + matchedConstruct);
			double sumOfMatchingScores = 0d;
			int numberOfMatchings = 0;
			for (DerivedOneToOneMatching derivedMatching : mapOfMatchedConstructsWithDerivedMatchings.get(matchedConstructsArray)) {
				logger.debug("derivedMatching: " + derivedMatching);
				sumOfMatchingScores += derivedMatching.getSumOfMatchingScores();
				numberOfMatchings++;
			}
			//double avgMatchingScore = this.calculateAvgMatchingScore(sumOfMatchingScores, constructsArray.length, matchedConstructsArray.length);
			double avgMatchingScore = sumOfMatchingScores / numberOfMatchings;
			matchedConstructsWithAvgOfMatchingScore.put(matchedConstructsArray, avgMatchingScore);
		}
		return matchedConstructsWithAvgOfMatchingScore;
	}

	public Map<CanonicalModelConstruct[], Double> identifyConstructsArrayWithMaxAvgMatchingScores(
			Map<CanonicalModelConstruct[], Double> matchedConstructsWithAvgOfMatchingScore) {
		logger.debug("in identifyConstructArrayWithMaxMatchingScores");
		logger.debug("this: " + this);
		logger.debug("matchedConstructsWithSumOfMatchingScore: " + matchedConstructsWithAvgOfMatchingScore);
		Map<CanonicalModelConstruct[], Double> constructsArrayWithMaxAvgMatchingScore = new HashMap<CanonicalModelConstruct[], Double>(); //TODO not the best data structure
		CanonicalModelConstruct[] constructArrayWithMaxMatchingScore = null;
		double maxAvgMatchingScore = 0d;
		for (CanonicalModelConstruct[] matchedConstructs : matchedConstructsWithAvgOfMatchingScore.keySet()) {
			logger.debug("matchedConstructs: " + matchedConstructs);
			for (CanonicalModelConstruct matchedConstruct : matchedConstructs) {
				logger.debug("matchedConstruct: " + matchedConstruct);
			}
			double avgMatchingScore = matchedConstructsWithAvgOfMatchingScore.get(matchedConstructs);
			logger.debug("matchedConstructsWithAvgOfMatchingScore: " + matchedConstructsWithAvgOfMatchingScore);
			logger.debug("avgMatchingScore: " + avgMatchingScore);
			logger.debug("maxAvgMatchingScore: " + maxAvgMatchingScore);
			if (constructArrayWithMaxMatchingScore == null || maxAvgMatchingScore < avgMatchingScore) {
				logger.debug("constructArrayWithMaxMatchingScore == null or found new maxAvgMatchingScore");
				constructArrayWithMaxMatchingScore = matchedConstructs;
				maxAvgMatchingScore = avgMatchingScore;
			}
			logger.debug("constructArrayWithMaxMatchingScore: " + constructArrayWithMaxMatchingScore);
			logger.debug("maxAvgMatchingScore: " + maxAvgMatchingScore);
		}
		logger.debug("constructArrayWithMaxMatchingScore: " + constructArrayWithMaxMatchingScore);
		logger.debug("maxAvgMatchingScore: " + maxAvgMatchingScore);
		if (constructArrayWithMaxMatchingScore != null)
			constructsArrayWithMaxAvgMatchingScore.put(constructArrayWithMaxMatchingScore, maxAvgMatchingScore);
		return constructsArrayWithMaxAvgMatchingScore;
	}

	protected double calculateAvgMatchingScore(double sumOfMatchingScore, int numberOfConstructs1, int numberOfConstructs2) {
		logger.debug("this: " + this);
		if (numberOfConstructs1 == 0 && numberOfConstructs2 == 0)
			return 0d;
		return sumOfMatchingScore / ((double) numberOfConstructs1 + (double) numberOfConstructs2);
	}

	//TODO not all required information is provided as input, needs the maps here in DerivedOneToOneMatchingsGeneratorServiceImpl
	public Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings(
			CanonicalModelConstruct construct, Set<CanonicalModelConstruct[]> setOfArraysOfConstructs, boolean isSourceToTarget) {
		//TODO isSourceToTarget isn't used ... but may be needed if it can't be ensured that the sets of source and target constructs are distinct
		logger.debug("in getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings");
		logger.debug("this: " + this);
		logger.debug("construct: " + construct);
		logger.debug("setOfArraysOfConstructs: " + setOfArraysOfConstructs);
		logger.debug("isSourceToTarget: " + isSourceToTarget);
		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> matchedConstructArraysWithDerivedMatchingsSetMap = new HashMap<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>();
		for (CanonicalModelConstruct[] arrayOfConstructs : setOfArraysOfConstructs) {
			Set<DerivedOneToOneMatching> setOfMatchings = new HashSet<DerivedOneToOneMatching>();
			logger.debug("arrayOfConstructs: " + arrayOfConstructs);
			int numberOfNullElements = 0;
			for (CanonicalModelConstruct constructInArray : arrayOfConstructs) {
				logger.debug("constructInArray: " + constructInArray);
				DerivedOneToOneMatching derivedMatching = null;
				if (constructInArray != null) {
					logger.debug("constructInArray != null - check for derivedMatching");
					if (construct instanceof SuperAbstract && constructInArray instanceof SuperAbstract) {
						logger.debug("both constructs are SuperAbstracts");
						derivedMatching = this.getDerivedOneToOneMatchingBetweenSourceAndTargetSuperAbstract((SuperAbstract) construct,
								(SuperAbstract) constructInArray);
						logger.debug("derivedMatching: " + derivedMatching);
					} else if (construct instanceof SuperLexical && constructInArray instanceof SuperLexical) {
						logger.debug("both constructs are SuperLexicals");
						derivedMatching = this.getDerivedOneToOneMatchingBetweenSourceAndTargetSuperLexical((SuperLexical) construct,
								(SuperLexical) constructInArray);
						logger.debug("derivedMatching: " + derivedMatching);
					} else {
						logger.debug("not the case that both constructs are either SuperAbstracts or SuperLexicals ... ignored for now as inferCorrespondences doesn't deal with it up to now");
					}
				} else {
					logger.debug("constructInArray == null - create DerivedMatching with 0.0 as score");
					derivedMatching = new DerivedOneToOneMatching(construct, constructInArray);
					derivedMatching.setSumOfMatchingScores(0d);
					logger.debug("derivedMatching: " + derivedMatching);
					numberOfNullElements++;
				}
				logger.debug("derivedMatching: " + derivedMatching);
				if (derivedMatching != null) {
					logger.debug("found derived matching - add to set");
					setOfMatchings.add(derivedMatching);
				}
			}
			logger.debug("setOfMatchings.size(): " + setOfMatchings.size());
			logger.debug("numberOfNullElements: " + numberOfNullElements);
			if (setOfMatchings.size() > numberOfNullElements) {
				logger.debug("found matchings between construct and constructs in arrayOfConstructs, add to matchedConstructArraysWithDerivedMatchingsSetMap");
				matchedConstructArraysWithDerivedMatchingsSetMap.put(arrayOfConstructs, setOfMatchings);
			} else {
				logger.debug("only found matchings between construct and null elements, ignore");
			}
		}
		return matchedConstructArraysWithDerivedMatchingsSetMap;
	}

	protected Map<CanonicalModelConstruct, Map<CanonicalModelConstruct, Set<Matching>>> generateMatchedSourceConstructsTargetConstructsMatchingsMap(
			LinkedHashSet<Schema> sourceSchemas, LinkedHashSet<Schema> targetSchemas, List<Matching> matchings) {
		logger.debug("in generateMatchedSourceConstructsTargetConstructsMatchingsMap");
		logger.debug("this: " + this);
		logger.debug("sourceSchemas: " + sourceSchemas);
		logger.debug("targetSchemas: " + targetSchemas);
		logger.debug("matchings: " + matchings);
		if (sourceSchemas != null)
			logger.debug("sourceSchemas.size(): " + sourceSchemas.size());
		if (targetSchemas != null)
			logger.debug("targetSchemas.size(): " + targetSchemas.size());
		if (matchings != null)
			logger.debug("matching.size()s: " + matchings.size());
		Map<CanonicalModelConstruct, Map<CanonicalModelConstruct, Set<Matching>>> matchedSourceConstructsTargetConstructsMatchingsMap = new HashMap<CanonicalModelConstruct, Map<CanonicalModelConstruct, Set<Matching>>>();
		Set<CanonicalModelConstruct> constructsInSourceSchemas = this.generateSetOfSuperAbstractsAndSuperLexicalsInSchemas(sourceSchemas);
		Set<CanonicalModelConstruct> constructsInTargetSchemas = this.generateSetOfSuperAbstractsAndSuperLexicalsInSchemas(targetSchemas);
		logger.debug("constructsInSourceSchemas: " + constructsInSourceSchemas);
		logger.debug("constructsInSourceSchemas.size(): " + constructsInSourceSchemas.size());
		logger.debug("constructsInTargetSchemas: " + constructsInTargetSchemas);
		logger.debug("constructsInTargetSchemas.size(): " + constructsInTargetSchemas.size());
		for (Matching matching : matchings) {
			logger.debug("matching: " + matching);
			if (matching instanceof OneToOneMatching) {
				logger.debug("is OneToOneMatching");
				matchedSourceConstructsTargetConstructsMatchingsMap = this
						.processMatchingAndPlaceInMatchedSourceConstructsTargetConstructsMatchingsMap(
								matchedSourceConstructsTargetConstructsMatchingsMap, constructsInSourceSchemas, constructsInTargetSchemas,
								(OneToOneMatching) matching);
			}
		}
		return matchedSourceConstructsTargetConstructsMatchingsMap;
	}

	protected Map<CanonicalModelConstruct, Map<CanonicalModelConstruct, Set<Matching>>> processMatchingAndPlaceInMatchedSourceConstructsTargetConstructsMatchingsMap(
			Map<CanonicalModelConstruct, Map<CanonicalModelConstruct, Set<Matching>>> matchedSourceConstructsTargetConstructsMatchingsMap,
			Set<CanonicalModelConstruct> constructsInSourceSchemas, Set<CanonicalModelConstruct> constructsInTargetSchemas,
			OneToOneMatching oneToOneMatching) {
		logger.debug("in processMatchingAndPlaceInMatchedSourceConstructsTargetConstructsMatchingsMap");
		logger.debug("this: " + this);
		logger.debug("constructsInSourceSchemas: " + constructsInSourceSchemas);
		logger.debug("constructsInSourceSchemas.size(): " + constructsInSourceSchemas.size());
		logger.debug("constructsInTargetSchemas: " + constructsInTargetSchemas);
		logger.debug("constructsInTargetSchemas.size(): " + constructsInTargetSchemas.size());
		logger.debug("matchedSourceConstructsTargetConstructsMatchingsMap: " + matchedSourceConstructsTargetConstructsMatchingsMap);
		logger.debug("matchedSourceConstructsTargetConstructsMatchingsMap.size(): " + matchedSourceConstructsTargetConstructsMatchingsMap.size());
		CanonicalModelConstruct sourceConstruct = null;
		CanonicalModelConstruct targetConstruct = null;
		if (constructsInSourceSchemas.contains(oneToOneMatching.getConstruct1())
				&& constructsInTargetSchemas.contains(oneToOneMatching.getConstruct2())) {
			sourceConstruct = oneToOneMatching.getConstruct1();
			targetConstruct = oneToOneMatching.getConstruct2();
		} else if (constructsInSourceSchemas.contains(oneToOneMatching.getConstruct2())
				&& constructsInTargetSchemas.contains(oneToOneMatching.getConstruct1())) {
			sourceConstruct = oneToOneMatching.getConstruct2();
			targetConstruct = oneToOneMatching.getConstruct1();
		}
		logger.debug("sourceConstruct: " + sourceConstruct);
		logger.debug("targetConstruct: " + targetConstruct);
		if (sourceConstruct != null & targetConstruct != null) {
			matchedSourceConstructsTargetConstructsMatchingsMap = this.placeOneToOneMatchingInMatchedSourceConstructsTargetConstructsMatchingsMap(
					matchedSourceConstructsTargetConstructsMatchingsMap, sourceConstruct, targetConstruct, oneToOneMatching);
		}
		logger.debug("matchedSourceConstructsTargetConstructsMatchingsMap: " + matchedSourceConstructsTargetConstructsMatchingsMap);
		logger.debug("matchedSourceConstructsTargetConstructsMatchingsMap.size(): " + matchedSourceConstructsTargetConstructsMatchingsMap.size());
		return matchedSourceConstructsTargetConstructsMatchingsMap;
	}

	protected Map<CanonicalModelConstruct, Map<CanonicalModelConstruct, Set<Matching>>> placeOneToOneMatchingInMatchedSourceConstructsTargetConstructsMatchingsMap(
			Map<CanonicalModelConstruct, Map<CanonicalModelConstruct, Set<Matching>>> matchedSourceConstructsTargetConstructsMatchingsMap,
			CanonicalModelConstruct sourceConstruct, CanonicalModelConstruct targetConstruct, OneToOneMatching oneToOneMatching) {
		logger.debug("in placeOneToOneMatchingInMatchedSourceConstructsTargetConstructsMatchingsMap");
		logger.debug("this: " + this);
		logger.debug("matchedSourceConstructsTargetConstructsMatchingsMap: " + matchedSourceConstructsTargetConstructsMatchingsMap);
		logger.debug("matchedSourceConstructsTargetConstructsMatchingsMap.size(): " + matchedSourceConstructsTargetConstructsMatchingsMap.size());
		logger.debug("sourceConstruct: " + sourceConstruct);
		logger.debug("targetConstruct: " + targetConstruct);
		logger.debug("oneToOneMatching: " + oneToOneMatching);
		if ((oneToOneMatching.getConstruct1().equals(sourceConstruct) && oneToOneMatching.getConstruct2().equals(targetConstruct))
				|| (oneToOneMatching.getConstruct2().equals(sourceConstruct) && oneToOneMatching.getConstruct1().equals(targetConstruct))) {
			if (matchedSourceConstructsTargetConstructsMatchingsMap.containsKey(sourceConstruct)) {
				Map<CanonicalModelConstruct, Set<Matching>> targetConstructMatchingMap = matchedSourceConstructsTargetConstructsMatchingsMap
						.get(sourceConstruct);
				if (targetConstructMatchingMap.containsKey(targetConstruct)) {
					Set<Matching> matchingsBetweenSourceAndTargetConstructs = targetConstructMatchingMap.get(targetConstruct);
					if (!matchingsBetweenSourceAndTargetConstructs.contains(oneToOneMatching))
						matchingsBetweenSourceAndTargetConstructs.add(oneToOneMatching);
				} else {
					Set<Matching> matchingsBetweenSourceAndTargetConstructs = new HashSet<Matching>();
					matchingsBetweenSourceAndTargetConstructs.add(oneToOneMatching);
					targetConstructMatchingMap.put(targetConstruct, matchingsBetweenSourceAndTargetConstructs);
				}
			} else {
				Set<Matching> matchingsBetweenSourceAndTargetConstructs = new HashSet<Matching>();
				matchingsBetweenSourceAndTargetConstructs.add(oneToOneMatching);
				Map<CanonicalModelConstruct, Set<Matching>> targetConstructMatchingMap = new HashMap<CanonicalModelConstruct, Set<Matching>>();
				targetConstructMatchingMap.put(targetConstruct, matchingsBetweenSourceAndTargetConstructs);
				matchedSourceConstructsTargetConstructsMatchingsMap.put(sourceConstruct, targetConstructMatchingMap);
			}
		}
		return matchedSourceConstructsTargetConstructsMatchingsMap;
	}

	protected Set<CanonicalModelConstruct> generateSetOfSuperAbstractsAndSuperLexicalsInSchemas(Set<Schema> schemas) {
		Set<CanonicalModelConstruct> constructsSet = new HashSet<CanonicalModelConstruct>();
		for (Schema schema : schemas)
			constructsSet.addAll(schema.getSuperAbstractsAndSuperLexicals());
		return constructsSet;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getDerivedOneToOneMatchingOrderedDescendingBySumOfMatchingScores(java.util.List)
	 */
	public List<DerivedOneToOneMatching> getDerivedOneToOneMatchingOrderedDescendingBySumOfMatchingScores(
			List<DerivedOneToOneMatching> derivedOneToOneMatchings) {
		return this.quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(derivedOneToOneMatchings, false);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getMatchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical)
	 */
	public Map<SuperAbstract, Map<SuperLexical, DerivedOneToOneMatching>> getMatchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings(
			Map<SuperLexical, DerivedOneToOneMatching> matchedSuperLexicalsWithDerivedMatching) {
		Map<SuperAbstract, Map<SuperLexical, DerivedOneToOneMatching>> matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings = new HashMap<SuperAbstract, Map<SuperLexical, DerivedOneToOneMatching>>();

		for (SuperLexical matchedSuperLexical : matchedSuperLexicalsWithDerivedMatching.keySet()) {
			SuperAbstract firstAncestorSuperAbstract = matchedSuperLexical.getFirstAncestorSuperAbstract();
			Map<SuperLexical, DerivedOneToOneMatching> superLexicalWithMatchingMap = null;
			if (matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.containsKey(firstAncestorSuperAbstract))
				superLexicalWithMatchingMap = matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(firstAncestorSuperAbstract);
			else
				superLexicalWithMatchingMap = new HashMap<SuperLexical, DerivedOneToOneMatching>();
			superLexicalWithMatchingMap.put(matchedSuperLexical, matchedSuperLexicalsWithDerivedMatching.get(matchedSuperLexical));
			matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.put(firstAncestorSuperAbstract, superLexicalWithMatchingMap);
		}
		return matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings;
	}

	public MostSimilarSuperLexicalPerSuperAbstract getMostSimilarSuperLexicalWithLargestSumOfMatchingScoresWithinGroupOfMatches(
			Map<SuperLexical, DerivedOneToOneMatching> derivedOneToOneMatchings) {

		double currentMaxSumOfMatchingScore = 0;
		SuperLexical currentSuperLexicalWithMaxSumOfMatchingScore = null;
		DerivedOneToOneMatching currentDerivedMatchingWithMaxSumOfMatchingScore = null;

		for (SuperLexical matchedSuperLexical : derivedOneToOneMatchings.keySet()) {
			DerivedOneToOneMatching currentDerivedMatching = derivedOneToOneMatchings.get(matchedSuperLexical);
			if (currentDerivedMatching.getSumOfMatchingScores() > currentMaxSumOfMatchingScore) {
				currentMaxSumOfMatchingScore = currentDerivedMatching.getSumOfMatchingScores();
				currentSuperLexicalWithMaxSumOfMatchingScore = matchedSuperLexical;
				currentDerivedMatchingWithMaxSumOfMatchingScore = currentDerivedMatching;
			}
		}

		return new MostSimilarSuperLexicalPerSuperAbstract(currentSuperLexicalWithMaxSumOfMatchingScore.getFirstAncestorSuperAbstract(),
				currentSuperLexicalWithMaxSumOfMatchingScore, currentDerivedMatchingWithMaxSumOfMatchingScore);
	}

	protected List<DerivedOneToOneMatching> quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(
			List<DerivedOneToOneMatching> unorderedListOfDerivedMatchings, boolean orderAscending) {
		logger.debug("unorderedListOfDerivedMatchings: " + unorderedListOfDerivedMatchings);
		if (unorderedListOfDerivedMatchings.size() <= 1)
			return unorderedListOfDerivedMatchings;
		int listIndex = 0;
		DerivedOneToOneMatching pivotMatching = unorderedListOfDerivedMatchings.get(listIndex);
		double pivotMatchingScore = pivotMatching.getSumOfMatchingScores();
		List<DerivedOneToOneMatching> matchingsWithSmallerMatchingScore = new ArrayList<DerivedOneToOneMatching>();
		List<DerivedOneToOneMatching> matchingsWithLargerMatchingScore = new ArrayList<DerivedOneToOneMatching>();
		unorderedListOfDerivedMatchings.remove(pivotMatching);
		for (DerivedOneToOneMatching currentMatching : unorderedListOfDerivedMatchings)
			if (currentMatching.getSumOfMatchingScores() <= pivotMatchingScore)
				matchingsWithSmallerMatchingScore.add(currentMatching);
			else
				matchingsWithLargerMatchingScore.add(currentMatching);
		return this.addToOrderedListOfMatching(matchingsWithSmallerMatchingScore, pivotMatching, matchingsWithLargerMatchingScore, orderAscending);
	}

	protected List<DerivedOneToOneMatching> addToOrderedListOfMatching(List<DerivedOneToOneMatching> matchingsWithSmallerMatchingScore,
			DerivedOneToOneMatching pivotMatching, List<DerivedOneToOneMatching> matchingsWithLargerMatchingScore, boolean orderAscending) {
		List<DerivedOneToOneMatching> orderedListOfMatching = new ArrayList<DerivedOneToOneMatching>();
		if (orderAscending)
			orderedListOfMatching
					.addAll(quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(matchingsWithSmallerMatchingScore, orderAscending));
		else
			orderedListOfMatching
					.addAll(quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(matchingsWithLargerMatchingScore, orderAscending));
		orderedListOfMatching.add(pivotMatching);
		if (orderAscending)
			orderedListOfMatching
					.addAll(quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(matchingsWithLargerMatchingScore, orderAscending));
		else
			orderedListOfMatching
					.addAll(quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(matchingsWithSmallerMatchingScore, orderAscending));
		return orderedListOfMatching;
	}

	//the following methods all assume that a construct is either in the source schemas or in the target schemas, but not in both
	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getSuperAbstractsMatchedWithSuperAbstract(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract)
	 */
	//TODO refactor, might have to add isSource
	public Set<SuperAbstract> getSuperAbstractsMatchedWithSuperAbstract(SuperAbstract superAbstract) {
		if (sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap != null
				&& sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.containsKey(superAbstract)
				&& !targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.containsKey(superAbstract))
			return sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.get(superAbstract).keySet();
		if (targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap != null
				&& targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.containsKey(superAbstract)
				&& !sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.containsKey(superAbstract))
			return targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.get(superAbstract).keySet();
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getSuperLexicalsMatchedWithSuperLexical(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical)
	 */
	//TODO refactor, might have to add isSource
	public Set<SuperLexical> getSuperLexicalsMatchedWithSuperLexical(SuperLexical superLexical) {
		if (sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap != null
				&& sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.containsKey(superLexical)
				&& !targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.containsKey(superLexical))
			return sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.get(superLexical).keySet();
		if (targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap != null
				&& targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.containsKey(superLexical)
				&& !sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.containsKey(superLexical))
			return targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.get(superLexical).keySet();
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getDerivedOneToOneMatchingBetweenSourceAndTargetSuperAbstract(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract, uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract)
	 */
	//TODO refactor, might have to add isSource
	public DerivedOneToOneMatching getDerivedOneToOneMatchingBetweenSourceAndTargetSuperAbstract(SuperAbstract sourceSuperAbstract,
			SuperAbstract targetSuperAbstract) {
		if (sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap != null
				&& sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.containsKey(sourceSuperAbstract)
				&& sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.get(sourceSuperAbstract).containsKey(targetSuperAbstract))
			return sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.get(sourceSuperAbstract).get(targetSuperAbstract);
		if (targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap != null
				&& targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.containsKey(sourceSuperAbstract)
				&& targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.get(sourceSuperAbstract).containsKey(targetSuperAbstract))
			return targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.get(sourceSuperAbstract).get(targetSuperAbstract);
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getDerivedOneToOneMatchingBetweenSourceAndTargetSuperLexical(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical, uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical)
	 */
	//TODO refactor, might have to add isSource
	public DerivedOneToOneMatching getDerivedOneToOneMatchingBetweenSourceAndTargetSuperLexical(SuperLexical sourceSuperLexical,
			SuperLexical targetSuperLexical) {
		if (sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap != null
				&& sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.containsKey(sourceSuperLexical)
				&& sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.get(sourceSuperLexical).containsKey(targetSuperLexical))
			return sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.get(sourceSuperLexical).get(targetSuperLexical);
		if (targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap != null
				&& targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.containsKey(sourceSuperLexical)
				&& targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.get(sourceSuperLexical).containsKey(targetSuperLexical))
			return targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.get(sourceSuperLexical).get(targetSuperLexical);
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getAllSuperLexicalsMatchedWithSuperLexicalWithDerivedMatching(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical)
	 */
	//TODO refactor, might have to add isSource
	public Map<SuperLexical, DerivedOneToOneMatching> getAllSuperLexicalsMatchedWithSuperLexicalWithDerivedMatching(SuperLexical superLexical) {
		if (sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap != null
				&& sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.containsKey(superLexical))
			return sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.get(superLexical);
		if (targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap != null
				&& targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.containsKey(superLexical))
			return targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.get(superLexical);
		return null;
	}

	protected DerivedOneToOneMatching createDerivedMatching(CanonicalModelConstruct sourceConstruct, CanonicalModelConstruct targetConstruct,
			Set<Matching> matchingsBetweenSourceAndTargetConstruct) {
		DerivedOneToOneMatching derivedMatching = new DerivedOneToOneMatching(sourceConstruct, targetConstruct);
		for (Matching matching : matchingsBetweenSourceAndTargetConstruct)
			derivedMatching.addMatchingScore(matching.getScore());
		return derivedMatching;
	}

	/*
	protected double determineCurrentMaxSumOfMatchingScore(DerivedOneToOneMatching derivedMatching) {
		double max = maxSumOfMatchingScore;
		if (derivedMatching.getSumOfMatchingScores() > max)
			max = derivedMatching.getSumOfMatchingScores();
		return max;
	}
	*/

	/*
	public MostSimilarSuperLexicalPerSuperAbstract getDerivedMatchingWithLargestSumOfMatchingScoresWithinGroupOfMatchesPerSuperAbstractOrWithSameNameAsSuperLexical(
			SuperLexical currentSuperLexical, Map<SuperLexical, DerivedOneToOneMatching> derivedOneToOneMatchings) {

		Map<SuperLexical, DerivedOneToOneMatching> superLexicalWithSameNameAndDerivedMatching = this
				.getSuperLexicalWithSameNameAsCurrentSuperLexical(currentSuperLexical, derivedOneToOneMatchings);

		double currentMaxSumOfMatchingScore = 0;
		SuperLexical currentSuperLexicalWithMaxSumOfMatchingScore = null;
		DerivedOneToOneMatching currentDerivedMatchingWithMaxSumOfMatchingScore = null;

		for (SuperLexical matchedSuperLexical : derivedOneToOneMatchings.keySet()) {
			DerivedOneToOneMatching currentDerivedMatching = derivedOneToOneMatchings.get(matchedSuperLexical);
			if (currentDerivedMatching.getSumOfMatchingScores() > currentMaxSumOfMatchingScore) {
				currentMaxSumOfMatchingScore = currentDerivedMatching.getSumOfMatchingScores();
				currentSuperLexicalWithMaxSumOfMatchingScore = matchedSuperLexical;
				currentDerivedMatchingWithMaxSumOfMatchingScore = currentDerivedMatching;
			}
		}

		if (superLexicalWithSameNameAndDerivedMatching.size() == 1) {
			SuperLexical superLexicalWithSameName = superLexicalWithSameNameAndDerivedMatching.keySet().iterator().next();
			DerivedOneToOneMatching derivedMatchingToSuperLexicalWithSameName = superLexicalWithSameNameAndDerivedMatching
					.get(superLexicalWithSameName);
			if (derivedMatchingToSuperLexicalWithSameName.getSumOfMatchingScores() > currentMaxSumOfMatchingScore * 0.8) { //TODOthis is a bit of a hack
				currentMaxSumOfMatchingScore = derivedMatchingToSuperLexicalWithSameName.getSumOfMatchingScores();
				currentSuperLexicalWithMaxSumOfMatchingScore = superLexicalWithSameName;
				currentDerivedMatchingWithMaxSumOfMatchingScore = derivedMatchingToSuperLexicalWithSameName;
			}
		}

		return new MostSimilarSuperLexicalPerSuperAbstract(currentSuperLexicalWithMaxSumOfMatchingScore.getFirstAncestorSuperAbstract(),
				currentSuperLexicalWithMaxSumOfMatchingScore, currentDerivedMatchingWithMaxSumOfMatchingScore);
	}
	*/

	/*
	protected Map<SuperLexical, DerivedOneToOneMatching> getSuperLexicalWithSameNameAsCurrentSuperLexical(SuperLexical currentSuperLexical,
			Map<SuperLexical, DerivedOneToOneMatching> derivedOneToOneMatchings) {
		Map<SuperLexical, DerivedOneToOneMatching> superLexicalWithSameName = new HashMap<SuperLexical, DerivedOneToOneMatching>();
		for (SuperLexical matchedSuperLexical : derivedOneToOneMatchings.keySet()) {
			if (!currentSuperLexical.equals(matchedSuperLexical) && currentSuperLexical.getName().equalsIgnoreCase(matchedSuperLexical.getName())) {
				if (superLexicalWithSameName.size() > 0)
					logger.error("multiple superLexicals with same name in group ... TODO");
				else
					superLexicalWithSameName.put(matchedSuperLexical, derivedOneToOneMatchings.get(matchedSuperLexical));
			}
		}
		return superLexicalWithSameName;
	}
	*/

	protected void addToTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap(CanonicalModelConstruct sourceConstruct,
			CanonicalModelConstruct targetConstruct, DerivedOneToOneMatching derivedOneToOneMatching) {
		Map<SuperAbstract, DerivedOneToOneMatching> sourceSuperAbstractDerivedMatchingMap = null;
		if (targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.containsKey(targetConstruct))
			sourceSuperAbstractDerivedMatchingMap = targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.get(targetConstruct);
		else
			sourceSuperAbstractDerivedMatchingMap = new HashMap<SuperAbstract, DerivedOneToOneMatching>();
		if (!sourceSuperAbstractDerivedMatchingMap.containsKey(sourceConstruct))
			sourceSuperAbstractDerivedMatchingMap.put((SuperAbstract) sourceConstruct, derivedOneToOneMatching);
		targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap.put((SuperAbstract) targetConstruct, sourceSuperAbstractDerivedMatchingMap);
	}

	protected void addToSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap(CanonicalModelConstruct sourceConstruct,
			CanonicalModelConstruct targetConstruct, DerivedOneToOneMatching derivedOneToOneMatching) {
		Map<SuperAbstract, DerivedOneToOneMatching> targetSuperAbstractDerivedMatchingMap = null;
		if (sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.containsKey(sourceConstruct))
			targetSuperAbstractDerivedMatchingMap = sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.get(sourceConstruct);
		else
			targetSuperAbstractDerivedMatchingMap = new HashMap<SuperAbstract, DerivedOneToOneMatching>();
		if (!targetSuperAbstractDerivedMatchingMap.containsKey(targetConstruct))
			targetSuperAbstractDerivedMatchingMap.put((SuperAbstract) targetConstruct, derivedOneToOneMatching);
		sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.put((SuperAbstract) sourceConstruct, targetSuperAbstractDerivedMatchingMap);
	}

	protected void addToSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap(CanonicalModelConstruct sourceConstruct,
			CanonicalModelConstruct targetConstruct, DerivedOneToOneMatching derivedOneToOneMatching) {
		Map<SuperLexical, DerivedOneToOneMatching> targetSuperLexicalDerivedOneToOneMatchingMap = null;
		if (sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.containsKey(sourceConstruct))
			targetSuperLexicalDerivedOneToOneMatchingMap = sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.get(sourceConstruct);
		else
			targetSuperLexicalDerivedOneToOneMatchingMap = new HashMap<SuperLexical, DerivedOneToOneMatching>();
		if (!targetSuperLexicalDerivedOneToOneMatchingMap.containsKey(targetConstruct))
			targetSuperLexicalDerivedOneToOneMatchingMap.put((SuperLexical) targetConstruct, derivedOneToOneMatching);
		sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.put((SuperLexical) sourceConstruct,
				targetSuperLexicalDerivedOneToOneMatchingMap);
	}

	protected void addToTargetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap(CanonicalModelConstruct sourceConstruct,
			CanonicalModelConstruct targetConstruct, DerivedOneToOneMatching derivedMatching) {
		Map<SuperLexical, DerivedOneToOneMatching> sourceSuperLexicalDerivedOneToOneMatchingMap = null;
		if (targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.containsKey(targetConstruct))
			sourceSuperLexicalDerivedOneToOneMatchingMap = targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.get(targetConstruct);
		else
			sourceSuperLexicalDerivedOneToOneMatchingMap = new HashMap<SuperLexical, DerivedOneToOneMatching>();
		if (!sourceSuperLexicalDerivedOneToOneMatchingMap.containsKey(sourceConstruct))
			sourceSuperLexicalDerivedOneToOneMatchingMap.put((SuperLexical) sourceConstruct, derivedMatching);
		targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap.put((SuperLexical) targetConstruct,
				sourceSuperLexicalDerivedOneToOneMatchingMap);
	}

	private void init() {
		//maxSumOfMatchingScore = 0;
		sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap = new HashMap<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>>();
		targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap = new HashMap<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>>();
		sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap = new HashMap<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>>();
		targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap = new HashMap<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>>();
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getDerivedOneToOneMatchings()
	 */
	public List<DerivedOneToOneMatching> getDerivedOneToOneMatchings() {
		return derivedOneToOneMatchings;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#setDerivedOneToOneMatchings(java.util.List)
	 */
	public void setDerivedOneToOneMatchings(List<DerivedOneToOneMatching> derivedOneToOneMatchings) {
		this.derivedOneToOneMatchings = derivedOneToOneMatchings;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap()
	 */
	public Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> getSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap() {
		return sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#setSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap(java.util.Map)
	 */
	public void setSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap(
			Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap) {
		this.sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap = sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap()
	 */
	public Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> getTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap() {
		return targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#setTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap(java.util.Map)
	 */
	public void setTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap(
			Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap) {
		this.targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap = targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap()
	 */
	public Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> getSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap() {
		return sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#setSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap(java.util.Map)
	 */
	public void setSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap(
			Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap) {
		this.sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap = sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getTargetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap()
	 */
	public Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> getTargetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap() {
		return targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#setTargetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap(java.util.Map)
	 */
	public void setTargetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap(
			Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap) {
		this.targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap = targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#getMaxSumOfMatchingScore()
	 */
	public double getMaxSumOfMatchingScore() {
		return maxSumOfMatchingScore;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorService#setMaxSumOfMatchingScore(double)
	 */
	public void setMaxSumOfMatchingScore(double maxSumOfMatchingScore) {
		this.maxSumOfMatchingScore = maxSumOfMatchingScore;
	}

	/**
	 * @return the sourceSchemas
	 */
	public LinkedHashSet<Schema> getSourceSchemas() {
		return sourceSchemas;
	}

	/**
	 * @return the targetSchemas
	 */
	public LinkedHashSet<Schema> getTargetSchemas() {
		return targetSchemas;
	}

	/**
	 * @return the matchings
	 */
	public List<Matching> getMatchings() {
		return matchings;
	}

}
