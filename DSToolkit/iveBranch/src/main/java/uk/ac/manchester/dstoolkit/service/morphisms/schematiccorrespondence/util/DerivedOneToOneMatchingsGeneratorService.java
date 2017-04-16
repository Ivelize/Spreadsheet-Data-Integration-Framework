/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.MostSimilarSuperLexicalPerSuperAbstract;

/**
 * @author chedeler
 *
 */
public interface DerivedOneToOneMatchingsGeneratorService {

	public abstract List<DerivedOneToOneMatching> generateDerivedMatchings(LinkedHashSet<Schema> sourceSchemas, LinkedHashSet<Schema> targetSchemas,
			List<Matching> matchings);

	public abstract List<DerivedOneToOneMatching> getDerivedOneToOneMatchingOrderedDescendingBySumOfMatchingScores(
			List<DerivedOneToOneMatching> derivedOneToOneMatchings);

	public abstract Map<SuperAbstract, Map<SuperLexical, DerivedOneToOneMatching>> getMatchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings(
			Map<SuperLexical, DerivedOneToOneMatching> matchedSuperLexicalsWithDerivedMatching);

	public abstract MostSimilarSuperLexicalPerSuperAbstract getMostSimilarSuperLexicalWithLargestSumOfMatchingScoresWithinGroupOfMatches(
			Map<SuperLexical, DerivedOneToOneMatching> derivedOneToOneMatchings);

	public abstract Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings(
			CanonicalModelConstruct construct, Set<CanonicalModelConstruct[]> setOfArraysOfConstructs, boolean isSourceToTarget);

	public abstract Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> groupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings(
			List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> constructMatchedWithConstructArraysAndDerivedMatchingsList);

	public abstract Map<CanonicalModelConstruct[], Double> calculateAvgOfMatchingScoresForMatchedConstructsArrays(
			CanonicalModelConstruct[] constructsArray,
			Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> mapOfMatchedConstructsWithDerivedMatchings);

	public abstract Map<CanonicalModelConstruct[], Double> identifyConstructsArrayWithMaxAvgMatchingScores(
			Map<CanonicalModelConstruct[], Double> matchedConstructsWithAvgOfMatchingScore);

	//public Map<CanonicalModelConstruct[], Double> getConstructsArrayWithMaxAvgMatchingScores(CanonicalModelConstruct[] constructsArray,
	//		List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> constructMatchedWithConstructArraysAndDerivedMatchings);

	//the following methods all assume that a construct is either in the source schemas or in the target schemas, but not in both
	//TODO might have to add additional methods with boolean isSourceToTarget
	//TODO might have to rename the methods here to make it more obvious that it's checking in both directions
	public abstract Set<SuperAbstract> getSuperAbstractsMatchedWithSuperAbstract(SuperAbstract superAbstract);

	public abstract Set<SuperLexical> getSuperLexicalsMatchedWithSuperLexical(SuperLexical superLexical);

	public abstract DerivedOneToOneMatching getDerivedOneToOneMatchingBetweenSourceAndTargetSuperAbstract(SuperAbstract sourceSuperAbstract,
			SuperAbstract targetSuperAbstract);

	public abstract DerivedOneToOneMatching getDerivedOneToOneMatchingBetweenSourceAndTargetSuperLexical(SuperLexical sourceSuperLexical,
			SuperLexical targetSuperLexical);

	public abstract Map<SuperLexical, DerivedOneToOneMatching> getAllSuperLexicalsMatchedWithSuperLexicalWithDerivedMatching(SuperLexical superLexical);

	/**
	 * @return the derivedOneToOneMatchings
	 */
	public abstract List<DerivedOneToOneMatching> getDerivedOneToOneMatchings();

	/**
	 * @param derivedOneToOneMatchings the derivedOneToOneMatchings to set
	 */
	public abstract void setDerivedOneToOneMatchings(List<DerivedOneToOneMatching> derivedOneToOneMatchings);

	/**
	 * @return the sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap
	 */
	public abstract Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> getSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap();

	/**
	 * @param sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap the sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap to set
	 */
	public abstract void setSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap(
			Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap);

	/**
	 * @return the targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap
	 */
	public abstract Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> getTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap();

	/**
	 * @param targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap the targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap to set
	 */
	public abstract void setTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap(
			Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> targetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap);

	/**
	 * @return the sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap
	 */
	public abstract Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> getSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap();

	/**
	 * @param sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap the sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap to set
	 */
	public abstract void setSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap(
			Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap);

	/**
	 * @return the targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap
	 */
	public abstract Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> getTargetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap();

	/**
	 * @param targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap the targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap to set
	 */
	public abstract void setTargetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap(
			Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> targetSuperLexicalSourceSuperLexicalsDerivedOneToOneMatchingMap);

	/**
	 * @return the maxSumOfMatchingScore
	 */
	public abstract double getMaxSumOfMatchingScore();

	/**
	 * @param maxSumOfMatchingScore the maxSumOfMatchingScore to set
	 */
	public abstract void setMaxSumOfMatchingScore(double maxSumOfMatchingScore);

}