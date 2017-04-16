/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierService;

/**
 * @author chedeler
 *
 */
@Configurable(autowire = Autowire.BY_NAME)
public class ELRChromosome {

	//TODO sideeffects - also generates and has sets of superAbstractsInSourceSchemas, superAbstractsInTargetSchemas, matchedSourceSuperAbstracts and matchedTargetSuperAbstracts as well as map matchedSourceSuperAbstractTargetSuperAbstractsMap
	//TODO has handle to derivedOneToOneMatchingsGeneratorService and equivalentSuperLexicalsIdentifierService and provides methods that utilise the methods in them

	private static Logger logger = Logger.getLogger(ELRChromosome.class);

	@Autowired
	@Qualifier("equivalentSuperLexicalsIdentifierService")
	private EquivalentSuperLexicalsIdentifierService equivalentSuperLexicalsIdentifierService;

	@Autowired
	@Qualifier("derivedOneToOneMatchingsGeneratorService")
	private DerivedOneToOneMatchingsGeneratorService derivedOneToOneMatchingsGeneratorService;

	private final LinkedHashSet<Schema> sourceSchemas;
	private final LinkedHashSet<Schema> targetSchemas;
	private final List<Matching> matchings;

	private List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts;

	private Set<SuperAbstract> superAbstractsInSourceSchemas;
	private Set<SuperAbstract> superAbstractsInTargetSchemas;
	private Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas;
	private Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas;

	private Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap;

	private List<DerivedOneToOneMatching> derivedOneToOneMatchings;
	private Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsInSource;
	private Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsInTarget;

	private void init() {
		chromosomeOfPairsOfSuperAbstracts = null;
		superAbstractsInSourceSchemas = null;
		superAbstractsInTargetSchemas = null;
		matchedSuperAbstractsInSourceSchemas = null;
		matchedSuperAbstractsInTargetSchemas = null;
		matchedSourceSuperAbstractTargetSuperAbstractsMap = null;
		derivedOneToOneMatchings = null;
		equivalentSuperLexicalsInSource = null;
		equivalentSuperLexicalsInTarget = null;
	}

	public ELRChromosome(LinkedHashSet<Schema> sourceSchemas, LinkedHashSet<Schema> targetSchemas, List<Matching> matchings) {
		logger.debug("in ELRChromosome");
		logger.debug("this: " + this);
		logger.debug("equivalentSuperLexicalsIdentifierService: " + equivalentSuperLexicalsIdentifierService);
		logger.debug("derivedOneToOneMatchingsGeneratorService: " + derivedOneToOneMatchingsGeneratorService);
		logger.debug("sourceSchemas: " + sourceSchemas);
		logger.debug("targetSchemas: " + targetSchemas);
		logger.debug("matchings: " + matchings);
		if (sourceSchemas != null)
			logger.debug("sourceSchemas.size(): " + sourceSchemas.size());
		if (targetSchemas != null)
			logger.debug("targetSchemas.size(): " + targetSchemas.size());
		if (matchings != null)
			logger.debug("matching.size()s: " + matchings.size());

		init();

		this.sourceSchemas = sourceSchemas;
		this.targetSchemas = targetSchemas;
		this.matchings = matchings;

		if (sourceSchemas != null && targetSchemas != null && matchings != null) {
			this.superAbstractsInSourceSchemas = this.generateSetOfSuperAbstractsInSchemas(sourceSchemas);
			this.superAbstractsInTargetSchemas = this.generateSetOfSuperAbstractsInSchemas(targetSchemas);

			this.matchedSourceSuperAbstractTargetSuperAbstractsMap = this.generateMapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts(
					superAbstractsInSourceSchemas, superAbstractsInTargetSchemas, matchings);

			this.matchedSuperAbstractsInSourceSchemas = this.matchedSourceSuperAbstractTargetSuperAbstractsMap.keySet();
			this.matchedSuperAbstractsInTargetSchemas = this.identifyMatchedTargetSuperAbstracts(matchedSourceSuperAbstractTargetSuperAbstractsMap);

			this.chromosomeOfPairsOfSuperAbstracts = this
					.generateChromosomeOfPairsOfSuperAbstracts(matchedSourceSuperAbstractTargetSuperAbstractsMap);
		}
	}

	public void generateDerivedOneToOneMatchings() {
		logger.debug("in generateDerivedOneToOneMatchings");
		logger.debug("this: " + this);
		logger.debug("derivedOneToOneMatchingsGeneratorService: " + derivedOneToOneMatchingsGeneratorService);
		// TODO the following are additional bits required later for feasibility checking and calculation of distance between vectors
		//if (derivedOneToOneMatchingsGeneratorService == null)
		//	derivedOneToOneMatchingsGeneratorService = new DerivedOneToOneMatchingsGeneratorServiceImpl();
		//if (equivalentSuperLexicalsIdentifierService == null)
		//	equivalentSuperLexicalsIdentifierService = new EquivalentSuperLexicalsIdentifierServiceImpl();
		this.derivedOneToOneMatchings = derivedOneToOneMatchingsGeneratorService.generateDerivedMatchings(sourceSchemas, targetSchemas, matchings);
	}

	public void identifyEquivalentSuperLexicals() {
		logger.debug("in identifyEquivalentSuperLexicals");
		logger.debug("this: " + this);
		logger.debug("equivalentSuperLexicalsIdentifierService: " + equivalentSuperLexicalsIdentifierService);
		logger.debug("derivedOneToOneMatchingsGeneratorService: " + derivedOneToOneMatchingsGeneratorService);
		//TODO infer from matchers that produced the matchings
		double maxMatchingScore = 1.0;
		// TODO the following are additional bits required later for feasibility checking and calculation of distance between vectors
		this.equivalentSuperLexicalsInSource = equivalentSuperLexicalsIdentifierService.identifyEquivalentSuperLexicals(
				derivedOneToOneMatchingsGeneratorService, maxMatchingScore, true);
		this.equivalentSuperLexicalsInTarget = equivalentSuperLexicalsIdentifierService.identifyEquivalentSuperLexicals(
				derivedOneToOneMatchingsGeneratorService, maxMatchingScore, false);
	}

	public Set<SuperLexical> getEquivalentSuperLexicalsForSuperLexical(SuperLexical superLexical, boolean isSource) {
		return equivalentSuperLexicalsIdentifierService.getEquivalentSuperLexicalsForSuperLexical(superLexical, isSource);
	}

	public Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings(
			CanonicalModelConstruct construct, Set<CanonicalModelConstruct[]> setOfArraysOfConstructs, boolean isSourceToTarget) {
		return this.derivedOneToOneMatchingsGeneratorService.getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings(construct,
				setOfArraysOfConstructs, isSourceToTarget);
	}

	public Map<CanonicalModelConstruct[], Double> identifyConstructsArrayWithMaxAvgMatchingScores(
			Map<CanonicalModelConstruct[], Double> matchedConstructsWithAvgOfMatchingScore) {
		return this.derivedOneToOneMatchingsGeneratorService.identifyConstructsArrayWithMaxAvgMatchingScores(matchedConstructsWithAvgOfMatchingScore);
	}

	public Map<CanonicalModelConstruct[], Double> calculateAvgOfMatchingScoresForMatchedConstructsArrays(CanonicalModelConstruct[] constructsArray,
			Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> mapOfMatchedConstructsWithDerivedMatchings) {
		return this.derivedOneToOneMatchingsGeneratorService.calculateAvgOfMatchingScoresForMatchedConstructsArrays(constructsArray,
				mapOfMatchedConstructsWithDerivedMatchings);
	}

	public Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> groupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings(
			List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> constructMatchedWithConstructArraysAndDerivedMatchingsList) {
		return this.derivedOneToOneMatchingsGeneratorService
				.groupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings(constructMatchedWithConstructArraysAndDerivedMatchingsList);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.OneToOneMatchingProcessorService#isMatched(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract)
	 */
	public boolean isMatched(SuperAbstract superAbstract) {
		if (matchedSuperAbstractsInSourceSchemas.contains(superAbstract) || matchedSuperAbstractsInTargetSchemas.contains(superAbstract))
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.OneToOneMatchingProcessorService#isMatchedWithEachOther(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract, uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract)
	 */
	public boolean areMatchedWithEachOther(SuperAbstract superAbstract1, SuperAbstract superAbstract2) {
		if (matchedSuperAbstractsInSourceSchemas.contains(superAbstract1) && matchedSuperAbstractsInTargetSchemas.contains(superAbstract2)) {
			return matchedSourceSuperAbstractTargetSuperAbstractsMap.get(superAbstract1).contains(superAbstract2);
		} else if (matchedSuperAbstractsInSourceSchemas.contains(superAbstract2) && matchedSuperAbstractsInTargetSchemas.contains(superAbstract1)) {
			return matchedSourceSuperAbstractTargetSuperAbstractsMap.get(superAbstract2).contains(superAbstract1);
		}
		return false;
	}

	public Set<SuperAbstract> getTargetSuperAbstractsMatchedWithSourceSuperAbstract(SuperAbstract sourceSuperAbstract) {
		Set<SuperAbstract> matchedSuperAbstracts = new LinkedHashSet<SuperAbstract>();
		if (matchedSuperAbstractsInSourceSchemas.contains(sourceSuperAbstract)) {
			matchedSuperAbstracts.addAll(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sourceSuperAbstract));
		}
		return matchedSuperAbstracts;
	}

	protected List<SuperAbstract[]> generateChromosomeOfPairsOfSuperAbstracts(
			Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap) {
		logger.debug("in generateChromosomeOfPairsOfSuperAbstracts");
		logger.debug("this: " + this);
		logger.debug("equivalentSuperLexicalsIdentifierService: " + equivalentSuperLexicalsIdentifierService);
		logger.debug("derivedOneToOneMatchingsGeneratorService: " + derivedOneToOneMatchingsGeneratorService);
		List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts = new ArrayList<SuperAbstract[]>();
		for (SuperAbstract sourceSuperAbstract : matchedSourceSuperAbstractTargetSuperAbstractsMap.keySet()) {
			for (SuperAbstract targetSuperAbstract : matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sourceSuperAbstract)) {
				logger.debug("sourceSuperAbstract: " + sourceSuperAbstract);
				logger.debug("targetSuperAbstract: " + targetSuperAbstract);
				SuperAbstract[] matchedPairOfSuperAbstracts = { sourceSuperAbstract, targetSuperAbstract };
				logger.debug("matchedPairOfSuperAbstracts[0]: " + matchedPairOfSuperAbstracts[0]);
				logger.debug("matchedPairOfSuperAbstracts[1]: " + matchedPairOfSuperAbstracts[1]);
				chromosomeOfPairsOfSuperAbstracts.add(matchedPairOfSuperAbstracts);
			}
		}
		return chromosomeOfPairsOfSuperAbstracts;
	}

	protected Set<SuperAbstract> identifyMatchedTargetSuperAbstracts(
			Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap) {
		logger.debug("in identifyMatchedTargetSuperAbstracts");
		logger.debug("matchedSourceSuperAbstractTargetSuperAbstractsMap: " + matchedSourceSuperAbstractTargetSuperAbstractsMap);
		logger.debug("this: " + this);
		logger.debug("equivalentSuperLexicalsIdentifierService: " + equivalentSuperLexicalsIdentifierService);
		logger.debug("derivedOneToOneMatchingsGeneratorService: " + derivedOneToOneMatchingsGeneratorService);
		Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas = new HashSet<SuperAbstract>();
		for (SuperAbstract sourceSuperAbstract : matchedSourceSuperAbstractTargetSuperAbstractsMap.keySet()) {
			for (SuperAbstract targetSuperAbstract : matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sourceSuperAbstract)) {
				logger.debug("sourceSuperAbstract: " + sourceSuperAbstract);
				logger.debug("targetSuperAbstract: " + targetSuperAbstract);
				if (!matchedSuperAbstractsInTargetSchemas.contains(targetSuperAbstract))
					matchedSuperAbstractsInTargetSchemas.add(targetSuperAbstract);
			}
		}
		return matchedSuperAbstractsInTargetSchemas;
	}

	protected Map<SuperAbstract, Set<SuperAbstract>> generateMapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts(
			Set<SuperAbstract> superAbstractsInSourceSchemas, Set<SuperAbstract> superAbstractsInTargetSchemas, List<Matching> matchings) {
		Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap = new HashMap<SuperAbstract, Set<SuperAbstract>>();
		for (Matching matching : matchings)
			if (matching instanceof OneToOneMatching) {
				SuperAbstract[] matchedSourceAndTargetSuperAbstract = this.getPairOfMatchedSourceAndTargetSuperAbstractFromOneToOneMatching(
						superAbstractsInSourceSchemas, superAbstractsInTargetSchemas, (OneToOneMatching) matching);
				if (matchedSourceAndTargetSuperAbstract != null) {
					matchedSourceSuperAbstractTargetSuperAbstractsMap = this
							.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(
									matchedSourceSuperAbstractTargetSuperAbstractsMap, matchedSourceAndTargetSuperAbstract[0],
									matchedSourceAndTargetSuperAbstract[1]);
				}
			}
		return matchedSourceSuperAbstractTargetSuperAbstractsMap;
	}

	protected SuperAbstract[] getPairOfMatchedSourceAndTargetSuperAbstractFromOneToOneMatching(Set<SuperAbstract> superAbstractsInSourceSchemas,
			Set<SuperAbstract> superAbstractsInTargetSchemas, OneToOneMatching oneToOneMatching) {
		SuperAbstract matchedSourceSuperAbstract = null;
		SuperAbstract matchedTargetSuperAbstract = null;
		SuperAbstract superAbstract1OfOneToOneMatching = this.identifyFirstAncestorSuperAbstract(oneToOneMatching.getConstruct1());
		SuperAbstract superAbstract2OfOneToOneMatching = this.identifyFirstAncestorSuperAbstract(oneToOneMatching.getConstruct2());
		if (superAbstractsInSourceSchemas.contains(superAbstract1OfOneToOneMatching)
				&& superAbstractsInTargetSchemas.contains(superAbstract2OfOneToOneMatching)) {
			matchedSourceSuperAbstract = superAbstract1OfOneToOneMatching;
			matchedTargetSuperAbstract = superAbstract2OfOneToOneMatching;
		} else if (superAbstractsInSourceSchemas.contains(superAbstract2OfOneToOneMatching)
				&& superAbstractsInTargetSchemas.contains(superAbstract1OfOneToOneMatching)) {
			matchedSourceSuperAbstract = superAbstract2OfOneToOneMatching;
			matchedTargetSuperAbstract = superAbstract1OfOneToOneMatching;
		}
		if (matchedSourceSuperAbstract != null & matchedTargetSuperAbstract != null) {
			SuperAbstract[] matchedSourceAndTargetSuperAbstract = { matchedSourceSuperAbstract, matchedTargetSuperAbstract };
			return matchedSourceAndTargetSuperAbstract;
		}
		return null;
	}

	protected Set<SuperAbstract> generateSetOfSuperAbstractsInSchemas(Set<Schema> schemas) {
		Set<SuperAbstract> superAbstractsSet = new HashSet<SuperAbstract>();
		for (Schema schema : schemas)
			superAbstractsSet.addAll(schema.getSuperAbstracts());
		return superAbstractsSet;
	}

	protected Map<SuperAbstract, Set<SuperAbstract>> placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(
			Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap, SuperAbstract sourceSuperAbstract,
			SuperAbstract targetSuperAbstract) {
		if (matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sourceSuperAbstract)) {
			Set<SuperAbstract> targetSuperAbstractsForSourceSuperAbstract = matchedSourceSuperAbstractTargetSuperAbstractsMap
					.get(sourceSuperAbstract);
			if (!targetSuperAbstractsForSourceSuperAbstract.contains(targetSuperAbstract))
				targetSuperAbstractsForSourceSuperAbstract.add(targetSuperAbstract);
		} else {
			LinkedHashSet<SuperAbstract> targetSuperAbstractsForSourceSuperAbstract = new LinkedHashSet<SuperAbstract>();
			targetSuperAbstractsForSourceSuperAbstract.add(targetSuperAbstract);
			matchedSourceSuperAbstractTargetSuperAbstractsMap.put(sourceSuperAbstract, targetSuperAbstractsForSourceSuperAbstract);
		}
		return matchedSourceSuperAbstractTargetSuperAbstractsMap;
	}

	//TODO code duplication - refactor
	protected SuperAbstract identifyFirstAncestorSuperAbstract(CanonicalModelConstruct construct) {
		if (construct instanceof SuperAbstract)
			return (SuperAbstract) construct;
		else if (construct instanceof SuperLexical)
			return ((SuperLexical) construct).getFirstAncestorSuperAbstract();
		return null;
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

	/**
	 * @return the chromosomeOfPairsOfSuperAbstracts
	 */
	public List<SuperAbstract[]> getChromosomeOfPairsOfSuperAbstracts() {
		return chromosomeOfPairsOfSuperAbstracts;
	}

	/**
	 * @return the matchedSuperAbstractsInSourceSchemas
	 */
	public Set<SuperAbstract> getMatchedSuperAbstractsInSourceSchemas() {
		return matchedSuperAbstractsInSourceSchemas;
	}

	/**
	 * @return the matchedSuperAbstractsInTargetSchemas
	 */
	public Set<SuperAbstract> getMatchedSuperAbstractsInTargetSchemas() {
		return matchedSuperAbstractsInTargetSchemas;
	}

	/**
	 * @return the superAbstractsInSourceSchemas
	 */
	public Set<SuperAbstract> getSuperAbstractsInSourceSchemas() {
		return superAbstractsInSourceSchemas;
	}

	/**
	 * @return the superAbstractsInTargetSchemas
	 */
	public Set<SuperAbstract> getSuperAbstractsInTargetSchemas() {
		return superAbstractsInTargetSchemas;
	}

	/**
	 * @return the matchedSourceSuperAbstractTargetSuperAbstractsMap
	 */
	public Map<SuperAbstract, Set<SuperAbstract>> getMatchedSourceSuperAbstractTargetSuperAbstractsMap() {
		return matchedSourceSuperAbstractTargetSuperAbstractsMap;
	}

	/**
	 * @param equivalentSuperLexicalsIdentifierService the equivalentSuperLexicalsIdentifierService to set
	 */
	protected void setEquivalentSuperLexicalsIdentifierService(EquivalentSuperLexicalsIdentifierService equivalentSuperLexicalsIdentifierService) {
		this.equivalentSuperLexicalsIdentifierService = equivalentSuperLexicalsIdentifierService;
	}

	/**
	 * @param derivedOneToOneMatchingsGeneratorService the derivedOneToOneMatchingsGeneratorService to set
	 */
	protected void setDerivedOneToOneMatchingsGeneratorService(DerivedOneToOneMatchingsGeneratorService derivedOneToOneMatchingsGeneratorService) {
		this.derivedOneToOneMatchingsGeneratorService = derivedOneToOneMatchingsGeneratorService;
	}

	/**
	 * @param superAbstractsInSourceSchemas the superAbstractsInSourceSchemas to set
	 */
	protected void setSuperAbstractsInSourceSchemas(Set<SuperAbstract> superAbstractsInSourceSchemas) {
		this.superAbstractsInSourceSchemas = superAbstractsInSourceSchemas;
	}

	/**
	 * @param superAbstractsInTargetSchemas the superAbstractsInTargetSchemas to set
	 */
	protected void setSuperAbstractsInTargetSchemas(Set<SuperAbstract> superAbstractsInTargetSchemas) {
		this.superAbstractsInTargetSchemas = superAbstractsInTargetSchemas;
	}

	/**
	 * @param matchedSuperAbstractsInSourceSchemas the matchedSuperAbstractsInSourceSchemas to set
	 */
	protected void setMatchedSuperAbstractsInSourceSchemas(Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas) {
		this.matchedSuperAbstractsInSourceSchemas = matchedSuperAbstractsInSourceSchemas;
	}

	/**
	 * @param matchedSuperAbstractsInTargetSchemas the matchedSuperAbstractsInTargetSchemas to set
	 */
	protected void setMatchedSuperAbstractsInTargetSchemas(Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas) {
		this.matchedSuperAbstractsInTargetSchemas = matchedSuperAbstractsInTargetSchemas;
	}

	/**
	 * @param matchedSourceSuperAbstractTargetSuperAbstractsMap the matchedSourceSuperAbstractTargetSuperAbstractsMap to set
	 */
	protected void setMatchedSourceSuperAbstractTargetSuperAbstractsMap(
			Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap) {
		this.matchedSourceSuperAbstractTargetSuperAbstractsMap = matchedSourceSuperAbstractTargetSuperAbstractsMap;
	}

	/**
	 * @param chromosomeOfPairsOfSuperAbstracts the chromosomeOfPairsOfSuperAbstracts to set
	 */
	protected void setChromosomeOfPairsOfSuperAbstracts(List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts) {
		this.chromosomeOfPairsOfSuperAbstracts = chromosomeOfPairsOfSuperAbstracts;
	}

}
